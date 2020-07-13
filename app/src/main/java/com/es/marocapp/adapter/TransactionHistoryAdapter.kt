package com.es.marocapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.model.CustomModelHistoryItem
import com.es.marocapp.utils.Constants

class TransactionHistoryAdapter(
    models: ArrayList<CustomModelHistoryItem>?,
    mListner: HistoryDetailListner
) :
    RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {
    private var models: ArrayList<CustomModelHistoryItem>?
    var context: Context? = null
    var listner: HistoryDetailListner

    fun updateList(arrayList: ArrayList<CustomModelHistoryItem>?) {
        models = arrayList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        val convertView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.statment_history_row_view, parent, false)
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        if (getItemViewType(position) == typeHeader) {
            /**
             * UI Binding for Header
             */
            holder.headerTextViewLabel!!.visibility = View.VISIBLE
            if (models!![position].date.isEmpty()) {
                holder.headerTextViewLabel!!.text = " "
            } else {
                holder.headerTextViewLabel!!.text = models!![position].date
            }
            holder.dataContainer!!.visibility = View.GONE
        } else {
            /**
             * UI Binding for Data
             */
            holder.headerTextViewLabel!!.visibility = View.GONE
            holder.dataContainer!!.visibility = View.VISIBLE

            holder.tvBillType?.text = models!![position].historyList.transfertype
            holder.tvCompanyName?.text = models!![position].historyList.toname
            holder.tvBillDate?.text = models!![position].date

            val sName: String = Constants.balanceInfoAndResponse.firstname + " " + Constants.balanceInfoAndResponse.surname
            if(sName.equals(models!![position].historyList.toname)){
                holder.tvBillAmount?.text = "+"+Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+models!![position].historyList.fromamount
                holder.tvBillAmount?.setTextColor(Color.parseColor("#008000"))
            }else{
                holder.tvBillAmount?.text = "-"+Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+models!![position].historyList.fromamount
                holder.tvBillAmount?.setTextColor(Color.parseColor("#ff0000"))
            }

            when(models!![position].historyList.transfertype){
                "PAYMENT"-> holder.transferTypeIcon?.setImageResource(R.drawable.others)
                "EXTERNAL_PAYMENT"-> holder.transferTypeIcon?.setImageResource(R.drawable.others)
                "CASH_IN"-> holder.transferTypeIcon?.setImageResource(R.drawable.ic_withdraw)
                "WITHDRAW"-> holder.transferTypeIcon?.setImageResource(R.drawable.ic_withdraw)
                "DEPOSIT"-> holder.transferTypeIcon?.setImageResource(R.drawable.ic_deposit)
                "CASH_OUT"-> holder.transferTypeIcon?.setImageResource(R.drawable.ic_deposit)
                "TRANSFER"-> holder.transferTypeIcon?.setImageResource(R.drawable.ic_favorite_transfers)
                "FLOAT_TRANSFER"-> holder.transferTypeIcon?.setImageResource(R.drawable.ic_favorite_transfers)
                else-> holder.transferTypeIcon?.setImageResource(R.drawable.others)
            }

            holder.dataContainer!!.setOnClickListener {
                listner.onHistoryDetailClickListner(models!![position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (models != null && models!!.size > 0) {
            models!![position].typeOfData
        } else {
            0
        }
    }

    fun updateHistoryList(newList : ArrayList<CustomModelHistoryItem>){
        models?.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return models!!.size
    }

    inner class ViewHolder(convertView: View) :
        RecyclerView.ViewHolder(convertView) {
        var headerTextViewLabel: TextView? = null
        var dataContainer: CardView? = null
        var tvBillType: TextView? = null
        var tvCompanyName: TextView? = null
        var tvBillDate: TextView? = null
        var tvBillAmount: TextView? = null
        var transferTypeIcon: ImageView? = null

        init {
            // Lookup view for data population
            headerTextViewLabel = convertView.findViewById(R.id.headerTextViewLabel)
            dataContainer = convertView.findViewById(R.id.dataContainer)
            tvBillType = convertView.findViewById(R.id.row_bill_type)
            tvCompanyName = convertView.findViewById(R.id.row_company_name)
            tvBillDate = convertView.findViewById(R.id.row_bill_date)
            tvBillAmount = convertView.findViewById(R.id.row_bill_amount)
            transferTypeIcon = convertView.findViewById(R.id.row_company_icon)
        }
    }

    interface HistoryDetailListner {
        fun onHistoryDetailClickListner(customModelHistoryItem: CustomModelHistoryItem?)
    }

    companion object {
        private const val typeHeader = 0
    }

    init {
        this.models = models
        listner = mListner
    }
}
