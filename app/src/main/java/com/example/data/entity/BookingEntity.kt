package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long,
    val clientName: String,
    val clientPhone: String,
    val serviceId: Long,
    val serviceTitle: String,
    val serviceCategory: String,
    val travelDate: String,
    val passengersCount: Int = 1,
    val notes: String = "",
    val status: String = "قيد المراجعة", // قيد المراجعة, مؤكد, مكتمل, ملغي
    val createdAt: Long = System.currentTimeMillis()
)
