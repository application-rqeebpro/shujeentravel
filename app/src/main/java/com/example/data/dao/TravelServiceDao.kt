package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.TravelServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelServiceDao {
    @Query("SELECT * FROM travel_services ORDER BY sortOrder ASC, id DESC")
    fun getAllServices(): Flow<List<TravelServiceEntity>>

    @Query("SELECT * FROM travel_services WHERE category = :category ORDER BY sortOrder ASC, id DESC")
    fun getServicesByCategory(category: String): Flow<List<TravelServiceEntity>>

    @Query("SELECT * FROM travel_services WHERE isFeatured = 1 ORDER BY sortOrder ASC")
    fun getFeaturedServices(): Flow<List<TravelServiceEntity>>

    @Query("SELECT * FROM travel_services WHERE id = :id LIMIT 1")
    suspend fun getServiceById(id: Long): TravelServiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: TravelServiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<TravelServiceEntity>)

    @Update
    suspend fun updateService(service: TravelServiceEntity)

    @Delete
    suspend fun deleteService(service: TravelServiceEntity)

    @Query("SELECT COUNT(*) FROM travel_services")
    suspend fun getCount(): Int
}
