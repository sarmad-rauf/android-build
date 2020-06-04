package com.es.marocapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.es.marocapp.R
import com.es.marocapp.model.QuickAmountModel
import com.es.marocapp.utils.Constants

class QuickAmountAdapter(
    var mContext: Context,var maximumUserBalance: Int,
    private val mQuickAmountItems: ArrayList<QuickAmountModel>,
    var listner: QuickAmountAdpterListner
) : RecyclerView.Adapter<QuickAmountAdapter.QuickAmountItemViewHolder>() {

    override fun getItemCount() = mQuickAmountItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickAmountItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_quick_amount_item, parent, false)
        return QuickAmountItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuickAmountItemViewHolder, position: Int) {
        holder.quickAmountItemName.text = Constants.CURRENT_CURRENCY_TYPE+mQuickAmountItems[position].quickAmountVal

        if(mQuickAmountItems[position].isQuickAmountActive){
            holder.quickAmountItemName.background = mContext.resources.getDrawable(R.drawable.button_quick_recharge_enable)
            holder.quickAmountItemName.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite))
        }else{
            holder.quickAmountItemName.background = mContext.resources.getDrawable(R.drawable.button_quick_recharge_disable)
            holder.quickAmountItemName.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextGreyMedium))
        }

        holder.mQuickAmountItemLayout.setOnClickListener {
            if(mQuickAmountItems[position].quickAmountVal.toInt()<= maximumUserBalance){
                updateQuickAmountList(position)
                listner.onAmountItemTypeClick(mQuickAmountItems[position].quickAmountVal)
            }
        }
    }

    class QuickAmountItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var quickAmountItemName : TextView = view.findViewById(R.id.quickRechargeBtnOne)
        var mQuickAmountItemLayout : ConstraintLayout = view.findViewById(R.id.containerLayout)
    }

    fun updateQuickAmountList(position: Int){
        for(i in  0 until mQuickAmountItems.size){
            mQuickAmountItems[i].isQuickAmountActive = i==position
        }
        notifyDataSetChanged()
    }

    interface QuickAmountAdpterListner{
        fun onAmountItemTypeClick(amount: String)
    }
}