package com.example.mcrmedicinereminder.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcrmedicinereminder.repository.MedicineRepository
import com.example.mcrmedicinereminder.data.MedicineReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderViewModel(private val medicineRepository: MedicineRepository) : ViewModel() {

    fun getReminder(): LiveData<List<MedicineReminder>> {
        return medicineRepository.getReminder()
    }
    fun getDonateMedicine(): LiveData<Int> {
        return medicineRepository.getDonateMedicine()
    }
    fun getStockMedicine(): LiveData<List<RestockMedicine>> {
        return medicineRepository.getStockMedicine()
    }

    fun insertReminder(medicineReminder: MedicineReminder) {
        viewModelScope.launch(Dispatchers.IO) {
            medicineRepository.insertReminder(medicineReminder)
        }
    }
    fun deleteReminder(medicineReminder: MedicineReminder) {
        viewModelScope.launch(Dispatchers.IO) {
            medicineRepository.deleteReminder(medicineReminder)
        }
    }
    fun updateReminder(medicineReminder: MedicineReminder) {
        viewModelScope.launch(Dispatchers.IO) {
            medicineRepository.updateReminder(medicineReminder)
        }
    }
    fun updateReminder(medicineName: String, medicineStock: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            medicineRepository.updateReminder(medicineName,medicineStock)
        }
    }
}