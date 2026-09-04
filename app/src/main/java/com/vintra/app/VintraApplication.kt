package com.vintra.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// TODO: Classe base do app. Colocar aqui todas as inicializações globais (Hilt, Firebase, etc) pra garantir o ciclo de vida correto dos singletons.
@HiltAndroidApp
class VintraApplication : Application()
