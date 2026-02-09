package org.opensignalfoundation.iranvpn.fallback

import org.opensignalfoundation.iranvpn.model.PathConfig

/**
 * PathRunner for RostamWG (obfuscated WireGuard).
 * Requires: RostamWG binary or WireGuard tunnel library with Rostam config.
 */
class RostamPathRunner : PathRunner {
    override fun connect(config: PathConfig): PathResult {
        if (config !is PathConfig.RostamConfig) return PathResult.Failure("wrong config type")
        // TODO: Start RostamWG tunnel with wireguardConfig.
        return PathResult.Failure("Rostam integration pending - add RostamWG binary or WireGuard library")
    }

    override fun disconnect() {
        // TODO: Stop Rostam tunnel
    }
}
