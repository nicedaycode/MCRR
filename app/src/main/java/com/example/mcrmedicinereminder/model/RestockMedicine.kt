package com.example.mcrmedicinereminder.model

import androidx.room.ColumnInfo

data class RestockMedicine (
    @ColumnInfo(name = "Medicine_NAME")
    val name : String,
    @ColumnInfo(name = "Medicine_STOCK")
    val number : Int
)