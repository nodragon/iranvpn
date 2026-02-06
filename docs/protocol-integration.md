# Protocol integration guide (US-007)

Contributors can add new transport protocols without a major refactor.

## Core components

- **`core/src/protocol_registry.rs`** — `ProtocolRegistry` holds pluggable `ProtocolHandler`s keyed by `PathKind`.
- **`core/src/config.rs`** — Add a new variant to `PathKind` and `PathConfig` for the protocol.
- **`core/src/fallback.rs`** — Include the new `PathKind` in `DEFAULT_PATH_ORDER` (or allow user-defined order).

## Adding a new protocol

1. **Config:** In `config.rs`, add e.g. `PathKind::MyProtocol` and `PathConfig::MyProtocol { ... }`.
2. **Handler:** Implement `ProtocolHandler` in Rust (or in platform code if the protocol is native-only):
   - `kind()` → your `PathKind`
   - `connect(config)` → establish tunnel
   - `status(config)` → `PathStatus::Connected` / `Failed(...)` / `Disconnected`
   - `disconnect()` → tear down
3. **Register:** In the app bootstrap, `registry.register(Arc::new(MyProtocolHandler::new()))`.
4. **Fallback:** The engine will try your path in order; no change needed if you use the default order.

## Measurement hooks (optional)

For OONI or Filter Watch–style visibility into blocking, add optional hooks in the fallback engine or handlers (e.g. report which path failed and why). Document the event format so external tools can consume it.
