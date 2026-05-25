package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class, AdminConfig::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nc_tech_hub_db"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed database asynchronously in the IO pool
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            val dao = database.userDao()

                            // Seeding default general Admin settings
                            dao.insertAdminConfig(AdminConfig())

                            // Seeding default Admin user account
                            dao.insertUser(
                                User(
                                    username = "admin",
                                    passwordHash = "admin123",
                                    email = "nctechandservices@gmail.com",
                                    role = "Admin",
                                    planName = "Enterprise",
                                    isTwoFactorEnabled = true, // Force 2FA for Admin secure check
                                    secretOtpKey = "555555",
                                    mandatoryDetailsFilled = true,
                                    contactNo = "+91 6353161655",
                                    businessName = "NC Tech Hub Admin Suite",
                                    gstNo = "24AAAAC1234A1Z1",
                                    address = "Gift City, Gandhinagar, Gujarat"
                                )
                            )

                            // Seeding mock active subscriber users to populate administration displays
                            dao.insertUser(
                                User(
                                    username = "nikunj",
                                    passwordHash = "nikunj123",
                                    email = "nikunj@nctech.com",
                                    role = "User",
                                    planName = "Basic",
                                    isTwoFactorEnabled = true,
                                    secretOtpKey = "111111",
                                    mandatoryDetailsFilled = true,
                                    contactNo = "+91 6353161655",
                                    businessName = "Nikunj Car Deals",
                                    gstNo = "24KUNJ1234X8B9Z",
                                    address = "Ahmdabad, Gujarat"
                                )
                            )
                            dao.insertUser(
                                User(
                                    username = "chirag",
                                    passwordHash = "chirag123",
                                    email = "chirag@nctech.com",
                                    role = "User",
                                    planName = "Premium",
                                    isTwoFactorEnabled = false,
                                    secretOtpKey = "222222",
                                    mandatoryDetailsFilled = true,
                                    contactNo = "+91 8200782140",
                                    businessName = "Chirag Insurance Group",
                                    gstNo = "24CHIRAG9876C1Z6",
                                    address = "Surat, Gujarat"
                                )
                            )
                            dao.insertUser(
                                User(
                                    username = "dev_studio",
                                    passwordHash = "dev123",
                                    email = "studio@nctech.com",
                                    role = "User",
                                    planName = "Enterprise",
                                    isTwoFactorEnabled = true,
                                    secretOtpKey = "333333",
                                    mandatoryDetailsFilled = false,
                                    contactNo = "+91 9998887776",
                                    businessName = "Dev Studio Inc.",
                                    gstNo = "24STUDIO5555M1Z5",
                                    address = "Mumbai, Maharashtra"
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
