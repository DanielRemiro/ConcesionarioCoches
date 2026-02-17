package com.example.concesionariocoches.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.concesionariocoches.api.dto.CocheDto
import com.example.concesionariocoches.model.matricula.MatriculaEntity
import com.example.concesionariocoches.model.middle.CocheCompleto
import com.example.concesionariocoches.repository.CocheRepository

/**
 * VIEWMODEL: Gestiona el estado de la UI y la lógica de interacción.
 * Recibe el repositorio por constructor mediante una Factory.
 */
class CocheViewModel(private val repository: CocheRepository) : ViewModel() {

    /** * CONVERSIÓN A STATEFLOW (Patrón Observer):
     * 'stateIn' transforma los flujos de la base de datos en estados observables.
     * 'WhileSubscribed(5000)' es una optimización: si la pantalla no es visible por más
     * de 5 segundos, deja de observar los datos para ahorrar batería.
     */
    val cochesState = repository.coches
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estados para las listas de apoyo necesarias en los diálogos de creación/edición
    val marcasState = repository.todasLasMarcas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clientesState = repository.todosLosClientes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * BLOQUE INIT:
     * Nada más crearse el ViewModel, lanzamos una sincronización inicial
     * para asegurar que el usuario vea datos actualizados al abrir la app.
     */
    init {
        refresh()
    }

    /**
     * OPERACIONES ASÍNCRONAS (Corrutinas):
     * Todas las funciones utilizan 'viewModelScope.launch'.
     * Esto permite que las llamadas a la API o DB se ejecuten en hilos secundarios
     * sin bloquear la fluidez de la interfaz de usuario.
     */

    // 1. Sincronización manual con la API
    fun refresh() {
        viewModelScope.launch {
            repository.refreshCoches()
        }
    }

    // 2. Creación de un nuevo vehículo
    fun agregarCoche(dto: CocheDto, matricula: MatriculaEntity) {
        viewModelScope.launch {
            repository.crearCoche(dto, matricula)
        }
    }

    // 3. Eliminación física y lógica del vehículo
    fun eliminarCoche(cocheCompleto: CocheCompleto) {
        viewModelScope.launch {
            repository.borrarTodoElCoche(cocheCompleto)
        }
    }

    // 4. Actualización de datos existentes
    fun actualizarCoche(dto: CocheDto, matricula: MatriculaEntity) {
        viewModelScope.launch {
            repository.actualizarCocheCompleto(dto, matricula)
        }
    }
}