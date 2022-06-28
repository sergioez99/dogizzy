package com.example.dogizzy.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun Registro(navController: NavHostController) {
    val context =  LocalContext.current
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
                modifier = Modifier.padding(top = 100.dp, end = 210.dp)
            )
            var email by rememberSaveable { mutableStateOf("") }
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Introduzca su correo electrónico", style = MaterialTheme.typography.displaySmall, color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    textColor = Color.Black
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
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    textColor = Color.Black
                ),
                modifier = Modifier
                    .width(251.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(4.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            Text(
                "Confirmar contraseña",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 40.dp, end = 90.dp)
            )
            var pass2 by rememberSaveable { mutableStateOf("") }
            TextField(
                value = pass2,
                onValueChange = { pass2 = it },
                placeholder = { Text("Repita su contraseña", style = MaterialTheme.typography.displaySmall, color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    textColor = Color.Black
                ),
                modifier = Modifier
                    .width(251.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(4.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            Text(
                "¿Ya tienes cuenta?",
                color = Color.White,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 45.dp)
            )
            Text(
                "Inicia sesión",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable(onClick = { navController.navigate("login") })
            )
            //Botón de registro
            Button(
                onClick = {
                    if(pass != pass2){
                        Toast.makeText(
                            context,
                            "Las contraseñas no coinciden",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else if(email.isEmpty() || pass.isEmpty() || pass2.isEmpty()){
                        Toast.makeText(
                            context,
                            "Por favor, complete todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        auth.createUserWithEmailAndPassword(
                            email,
                            pass
                        )
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    user = auth.currentUser
                                    //Entrada en base de datos
                                    Firebase.auth.currentUser?.uid?.let {
                                        Firebase.firestore.collection("users").document(it).set(
                                            hashMapOf(
                                                "Nombre" to "Nombre de tu mascota",
                                                "Ciudad" to "Tu ciudad",
                                                "Edad" to "Edad de tu mascota",
                                                "Bio" to "¡Pon aquí tu biografía!",
                                                "Tags" to mutableListOf("Ponga aquí sus etiquetas")
                                            )
                                        )
                                    }
                                    Toast.makeText(
                                        context,
                                        "Te has registrado correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("login")
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
                    .padding(top = 60.dp)
                    .width(230.dp)
                    .height(35.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondary),
                shape = RoundedCornerShape(20.dp),
            ){
                Text("Registrarse",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall)
            }
        }
    }
}