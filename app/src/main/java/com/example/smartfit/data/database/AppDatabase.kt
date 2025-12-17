package com.example.smartfit.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ActivityLog::class, User::class, UserCredentials::class, FoodIntakeLog::class],
    version = 7, // Added avatar fields to User entity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun userDao(): UserDao
    abstract fun credentialsDao(): CredentialsDao
    abstract fun foodIntakeDao(): FoodIntakeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 1 to 2: Add user_profile table
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create user_profile table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_profile (
                        id INTEGER PRIMARY KEY NOT NULL,
                        name TEXT NOT NULL,
                        age INTEGER NOT NULL,
                        weight REAL NOT NULL,
                        height REAL NOT NULL,
                        gender TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }
        
        // Migration from version 2 to 3: No schema changes, just version bump
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // No changes needed, just version increment to fix schema mismatch
            }
        }
        
        // Migration from version 6 to 7: Add avatar fields to user_profile
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add avatar fields to user_profile table
                database.execSQL("ALTER TABLE user_profile ADD COLUMN avatarType TEXT NOT NULL DEFAULT 'preset'")
                database.execSQL("ALTER TABLE user_profile ADD COLUMN avatarId INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE user_profile ADD COLUMN customAvatarPath TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smartfit_database"
                )
                    .addMigrations(MIGRATION_6_7)
                    .fallbackToDestructiveMigration() // Recreate DB on version mismatch (dev mode)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}