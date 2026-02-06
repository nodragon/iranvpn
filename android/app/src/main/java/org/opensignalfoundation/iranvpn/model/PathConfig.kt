package org.opensignalfoundation.iranvpn.model

import org.json.JSONObject

/**
 * Kotlin mirror of core PathConfig. Parsed from JSON.
 */
sealed class PathConfig {
    abstract fun kind(): PathKind

    data class PsiphonConfig(val serverListUrl: String?) : PathConfig() {
        override fun kind() = PathKind.PSIPHON
    }
    data class ConduitConfig(val discoveryUrl: String?) : PathConfig() {
        override fun kind() = PathKind.CONDUIT
    }
    data class XrayConfig(val configJson: String, val subscriptionUrl: String?) : PathConfig() {
        override fun kind() = PathKind.XRAY
    }
    data class RostamConfig(val wireguardConfig: String) : PathConfig() {
        override fun kind() = PathKind.ROSTAM
    }

    companion object {
        fun fromJson(obj: JSONObject): PathConfig {
            return when (obj.optString("type", "")) {
                "psiphon" -> PsiphonConfig(obj.optString("server_list_url").takeIf { it.isNotEmpty() })
                "conduit" -> ConduitConfig(obj.optString("discovery_url").takeIf { it.isNotEmpty() })
                "xray" -> XrayConfig(
                    configJson = obj.optString("config_json", "{}"),
                    subscriptionUrl = obj.optString("subscription_url").takeIf { it.isNotEmpty() }
                )
                "rostam" -> RostamConfig(wireguardConfig = obj.getString("wireguard_config"))
                else -> throw IllegalArgumentException("unknown path type: ${obj.optString("type")}")
            }
        }
    }
}
