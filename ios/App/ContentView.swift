import SwiftUI

/// US-001: Zero-config Connect/Disconnect. US-008: Legal disclaimer shown on first launch (see App entry).
struct ContentView: View {
    @State private var isConnected = false
    @State private var isConnecting = false

    var body: some View {
        VStack(spacing: 16) {
            Text(statusText)
                .font(.headline)
            if isConnecting {
                ProgressView()
            } else if isConnected {
                Button("Disconnect") { disconnect() }
            } else {
                Button("Connect") { connect() }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    private var statusText: String {
        if isConnecting { return "Connecting…" }
        if isConnected { return "Connected" }
        return "Disconnected"
    }

    private func connect() {
        isConnecting = true
        // TODO: Request VPN permission, start NEPacketTunnelProvider with core fallback engine
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            isConnecting = false
            isConnected = true
        }
    }

    private func disconnect() {
        isConnected = false
    }
}

#Preview {
    ContentView()
}
