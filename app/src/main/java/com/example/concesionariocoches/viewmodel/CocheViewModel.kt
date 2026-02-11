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

    // Estado observable por la UI (CocheCompleto incluye Marca, Matricula y Clientes)
    val cochesState = repository.coches
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshCoches()
        }
    }

    fun agregarCoche(dto: CocheDto) {
        viewModelScope.launch {
            // El repositorio se encarga de desglosar el DTO en las 3 tablas
            repository.crearCoche(dto)
        }
    }

    fun eliminarCoche(coche: CocheEntity) {
        viewModelScope.launch {
            // Al borrar el coche, la tabla intermedia (CrossRef) se limpia sola
            // gracias al onDelete = CASCADE que pusimos en la entidad.
            repository.borrarCoche(coche)
        }
    }

    // Factory para el ViewModel
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