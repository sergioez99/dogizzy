package com.example.dogizzy

import android.media.Image
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dogizzy.ui.theme.DogizzyTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dogizzy.R
import com.example.dogizzy.model.UserState
import com.example.dogizzy.model.UserStateViewModel
import com.example.dogizzy.presentation.*
import com.example.dogizzy.presentation.camara.CameraActivity
import com.example.dogizzy.presentation.components.BottomNavItem
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    private val userState by viewModels<UserStateViewModel>()
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            DogizzyTheme() {
                CompositionLocalProvider(UserState provides userState) {
                    val vm = UserState.current
                    val navController = rememberNavController()
                    val start = if(vm.isLoggedIn) "main" else "splash_screen"
                    NavHost(navController = navController, startDestination = start ) {
                        composable("splash_screen"){
                            Splash(navController)
                        }
                        composable("login") {
                            Login(navController)
                        }
                        composable("register"){
                            Registro(navController)
                        }
                        composable("main"){
                            Main(navController)
                        }
                        composable("perfil"){
                            Perfil(navController)
                        }
                        composable("perfil/{perfilSeleccionado}",
                            arguments = listOf(
                                navArgument("perfilSeleccionado") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            backStackEntry.arguments?.getString("perfilSeleccionado")?.let { perfil ->
                                PerfilSeleccionado(navController, perfil = perfil)
                            }
                        }
                        composable("chat"){
                            Chat(navController)
                        }
                        //Hacer lo mismo q en perfil arriba
                        composable("chatScreen/{perfilSeleccionado}",
                            arguments = listOf(
                                navArgument("perfilSeleccionado") {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            backStackEntry.arguments?.getString("perfilSeleccionado")?.let { perfil ->
                                ChatScreen(perfilUID = perfil)
                            }
                        }
                        composable("config"){
                            Config(navController)
                        }
                        composable("edit/{foto}",
                            arguments = listOf(
                                navArgument("foto") {
                                    type = NavType.StringType
                                }
                            )
                        ){ backStackEntry ->
                            backStackEntry.arguments?.getString("foto")?.let { foto ->
                                editProfile(navController, foto = foto)
                            }
                        }
                        composable("edit_tags"){
                            editTags(navController)
                        }
                        composable("subir_fotos"){
                            subirFotos(navController)
                        }
                        composable("camera/{foto}",
                            arguments = listOf(
                                navArgument("foto") {
                                    type = NavType.StringType
                                }
                            )
                        ){ backStackEntry ->
                            backStackEntry.arguments?.getString("foto")?.let { foto ->
                                CameraActivity(navController, Modifier.fillMaxSize(), id = foto)
                            }
                        }
                        composable("uploadFoto/{foto}",
                            arguments = listOf(
                                navArgument("foto") {
                                    type = NavType.StringType
                                }
                            )
                        ){ backStackEntry ->
                            backStackEntry.arguments?.getString("foto")?.let { foto ->
                                uploadFoto(navController, foto = foto)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigation(navController: NavHostController) {
    val items = listOf(
        //BottomNavItem.Buscador,
        BottomNavItem.Chat,
        BottomNavItem.Inicio,
        BottomNavItem.Perfil,
        //BottomNavItem.Config
    )
    NavigationBar(
        containerColor = colorResource(id = R.color.white),
        contentColor = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                //selectedContentColor = Color.Black,
                //unselectedContentColor = Color.Black.copy(0.4f),
                selected = currentRoute == item.screen_route,
                onClick = {
                    navController.navigate(item.screen_route) {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}






