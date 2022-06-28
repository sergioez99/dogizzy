package com.example.dogizzy.presentation

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.dogizzy.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Config(navController: NavHostController) {
    Scaffold(containerColor = MaterialTheme.colorScheme.onSurface) {
        LazyColumn(){
            items(1){
                /*
                val currentMode = AppCompatDelegate.getDefaultNightMode()
                Text(
                    "Cambiar tema",
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier
                        .padding(top = 15.dp, start = 15.dp)
                        .clickable(onClick = {
                            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES){
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            }
                            else{
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            }
                        })
                )
                */


            }
        }
    }
}

