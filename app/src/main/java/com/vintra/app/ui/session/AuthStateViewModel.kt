package com.vintra.app.ui.session

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject

@HiltViewModel
class AuthStateViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val scope = CoroutineScope(SupervisorJob())

    val isAuthenticated: StateFlow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = firebaseAuth.currentUser != null
    )
}