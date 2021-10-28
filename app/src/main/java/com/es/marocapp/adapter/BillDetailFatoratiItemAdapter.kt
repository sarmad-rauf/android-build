package com.es.marocapp.adapter

import android.view.Gravity
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

class BillDetailFatoratiItemAdapter(private val bills : ArrayList<FatoratiCustomParamModel>, val isLanguageEngOrFr : Boolean) : RecyclerView.Adapter<BillDetailFatoratiItemAdapter.BillPaymentItemViewHolder>() {

    override fun getItemCount() = bills.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillPaymentItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bill_payment_detail_row_layout, parent, false)
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = .inflate(inflater)
        return BillPaymentItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillPaymentItemViewHolder, position: Int) {
        val gravity = if(isLanguageEngOrFr) Gravity.START else Gravity.END
        holder.customerNameVal.gravity=gravity
        holder.billDueDateVal.gravity=gravity
        holder.billingMonthVal.gravity=gravity
        holder.billingAmountVal.gravity=gravity
//        holder.billDueDateTitle.text = LanguageData.getStringValue("Description")
        if(bills[position].showDescription)
        {
            //Show Auto Du Moroc Views
            holder.billingDescription.visibility=View.VISIBLE
            holder.customerNameTitle.visibility = View.GONE
            holder.customerNameVal.visibility = View.GONE
            holder.billDueDateTitle.visibility = View.GONE
            holder.billingAmountTitle.visibility =View.GONE
            holder.billingAmountVal.visibility =View.GONE
            holder.billStatusTitle.visibility = View.GONE
            holder.billStatusVal.visibility = View.GONE
            holder.billDueDateTitle.visibility = View.GONE
            holder.billDueDateVal.visibility = View.GONE
            holder.billStatusVal.visibility = View.GONE
            holder.billStatusVal.visibility = View.GONE
        }
        holder.customerNameTitle.text = LanguageData.getStringValue("CustomerName")//-------------------------> Changed From Description TO Reference Number
        holder.billDueDateTitle.text = LanguageData.getStringValue("ReferenceNumber")//-------------------------> Changed From Description TO Reference Number
        holder.billingMonthTitle.text = LanguageData.getStringValue("Amount")
        holder.billingAmountTitle.text = LanguageData.getStringValue("Address")
        holder.billStatusTitle.text = LanguageData.getStringValue("Status")
//        holder.billingAmountTitle.text = LanguageData.getStringValue("Amount")
        holder.billStatusVal.text = LanguageData.getStringValue("Unpaid")
        var description = (bills[position].description)
        description=description.replace("<br/>","\n")
        holder.billingDescription.text = description

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

        var address = getAddressFromString(bills[position].description)
        address=address.replace("<br/>","\n")
        var custommeerName = getNameFromString(bills[position].description)
        custommeerName=custommeerName.replace("<br/>","\n")
        holder.billingAmountVal.text = address
        holder.customerNameVal.text =custommeerName
        getDateFromString(bills[position].description)
    }



    fun getAddressFromString(description: String): String{

        //"description":"NOM: Mohammed TEMSAMANI - ADRESSE:99000, Av., Hassan II, - DATE : 20170522"
        // Name - Address - Date
        return if(description.isNullOrEmpty()){
            "-"
        }else{
            var withoutNameString = description.substringAfter("-") //ADRESSE:99000, Av., Hassan II, - DATE : 20170522
            var withoutDateString = withoutNameString.substringBefore("-") //ADRESSE:99000, Av., Hassan II,
            var withoutAddressCollen = withoutDateString.substringAfter(":").removeSuffix(",").trim()
            withoutAddressCollen
        }
    }

    fun getNameFromString(description: String) : String{
        //"description":"NOM: Mohammed TEMSAMANI - ADRESSE:99000, Av., Hassan II, - DATE : 20170522" yyyyMMdd
        // Name - Address - Date
        return if(description.isNullOrEmpty()){
            "-"
        }else{
            val withoutAdressAndDateString = description.substringBefore("-")
            val withoutCollenName = withoutAdressAndDateString.substringAfter(":").trim()
            return withoutCollenName
        }
    }

    fun getDateFromString(description: String) : String{
        //"description":"NOM: Mohammed TEMSAMANI - ADRESSE:99000, Av., Hassan II, - DATE : 20170522" yyyyMMdd
        // Name - Address - Date
        return if(description.isNullOrEmpty()){
            "-"
        }else{
            val withoutNameString = description.substringAfter("-")
            val withoutAddressNameString = withoutNameString.substringAfter("-")
            val withoutCollenDate  = withoutAddressNameString.substringAfter(":").trim()
//            Logger.debugLog("TestingDateAdapter",withoutCollenDate)
            val date = Constants.parseDateFromString(withoutCollenDate)
//            Logger.debugLog("TestingDateAdapterParsed",date.toString())
            return date
        }
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
        var billingDescription : TextView = view.findViewById(R.id.description)

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