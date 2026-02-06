# Volunteer P2P station (US-004)

One-click or minimal-step install for a **Conduit-style** relay so volunteers (diaspora or allies) can share bandwidth with users in Iran.

## Components

- **Conduit** ([Psiphon-Inc/conduit](https://github.com/Psiphon-Inc/conduit)): run as station; traffic is end-to-end encrypted so volunteers cannot see content.
- This folder: wrapper or launcher with minimal config, optional small UI for status.

## Status UI (planned)

- Number of connected users
- Throughput
- Configurable bandwidth and battery limits (Android)
- Link to Cure53 (or equivalent) security audit report for P2P components

## Platforms

- **Android:** background operation
- **Windows, macOS:** desktop station
- **iOS:** planned (Conduit iOS support is planned)

## Security

- Cure53 or equivalent audit for P2P components (NFR-2).
- Volunteers cannot see user traffic (encrypted).

## Usage

1. Install Conduit station (or this wrapper when built).
2. Run; optionally set bandwidth/battery limits.
3. Share the app with users; they connect via zero-config client which will use Conduit in the fallback chain.
