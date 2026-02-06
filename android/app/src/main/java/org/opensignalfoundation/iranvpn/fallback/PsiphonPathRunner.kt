package org.opensignalfoundation.iranvpn.fallback

import android.content.Context
import org.opensignalfoundation.iranvpn.model.PathConfig
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * PathRunner for Psiphon (includes Conduit). Integrates Psiphon Tunnel Core SDK when available.
 * Add: implementation("ca.psiphon:psiphontunnel:2.0.35") and the Psiphon Maven repo to enable.
 * Provides local SOCKS5 proxy for TUN→SOCKS forwarding.
 */
class PsiphonPathRunner(private val context: Context) : PathRunner, SocksProxyProvider {

    private var tunnelInstance: Any? = null

    override fun connect(config: PathConfig): PathResult {
        if (config !is PathConfig.PsiphonConfig) return PathResult.Failure("wrong config type")
        return try {
            connectViaPsiphon()
        } catch (e: ClassNotFoundException) {
            PathResult.Failure("Psiphon SDK not on classpath - add ca.psiphon:psiphontunnel dependency")
        } catch (e: Exception) {
            PathResult.Failure("Psiphon error: ${e.message}")
        }
    }

    private fun connectViaPsiphon(): PathResult {
        val latch = CountDownLatch(1)
        val tunnelClass = Class.forName("ca.psiphon.PsiphonTunnel")
        val hostServiceClass = Class.forName("ca.psiphon.PsiphonTunnel\$HostService")
        val hostService = java.lang.reflect.Proxy.newProxyInstance(
            hostServiceClass.classLoader,
            arrayOf(hostServiceClass)
        ) { _, method, _ ->
            when (method.name) {
                "getContext" -> context
                "getPsiphonConfig" -> """{"EmitDiagnosticNotices":false,"EstablishTunnelTimeoutSeconds":0}"""
                "onConnected" -> { latch.countDown(); null }
                else -> when (method.returnType.name) {
                    "void" -> null
                    "boolean" -> false
                    "int" -> 0
                    else -> null
                }
            }
        }

        val newTunnel = tunnelClass.getMethod("newPsiphonTunnel", hostServiceClass).invoke(null, hostService)
        tunnelInstance = newTunnel
        newTunnel?.javaClass?.getMethod("setVpnMode", Boolean::class.javaPrimitiveType)?.invoke(newTunnel, false)
        newTunnel?.javaClass?.getMethod("startTunneling", String::class.java)?.invoke(newTunnel, "")
        val ok = latch.await(90, TimeUnit.SECONDS)
        return if (ok) PathResult.Success else PathResult.Failure("Psiphon connection timeout")
    }

    override fun disconnect() {
        try {
            tunnelInstance?.javaClass?.getMethod("stop")?.invoke(tunnelInstance)
        } catch (_: Exception) {}
        tunnelInstance = null
    }

    override fun getLocalSocksPort(): Int = try {
        (tunnelInstance?.javaClass?.getMethod("getLocalSocksProxyPort")?.invoke(tunnelInstance) as? Int) ?: 0
    } catch (_: Exception) { 0 }
}
