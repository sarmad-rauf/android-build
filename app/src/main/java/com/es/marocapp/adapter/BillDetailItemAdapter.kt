package com.es.marocapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.InvoiceCustomModel
import com.es.marocapp.utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class BillDetailItemAdapter(private val bills : ArrayList<InvoiceCustomModel>) : RecyclerView.Adapter<BillDetailItemAdapter.BillPaymentItemViewHolder>() {

    override fun getItemCount() = bills.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillPaymentItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bill_payment_detail_row_layout, parent, false)
        return BillPaymentItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillPaymentItemViewHolder, position: Int) {
        holder.billDueDateTitle.text = LanguageData.getStringValue("DueDate")
        holder.billingMonthTitle.text = LanguageData.getStringValue("BillingMonth")
        holder.billStatusTitle.text = LanguageData.getStringValue("Status")
        holder.billStatusVal.text = LanguageData.getStringValue("Unpaid")

        holder.isBillSelected.isChecked = bills[position].isBillSelected
        var date = Constants.parseDateFromString(bills[position].month)
        holder.billDueDateVal.text = date
        holder.billingMonthVal.text = Constants.getMonthFromParsedDate(date)

        holder.isBillSelected.setOnClickListener{
            if ((it as CompoundButton).isChecked) {
                bills[position].isBillSelected = true
                notifyDataSetChanged()
            } else {
                bills[position].isBillSelected = false
                notifyDataSetChanged()
            }
        }
    }


    class BillPaymentItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var billDueDateTitle : TextView = view.findViewById(R.id.dueDateTitle)
        var billDueDateVal : TextView = view.findViewById(R.id.dueDateVal)
        var billingMonthTitle : TextView = view.findViewById(R.id.billingMonthTitle)
        var billingMonthVal : TextView = view.findViewById(R.id.billingMonthVal)
        var billStatusTitle : TextView = view.findViewById(R.id.billStatusTitle)
        var billStatusVal : TextView = view.findViewById(R.id.billStatusVal)

        var isBillSelected : CheckBox = view.findViewById(R.id.isBillSelectedCheckBox)
    }

    fun getUpdateList(): ArrayList<InvoiceCustomModel> {
        return bills
    }

}