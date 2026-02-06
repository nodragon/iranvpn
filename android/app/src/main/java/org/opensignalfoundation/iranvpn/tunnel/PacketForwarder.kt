package org.opensignalfoundation.iranvpn.tunnel

import android.os.ParcelFileDescriptor
import java.io.FileInputStream
import java.nio.ByteBuffer
import kotlin.concurrent.thread

/**
 * Forwards TUN traffic. When socksPort > 0, uses tun2socks (badvpn) to route through SOCKS5.
 * When socksPort <= 0, runs a read loop that drops packets (for non-SOCKS paths).
 */
object PacketForwarder {

    private const val DEFAULT_MTU = 1500
    private const val DEFAULT_NET_IPV4 = "10.0.0.2"
    private const val DEFAULT_NETMASK = "255.255.255.0"

    /**
     * Start forwarding. When socksPort > 0, runs tun2socks (blocking in background thread).
     * When socksPort <= 0, runs read loop that drops packets.
     *
     * @param tunFd TUN file descriptor
     * @param socksPort Local SOCKS5 port; 0 = no forwarding (drop packets)
     * @param mtu Must match VpnService.Builder.setMtu()
     * @param netIPv4 Must match VpnService.Builder.addAddress()
     * @param netmask For tun2socks (e.g. 255.255.255.0 for /24)
     * @param forwardUdp true if SOCKS5 server supports UDP (e.g. Xray)
     * @param running Supplier returning false to stop
     */
    @JvmOverloads
    fun start(
        tunFd: ParcelFileDescriptor,
        socksPort: Int,
        mtu: Int = DEFAULT_MTU,
        netIPv4: String = DEFAULT_NET_IPV4,
        netmask: String = DEFAULT_NETMASK,
        forwardUdp: Boolean = false,
        running: () -> Boolean,
    ) {
        if (socksPort <= 0) {
            thread(name = "vpn-tun-read") {
                runReadLoop(tunFd, running) { /* drop packets when no SOCKS */ }
            }
            return
        }
        thread(name = "vpn-tun2socks") {
            try {
                val exitCode = Tun2SocksNative.startTun2Socks(
                    tunFd = tunFd,
                    mtu = mtu,
                    socksHost = "127.0.0.1",
                    socksPort = socksPort,
                    netIPv4 = netIPv4,
                    netIPv6 = null,
                    netmask = netmask,
                    forwardUdp = forwardUdp,
                    logLevel = 3, // NOTICE
                )
                if (exitCode != 0 && running()) {
                    android.util.Log.w("PacketForwarder", "tun2socks exited with code $exitCode")
                }
            } catch (e: Throwable) {
                if (running()) {
                    android.util.Log.e("PacketForwarder", "tun2socks failed", e)
                }
            }
        }
    }

    /**
     * Stop tun2socks. Call when disconnecting. Safe to call from any thread.
     */
    fun stop() {
        Tun2SocksNative.stopTun2Socks()
    }

    private fun runReadLoop(
        pfd: ParcelFileDescriptor,
        running: () -> Boolean,
        onPacket: (ByteBuffer) -> Unit,
    ) {
        val channel = FileInputStream(pfd.fileDescriptor).channel
        val buffer = ByteBuffer.allocate(32767)
        while (running() && pfd.fileDescriptor.valid()) {
            buffer.clear()
            val n = channel.read(buffer)
            if (n <= 0) {
                try {
                    Thread.sleep(1)
                } catch (_: InterruptedException) {
                    break
                }
                continue
            }
            buffer.flip()
            val copy = ByteBuffer.allocate(buffer.remaining())
            copy.put(buffer)
            copy.flip()
            onPacket(copy)
        }
    }
}
