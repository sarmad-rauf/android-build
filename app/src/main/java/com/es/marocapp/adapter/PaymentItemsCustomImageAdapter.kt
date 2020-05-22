package com.es.marocapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R

class PaymentItemsCustomImageAdapter(private val paymentItems : ArrayList<String>, var listner : PaymentItemCustomTypeClickListner) : RecyclerView.Adapter<PaymentItemsCustomImageAdapter.PaymentItemViewHolder>() {

    override fun getItemCount() = paymentItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.payment_items_row_layout, parent, false)
        return PaymentItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentItemViewHolder, position: Int) {
        holder.paymentItem.text = paymentItems[position]

        var paymentItemName = paymentItems[position]
        var splited = paymentItemName.split("\\s+")
        var iconText = ""
        if(!splited.isNullOrEmpty()){
            for(item in splited){
                iconText += item.substring(0, 1)
            }
        }

        holder.customizeIconText.text = iconText

        holder.mPaymentItemLayout.setOnClickListener {
            listner.onPaymentItemTypeClick()
        }
    }

    class PaymentItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var paymentItem : TextView = view.findViewById(R.id.payment_type_name)
        var customizeIconText : TextView = view.findViewById(R.id.customizeIconText)
        var mPaymentItemLayout : ConstraintLayout = view.findViewById(R.id.containerLayout)
    }


    interface PaymentItemCustomTypeClickListner{
        fun onPaymentItemTypeClick()
    }
}
