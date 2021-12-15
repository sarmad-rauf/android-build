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
import com.es.marocapp.model.responses.History
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.Logger
import kotlin.collections.ArrayList

class TransactionHistoryAdapter(
    models: ArrayList<History>?,
    mListner: HistoryDetailListner
) :
    RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {
    private var models: ArrayList<History>?
    var context: Context? = null
    var listner: HistoryDetailListner

    fun updateList(arrayList: ArrayList<History>?) {
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
        /* if (getItemViewType(position) == typeHeader) {
             */
        /**
         * UI Binding for Header
         *//*
            holder.headerTextViewLabel!!.visibility = View.VISIBLE
            if (models!![position].date.isEmpty()) {
                holder.headerTextViewLabel!!.text = " "
            } else {
                holder.headerTextViewLabel!!.text = models!![position].date
            }
            holder.dataContainer!!.visibility = View.GONE
        } else {*/
        /**
         * UI Binding for Data
         */
        holder.headerTextViewLabel!!.visibility = View.GONE
        holder.dataContainer!!.visibility = View.VISIBLE

        holder.tvBillType?.text = models!![position].transfertype
        holder.tvCompanyName?.text = models!![position].toname
        var dateToShow: String = Constants.getZoneFormattedDateAndTime(models!![position].date)
        holder.tvBillDate?.text = dateToShow

        val sName: String =
            Constants.balanceInfoAndResponse?.firstname + " " + Constants.balanceInfoAndResponse?.surname
        if (models!![position].transactionstatus.equals("FAILED", true)) {
            holder.tvBillAmount?.text =
                models!![position].toamount + " " + Constants.CURRENT_CURRENCY_TYPE_TO_SHOW
        } else {
            if (sName.equals(models!![position].toname)) {
                holder.tvBillAmount?.text =
                    "+" + models!![position].toamount + " " + Constants.CURRENT_CURRENCY_TYPE_TO_SHOW
                holder.tvBillAmount?.setTextColor(Color.parseColor("#008000"))
            } else {
                holder.tvBillAmount?.text =
                    "-" + models!![position].toamount + " " + Constants.CURRENT_CURRENCY_TYPE_TO_SHOW
                holder.tvBillAmount?.setTextColor(Color.parseColor("#ff0000"))
            }
        }

        when (models!![position].transferTypeEwp) {
            "TRANSFER" -> holder.transferTypeIcon?.setImageResource(R.drawable.transfer_argent)
            "INTEROP_TRANSFER" -> holder.transferTypeIcon?.setImageResource(R.drawable.transfer_argent)
            "ADJUSTMENT" -> holder.transferTypeIcon?.setImageResource(R.drawable.transfer_argent)
            "CUSTOM_CASHIN_VIA_CARD" -> holder.transferTypeIcon?.setImageResource(R.drawable.transfer_argent)
            "CASH_IN" -> holder.transferTypeIcon?.setImageResource(R.drawable.transfer_argent)
            "CASH_OUT" -> holder.transferTypeIcon?.setImageResource(R.drawable.transfer_argent)
            "WITHDRAWAL" -> holder.transferTypeIcon?.setImageResource(R.drawable.transfer_argent)
            "DEPOSIT" -> holder.transferTypeIcon?.setImageResource(R.drawable.transfer_argent)
            "INTEROP_PAYMENT_SEND" -> holder.transferTypeIcon?.setImageResource(R.drawable.paement_marchand)
            "PAYMENT" -> holder.transferTypeIcon?.setImageResource(R.drawable.paement_marchand)
            "EXTERNAL_PAYMENT" -> holder.transferTypeIcon?.setImageResource(R.drawable.paement_facture)
            else -> holder.transferTypeIcon?.setImageResource(R.drawable.circle_generic_transaction)
        }

        Logger.debugLog(
            "transactionHistory",
            "transation type ${models!![position].transferTypeEwp}"
        )
        when (models!![position].transactionstatus) {
            "SUCCESSFUL" -> holder.statusImg?.setImageResource(R.drawable.approvals_active)
            "PENDING" -> holder.statusImg?.setImageResource(R.drawable.ic_pending_svg)
            "FAILED" -> holder.statusImg?.setImageResource(R.drawable.failed_2)
            else -> holder.statusImg?.visibility = View.INVISIBLE
        }

        holder.dataContainer!!.setOnClickListener {
            listner.onHistoryDetailClickListner(models!![position])
        }
        /*}*/
    }


    /*override fun getItemViewType(position: Int): Int {
        return if (models != null && models!!.size > 0) {
            models!![position].typeOfData
        } else {
            0
        }
    }*/

    fun updateHistoryList(newList: List<History>) {
        models?.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateHistoryList() {
        models?.clear()
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
        var statusImg: ImageView? = null

        init {
            // Lookup view for data population
            headerTextViewLabel = convertView.findViewById(R.id.headerTextViewLabel)
            dataContainer = convertView.findViewById(R.id.dataContainer)
            tvBillType = convertView.findViewById(R.id.row_bill_type)
            tvCompanyName = convertView.findViewById(R.id.row_company_name)
            tvBillDate = convertView.findViewById(R.id.row_bill_date)
            tvBillAmount = convertView.findViewById(R.id.row_bill_amount)
            transferTypeIcon = convertView.findViewById(R.id.row_company_icon)
            statusImg = convertView.findViewById(R.id.statusImg)
        }
    }

    interface HistoryDetailListner {
        fun onHistoryDetailClickListner(customModelHistoryItem: History)
    }

    companion object {
        private const val typeHeader = 0
    }

    init {
        this.models = models
        listner = mListner
    }
}
