package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.TravelRepository
import com.example.data.entity.AgencyNewsEntity
import com.example.data.entity.AgencySettingsEntity
import com.example.data.entity.BookingEntity
import com.example.data.entity.ClientEntity
import com.example.data.entity.TravelServiceEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    LOGIN,
    DASHBOARD,
    MY_BOOKINGS,
    PROFILE,
    ADMIN
}

class ShajeenViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TravelRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TravelRepository(db)
        viewModelScope.launch {
            repository.ensureDefaultData()
        }
    }

    // Current Screen & Auth State
    private val _currentScreen = MutableStateFlow(AppScreen.LOGIN)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentClient = MutableStateFlow<ClientEntity?>(null)
    val currentClient: StateFlow<ClientEntity?> = _currentClient.asStateFlow()

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    // Agency Settings
    val agencySettings: StateFlow<AgencySettingsEntity?> = repository.agencySettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AgencySettingsEntity()
        )

    // Agency News
    val agencyNews: StateFlow<List<AgencyNewsEntity>> = repository.allNews
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Services & Filtering
    val allServices: StateFlow<List<TravelServiceEntity>> = repository.allServices
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered Services for UI
    val filteredServices: StateFlow<List<TravelServiceEntity>> = combine(
        allServices,
        _selectedCategory,
        _searchQuery
    ) { services, category, query ->
        services.filter { service ->
            val matchesCategory = (category == "الكل" || service.category == category)
            val matchesQuery = query.isBlank() ||
                service.title.contains(query, ignoreCase = true) ||
                service.description.contains(query, ignoreCase = true) ||
                service.subtitle.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Client Bookings
    val currentClientBookings: StateFlow<List<BookingEntity>> = _currentClient.flatMapLatest { client ->
        if (client != null) {
            repository.getBookingsByClient(client.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Admin Data
    val allClients: StateFlow<List<ClientEntity>> = repository.allClients
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allBookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Navigation methods
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Client Auth / Registration
    fun registerOrLoginClient(
        fullName: String,
        phone: String,
        idType: String,
        idNumber: String,
        country: String,
        city: String,
        district: String,
        area: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val existing = repository.getClientByPhone(phone)
            val client = if (existing != null) {
                existing.copy(
                    fullName = fullName,
                    idType = idType,
                    idNumber = idNumber,
                    country = country,
                    city = city,
                    district = district,
                    area = area
                )
            } else {
                ClientEntity(
                    fullName = fullName,
                    phone = phone,
                    idType = idType,
                    idNumber = idNumber,
                    country = country,
                    city = city,
                    district = district,
                    area = area
                )
            }
            val id = repository.insertOrUpdateClient(client)
            val finalClient = if (existing != null) client else client.copy(id = id)
            _currentClient.value = finalClient
            _currentScreen.value = AppScreen.DASHBOARD
            onSuccess()
        }
    }

    fun quickLoginByPhone(phone: String, onFound: (Boolean) -> Unit) {
        viewModelScope.launch {
            val client = repository.getClientByPhone(phone)
            if (client != null) {
                _currentClient.value = client
                _currentScreen.value = AppScreen.DASHBOARD
                onFound(true)
            } else {
                onFound(false)
            }
        }
    }

    fun logoutClient() {
        _currentClient.value = null
        _currentScreen.value = AppScreen.LOGIN
    }

    // Admin Auth
    fun loginAdmin(pin: String, onResult: (Boolean) -> Unit) {
        // Admin PIN defaults to 7777 or 1234
        if (pin == "7777" || pin == "1234" || pin == "admin") {
            _isAdminLoggedIn.value = true
            _currentScreen.value = AppScreen.ADMIN
            onResult(true)
        } else {
            onResult(false)
        }
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
        if (_currentClient.value != null) {
            _currentScreen.value = AppScreen.DASHBOARD
        } else {
            _currentScreen.value = AppScreen.LOGIN
        }
    }

    // Booking actions
    fun createBooking(
        service: TravelServiceEntity,
        travelDate: String,
        passengersCount: Int,
        notes: String,
        onComplete: (Boolean) -> Unit
    ) {
        val client = _currentClient.value
        if (client == null) {
            onComplete(false)
            return
        }
        viewModelScope.launch {
            val booking = BookingEntity(
                clientId = client.id,
                clientName = client.fullName,
                clientPhone = client.phone,
                serviceId = service.id,
                serviceTitle = service.title,
                serviceCategory = service.category,
                travelDate = travelDate,
                passengersCount = passengersCount,
                notes = notes,
                status = "قيد المراجعة"
            )
            repository.insertBooking(booking)
            onComplete(true)
        }
    }

    fun updateBookingStatus(bookingId: Long, status: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, status)
        }
    }

    fun cancelBooking(booking: BookingEntity) {
        viewModelScope.launch {
            repository.updateBookingStatus(booking.id, "ملغي")
        }
    }

    // Service Management (Admin)
    fun saveService(service: TravelServiceEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            if (service.id == 0L) {
                repository.insertService(service)
            } else {
                repository.updateService(service)
            }
            onDone()
        }
    }

    fun deleteService(service: TravelServiceEntity) {
        viewModelScope.launch {
            repository.deleteService(service)
        }
    }

    // Agency Settings (Admin)
    fun updateAgencySettings(
        address: String,
        phone1: String,
        phone2: String,
        phone3: String,
        phone4: String,
        announcement: String,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val current = repository.agencySettings
            val settings = AgencySettingsEntity(
                id = 1,
                address = address,
                phone1 = phone1,
                phone2 = phone2,
                phone3 = phone3,
                phone4 = phone4,
                announcement = announcement
            )
            repository.saveAgencySettings(settings)
            onDone()
        }
    }

    // News Management (Admin)
    fun addNews(title: String, content: String, dateText: String, tag: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.insertNews(
                AgencyNewsEntity(
                    title = title,
                    content = content,
                    dateText = dateText,
                    tag = tag
                )
            )
            onDone()
        }
    }

    fun deleteNews(news: AgencyNewsEntity) {
        viewModelScope.launch {
            repository.deleteNews(news)
        }
    }

    // Client Management (Admin)
    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.deleteClient(client)
        }
    }
}
