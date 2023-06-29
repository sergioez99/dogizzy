package com.example.dogizzy.presentation


import android.net.Uri
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.dogizzy.R
import com.example.dogizzy.model.UsersRepo
import com.example.dogizzy.model.UsersViewModel
import com.example.dogizzy.model.UsersViewModelFactory
import com.example.dogizzy.presentation.components.Constants.DATE_LENGTH
import com.example.dogizzy.presentation.components.Constants.DATE_MASK
import com.example.dogizzy.presentation.components.MaskVisualTransformation
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.dogizzy.model.Response
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editProfile(navController: NavHostController, foto: String) {
    Scaffold(containerColor = MaterialTheme.colorScheme.onSurface) {
        LazyColumn() {
            items(1) {
                editProfileBody(navController, foto)
            }
        }
    }
}

@Composable
fun editProfileBody(navController: NavHostController, foto: String, usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(UsersRepo()))) {

    val context =  LocalContext.current
    val perfil = auth.currentUser?.uid

    var name by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }
    val tags = rememberSaveable { mutableSetOf<String>() }


    when(val userDetails = usersViewModel.getUserDetails(auth.currentUser?.uid).collectAsState(initial = null).value) {
        is Response.Success -> {
            userDetails.data?.forEach {
                if (it.key == "Nombre") {
                    name = it.value.toString()
                }
                if (it.key == "Ciudad") {
                    city = it.value.toString()
                }
                if (it.key == "Edad") {
                    age = it.value.toString()
                }
                if (it.key == "Bio") {
                    bio = it.value.toString()
                }
                if (it.key == "Tags"){
                    val list = it.value as List<String>
                    list.forEach{
                        tags.add(it)
                    }
                }
            }
        }
    }

    //Placeholder aqui
    val imageUri = rememberSaveable { mutableStateOf("") }
    val downloaded = rememberSaveable { mutableStateOf(false) }
    val painter = rememberAsyncImagePainter(
        if (imageUri.value.isEmpty())
            R.drawable.defaultprofile//Placeholder
        else
            imageUri.value
    )

    //Coger la profile pic de la base de datos
    if(!downloaded.value){
        val profileRef = storageRef.child("profilePics/" + perfil + "/foto")
        profileRef.downloadUrl.addOnSuccessListener {
            imageUri.value = it.toString()
            downloaded.value = true
        }
    }


    //Actualiza la foto cuando cambiamos
    Log.d("foto", foto)
    if(foto != "empty") imageUri.value = foto

    Row(){
        Box(modifier = Modifier.padding(top = 10.dp, start = 15.dp)){
            Image(
                painter = rememberAsyncImagePainter(R.drawable.goback),
                contentDescription = "hanachan",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        navController.navigate("perfil") {
                            popUpTo("main")
                        }
                    }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 15.dp, end = 35.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                //Aqui poner la foto de cada uno según la base de datos
                painter = painter,
                contentDescription = "avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                    .clickable(onClick = {
                        navController.navigate("camera/edit")
                    })
            )
            Row(modifier = Modifier.padding(top = 5.dp)){
                Text(
                    "Cambiar la foto de perfil",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }
    }


    var nombre by rememberSaveable { mutableStateOf("") }
    var edad by rememberSaveable { mutableStateOf("") }
    var ciudad by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.padding(start = 15.dp, top = 30.dp)){
        Row(modifier = Modifier.padding(top = 10.dp)){
            //Cambiar nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {nombre = it},
                label = { (Text("Nombre", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.surface)) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.surface,
                    backgroundColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(4.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true
            )
        }
        Row(modifier = Modifier.padding(top = 10.dp)){
            //No tiene ni min ni max yet
            OutlinedTextField(
                value = edad,
                onValueChange = {
                    if (it.length <= DATE_LENGTH) {
                        edad = it
                    }
                },
                label = { (Text("Edad", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.surface)) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.surface,
                    backgroundColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(4.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true
            )
        }
        Row(modifier = Modifier.padding(top = 10.dp)){
            OutlinedTextField(
                value = ciudad,
                onValueChange = { ciudad = it },
                label = { (Text("Ciudad", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.surface)) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.surface,
                    backgroundColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(4.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
            )
        }
        Row(modifier = Modifier.padding(top = 10.dp)){
            OutlinedTextField(
                value = descripcion,
                onValueChange = {descripcion = it},
                label = { (Text("Sobre mi", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.surface)) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.surface,
                    backgroundColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(4.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = {
                 //usersViewModel.setUserDetails(nombre, edad, ciudad, descripcion, foto, name, age, city, bio, tags.toList())
                if(foto.isNotEmpty() || foto == "empty"){
                    val fileRef = storageRef.child("profilePics/" + auth.currentUser?.uid + "/foto")
                    fileRef.putFile(foto.toUri()).addOnFailureListener{

                    }
                }
                var changeName = name
                var changeCity = city
                var changeEdad = age
                var changeBio = bio

                if(nombre.isNotEmpty()) changeName = nombre
                if(ciudad.isNotEmpty()) changeCity = ciudad
                if(edad.isNotEmpty()) changeEdad = edad
                if(descripcion.isNotEmpty()) changeBio = descripcion


                Firebase.auth.currentUser?.uid?.let {
                        Firebase.firestore.collection("users").document(it).set(
                            hashMapOf(
                                "Nombre" to changeName,
                                "Ciudad" to changeCity,
                                "Edad" to changeEdad,
                                "Bio" to changeBio,
                                "Tags" to tags.toList()
                            )
                        ).addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Tu perfil se ha actualizado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("perfil"){
                                popUpTo("main")
                            }
                        }
                            .addOnFailureListener{
                                Toast.makeText(
                                    context,
                                    "Tu perfil no se ha podido actualizar, intentalo de nuevo",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("perfil"){
                                    popUpTo("main")
                                }
                            }
                }
            },
            modifier = Modifier
                .padding(top = 90.dp)
                .width(230.dp)
                .height(35.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.onSecondary),
            shape = RoundedCornerShape(20.dp)
        ){
            Text("Guardar cambios",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.displaySmall)
        }
    }
}

