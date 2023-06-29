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

    fun getUserProfilePic() = callbackFlow {
        storageRef.child("profileImages/" + auth.currentUser?.uid + "/foto").downloadUrl
            .addOnSuccessListener {
                trySend(it)
            }
        awaitClose()
    }

    fun setUserPhotos(foto: String){
        if(foto.isNotEmpty()){
            val fileRef = storageRef.child("profileImages/" + auth.currentUser?.uid + foto)
            fileRef.putFile(foto.toUri()).addOnFailureListener{
               Log.d("No ha subido la foto", "")
            }
        }
    }

    fun setUserDetails(nombre: String, edad: String, ciudad: String, descripcion: String, foto: String, name: String, age: String, city: String, bio: String, tags: List<String>): String{
        var success = "true"

        //Foto
        if(foto.isNotEmpty()){
            val fileRef = storageRef.child("profilePics/" + auth.currentUser?.uid + "/foto")
            fileRef.putFile(foto.toUri()).addOnFailureListener{
                success = "false"
            }
        }
        var changeName = name
        var changeCity = city
        var changeEdad = age
        var changeBio = bio

        var nt = false
        var ct = false
        var et = false

        //Datos del perfil
        if(success == "true"){
            Firebase.auth.currentUser?.uid?.let {
                if(nombre.isNotEmpty()){
                    Firebase.firestore.collection("users").document(it).set(
                        hashMapOf(
                            "Nombre" to nombre,
                            "Ciudad" to changeCity,
                            "Edad" to changeEdad,
                            "Bio" to changeBio,
                            "Tags" to tags
                        )
                    ).addOnSuccessListener {
                        Log.d("Entras aqui?", "o no")
                        nt = true
                    }.addOnFailureListener{
                        success = "false"
                    }
                }
                if(nt) changeName = nombre
                if(ciudad.isNotEmpty()){
                    Log.d("NT", nt.toString())
                    Firebase.firestore.collection("users").document(it).set(
                        hashMapOf(
                            "Nombre" to changeName,
                            "Ciudad" to ciudad,
                            "Edad" to changeEdad,
                            "Bio" to changeBio,
                            "Tags" to tags
                        )
                    ).addOnSuccessListener {
                        ct = true
                    }.addOnFailureListener{
                        success = "false"
                    }
                }
                if(ct) changeCity = ciudad
                if(edad.isNotEmpty()){
                    Log.d("CT", ct.toString())
                    Firebase.firestore.collection("users").document(it).set(
                        hashMapOf(
                            "Nombre" to changeName,
                            "Ciudad" to changeCity,
                            "Edad" to edad,
                            "Bio" to changeBio,
                            "Tags" to tags
                        )
                    ).addOnSuccessListener {
                        et = true
                    }.addOnFailureListener{
                        success = "false"
                    }
                }
                if(et) changeEdad = edad
                if(descripcion.isNotEmpty()){
                    Log.d("ET", et.toString())
                    Firebase.firestore.collection("users").document(it).set(
                        hashMapOf(
                            "Nombre" to changeName,
                            "Ciudad" to changeCity,
                            "Edad" to changeEdad,
                            "Bio" to descripcion,
                            "Tags" to tags
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
    fun setUserDetails(nombre: String, edad: String, ciudad: String, descripcion: String, foto: String, name: String, age: String, city: String, bio: String, tags: List<String>) = usersRepo.setUserDetails(nombre, edad, ciudad, descripcion, foto, name, age, city, bio, tags)
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

