package com.es.marocapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.BillPaymentFatoratiResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.utils.Constants
import java.util.ArrayList

class FatoratiPaidBillAdapter(private val billStatus: BillPaymentFatoratiResponse, val listOfSelectedBillAmount: ArrayList<String>,
                              val listOfSelectedBillFee: String,
                              val receiverNumber: String
) : RecyclerView.Adapter<FatoratiPaidBillAdapter.BillPaymentItemViewHolder>() {

    override fun getItemCount() = listOfSelectedBillAmount.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillPaymentItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bill_paid_item_layout, parent, false)
        return BillPaymentItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillPaymentItemViewHolder, position: Int) {
        holder.tvSourceTitle.text = LanguageData.getStringValue("Source")
        holder.tvReceiverNameTitle.visibility = View.GONE
        holder.tvReceiverNumberTitle.text = LanguageData.getStringValue("ReceiverNumber")
        holder.tvAmountTitle.text = LanguageData.getStringValue("Amount")
        holder.tvFeeTitle.text = LanguageData.getStringValue("Fee")
        holder.tvTotalCostTitle.text = LanguageData.getStringValue("TotalCost")

        holder.tvSourceVal.text = LanguageData.getStringValue("Wallet")
        holder.tvReceiverNameVal.visibility = View.GONE
        holder.tvReceiverNumberVal.text = receiverNumber
        holder.tvAmountVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+listOfSelectedBillAmount[position]
        holder.tvFeeVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+listOfSelectedBillFee
        holder.tvTotalCostVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+" "+Constants.addAmountAndFee(listOfSelectedBillAmount[position].toDouble(), listOfSelectedBillFee.toDouble())



            if(billStatus.responseCode.equals(ApiConstant.API_SUCCESS)){
                holder.titleBillStatus.text = LanguageData.getStringValue("Successful")
                holder.imgBillStatus.setImageResource(R.drawable.ic_payment_successfull)
                holder.titleBillStatus.setTextColor(R.color.colorSuccess)

            }else if(billStatus.responseCode.equals(ApiConstant.API_FAILURE)){
                holder.titleBillStatus.text = LanguageData.getStringValue("Failed")
                holder.imgBillStatus.setImageResource(R.drawable.ic_payment_failed)
                holder.titleBillStatus.setTextColor(R.color.colorFail)

            }else if(billStatus.responseCode.equals(ApiConstant.API_PENDING)){
                holder.titleBillStatus.text = LanguageData.getStringValue("Pending")
                holder.imgBillStatus.setImageResource(R.drawable.ic_payment_pending)
                holder.titleBillStatus.setTextColor(R.color.colorPending)
            }


    }


    class BillPaymentItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        //        tvOwnerNameTitle  == SourceTitle
//        tvOwnerNameVal == SourceValue
//        tvContactNumTitle == ReceiverNameTitle
//        tvContactNumVal == ReceiverNameVal
//        tvReceiverNumberTitle == ReceiverNUmberTitl
//        tvReceiverNumberVal == ReceiverNumberVal
//
//        tvOwnerNameTitle2== AmountTitle
//        tvOwnerNameVal2 = Amount Value
//
//        tvContactNumTitle2 == Fee Title
//        tvContactNumVal2 == FeeValue
//
//        tvDHTitle == TotalCostTitle
//        tvDHVal == TotalCostValue
//
//        titleBillStatus== TitleBillStatus
//        imgBillStatus == img according to bill
        var tvSourceTitle : TextView = view.findViewById(R.id.tvOwnerNameTitle)
        var tvSourceVal : TextView = view.findViewById(R.id.tvOwnerNameVal)
        var tvReceiverNameTitle : TextView = view.findViewById(R.id.tvContactNumTitle)
        var tvReceiverNameVal : TextView = view.findViewById(R.id.tvContactNumVal)
        var tvReceiverNumberTitle : TextView = view.findViewById(R.id.tvReceiverNumberTitle)
        var tvReceiverNumberVal : TextView = view.findViewById(R.id.tvReceiverNumberVal)

        var tvAmountTitle : TextView = view.findViewById(R.id.tvOwnerNameTitle2)
        var tvAmountVal : TextView = view.findViewById(R.id.tvOwnerNameVal2)
        var tvFeeTitle : TextView = view.findViewById(R.id.tvContactNumTitle2)
        var tvFeeVal : TextView = view.findViewById(R.id.tvContactNumVal2)

        var tvTotalCostTitle : TextView = view.findViewById(R.id.tvDHTitle)
        var tvTotalCostVal : TextView = view.findViewById(R.id.tvDHVal)

        var titleBillStatus : TextView = view.findViewById(R.id.titleBillStatus)
        var imgBillStatus : ImageView = view.findViewById(R.id.imgBillStatus)

    }

}