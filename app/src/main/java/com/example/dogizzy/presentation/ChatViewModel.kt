package com.example.dogizzy.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.dogizzy.presentation.components.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okhttp3.internal.wait
import java.lang.IllegalArgumentException

//Lógica del chat
class ChatViewModel : ViewModel() {
    private val _message = MutableLiveData("")
    val message: LiveData<String> = _message

    private var _messages = MutableLiveData(emptyList<Map<String, Any>>().toMutableList())
    val messages: LiveData<MutableList<Map<String, Any>>> = _messages

    /**
     * Update the message value as user types
     */
    fun updateMessage(message: String) {
        _message.value = message
    }

    /**
     * Send message
     */
    fun addMessage(perfilUID: String) {
        val message: String = _message.value ?: throw IllegalArgumentException("message empty")
        if (message.isNotEmpty()) {
            auth.currentUser?.uid?.let {
                Firebase.firestore.collection("users").document(it).collection("chats").document(perfilUID).collection("mensajes").document().set(
                    hashMapOf(
                        Constants.MESSAGE to message,
                        Constants.SENT_BY to Firebase.auth.currentUser?.uid,
                        Constants.SENT_ON to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                ).addOnSuccessListener {
                    _message.value = ""
                }
            }
            //Crear el chat para el otro usuario en su base de datos también
            auth.currentUser?.uid?.let {
                Firebase.firestore.collection("users").document(perfilUID).collection("chats").document(it).collection("mensajes").document().set(
                    hashMapOf(
                        Constants.MESSAGE to message,
                        Constants.SENT_BY to Firebase.auth.currentUser?.uid,
                        Constants.SENT_ON to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                ).addOnSuccessListener {
                    _message.value = ""
                }
            }
        }
    }

    /**
     * Get the messages
     */
    fun getMessages(perfilUID: String) {
        Firebase.firestore.collection("users").document(auth.currentUser?.uid!!).collection("chats").document(perfilUID).collection("mensajes")
            .orderBy(Constants.SENT_ON)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val list = emptyList<Map<String, Any>>().toMutableList()

                if (value != null) {
                    for (doc in value) {
                        val data = doc.data
                        data[Constants.IS_CURRENT_USER] =
                            Firebase.auth.currentUser?.uid.toString() == data[Constants.SENT_BY].toString()

                        list.add(data)
                    }
                }

                updateMessages(list)
            }
    }

    /**
     * Update the list after getting the details from firestore
     */
    fun updateMessages(list: MutableList<Map<String, Any>>) {
        _messages.value = list.asReversed()
    }

    //Le asignamos valores para que se cree el documento -> poder luego buscarlo en la lista de chats
    fun crearChat(navController: NavHostController, perfil: String?) {
        auth.currentUser?.uid?.let { uid ->
            perfil?.let {
                Firebase.firestore.collection("users").document(uid).collection("chats").document(it).set(
                    hashMapOf(
                        "Canal" to "chat creado"
                    )
                )
            }

        }

        auth.currentUser?.uid?.let { uid ->
            perfil?.let {
                Firebase.firestore.collection("users").document(it).collection("chats").document(uid).set(
                    hashMapOf(
                        "Canal" to "chat creado"
                    )
                )
            }

        }

        navController.navigate("chatScreen/$perfil")
    }

}