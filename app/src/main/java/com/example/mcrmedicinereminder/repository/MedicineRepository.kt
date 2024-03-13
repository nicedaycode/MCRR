package com.example.mcrmedicinereminder.repository

import androidx.lifecycle.LiveData
import com.example.mcrmedicinereminder.model.RestockMedicine
import com.example.mcrmedicinereminder.data.MedicineReminder
import com.example.mcrmedicinereminder.data.MedicineReminderDao

class MedicineRepository(private val medicineReminderDao: MedicineReminderDao) {

    fun getReminder(): LiveData<List<MedicineReminder>> {
        return medicineReminderDao.getMedicineReminder()
    }
    fun getDonateMedicine(): LiveData<Int> {
        return medicineReminderDao.getDonateMedicine()
    }
    fun getStockMedicine(): LiveData<List<RestockMedicine>> {
        return medicineReminderDao.getStockMedicine()
    }

    suspend fun insertReminder(medicineReminder: MedicineReminder) {
        medicineReminderDao.insertMedicineReminder(medicineReminder)
    }

    suspend fun deleteReminder(medicineReminder: MedicineReminder) {
        medicineReminderDao.deleteMedicineReminder(medicineReminder)
    }
    suspend fun updateReminder(medicineReminder: MedicineReminder) {
        medicineReminderDao.updateMedicineCheck(medicineReminder)
    }

    suspend fun updateReminder(medicineName: String, medicineStock: Int) {
        medicineReminderDao.updateMedicineReminder(medicineName, medicineStock)
    }
}