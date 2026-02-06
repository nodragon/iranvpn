package org.opensignalfoundation.iranvpn.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * US-001: Zero-config connection — single Connect/Disconnect; clear status.
 * Shows active path (Psiphon/Conduit/Xray/Rostam) when connected.
 */
@Composable
fun ConnectScreen(
    isConnected: Boolean,
    activePath: String? = null,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    var isConnecting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                isConnecting -> "Connecting…"
                isConnected -> if (activePath != null) "Connected via $activePath" else "Connected"
                else -> "Disconnected"
            },
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))
        if (isConnecting) {
            CircularProgressIndicator(Modifier.size(48.dp))
        } else {
            if (isConnected) {
                Button(onClick = onDisconnect) { Text("Disconnect") }
            } else {
                Button(onClick = {
                    isConnecting = true
                    onConnect()
                    isConnecting = false
                }) { Text("Connect") }
            }
        }
    }
}
