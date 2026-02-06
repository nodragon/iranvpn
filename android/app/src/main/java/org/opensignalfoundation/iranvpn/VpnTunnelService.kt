package org.opensignalfoundation.iranvpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.opensignalfoundation.iranvpn.fallback.ConduitPathRunner
import org.opensignalfoundation.iranvpn.fallback.FallbackOrchestrator
import org.opensignalfoundation.iranvpn.fallback.PathRunner
import org.opensignalfoundation.iranvpn.fallback.PsiphonPathRunner
import org.opensignalfoundation.iranvpn.fallback.SocksProxyProvider
import org.opensignalfoundation.iranvpn.tunnel.PacketForwarder
import org.opensignalfoundation.iranvpn.fallback.RostamPathRunner
import org.opensignalfoundation.iranvpn.fallback.XrayPathRunner
import org.opensignalfoundation.iranvpn.model.PathKind
import kotlin.concurrent.thread

/**
 * VPN tunnel service. Fetches server list, runs fallback engine (Psiphon -> Conduit -> Xray -> Rostam).
 * When a path connects, establishes TUN and forwards packets (Xray: SOCKS; Psiphon/Rostam: tunnel).
 */
class VpnTunnelService : VpnService() {

    private var tunnelFd: ParcelFileDescriptor? = null
    private var running = false
    private var activeRunner: PathRunner? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> connect()
            ACTION_DISCONNECT -> disconnect()
        }
        return START_STICKY
    }

    private fun connect() {
        if (running) return
        startForeground(NOTIFICATION_ID, createNotification("Connecting…"))
        running = true

        thread(name = "vpn-connect") {
            var connectedRunners: Map<PathKind, PathRunner>? = null
            val result = runBlocking {
                withContext(Dispatchers.IO) {
                    val serverList = ConfigFetcher.fetch().getOrElse {
                        return@withContext Result.failure<PathKind>(it)
                    }
                    val pathRunners: Map<PathKind, PathRunner> = mapOf(
                        PathKind.PSIPHON to PsiphonPathRunner(this@VpnTunnelService),
                        PathKind.CONDUIT to ConduitPathRunner(),
                        PathKind.XRAY to XrayPathRunner(this@VpnTunnelService),
                        PathKind.ROSTAM to RostamPathRunner(),
                    )
                    connectedRunners = pathRunners
                    val orchestrator = FallbackOrchestrator(serverList, pathRunners)
                    orchestrator.run()
                }
            }

            runOnMain {
                result.fold(
                    onSuccess = { pathKind ->
                        activePathName = pathKind.name.lowercase().replaceFirstChar { it.uppercase() }
                        activeRunner = connectedRunners?.get(pathKind)
                        establishTun()
                    },
                    onFailure = {
                        updateNotification("Failed: ${it.message}")
                        isConnected = false
                        running = false
                        stopSelf()
                    }
                )
            }
        }
    }

    private fun runOnMain(block: () -> Unit) {
        android.os.Handler(android.os.Looper.getMainLooper()).post(block)
    }

    private fun pathRunnersMap(): Map<PathKind, PathRunner> = mapOf(
        PathKind.PSIPHON to PsiphonPathRunner(this),
        PathKind.CONDUIT to ConduitPathRunner(),
        PathKind.XRAY to XrayPathRunner(this),
        PathKind.ROSTAM to RostamPathRunner(),
    )

    private fun establishTun() {
        val builder = Builder()
            .setSession("IranVPN")
            .setMtu(1500)
            .addAddress("10.0.0.2", 24)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("1.1.1.1")
            .addDnsServer("8.8.8.8")
            .setBlocking(false)

        tunnelFd = builder.establish()
        if (tunnelFd == null) {
            activeRunner?.disconnect()
            stopSelf()
            running = false
            isConnected = false
            return
        }

        isConnected = true
        updateNotification("Connected")
        val socksPort = (activeRunner as? SocksProxyProvider)?.getLocalSocksPort() ?: 0
        val forwardUdp = (activeRunner as? SocksProxyProvider)?.supportsUdp() ?: false
        PacketForwarder.start(tunnelFd!!, socksPort, forwardUdp = forwardUdp) { running }
    }

    private fun disconnect() {
        running = false
        isConnected = false
        activePathName = null
        PacketForwarder.stop()
        activeRunner?.disconnect()
        activeRunner = null
        tunnelFd?.close()
        tunnelFd = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        disconnect()
        super.onDestroy()
    }

    private fun createNotification(status: String): Notification {
        createChannel()
        val pending = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Iran VPN")
            .setContentText(status)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pending)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(status: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, createNotification(status))
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID,
                "VPN",
                NotificationManager.IMPORTANCE_LOW
            ).apply { setShowBadge(false) }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(ch)
        }
    }

    companion object {
        const val ACTION_CONNECT = "org.opensignalfoundation.iranvpn.CONNECT"
        const val ACTION_DISCONNECT = "org.opensignalfoundation.iranvpn.DISCONNECT"
        private const val CHANNEL_ID = "iran_vpn_channel"
        private const val NOTIFICATION_ID = 1

        @Volatile
        var isConnected = false
            private set

        @Volatile
        var activePathName: String? = null
            private set

        fun disconnect(context: Context) {
            context.startService(Intent(context, VpnTunnelService::class.java).apply {
                action = ACTION_DISCONNECT
            })
        }
    }
}
