package com.example.concesionariocoches.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.concesionariocoches.api.dto.CocheDto
import com.example.concesionariocoches.model.coche.CocheEntity
import com.example.concesionariocoches.repository.CocheRepository

class CocheViewModel(private val repository: CocheRepository) : ViewModel() {

    // Estado observable por la UI
    val cochesState = repository.coches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Cargar datos al iniciar
        viewModelScope.launch {
            repository.refreshCoches()
        }
    }

    fun agregarCoche(dto: CocheDto) {
        viewModelScope.launch {
            repository.crearCoche(dto)
        }
    }

    fun eliminarCoche(coche: CocheEntity) {
        viewModelScope.launch {
            repository.borrarCoche(coche)
        }
    }

    // Factory para inyectar dependencias manualmente
    class Factory(private val repository: CocheRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CocheViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CocheViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}