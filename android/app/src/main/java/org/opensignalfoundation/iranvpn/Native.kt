package org.opensignalfoundation.iranvpn

/**
 * JNI bridge to iran-vpn-core (Rust).
 * Requires libiran_vpn_core.so built for Android with `cargo build --target aarch64-linux-android --features jni`.
 * Loads lazily; if load fails, ConfigFetcher uses Java HTTP fallback.
 */
object Native {
    @Volatile
    private var loaded = false

    @Volatile
    private var loadError: Throwable? = null

    fun isAvailable(): Boolean {
        if (!loaded) {
            try {
                System.loadLibrary("iran_vpn_core")
                loaded = true
            } catch (e: UnsatisfiedLinkError) {
                loadError = e
                loaded = true // don't retry
            }
        }
        return loadError == null
    }

    /**
     * Fetch server list from redundant sources.
     * @param sourcesJson JSON array of ConfigSource: [{"url":"...","label":"..."}]
     * @return JSON ServerList or throws on failure
     */
    external fun fetchServerList(sourcesJson: String): String
}
