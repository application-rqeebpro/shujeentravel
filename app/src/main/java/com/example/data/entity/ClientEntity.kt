package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val phone: String,
    val idType: String, // بطاقة شخصية, جواز سفر, بطاقة عائلية
    val idNumber: String,
    val country: String,
    val city: String,
    val district: String,
    val area: String,
    val registeredAt: Long = System.currentTimeMillis()
)
