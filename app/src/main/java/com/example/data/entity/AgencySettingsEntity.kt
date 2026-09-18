package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agency_settings")
data class AgencySettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val agencyName: String = "وكالة شجين للسفريات والسياحة",
    val address: String = "صنعاء - شارع خولان - جوار السلامي لمواد البناء",
    val phone1: String = "+967 777779492",
    val phone2: String = "+966 551160835",
    val phone3: String = "+967 774191789",
    val phone4: String = "+967 770038009",
    val announcement: String = "عروض خاصة لموسم العمرة ورحلات النقل الدولي المباشر إلى المملكة العربية السعودية."
)
