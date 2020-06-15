package com.es.marocapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R

class CustomizeIconsAdapter(private val airTimeItem : ArrayList<String>,
                            var listner : CustomizeItemClickListner) : RecyclerView.Adapter<CustomizeIconsAdapter.CustomIconItemViewHolder>() {

    override fun getItemCount() = airTimeItem.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomIconItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.payment_items_row_layout, parent, false)
        return CustomIconItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomIconItemViewHolder, position: Int) {
        holder.paymentItem.text = airTimeItem[position]


        holder.mPaymentItemLayout.setOnClickListener {
            listner.onCustomizeItemTypeClick(airTimeItem[position])
        }
    }

    fun updateList(newList : ArrayList<String>){
        airTimeItem.addAll(newList)
        notifyDataSetChanged()
    }

    class CustomIconItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var paymentItem : TextView = view.findViewById(R.id.payment_type_name)
        var paymentItemIcon : ImageView = view.findViewById(R.id.img_Info)
        var mPaymentItemLayout : ConstraintLayout = view.findViewById(R.id.containerLayout)
    }


    interface CustomizeItemClickListner{
        fun onCustomizeItemTypeClick(paymentItems: String)
    }
}