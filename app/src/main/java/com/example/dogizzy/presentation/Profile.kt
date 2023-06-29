package com.example.dogizzy.presentation

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.dogizzy.util.FirestoreUtil
import com.example.dogizzy.util.StorageUtil
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.target.ImageViewTarget
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firestore.v1.FirestoreGrpc
import okhttp3.internal.userAgent
import java.time.format.TextStyle
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.dogizzy.BottomNavigation
import com.example.dogizzy.R
import com.example.dogizzy.model.Response
import com.example.dogizzy.model.UsersRepo
import com.example.dogizzy.model.UsersViewModel
import com.example.dogizzy.model.UsersViewModelFactory
import com.example.dogizzy.presentation.camara.EMPTY_IMAGE_URI
import com.example.dogizzy.presentation.components.SimpleFlowRow
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.*
import okhttp3.internal.wait
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.firebase.storage.ListResult


val storageRef = FirebaseStorage.getInstance().reference
var charged = false


//Perfil del propio usuario (se puede editar)
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class, ExperimentalCoilApi::class
)
//Perfil propio
@Composable
fun Perfil(navController: NavHostController, usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(UsersRepo()))) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.onSurface,
        bottomBar = { BottomNavigation(navController = navController) },
    ) {
        Column(){
            Box(
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.onPrimary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )

                    )
                    .padding(top = 15.dp, start = 15.dp)
            ) {
                ProfilePic(auth.currentUser?.uid)
                ProfileInfo(navController, auth.currentUser?.uid)
            }
            Column(modifier = Modifier.verticalScroll(rememberScrollState())){
                ProfileData(navController, auth.currentUser?.uid)
                ProfileImages(navController, auth.currentUser?.uid)
            }
        }
    }
    //if(!charged)
      //  SplashCargar(navController)
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProfilePic(perfil: String?) {

    val imageUri = rememberSaveable { mutableStateOf("") }
    val painter = rememberAsyncImagePainter(
        if (imageUri.value.isEmpty())
            R.drawable.defaultprofile
        else
            imageUri.value
    )
    val downloaded = remember { mutableStateOf(false) }

    if(!downloaded.value) {
        //Coger la profile pic de la base de datos
        val profileRef = storageRef.child("profilePics/" + perfil + "/foto")
        profileRef.downloadUrl.addOnSuccessListener {
            imageUri.value = it.toString()
            downloaded.value = true
        }
    }

    Image(
        //Aqui poner la foto de cada uno según la base de datos
        painter = painter,
        contentDescription = "avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfo(navController: NavHostController, perfil: String?, usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(UsersRepo())), chatViewModel: ChatViewModel = viewModel()) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var ciudad by rememberSaveable { mutableStateOf("") }
    var edad by rememberSaveable { mutableStateOf("") }
    var downloaded = remember { mutableStateOf(false) }

    if(!downloaded.value){
        when (val userInfo = usersViewModel.getUserDetails(perfil).collectAsState(initial = null).value) {

            is Response.Error -> {
                Log.d("No existe el documento", userInfo.toString())
            }

            is Response.Success -> {
                userInfo.data?.forEach() {
                    if(it.key == "Nombre") {
                        nombre = it.value.toString()
                    }
                    if(it.key == "Ciudad") {
                        ciudad = it.value.toString()

                    }
                    if(it.key == "Edad") {
                        edad = it.value.toString()

                    }
                }
                downloaded.value = true
            }
        }
    }

    Text(
        text = nombre,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 15.dp, start = 115.dp)
    )
    Text(
        text = ciudad,
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 40.dp, start = 115.dp)
    )
    Text(
        text = edad,
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 60.dp, start = 115.dp)
    )

    // Boton chat o boton config
    if(perfil != auth.currentUser?.uid){
        Card(shape = CircleShape,
            modifier = Modifier
                .padding(top = 70.dp, start = 320.dp),
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSurface
        ){
            Image(
                //Aqui poner la foto de cada uno según la base de datos
                painter = painterResource(R.drawable.chatbubbles),
                contentDescription = "chat",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                    .clickable(onClick = { chatViewModel.crearChat(navController, perfil) })
            )
        }
    } else {
        Box(modifier = Modifier.padding(start = 330.dp, top = 10.dp)){
            Image(
                painter = rememberAsyncImagePainter(R.drawable.whiteconfig),
                contentDescription = "config",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = { navController.navigate("config") })
            )
        }
        val imageUri = "empty"
        Box(modifier = Modifier.padding(top = 80.dp, start = 330.dp)){
            Image(
                painter = rememberAsyncImagePainter(R.drawable.pencil),
                contentDescription = "config",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = { navController.navigate("edit/${imageUri}"){
                    } })
            )
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileData(navController: NavHostController, perfil: String?, usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(UsersRepo()))) {

    Log.d("Me estoy ejecutando", "soy profile")
    var bio by rememberSaveable { mutableStateOf("") }
    //Lista de tags de la base de datos
    val tags = rememberSaveable { mutableSetOf("") }
    var list = listOf("")
    var downloaded = remember { mutableStateOf(false) }

    if(!downloaded.value){
        when (val userInfo = usersViewModel.getUserDetails(perfil).collectAsState(initial = null).value) {

            is Response.Error -> {
                Log.d("No existe el documento", userInfo.toString())
            }

            is Response.Success -> {
                userInfo.data?.forEach {
                    if(it.key == "Bio") {
                        bio = it.value.toString()
                    }
                    if(it.key == "Tags"){
                        list = it.value as List<String>
                        list.forEach{
                            tags.add(it)
                        }
                        tags.remove("")
                    }

                }
                downloaded.value = true
            }
        }
    }

    Text(text = "Sobre mi",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.padding(top = 15.dp, start = 15.dp)
    )

    Text(text = bio,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.padding(top = 10.dp, start = 15.dp)
    )

    Row(){
        Text(text = "Intereses",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(top = 20.dp, start = 15.dp)
        )
        Row(modifier = Modifier.padding(start = 10.dp, top = 20.dp)){
            if(perfil == auth.currentUser?.uid){
                Image(
                    //Aqui poner la foto de cada uno según la base de datos
                    painter = rememberAsyncImagePainter(R.drawable.pencilblack),
                    contentDescription = "config",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = { navController.navigate("edit_tags") })
                )
            }
        }
    }

    SimpleFlowRow(
        verticalGap = 1.dp,
        horizontalGap = 1.dp,
        alignment = Alignment.Start,
        modifier = Modifier.padding(4.dp)
    ) {
        tags.forEach { tags ->
            //var color = tag.color
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(top = 10.dp, start = 15.dp),
                //Cambiar el color segun etiqueta? how
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = tags,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(top = 5.dp, start = 15.dp, bottom = 5.dp, end = 15.dp)
                )
            }
        }
    }

}


@Composable
fun ProfileImages(navController: NavHostController, perfil: String?, usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(UsersRepo()))) {

    Row(){
        Text(text = "Imágenes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(top = 30.dp, start = 15.dp)
        )
        Row(modifier = Modifier.padding(start = 10.dp, top = 33.dp)){
            if(perfil == auth.currentUser?.uid){
                Image(
                    //Aqui poner la foto de cada uno según la base de datos
                    painter = rememberAsyncImagePainter(R.drawable.camera),
                    contentDescription = "fotos",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = { navController.navigate("subir_fotos") })
                )
            }
        }
    }

    val foto = remember { mutableSetOf<Uri>() }
    var showFoto = remember { mutableStateOf(EMPTY_IMAGE_URI)}
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        Dialog(
            onDismissRequest = { showDialog.value = false }
        ){
            Box(contentAlignment = Alignment.Center){
                Image(
                    painter = rememberAsyncImagePainter(showFoto.value),
                    contentDescription = "hanachan",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(15.dp))

                )
            }
        }
    }

    var downloaded = remember { mutableStateOf(false) }
    
    SimpleFlowRow(
        modifier = Modifier.padding(top = 15.dp, start = 5.dp)
    ) {
        if(!downloaded.value){
            when(val userPhotos = usersViewModel.getUserPhotos(perfil).collectAsState(initial = null).value){

                is ListResult -> {
                    userPhotos.items.forEach{
                        it.downloadUrl.addOnSuccessListener {
                            foto.add(it)
                        }
                    }
                    downloaded.value = true
                }
            }
        }
        when (downloaded.value) {
            true -> {
                foto.forEach{
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "hanachan",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(128.dp)
                            .clickable {
                                showFoto.value = it
                                showDialog.value = true
                            }
                    )
                }
            }
        }
    }





}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashCargar(navController: NavHostController) {
    val context =  LocalContext.current
    Log.d("Splash", "Ha entrado al splash")
    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomNavigation(navController = navController) }) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val imageLoader = ImageLoader.Builder(context)
                .components {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()

            Image(
                painter = rememberAsyncImagePainter(R.drawable.paws, imageLoader),
                contentDescription = "paws",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(128.dp)
            )


        }
        /*
        LaunchedEffect(key1 = true){
            delay(2000L)
            navController.navigate("perfil")
            charged = true
            Log.d("Splash", "Ha salido del splash")
        }

         */
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilSeleccionado(navController: NavHostController, perfil: String, usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(UsersRepo()))) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.onSurface,
        bottomBar = { BottomNavigation(navController = navController) },
    ) {
        Column(){
            Box(
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.onPrimary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )

                    )
                    .padding(start = 15.dp, top = 15.dp)
            ) {
                ProfilePic(perfil)
                ProfileInfo(navController, perfil)
            }
            Column(modifier = Modifier.verticalScroll(rememberScrollState())){
                ProfileData(navController, perfil)
                ProfileImages(navController, perfil)
            }
        }
    }
}