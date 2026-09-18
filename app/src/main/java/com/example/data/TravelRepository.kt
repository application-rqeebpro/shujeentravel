package com.example.data

import com.example.data.entity.AgencyNewsEntity
import com.example.data.entity.AgencySettingsEntity
import com.example.data.entity.BookingEntity
import com.example.data.entity.ClientEntity
import com.example.data.entity.TravelServiceEntity
import kotlinx.coroutines.flow.Flow

class TravelRepository(private val db: AppDatabase) {

    // Clients
    val allClients: Flow<List<ClientEntity>> = db.clientDao().getAllClients()
    suspend fun getClientById(id: Long) = db.clientDao().getClientById(id)
    suspend fun getClientByPhone(phone: String) = db.clientDao().getClientByPhone(phone)
    suspend fun insertOrUpdateClient(client: ClientEntity): Long = db.clientDao().insertClient(client)
    suspend fun deleteClient(client: ClientEntity) = db.clientDao().deleteClient(client)

    // Travel Services
    val allServices: Flow<List<TravelServiceEntity>> = db.travelServiceDao().getAllServices()
    val featuredServices: Flow<List<TravelServiceEntity>> = db.travelServiceDao().getFeaturedServices()
    fun getServicesByCategory(category: String): Flow<List<TravelServiceEntity>> =
        db.travelServiceDao().getServicesByCategory(category)

    suspend fun insertService(service: TravelServiceEntity) = db.travelServiceDao().insertService(service)
    suspend fun updateService(service: TravelServiceEntity) = db.travelServiceDao().updateService(service)
    suspend fun deleteService(service: TravelServiceEntity) = db.travelServiceDao().deleteService(service)

    // Bookings
    val allBookings: Flow<List<BookingEntity>> = db.bookingDao().getAllBookings()
    fun getBookingsByClient(clientId: Long): Flow<List<BookingEntity>> = db.bookingDao().getBookingsByClient(clientId)
    fun getBookingsByPhone(phone: String): Flow<List<BookingEntity>> = db.bookingDao().getBookingsByPhone(phone)
    suspend fun insertBooking(booking: BookingEntity) = db.bookingDao().insertBooking(booking)
    suspend fun updateBookingStatus(id: Long, status: String) = db.bookingDao().updateStatus(id, status)
    suspend fun deleteBooking(booking: BookingEntity) = db.bookingDao().deleteBooking(booking)

    // Agency Settings & News
    val agencySettings: Flow<AgencySettingsEntity?> = db.agencyDao().getAgencySettings()
    suspend fun saveAgencySettings(settings: AgencySettingsEntity) = db.agencyDao().saveAgencySettings(settings)

    val allNews: Flow<List<AgencyNewsEntity>> = db.agencyDao().getAllNews()
    suspend fun insertNews(news: AgencyNewsEntity) = db.agencyDao().insertNews(news)
    suspend fun deleteNews(news: AgencyNewsEntity) = db.agencyDao().deleteNews(news)

    // Pre-populate default services and agency settings
    suspend fun ensureDefaultData() {
        // 1. Settings
        val currentSettings = db.agencyDao().getAgencySettingsOnce()
        if (currentSettings == null) {
            db.agencyDao().saveAgencySettings(
                AgencySettingsEntity(
                    id = 1,
                    agencyName = "وكالة شجين للسفريات والسياحة",
                    address = "صنعاء - شارع خولان - جوار السلامي لمواد البناء",
                    phone1 = "+967 777779492",
                    phone2 = "+966 551160835",
                    phone3 = "+967 774191789",
                    phone4 = "+967 770038009",
                    announcement = "مرحباً بكم في وكالة شجين! خصومات حصرية لرحلات العمرة وتذاكر الطيران لجميع الوجهات العالمية."
                )
            )
        }

        // 2. Services
        if (db.travelServiceDao().getCount() == 0) {
            val defaultServices = listOf(
                // Category 1: خدمات السفر والسياحة
                TravelServiceEntity(
                    title = "حجز تذاكر طيران دولية ومحلية",
                    category = "خدمات السفر والسياحة",
                    subtitle = "أفضل الأسعار على جميع خطوط الطيران",
                    description = "نوفر حجوزات مؤكدة على طيران اليمنية، الخطوط السعودية، مصر للطيران، طيران الإمارات وفلاي دبي مع إمكانية تعديل وتأكيد المواعيد فوراً.",
                    price = "حسب الوجهة",
                    iconType = "flight",
                    badge = "الأكثر طلباً",
                    sortOrder = 1
                ),
                TravelServiceEntity(
                    title = "إصدار تأشيرات الدخول والزيارة",
                    category = "خدمات السفر والسياحة",
                    subtitle = "تأشيرات سياحية، تجارية، وعائلية",
                    description = "تخليص ومعاملة تأشيرات السعودية (زيارة شخصية، عائلية، تجارية، سياحية) ومصر، الإمارات، سلطنة عمان، الأردن، وغيرها بدقة وسرعة قياسية.",
                    price = "تبدأ من 150$",
                    iconType = "visa",
                    badge = "شامل الإجراءات",
                    sortOrder = 2
                ),
                TravelServiceEntity(
                    title = "باقات سياحية متكاملة وحجوزات فنادق",
                    category = "خدمات السفر والسياحة",
                    subtitle = "فنادق مختارة 4 و 5 نجوم بأسعار مخفضة",
                    description = "حجوزات فندقية عالمية ومحلية تشمل الإفطار والاستقبال من وإلى المطار مع توفير برامج سياحية متكاملة للأفراد والعائلات.",
                    price = "خصم يصل إلى 25%",
                    iconType = "hotel",
                    badge = "عروض خاصة",
                    sortOrder = 3
                ),

                // Category 2: الحج والعمرة
                TravelServiceEntity(
                    title = "برنامج عمرة VIP براً وجواً",
                    category = "الحج والعمرة",
                    subtitle = "فنادق 5 نجوم قريبة من الحرمين الشريفين",
                    description = "يشمل التأشيرة، تذاكر الطيران أو حافلات VIP الفاخرة، الإقامة في مكة المكرمة والمدينة المنورة، زيارات المزارات الدينية مع مرشدين متخصصين.",
                    price = "تبدأ من 450$",
                    iconType = "kaaba",
                    badge = "VIP متميز",
                    sortOrder = 4
                ),
                TravelServiceEntity(
                    title = "برنامج العمرة الاقتصادي الميسر",
                    category = "الحج والعمرة",
                    subtitle = "رحلات برية أسبوعية منتظمة ومريحة",
                    description = "برامج عمرة ميسرة تناسب جميع المعتمرين مع توفير سكن مريح ووسائل نقل حديثة مكيفة وخدمات إشراف وتفويج متكاملة طوال الرحلة.",
                    price = "280$ فقط",
                    iconType = "kaaba",
                    badge = "اقتصادي",
                    sortOrder = 5
                ),
                TravelServiceEntity(
                    title = "خدمات تفويج وإرشاد المعتمرين",
                    category = "الحج والعمرة",
                    subtitle = "إشراف ومتابعة مدار الساعة",
                    description = "كوادر متخصصة في استقبال وتوديع المعتمرين في المنافذ والمطارات، وإنهاء كافة الإجراءات الرسمية بسهولة ويسر.",
                    price = "شامل مع الباقات",
                    iconType = "kaaba",
                    badge = "خدمة 24/7",
                    sortOrder = 6
                ),

                // Category 3: حجوزات النقل
                TravelServiceEntity(
                    title = "رحلات بولمان دولية VIP (اليمن - السعودية)",
                    category = "حجوزات النقل",
                    subtitle = "باصات حديثة مرسيدس مزودة بإنترنت وشاشات",
                    description = "رحلات يومية منتظمة تنطلق من صنعاء، عدن، تعز، إب، وحضرموت إلى كافة مدن المملكة: الرياض، جدة، مكة، المدينة، الدمام، جازان وشرورة.",
                    price = "أسعار تنافسية",
                    iconType = "bus",
                    badge = "رحلات يومية",
                    sortOrder = 7
                ),
                TravelServiceEntity(
                    title = "نقل سياحي وتأجير سيارات خاصة (VIP Taxi)",
                    category = "حجوزات النقل",
                    subtitle = "سيارات صالون ولكزس وإتش ون حديثة",
                    description = "خدمات التوصيل الداخلي والدولي الخاص بين المحافظات والمطارات والمنافذ البرية بأقصى درجات الراحة والأمان وسائقين ذوي خبرة.",
                    price = "حسب المشوار",
                    iconType = "taxi",
                    badge = "سريع ومريح",
                    sortOrder = 8
                ),
                TravelServiceEntity(
                    title = "شحن الأمانات والطرود والوثائق الرسمية",
                    category = "حجوزات النقل",
                    subtitle = "خدمة نقل موثوقة ومؤمنة بين اليمن ودول الخليج",
                    description = "نقل الطرود السريعة والمستندات والوثائق المهمة بكل أمان وسرعة مع التتبع المستمر حتى التسليم لليد.",
                    price = "حسب الوزن",
                    iconType = "bus",
                    badge = "ضمان وأمان",
                    sortOrder = 9
                ),

                // Category 4: البرامج السياحية
                TravelServiceEntity(
                    title = "البرنامج السياحي التراثي اليمني",
                    category = "البرامج السياحية",
                    subtitle = "استكشف سحر صنعاء القديمة، شبام كوكبان، ودار الحجر",
                    description = "رحلات سياحية وثقافية تشمل المواصلات، وجبات تقليدية، زيارة المعالم التاريخية، ودليل سياحي محترف يشرح تاريخ الحضارة العريقة.",
                    price = "برنامج 3 أيام",
                    iconType = "mountain",
                    badge = "تراث أصيل",
                    sortOrder = 10
                ),
                TravelServiceEntity(
                    title = "موسم صلالة السياحي (سلطنة عمان)",
                    category = "البرامج السياحية",
                    subtitle = "طبيعة خلابة، شلالات وضباب في خريف صلالة",
                    description = "برامج متكاملة للأفراد والعائلات تشمل النقل المريح، حجوزات الفنادق والمنتجعات، وجولات يومية إلى عين رزات، وادي دربات، والمغسيل.",
                    price = "عروض الموسم",
                    iconType = "tour",
                    badge = "طبيعة ساحرة",
                    sortOrder = 11
                ),
                TravelServiceEntity(
                    title = "رحلة المغامرة والاستكشاف في جزيرة سقطرى",
                    category = "البرامج السياحية",
                    subtitle = "أعجوبة الطبيعة وشجرة دم الأخوين الفريدة",
                    description = "باقة استثنائية لزيارة محمية ديطوح، وادي عيره، هضبة دكسم، ومواقع الغوص والشواطئ النقية مع التخييم الفاخر والدليل المحلي.",
                    price = "برنامج 5 أيام",
                    iconType = "tour",
                    badge = "وجهة عالمية",
                    sortOrder = 12
                )
            )
            db.travelServiceDao().insertAll(defaultServices)
        }

        // 3. News
        if (db.agencyDao().getNewsCount() == 0) {
            val defaultNews = listOf(
                AgencyNewsEntity(
                    title = "بدء التسجيل لرحلات العمرة لشهر رجب وشعبان",
                    content = "تعلن وكالة شجين للسفريات والسياحة عن فتح باب التسجيل لرحلات العمرة المباركة بأسعار خاصة وخدمات فندقية راقية.",
                    dateText = "مستمر حالياً",
                    tag = "موسم العمرة"
                ),
                AgencyNewsEntity(
                    title = "تدشين خطوط نقل حديثة VIP إلى مكة والرياض",
                    content = "تم تعزيز أسطول النقل بباصات VIP جديدة موديل العام مزودة بإنترنت فضائي ومقاعد تدليك لراحة المسافرين.",
                    dateText = "تحديث جديد",
                    tag = "خدمات النقل"
                )
            )
            db.agencyDao().insertAllNews(defaultNews)
        }
    }
}
