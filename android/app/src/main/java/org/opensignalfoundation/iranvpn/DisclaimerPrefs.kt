package org.opensignalfoundation.iranvpn

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

private const val PREFS_NAME = "iran_vpn_prefs"
private const val KEY_DISCLAIMER_ACCEPTED = "disclaimer_accepted"

class DisclaimerPrefs(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun hasAccepted(): Boolean = prefs.getBoolean(KEY_DISCLAIMER_ACCEPTED, false)

    fun setAccepted(accepted: Boolean) {
        prefs.edit { putBoolean(KEY_DISCLAIMER_ACCEPTED, accepted) }
    }
}
