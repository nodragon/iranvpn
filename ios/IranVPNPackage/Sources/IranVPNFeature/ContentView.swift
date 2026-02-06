import SwiftUI

/// US-001: Zero-config Connect/Disconnect. US-008: Legal disclaimer on first launch.
public struct ContentView: View {
    @AppStorage("disclaimerAccepted") private var disclaimerAccepted = false
    @State private var isConnected = false
    @State private var isConnecting = false
    @State private var activePath: String? = nil

    public init() {}

    public var body: some View {
        Group {
            if !disclaimerAccepted {
                LegalDisclaimerView(onAccept: { disclaimerAccepted = true })
            } else {
                ConnectView(
                    isConnected: isConnected,
                    isConnecting: isConnecting,
                    activePath: activePath,
                    onConnect: connect,
                    onDisconnect: disconnect
                )
            }
        }
    }

    private func connect() {
        isConnecting = true
        // TODO: Request VPN permission, start NEPacketTunnelProvider with core fallback engine
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            isConnecting = false
            isConnected = true
            activePath = "Psiphon"
        }
    }

    private func disconnect() {
        isConnected = false
        activePath = nil
    }
}

struct LegalDisclaimerView: View {
    let onAccept: () -> Void

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                Text("Legal Notice")
                    .font(.headline)
                    .fontWeight(.bold)
                Text("Unauthorized VPN use is illegal in Iran (Supreme Council of Cyberspace, Feb 2024). This app is for research and informational use only. Users assume all legal and personal risks. We do not encourage illegal activity.")
                    .font(.body)
                    .multilineTextAlignment(.center)
                Text("Security: Use DNS-over-HTTPS when possible; avoid public Wi-Fi; keep the app updated.")
                    .font(.caption)
                    .multilineTextAlignment(.center)
                Button("I understand and accept the risks", action: onAccept)
                    .buttonStyle(.borderedProminent)
            }
            .padding(24)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

struct ConnectView: View {
    let isConnected: Bool
    let isConnecting: Bool
    let activePath: String?
    let onConnect: () -> Void
    let onDisconnect: () -> Void

    private var statusText: String {
        if isConnecting { return "Connecting…" }
        if isConnected {
            return activePath != nil ? "Connected via \(activePath!)" : "Connected"
        }
        return "Disconnected"
    }

    var body: some View {
        VStack(spacing: 16) {
            Text(statusText)
                .font(.headline)
            if isConnecting {
                ProgressView()
            } else if isConnected {
                Button("Disconnect", action: onDisconnect)
            } else {
                Button("Connect", action: onConnect)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

#Preview {
    ContentView()
}
