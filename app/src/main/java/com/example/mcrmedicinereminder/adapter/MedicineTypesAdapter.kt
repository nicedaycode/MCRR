package com.example.mcrmedicinereminder.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mcrmedicinereminder.R
import com.example.mcrmedicinereminder.model.MedicineTypes

class MedicineTypesAdapter(
    private val context: Context,
    private val arrMedicineTypes: ArrayList<MedicineTypes>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MedicineTypesAdapter.ViewHolder>() {
    private var rmPosition: Int? = 1
    private var last: Int? = null

    class ViewHolder(itemView: View, onItemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        var item_txt: TextView
        var item_image: ImageView
        var item_bg: LinearLayout

        init {
            item_txt = itemView.findViewById(R.id.item_txt)
            item_image = itemView.findViewById(R.id.item_image)
            item_bg = itemView.findViewById(R.id.item_bg)
            itemView.setOnClickListener {
//                it.item_bg.setBackgroundResource(R.drawable.selected_medicine_bg)
                val medicineName = item_txt.text.toString()
                Log.d("RAHUL",adapterPosition.toString())
                onItemClickListener.updateBackground(adapterPosition)
            }
        }

        fun bind(info: MedicineTypes) {
            item_txt.text = info.name
            item_image.setImageResource(info.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.medicine_types, parent, false),
            onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (rmPosition == position) {
            holder.item_bg.setBackgroundResource(R.drawable.selected_medicine_bg)
        }
        if (last == position ) {
            holder.item_bg.setBackgroundColor(Color.WHITE)
        }

        val pos = arrMedicineTypes[position]
        holder.bind(pos)
    }

    override fun getItemCount(): Int {
        return arrMedicineTypes.size
    }

    interface OnItemClickListener {
        fun updateBackground(position: Int)
    }

    fun changeUI(position: Int) {
        this.last = this.rmPosition
        this.rmPosition = position
        last?.let { notifyItemChanged(it) }
        notifyItemChanged(position)
    }
}