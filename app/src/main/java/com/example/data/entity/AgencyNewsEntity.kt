package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agency_news")
data class AgencyNewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val dateText: String,
    val tag: String = "إعلان رسمي",
    val createdAt: Long = System.currentTimeMillis()
)
