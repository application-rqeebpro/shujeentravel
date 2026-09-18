package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.AgencyNewsEntity
import com.example.data.entity.AgencySettingsEntity
import com.example.data.entity.BookingEntity
import com.example.data.entity.ClientEntity
import com.example.data.entity.TravelServiceEntity
import com.example.ui.components.dialPhone
import com.example.ui.components.getServiceIcon
import com.example.ui.theme.ShajeenDarkBlue
import com.example.ui.theme.ShajeenGold
import com.example.ui.theme.ShajeenSkyBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    services: List<TravelServiceEntity>,
    clients: List<ClientEntity>,
    bookings: List<BookingEntity>,
    newsList: List<AgencyNewsEntity>,
    agencySettings: AgencySettingsEntity?,
    onSaveService: (TravelServiceEntity) -> Unit,
    onDeleteService: (TravelServiceEntity) -> Unit,
    onDeleteClient: (ClientEntity) -> Unit,
    onUpdateBookingStatus: (bookingId: Long, status: String) -> Unit,
    onUpdateAgencySettings: (
        address: String,
        phone1: String,
        phone2: String,
        phone3: String,
        phone4: String,
        announcement: String
    ) -> Unit,
    onAddNews: (title: String, content: String, dateText: String, tag: String) -> Unit,
    onDeleteNews: (AgencyNewsEntity) -> Unit,
    onLogoutAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "الخدمات (${services.size})",
        "الحجوزات (${bookings.size})",
        "العملاء (${clients.size})",
        "بيانات الوكالة",
        "الأخبار"
    )

    // Service Add/Edit Dialog State
    var showServiceDialog by remember { mutableStateOf(false) }
    var editingService by remember { mutableStateOf<TravelServiceEntity?>(null) }

    // News Add Dialog State
    var showNewsDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Admin Top Header
            Surface(
                color = ShajeenDarkBlue,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = ShajeenGold,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "لوحة تحكم المالك والإدارة",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "وكالة شجين للسفريات والسياحة",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ShajeenGold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = onLogoutAdmin,
                            modifier = Modifier.testTag("admin_logout_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "خروج من لوحة الإدارة",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tab Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = (selectedTab == index)
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedTab = index },
                                label = {
                                    Text(
                                        text = title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ShajeenGold,
                                    selectedLabelColor = ShajeenDarkBlue,
                                    containerColor = Color.White.copy(alpha = 0.15f),
                                    labelColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("admin_tab_$index")
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Services Tab
                    ServicesAdminTab(
                        services = services,
                        onAddNew = {
                            editingService = null
                            showServiceDialog = true
                        },
                        onEdit = {
                            editingService = it
                            showServiceDialog = true
                        },
                        onDelete = onDeleteService
                    )
                }
                1 -> {
                    // Bookings Tab
                    BookingsAdminTab(
                        bookings = bookings,
                        onStatusChange = onUpdateBookingStatus
                    )
                }
                2 -> {
                    // Clients Tab
                    ClientsAdminTab(
                        clients = clients,
                        onDeleteClient = onDeleteClient
                    )
                }
                3 -> {
                    // Agency Settings Tab
                    AgencySettingsAdminTab(
                        agencySettings = agencySettings,
                        onSave = { address, p1, p2, p3, p4, ann ->
                            onUpdateAgencySettings(address, p1, p2, p3, p4, ann)
                            Toast.makeText(context, "تم حفظ بيانات الوكالة بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                4 -> {
                    // News Tab
                    NewsAdminTab(
                        newsList = newsList,
                        onAddNew = { showNewsDialog = true },
                        onDeleteNews = onDeleteNews
                    )
                }
            }
        }

        // Add/Edit Service Dialog
        if (showServiceDialog) {
            ServiceEditDialog(
                existingService = editingService,
                onDismiss = {
                    showServiceDialog = false
                    editingService = null
                },
                onSave = { service ->
                    onSaveService(service)
                    showServiceDialog = false
                    editingService = null
                    Toast.makeText(context, "تم حفظ الخدمة بنجاح", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Add News Dialog
        if (showNewsDialog) {
            NewsAddDialog(
                onDismiss = { showNewsDialog = false },
                onConfirm = { title, content, date, tag ->
                    onAddNews(title, content, date, tag)
                    showNewsDialog = false
                    Toast.makeText(context, "تم نشر الخبر بنجاح", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

// ---------------- 1. Services Admin Tab ----------------
@Composable
fun ServicesAdminTab(
    services: List<TravelServiceEntity>,
    onAddNew: () -> Unit,
    onEdit: (TravelServiceEntity) -> Unit,
    onDelete: (TravelServiceEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "إدارة باقات وخدمات الوكالة",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = onAddNew,
                    colors = ButtonDefaults.buttonColors(containerColor = ShajeenDarkBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_service_fab_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة خدمة جديدة", fontSize = 12.sp)
                }
            }
        }

        items(services, key = { it.id }) { service ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ShajeenSkyBlue.copy(alpha = 0.12f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = getServiceIcon(service.iconType),
                                contentDescription = null,
                                tint = ShajeenDarkBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = service.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${service.category} • ${service.price}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ShajeenSkyBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Row {
                        IconButton(onClick = { onEdit(service) }) {
                            Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = ShajeenSkyBlue)
                        }
                        IconButton(onClick = { onDelete(service) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }
    }
}

// ---------------- 2. Bookings Admin Tab ----------------
@Composable
fun BookingsAdminTab(
    bookings: List<BookingEntity>,
    onStatusChange: (Long, String) -> Unit
) {
    val statuses = listOf("مؤكد", "قيد المراجعة", "مكتمل", "ملغي")
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "جميع طلبات الحجز الواردة من العملاء",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (bookings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد أي حجوزات مسجلة حتى الآن", color = Color.Gray)
                }
            }
        } else {
            items(bookings, key = { it.id }) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = booking.clientName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = booking.serviceTitle,
                                    style = MaterialTheme.typography.bodySmall.copy(color = ShajeenSkyBlue)
                                )
                            }

                            Button(
                                onClick = { dialPhone(context, booking.clientPhone) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ShajeenSkyBlue),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("اتصال بالعميل", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "الهاتف: ${booking.clientPhone} • التاريخ: ${booking.travelDate} • الأفراد: ${booking.passengersCount}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )

                        if (booking.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ملاحظات: ${booking.notes}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Change Status Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "تعديل الحالة:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                statuses.forEach { st ->
                                    val isCurrent = (booking.status == st)
                                    Surface(
                                        onClick = { onStatusChange(booking.id, st) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCurrent) ShajeenDarkBlue else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = st,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- 3. Clients Admin Tab ----------------
@Composable
fun ClientsAdminTab(
    clients: List<ClientEntity>,
    onDeleteClient: (ClientEntity) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "قائمة العملاء والمسافرين المسجلين (${clients.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (clients.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("لا يوجد عملاء مسجلين حالياً", color = Color.Gray)
                }
            }
        } else {
            items(clients, key = { it.id }) { client ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = client.fullName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${client.idType}: ${client.idNumber}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ShajeenSkyBlue)
                                )
                            }

                            Row {
                                IconButton(onClick = { dialPhone(context, client.phone) }) {
                                    Icon(Icons.Default.Phone, contentDescription = "اتصال", tint = ShajeenSkyBlue)
                                }
                                IconButton(onClick = { onDeleteClient(client) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red.copy(alpha = 0.8f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "العنوان: ${client.country} - ${client.city} - ${client.district} (${client.area})",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                        )
                        Text(
                            text = "رقم الهاتف: ${client.phone}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        )
                    }
                }
            }
        }
    }
}

// ---------------- 4. Agency Settings Admin Tab ----------------
@Composable
fun AgencySettingsAdminTab(
    agencySettings: AgencySettingsEntity?,
    onSave: (address: String, p1: String, p2: String, p3: String, p4: String, announcement: String) -> Unit
) {
    var address by remember(agencySettings) {
        mutableStateOf(agencySettings?.address ?: "صنعاء - شارع خولان - جوار السلامي لمواد البناء")
    }
    var phone1 by remember(agencySettings) {
        mutableStateOf(agencySettings?.phone1 ?: "+967 777779492")
    }
    var phone2 by remember(agencySettings) {
        mutableStateOf(agencySettings?.phone2 ?: "+966 551160835")
    }
    var phone3 by remember(agencySettings) {
        mutableStateOf(agencySettings?.phone3 ?: "+967 774191789")
    }
    var phone4 by remember(agencySettings) {
        mutableStateOf(agencySettings?.phone4 ?: "+967 770038009")
    }
    var announcement by remember(agencySettings) {
        mutableStateOf(agencySettings?.announcement ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "تحديث بيانات التواصل وعنوان الوكالة",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("عنوان الوكالة الرئيسي *") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "أرقام التواصل الأربعة المعتمدة:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )

        OutlinedTextField(
            value = phone1,
            onValueChange = { phone1 = it },
            label = { Text("رقم التواصل 1 (واتساب / اتصال)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone2,
            onValueChange = { phone2 = it },
            label = { Text("رقم التواصل 2 (المملكة العربية السعودية)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone3,
            onValueChange = { phone3 = it },
            label = { Text("رقم التواصل 3 (خدمة العملاء)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone4,
            onValueChange = { phone4 = it },
            label = { Text("رقم التواصل 4 (الحجوزات السريعة)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = announcement,
            onValueChange = { announcement = it },
            label = { Text("شريط الإعلانات والتنبيهات للعملاء") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onSave(address.trim(), phone1.trim(), phone2.trim(), phone3.trim(), phone4.trim(), announcement.trim())
            },
            colors = ButtonDefaults.buttonColors(containerColor = ShajeenDarkBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_agency_settings_btn")
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("حفظ التحديثات", fontWeight = FontWeight.Bold)
        }
    }
}

// ---------------- 5. News Admin Tab ----------------
@Composable
fun NewsAdminTab(
    newsList: List<AgencyNewsEntity>,
    onAddNew: () -> Unit,
    onDeleteNews: (AgencyNewsEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "أخبار وإعلانات الوكالة",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = onAddNew,
                    colors = ButtonDefaults.buttonColors(containerColor = ShajeenDarkBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_news_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نشر إعلان جديد", fontSize = 12.sp)
                }
            }
        }

        items(newsList, key = { it.id }) { news ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = news.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ShajeenDarkBlue
                            )
                        )
                        IconButton(onClick = { onDeleteNews(news) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red.copy(alpha = 0.8f))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = news.content,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${news.tag} • ${news.dateText}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
                    )
                }
            }
        }
    }
}

// ---------------- Service Edit / Add Dialog ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceEditDialog(
    existingService: TravelServiceEntity?,
    onDismiss: () -> Unit,
    onSave: (TravelServiceEntity) -> Unit
) {
    val categories = listOf(
        "خدمات السفر والسياحة",
        "الحج والعمرة",
        "حجوزات النقل",
        "البرامج السياحية"
    )

    val iconTypes = listOf("flight", "visa", "kaaba", "bus", "taxi", "hotel", "tour")

    var title by remember { mutableStateOf(existingService?.title ?: "") }
    var category by remember { mutableStateOf(existingService?.category ?: categories[0]) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var subtitle by remember { mutableStateOf(existingService?.subtitle ?: "") }
    var description by remember { mutableStateOf(existingService?.description ?: "") }
    var price by remember { mutableStateOf(existingService?.price ?: "") }
    var badge by remember { mutableStateOf(existingService?.badge ?: "") }
    var iconType by remember { mutableStateOf(existingService?.iconType ?: "flight") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (existingService == null) "إضافة خدمة سفر جديدة" else "تعديل الخدمة",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم الخدمة *") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("القسم / الفئة *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("الوصف المختصر") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("السعر أو التكلفة المقدرة *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = badge,
                    onValueChange = { badge = it },
                    label = { Text("شارة الخدمة (مثال: الأكثر طلباً / VIP)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("تفاصيل الخدمة والشروط") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("إلغاء")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(
                                    TravelServiceEntity(
                                        id = existingService?.id ?: 0L,
                                        title = title.trim(),
                                        category = category,
                                        subtitle = subtitle.trim(),
                                        description = description.trim(),
                                        price = price.trim().ifEmpty { "حسب الطلب" },
                                        badge = badge.trim(),
                                        iconType = iconType
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ShajeenDarkBlue),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("حفظ")
                    }
                }
            }
        }
    }
}

// ---------------- News Add Dialog ----------------
@Composable
fun NewsAddDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, date: String, tag: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("عاجل") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة خبر أو إعلان رسمي", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الإعلان") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("نص الإعلان أو التفاصيل") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("التصنيف (مثال: عمرة / نقل / تأشيرات)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale("ar")).format(Date())
                        onConfirm(title.trim(), content.trim(), currentDate, tag.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ShajeenDarkBlue)
            ) {
                Text("نشر")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
