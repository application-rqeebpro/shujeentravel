package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MyBookingsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.ShajeenDarkBlue
import com.example.ui.theme.ShajeenGold
import com.example.ui.theme.ShajeenSkyBlue
import kotlinx.coroutines.launch

@Composable
fun ShajeenApp(
    viewModel: ShajeenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentClient by viewModel.currentClient.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()

    val agencySettings by viewModel.agencySettings.collectAsStateWithLifecycle()
    val agencyNews by viewModel.agencyNews.collectAsStateWithLifecycle()
    val filteredServices by viewModel.filteredServices.collectAsStateWithLifecycle()
    val allServices by viewModel.allServices.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val clientBookings by viewModel.currentClientBookings.collectAsStateWithLifecycle()
    val allClients by viewModel.allClients.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()

    // Mandatory RTL (Right-to-Left) Arabic layout direction support
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                // Show bottom navigation bar when user or admin is logged in (not on login screen)
                if (currentScreen != AppScreen.LOGIN) {
                    NavigationBar(
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("main_bottom_nav_bar"),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp
                    ) {
                        // 1. Home
                        NavigationBarItem(
                            selected = (currentScreen == AppScreen.DASHBOARD),
                            onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == AppScreen.DASHBOARD) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "الرئيسية",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "الرئيسية",
                                    fontSize = 11.sp,
                                    fontWeight = if (currentScreen == AppScreen.DASHBOARD) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ShajeenDarkBlue,
                                selectedTextColor = ShajeenDarkBlue,
                                indicatorColor = ShajeenGold.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag("nav_item_home")
                        )

                        // 2. My Bookings
                        NavigationBarItem(
                            selected = (currentScreen == AppScreen.MY_BOOKINGS),
                            onClick = { viewModel.navigateTo(AppScreen.MY_BOOKINGS) },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == AppScreen.MY_BOOKINGS) Icons.Filled.Bookmarks else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "حجوزاتي",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "حجوزاتي",
                                    fontSize = 11.sp,
                                    fontWeight = if (currentScreen == AppScreen.MY_BOOKINGS) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ShajeenDarkBlue,
                                selectedTextColor = ShajeenDarkBlue,
                                indicatorColor = ShajeenGold.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag("nav_item_bookings")
                        )

                        // 3. Profile
                        NavigationBarItem(
                            selected = (currentScreen == AppScreen.PROFILE),
                            onClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == AppScreen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "الملف الشخصي",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "الملف الشخصي",
                                    fontSize = 11.sp,
                                    fontWeight = if (currentScreen == AppScreen.PROFILE) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ShajeenDarkBlue,
                                selectedTextColor = ShajeenDarkBlue,
                                indicatorColor = ShajeenGold.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag("nav_item_profile")
                        )

                        // 4. Admin Panel / Settings
                        NavigationBarItem(
                            selected = (currentScreen == AppScreen.ADMIN),
                            onClick = {
                                if (isAdminLoggedIn) {
                                    viewModel.navigateTo(AppScreen.ADMIN)
                                } else {
                                    // Prompt admin login
                                    viewModel.loginAdmin("7777") { success ->
                                        if (success) {
                                            viewModel.navigateTo(AppScreen.ADMIN)
                                        }
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentScreen == AppScreen.ADMIN) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                                    contentDescription = "لوحة الإدارة",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "لوحة الإدارة",
                                    fontSize = 11.sp,
                                    fontWeight = if (currentScreen == AppScreen.ADMIN) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ShajeenDarkBlue,
                                selectedTextColor = ShajeenDarkBlue,
                                indicatorColor = ShajeenGold.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag("nav_item_admin")
                        )
                    }
                }
            }
        ) { innerPadding ->
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    AppScreen.LOGIN -> {
                        LoginScreen(
                            agencySettings = agencySettings,
                            onRegisterOrLogin = { name, phone, idType, idNum, country, city, dist, area ->
                                viewModel.registerOrLoginClient(
                                    name, phone, idType, idNum, country, city, dist, area
                                ) {
                                    Toast.makeText(context, "أهلاً بك يا $name في وكالة شجين", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onQuickPhoneLogin = { phone ->
                                viewModel.quickLoginByPhone(phone) { found ->
                                    if (found) {
                                        Toast.makeText(context, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "الرقم غير مسجل، يرجى إكمال بقية الحقول للمتابعة", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            onAdminLogin = { pin ->
                                viewModel.loginAdmin(pin) { success ->
                                    if (success) {
                                        Toast.makeText(context, "تم الدخول إلى لوحة تحكم المالك", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "رمز المرور غير صحيح", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }

                    AppScreen.DASHBOARD -> {
                        DashboardScreen(
                            currentClient = currentClient,
                            agencySettings = agencySettings,
                            newsList = agencyNews,
                            services = filteredServices,
                            selectedCategory = selectedCategory,
                            searchQuery = searchQuery,
                            onCategorySelected = { viewModel.setCategory(it) },
                            onSearchChanged = { viewModel.setSearchQuery(it) },
                            onBookService = { service, date, passengers, notes ->
                                viewModel.createBooking(service, date, passengers, notes) { success ->
                                    if (success) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("تم إرسال طلب حجز (${service.title}) بنجاح! سيتم التواصل معك قريباً.")
                                        }
                                    }
                                }
                            },
                            onLogout = { viewModel.logoutClient() }
                        )
                    }

                    AppScreen.MY_BOOKINGS -> {
                        MyBookingsScreen(
                            bookings = clientBookings,
                            onCancelBooking = { booking ->
                                viewModel.cancelBooking(booking)
                                Toast.makeText(context, "تم إلغاء طلب الحجز", Toast.LENGTH_SHORT).show()
                            },
                            onBrowseServices = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }

                    AppScreen.PROFILE -> {
                        ProfileScreen(
                            currentClient = currentClient,
                            agencySettings = agencySettings,
                            onLogout = { viewModel.logoutClient() }
                        )
                    }

                    AppScreen.ADMIN -> {
                        AdminScreen(
                            services = allServices,
                            clients = allClients,
                            bookings = allBookings,
                            newsList = agencyNews,
                            agencySettings = agencySettings,
                            onSaveService = { service -> viewModel.saveService(service) {} },
                            onDeleteService = { service -> viewModel.deleteService(service) },
                            onDeleteClient = { client -> viewModel.deleteClient(client) },
                            onUpdateBookingStatus = { id, status -> viewModel.updateBookingStatus(id, status) },
                            onUpdateAgencySettings = { addr, p1, p2, p3, p4, ann ->
                                viewModel.updateAgencySettings(addr, p1, p2, p3, p4, ann) {}
                            },
                            onAddNews = { title, content, date, tag ->
                                viewModel.addNews(title, content, date, tag) {}
                            },
                            onDeleteNews = { news -> viewModel.deleteNews(news) },
                            onLogoutAdmin = { viewModel.logoutAdmin() }
                        )
                    }
                }
            }
        }
    }
}
