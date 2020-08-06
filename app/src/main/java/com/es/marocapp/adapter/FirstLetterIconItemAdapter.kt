package com.es.marocapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.model.responses.Creancier

class FirstLetterIconItemAdapter(private val paymentItems : ArrayList<String>,
                                         var listner: PaymentItemTypeClickListner) : RecyclerView.Adapter<FirstLetterIconItemAdapter.FirstLetterItemViewHolder>() {

    var numberRegex = "^[0-9]*$".toRegex()

    override fun getItemCount() = paymentItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirstLetterItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_first_letter_icon_row, parent, false)
        return FirstLetterItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FirstLetterItemViewHolder, position: Int) {
        holder.paymentItem.text = paymentItems[position]
        var name = paymentItems[position]

        if(name.matches(numberRegex)){
            holder.paymentItemIcon.text = name
        }else{
            holder.paymentItemIcon.text = name[0].toString()
        }

        holder.mPaymentItemLayout.setOnClickListener {
            listner.onPaymentItemTypeClick(paymentItems[position])
        }
    }

    class FirstLetterItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var paymentItem : TextView = view.findViewById(R.id.payment_type_name)
        var paymentItemIcon : TextView = view.findViewById(R.id.img_Info)
        var mPaymentItemLayout : ConstraintLayout = view.findViewById(R.id.containerLayout)
    }


    interface PaymentItemTypeClickListner{
        fun onPaymentItemTypeClick(paymentItems: String)
    }
}