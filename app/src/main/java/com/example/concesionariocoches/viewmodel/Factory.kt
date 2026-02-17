package com.example.concesionariocoches.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.concesionariocoches.repository.CocheRepository

/**
 * FÁBRICA DE VIEWMODELS: Implementa la interfaz 'ViewModelProvider.Factory'.
 * Su único trabajo es saber cómo instanciar nuestro ViewModel con sus dependencias.
 */
class Factory(private val repository: CocheRepository) : ViewModelProvider.Factory {

    /**
     * MÉTODO CREATE: Es el método que Android llama automáticamente
     * cuando la Activity solicita un ViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // 1. COMPROBACIÓN DE SEGURIDAD: Verificamos que la clase que se nos pide
        // es efectivamente nuestro 'CocheViewModel'.
        if (modelClass.isAssignableFrom(CocheViewModel::class.java)) {

            /** 2. INSTANCIACIÓN MANUAL:
             * Aquí es donde ocurre la "magia". Creamos el ViewModel nosotros mismos
             * pasándole el 'repository' que recibimos en el constructor de la Factory.
             */
            @Suppress("UNCHECKED_CAST")
            return CocheViewModel(repository) as T
        }

        // 3. GESTIÓN DE ERRORES: Si se intenta usar esta fábrica para un ViewModel
        // que no conoce, lanzamos una excepción clara para depurar el error.
        throw IllegalArgumentException("Clase erronea: Esta fábrica solo conoce CocheViewModel")
    }
}