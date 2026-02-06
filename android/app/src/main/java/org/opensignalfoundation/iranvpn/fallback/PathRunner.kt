package org.opensignalfoundation.iranvpn.fallback

import org.opensignalfoundation.iranvpn.model.PathConfig

/**
 * Platform implementation of a transport path (Psiphon, Conduit, Xray, Rostam, etc.).
 */
interface PathRunner {
    fun connect(config: PathConfig): PathResult
    fun disconnect()
}
