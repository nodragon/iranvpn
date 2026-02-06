package org.opensignalfoundation.iranvpn

import org.json.JSONArray
import org.json.JSONObject
import org.opensignalfoundation.iranvpn.model.ConfigSource
import org.opensignalfoundation.iranvpn.model.ServerList
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Fetches server list. Uses Native (Rust core) when available, else falls back to Java HTTP.
 */
object ConfigFetcher {
    private val defaultSources = listOf(
        ConfigSource(
            url = "https://raw.githubusercontent.com/opensignalfoundation/iran-vpn/main/dist-config/server-list.json",
            label = "Primary"
        ),
        ConfigSource(
            url = "https://primary.example.com/config.json",
            label = "Mirror"
        ),
    )

    fun defaultSources(): List<ConfigSource> = defaultSources

    fun fetch(sources: List<ConfigSource> = defaultSources): Result<ServerList> {
        return runCatching {
            if (Native.isAvailable()) {
                val arr = JSONArray()
                sources.forEach { s -> arr.put(JSONObject().apply { put("url", s.url); put("label", s.label) }) }
                ServerList.fromJson(Native.fetchServerList(arr.toString()))
            } else {
                fetchViaHttp(sources)
            }
        }.recoverCatching {
            fallbackServerList()
        }
    }

    private fun fetchViaHttp(sources: List<ConfigSource>): ServerList {
        for (source in sources) {
            try {
                val json = fetchUrl(source.url)
                return ServerList.fromJson(json)
            } catch (_: Exception) {
                continue
            }
        }
        throw RuntimeException("all config sources failed")
    }

    private fun fallbackServerList(): ServerList {
        val json = """{"paths":[{"type":"psiphon","server_list_url":null},{"type":"conduit","discovery_url":"https://conduit.psiphon.ca/discovery"}],"sources":[]}"""
        return ServerList.fromJson(json)
    }

    private fun fetchUrl(url: String): String {
        val conn = URL(url).openConnection() as HttpsURLConnection
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.requestMethod = "GET"
        conn.connect()
        if (conn.responseCode != 200) throw RuntimeException("HTTP ${conn.responseCode}")
        return conn.inputStream.bufferedReader().use { it.readText() }
    }
}
