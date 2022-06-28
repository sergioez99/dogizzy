package com.example.dogizzy.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.dogizzy.model.UserState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

val auth = Firebase.auth
var user = auth.currentUser
@Composable
fun Login(navController: NavHostController) {
    val context =  LocalContext.current
    val vm = UserState.current
    val coroutineScope = rememberCoroutineScope()
    Surface() {
        //Fondo gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )) {
        }
        //Elements
        Column(
            modifier = Modifier.fillMaxSize(),
            //verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "DOGIZZY",
                color = Color.White,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(top = 52.dp)
            )
            Text(
                "Amistades y paseos",
                color = Color.White,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "Email",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 140.dp, end = 210.dp)
            )
            var email by rememberSaveable { mutableStateOf("") }
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Introduzca su correo electrónico", style = MaterialTheme.typography.displaySmall, color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .width(251.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(4.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Text(
                "Contraseña",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 40.dp, end = 165.dp)
            )
            var pass by rememberSaveable { mutableStateOf("") }
            TextField(
                value = pass,
                onValueChange = { pass = it },
                placeholder = { Text("Introduzca su contraseña", style = MaterialTheme.typography.displaySmall, color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .width(251.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(4.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            //Registrarse
            Text(
                "¿Todavia no tienes cuenta?",
                color = Color.White,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 45.dp)
            )
            Text(
                "Registrate",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable(onClick = { navController.navigate("register") })
            )
            //Botón de login
            Button(
                onClick = {
                    if(email.isEmpty() || pass.isEmpty()){
                        Toast.makeText(
                            context,
                            "Por favor, complete todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        auth.signInWithEmailAndPassword(
                            email,
                            pass
                        )
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    coroutineScope.launch {
                                        vm.signIn(email, pass)
                                    }
                                    user = auth.currentUser
                                    navController.navigate("main")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Revisa tu email y contraseña",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                },
                modifier = Modifier
                    .padding(top = 90.dp)
                    .width(230.dp)
                    .height(35.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondary),
                shape = RoundedCornerShape(20.dp)
            ){
                Text("Acceder",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall)
            }
        }
    }
}