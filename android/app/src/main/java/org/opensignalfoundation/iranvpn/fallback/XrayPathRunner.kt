package org.opensignalfoundation.iranvpn.fallback

import android.content.Context
import org.opensignalfoundation.iranvpn.model.PathConfig
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * PathRunner for Xray (VLESS, VMess, Trojan). Runs Xray as subprocess; provides local SOCKS5.
 * Requires: xray binary in assets/xray/xray (or assets/xray/&lt;abi&gt;/xray).
 * Config inbound should use SOCKS on port 10808 (or parse port from config).
 */
class XrayPathRunner(private val context: Context) : PathRunner, SocksProxyProvider {

    private val processRef = AtomicReference<Process?>(null)
    private val configFileRef = AtomicReference<File?>(null)
    private var socksPort = DEFAULT_SOCKS_PORT

    override fun connect(config: PathConfig): PathResult {
        if (config !is PathConfig.XrayConfig) return PathResult.Failure("wrong config type")
        return runCatching { startXray(config) }.getOrElse {
            PathResult.Failure("Xray error: ${it.message}")
        }
    }

    private fun startXray(config: PathConfig.XrayConfig): PathResult {
        var configJson = config.configJson.trim()
        if (configJson.isEmpty() || configJson == "{}") {
            config.subscriptionUrl?.let { url ->
                configJson = fetchSubscription(url) ?: return PathResult.Failure("Failed to fetch Xray subscription")
            } ?: return PathResult.Failure("Xray config or subscription URL required")
        }
        val configFile = File(context.cacheDir, "xray_config.json")
        configFile.writeText(configJson, Charsets.UTF_8)
        configFileRef.set(configFile)

        socksPort = parseSocksPort(configJson) ?: DEFAULT_SOCKS_PORT

        val xrayPath = findXrayBinary() ?: return PathResult.Failure(
            "Xray binary not found - add assets/xray/xray or build with AndroidLibXrayLite"
        )

        val process = ProcessBuilder(xrayPath, "run", "-c", configFile.absolutePath)
            .directory(context.cacheDir)
            .redirectErrorStream(true)
            .start()
        processRef.set(process)

        val exited = process.waitFor(5, TimeUnit.SECONDS)
        if (exited && process.exitValue() != 0) {
            val err = try { process.inputStream.bufferedReader().readText().take(200) } catch (_: Exception) { "" }
            process.destroyForcibly()
            return PathResult.Failure("Xray exited: $err")
        }
        if (exited) {
            process.destroyForcibly()
            return PathResult.Failure("Xray process exited unexpectedly")
        }
        Thread.sleep(500)
        return PathResult.Success
    }

    override fun disconnect() {
        processRef.getAndSet(null)?.destroyForcibly()
        configFileRef.getAndSet(null)?.delete()
    }

    override fun getLocalSocksPort(): Int = socksPort

    override fun supportsUdp(): Boolean = true

    private fun findXrayBinary(): String? {
        val abi = android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: "armeabi-v7a"
        val candidates = listOf(
            "xray/$abi/xray",
            "xray/xray",
        )
        for (assetPath in candidates) {
            try {
                context.assets.open(assetPath).use { }
                val out = File(context.cacheDir, "xray_$abi").apply {
                    parentFile?.mkdirs()
                    delete()
                }
                context.assets.open(assetPath).use { inp ->
                    out.outputStream().use { inp.copyTo(it) }
                }
                out.setExecutable(true)
                return out.absolutePath
            } catch (_: Exception) {
                continue
            }
        }
        return null
    }

    private fun fetchSubscription(url: String): String? = try {
        URL(url).openStream().bufferedReader().readText()
    } catch (_: Exception) {
        null
    }

    private fun parseSocksPort(configJson: String): Int? {
        return try {
            val json = org.json.JSONObject(configJson)
            val inbounds = json.optJSONArray("inbounds") ?: return null
            for (i in 0 until inbounds.length()) {
                val inv = inbounds.getJSONObject(i)
                if (inv.optString("protocol") == "socks") {
                    return inv.optJSONObject("settings")?.optInt("port", 0).takeIf { it != 0 }
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private const val DEFAULT_SOCKS_PORT = 10808
    }
}
