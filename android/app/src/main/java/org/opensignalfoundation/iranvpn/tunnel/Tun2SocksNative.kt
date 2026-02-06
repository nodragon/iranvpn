package org.opensignalfoundation.iranvpn.tunnel

import android.os.ParcelFileDescriptor
import android.util.Log
import java.util.Locale

/**
 * JNI bridge to badvpn-tun2socks (from universal-android-tun2socks).
 * Routes TUN traffic through a local SOCKS5 proxy.
 */
object Tun2SocksNative {

    private const val TAG = "Tun2SocksNative"

    init {
        try {
            System.loadLibrary("tun2socks")
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load tun2socks native library", e)
        }
    }

    /**
     * Start tun2socks in the current thread (blocking).
     * Call from a background thread.
     *
     * @param tunFd TUN file descriptor from VpnService.Builder.establish()
     * @param mtu MTU (e.g. 1500)
     * @param socksHost SOCKS5 server host (e.g. "127.0.0.1")
     * @param socksPort SOCKS5 server port
     * @param netIPv4 VPN interface IPv4 (e.g. "10.0.0.2")
     * @param netIPv6 VPN interface IPv6 or null to disable IPv6
     * @param netmask Netmask (e.g. "255.255.255.0")
     * @param forwardUdp Whether SOCKS5 UDP is supported (e.g. Xray supports it)
     * @param logLevel Log level (0=NONE, 5=DEBUG)
     * @return 0 on normal exit, non-zero on error
     */
    @JvmStatic
    fun startTun2Socks(
        tunFd: ParcelFileDescriptor,
        mtu: Int,
        socksHost: String,
        socksPort: Int,
        netIPv4: String,
        netIPv6: String?,
        netmask: String,
        forwardUdp: Boolean,
        logLevel: Int = 3, // NOTICE
    ): Int {
        val args = mutableListOf(
            "badvpn-tun2socks",
            "--logger", "stdout",
            "--loglevel", logLevel.toString(),
            "--tunfd", tunFd.fd.toString(),
            "--tunmtu", mtu.toString(),
            "--netif-ipaddr", netIPv4,
            "--netif-netmask", netmask,
            "--socks-server-addr", String.format(Locale.US, "%s:%d", socksHost, socksPort),
        )
        if (!netIPv6.isNullOrBlank()) {
            args.add("--netif-ip6addr")
            args.add(netIPv6)
        }
        if (forwardUdp) {
            args.add("--socks5-udp")
        }
        return nativeStartTun2Socks(args.toTypedArray())
    }

    /**
     * Stop tun2socks. Safe to call from any thread.
     */
    @JvmStatic
    external fun stopTun2Socks()

    private external fun nativeStartTun2Socks(args: Array<String>): Int
}
