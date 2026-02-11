package com.example.concesionariocoches.repository


import com.example.concesionariocoches.api.ConcesionarioApi
import com.example.concesionariocoches.api.dto.CocheDto
import com.example.concesionariocoches.data.dao.ConcesionarioDao
import kotlinx.coroutines.flow.Flow
import com.example.concesionariocoches.model.coche.CocheEntity
import com.example.concesionariocoches.model.marca.MarcaEntity
import com.example.concesionariocoches.model.motor.MotorEntity
import com.example.concesionariocoches.model.middle.CocheCompleto

class CocheRepository(
    private val api: ConcesionarioApi,
    private val dao: ConcesionarioDao
) {
    // La UI observa esto. Siempre muestra lo que hay en BD local.
    val coches: Flow<List<CocheCompleto>> = dao.getCochesCompletos()

    // Lógica de sincronización: API -> Base de Datos
    suspend fun refreshCoches() {
        try {
            val cochesRemotos = api.getCoches() //
            // Limpiamos caché antigua (opcional, depende de tu lógica de negocio)
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
        dto.marca?.let { marcaDto ->
            dao.insertMarca(MarcaEntity(marcaId = marcaDto.id, nombre = marcaDto.nombre, pais = marcaDto.pais))
        }

        // 2. Guardar o actualizar Motor relacionado
        dto.motor?.let { motorDto ->
            dao.insertMotor(MotorEntity(motorId = motorDto.id, combustible = motorDto.combustible, potencia = motorDto.potencia))
        }

        // 3. Guardar el Coche
        val cocheEntity = CocheEntity(
            id = dto.id,
            modelo = dto.modelo,
            precio = dto.precio,
            descripcion = dto.descripcion,
            marcaId = dto.marcaId,
            motorId = dto.motorId
        )
        dao.insertCoche(cocheEntity)

        // Aquí podrías insertar también las relaciones N:M con clientes si vinieran en el DTO
    }

    suspend fun crearCoche(cocheDto: CocheDto) {
        // POST a la API
        val nuevoCoche = api.crearCoche(cocheDto) //
        // Guardar en BD local
        guardarCocheLocalmente(nuevoCoche)
    }

    suspend fun borrarCoche(coche: CocheEntity) {
        // DELETE en API
        try {
            api.eliminarCoche(coche.id) //
        } catch (e: Exception) {
            // Si falla la API, decidimos si borrar localmente o no.
            // Para este ejercicio borramos localmente también.
        }
        // DELETE en BD
        dao.deleteCoche(coche)
    }
}