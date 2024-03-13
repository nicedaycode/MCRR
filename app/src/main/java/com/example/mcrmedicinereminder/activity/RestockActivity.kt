package com.example.mcrmedicinereminder.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mcrmedicinereminder.R
import com.example.mcrmedicinereminder.adapter.RestockMedicineRecyclerviewAdapter
import com.example.mcrmedicinereminder.databinding.ActivityRestockMedicineBinding
import com.example.mcrmedicinereminder.model.ReminderViewModel
import com.example.mcrmedicinereminder.model.ReminderViewModelFactory
import com.example.mcrmedicinereminder.repository.MedicineRepository
import com.example.mcrmedicinereminder.data.MedicineReminderDatabase

class RestockActivity : AppCompatActivity(),RestockMedicineRecyclerviewAdapter.OnStockItemClick {
    private lateinit var binding: ActivityRestockMedicineBinding
    private lateinit var restockMedicineViewModel: ReminderViewModel
    lateinit var adapter: RestockMedicineRecyclerviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restock_medicine)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.restockMedicineRecyclerview.layoutManager = LinearLayoutManager(this)
        adapter = RestockMedicineRecyclerviewAdapter(this,this)
        binding.restockMedicineRecyclerview.adapter = adapter

        val dao = MedicineReminderDatabase.getDatabase(this).medicineReminderDao()
        val repository = MedicineRepository(dao)

        restockMedicineViewModel = ViewModelProvider(
            this,
            ReminderViewModelFactory(repository)
        ).get(ReminderViewModel::class.java)

        restockMedicineViewModel.getStockMedicine().observe(this, Observer {
            Log.d("RAHUL",it.toString())
            adapter.updateStockList(it)
        })
    }

    override fun updateStockMedicine(medicineName: String, medicineStock: Int) {
        restockMedicineViewModel.updateReminder(medicineName,medicineStock)
        adapter.notifyDataSetChanged()
    }
}