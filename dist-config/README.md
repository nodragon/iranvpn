# Config distribution (FR-8)

Server list and path configs must be fetched from **redundant or decentralized** sources so a single block does not break zero-config.

## Sources

- **Primary:** S3 or similar (e.g. Psiphon-style distribution).
- **Mirror:** Second URL (different domain) serving the same JSON.
- **Optional:** GitHub raw, IPFS, or community mirror.

## Format

`core` expects a JSON `ServerList`:

```json
{
  "paths": [
    { "type": "psiphon", "server_list_url": "https://..." },
    { "type": "conduit", "discovery_url": "https://..." }
  ],
  "sources": [
    { "url": "https://mirror1.example.com/config.json", "label": "Mirror 1" },
    { "url": "https://mirror2.example.com/config.json", "label": "Mirror 2" }
  ]
}
```

## Scripts

- `publish.sh` — upload server-list.json to S3 and/or mirror:
  ```bash
  # S3: set S3_BUCKET, optionally S3_PREFIX
  export S3_BUCKET=my-vpn-config
  # Mirror: set MIRROR_URL, optionally MIRROR_AUTH
  export MIRROR_URL=https://mirror.example.com/config.json
  ./publish.sh [server-list.json]
  ```
- Clients use `fetch_server_list(sources)` and try each URL until one succeeds.

## Default ConfigSource URLs (core)

Zero-config clients fetch from, in order:
1. `https://raw.githubusercontent.com/opensignalfoundation/iran-vpn/main/dist-config/server-list.json`
2. `https://primary.example.com/config.json` (replace with your mirror)

## US-005

Installers and updates should also use ≥2 distribution channels (GitHub Releases + mirror); this folder is for **config** distribution only.
