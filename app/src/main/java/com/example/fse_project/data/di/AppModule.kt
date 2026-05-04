package com.example.fse_project.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fse_project.App.Companion.BASE_URL
import com.example.fse_project.data.local.database.AppDao
import com.example.fse_project.data.local.database.AppDatabase
import com.example.fse_project.data.local.database.entities.ChargerEntity
import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.entities.ChargerType
import com.example.fse_project.data.local.database.entities.ConnectorType
import com.example.fse_project.data.local.database.entities.PowerOutput
import com.example.fse_project.data.local.database.entities.StationEntity
import com.example.fse_project.data.local.database.entities.UserEntity
import com.example.fse_project.data.local.database.entities.VehicleEntity
import com.example.fse_project.data.local.database.entities.WalletEntity
import com.example.fse_project.data.remote.service.DirectionsApi
import com.example.fse_project.data.repository.DirectionsRepositoryImpl
import com.example.fse_project.data.repository.ReportRepositoryImpl
import com.example.fse_project.data.repository.ReservationRepositoryImpl
import com.example.fse_project.data.repository.StationRepositoryImpl
import com.example.fse_project.data.repository.UserRepositoryImpl
import com.example.fse_project.domain.repository.DirectionsRepository
import com.example.fse_project.domain.repository.ReportRepository
import com.example.fse_project.domain.repository.ReservationRepository
import com.example.fse_project.domain.repository.StationRepository
import com.example.fse_project.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.math.roundToInt
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDirectionsApi(): DirectionsApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDirectionsRepository(api: DirectionsApi): DirectionsRepository {
        return DirectionsRepositoryImpl(api)
    }

    data class PrepopulatedStation(
        val name: String,
        val address: String,
        val lat: Double,
        val lng: Double
    )

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        dbProvider: Provider<AppDatabase>
    ): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "app_database"
        )
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = dbProvider.get().appDao()

                        val defaultUser1 = UserEntity(
                            id = 0,
                            name = "Ali Onur Eskiocak",
                            email = "a",
                            password = "a"
                        )
                        val newUserId1 = dao.insertUser(defaultUser1)
                        val defaultWallet = WalletEntity(userId = newUserId1, balance = 1500.0)
                        dao.insertWallet(defaultWallet)
                        val defaultVehicle1 = VehicleEntity(
                            id = 0,
                            ownerId = newUserId1,
                            brand = "Tesla",
                            model = "Model Y",
                            capacity = 75.0,
                            connectorType = ConnectorType.CCS,
                            licensePlate = "35 ALI 35",
                            currentKwh = (10..30).random().toDouble(),
                        )
                        dao.insertVehicle(defaultVehicle1)

                        val defaultUser2 = UserEntity(
                            id = 0,
                            name = "Kaan Çığrı",
                            email = "kaan",
                            password = "kaan"
                        )
                        val newUserId2 = dao.insertUser(defaultUser2)
                        val defaultWallet2 = WalletEntity(userId = newUserId2, balance = 1500.0)
                        dao.insertWallet(defaultWallet2)
                        val defaultVehicle2 = VehicleEntity(
                            id = 0,
                            ownerId = newUserId2,
                            brand = "Tesla",
                            model = "Model X",
                            capacity = 50.0,
                            connectorType = ConnectorType.TYPE_2,
                            licensePlate = "35 CGR 35",
                            currentKwh = (10..30).random().toDouble(),

                            )
                        dao.insertVehicle(defaultVehicle2)

                        val defaultUser3 = UserEntity(
                            id = 0,
                            name = "Furkan Çokbilen",
                            email = "furkan",
                            password = "furkan"
                        )
                        val newUserId3 = dao.insertUser(defaultUser3)
                        val defaultWallet3 = WalletEntity(userId = newUserId3, balance = 1500.0)
                        dao.insertWallet(defaultWallet3)
                        val defaultVehicle3 = VehicleEntity(
                            id = 0,
                            ownerId = newUserId3,
                            brand = "TOGG",
                            model = "T10X",
                            capacity = 90.0,
                            connectorType = ConnectorType.CHADEMO,
                            licensePlate = "35 FRK 35",
                            currentKwh = (10..30).random().toDouble(),
                            )
                        dao.insertVehicle(defaultVehicle3)

                        val defaultUser4 = UserEntity(
                            id = 0,
                            name = "Özümcan Şahin",
                            email = "ozum",
                            password = "ozum"
                        )
                        val newUserId4 = dao.insertUser(defaultUser4)
                        val defaultWallet4 = WalletEntity(userId = newUserId4, balance = 1500.0)
                        dao.insertWallet(defaultWallet4)
                        val defaultVehicle4 = VehicleEntity(
                            id = 0,
                            ownerId = newUserId4,
                            brand = "TOGG",
                            model = "T10F",
                            capacity = 80.0,
                            connectorType = ConnectorType.TYPE_2,
                            licensePlate = "35 OZM 35",
                            currentKwh = (10..30).random().toDouble(),

                            )
                        dao.insertVehicle(defaultVehicle4)

                        val stationsData = listOf(
                            // Yeni Eklenenler (Balçova, Gaziemir, Karabağlar, Buca)
                            PrepopulatedStation(
                                "Miggo Şarj İstasyonu",
                                "İnciraltı, Güldeste Sk., 35330 Balçova/İzmir",
                                38.39825803347759,
                                27.02420520820319
                            ),
                            PrepopulatedStation(
                                "Miggo Şarj İstasyonu",
                                "Bahçelerarası, Mithatpaşa Cd., 35330 Balçova/İzmir",
                                38.39562519986769,
                                27.042201874051123
                            ),
                            PrepopulatedStation(
                                "Zes Şarj İstasyonu",
                                "Bahçelerarası, Çağdaş Cd., 35330 Balçova/İzmir",
                                38.396941628657565,
                                27.044961362814472
                            ),
                            PrepopulatedStation(
                                "Trugo Şarj İstasyonu",
                                "Korutürk, 35330 Balçova/İzmir",
                                38.390453283285936,
                                27.037642718702976
                            ),
                            PrepopulatedStation(
                                "Oncharge Şarj İstasyonu",
                                "Bahçelerarası, Ş. B. Ali Resmi Tufan Cd. No:3, 35330 Balçova/İzmir",
                                38.399599377873024,
                                27.067604477906222
                            ),
                            PrepopulatedStation(
                                "Solarşarj Şarj İstasyonu",
                                "Bahçelerarası, Şehit Binbaşı Ali Resmi Tufan Sk İstinye Park AVM No:3, 35330 Balçova/İzmir",
                                38.39962403037289,
                                27.06835943741399
                            ),
                            PrepopulatedStation(
                                "Beefull Şarj İstasyonu",
                                "Bahçelerarası, Deniz Feneri Sk, 35330 Balçova/İzmir",
                                38.40036360145972,
                                27.06876837381403
                            ),
                            PrepopulatedStation(
                                "ePower Şarj İstasyonu",
                                "Bahçelerarası, İzmir-Çeşme Otoyolu, 35330 Balçova/İzmir",
                                38.37775829524745,
                                27.074966788005927
                            ),
                            PrepopulatedStation(
                                "GIO EV Şarj İstasyonu",
                                "Çetin Emeç, Kıvanç Sk No:14, 35330 Balçova/İzmir",
                                38.38190878377171,
                                27.073272520936246
                            ),
                            PrepopulatedStation(
                                "Eşarj Şarj İstasyonu",
                                "Dokuz Eylül, Akçay Cd. No:219, 35410 Gaziemir/İzmir",
                                38.32307146657757,
                                27.137486790258652
                            ),
                            PrepopulatedStation(
                                "Eşarj Şarj İstasyonu",
                                "9 Eylül Mah, Akçay Cd, Dokuz Eylül, 698. Sk. No:2, 35410 Gaziemir",
                                38.30895315664814,
                                27.14286310783556
                            ),
                            PrepopulatedStation(
                                "ZES Şarj İstasyonu",
                                "Millenium Otomotiv, Sevgi Mah. 902 Sok. No:8/1 B, 35410 Gaziemir/İzmir",
                                38.301548553941686,
                                27.138364556393658
                            ),
                            PrepopulatedStation(
                                "Eşarj Şarj İstasyonu",
                                "Sevgi, Menderes Cd. No:16, 35410 Gaziemir/İzmir",
                                38.29767375129327,
                                27.139571484829293
                            ),
                            PrepopulatedStation(
                                "Zes Şarj İstasyonu",
                                "Emrez, Akçay Cd. No: 58, 35410 Gaziemir/İzmir",
                                38.35587369175034,
                                27.133072609574313
                            ),
                            PrepopulatedStation(
                                "5 Şarj İstasyonu",
                                "Emrez, Akçay Cd. No:34/1, 35410 Gaziemir/İzmir",
                                38.35976028954495,
                                27.1351511181024
                            ),
                            PrepopulatedStation(
                                "Aksa Şarj İstasyonu",
                                "Uzundere, 6100. Sk. 15/3, 35120 Karabağlar/İzmir",
                                38.365401753783075,
                                27.11884282042051
                            ),
                            PrepopulatedStation(
                                "Eşarj Şarj İstasyonu",
                                "Emrez, Akçay Cd. 16A, 35410 Gaziemir/İzmir",
                                38.36239302757055,
                                27.13674997081631
                            ),
                            PrepopulatedStation(
                                "Otowatt Şarj İstasyonu",
                                "Adatepe, Doğuş Cd. No:207, 35400 Buca/İzmir",
                                38.36954889103257,
                                27.20204814566959
                            ),
                            PrepopulatedStation(
                                "Zeplin Energy Şarj İstasyonu",
                                "Buca Koop., 1413. Sk., 35390 Buca/İzmir",
                                38.371053121040866,
                                27.182062486745714
                            ),
                            PrepopulatedStation(
                                "WAT Mobilite Şarj İstasyonu",
                                "Tarık Akan Gençlik Merkezi, Kuruçeşme, Doğuş Cd. No:76 D:92, 35390 Buca/İzmir",
                                38.37681904640948,
                                27.192135258843347
                            ),

                            // Önceki Liste (Kaldığı Yerden Devam Ediyor)
                            PrepopulatedStation(
                                "WAT Mobilite Şarj İstasyonu",
                                "Vali Rahmi Bey, 296. Sk. No:1, 35380 Buca/İzmir",
                                38.39085589986421,
                                27.16319602472157
                            ),
                            PrepopulatedStation(
                                "5 Şarj Şarj İstasyonu",
                                "Güneşli, Eşrefpaşa Cd. No:416, 35100 Konak/İzmir",
                                38.40366990011291,
                                27.12910576002981
                            ),
                            PrepopulatedStation(
                                "Trugo Şarj İstasyonu",
                                "Güneşli, Eşrefpaşa Cd. No:408, 35270 Konak/İzmir",
                                38.40480105657808,
                                27.128494498497098
                            ),
                            PrepopulatedStation(
                                "Zes Şarj İstasyonu",
                                "Swissotel İzmir DC, Alsancak, Gazi Osman Paşa Blv. No 1, 35210 Konak/İzmir",
                                38.430011014313536,
                                27.137438304021476
                            ),
                            PrepopulatedStation(
                                "Eşarj Şarj İstasyonu",
                                "Akdeniz, Gazi Blv. No:1, 35210 Konak/İzmir",
                                38.42650778390244,
                                27.131766622516334
                            ),
                            PrepopulatedStation(
                                "Zes Şarj İstasyonu",
                                "Yeşilova, 4174. Sk No:84 D:304, 35080 Bornova/İzmir",
                                38.43325775938112,
                                27.208988747944435
                            ),
                            PrepopulatedStation(
                                "Zeplin Energy Şarj İstasyonu",
                                "Kazımdirik, 367. Sk. No:7, 35100 Bornova/İzmir",
                                38.452364371325366,
                                27.202242185923524
                            ),
                            PrepopulatedStation(
                                "Otopriz Şarj İstasyonu",
                                "Varyant Tower, Rafet Paşa, Yıldırım Beyazıt Cd. No:213, 35090 Bornova/İzmir",
                                38.44432722942072,
                                27.19656515039365
                            ),
                            PrepopulatedStation(
                                "En Yakıt Şarj İstasyonu",
                                "Gazi Osman Paşa, Refik Tulga Cd. 22/A, 35090 Bornova/İzmir",
                                38.43671677012164,
                                27.182263387808757
                            ),
                            PrepopulatedStation(
                                "Shell Recharge Şarj İstasyonu",
                                "Çınarlı, 1561. Sk., 35110 Konak/İzmir",
                                38.439826127202366,
                                27.17067391394456
                            ),
                            PrepopulatedStation(
                                "Beefull Şarj İstasyonu",
                                "Çınarlı, 1561. Sk., 35170 Konak/İzmir",
                                38.437981337646576,
                                27.167258821184358
                            ),
                            PrepopulatedStation(
                                "D-Charge Şarj İstasyonu",
                                "Eröz İzmir- Audi, Umurbey, 35230 Konak/İzmir",
                                38.43850811465564,
                                27.161271261754102
                            ),
                            PrepopulatedStation(
                                "Shell Recharge Şarj İstasyonu",
                                "İsmet Kaptan, 9 Eylül Meydanı, 35210 Konak/İzmir",
                                38.42596588603997,
                                27.142796868273773
                            ),
                            PrepopulatedStation(
                                "Otowatt Şarj İstasyonu",
                                "Oğuzlar, Mürselpaşa Blv. No:48, 35230 Konak/İzmir",
                                38.42717196422857,
                                27.149570812549896
                            ),
                            PrepopulatedStation(
                                "Zes Şarj İstasyonu",
                                "Yenişehir, İşçiler Cd. No:126, 35170 Konak/İzmir",
                                38.43247846897424,
                                27.158038242895046
                            ),
                            PrepopulatedStation(
                                "Q Charge Şarj İstasyonu",
                                "Alsancak, Ali Çetinkaya Blv No:79, 35220 Konak/İzmir",
                                38.43513157511414,
                                27.148954999433887
                            ),
                            PrepopulatedStation(
                                "Trugo Şarj İstasyonu",
                                "Uğur Mumcu, 35650 Çiğli/İzmir",
                                38.52450941073033,
                                27.04315953566557
                            ),
                            PrepopulatedStation(
                                "Trugo Charging Station",
                                "30 Ağustos, 35660 Menemen/İzmir",
                                38.56267847176313,
                                27.044856967440026
                            ),
                            PrepopulatedStation(
                                "En Yakit Şarj İstasyonu",
                                "Balatçık, 8789. Sk, 35620 Çiğli/İzmir",
                                38.516541138327646,
                                27.045493504496793
                            ),
                            PrepopulatedStation(
                                "Zes Şarj İstasyonu",
                                "Şemikler, Anadolu Cd. No:695, 35560 Karşıyaka/İzmir",
                                38.4866522631386,
                                27.084958793252767
                            ),
                            PrepopulatedStation(
                                "Zes Şarj İstasyonu",
                                "Maltepe, Anadolu Cd. No:736, 35640 Çiğli/İzmir",
                                38.4926310303921,
                                27.072652412888
                            ),
                            PrepopulatedStation(
                                "Trugo Charging Station",
                                "Bostanlı, 35590 Karşıyaka/İzmir",
                                38.46422746809933,
                                27.089838909604318
                            ),
                            PrepopulatedStation(
                                "En Yakit Şarj İstasyonu",
                                "Mavişehir, 35590 Karşıyaka/İzmir",
                                38.47303210818393,
                                27.07392548671884
                            ),
                            PrepopulatedStation(
                                "Otojet Şarj İstasyonu",
                                "Örnekköy, İzmir Çevre Yolu No:101, 35575 Karşıyaka/İzmir",
                                38.491800675493415,
                                27.105540153706826
                            ),
                            PrepopulatedStation(
                                "Eşarj Şarj İstasyonu",
                                "Cumhuriyet, Anadolu Cd. 546/A, 35570 Karşıyaka/İzmir",
                                38.480506901873724,
                                27.095355563060117
                            ),
                            PrepopulatedStation(
                                "Otowatt Elektrikli Araç Şarj İstasyonu",
                                "Bahriye Üçok, Rüştü Şardağ Cd No:52, 35580 Karşıyaka/İzmir",
                                38.464393603030516,
                                27.10978373323546
                            ),
                            PrepopulatedStation(
                                "Shell Recharge Şarj İstasyonu",
                                "ADA:36926, PAFTA: - PARSEL:7 No:170, 35510 Bayraklı/İzmir",
                                38.46904523423156,
                                27.136942641626675
                            ),
                            PrepopulatedStation(
                                "Tuncmatik Şarj İstasyonu",
                                "Yeşilova, 364/6. Sk. No:1, 35100 Bornova/İzmir",
                                38.44813084607872,
                                27.206345633971512
                            ),
                            PrepopulatedStation(
                                "Trugo Şarj İstasyonu",
                                "Çınarlı, 35170 Konak/İzmir",
                                38.4453207835989,
                                27.172898520250012
                            ),
                            PrepopulatedStation(
                                "Zes Şarj İstasyonu",
                                "Mistral İzmir/Rezidans, Çınarlı, Ankara Asfaltı Cd. Kapalı Otopark No: 15, 35179 Kemalpaşa Osb/Konak/İzmir",
                                38.450698403698965,
                                27.178906668406988
                            ),
                            PrepopulatedStation(
                                "ZES Şarj İstasyonu",
                                "Adalet, Manas Blv. 37-4, 35530 Bayraklı/İzmir",
                                38.456344473538024,
                                27.17633174787015
                            ),
                            PrepopulatedStation(
                                "5 Şarj Şarj İstasyonu",
                                "Kazımdirik, Üniversite Cd. No:66, 35100 Bornova/İzmir",
                                38.45661332298435,
                                27.198304403987112
                            ),
                            PrepopulatedStation(
                                "Şarjon Şarj İstasyonu",
                                "Kazımdirik, Üniversite Cd. No:9, 35100 Bornova/İzmir",
                                38.45740751653666,
                                27.217348910692124
                            ),
                            PrepopulatedStation(
                                "Eşarj Şarj İstasyonu",
                                "Doğanlar, Ankara Cd. 210B, 35050 Bornova/İzmir",
                                38.460633596169615,
                                27.25408444533722
                            ),
                            PrepopulatedStation(
                                "Trugo Şarj İstasyonu",
                                "Kemalpaşa, Kemalpaşa Cd. 282A, 35060 Bornova/İzmir",
                                38.43920298843347,
                                27.251248639038167
                            )
                        )

                        val chargerPool = listOf(
                            // 11 kW Kombinasyonları (AC Eco / Slow DC)
                            Triple("Eco Standard", PowerOutput.KW_11, ConnectorType.TYPE_2),
                            Triple("Eco Fast", PowerOutput.KW_11, ConnectorType.CCS),
                            Triple("Eco Legacy", PowerOutput.KW_11, ConnectorType.CHADEMO),

                            // 22 kW Kombinasyonları (AC Standard)
                            Triple("AC Standard", PowerOutput.KW_22, ConnectorType.TYPE_2),
                            Triple("Universal 22", PowerOutput.KW_22, ConnectorType.CCS),
                            Triple("CHADEMO 22", PowerOutput.KW_22, ConnectorType.CHADEMO),

                            // 50 kW Kombinasyonları (DC Fast)
                            Triple("Fast Charge T2", PowerOutput.KW_50, ConnectorType.TYPE_2),
                            Triple("Fast Charge CCS", PowerOutput.KW_50, ConnectorType.CCS),
                            Triple("Fast Charge CDM", PowerOutput.KW_50, ConnectorType.CHADEMO),

                            // 150 kW Kombinasyonları (DC Ultra)
                            Triple("Ultra T2", PowerOutput.KW_150, ConnectorType.TYPE_2),
                            Triple("Ultra CCS", PowerOutput.KW_150, ConnectorType.CCS),
                            Triple("Ultra CHADEMO", PowerOutput.KW_150, ConnectorType.CHADEMO),

                            // 300 kW Kombinasyonları (DC Hyper)
                            Triple("Hyper T2", PowerOutput.KW_300, ConnectorType.TYPE_2),
                            Triple("Hyper CCS", PowerOutput.KW_300, ConnectorType.CCS),
                            Triple("Hyper CHADEMO", PowerOutput.KW_300, ConnectorType.CHADEMO)
                        )

                        val statuses = ChargerStatus.values()

                        stationsData.forEach { data ->
                            val stationId = dao.insertStation(
                                StationEntity(
                                    id = 0,
                                    name = data.name,
                                    latitude = data.lat,
                                    longitude = data.lng,
                                    address = data.address
                                )
                            )

                            // Her istasyona rastgele 2 ile 4 arası cihaz ata
                            val numberOfChargers = Random.nextInt(3) + 2
                            val selectedChargers = chargerPool.shuffled().take(numberOfChargers)

                            selectedChargers.forEach { poolItem ->
                                // 🔹 1. Değişkenleri poolItem içinden çıkartıp tanımlıyoruz
                                val chargerName = poolItem.first   // Örn: "Hyper Charge"
                                val power = poolItem.second        // Örn: PowerOutput.KW_300
                                val connector = poolItem.third     // Örn: ConnectorType.CCS

                                // 🔹 2. Güce göre tipi belirliyoruz (11 ve 22 AC, diğerleri DC)
                                val type =
                                    if (power == PowerOutput.KW_11 || power == PowerOutput.KW_22) {
                                        ChargerType.AC
                                    } else {
                                        ChargerType.DC
                                    }

                                // 🔹 3. Güce göre fiyatı belirliyoruz (Daha önce konuştuğumuz 2. seçenek - Granüler Fiyat)
                                val basePrice = when (power) {
                                    PowerOutput.KW_11 -> 7.5 + (Random.nextDouble() * 1.5)
                                    PowerOutput.KW_22 -> 9.5 + (Random.nextDouble() * 2.0)
                                    PowerOutput.KW_50 -> 14.5 + (Random.nextDouble() * 3.0)
                                    PowerOutput.KW_150 -> 21.0 + (Random.nextDouble() * 4.0)
                                    PowerOutput.KW_300 -> 28.0 + (Random.nextDouble() * 6.0)
                                    else -> 12.0
                                }
                                val finalPrice = (basePrice * 100.0).roundToInt() / 100.0

                                // 🔹 4. Şimdi tanımladığımız bu değişkenleri Entity'e basıyoruz
                                dao.insertCharger(
                                    ChargerEntity(
                                        id = 0,
                                        stationOwnerId = stationId,
                                        chargerName = chargerName,
                                        pricePerKwh = finalPrice,  // Data class'ındaki 4. sıra
                                        chargerType = type,        // Az önce yukarıda tanımladığımız 'type'
                                        powerOutput = power,       // poolItem'dan gelen 'power'
                                        connectorType = connector, // poolItem'dan gelen 'connector'
                                        chargerStatus = statuses.random()
                                    )
                                )
                            }
                        }
                    }
                }
            })
            .build()
    }

    @Singleton
    @Provides
    fun provideAppDao(db: AppDatabase) = db.appDao()

    @Singleton
    @Provides
    fun provideStationRepository(dao: AppDao): StationRepository = StationRepositoryImpl(dao)

    @Singleton
    @Provides
    fun provideUserRepository(dao: AppDao): UserRepository = UserRepositoryImpl(dao)

    @Singleton
    @Provides
    fun provideReservationRepository(dao: AppDao): ReservationRepository =
        ReservationRepositoryImpl(dao)

    @Singleton
    @Provides
    fun provideReportRepository(dao: AppDao): ReportRepository =
        ReportRepositoryImpl(dao)

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("users")
        }
    }
}

