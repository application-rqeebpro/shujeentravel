package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.AgencyNewsEntity
import com.example.data.entity.AgencySettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgencyDao {
    @Query("SELECT * FROM agency_settings WHERE id = 1 LIMIT 1")
    fun getAgencySettings(): Flow<AgencySettingsEntity?>

    @Query("SELECT * FROM agency_settings WHERE id = 1 LIMIT 1")
    suspend fun getAgencySettingsOnce(): AgencySettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAgencySettings(settings: AgencySettingsEntity)

    @Query("SELECT * FROM agency_news ORDER BY createdAt DESC")
    fun getAllNews(): Flow<List<AgencyNewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: AgencyNewsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNews(list: List<AgencyNewsEntity>)

    @Update
    suspend fun updateNews(news: AgencyNewsEntity)

    @Delete
    suspend fun deleteNews(news: AgencyNewsEntity)

    @Query("SELECT COUNT(*) FROM agency_news")
    suspend fun getNewsCount(): Int
}
