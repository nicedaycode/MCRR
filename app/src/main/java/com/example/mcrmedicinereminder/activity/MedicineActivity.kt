package com.example.mcrmedicinereminder.activity

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mcrmedicinereminder.*
import com.example.mcrmedicinereminder.adapter.MedicineTypesAdapter
import com.example.mcrmedicinereminder.databinding.ActivityMedicineBinding
import com.example.mcrmedicinereminder.model.ReminderViewModel
import com.example.mcrmedicinereminder.model.ReminderViewModelFactory
import com.example.mcrmedicinereminder.repository.MedicineRepository
import com.example.mcrmedicinereminder.data.MedicineReminder
import com.example.mcrmedicinereminder.data.MedicineReminderDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MedicineActivity : AppCompatActivity(), MedicineTypesAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMedicineBinding
    private val medicineUnit: Array<String> =
        arrayOf("tab", "syrup", "time", "mL(CC)", "drop", "ball", "other")
    private val instructionArray: Array<String> = arrayOf(
        "No Instructions",
        "Take before meal",
        "Take after meal",
        "Take on an empty stomach",
        "Take with water",
        "Never take with milk",
        "Avoid Sugars",
        "Avoid fatty food",
        "Eat more vegetables",
        "Eat more iron-rich foods"
    )
    private lateinit var medicineName: String
    private var medicineImage: Int = 1
    private lateinit var adapter: MedicineTypesAdapter
    private lateinit var doseUnit: String
    private var stockSize: Int = 12
    private lateinit var medicinefeq: String
    private lateinit var medicineSchedule: String
    private lateinit var medicineInstruction: String
    lateinit var reminderViewModel: ReminderViewModel
    private val tabletFrequencyFormatter = SimpleDateFormat("hh:mm a", Locale.US)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_medicine)


        val dao = MedicineReminderDatabase.getDatabase(this).medicineReminderDao()
        val repository = MedicineRepository(dao)

        reminderViewModel =
            ViewModelProvider(this, ReminderViewModelFactory(repository)).get(ReminderViewModel::class.java)

        // Back Button
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        createNotificationChannel()

        // Medicine Type Recyclerview
        binding.medicineImgsRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = MedicineTypesAdapter(this, Constants.getMedicineType(), this)
        binding.medicineImgsRecyclerview.adapter = adapter


        // Dose Unit Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, medicineUnit)
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        binding.doseSpinner.adapter = adapter

        // Dose Spinner item Selected
        binding.doseSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    pos: Int,
                    id: Long
                ) {
                    doseUnit = medicineUnit[pos]
                    binding.stockQtyTxt.text = "12 $doseUnit"
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
//                    doseUnit = "tab"
                }
            }

        // Current Stock

        binding.stockQtyTxt.setOnClickListener {
            val dialog = Dialog(it.context)
            dialog.setContentView(R.layout.stock_unit)
            val stockQty = dialog.findViewById(R.id.stock_qty_edt) as EditText
            val stockUnit = dialog.findViewById(R.id.stock_unit) as TextView
            val adjust = dialog.findViewById(R.id.set_Qty) as Button
            stockUnit.text = doseUnit
            adjust.setOnClickListener {
                val qty = stockQty.text.toString().toInt()
                binding.stockQtyTxt.text = "$qty $doseUnit"
                stockSize = qty
                dialog.dismiss()
            }

            dialog.show()
        }


        medicineSchedule = binding.scheduleTxt.text.toString()
        // Schedule Activity
        binding.scheduleBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("unit", doseUnit)
            val intent = Intent(it.context, ScheduleActivity::class.java)
            intent.putExtras(bundle)
            startActivityForResult(intent, 1)

        }


        // Instruction Spinner
        val instructionAdapter =
            ArrayAdapter(this, R.layout.instruction_spinner_list, instructionArray)
        instructionAdapter.setDropDownViewResource(R.layout.instruction_spinner_list)
        binding.instructionSpn.adapter = instructionAdapter

        //  Instructions Item Selected
        binding.instructionSpn.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    pos: Int,
                    id: Long
                ) {
                    medicineInstruction = instructionArray[pos].toString()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                    medicineInstruction = "No Instruction"
                }
            }


        // Add Button
        binding.addBtn.setOnClickListener {
            addMedicineNotification("Medicine","Medicine is Added")
            getMedicineDetail()
        }
    }


    override fun updateBackground(position: Int) {
        medicineImage = position
        binding.medicineType.text = Constants.getMedicineType()[position].name
        adapter.changeUI(position)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("Change were made in the medication.")
        builder.apply {
            setPositiveButton("SAVE", DialogInterface.OnClickListener { dialog, id ->
                getMedicineDetail()
            })
        }
            .setNegativeButton(
                "DISCARD",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    finish()
                    dialogInterface.dismiss()
                })
        builder.show()
    }

    private fun getMedicineDetail() {
        if ((binding.medicineName.text.toString()).isEmpty()) {
            binding.medicineName.error = "Medicine Name"
        }
        if (binding.scheduleTxt.text.toString() == "Not Selected"){
            binding.scheduleTxt.error = "Set Schedule"
        }
        else {
            this.medicineName = binding.medicineName.text.toString()

            when (medicinefeq) {
                "One Times" -> {
                    // only one time
                    reminderViewModel.insertReminder(MedicineReminder(null,medicineName,medicineImage,Constants.getMedicineType()[medicineImage].name,binding.timeOneTab.text.toString(),stockSize,binding.timeOne.text.toString(),medicineInstruction,false))

                    val timeOne = tabletFrequencyFormatter.parse(binding.timeOne.text.toString())
                    if (timeOne != null) {
                        scheduleNotification1(medicineName,binding.timeOne.text.toString(),timeOne)
                    }
//                    Toast.makeText(
//                        this,
//                        "$medicineName\n ${Constants.getMedicineType()[medicineImage].name}\n1\n$stockSize\n$medicineInstruction",
//                        Toast.LENGTH_LONG
//                    )
//                        .show()
                }
                "Two Times" -> {

                    // first time
                    reminderViewModel.insertReminder(MedicineReminder(null,medicineName,medicineImage,Constants.getMedicineType()[medicineImage].name,binding.timeOneTab.text.toString(),stockSize,binding.timeOne.text.toString(),medicineInstruction,false))

                    // second time
                    reminderViewModel.insertReminder(MedicineReminder(null,medicineName,medicineImage,Constants.getMedicineType()[medicineImage].name,binding.timeThreeTab.text.toString(),stockSize,binding.timeThree.text.toString(),medicineInstruction,false))

                    val timeOne = tabletFrequencyFormatter.parse(binding.timeOne.text.toString())
                    val timeTwo = tabletFrequencyFormatter.parse(binding.timeThree.text.toString())
                    if (timeOne != null) {
                        scheduleNotification1(medicineName,binding.timeOne.text.toString(),timeOne)
                    }
                    if (timeTwo != null) {
                        scheduleNotification2(medicineName,binding.timeThree.text.toString(),timeTwo)
                    }
//                    Toast.makeText(
//                        this,
//                        "$medicineName\n ${Constants.getMedicineType()[medicineImage].name}\n2\n$doseUnit\n $stockSize\n$medicineInstruction",
//                        Toast.LENGTH_LONG
//                    )
//                        .show()
                }
                "Three Times" -> {
                    // first time
                    reminderViewModel.insertReminder(MedicineReminder(null,medicineName,medicineImage,Constants.getMedicineType()[medicineImage].name,binding.timeOneTab.text.toString(),stockSize,binding.timeOne.text.toString(),medicineInstruction,false))

                    // second time
                    reminderViewModel.insertReminder(MedicineReminder(null,medicineName,medicineImage,Constants.getMedicineType()[medicineImage].name,binding.timeTwoTab.text.toString(),stockSize,binding.timeTwo.text.toString(),medicineInstruction,false))

                    // third time
                    reminderViewModel.insertReminder(MedicineReminder(null,medicineName,medicineImage,Constants.getMedicineType()[medicineImage].name,binding.timeThreeTab.text.toString(),stockSize,binding.timeThree.text.toString(),medicineInstruction,false))

                    val timeOne = tabletFrequencyFormatter.parse(binding.timeOne.text.toString())
                    val timeTwo = tabletFrequencyFormatter.parse(binding.timeTwo.text.toString())
                    val timeThree = tabletFrequencyFormatter.parse(binding.timeThree.text.toString())
                    if (timeOne != null) {
                        scheduleNotification1(medicineName,binding.timeOne.text.toString(),timeOne)
                    }
                    if (timeTwo != null) {
                        scheduleNotification2(medicineName,binding.timeTwo.text.toString(),timeTwo)
                    }
//                    if (timeThree != null) {
//                        scheduleNotification3(medicineName,binding.timeThree.text.toString(),timeThree)
//                    }
//                    Toast.makeText(
//                        this,
//                        "$medicineName\n ${Constants.getMedicineType()[medicineImage].name}\n3\n$stockSize\n$medicineInstruction",
//                        Toast.LENGTH_LONG
//                    )
//                        .show()
                }
                "Four Times" -> {
                    // first time
                    reminderViewModel.insertReminder(MedicineReminder(null,medicineName,medicineImage,Constants.getMedicineType()[medicineImage].name,binding.timeOneTab.text.toString(),stockSize,binding.timeOne.text.toString(),medicineInstruction,false))

                    // second time
                    reminderViewModel.insertReminder(MedicineReminder(null,medicineName,medicineImage,Constants.getMedicineType()[medicineImage].name,binding.timeTwoTab.text.toString(),stockSize,binding.timeTwo.text.toString(),medicineInstruction,false))

                    // third time
                    reminderViewModel.insertReminder(MedicineReminder(null,medicineName,medicineImage,Constants.getMedicineType()[medicineImage].name,binding.timeThreeTab.text.toString(),stockSize,binding.timeThree.text.toString(),medicineInstruction,false))

                    // fourth time
                    reminderViewModel.insertReminder(MedicineReminder(null,medicineName,medicineImage,Constants.getMedicineType()[medicineImage].name,binding.timeFourTab.text.toString(),stockSize,binding.timeFour.text.toString(),medicineInstruction,false))
////                    Toast.makeText(
////                        this,
////                        "$medicineName\n ${Constants.getMedicineType()[medicineImage].name}\n4\n$stockSize\n 4\n$medicineInstruction",
////                        Toast.LENGTH_LONG
////                    )
////                        .show()
                }
            }

//            Toast.makeText(
//                this,
//                "$medicineName\n ${Constants.getMedicineType()[medicineImage].image}\n ${Constants.getMedicineType()[medicineImage].name}\n "+binding.timeOne.text+"\n $stockSize\n ${binding.timeOneTab.text}\n $medicineInstruction",
//                Toast.LENGTH_LONG
//            ).show()
//            Log.d("RAHUL","$medicineName\n" +
//                    " ${Constants.getMedicineType()[medicineImage].image}\n" +
//                    " ${Constants.getMedicineType()[medicineImage].name}\n" +
//                    " ${binding.timeOne.text}\n" +
//                    " $stockSize\n" +
//                    " ${binding.timeOneTab.text}\n" +
//                    " $medicineInstruction")
//            Toast.makeText(this,Constants.getMedicineType().get(medicineImage).name,Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?)  {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (intent != null) {
                    binding.scheduleTxt.setTextColor(resources.getColor(R.color.colour1))
                    binding.scheduleTxt.text = intent.getBundleExtra("Bundle")
                        ?.getString("schedulemessage", "Not Selected")
                    var medicineTimeTable: String? = null
                    medicinefeq =
                        intent.getBundleExtra("Bundle")?.getString("scheduleset", "Not Selected")
                            .toString()
                    when (intent.getBundleExtra("Bundle")
                        ?.getString("scheduleset", "Not Selected")) {
                        "One Times" -> {
                            binding.medicineTimeOne.visibility = View.VISIBLE
                            binding.timeOne.text =
                                intent.getBundleExtra("Bundle")?.getString("time1", "07:00 AM")
                            binding.timeOneTab.text =
                                intent.getBundleExtra("Bundle")?.getString("tablet1", "1 tab")
                            binding.medicineTimeTwo.visibility = View.GONE
                            binding.medicineTimeThree.visibility = View.GONE
                            binding.medicineTimeFour.visibility = View.GONE
                            medicineTimeTable =
                                binding.timeOne.text.toString() + " -> " + binding.timeOneTab.text.toString()

                        }
                        "Two Times" -> {
                            binding.medicineTimeOne.visibility = View.VISIBLE
                            binding.medicineTimeTwo.visibility = View.GONE
                            binding.timeOne.text =
                                intent.getBundleExtra("Bundle")?.getString("time1", "07:00 AM")
                            binding.timeOneTab.text =
                                intent.getBundleExtra("Bundle")?.getString("tablet1", "1 tab")
                            binding.timeThree.text =
                                intent.getBundleExtra("Bundle")?.getString("time3", "01:00 PM")
                            binding.timeThreeTab.text =
                                intent.getBundleExtra("Bundle")?.getString("tablet3", "1 tab")
                            binding.medicineTimeThree.visibility = View.VISIBLE
                            binding.medicineTimeFour.visibility = View.GONE
                            medicineTimeTable =
                                binding.timeOne.text.toString() + " -> " + binding.timeOneTab.text.toString() + "\n" + binding.timeTwo.text.toString() + " -> " + binding.timeTwoTab.text.toString()
                        }
                        "Three Times" -> {
                            binding.medicineTimeOne.visibility = View.VISIBLE
                            binding.medicineTimeTwo.visibility = View.VISIBLE
                            binding.medicineTimeThree.visibility = View.VISIBLE
                            binding.timeOne.text =
                                intent.getBundleExtra("Bundle")?.getString("time1", "07:00 AM")
                            binding.timeOneTab.text =
                                intent.getBundleExtra("Bundle")?.getString("tablet1", "1 tab")
                            binding.timeTwo.text =
                                intent.getBundleExtra("Bundle")?.getString("time2", "01:00 PM")
                            binding.timeTwoTab.text =
                                intent.getBundleExtra("Bundle")?.getString("tablet2", "1 tab")
                            binding.timeThree.text =
                                intent.getBundleExtra("Bundle")?.getString("time3", "07:00 PM")
                            binding.timeThreeTab.text =
                                intent.getBundleExtra("Bundle")?.getString("tablet3", "1 tab")
                            binding.medicineTimeFour.visibility = View.GONE
                            medicineTimeTable =
                                binding.timeOne.text.toString() + " -> " + binding.timeOneTab.text.toString() + "\n" + binding.timeTwo.text.toString() + " -> " + binding.timeTwoTab.text.toString() + "\n" + binding.timeThree.text.toString() + " -> " + binding.timeThreeTab.text.toString()
                        }
                        "Four Times" -> {
                            binding.medicineTimeOne.visibility = View.VISIBLE
                            binding.medicineTimeTwo.visibility = View.VISIBLE
                            binding.medicineTimeThree.visibility = View.VISIBLE
                            binding.medicineTimeFour.visibility = View.VISIBLE
                            binding.timeOne.text =
                                intent.getBundleExtra("Bundle")?.getString("time1", "07:00 AM")
                            binding.timeOneTab.text =
                                intent.getBundleExtra("Bundle")?.getString("tablet1", "1 tab")
                            binding.timeTwo.text =
                                intent.getBundleExtra("Bundle")?.getString("time2", "01:00 PM")
                            binding.timeTwoTab.text =
                                intent.getBundleExtra("Bundle")?.getString("tablet2", "1 tab")
                            binding.timeThree.text =
                                intent.getBundleExtra("Bundle")?.getString("time3", "07:00 PM")
                            binding.timeThreeTab.text =
                                intent.getBundleExtra("Bundle")?.getString("tablet3", "1 tab")
                            binding.timeFour.text =
                                intent.getBundleExtra("Bundle")?.getString("time4", "10:00 PM")
                            binding.timeFourTab.text =
                                intent.getBundleExtra("Bundle")?.getString("tablet4", "1 tab")
                            medicineTimeTable =
                                binding.timeOne.text.toString() + " -> " + binding.timeOneTab.text.toString() + "\n" + binding.timeTwo.text.toString() + " -> " + binding.timeTwoTab.text.toString() + "\n" + binding.timeThree.text.toString() + " -> " + binding.timeThreeTab.text.toString() + "\n" + binding.timeFour.text.toString() + " -> " + binding.timeFourTab.text.toString()
                        }
                    }
                    binding.scheduleTimingStart.visibility = View.VISIBLE
                    binding.starttime.visibility = View.VISIBLE
                    binding.starttime.text = intent.getBundleExtra("Bundle")
                        ?.getString("durationstart", "No Specific Date")
                    binding.scheduleTimingEnd.visibility = View.VISIBLE
                    binding.endtime.visibility = View.VISIBLE
                    binding.endtime.text = intent.getBundleExtra("Bundle")
                        ?.getString("durationend", "No Specific Date")
                    medicineSchedule =
                        binding.scheduleTxt.text.toString() + "\n" + medicineTimeTable + "\n" + binding.starttime.text + "\n" + binding.endtime.text
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val name = "Notify Channel"
        val name1 = "Notify Channel"
        val name2 = "Notify Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(Channel1ID,name,importance)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val channel2 = NotificationChannel(Channel2ID,name1,importance)
        channel2.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel2.description = desc
        channel2.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel2)

        val channel3 = NotificationChannel(Channel3ID,name2,importance)
        channel3.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel3.description = desc
        channel3.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel3)

    }
    private fun addMedicineNotification(title : String,message: String){
        val intent = Intent(applicationContext,AlarmReceiver::class.java)
        intent.putExtra("titleExtra",title)
        intent.putExtra("messageExtra",message)
        val id = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, id,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,time,pendingIntent
        )
    }
    private fun scheduleNotification1(title: String,time : String,medicineTime : Date){
        val intent = Intent(applicationContext,AlarmReceiver::class.java)
        intent.putExtra("titleExtra",title)
        intent.putExtra("messageExtra","It's $time take your medicine")
        val id = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, id,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,medicineTime.time,pendingIntent
        )
        Log.d("RAHUL",medicineTime.toString())
    }
    private fun scheduleNotification2(title: String,time : String,medicineTime : Date){
        val intent = Intent(applicationContext,AlarmReceiver::class.java)
        intent.putExtra("titleExtra",title)
        intent.putExtra("messageExtra","It's $time take your medicine")
        val id = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, id,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,medicineTime.time,pendingIntent
        )
        Log.d("RAHUL1",medicineTime.toString())
    }

    private fun getTime() : Long{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR,Calendar.MONTH,Calendar.DAY_OF_MONTH,Calendar.HOUR,Calendar.MINUTE)
        return calendar.timeInMillis
    }

}