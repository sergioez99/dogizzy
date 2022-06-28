package com.example.dogizzy.presentation.components

import com.example.dogizzy.R


sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){

    //object Buscador : BottomNavItem("Buscador", R.drawable.search,"buscador")
    object Chat: BottomNavItem("Chat",R.drawable.chatbubbles,"chat")
    object Inicio: BottomNavItem("Inicio",R.drawable.home,"main")
    object Perfil: BottomNavItem("Perfil",R.drawable.usuario,"perfil")
    //object Config: BottomNavItem("Config",R.drawable.menu,"config")

}