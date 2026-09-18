package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AgencyDao
import com.example.data.dao.BookingDao
import com.example.data.dao.ClientDao
import com.example.data.dao.TravelServiceDao
import com.example.data.entity.AgencyNewsEntity
import com.example.data.entity.AgencySettingsEntity
import com.example.data.entity.BookingEntity
import com.example.data.entity.ClientEntity
import com.example.data.entity.TravelServiceEntity

@Database(
    entities = [
        ClientEntity::class,
        TravelServiceEntity::class,
        BookingEntity::class,
        AgencySettingsEntity::class,
        AgencyNewsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun travelServiceDao(): TravelServiceDao
    abstract fun bookingDao(): BookingDao
    abstract fun agencyDao(): AgencyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shajeen_travel.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
