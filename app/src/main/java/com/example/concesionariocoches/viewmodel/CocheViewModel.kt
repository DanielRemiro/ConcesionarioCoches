package com.example.concesionariocoches.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.concesionariocoches.api.dto.CocheDto
import com.example.concesionariocoches.model.coche.CocheEntity
import com.example.concesionariocoches.model.matricula.MatriculaEntity
import com.example.concesionariocoches.model.middle.CocheCompleto
import com.example.concesionariocoches.repository.CocheRepository

class CocheViewModel(private val repository: CocheRepository) : ViewModel() {

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

    fun agregarCoche(dto: CocheDto, matricula: MatriculaEntity) {
        viewModelScope.launch {
            repository.crearCoche(dto, matricula)
        }
    }

    // CocheViewModel.kt

    // Cambiamos CocheEntity por CocheCompleto
    fun eliminarCoche(cocheCompleto: CocheCompleto) {
        viewModelScope.launch {
            repository.borrarTodoElCoche(cocheCompleto)
        }
    }

    fun actualizarCoche(dto: CocheDto, matricula: MatriculaEntity) {
        viewModelScope.launch {
            repository.actualizarCocheCompleto(dto, matricula)
        }
    }


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