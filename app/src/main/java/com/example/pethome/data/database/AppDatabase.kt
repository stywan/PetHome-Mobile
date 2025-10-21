package com.example.pethome.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pethome.data.dao.AppointmentDao
import com.example.pethome.data.dao.PetDao
import com.example.pethome.data.dao.VeterinaryServiceDao
import com.example.pethome.data.model.Appointment
import com.example.pethome.data.model.Pet
import com.example.pethome.data.model.VeterinaryService

@Database(
    entities = [Pet::class, VeterinaryService::class, Appointment::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
    abstract fun veterinaryServiceDao(): VeterinaryServiceDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pethome_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
