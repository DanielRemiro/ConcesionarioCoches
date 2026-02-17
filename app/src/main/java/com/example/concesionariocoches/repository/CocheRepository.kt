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
    val todasLasMarcas: Flow<List<MarcaEntity>> = dao.getAllMarcas()
    val todosLosClientes: Flow<List<ClienteEntity>> = dao.getAllClientes()

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

            api.actualizarCoche(dto.id, dto)
            android.util.Log.d("API_UPDATE", "Actualización en servidor exitosa")
        } catch (e: Exception) {

            android.util.Log.e("API_UPDATE", "Error al actualizar en API: ${e.message}")
        } finally {

            dao.insertMatricula(matricula)

            val cocheLocal = CocheEntity(
                cocheId = dto.id,
                modelo = dto.modelo,
                precio = dto.precio,
                marcaId = dto.marcaId,
                matriculaId = matricula.matriculaId
            )
            dao.updateCoche(cocheLocal)


            dao.deleteCocheClienteRefs(dto.id)
            dto.clientesIds?.forEach { id ->
                dao.insertCocheClienteRef(CocheClienteCrossRef(dto.id, id))
            }
            android.util.Log.d("LOCAL_UPDATE", "Base de datos local actualizada")
        }
    }

    suspend fun borrarTodoElCoche(cocheCompleto: CocheCompleto) {
        try {

            api.eliminarCoche(cocheCompleto.coche.cocheId)
            android.util.Log.d("API_DELETE", "Coche eliminado del servidor correctamente.")

        } catch (e: Exception) {

            android.util.Log.e(
                "API_DELETE",
                "Error al borrar en API (posiblemente no existe): ${e.message}"
            )
        } finally {

            dao.deleteCoche(cocheCompleto.coche)
            dao.deleteMatricula(cocheCompleto.matricula)
            android.util.Log.d("LOCAL_DELETE", "Coche eliminado de la base de datos local.")
        }
    }
    suspend fun refreshCoches() {
        try {

            dao.deleteAllCoches()
            dao.deleteAllMarcas()
            dao.deleteAllMatriculas()
            dao.deleteAllClientes()
            dao.deleteAllCocheClienteRefs()

            val clientes = api.getClientes()
            clientes.forEach { dto ->
                dao.insertCliente(ClienteEntity(id = dto.id, nombre = dto.nombre, telefono = dto.telefono))
            }

            val marcas = api.getMarcas()
            marcas.forEach { dto ->
                dao.insertMarca(MarcaEntity(marcaId = dto.id, nombre = dto.nombre, pais = dto.pais))
            }

            val matriculas = api.getMatriculas()
            matriculas.forEach { dto ->
                dao.insertMatricula(MatriculaEntity(matriculaId = dto.id, numero = dto.numero, fechaMatriculacion = dto.fechaMatriculacion))
            }

            val coches = api.getCoches()
            coches.forEach { dto ->
                dao.insertCoche(CocheEntity(dto.id, dto.modelo, dto.precio, dto.marcaId, dto.matriculaId))
                dto.clientesIds?.forEach { clienteId ->
                    dao.insertCocheClienteRef(CocheClienteCrossRef(cocheId = dto.id, clienteId = clienteId))
                }
            }
            android.util.Log.d("API_SYNC", "Sincronización limpia completada.")
        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Error sincronizando: ${e.message}")
        }
    }
}