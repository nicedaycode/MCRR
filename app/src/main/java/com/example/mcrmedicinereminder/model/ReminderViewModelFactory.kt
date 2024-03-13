package com.example.mcrmedicinereminder.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mcrmedicinereminder.repository.MedicineRepository

class ReminderViewModelFactory(private val medicineRepository: MedicineRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReminderViewModel(medicineRepository) as T
    }
}