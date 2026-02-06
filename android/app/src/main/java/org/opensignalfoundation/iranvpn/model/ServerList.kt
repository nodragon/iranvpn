package org.opensignalfoundation.iranvpn.model

import org.json.JSONArray
import org.json.JSONObject

data class ConfigSource(val url: String, val label: String?)

data class ServerList(val paths: List<PathConfig>, val sources: List<ConfigSource>) {
    companion object {
        fun fromJson(json: String): ServerList {
            val obj = JSONObject(json)
            val pathsArr = obj.optJSONArray("paths") ?: JSONArray()
            val sourcesArr = obj.optJSONArray("sources") ?: JSONArray()
            val paths = (0 until pathsArr.length()).map { i ->
                PathConfig.fromJson(pathsArr.getJSONObject(i))
            }
            val sources = (0 until sourcesArr.length()).map { i ->
                val o = sourcesArr.getJSONObject(i)
                ConfigSource(
                    url = o.getString("url"),
                    label = o.optString("label").takeIf { it.isNotEmpty() }
                )
            }
            return ServerList(paths = paths, sources = sources)
        }
    }
}
