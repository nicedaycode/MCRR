package com.example.mcrmedicinereminder.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mcrmedicinereminder.R
import com.example.mcrmedicinereminder.model.RestockMedicine

class RestockMedicineRecyclerviewAdapter(
    private val context: Context,
    private val onStockItemClick: OnStockItemClick
) :
    RecyclerView.Adapter<RestockMedicineRecyclerviewAdapter.ViewHolder>() {

    private val arrRestockMedicine = ArrayList<RestockMedicine>()

    class ViewHolder(itemView: View, private val onStockItemClick: OnStockItemClick) :
        RecyclerView.ViewHolder(itemView) {
        var medicine_name: TextView
        var medicine_stock: TextView

        init {
            medicine_name = itemView.findViewById(R.id.medicine_name)
            medicine_stock = itemView.findViewById(R.id.medicine_stock)
        }
        fun bind(info: RestockMedicine) {
            medicine_name.text = info.name
            medicine_stock.text = info.number.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.restock_medicine_recyclerview_item, parent, false),
            onStockItemClick
        )
    }

    override fun getItemCount(): Int {
        return arrRestockMedicine.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = arrRestockMedicine[position]
        holder.bind(pos)

        holder.itemView.setOnClickListener{
            val medicineName = pos.name
            val dialog = Dialog(it.context)
            dialog.setContentView(R.layout.stock_adjustment)
            val name_of_medicine = dialog.findViewById(R.id.medicine_name) as TextView
            val stockQty = dialog.findViewById(R.id.stock_qty_edt) as EditText
            val restock = dialog.findViewById(R.id.set_stock) as Button
            name_of_medicine.text = medicineName
            restock.setOnClickListener {
                val qty = stockQty.text.toString().toInt()
                onStockItemClick.updateStockMedicine(medicineName,qty)
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    fun updateStockList(restockMedicine: List<RestockMedicine>) {
        arrRestockMedicine.clear()
        arrRestockMedicine.addAll(restockMedicine)
        notifyDataSetChanged()
    }

    interface OnStockItemClick {
        fun updateStockMedicine(medicineName: String, medicineStock: Int)
    }
}