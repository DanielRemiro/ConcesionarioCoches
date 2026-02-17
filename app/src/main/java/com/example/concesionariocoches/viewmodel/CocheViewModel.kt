package com.example.concesionariocoches.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.concesionariocoches.api.dto.CocheDto
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

    val marcasState = repository.todasLasMarcas.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val clientesState = repository.todosLosClientes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
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

}