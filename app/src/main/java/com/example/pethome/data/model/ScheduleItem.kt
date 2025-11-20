package com.example.pethome.data.model

sealed class ScheduleItem(
    open val id: String,
    open val petId: String,
    open val petName: String,
    open val dateTime: Long,
    open val time: String
) {
    data class MedicineSchedule(
        override val id: String,
        override val petId: String,
        override val petName: String,
        val medicineName: String,
        val dosage: String,
        override val dateTime: Long,
        override val time: String
    ) : ScheduleItem(id, petId, petName, dateTime, time)

    data class ServiceAppointment(
        override val id: String,
        override val petId: String,
        override val petName: String,
        val serviceName: String,
        val serviceCategory: String,
        val serviceDescription: String,
        override val dateTime: Long,
        override val time: String,
        val status: String
    ) : ScheduleItem(id, petId, petName, dateTime, time)
}
