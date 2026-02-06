package org.opensignalfoundation.iranvpn.fallback

/**
 * PathRunners that provide a local SOCKS5 proxy (e.g. Psiphon, Xray) implement this.
 * Used to route TUN traffic through the proxy via tun2socks.
 */
interface SocksProxyProvider {
    /** Local SOCKS5 proxy port when connected, or 0 if not available. */
    fun getLocalSocksPort(): Int

    /** Whether the SOCKS5 server supports UDP (e.g. Xray). Default false. */
    fun supportsUdp(): Boolean = false
}
