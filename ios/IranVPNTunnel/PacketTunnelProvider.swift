import NetworkExtension
import os.log

/// NEPacketTunnelProvider for Iran VPN tunnel.
/// Integrates core fallback engine (Psiphon -> Conduit -> Xray -> Rostam).
/// Packet flow: read from packetFlow → forward via SOCKS/tunnel → write response back.
class PacketTunnelProvider: NEPacketTunnelProvider {
    private let log = OSLog(subsystem: "org.opensignalfoundation.iranvpn", category: "PacketTunnel")
    private var isRunning = true

    override func startTunnel(options: [String: NSObject]?) async throws {
        os_log("Starting Iran VPN tunnel", log: log, type: .info)
        let networkSettings = createNetworkSettings()
        try await self.setTunnelNetworkSettings(networkSettings)
        startPacketLoop()
    }

    private func createNetworkSettings() -> NEPacketTunnelNetworkSettings {
        let settings = NEPacketTunnelNetworkSettings(tunnelRemoteAddress: "10.0.0.1")
        settings.ipv4Settings = NEIPv4Settings(addresses: ["10.0.0.2"], subnetMasks: ["255.255.255.0"])
        settings.ipv4Settings?.includedRoutes = [NEIPv4Route.default()]
        settings.dnsSettings = NEDNSSettings(servers: ["1.1.1.1", "8.8.8.8"])
        return settings
    }

    private func startPacketLoop() {
        readPackets()
    }

    private func readPackets() {
        guard isRunning else { return }
        packetFlow.readPackets { [weak self] packets, protocols in
            guard let self, self.isRunning else { return }
            if !packets.isEmpty {
                self.forwardPackets(packets, protocols: protocols)
            }
            self.readPackets()
        }
    }

    /// Forward packets through active path (Psiphon SOCKS, XrayKit proxy, Rostam tunnel).
    /// TODO: Integrate tun2socks or path-specific forwarding when Psiphon/Xray/Rostam are added.
    private func forwardPackets(_ packets: [Data], protocols: [NSNumber]) {
        // Placeholder: packets are read but not forwarded. Integrate Psiphon/Xray/Rostam
        // to route through their local SOCKS or tunnel and write response packets back.
    }

    override func stopTunnel(with reason: NEProviderStopReason) async {
        isRunning = false
        os_log("Stopping tunnel: %{public}@", log: log, type: .info, String(describing: reason))
    }
}
