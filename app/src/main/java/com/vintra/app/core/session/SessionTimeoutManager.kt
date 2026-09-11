package com.vintra.app.core.session

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.vintra.app.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TIMEOUT_MILLIS = 5 * 60 * 1000L
private const val PREFS_NAME = "vintra_session_prefs"
private const val KEY_BACKGROUND_TIMESTAMP = "background_timestamp"

@Singleton
class SessionTimeoutManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository
) : DefaultLifecycleObserver {

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun onStop(owner: LifecycleOwner) {
        prefs.edit().putLong(KEY_BACKGROUND_TIMESTAMP, System.currentTimeMillis()).apply()
    }

    override fun onStart(owner: LifecycleOwner) {
        val backgroundTimestamp = prefs.getLong(KEY_BACKGROUND_TIMESTAMP, 0L)
        prefs.edit().remove(KEY_BACKGROUND_TIMESTAMP).apply()

        if (backgroundTimestamp == 0L) return

        val elapsedMillis = System.currentTimeMillis() - backgroundTimestamp
        if (elapsedMillis >= TIMEOUT_MILLIS) {
            authRepository.signOut()
        }
    }
}