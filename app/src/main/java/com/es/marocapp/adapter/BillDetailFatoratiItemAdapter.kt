package com.es.marocapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.FatoratiCustomParamModel
import com.es.marocapp.utils.Constants
import java.util.ArrayList

class BillDetailFatoratiItemAdapter(private val bills : ArrayList<FatoratiCustomParamModel>) : RecyclerView.Adapter<BillDetailFatoratiItemAdapter.BillPaymentItemViewHolder>() {

    override fun getItemCount() = bills.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillPaymentItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bill_payment_detail_row_layout, parent, false)
        return BillPaymentItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillPaymentItemViewHolder, position: Int) {
//        holder.billDueDateTitle.text = LanguageData.getStringValue("Description")
        holder.customerNameTitle.text = LanguageData.getStringValue("CustomerName")//-------------------------> Changed From Description TO Reference Number
        holder.billDueDateTitle.text = LanguageData.getStringValue("ReferenceNumber")//-------------------------> Changed From Description TO Reference Number
        holder.billingMonthTitle.text = LanguageData.getStringValue("Amount")
        holder.billingAmountTitle.text = LanguageData.getStringValue("Address")
        holder.billStatusTitle.text = LanguageData.getStringValue("Status")
//        holder.billingAmountTitle.text = LanguageData.getStringValue("Amount")
        holder.billStatusVal.text = LanguageData.getStringValue("Unpaid")

        holder.isBillSelected.isChecked = bills[position].isItemSelected
//        var date = Constants.parseDateFromString(bills[position].month)
//        holder.billDueDateVal.text = date
//        holder.billingMonthVal.text = Constants.getMonthFromParsedDate(date)

//        holder.billingAmountVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+bills[position].prixTTC


        holder.billingMonthVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + bills[position].prixTTC
//        holder.billDueDateVal.text = bills[position].description
        holder.billDueDateVal.text = bills[position].idArticle //-------------------------> Changed From Address TO bill Number

        holder.isBillSelected.setOnClickListener{
            if ((it as CompoundButton).isChecked) {
                bills[position].isItemSelected = true
                notifyDataSetChanged()
            } else {
                bills[position].isItemSelected = false
                notifyDataSetChanged()
            }
        }

        /*holder.billingAmountTitle.visibility = View.GONE
        holder.billingAmountVal.visibility = View.GONE*/


        holder.billingAmountVal.text = getAddressFromString(bills[position].description)
        holder.customerNameVal.text = getNameFromString(bills[position].description)
    }


    fun getAddressFromString(description: String): String{

        //"description":"NOM: Mohammed TEMSAMANI - ADRESSE:99000, Av., Hassan II, - DATE : 20170522"
        // Name - Address - Date

        var withoutNameString = description.substringAfter("-") //ADRESSE:99000, Av., Hassan II, - DATE : 20170522
        var withoutDateString = withoutNameString.substringBefore("-") //ADRESSE:99000, Av., Hassan II,
        var withoutAddressCollen = withoutDateString.substringAfter(":").removeSuffix(",").trim()
        return withoutAddressCollen
    }

    fun getNameFromString(description: String) : String{
        //"description":"NOM: Mohammed TEMSAMANI - ADRESSE:99000, Av., Hassan II, - DATE : 20170522"
        // Name - Address - Date

        var withoutAdressAndDateString = description.substringBefore("-")
        var withoutCollenName = withoutAdressAndDateString.substringAfter(":").trim()
        return withoutCollenName
    }

    class BillPaymentItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var billDueDateTitle : TextView = view.findViewById(R.id.dueDateTitle)
        var billDueDateVal : TextView = view.findViewById(R.id.dueDateVal)
        var billingMonthTitle : TextView = view.findViewById(R.id.billingMonthTitle)
        var billingMonthVal : TextView = view.findViewById(R.id.billingMonthVal)
        var billStatusTitle : TextView = view.findViewById(R.id.billStatusTitle)
        var billStatusVal : TextView = view.findViewById(R.id.billStatusVal)
        var billingAmountTitle : TextView = view.findViewById(R.id.billingAmountTitle)
        var billingAmountVal : TextView = view.findViewById(R.id.billingAmountVal)

        var customerNameTitle : TextView = view.findViewById(R.id.customerNameTitle)
        var customerNameVal : TextView = view.findViewById(R.id.customerNameVal)

        var isBillSelected : CheckBox = view.findViewById(R.id.isBillSelectedCheckBox)
    }

    fun getUpdateList(): ArrayList<FatoratiCustomParamModel> {
        var selectedBills : ArrayList<FatoratiCustomParamModel> = arrayListOf()
        for(i in bills.indices){
            if(bills[i].isItemSelected){
                selectedBills.add(bills[i])
            }
        }
        return selectedBills
    }

}