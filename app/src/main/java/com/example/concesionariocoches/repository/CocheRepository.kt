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
            // --- FASE 1: DATOS INDEPENDIENTES (Sin Foreign Keys) ---

            // 1. Clientes
            val clientes = api.getClientes()
            clientes.forEach { dto ->
                dao.insertCliente(
                    ClienteEntity(id = dto.id, nombre = dto.nombre, telefono = dto.telefono)
                )
            }

            // 2. Marcas
            val marcas = api.getMarcas()
            marcas.forEach { dto ->
                dao.insertMarca(
                    MarcaEntity(marcaId = dto.id, nombre = dto.nombre, pais = dto.pais)
                )
            }

            // 3. Matrículas
            val matriculas = api.getMatriculas()
            matriculas.forEach { dto ->
                dao.insertMatricula(
                    MatriculaEntity(
                        matriculaId = dto.id,
                        numero = dto.numero,
                        fechaMatriculacion = dto.fechaMatriculacion
                    )
                )
            }

            // --- FASE 2: DATOS DEPENDIENTES (Coches) ---

            // 4. Coches (Ahora sí es seguro insertarlos porque sus "padres" ya existen)
            val coches = api.getCoches()
            coches.forEach { dto ->
                // Insertar el Coche
                val cocheEntity = CocheEntity(
                    cocheId = dto.id,
                    modelo = dto.modelo,
                    precio = dto.precio,
                    marcaId = dto.marcaId,       // Room ya encontrará esta marcaId en la BD
                    matriculaId = dto.matriculaId // Room ya encontrará esta matriculaId
                )
                dao.insertCoche(cocheEntity)

                // 5. Relaciones N:M (Coche <-> Cliente)
                dto.clientesIds?.forEach { clienteId ->
                    dao.insertCocheClienteRef(
                        CocheClienteCrossRef(cocheId = dto.id, clienteId = clienteId)
                    )
                }
            }

            android.util.Log.d("API_SYNC", "Sincronización exitosa completa.")

        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Error crítico sincronizando: ${e.message}")
            e.printStackTrace()
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
    suspend fun actualizarCoche(cocheDto: CocheDto) {
        try {
            // 1. Actualización en la API
            api.actualizarCoche(cocheDto.id, cocheDto)

            // 2. Mapear DTO a Entity para Room
            val cocheEntity = CocheEntity(
                cocheId = cocheDto.id,
                modelo = cocheDto.modelo,
                precio = cocheDto.precio,
                marcaId = cocheDto.marcaId,
                matriculaId = cocheDto.matriculaId
            )

            // 3. Actualizar en la BD local
            // Usamos updateCoche que ya definiste en el DAO
            dao.updateCoche(cocheEntity)

            // 4. Opcional: Si el DTO trae nuevos clientes, actualizamos la tabla intermedia
            cocheDto.clientesIds?.forEach { clienteId ->
                dao.insertCocheClienteRef(CocheClienteCrossRef(cocheDto.id, clienteId))
            }

        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Error al actualizar: ${e.message}")
        }
    }
}