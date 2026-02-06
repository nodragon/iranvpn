//! JNI bridge for Android. Enable with `cargo build --features jni`.
//! Provides: fetch_server_list(sources_json) -> server_list_json

#![cfg(all(feature = "jni", target_os = "android"))]

use jni::objects::{JObject, JString};
use jni::sys::jstring;
use jni::JNIEnv;
use std::ops::Deref;

fn sources_from_jstring(env: &JNIEnv, s: JString) -> Result<Vec<crate::config::ConfigSource>, String> {
    let s: String = env
        .get_string(s)
        .map_err(|e| e.to_string())?
        .into();
    serde_json::from_str(&s).map_err(|e| e.to_string())
}

fn jstring_from_json(env: &JNIEnv, json: &str) -> jstring {
    env.new_string(json).unwrap().into_raw()
}

/// Fetch server list. Called from Kotlin: Native.fetchServerList(sourcesJson: String): String
#[no_mangle]
pub unsafe extern "C" fn Java_org_opensignalfoundation_iranvpn_Native_fetchServerList(
    env: JNIEnv,
    _class: JObject,
    sources_json: JString,
) -> jstring {
    let result = (|| {
        let sources = sources_from_jstring(&env, sources_json)?;
        let list = tokio::runtime::Runtime::new()
            .map_err(|e| e.to_string())?
            .block_on(crate::fetch::fetch_server_list(&sources))
            .map_err(|e| e.to_string())?;
        Ok::<_, String>(serde_json::to_string(&list).map_err(|e| e.to_string())?)
    })();
    match result {
        Ok(json) => jstring_from_json(&env, &json),
        Err(e) => {
            let _ = env.throw_new("java/lang/RuntimeException", &e);
            std::ptr::null_mut()
        }
    }
}
