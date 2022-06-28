package com.example.dogizzy.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class TagsViewModel : ViewModel() {

    private val _tags = mutableStateListOf<String>()
    val tags: MutableList<String> = _tags

    private val _tags2 = mutableStateListOf<String>()
    val tags2: MutableList<String> = _tags2

    fun addElement(tag: String){
        _tags.add(tag)
    }

    fun removeElement(tag: String){
        _tags.remove(tag)
    }

    fun addTagElement(tag: String){
        _tags2.add(tag)
    }

    fun removeTagElement(tag: String){
        _tags2.remove(tag)
    }
}