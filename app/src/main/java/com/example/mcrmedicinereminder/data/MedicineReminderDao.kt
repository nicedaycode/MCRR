package com.example.mcrmedicinereminder.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mcrmedicinereminder.data.MedicineReminder
import com.example.mcrmedicinereminder.model.RestockMedicine

@Dao
interface MedicineReminderDao {
    @Query("SELECT * FROM medicine_reminder_details ")
    fun getMedicineReminder(): LiveData<List<MedicineReminder>>

    @Query("UPDATE medicine_reminder_details SET Medicine_STOCK = :medicineStock WHERE Medicine_NAME = :medicineName")
    fun updateMedicineReminder(medicineName: String, medicineStock: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMedicineReminder(medicineReminder: MedicineReminder)

    @Delete
    fun deleteMedicineReminder(medicineReminder: MedicineReminder)

    @Update
    fun updateMedicineCheck(medicineReminder: MedicineReminder)

    // Get Medicine Details
    @Query("SELECT SUM(Medicine_STOCK) FROM (SELECT DISTINCT Medicine_NAME ,Medicine_STOCK FROM medicine_reminder_details WHERE Medicine_TYPE = \"Tablet\")")
    fun getDonateMedicine() : LiveData<Int>

    // Get Stock Medicine Details
    @Query("SELECT DISTINCT Medicine_NAME ,Medicine_STOCK FROM medicine_reminder_details WHERE Medicine_TYPE = \"Tablet\" ")
    fun getStockMedicine() : LiveData<List<RestockMedicine>>


}