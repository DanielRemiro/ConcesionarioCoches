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
    val coches: Flow<List<CocheCompleto>> = dao.getCochesCompletos()
    val clientes: Flow<List<ClienteEntity>> = dao.getAllClientes()
    val todasLasMarcas: Flow<List<MarcaEntity>> = dao.getAllMarcas()
    val todosLosClientes: Flow<List<ClienteEntity>> = dao.getAllClientes()

    suspend fun refreshCoches() {
        try {
            val clientes = api.getClientes()
            clientes.forEach { dto ->
                dao.insertCliente(
                    ClienteEntity(id = dto.id, nombre = dto.nombre, telefono = dto.telefono)
                )
            }

            val marcas = api.getMarcas()
            marcas.forEach { dto ->
                dao.insertMarca(
                    MarcaEntity(marcaId = dto.id, nombre = dto.nombre, pais = dto.pais)
                )
            }

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

            val coches = api.getCoches()
            coches.forEach { dto ->
                // Insertar el Coche
                val cocheEntity = CocheEntity(
                    cocheId = dto.id,
                    modelo = dto.modelo,
                    precio = dto.precio,
                    marcaId = dto.marcaId,
                    matriculaId = dto.matriculaId
                )
                dao.insertCoche(cocheEntity)

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
        dto.marca?.let { marcaDto ->
            dao.insertMarca(
                MarcaEntity(
                    marcaId = marcaDto.id,
                    nombre = marcaDto.nombre,
                    pais = marcaDto.pais
                )
            )
        }

        dto.matricula?.let { matriculaDto ->
            dao.insertMatricula(
                MatriculaEntity(
                    matriculaId = matriculaDto.id,
                    numero = matriculaDto.numero,
                    fechaMatriculacion = matriculaDto.fechaMatriculacion
                )
            )
        }

        val cocheEntity = CocheEntity(
            cocheId = dto.id,
            modelo = dto.modelo,
            precio = dto.precio,
            marcaId = dto.marcaId,
            matriculaId = dto.matriculaId // NUEVO
        )
        dao.insertCoche(cocheEntity)

        dto.clientesIds?.forEach { clienteId ->
            val referencia = CocheClienteCrossRef(
                cocheId = dto.id,
                clienteId = clienteId
            )
            dao.insertCocheClienteRef(referencia)
        }
    }

    suspend fun crearCoche(cocheDto: CocheDto, matriculaNueva: MatriculaEntity) {
        try {
            val nuevoCoche = api.crearCoche(cocheDto)

            dao.insertMatricula(matriculaNueva)

            guardarCocheLocalmente(nuevoCoche)

            cocheDto.clientesIds?.forEach { clienteId ->
                dao.insertCocheClienteRef(CocheClienteCrossRef(nuevoCoche.id, clienteId))
            }
        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Error al crear: ${e.message}")
        }
    }


    suspend fun actualizarCocheCompleto(dto: CocheDto, matricula: MatriculaEntity) {
        try {
            // 1. Intentamos actualizar en el servidor
            api.actualizarCoche(dto.id, dto)
            android.util.Log.d("API_UPDATE", "Actualización en servidor exitosa")
        } catch (e: Exception) {
            // Si falla la API (error 404, red, etc.), lo registramos pero NO detenemos el proceso
            android.util.Log.e("API_UPDATE", "Error al actualizar en API: ${e.message}")
        } finally {
            // 2. ESTO SIEMPRE SE EJECUTA: Actualizamos Room localmente
            // Aquí es donde el nuevo modelo (nombre) se guarda de verdad en el móvil
            dao.insertMatricula(matricula)

            val cocheLocal = CocheEntity(
                cocheId = dto.id,
                modelo = dto.modelo, // Aquí viaja el nuevo nombre
                precio = dto.precio,
                marcaId = dto.marcaId,
                matriculaId = matricula.matriculaId
            )
            dao.updateCoche(cocheLocal)

            // Actualizamos también los clientes interesados
            dao.deleteCocheClienteRefs(dto.id)
            dto.clientesIds?.forEach { id ->
                dao.insertCocheClienteRef(CocheClienteCrossRef(dto.id, id))
            }
            android.util.Log.d("LOCAL_UPDATE", "Base de datos local actualizada")
        }
    }

    suspend fun borrarTodoElCoche(cocheCompleto: CocheCompleto) {
        try {
            // Intentamos borrar en el servidor
            api.eliminarCoche(cocheCompleto.coche.cocheId)
            android.util.Log.d("API_DELETE", "Coche eliminado del servidor correctamente.")
        } catch (e: Exception) {
            // Si el coche no está en la API (error 404) o no hay internet,
            // capturamos el error para que la app NO se cierre.
            android.util.Log.e(
                "API_DELETE",
                "Error al borrar en API (posiblemente no existe): ${e.message}"
            )
        } finally {
            // ESTO ES LO MÁS IMPORTANTE:
            // Pase lo que pase con la API, lo borramos de la base de datos local
            // para que la interfaz se actualice y el coche desaparezca de la lista.
            dao.deleteCoche(cocheCompleto.coche)
            dao.deleteMatricula(cocheCompleto.matricula)
            android.util.Log.d("LOCAL_DELETE", "Coche eliminado de la base de datos local.")
        }
    }
}