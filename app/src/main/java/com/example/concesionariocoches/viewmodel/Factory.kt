package com.example.concesionariocoches.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.concesionariocoches.repository.CocheRepository

class Factory(private val repository: CocheRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CocheViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CocheViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase erronea")
    }
}