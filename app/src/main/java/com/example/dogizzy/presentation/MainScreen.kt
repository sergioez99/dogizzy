package com.example.dogizzy.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.dogizzy.BottomNavigation
import com.example.dogizzy.R
import com.example.dogizzy.model.Response
import com.example.dogizzy.model.UsersRepo
import com.example.dogizzy.model.UsersViewModel
import com.example.dogizzy.model.UsersViewModelFactory
import com.example.dogizzy.presentation.components.Constants
import com.example.dogizzy.presentation.components.SimpleFlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(navController: NavHostController, usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(UsersRepo()))) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.onSurface,
        bottomBar = { BottomNavigation(navController = navController) }
    ) {
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
        Text(
            "Usuarios recomendados",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(top = 55.dp, start = 15.dp)
        )

        val ids = mutableSetOf<String>()
        val nombre = mutableMapOf<String, String>()
        val ciudad = mutableListOf<String>()
        val edades = mutableMapOf<String, String>()
        val ciudades = mutableMapOf<String, String>()
        val tags = mutableSetOf<Set<String>>()
        val tags_uid = mutableSetOf<String>()
        var ciudad_uid = ""
        var list = listOf("")
        var id = false

        when (val userInfo = usersViewModel.getAllUsers().collectAsState(initial = null).value) {

            is Response.Error -> {
                Log.d("No existe el documento", userInfo.toString())
            }

            is Response.Success -> {
                Log.d("SUCCESS", "si")
                userInfo.data?.forEach { document ->
                    ids.add(document.id)
                    if(document.id == auth.currentUser?.uid){
                        id = true
                        ids.remove(document.id)
                    }
                    document.data?.entries?.forEach{ it ->
                        if(it.key == "Nombre")
                            nombre[document.id] = it.value.toString()
                        if(it.key == "Ciudad"){
                            ciudad.add(it.value.toString())
                            ciudades[document.id] = it.value.toString()
                            if(id){
                                ciudad_uid = it.value.toString()
                                ciudades.remove(document.id)
                            }

                        }
                        if(it.key == "Edad"){
                            edades[document.id] = it.value.toString()
                            if(id){
                                edades.remove(document.id)
                            }
                        }
                        if(it.key == "Tags"){
                            list = it.value as List<String>
                            val tag_list = mutableSetOf<String>()
                            list.forEach{
                                if(id) tags_uid.add(it)
                                else tag_list.add(it)
                            }
                            tags.add(tag_list)
                        }
                    }
                    if(id) id = false
                }

                val ids_ordenados = algoritmo_comparacion(tags, ciudad, tags_uid, ciudad_uid, ids)

                LazyColumn(modifier = Modifier.padding(top = 30.dp)){
                    var i = 0 //Para los demas params
                    var pad = 55
                    items(1) {
                        SimpleFlowRow(
                            verticalGap = 1.dp,
                            horizontalGap = 1.dp,
                            alignment = Alignment.Start
                        ){
                            ids_ordenados.forEach(){
                                if(i != ids_ordenados.size && i < ids_ordenados.size){
                                    if(i == 2)
                                        pad = 10
                                    ElevatedCard(
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .clickable(onClick = { navController.navigate("perfil/${it.key}") })
                                            .padding(top = pad.dp, start = 15.dp)
                                            .shadow(
                                                elevation = 30.dp,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .size(width = 175.dp, height = 250.dp),
                                        containerColor = MaterialTheme.colorScheme.onSurface
                                    ){
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(top = 25.dp, start = 15.dp)
                                        ){
                                            Image(
                                                //Fotos de los usuarios sugeridos
                                                painter = painterResource(R.drawable.shiba),
                                                contentDescription = "avatar",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(CircleShape)
                                            )
                                            nombre[it.key]?.let { it1 ->
                                                Text(text = it1,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.secondary,
                                                    modifier = Modifier.padding(top = 15.dp)
                                                )
                                            }
                                            ciudades[it.key]?.let { it1 ->
                                                Text(text = it1,
                                                    style = MaterialTheme.typography.displaySmall,
                                                    color = MaterialTheme.colorScheme.surface,
                                                    modifier = Modifier.padding(top = 5.dp)
                                                )
                                            }
                                            edades[it.key]?.let { it1 ->
                                                Text(text = it1,
                                                    style = MaterialTheme.typography.displaySmall,
                                                    color = MaterialTheme.colorScheme.surface,
                                                    modifier = Modifier.padding(top = 5.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                i++
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun algoritmo_comparacion(tags: Set<Set<String>>, ciudad: List<String>, tags_uid: Set<String>, ciudad_uid: String, ids: Set<String>): Map<String, Int> {
    var similaridad = 0
    val lista_similares = mutableListOf<Int>()
    val lista_ids_valores = mutableMapOf<String, Int>()

    //Comparar si tienen los mismos tags y si la ciudad es la misma (pero tienen q estar escritas iguales uf)
    for((index, tag_list) in tags.withIndex()){
        tag_list.forEach{
            for(tag_uid in tags_uid){
                if(tag_uid == it)
                    similaridad++
            }
        }
        if(ciudad.elementAt(index).equals(ciudad_uid, true)) //ignoramos mayusculas porsiaca
            similaridad++
        lista_similares.add(similaridad)
        similaridad = 0
    }

    //Asignar a cada id su valor
    for((i, id) in ids.withIndex()){
        lista_ids_valores[id] = lista_similares.elementAt(i)
    }


    //Ordenamos la lista por los valores de similaridad
    val lista_ordenada = lista_ids_valores.toList().sortedBy { (_, value) -> value}.reversed().toMap()

    return lista_ordenada

}
