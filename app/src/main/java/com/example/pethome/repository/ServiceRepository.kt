package com.example.pethome.repository

import com.example.pethome.data.dao.AppointmentDao
import com.example.pethome.data.dao.VeterinaryServiceDao
import com.example.pethome.data.model.Appointment
import com.example.pethome.data.model.VeterinaryService
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ServiceRepository(
    private val serviceDao: VeterinaryServiceDao,
    private val appointmentDao: AppointmentDao
) {

    // Servicios Veterinarios
    fun getAllServices(): Flow<List<VeterinaryService>> {
        return serviceDao.getAllServices()
    }

    suspend fun getServiceById(serviceId: String): VeterinaryService? {
        return serviceDao.getServiceById(serviceId)
    }

    fun getServicesByCategory(category: String): Flow<List<VeterinaryService>> {
        return serviceDao.getServicesByCategory(category)
    }

    suspend fun initializeSampleServices() {
        val sampleServices = listOf(
            VeterinaryService(
                id = "1",
                name = "Consulta General",
                shortDescription = "Revisión médica completa de tu mascota",
                description = "Consulta veterinaria general que incluye examen físico completo, revisión de signos vitales, evaluación de comportamiento y recomendaciones de salud. Ideal para chequeos rutinarios y seguimiento del bienestar de tu mascota.",
                price = 25000.0,
                duration = 30,
                category = "Consulta"
            ),
            VeterinaryService(
                id = "2",
                name = "Vacunación",
                shortDescription = "Aplicación de vacunas esenciales",
                description = "Servicio de vacunación completo que incluye la aplicación de vacunas esenciales según el calendario de inmunización. Protege a tu mascota contra enfermedades virales y bacterianas comunes. Incluye certificado de vacunación.",
                price = 15000.0,
                duration = 20,
                category = "Prevención"
            ),
            VeterinaryService(
                id = "3",
                name = "Desparasitación",
                shortDescription = "Tratamiento contra parásitos internos y externos",
                description = "Tratamiento completo para eliminar parásitos internos (lombrices) y externos (pulgas, garrapatas). Incluye evaluación del estado de salud, aplicación del tratamiento y recomendaciones de prevención.",
                price = 12000.0,
                duration = 15,
                category = "Prevención"
            ),
            VeterinaryService(
                id = "4",
                name = "Baño y Peluquería",
                shortDescription = "Servicio completo de estética canina",
                description = "Servicio de estética que incluye baño con shampoo especializado, secado, corte de pelo según raza, limpieza de oídos, corte de uñas y perfumado. Tu mascota quedará limpia, bonita y con olor agradable.",
                price = 20000.0,
                duration = 60,
                category = "Estética"
            ),
            VeterinaryService(
                id = "5",
                name = "Cirugía de Esterilización",
                shortDescription = "Procedimiento quirúrgico para esterilizar",
                description = "Cirugía de esterilización (castración o ovariohisterectomía) realizada por veterinario especializado. Incluye pre-operatorio, anestesia general, cirugía, recuperación post-operatoria y medicamentos. Contribuye al control poblacional y previene enfermedades.",
                price = 80000.0,
                duration = 120,
                category = "Cirugía"
            ),
            VeterinaryService(
                id = "6",
                name = "Consulta Especializada",
                shortDescription = "Atención con médico especialista",
                description = "Consulta con médico veterinario especializado en áreas específicas como dermatología, cardiología, oftalmología u ortopedia. Incluye evaluación detallada, diagnóstico especializado y plan de tratamiento personalizado.",
                price = 45000.0,
                duration = 45,
                category = "Especializada"
            ),
            VeterinaryService(
                id = "7",
                name = "Exámenes de Laboratorio",
                shortDescription = "Análisis clínicos y diagnósticos",
                description = "Servicio de laboratorio que incluye análisis de sangre, orina, heces y otros estudios diagnósticos. Permite detectar enfermedades de forma temprana y monitorear la salud de tu mascota. Resultados en 24-48 horas.",
                price = 35000.0,
                duration = 30,
                category = "Diagnóstico"
            ),
            VeterinaryService(
                id = "8",
                name = "Urgencias 24/7",
                shortDescription = "Atención de emergencia las 24 horas",
                description = "Servicio de urgencias veterinarias disponible las 24 horas del día, los 7 días de la semana. Atención inmediata para casos de emergencia como accidentes, intoxicaciones, dificultad respiratoria o síntomas graves. Equipo médico siempre disponible.",
                price = 50000.0,
                duration = 60,
                category = "Urgencia"
            )
        )

        sampleServices.forEach { service ->
            serviceDao.insertService(service)
        }
    }

    // Citas/Reservas
    fun getAppointmentsByUser(userId: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsByUser(userId)
    }

    fun getAppointmentsByPet(petId: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsByPet(petId)
    }

    suspend fun createAppointment(appointment: Appointment): Result<Appointment> {
        return try {
            val newAppointment = appointment.copy(id = UUID.randomUUID().toString())
            appointmentDao.insertAppointment(newAppointment)
            Result.success(newAppointment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAppointment(appointment: Appointment): Result<Appointment> {
        return try {
            appointmentDao.updateAppointment(appointment)
            Result.success(appointment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelAppointment(appointmentId: String): Result<Unit> {
        return try {
            val appointment = appointmentDao.getAppointmentById(appointmentId)
            if (appointment != null) {
                appointmentDao.updateAppointment(appointment.copy(status = "Cancelada"))
                Result.success(Unit)
            } else {
                Result.failure(Exception("Cita no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
