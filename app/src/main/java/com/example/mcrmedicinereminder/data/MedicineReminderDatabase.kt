package com.example.mcrmedicinereminder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MedicineReminder::class], version = 1)
abstract class MedicineReminderDatabase : RoomDatabase() {

    abstract fun medicineReminderDao(): MedicineReminderDao

    companion object {
        @Volatile
        private var INSTANCE: MedicineReminderDatabase? = null

        fun getDatabase(context: Context): MedicineReminderDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                       MedicineReminderDatabase::class.java, "Medicine_Database"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}