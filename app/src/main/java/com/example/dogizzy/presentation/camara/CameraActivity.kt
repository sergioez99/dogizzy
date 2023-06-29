package com.example.dogizzy.presentation.camara

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.text.htmlEncode
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okio.ByteString.Companion.encodeUtf8
import java.net.URLEncoder
import java.nio.charset.StandardCharsets



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraActivity(navController: NavHostController, modifier: Modifier = Modifier, id: String) {
    var imageUri by remember { mutableStateOf(EMPTY_IMAGE_URI) }
    if (imageUri != EMPTY_IMAGE_URI) {
        Box(modifier = modifier) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Captured image"
            )
            Button(
                modifier = Modifier.align(Alignment.BottomStart),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.onSecondary),
                onClick = {
                    imageUri = EMPTY_IMAGE_URI
                }
            ) {
                Text("Borrar foto")
            }
            Button(
                modifier = Modifier.align(Alignment.BottomEnd),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.onSecondary),
                onClick = {
                    val encodedUrl = URLEncoder.encode(imageUri.toString(), StandardCharsets.UTF_8.toString())
                    if(id == "edit")
                        navController.navigate("edit/${encodedUrl}"){
                            popUpTo("main")
                        }
                    else{
                        navController.navigate("uploadFoto/${encodedUrl}"){
                            popUpTo("main")
                        }
                    }
                }
            ) {
                Text("Guardar foto")
            }
        }
    } else {
        var showGallerySelect by remember { mutableStateOf(false) }
        if (showGallerySelect) {
            GallerySelect(
                modifier = modifier,
                onImageUri = { uri ->
                    showGallerySelect = false
                    imageUri = uri
                }
            )
        } else {
            Box(modifier = modifier) {
                CameraCapture(
                    modifier = modifier,
                    onImageFile = { file ->
                        imageUri = file.toUri()
                    }
                )
                Button(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.onSecondary),
                    onClick = {
                        showGallerySelect = true
                    }
                ) {
                    Text("Galería")
                }
            }
        }
    }
}

val EMPTY_IMAGE_URI: Uri = Uri.parse("file://dev/null")