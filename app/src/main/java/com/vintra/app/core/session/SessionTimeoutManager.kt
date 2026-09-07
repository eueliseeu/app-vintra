package com.vintra.app.core.session

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.vintra.app.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TIMEOUT_MILLIS = 5 * 60 * 1000L

@Singleton
class SessionTimeoutManager @Inject constructor(
    private val authRepository: AuthRepository
) : DefaultLifecycleObserver {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var pendingSignOutJob: Job? = null

    override fun onStop(owner: LifecycleOwner) {
        // Se o usuário sair totalmente do aplicativo ou deixar ele em segundo plano a sessão fecha.
        pendingSignOutJob = scope.launch {
            delay(TIMEOUT_MILLIS)
            authRepository.signOut()
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        pendingSignOutJob?.cancel()
        pendingSignOutJob = null
    }
}