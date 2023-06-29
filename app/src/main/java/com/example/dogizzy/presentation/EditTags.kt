package com.example.dogizzy.presentation

import android.net.Uri
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.example.dogizzy.model.Response
import com.example.dogizzy.presentation.components.DragDropList
import com.example.dogizzy.presentation.components.SimpleFlowRow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editTags(navController: NavHostController, usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(UsersRepo())), tagsViewModel: TagsViewModel = viewModel()) {
    Scaffold(containerColor = MaterialTheme.colorScheme.onSurface) {
        //val tags = rememberSaveable { mutableSetOf("") }
        val tags = tagsViewModel.tags
        var list = listOf("")
        //Siempre se pueden añadir más i guess

        val alltags = tagsViewModel.tags2
        val strings = "Macho,Hembra,Pasear,Parques,Lagos,Paseo por ciudad,Paseo por caminos,Jugar,Amistad,Relajarse en el parque,Relaciones entre perros,Criar,Labrador Retriever,Pastor Alemán,Yorkshire Terrier," +
                "Beagle,Golden Retriever,Chihuahua,Shiba Inu,Pomeranian,Caniche,Husky Siberiano,Dálmata,Boxer,Bulldog"
        val separated = strings.split(",")

        val lleno = rememberSaveable { mutableStateOf<Boolean>(false)}
        val full = rememberSaveable { mutableStateOf<Int>(0)}

        if(alltags.isEmpty()){
            separated.forEach{
                alltags.add(it)
            }
        }

        var name = ""
        var city = ""
        var edad = ""
        var bio = ""

        when (val userInfo = usersViewModel.getUserDetails(auth.currentUser?.uid).collectAsState(initial = null).value) {

            is Response.Error -> {
                Log.d("No existe el documento", userInfo.toString())
            }


            is Response.Success -> {
                userInfo.data?.forEach {
                    if (it.key == "Nombre"){
                        name = it.value.toString()
                    }
                    if (it.key == "Edad"){
                        edad = it.value.toString()
                    }
                    if (it.key == "Ciudad"){
                        city = it.value.toString()
                    }
                    if (it.key == "Bio"){
                        bio = it.value.toString()
                    }
                    if (it.key == "Tags") {
                        list = it.value as MutableList<String>
                        if(tags.isEmpty()){
                            Log.d("lleno", lleno.toString())
                            if(!lleno.value){
                                lleno.value = true
                                list.forEach {
                                    tagsViewModel.addElement(it)
                                    tagsViewModel.removeTagElement(it)
                                    full.value++
                                }
                            }
                        }
                    }
                }
            }
        }


        LazyColumn() {
            items(1) {


                Row(){
                    Box(modifier = Modifier.padding(top = 10.dp, start = 10.dp)){
                        Image(
                            painter = rememberAsyncImagePainter(R.drawable.goback),
                            contentDescription = "hanachan",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    navController.navigate("perfil") {
                                        popUpTo("edit_tags")
                                    }
                                }
                        )
                    }
                    Text(text = "Tus intereses",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.padding(top = 10.dp, start = 15.dp)
                    )
                    Text(
                        text = tags.size.toString() + "/10",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.padding(top = 10.dp, start = 15.dp)
                    )
                }



                SimpleFlowRow(
                    verticalGap = 1.dp,
                    horizontalGap = 1.dp,
                    alignment = Alignment.Start,
                    modifier = Modifier.padding(4.dp)
                ) {
                    tags.forEach { tags_string ->
                        //var color = tag.color
                        Card(
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(top = 10.dp, start = 15.dp)
                                .clickable {
                                    tagsViewModel.addTagElement(tags_string)
                                    tagsViewModel.removeElement(tags_string)
                                    full.value--
                                },
                            //Cambiar el color segun etiqueta? how
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = tags_string,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.padding(top = 5.dp, start = 15.dp, bottom = 5.dp, end = 15.dp)
                            )
                        }
                    }
                }

                Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))

                Text(text = "Seleccione un nuevo interés",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(top = 10.dp, start = 15.dp)
                )

                SimpleFlowRow(
                    verticalGap = 1.dp,
                    horizontalGap = 1.dp,
                    alignment = Alignment.Start,
                    modifier = Modifier.padding(4.dp)
                ) {
                    alltags.forEach { tags_string ->
                        //var color = tag.color
                        Card(
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(top = 10.dp, start = 15.dp)
                                .clickable {
                                    if(full.value < 10){
                                        tagsViewModel.removeTagElement(tags_string)
                                        tagsViewModel.addElement(tags_string)
                                        full.value++
                                    }
                                },
                            //Cambiar el color segun etiqueta? how
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = tags_string,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.padding(top = 5.dp, start = 15.dp, bottom = 5.dp, end = 15.dp)
                            )
                        }
                    }
                }


                Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))

                val context =  LocalContext.current

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    androidx.compose.material.Button(
                        onClick = {
                            Firebase.auth.currentUser?.uid?.let {
                                Firebase.firestore.collection("users").document(it).set(
                                    hashMapOf(
                                        "Nombre" to name,
                                        "Ciudad" to city,
                                        "Edad" to edad,
                                        "Bio" to bio,
                                        "Tags" to tags
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
                            .width(230.dp)
                            .height(35.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.onSecondary),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        androidx.compose.material.Text(
                            "Guardar cambios",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                }
            }
        }
    }
}