package com.example.mcrmedicinereminder.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import com.example.mcrmedicinereminder.R
import com.example.mcrmedicinereminder.databinding.ActivityScheduleBinding
import com.example.mcrmedicinereminder.model.WeekDayValues
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class ScheduleActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    lateinit var binding: ActivityScheduleBinding
    private val frequencyArray: Array<String> = arrayOf(
        "1",
        "2",
        "3",
        "4",
    )
    private lateinit var medicineFrequency: String
    private val calendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy hh:mm a", Locale.US)
    private val medicineCalendar = Calendar.getInstance()
    private val tabletFrequencyFormatter = SimpleDateFormat("hh:mm a", Locale.US)

    // Schedule Message variables
    private lateinit var noOfDaysSM: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule)

        // Back Pressed
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        val bundle = intent.extras
        val value = bundle?.getString("unit")
        binding.timeOneTab.text = "1 $value"
        binding.timeTwoTab.text = "1 $value"
        binding.timeThreeTab.text = "1 $value"
        binding.timeFourTab.text = "1 $value"
        // Save Schedule
        binding.saveBtn.setOnClickListener {
//            super.onBackPressed()
            val bundle = Bundle()
            bundle.putString("scheduleset", medicineFrequency)
            bundle.putString("schedulemessage", chooseWeekDays())

            bundle.putString("time1", binding.timeOne.text.toString())
            bundle.putString("time2", binding.timeTwo.text.toString())
            bundle.putString("time3", binding.timeThree.text.toString())
            bundle.putString("time4", binding.timeFour.text.toString())

            bundle.putString("tablet1", binding.timeOneTab.text.toString())
            bundle.putString("tablet2", binding.timeTwoTab.text.toString())
            bundle.putString("tablet3", binding.timeThreeTab.text.toString())
            bundle.putString("tablet4", binding.timeFourTab.text.toString())

            bundle.putString("durationstart", binding.durationStart.text.toString())

            if (binding.durationEnd.text.toString() != "Continuous") {
                bundle.putString("durationend", binding.durationEnd.text.toString())
            }
            val intent = Intent()
            intent.putExtra("Bundle",bundle)
            setResult(RESULT_OK,intent)
            finish()

        }


        // Frequency Spinner
        val frequencyAdapter =
            ArrayAdapter(this, R.layout.instruction_spinner_list, frequencyArray)
        frequencyAdapter.setDropDownViewResource(R.layout.instruction_spinner_list)
        binding.frequencySpn.adapter = frequencyAdapter
        binding.frequencySpn.setSelection(2)
        binding.frequencySpn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                medicineFrequency = frequencyArray[p2]
                when (binding.frequencySpn.selectedItem.toString()) {
                    "1" -> {
                        medicineFrequency = "One Times"
                        binding.medicineTimeOne.visibility = View.VISIBLE
                        binding.medicineTimeTwo.visibility = View.GONE
                        binding.medicineTimeThree.visibility = View.GONE
                        binding.medicineTimeFour.visibility = View.GONE
                    }
                    "2" -> {
                        medicineFrequency = "Two Times"
                        binding.medicineTimeOne.visibility = View.VISIBLE
                        binding.medicineTimeTwo.visibility = View.GONE
                        binding.medicineTimeThree.visibility = View.VISIBLE
                        binding.medicineTimeFour.visibility = View.GONE
                    }
                    "3" -> {
                        medicineFrequency = "Three Times"
                        binding.medicineTimeOne.visibility = View.VISIBLE
                        binding.medicineTimeTwo.visibility = View.VISIBLE
                        binding.medicineTimeThree.visibility = View.VISIBLE
                        binding.medicineTimeFour.visibility = View.GONE
                    }
                    "4" -> {
                        medicineFrequency = "Four Times"
                        binding.medicineTimeOne.visibility = View.VISIBLE
                        binding.medicineTimeTwo.visibility = View.VISIBLE
                        binding.medicineTimeThree.visibility = View.VISIBLE
                        binding.medicineTimeFour.visibility = View.VISIBLE
                    }

                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        // Set Timing
        medicineFrequencyTime()

        //Set Dose
        tabletAdjustment()

        // Set Duration
        durationTime()

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("Change were made in the schedule.")
        builder.apply {
            setPositiveButton("SAVE", DialogInterface.OnClickListener { dialog, id ->
                val bundle = Bundle()
                bundle.putString("scheduleset", medicineFrequency)
                bundle.putString("schedulemessage", chooseWeekDays())

                bundle.putString("time1", binding.timeOne.text.toString())
                bundle.putString("time2", binding.timeTwo.text.toString())
                bundle.putString("time3", binding.timeThree.text.toString())
                bundle.putString("time4", binding.timeFour.text.toString())

                bundle.putString("tablet1", binding.timeOneTab.text.toString())
                bundle.putString("tablet2", binding.timeTwoTab.text.toString())
                bundle.putString("tablet3", binding.timeThreeTab.text.toString())
                bundle.putString("tablet4", binding.timeFourTab.text.toString())

                bundle.putString("durationstart", binding.durationStart.text.toString())

                if (binding.durationEnd.text.toString() != "Continuous") {
                    bundle.putString("durationend", binding.durationEnd.text.toString())
                }
                val intent = Intent()
                intent.putExtra("Bundle",bundle)
                setResult(RESULT_OK,intent)
                finish()
            })
        }
            .setNegativeButton("DISCARD", DialogInterface.OnClickListener { dialogInterface, i ->
                finish()
                dialogInterface.dismiss()
            })
        builder.show()

    }

    private fun chooseWeekDays(): String {

        val dayValues = arrayOf(
            WeekDayValues("Monday", binding.checkMonday.isChecked),
            WeekDayValues("Tuesday", binding.checkTuesday.isChecked),
            WeekDayValues("Wednesday", binding.checkWednesday.isChecked),
            WeekDayValues("Thursday", binding.checkThursday.isChecked),
            WeekDayValues("Friday", binding.checkFriday.isChecked),
            WeekDayValues("Saturday", binding.checkSaturday.isChecked),
            WeekDayValues("Sunday", binding.checkSunday.isChecked)
        )

        val days: ArrayList<WeekDayValues> = ArrayList()
        val daysFalse: ArrayList<WeekDayValues> = ArrayList()
        for (item in dayValues.indices) {
            if (dayValues[item].value) {
                days.add(WeekDayValues(dayValues[item].day, dayValues[item].value))
            } else {
                daysFalse.add(WeekDayValues(dayValues[item].day, dayValues[item].value))
            }
        }

        if (days.size == 1) {
            noOfDaysSM = "$medicineFrequency Every " + days[0].day
        }
        if (days.size == 2) {
            noOfDaysSM = "$medicineFrequency Every " + days[0].day + " And " + days[1].day
        }
        if (days.size == 3) {
            noOfDaysSM =
                "$medicineFrequency Every " + days[0].day + ", " + days[1].day + " And " + days[2].day
        }
        if (days.size == 4) {
            noOfDaysSM =
                "$medicineFrequency Every " + days[0].day + ", " + days[1].day + ", " + days[2].day + " And " + days[3].day
        }
        if (days.size == 5) {
            noOfDaysSM =
                "$medicineFrequency Daily Except For " + daysFalse[0].day + " And " + daysFalse[1].day
        }
        if (days.size == 6) {
            noOfDaysSM = "$medicineFrequency Daily Except For " + daysFalse[0].day
        }
        if (days.size == 7) {
            noOfDaysSM = "$medicineFrequency Daily"
        }
        return noOfDaysSM
    }

    private fun medicineFrequencyTime() {
        binding.timeOne.setOnClickListener {
            TimePickerDialog(
                it.context,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                        medicineCalendar.apply {
                            set(Calendar.HOUR_OF_DAY, p1)
                            set(Calendar.MINUTE, p2)
                        }
                        binding.timeOne.text =
                            tabletFrequencyFormatter.format(medicineCalendar.timeInMillis)
                    }

                },
                7,
                0,
                false
            ).show()
        }
        binding.timeTwo.setOnClickListener {
            TimePickerDialog(
                it.context,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                        medicineCalendar.apply {
                            set(Calendar.HOUR_OF_DAY, p1)
                            set(Calendar.MINUTE, p2)
                        }
                        binding.timeTwo.text =
                            tabletFrequencyFormatter.format(medicineCalendar.timeInMillis)
                    }

                },
                1,
                0,
                false
            ).show()
        }
        binding.timeThree.setOnClickListener {
            TimePickerDialog(
                it.context,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                        medicineCalendar.apply {
                            set(Calendar.HOUR_OF_DAY, p1)
                            set(Calendar.MINUTE, p2)
                        }
                        binding.timeThree.text =
                            tabletFrequencyFormatter.format(medicineCalendar.timeInMillis)
                    }

                },
                7,
                0,
                false
            ).show()
            binding.timeFour.setOnClickListener {
                TimePickerDialog(
                    it.context,
                    object : TimePickerDialog.OnTimeSetListener {
                        override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                            medicineCalendar.apply {
                                set(Calendar.HOUR_OF_DAY, p1)
                                set(Calendar.MINUTE, p2)
                            }
                            binding.timeFour.text =
                                tabletFrequencyFormatter.format(medicineCalendar.timeInMillis)
                        }

                    },
                    10,
                    0,
                    false
                ).show()
            }
        }
    }

    private fun tabletAdjustment() {

        binding.timeOneTab.setOnClickListener {
            val dialog = Dialog(it.context)
            dialog.setContentView(R.layout.tablet_adjustment)
            val stockQty = dialog.findViewById(R.id.stock_qty_edt) as EditText
            val stockUnit = dialog.findViewById(R.id.stock_unit) as TextView
            val adjust = dialog.findViewById(R.id.set_Qty) as Button
            adjust.setOnClickListener {
                val qty = stockQty.text.toString()
                binding.timeOneTab.text = qty + " tab"
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.timeTwoTab.setOnClickListener {
            val dialog = Dialog(it.context)
            dialog.setContentView(R.layout.tablet_adjustment)
            val stockQty = dialog.findViewById(R.id.stock_qty_edt) as EditText
            val stockUnit = dialog.findViewById(R.id.stock_unit) as TextView
            val adjust = dialog.findViewById(R.id.set_Qty) as Button
            adjust.setOnClickListener {
                val qty = stockQty.text.toString()
                binding.timeTwoTab.text = qty + " tab"
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.timeThreeTab.setOnClickListener {
            val dialog = Dialog(it.context)
            dialog.setContentView(R.layout.tablet_adjustment)
            val stockQty = dialog.findViewById(R.id.stock_qty_edt) as EditText
            val stockUnit = dialog.findViewById(R.id.stock_unit) as TextView
            val adjust = dialog.findViewById(R.id.set_Qty) as Button
            adjust.setOnClickListener {
                val qty = stockQty.text.toString()
                binding.timeThreeTab.text = qty + " tab"
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.timeFourTab.setOnClickListener {
            val dialog = Dialog(it.context)
            dialog.setContentView(R.layout.tablet_adjustment)
            val stockQty = dialog.findViewById(R.id.stock_qty_edt) as EditText
            val stockUnit = dialog.findViewById(R.id.stock_unit) as TextView
            val adjust = dialog.findViewById(R.id.set_Qty) as Button
            adjust.setOnClickListener {
                val qty = stockQty.text.toString()
                binding.timeFourTab.text = qty + " tab"
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun durationTime() {
        binding.durationStart.text = formatter.format(medicineCalendar.timeInMillis)
        // Duration Start
        binding.durationStart.setOnClickListener {
            DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Duration End
        binding.durationEnd.setOnClickListener {
            DatePickerDialog(
                this,
                object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                        calendar.set(p1, p2, p3)
                        binding.durationEnd.text = formatter.format(calendar.timeInMillis)
                        if (p0 != null) {
                            TimePickerDialog(
                                p0.context,
                                object : TimePickerDialog.OnTimeSetListener {
                                    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                                        calendar.apply {
                                            set(Calendar.HOUR_OF_DAY, p1)
                                            set(Calendar.MINUTE, p2)
                                        }
                                        binding.durationEnd.text =
                                            formatter.format(calendar.timeInMillis)
                                    }
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                false
                            ).show()
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        calendar.set(p1, p2, p3)
        displayFormattedDate(calendar.timeInMillis)
        TimePickerDialog(
            this,
            this,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun displayFormattedDate(timestamp: Long) {
        binding.durationStart.text = formatter.format(timestamp)
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, p1)
            set(Calendar.MINUTE, p2)
        }
        displayFormattedDate(calendar.timeInMillis)
    }


}