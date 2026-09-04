package com.vintra.app.core.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION_SYSTEM = "system"
private const val DOCUMENT_CONNECTION_TEST = "connection_test"

@HiltViewModel
class ConnectionTestViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _status = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Loading)
    val status: StateFlow<ConnectionStatus> = _status.asStateFlow()

    init {
        testConnection()
    }
    
    // TODO: Faz um teste rápido de leitura no Firebase pra garantir que a comunicação real com o servidor tá ok, e não só o SDK local.
    fun testConnection() {
        viewModelScope.launch {
            _status.value = ConnectionStatus.Loading
            runCatching {
                val docRef = firestore.collection(COLLECTION_SYSTEM)
                    .document(DOCUMENT_CONNECTION_TEST)

                val snapshot = docRef.get().await()

                System.currentTimeMillis()
            }.onSuccess { timestamp ->
                _status.value = ConnectionStatus.Success(
                    message = "Conectado ao Firestore com sucesso.",
                    timestamp = timestamp
                )
            }.onFailure { throwable ->
                _status.value = ConnectionStatus.Error(
                    message = throwable.message ?: "Erro desconhecido ao conectar ao Firestore."
                )
            }
        }
    }
}
