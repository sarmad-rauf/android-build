package com.es.marocapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.model.responses.Limits
import com.es.marocapp.utils.Constants

class AccountDetailLimitListAdapter(private val limits: ArrayList<Limits>) :
    RecyclerView.Adapter<AccountDetailLimitListAdapter.LimitsItemViewHolder>() {

    override fun getItemCount() = limits.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LimitsItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_limit_list_item, parent, false)
        return LimitsItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: LimitsItemViewHolder, position: Int) {
        holder.limitListTitle.text = limits[position].name
        holder.limitListValue.text =Constants.CURRENT_CURRENCY_TYPE_TO_SHOW +" "+ limits[position].threshhold

        if(position.equals(limits.size)){
            holder.limitListDivider.visibility = View.GONE
        }
    }

    class LimitsItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var limitListTitle: TextView = view.findViewById(R.id.tvYearlySendingLimitTitle)
        var limitListValue: TextView = view.findViewById(R.id.tvYearlySendingLimitVal)
        var limitListDivider: ImageView = view.findViewById(R.id.divider1)
    }


    interface ApprovalItemClickListner {
        fun onApprovalItemTypeClick()
    }
}