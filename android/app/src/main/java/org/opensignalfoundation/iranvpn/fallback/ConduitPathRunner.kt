package org.opensignalfoundation.iranvpn.fallback

import org.opensignalfoundation.iranvpn.model.PathConfig

/**
 * PathRunner for Conduit (P2P volunteer relay). Conduit is built into Psiphon SDK;
 * when PsiphonPathRunner connects, Conduit discovery/stations are used automatically.
 * This runner is for Conduit-only configs when not using Psiphon.
 */
class ConduitPathRunner : PathRunner {
    override fun connect(config: PathConfig): PathResult {
        if (config !is PathConfig.ConduitConfig) return PathResult.Failure("wrong config type")
        // Conduit discovery_url → fetch station list → connect as P2P. Psiphon includes this.
        return PathResult.Failure("Conduit standalone pending - use Psiphon path (includes Conduit)")
    }

    override fun disconnect() {
        // TODO: Disconnect from Conduit station
    }
}
