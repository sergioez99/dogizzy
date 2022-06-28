package com.example.dogizzy.presentation

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.dogizzy.presentation.components.SimpleFlowRow
import com.google.firebase.storage.ListResult

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun subirFotos(navController: NavHostController, usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(UsersRepo()))) {
    Scaffold(containerColor = MaterialTheme.colorScheme.onSurface) {
        LazyColumn() {
            items(1) {
                val foto = rememberSaveable { mutableStateOf(mutableSetOf<Uri>()) }

                Text(text = "Tus imágenes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(top = 10.dp, start = 15.dp)
                )

                SimpleFlowRow(
                    modifier = Modifier.padding(top = 15.dp)
                ) {
                    when(val userPhotos = usersViewModel.getUserPhotos(auth.currentUser?.uid).collectAsState(initial = null).value){

                        is ListResult -> {
                            userPhotos.items.forEach{
                                it.downloadUrl.addOnSuccessListener {
                                    foto.value.add(it)
                                }
                            }
                            foto.value.forEach{
                                Image(
                                    painter = rememberAsyncImagePainter(it),
                                    contentDescription = "hanachan",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(128.dp)
                                        .clickable { borrarFoto(it, foto) }
                                )
                            }
                            Image(
                                painter = rememberAsyncImagePainter(R.drawable.add),
                                contentDescription = "hanachan",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(128.dp)
                                    .clickable { navController.navigate("camera/fotos") }
                            )
                        }
                    }
                }

            }
        }
    }
}

fun borrarFoto(foto: Uri, list:  MutableState<MutableSet<Uri>>){

    storageRef.child(foto.toString()).delete().addOnSuccessListener {
        list.value.remove(foto)
    }.addOnFailureListener{
        Log.d("No se ha borrado lafoto", "")
    }
}

fun uploadFoto(navController: NavHostController, foto: String){

    val fileRef = storageRef.child("profileImages/" + auth.currentUser?.uid + "/" + foto)
    fileRef.putFile(foto.toUri()).addOnSuccessListener {
        navController.navigate("subir_fotos")
    }.addOnFailureListener{
        Log.d("No se ha subido la foto", "")
    }

}