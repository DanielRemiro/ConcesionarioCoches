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

/**
 * REPOSITORIO: Centraliza el acceso a los datos de los vehículos.
 * Recibe por constructor la interfaz de la API y el DAO de la base de datos local.
 */
class CocheRepository(
    private val api: ConcesionarioApi,
    private val dao: ConcesionarioDao
) {
    /** * EXPOSICIÓN DE DATOS REACTIVA:
     * Usamos Flow para que la interfaz de usuario se actualice automáticamente
     * cada vez que los datos en la base de datos local cambien.
     */
    val coches: Flow<List<CocheCompleto>> = dao.getCochesCompletos()
    val todasLasMarcas: Flow<List<MarcaEntity>> = dao.getAllMarcas()
    val todosLosClientes: Flow<List<ClienteEntity>> = dao.getAllClientes()

    /**
     * METODO PRIVADO DE APOYO:
     * Convierte los objetos de red (DTO) en entidades de base de datos (Entity)
     * y los guarda de forma estructurada en las tablas locales.
     */
    private suspend fun guardarCocheLocalmente(dto: CocheDto) {
        // Guardamos la marca y la matrícula primero para mantener la integridad referencial
        dto.marca?.let { marcaDto ->
            dao.insertMarca(MarcaEntity(marcaId = marcaDto.id, nombre = marcaDto.nombre, pais = marcaDto.pais))
        }

        dto.matricula?.let { matriculaDto ->
            dao.insertMatricula(MatriculaEntity(matriculaId = matriculaDto.id, numero = matriculaDto.numero, fechaMatriculacion = matriculaDto.fechaMatriculacion))
        }

        // Insertamos el coche principal
        val cocheEntity = CocheEntity(
            cocheId = dto.id,
            modelo = dto.modelo,
            precio = dto.precio,
            marcaId = dto.marcaId,
            matriculaId = dto.matriculaId
        )
        dao.insertCoche(cocheEntity)

        // Gestionamos la relación N:M (Muchos a Muchos) con los clientes interesados
        dto.clientesIds?.forEach { clienteId ->
            dao.insertCocheClienteRef(CocheClienteCrossRef(cocheId = dto.id, clienteId = clienteId))
        }
    }

    /**
     * CREACIÓN DE VEHÍCULO:
     * Intenta registrar el coche en el servidor y, si tiene éxito, lo replica en local.
     */
    suspend fun crearCoche(cocheDto: CocheDto, matriculaNueva: MatriculaEntity) {
        try {
            val nuevoCoche = api.crearCoche(cocheDto) // 1. Llamada al servidor
            dao.insertMatricula(matriculaNueva)      // 2. Guardado local
            guardarCocheLocalmente(nuevoCoche)
        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Error al crear: ${e.message}")
        }
    }

    /**
     * ACTUALIZACION COMPLETA:
     * Aqui aplicamos una politica de "Local First" mediante el bloque 'finally'.
     * Pase lo que pase con la red, la base de datos local se actualiza para que
     * el usuario no perciba errores de conexion.
     */
    suspend fun actualizarCocheCompleto(dto: CocheDto, matricula: MatriculaEntity) {
        try {
            api.actualizarCoche(dto.id, dto) // Intento de actualización remota
        } catch (e: Exception) {
            android.util.Log.e("API_UPDATE", "Fallo en API, pero actualizaremos en local")
        } finally {
            // Este codigo se ejecuta siempre: asegura consistencia en el dispositivo del usuario
            dao.insertMatricula(matricula)
            val cocheLocal = CocheEntity(
                cocheId = dto.id, modelo = dto.modelo, precio = dto.precio,
                marcaId = dto.marcaId, matriculaId = matricula.matriculaId
            )
            dao.updateCoche(cocheLocal)

            // Refrescamos las relaciones con clientes (borramos antiguas y ponemos nuevas)
            dao.deleteCocheClienteRefs(dto.id)
            dto.clientesIds?.forEach { id ->
                dao.insertCocheClienteRef(CocheClienteCrossRef(dto.id, id))
            }
        }
    }

    /**
     * ELIMINACION DE DATOS:
     * Similar a la actualización, priorizamos que el coche desaparezca de la vista
     * del usuario borrandolo de Room, incluso si la API devuelve un error .
     */
    suspend fun borrarTodoElCoche(cocheCompleto: CocheCompleto) {
        try {
            api.eliminarCoche(cocheCompleto.coche.cocheId)
        } catch (e: Exception) {
            android.util.Log.e("API_DELETE", "No se pudo borrar en servidor, procediendo en local")
        } finally {
            dao.deleteCoche(cocheCompleto.coche)
            dao.deleteMatricula(cocheCompleto.matricula)
        }
    }

    /**
     * SINCRONIZACIoN TOTAL (Refresh):
     * Este proceso limpia la base de datos local y descarga  el estado actual del servidor.
     * Es útil para corregir posibles desincronizaciones o cargar datos por primera vez.
     */
    suspend fun refreshCoches() {
        try {
            // 1. Limpieza de tablas locales para evitar datos huérfanos
            dao.deleteAllCoches()
            dao.deleteAllMarcas()
            dao.deleteAllMatriculas()
            dao.deleteAllClientes()
            dao.deleteAllCocheClienteRefs()

            // 2. Descarga secuencial de todas las entidades desde la API
            val clientes = api.getClientes()
            clientes.forEach { dao.insertCliente(ClienteEntity(it.id, it.nombre, it.telefono)) }

            val marcas = api.getMarcas()
            marcas.forEach { dao.insertMarca(MarcaEntity(it.id, it.nombre, it.pais)) }

            val matriculas = api.getMatriculas()
            matriculas.forEach { dao.insertMatricula(MatriculaEntity(it.id, it.numero, it.fechaMatriculacion)) }

            val coches = api.getCoches()
            coches.forEach { dto ->
                dao.insertCoche(CocheEntity(dto.id, dto.modelo, dto.precio, dto.marcaId, dto.matriculaId))
                dto.clientesIds?.forEach { cId ->
                    dao.insertCocheClienteRef(CocheClienteCrossRef(dto.id, cId))
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Error en sincronización completa: ${e.message}")
        }
    }
}