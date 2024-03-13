package com.example.mcrmedicinereminder.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine_reminder_details")
data class MedicineReminder(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Medicine_ID")
    val id: Int? = null,
    @ColumnInfo(name = "Medicine_NAME")
    val medicineName: String,
    @ColumnInfo(name = "Medicine_Image")
    val medicineImage: Int,
    @ColumnInfo(name = "Medicine_TYPE")
    val medicineType: String,
    @ColumnInfo(name = "Medicine_QUANTITY")
    val medicineQnty: String,
    @ColumnInfo(name = "Medicine_STOCK")
    val stockSize: Int,
    @ColumnInfo(name = "Medicine_TIME")
    val medicineTime: String,
    @ColumnInfo(name = "Medicine_INSTRUCTION")
    val medicineInstruction: String,
    @ColumnInfo(name = "Medicine_CHECK")
    var medicineCheck: Boolean
)
