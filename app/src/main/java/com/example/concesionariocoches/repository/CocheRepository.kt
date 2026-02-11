package com.example.concesionariocoches.repository

import com.example.concesionariocoches.api.ConcesionarioApi
import com.example.concesionariocoches.api.dto.CocheDto
import com.example.concesionariocoches.data.dao.ConcesionarioDao
import kotlinx.coroutines.flow.Flow
import com.example.concesionariocoches.model.coche.CocheEntity
import com.example.concesionariocoches.model.marca.MarcaEntity
import com.example.concesionariocoches.model.matricula.MatriculaEntity
import com.example.concesionariocoches.model.middle.CocheCompleto
import com.example.concesionariocoches.model.cross.CocheClienteCrossRef
import com.example.concesionariocoches.model.cliente.ClienteEntity

class CocheRepository(
    private val api: ConcesionarioApi,
    private val dao: ConcesionarioDao
) {
    // La UI observa esto. Siempre muestra lo que hay en BD local.
    // Esto devolverá el Coche + Marca + Matrícula + Lista de Clientes
    val coches: Flow<List<CocheCompleto>> = dao.getCochesCompletos()

    // Lógica de sincronización: API -> Base de Datos
    suspend fun refreshCoches() {
        try {
            // Asumimos que la API devuelve una lista de DTOs completos
            val cochesRemotos = api.getCoches()

            // Opcional: Limpiar BD para evitar datos huerfanos si se borraron en el servidor
            // dao.deleteAllCoches()

            cochesRemotos.forEach { dto ->
                guardarCocheLocalmente(dto)
            }
        } catch (e: Exception) {
            e.printStackTrace() // Manejar error de red
        }
    }

    private suspend fun guardarCocheLocalmente(dto: CocheDto) {
        // 1. Guardar o actualizar Marca relacionada
        // (Asumiendo que el DTO trae el objeto marca completo, o al menos sus datos)
        dto.marca?.let { marcaDto ->
            dao.insertMarca(
                MarcaEntity(
                    marcaId = marcaDto.id,
                    nombre = marcaDto.nombre,
                    pais = marcaDto.pais
                )
            )
        }

        // 2. Guardar o actualizar Matrícula relacionada (NUEVO)
        dto.matricula?.let { matriculaDto ->
            dao.insertMatricula(
                MatriculaEntity(
                    matriculaId = matriculaDto.id,
                    numero = matriculaDto.numero,
                    fechaMatriculacion = matriculaDto.fechaMatriculacion
                )
            )
        }

        // 3. Guardar el Coche
        // Nota: Asegúrate de que dto.marcaId y dto.matriculaId no sean nulos
        val cocheEntity = CocheEntity(
            cocheId = dto.id,
            modelo = dto.modelo,
            precio = dto.precio,
            // descripcion = dto.descripcion, // ELIMINADO
            marcaId = dto.marcaId,
            matriculaId = dto.matriculaId // NUEVO
        )
        dao.insertCoche(cocheEntity)

        // 4. Guardar relaciones N:M con Clientes (NUEVO)
        // Si el DTO trae una lista de IDs de clientes interesados:
        dto.clientesIds?.forEach { clienteId ->
            // Primero aseguramos que el cliente exista (si el DTO trae datos del cliente)
            // Si solo trae IDs, asumimos que el cliente ya se sincronizó en otro proceso
            // o insertamos solo la referencia.

            val referencia = CocheClienteCrossRef(
                cocheId = dto.id,
                clienteId = clienteId
            )
            dao.insertCocheClienteRef(referencia)
        }
    }

    suspend fun crearCoche(cocheDto: CocheDto) {
        // POST a la API
        val nuevoCoche = api.crearCoche(cocheDto)
        // Guardar en BD local lo que nos devolvió la API
        guardarCocheLocalmente(nuevoCoche)
    }

    suspend fun borrarCoche(coche: CocheEntity) {
        // DELETE en API
        try {
            api.eliminarCoche(coche.cocheId)
        } catch (e: Exception) {
            // Log error
        }
        // DELETE en BD
        dao.deleteCoche(coche)
    }
}