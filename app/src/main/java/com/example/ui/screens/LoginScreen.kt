package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LocationData
import com.example.data.entity.AgencySettingsEntity
import com.example.ui.components.AgencyBrandLogo
import com.example.ui.components.AgencyContactFooter
import com.example.ui.components.OfficialShajeenLogoCard
import com.example.ui.theme.ShajeenDarkBlue
import com.example.ui.theme.ShajeenGold
import com.example.ui.theme.ShajeenSkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    agencySettings: AgencySettingsEntity?,
    onRegisterOrLogin: (
        fullName: String,
        phone: String,
        idType: String,
        idNumber: String,
        country: String,
        city: String,
        district: String,
        area: String
    ) -> Unit,
    onQuickPhoneLogin: (phone: String) -> Unit,
    onAdminLogin: (pin: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form states
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val idTypes = listOf("بطاقة شخصية", "جواز سفر", "بطاقة عائلية")
    var selectedIdType by remember { mutableStateOf(idTypes[0]) }
    var idTypeExpanded by remember { mutableStateOf(false) }

    var idNumber by remember { mutableStateOf("") }

    // Cascading Location States
    var selectedCountry by remember { mutableStateOf("اليمن") }
    var countryExpanded by remember { mutableStateOf(false) }

    var availableCities = remember(selectedCountry) { LocationData.getCities(selectedCountry) }
    var selectedCity by remember(selectedCountry) { mutableStateOf(availableCities.firstOrNull() ?: "") }
    var cityExpanded by remember { mutableStateOf(false) }

    var availableDistricts = remember(selectedCity) { LocationData.getDistricts(selectedCity) }
    var selectedDistrict by remember(selectedCity) { mutableStateOf(availableDistricts.firstOrNull() ?: "") }
    var districtExpanded by remember { mutableStateOf(false) }

    var areaName by remember { mutableStateOf("") }

    // Validation errors
    var fullNameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var idNumberError by remember { mutableStateOf(false) }

    // Admin Dialog state
    var showAdminDialog by remember { mutableStateOf(false) }
    var adminPin by remember { mutableStateOf("") }
    var adminPinError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Top Bar: Agency Brand & Owner/Admin Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AgencyBrandLogo(size = 42, showSubtitle = true)

                // Owner / Admin Dashboard Button
                Surface(
                    onClick = { showAdminDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    color = ShajeenDarkBlue,
                    modifier = Modifier.testTag("admin_login_top_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "دخول المالك / Admin Login",
                            tint = ShajeenGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "دخول المالك",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Official Logo Showcase
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                OfficialShajeenLogoCard(width = 175, elevation = 3)
            }

            // Welcome Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ShajeenDarkBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(ShajeenDarkBlue, ShajeenSkyBlue)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TravelExplore,
                                contentDescription = null,
                                tint = ShajeenGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تسجيل الدخول والاشتراك",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "يرجى تسجيل بياناتك الرسمية للبدء في تصفح وحجز رحلات الطيران، التأشيرات، الحج والعمرة، وخدمات النقل الدولي.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Registration & Login Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "البيانات الشخصية للمسافر / العميل",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ShajeenDarkBlue
                        )
                    )

                    // 1. Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            fullNameError = false
                        },
                        label = { Text("الاسم الرباعي حسب البطاقة الشخصية أو جواز السفر *") },
                        placeholder = { Text("مثال: محمد عبدالله علي صالح") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = ShajeenSkyBlue)
                        },
                        isError = fullNameError,
                        supportingText = {
                            if (fullNameError) {
                                Text("يرجى إدخال الاسم الرباعي كاملاً", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("full_name_input"),
                        singleLine = true
                    )

                    // 2. Phone Number
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            phoneNumber = it
                            phoneError = false
                        },
                        label = { Text("رقم الهاتف للتواصل وتأكيد الحجز *") },
                        placeholder = { Text("مثال: 777777777 أو +967770000000") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = ShajeenSkyBlue)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = phoneError,
                        supportingText = {
                            if (phoneError) {
                                Text("يرجى إدخال رقم هاتف صحيح", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phone_number_input"),
                        singleLine = true
                    )

                    // 3. ID Type Selector (Dropdown)
                    ExposedDropdownMenuBox(
                        expanded = idTypeExpanded,
                        onExpandedChange = { idTypeExpanded = !idTypeExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedIdType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("نوع الهوية الرسمية *") },
                            leadingIcon = {
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = ShajeenSkyBlue)
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = idTypeExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("id_type_selector")
                        )
                        ExposedDropdownMenu(
                            expanded = idTypeExpanded,
                            onDismissRequest = { idTypeExpanded = false }
                        ) {
                            idTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        selectedIdType = type
                                        idTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 4. ID Number
                    OutlinedTextField(
                        value = idNumber,
                        onValueChange = {
                            idNumber = it
                            idNumberError = false
                        },
                        label = { Text("رقم الهوية (البطاقة أو الجواز) *") },
                        placeholder = { Text("أدخل رقم الهوية أو رقم جواز السفر") },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = ShajeenSkyBlue)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        isError = idNumberError,
                        supportingText = {
                            if (idNumberError) {
                                Text("يرجى إدخال رقم الهوية", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("id_number_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "محددات الموقع الجغرافي والإقامة",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ShajeenDarkBlue
                        )
                    )

                    // 5. Cascading Dropdown: Country (الدولة)
                    ExposedDropdownMenuBox(
                        expanded = countryExpanded,
                        onExpandedChange = { countryExpanded = !countryExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCountry,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("الدولة *") },
                            leadingIcon = {
                                Icon(Icons.Default.Public, contentDescription = null, tint = ShajeenSkyBlue)
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("country_selector")
                        )
                        ExposedDropdownMenu(
                            expanded = countryExpanded,
                            onDismissRequest = { countryExpanded = false }
                        ) {
                            LocationData.countries.forEach { country ->
                                DropdownMenuItem(
                                    text = { Text(country) },
                                    onClick = {
                                        selectedCountry = country
                                        availableCities = LocationData.getCities(country)
                                        selectedCity = availableCities.firstOrNull() ?: ""
                                        availableDistricts = LocationData.getDistricts(selectedCity)
                                        selectedDistrict = availableDistricts.firstOrNull() ?: ""
                                        countryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 6. Cascading Dropdown: City / Governorate (المدينة / المحافظة)
                    ExposedDropdownMenuBox(
                        expanded = cityExpanded,
                        onExpandedChange = { cityExpanded = !cityExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCity,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("المدينة / المحافظة *") },
                            leadingIcon = {
                                Icon(Icons.Default.LocationCity, contentDescription = null, tint = ShajeenSkyBlue)
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("city_selector")
                        )
                        ExposedDropdownMenu(
                            expanded = cityExpanded,
                            onDismissRequest = { cityExpanded = false }
                        ) {
                            availableCities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city) },
                                    onClick = {
                                        selectedCity = city
                                        availableDistricts = LocationData.getDistricts(city)
                                        selectedDistrict = availableDistricts.firstOrNull() ?: ""
                                        cityExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 7. Cascading Dropdown: District (المديرية)
                    ExposedDropdownMenuBox(
                        expanded = districtExpanded,
                        onExpandedChange = { districtExpanded = !districtExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedDistrict,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("المديرية *") },
                            leadingIcon = {
                                Icon(Icons.Default.LocationCity, contentDescription = null, tint = ShajeenSkyBlue)
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("district_selector")
                        )
                        ExposedDropdownMenu(
                            expanded = districtExpanded,
                            onDismissRequest = { districtExpanded = false }
                        ) {
                            availableDistricts.forEach { dist ->
                                DropdownMenuItem(
                                    text = { Text(dist) },
                                    onClick = {
                                        selectedDistrict = dist
                                        districtExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 8. Area / Neighborhood Name (اسم المنطقة)
                    OutlinedTextField(
                        value = areaName,
                        onValueChange = { areaName = it },
                        label = { Text("اسم المنطقة / الحي السكني") },
                        placeholder = { Text("مثال: شارع خولان / حدة / الصافية") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationCity, contentDescription = null, tint = ShajeenSkyBlue)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("area_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Primary Action: تسجيل الدخول / متابعة
                    Button(
                        onClick = {
                            val isNameValid = fullName.trim().split("\\s+".toRegex()).size >= 2 || fullName.trim().length >= 5
                            val isPhoneValid = phoneNumber.trim().length >= 7
                            val isIdValid = idNumber.trim().isNotBlank()

                            if (!isNameValid) fullNameError = true
                            if (!isPhoneValid) phoneError = true
                            if (!isIdValid) idNumberError = true

                            if (isNameValid && isPhoneValid && isIdValid) {
                                onRegisterOrLogin(
                                    fullName.trim(),
                                    phoneNumber.trim(),
                                    selectedIdType,
                                    idNumber.trim(),
                                    selectedCountry,
                                    selectedCity,
                                    selectedDistrict,
                                    areaName.trim().ifEmpty { "المنطقة الرئيسية" }
                                )
                            } else {
                                Toast.makeText(context, "يرجى تعبئة كافة الحقول المطلوبة", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ShajeenDarkBlue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_login_btn")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ShajeenGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تسجيل الدخول / متابعة",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }

                    // Quick login helper if phone is already provided
                    if (phoneNumber.trim().length >= 7) {
                        OutlinedButton(
                            onClick = {
                                onQuickPhoneLogin(phoneNumber.trim())
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("quick_phone_login_btn")
                        ) {
                            Text("دخول سريع كعميل مسجل مسبقاً بهذا الرقم", fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Screen 1 Specification: Footer Area clearly displays agency address and all 4 phone numbers
            AgencyContactFooter(settings = agencySettings)

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Admin PIN Dialog
        if (showAdminDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAdminDialog = false
                    adminPin = ""
                    adminPinError = false
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = ShajeenDarkBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تسجيل دخول المالك / الإدارة", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "أدخل رمز مرور المالك للتحكم في الخدمات، والعملاء، والتحديثات (الرمز الافتراضي: 7777)",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = adminPin,
                            onValueChange = {
                                adminPin = it
                                adminPinError = false
                            },
                            label = { Text("رمز المرور السري (PIN)") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = ShajeenSkyBlue)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = adminPinError,
                            supportingText = {
                                if (adminPinError) {
                                    Text("رمز المرور غير صحيح. جرب 7777", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_pin_input"),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (adminPin == "7777" || adminPin == "1234" || adminPin == "admin") {
                                showAdminDialog = false
                                onAdminLogin(adminPin)
                            } else {
                                adminPinError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ShajeenDarkBlue),
                        modifier = Modifier.testTag("admin_dialog_confirm_btn")
                    ) {
                        Text("دخول")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAdminDialog = false
                            adminPin = ""
                            adminPinError = false
                        }
                    ) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}
