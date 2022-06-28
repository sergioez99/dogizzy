package com.example.dogizzy.presentation

import android.graphics.drawable.Drawable
import android.media.Image
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.dogizzy.BottomNavigation
import com.example.dogizzy.R
import com.example.dogizzy.model.Response
import com.example.dogizzy.model.UsersRepo
import com.example.dogizzy.model.UsersViewModel
import com.example.dogizzy.model.UsersViewModelFactory
import com.example.dogizzy.presentation.components.Constants
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


@OptIn(
    ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
@Composable
fun Chat(navController: NavHostController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.onSurface,
        bottomBar = { BottomNavigation(navController = navController) }
    ){
            //Nombre de la app
        Text(
            "DO",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = 15.dp, start = 15.dp)
        )
        Text(
            "GI",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = 15.dp, start = 53.dp)
        )
        Text(
            "ZZY",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = 15.dp, start = 81.dp)
        )
        Column(
            modifier = Modifier.padding(top = 45.dp)
        ){
            var busqueda by rememberSaveable { mutableStateOf("") }
            TextField(
                value = busqueda,
                onValueChange = {
                    busqueda = it
                },
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.search),
                        contentDescription = "search",
                        modifier = Modifier.size(20.dp)
                    )
                },
                placeholder = { Text("Buscar", style = MaterialTheme.typography.displaySmall, color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.LightGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(20.dp),
                shape = RoundedCornerShape(40.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Divider(color = Color.LightGray, thickness = 1.dp)
            ListaChats(navController, busqueda)
        }

    }
}

@Composable
//Aquí el estilo del perfil (el de figma)
fun ChatListItem(perfil: String, foto: Int, ultimoMsg: String, onItemClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onItemClick(perfil) })
            .height(100.dp)
            .fillMaxWidth()
            .padding(PaddingValues(15.dp, 15.dp))
    ) {
        Image(
            //Aqui poner la foto de cada uno según la base de datos
            painter = painterResource(foto),
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(75.dp)
                .clip(CircleShape)
        )
        Column(){
            Text(text = perfil,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(top = 7.dp, start = 15.dp)
            )
            Text(text = ultimoMsg,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(top = 5.dp, start = 15.dp)
            )
        }
    }
    Divider(color = Color.LightGray, thickness = 1.dp)
}

@Composable
fun ListaChats(navController: NavHostController, busqueda: String, usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(UsersRepo()))) {
    val perfiles = rememberSaveable { mutableSetOf<String>() }
    val nombre = rememberSaveable { mutableSetOf<String>() }

    var infoRecieved = false

    when (val userInfo = usersViewModel.getAllChats().collectAsState(initial = null).value) {

        is Response.Error -> {
            Log.d("No existe el documento", userInfo.toString())
        }

        is Response.Success -> {
            userInfo.data?.forEach {
                Log.d("chats", it.id)
                perfiles.add(it.id)
                when (val userInfo =
                    usersViewModel.getUserDetails(it.id).collectAsState(initial = null).value) {

                    is Response.Error -> {
                        Log.d("No existe el documento", userInfo.toString())
                    }

                    is Response.Success -> {
                        //Aquí seria sacar la foto, el nombre y el último msg (Habría que hacer otra función)
                        userInfo.data?.forEach {
                            if(it.key == "Nombre")
                                nombre.add(it.value.toString())

                        }
                        infoRecieved = true
                    }
                }
            }
            when(infoRecieved){
                true -> {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(1) {
                            val searchedText = busqueda
                            val perfilesFiltrados = if (searchedText.isEmpty()) {
                                nombre
                            } else {
                                val resultList = mutableSetOf<String>()
                                //lo que quiera buscar para match
                                for (name in nombre) {
                                    if (name.lowercase(Locale.getDefault())
                                            .contains(searchedText.lowercase(Locale.getDefault()))
                                    ) {
                                        resultList.add(name)
                                    }
                                }
                                resultList
                            }
                            perfilesFiltrados.forEach {
                                ChatListItem(
                                    perfil = it,
                                    foto = R.drawable.shiba,
                                    ultimoMsg = "Último msg",
                                    //Quiza poner it?
                                    onItemClick = {
                                        //CONDICIÓN PARA RELACIONAR CHAT CON SU ID
                                        for(i in nombre.indices) {
                                            if(it == nombre.elementAt(i)){
                                                navController.navigate("chatScreen/${perfiles.elementAt(i)}") {
                                                    popUpTo("main") {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatViewModel: ChatViewModel = viewModel(), perfilUID: String, usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(UsersRepo()))) {
    Scaffold(containerColor = MaterialTheme.colorScheme.onSurface)
    {

        //Sacar los mensajes de la base de datos
        val mensajes = chatViewModel.getMessages(perfilUID)

        val message: String by chatViewModel.message.observeAsState(initial = "")
        val messages: List<Map<String, Any>> by chatViewModel.messages.observeAsState(
            initial = emptyList<Map<String, Any>>().toMutableList()
        )

        var nombre by rememberSaveable { mutableStateOf("") }


        when (val userInfo = usersViewModel.getUserDetails(perfilUID).collectAsState(initial = null).value) {

            is Response.Error -> {
                Log.d("No existe el documento", userInfo.toString())
            }

            is Response.Success -> {
                userInfo.data?.forEach {
                    if(it.key == "Nombre")
                        nombre = it.value.toString()

                }

                //Algo para la imagen

                //Lista de mensajes
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    //Al tener el perfil maybe pinchar en la foto y q te lleve o algo asi
                    Row(
                        modifier = Modifier
                            //.clickable(onClick = { onItemClick(perfil) })
                            .height(100.dp)
                            .fillMaxWidth()
                            .padding(PaddingValues(15.dp, 15.dp))
                    ) {
                        Image(
                            //Aqui poner la foto de cada uno según la base de datos
                            painter = painterResource(R.drawable.hana4),
                            contentDescription = "avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(75.dp)
                                .clip(CircleShape)
                        )
                        Column(){
                            Text(text = nombre,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.padding(top = 7.dp, start = 15.dp)
                            )
                            //Aquí lo de la conexión
                        }
                    }
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 0.85f, fill = true),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        reverseLayout = true
                    ) {
                        items(messages) { message ->
                            val isCurrentUser = message[Constants.IS_CURRENT_USER] as Boolean

                            SingleMessage(message[Constants.MESSAGE].toString(),isCurrentUser)

                        }
                    }
                    TextField(
                        value = message,
                        onValueChange = {
                            chatViewModel.updateMessage(it)
                        },
                        leadingIcon = {
                            Icon(
                                painterResource(id = R.drawable.smilingface),
                                contentDescription = "emoji",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    chatViewModel.addMessage(perfilUID)
                                }
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.sendmessage),
                                    contentDescription = "send",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        placeholder = { Text("Mensaje", style = MaterialTheme.typography.displaySmall, color = Color.Gray) },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.LightGray,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .padding(20.dp),
                        shape = RoundedCornerShape(40.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )
                }
            }
        }
    }
}


//Estilo de los mensajes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleMessage(message: String, isCurrentUser: Boolean) {
    val color = if (isCurrentUser) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
    val align = if (isCurrentUser) Arrangement.End else Arrangement.Start

    Row(horizontalArrangement = align, modifier = Modifier.fillMaxWidth()){
        ElevatedCard(
            shape = RoundedCornerShape(10.dp),
            containerColor = color,
            modifier = Modifier.shadow(elevation = 5.dp, shape = RoundedCornerShape(10.dp))
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(10.dp),
                color = MaterialTheme.colorScheme.surface
            )
        }
    }
}

/*
@Composable
fun EmojiKeyboard() {
    EmojIconActions emojIcon = new EmojIconActions(Context, rootView, emojiconEditText,
        emojiImageView);
    emojIcon.ShowEmojIcon()
}

 */


