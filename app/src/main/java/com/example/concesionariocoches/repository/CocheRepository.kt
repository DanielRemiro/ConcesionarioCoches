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

    suspend fun crearCoche(cocheDto: CocheDto, matriculaNueva: MatriculaEntity) {
        try {
            // 1. Enviar a la API (asumiendo que la API maneja la creación de la matrícula o el DTO la incluye)
            val nuevoCoche = api.crearCoche(cocheDto)

            // 2. Insertar Matrícula Localmente primero (para tener el ID disponible)
            dao.insertMatricula(matriculaNueva)

            // 3. Guardar el Coche y sus relaciones
            guardarCocheLocalmente(nuevoCoche)

            // 4. Relaciones N:M con Clientes
            cocheDto.clientesIds?.forEach { clienteId ->
                dao.insertCocheClienteRef(CocheClienteCrossRef(nuevoCoche.id, clienteId))
            }
        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "Error al crear: ${e.message}")
        }
    }

    suspend fun borrarCoche(cocheCompleto: CocheCompleto) {
        try {
            // 1. Intentar borrar en la API
            api.eliminarCoche(cocheCompleto.coche.cocheId)
        } catch (e: Exception) {
            // Log error
        }

        // 2. Borrar el coche de la BD local
        dao.deleteCoche(cocheCompleto.coche)

        // 3. Borrar la matrícula asociada (esto es lo que falta)
        // Necesitas añadir una función deleteMatricula en tu DAO
        dao.deleteMatricula(cocheCompleto.matricula)
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
    suspend fun actualizarCocheCompleto(dto: CocheDto, matricula: MatriculaEntity) {
        try {
            api.actualizarCoche(dto.id, dto)

            dao.insertMatricula(matricula) // REPLACE actúa como update
            dao.updateCoche(CocheEntity(dto.id, dto.modelo, dto.precio, dto.marcaId, matricula.matriculaId))

            // Actualizar interesados: limpiar y reinsertar
            dao.deleteCocheClienteRefs(dto.id)
            dto.clientesIds?.forEach { id ->
                dao.insertCocheClienteRef(CocheClienteCrossRef(dto.id, id))
            }
        } catch (e: Exception) { /* Manejar error */ }
    }
    suspend fun crearCocheCompleto(dto: CocheDto, matricula: MatriculaEntity) {
        try {
            // 1. API: Crear coche (opcional enviar a API primero)
            val nuevoCoche = api.crearCoche(dto)

            // 2. Local: Insertar la Matrícula nueva
            dao.insertMatricula(matricula)

            // 3. Local: Insertar el Coche vinculando el ID de la matrícula
            val cocheEntity = CocheEntity(
                cocheId = nuevoCoche.id,
                modelo = dto.modelo,
                precio = dto.precio,
                marcaId = dto.marcaId,
                matriculaId = matricula.matriculaId
            )
            dao.insertCoche(cocheEntity)

            // 4. Local: Vincular Clientes seleccionados
            dto.clientesIds?.forEach { clienteId ->
                dao.insertCocheClienteRef(CocheClienteCrossRef(nuevoCoche.id, clienteId))
            }
        } catch (e: Exception) { /* Manejar error */ }
    }
    suspend fun borrarTodoElCoche(cocheCompleto: CocheCompleto) {
        api.eliminarCoche(cocheCompleto.coche.cocheId)
        dao.deleteCoche(cocheCompleto.coche)
        dao.deleteMatricula(cocheCompleto.matricula)
    }
}