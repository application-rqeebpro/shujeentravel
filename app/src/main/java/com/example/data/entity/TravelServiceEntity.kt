package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "travel_services")
data class TravelServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // خدمات السفر والسياحة, الحج والعمرة, حجوزات النقل, البرامج السياحية
    val description: String,
    val price: String,
    val subtitle: String = "",
    val iconType: String = "flight", // flight, visa, kaaba, bus, taxi, hotel, tour, mountain
    val badge: String = "", // e.g. "الأكثر طلباً", "شامل التأشيرة", "VIP"
    val isFeatured: Boolean = true,
    val sortOrder: Int = 0
)
