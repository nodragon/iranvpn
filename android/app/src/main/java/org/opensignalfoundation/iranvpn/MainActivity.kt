package org.opensignalfoundation.iranvpn

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.opensignalfoundation.iranvpn.ui.ConnectScreen
import org.opensignalfoundation.iranvpn.ui.LegalDisclaimer
import org.opensignalfoundation.iranvpn.ui.Theme

class MainActivity : ComponentActivity() {

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            startVpnConnection()
        }
    }

    override fun onResume() {
        super.onResume()
        connectionState.value = VpnTunnelService.isConnected
        // activePathName is read from ConnectScreen
    }

    private val connectionState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionState.value = VpnTunnelService.isConnected
        setContent {
            Theme {
                var acceptedDisclaimer by rememberSaveable { mutableStateOf(DisclaimerPrefs(applicationContext).hasAccepted()) }
                if (!acceptedDisclaimer) {
                    LegalDisclaimer(
                        onAccept = {
                            DisclaimerPrefs(applicationContext).setAccepted(true)
                            acceptedDisclaimer = true
                        }
                    )
                } else {
                    val connected by remember { connectionState }
                    ConnectScreen(
                        isConnected = connected,
                        activePath = VpnTunnelService.activePathName,
                        onConnect = { prepareAndConnect() },
                        onDisconnect = {
                            VpnTunnelService.disconnect(applicationContext)
                            connectionState.value = false
                        }
                    )
                }
            }
        }
    }

    private fun prepareAndConnect() {
        val intent = android.net.VpnService.prepare(this)
        if (intent != null) {
            vpnPermissionLauncher.launch(intent)
        } else {
            startVpnConnection()
        }
    }

    private fun startVpnConnection() {
        startService(Intent(this, VpnTunnelService::class.java).apply {
            action = VpnTunnelService.ACTION_CONNECT
        })
    }
}
