package com.example.mcrmedicinereminder.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mcrmedicinereminder.R
import com.example.mcrmedicinereminder.data.MedicineReminder
import com.example.mcrmedicinereminder.Constants

class MedicineRecyclerviewAdapter(
    private val context: Context,
    private val onClickItem: OnClickItem
) :
    RecyclerView.Adapter<MedicineRecyclerviewAdapter.ViewHolder>() {

    var arrMedicineReminder = ArrayList<MedicineReminder>()

    class ViewHolder(itemView: View, private val onClickItem: OnClickItem) :
        RecyclerView.ViewHolder(itemView) {

        var medicine_check: LinearLayout
        var enable_btn: ImageView
        var disable_btn: ImageView
        var medicine_image: ImageView
        var medicine_name: TextView
        var medicine_qnty: TextView
        var medicine_instruction: TextView
        var stock_size: TextView
        var time: TextView

        init {
            medicine_check = itemView.findViewById(R.id.medicine_check)
            enable_btn = itemView.findViewById(R.id.enable_btn)
            disable_btn = itemView.findViewById(R.id.disable_btn)
            medicine_image = itemView.findViewById(R.id.medicine_image)
            medicine_name = itemView.findViewById(R.id.medicine_name)
            medicine_qnty = itemView.findViewById(R.id.medicine_qnty)
            medicine_instruction = itemView.findViewById(R.id.medicine_instruction)
            stock_size = itemView.findViewById(R.id.stock_size)
            time = itemView.findViewById(R.id.time)
        }

        fun bind(info: MedicineReminder) {
            medicine_image.setImageResource(Constants.getMedicineType()[info.medicineImage].image)
            medicine_name.text = info.medicineName
            medicine_qnty.text = info.medicineQnty
            medicine_instruction.text = info.medicineInstruction
            stock_size.text = info.stockSize.toString()
            time.text = info.medicineTime

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.medicine_reminder_recyclerview_item, parent, false), onClickItem
        )
    }

    override fun getItemCount(): Int {
        return arrMedicineReminder.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = arrMedicineReminder[position]
        holder.bind(pos)
        if (pos.medicineCheck) {
            holder.enable_btn.visibility = View.VISIBLE
            holder.disable_btn.visibility = View.GONE
        } else {
            holder.disable_btn.visibility = View.VISIBLE
            holder.enable_btn.visibility = View.GONE
        }
        holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure you want to delete this item ?")
            builder.apply {
                setPositiveButton("YES", DialogInterface.OnClickListener { dialog, id ->
                    onClickItem.deleteRow(arrMedicineReminder.get(position), position)

                })
            }
                .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
            builder.show()
            return@setOnLongClickListener true
        }

        holder.medicine_check.setOnClickListener {
            if (!pos.medicineCheck) {
                val default_medicine_name = pos.medicineName.toString()
                val stockSize = pos.stockSize - 1
                pos.medicineCheck = true
                onClickItem.updateMedicineCheck(pos, position)
                onClickItem.updateMedicineStatus(default_medicine_name, stockSize)
            }
        }
    }

    fun updateMedicineList(medicineReminder: List<MedicineReminder>) {
        arrMedicineReminder.clear()
        arrMedicineReminder.addAll(medicineReminder)
        notifyDataSetChanged()
    }

    interface OnClickItem {
        fun deleteRow(medicineReminder: MedicineReminder, position: Int)
        fun updateMedicineCheck(medicineReminder: MedicineReminder, position: Int)
        fun updateMedicineStatus(medicineName: String, medicineStock: Int)
    }

}