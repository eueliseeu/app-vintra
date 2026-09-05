package com.vintra.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.vintra.app.core.session.SessionTimeoutManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
// TODO: Classe base do app. Colocar aqui todas as inicializações globais (Hilt, Firebase, etc) pra garantir o ciclo de vida correto dos singletons.
@HiltAndroidApp
class VintraApplication : Application() {

    @Inject
    lateinit var sessionTimeoutManager: SessionTimeoutManager

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(sessionTimeoutManager)
    }
}