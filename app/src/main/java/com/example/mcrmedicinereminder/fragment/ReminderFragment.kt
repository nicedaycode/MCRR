package com.example.mcrmedicinereminder.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mcrmedicinereminder.activity.MedicineActivity
import com.example.mcrmedicinereminder.R
import com.example.mcrmedicinereminder.adapter.MedicineRecyclerviewAdapter
import com.example.mcrmedicinereminder.databinding.FragmentReminderBinding
import com.example.mcrmedicinereminder.model.ReminderViewModel
import com.example.mcrmedicinereminder.model.ReminderViewModelFactory
import com.example.mcrmedicinereminder.repository.MedicineRepository
import com.example.mcrmedicinereminder.data.MedicineReminder
import com.example.mcrmedicinereminder.data.MedicineReminderDatabase
import java.text.SimpleDateFormat
import java.util.*

class ReminderFragment : Fragment() , MedicineRecyclerviewAdapter.OnClickItem{
    private lateinit var binding: FragmentReminderBinding
    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("MMMM dd,", Locale.US)
    lateinit var adapter: MedicineRecyclerviewAdapter
    lateinit var reminderViewModel: ReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminder, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.shimmer.startShimmer()

        binding.addMedicineBtn.setOnClickListener {
            val intent = Intent(it.context, MedicineActivity::class.java)
            startActivity(intent)
        }

        binding.todayDate.text = formatter.format(calendar.timeInMillis)
//        binding.calender.setOnClickListener {
//            DatePickerDialog(
//                it.context,
//                object : DatePickerDialog.OnDateSetListener {
//                    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
//                        calendar.set(p1, p2, p3)
//                        binding.todayDate.text = formatter.format(calendar.timeInMillis)
//                    }
//                },
//                calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH)
//            ).show()
//        }


        binding.medicineReminderRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = MedicineRecyclerviewAdapter(requireContext(),this)
        binding.medicineReminderRecyclerview.adapter = adapter

        val dao = MedicineReminderDatabase.getDatabase(requireContext()).medicineReminderDao()
        val repository = MedicineRepository(dao)

        reminderViewModel =ViewModelProvider(requireActivity(),ReminderViewModelFactory(repository)).get(ReminderViewModel::class.java)

        reminderViewModel.getReminder().observe(requireActivity(), androidx.lifecycle.Observer {
            Handler().postDelayed({
                binding.shimmer.stopShimmer()
                binding.shimmer.visibility = View.GONE
                if (adapter.itemCount == 0){
                    binding.defaultLayout.visibility = View.VISIBLE
                }
                else{
                    binding.defaultLayout.visibility = View.GONE
                    binding.medicineReminderRecyclerview.visibility = View.VISIBLE
                }
//                binding.defaultLayout.visibility = View.VISIBLE
//                binding.medicineReminderRecyclerview.visibility = View.VISIBLE
            },1000)

            adapter.updateMedicineList(it)
            binding.taskNo.text = adapter.itemCount.toString()
        })


    }

    override fun deleteRow(medicineReminder: MedicineReminder, position: Int) {
        reminderViewModel.deleteReminder(medicineReminder)
        adapter.notifyItemChanged(position)
    }

    override fun updateMedicineCheck(medicineReminder: MedicineReminder, position: Int) {
        reminderViewModel.updateReminder(medicineReminder)
        adapter.notifyItemChanged(position)
    }

    override fun updateMedicineStatus(medicineName: String, medicineStock: Int) {
        reminderViewModel.updateReminder(medicineName,medicineStock)
        adapter.notifyDataSetChanged()
    }

}