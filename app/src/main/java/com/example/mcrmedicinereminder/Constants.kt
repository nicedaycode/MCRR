package com.example.mcrmedicinereminder

import com.example.mcrmedicinereminder.model.MedicineTypes

object Constants {
    fun getMedicineType(): ArrayList<MedicineTypes> {
        val medicineList = ArrayList<MedicineTypes>()
        val med1 = MedicineTypes("Others",R.drawable.others)
        medicineList.add(med1)
        val med2 = MedicineTypes("Tablet",R.drawable.tablet)
        medicineList.add(med2)
        val med3 = MedicineTypes("Syrup",R.drawable.syrup)
        medicineList.add(med3)
        val med4 = MedicineTypes("Ointment",R.drawable.ointment)
        medicineList.add(med4)
        val med5 = MedicineTypes("Injection",R.drawable.injection)
        medicineList.add(med5)
        val med6 = MedicineTypes("Dropper",R.drawable.dropper)
        medicineList.add(med6)
        val med7 = MedicineTypes("Homeopathy",R.drawable.homeopathy)
        medicineList.add(med7)
        return medicineList
    }

}