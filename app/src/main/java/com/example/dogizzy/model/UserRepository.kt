package com.example.dogizzy.model

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotApplyResult
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dogizzy.presentation.auth
import com.example.dogizzy.presentation.components.Constants
import com.example.dogizzy.presentation.storageRef
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import okhttp3.internal.wait

class UsersRepo {

    private val _userState = mutableStateOf<Response<MutableSet<MutableMap.MutableEntry<String, Any>>?>>(Response.Loading)
    val userState: State<Response<MutableSet<MutableMap.MutableEntry<String, Any>>?>> = _userState



    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserDetails(perfil: String?) = callbackFlow {

        perfil.let {
            val collection = Firebase.firestore.collection("users").document(it!!)
            val snapshotListener = collection.addSnapshotListener { value, error ->
                val response = if (error == null) {
                    Response.Success(value?.data?.entries)//Devuelve cada propiedad
                } else {
                    Response.Error(error.toString())
                }

                //_userState.value = response
                trySend(response)
            }

            awaitClose {
                snapshotListener.remove()
            }
        }
    }


    fun getAllUsers() = callbackFlow {

        val collection = Firebase.firestore.collection("users")
        val snapshotListener = collection.addSnapshotListener { value, error ->
            val response = if (error == null) {
                Response.Success(value?.documents)//Devuelve cada propiedad
            } else {
                Response.Error(error.toString())
            }

            //_userState.value = response
            trySend(response)
        }

        awaitClose {
            snapshotListener.remove()
        }

    }

    fun getAllChats() = callbackFlow {

        Firebase.auth.currentUser?.uid?.let {
            val snapshotListener = Firebase.firestore.collection("users").document(it).collection("chats")
                .addSnapshotListener { value, error ->
                    val response = if (error == null) {
                        Response.Success(value?.documents)
                    } else {
                        Response.Error(error.toString())
                    }

                    trySend(response)
                }

            awaitClose {
                snapshotListener.remove()
            }

        }

    }

    fun getUserPhotos(perfil: String?) = callbackFlow {

        storageRef.child("profileImages/" + perfil).listAll()
            .addOnSuccessListener {
                trySend(it)
            }
        awaitClose()


    }

    fun setUserPhotos(foto: String){
        if(foto.isNotEmpty()){
            val fileRef = storageRef.child("profileImages/" + auth.currentUser?.uid + "/foto")
            fileRef.putFile(foto.toUri()).addOnFailureListener{
               Log.d("No ha subido la foto", "")
            }
        }
    }

    fun setUserDetails(nombre: String, edad: String, ciudad: String, descripcion: String, foto: String, name: String, age: String, city: String, bio: String): String{
        var success = "true"

        //Foto
        if(foto.isNotEmpty()){
            val fileRef = storageRef.child("profilePics/" + auth.currentUser?.uid + "/foto")
            fileRef.putFile(foto.toUri()).addOnFailureListener{
                success = "false"
            }
        }

        //Datos del perfil
        if(success == "true"){
            Firebase.auth.currentUser?.uid?.let {
                if(nombre.isNotEmpty()){
                    Firebase.firestore.collection("users").document(it).collection("perfil").document("nombre").set(
                        hashMapOf(
                            "Nombre" to nombre,
                            "Ciudad" to city,
                            "Edad" to age,
                            "Bio" to bio
                        )
                    ).addOnFailureListener{
                        success = "false"
                    }
                }
                if(ciudad.isNotEmpty()){
                    Firebase.firestore.collection("users").document(it).collection("perfil").document("ciudad").set(
                        hashMapOf(
                            "Nombre" to name,
                            "Ciudad" to ciudad,
                            "Edad" to age,
                            "Bio" to bio
                        )
                    ).addOnFailureListener{
                        success = "false"
                    }
                }
                if(edad.isNotEmpty()){
                    Firebase.firestore.collection("users").document(it).collection("perfil").document("edad").set(
                        hashMapOf(
                            "Nombre" to name,
                            "Ciudad" to city,
                            "Edad" to edad,
                            "Bio" to bio
                        )
                    ).addOnFailureListener{
                        success = "false"
                    }
                }
                if(descripcion.isNotEmpty()){
                    Firebase.firestore.collection("users").document(it).collection("perfil").document("descripcion").set(
                        hashMapOf(
                            "Nombre" to name,
                            "Ciudad" to city,
                            "Edad" to age,
                            "Bio" to descripcion
                        )
                    ).addOnFailureListener{
                        success = "false"
                    }
                }

            }
        }

        return success
    }
}

class UsersViewModel(val usersRepo: UsersRepo): ViewModel() {

    fun getUserDetails(perfil: String?) = usersRepo.getUserDetails(perfil)
    fun getAllUsers() = usersRepo.getAllUsers()
    fun getAllChats() = usersRepo.getAllChats()
    fun getUserPhotos(perfil: String?) = usersRepo.getUserPhotos(perfil)
    fun setUserDetails(nombre: String, edad: String, ciudad: String, descripcion: String, foto: String, name: String, age: String, city: String, bio: String) = usersRepo.setUserDetails(nombre, edad, ciudad, descripcion, foto, name, age, city, bio)
    fun setUserPhotos(foto: String) = usersRepo.setUserPhotos(foto)
}

class UsersViewModelFactory(private val usersRepo: UsersRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            return UsersViewModel(usersRepo) as T
        }
        throw IllegalStateException()
    }
}


//Clases para el estado del login
class UserStateViewModel : ViewModel() {
    var isLoggedIn by mutableStateOf(false)
    var isBusy by mutableStateOf(false)

    suspend fun signIn(email: String, password: String) {
        isBusy = true
        delay(2000)
        isLoggedIn = true
        isBusy = false
    }

    suspend fun signOut() {
        isBusy = true
        delay(2000)
        isLoggedIn = false
        isBusy = false
    }
}

val UserState = compositionLocalOf<UserStateViewModel> { error("User State Context Not Found!") }

