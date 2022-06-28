package com.example.dogizzy.model

data class User (
    var ciudad: String? = null,
    var edad: String? = null,
    var nombre: String? = null,
    var bio: String? = null,
    var tags: List<String?>
)