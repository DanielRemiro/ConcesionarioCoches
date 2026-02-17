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

    suspend fun borrarCoche(cocheCompleto: CocheCompleto) {
        try {
            api.eliminarCoche(cocheCompleto.coche.cocheId)
        } catch (e: Exception) {

        }

        dao.deleteCoche(cocheCompleto.coche)

        dao.deleteMatricula(cocheCompleto.matricula)
    }
    suspend fun actualizarCoche(cocheDto: CocheDto) {
        try {
            api.actualizarCoche(cocheDto.id, cocheDto)

            val cocheEntity = CocheEntity(
                cocheId = cocheDto.id,
                modelo = cocheDto.modelo,
                precio = cocheDto.precio,
                marcaId = cocheDto.marcaId,
                matriculaId = cocheDto.matriculaId
            )

            dao.updateCoche(cocheEntity)

            cocheDto.clientesIds?.forEach { clienteId ->
                dao.insertCocheClienteRef(CocheClienteCrossRef(cocheDto.id, clienteId))
            }

        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Error al actualizar: ${e.message}")
        }
    }
    suspend fun actualizarCocheCompleto(dto: CocheDto, matricula: MatriculaEntity) {
        try {
            api.actualizarCoche(dto.id, dto)

            dao.insertMatricula(matricula)
            dao.updateCoche(CocheEntity(dto.id, dto.modelo, dto.precio, dto.marcaId, matricula.matriculaId))

            dao.deleteCocheClienteRefs(dto.id)
            dto.clientesIds?.forEach { id ->
                dao.insertCocheClienteRef(CocheClienteCrossRef(dto.id, id))
            }
        } catch (e: Exception) {  }
    }
    suspend fun crearCocheCompleto(dto: CocheDto, matricula: MatriculaEntity) {
        try {
            val nuevoCoche = api.crearCoche(dto)

            dao.insertMatricula(matricula)

            val cocheEntity = CocheEntity(
                cocheId = nuevoCoche.id,
                modelo = dto.modelo,
                precio = dto.precio,
                marcaId = dto.marcaId,
                matriculaId = matricula.matriculaId
            )
            dao.insertCoche(cocheEntity)

            dto.clientesIds?.forEach { clienteId ->
                dao.insertCocheClienteRef(CocheClienteCrossRef(nuevoCoche.id, clienteId))
            }
        } catch (e: Exception) {  }
    }
    suspend fun borrarTodoElCoche(cocheCompleto: CocheCompleto) {
        api.eliminarCoche(cocheCompleto.coche.cocheId)
        dao.deleteCoche(cocheCompleto.coche)
        dao.deleteMatricula(cocheCompleto.matricula)
    }
}