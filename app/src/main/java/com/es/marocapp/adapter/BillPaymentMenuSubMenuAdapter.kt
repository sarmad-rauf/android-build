package com.es.marocapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.es.marocapp.R
import com.es.marocapp.model.billpaymentmodel.BillPaymentSubMenuModel
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder

class BillPaymentMenuSubMenuAdapter(
    groups: List<ExpandableGroup<*>?>,
    var mListner: ContactlessItemsListner,
    context: Context
) :
    ExpandableRecyclerViewAdapter<BillPaymentMenuSubMenuAdapter.BillPaymentMenuItemViewHolder, BillPaymentMenuSubMenuAdapter.BillPaymentSubMenuItemViewHolder>(
        groups
    ) {
    var mList: List<*> = groups
    var mContext: Context = context

    override fun onCreateGroupViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BillPaymentMenuItemViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view: View =
            inflater.inflate(R.layout.bill_payment_menu_row_layout, parent, false)
        return BillPaymentMenuItemViewHolder(view)
    }

    override fun onCreateChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BillPaymentSubMenuItemViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view: View =
            inflater.inflate(R.layout.bill_payment_sub_menu_row_layout, parent, false)
        return BillPaymentSubMenuItemViewHolder(view)
    }

    override fun onBindChildViewHolder(
        holder: BillPaymentSubMenuItemViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>,
        childIndex: Int
    ) {
        val artist: BillPaymentSubMenuModel = group.items[childIndex] as BillPaymentSubMenuModel
//        holder.setSubItemText(artist.subItemName)
        /*when (flatPosition) {
            2 -> holder.subItemLeftIcon.setImageDrawable(mContext.getDrawable(R.drawable.epi_add_credit_debit))
            3 -> holder.subItemLeftIcon.setImageDrawable(mContext.getDrawable(R.drawable.epi_add_loyalty))
            else -> holder.subItemLeftIcon.setImageDrawable(mContext.getDrawable(R.drawable.epi_icon))
        }*/
    }

    override fun onBindGroupViewHolder(
        holder: BillPaymentMenuItemViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>
    ) {
        holder.setItemTitle(group)
        if (expandableList.groups[flatPosition].items.isEmpty()) {
            holder.listState.setImageResource(R.drawable.list_open_icon)
        } else {
            holder.listState.setImageResource(R.drawable.list_closed_icon)
        }
        when (flatPosition) {
            /*0, 1 -> holder.leftIcon.setImageDrawable(mContext.getDrawable(R.drawable.epi_add_btn))
            2 -> holder.leftIcon.setImageDrawable(mContext.getDrawable(R.drawable.epi_view_bank))
            3 -> holder.leftIcon.setImageDrawable(mContext.getDrawable(R.drawable.epi_view_card))
            else -> holder.leftIcon.setImageDrawable(mContext.getDrawable(R.drawable.epi_icon))*/
        }
    }

    inner class BillPaymentMenuItemViewHolder(itemView: View) :
        GroupViewHolder(itemView) {
        private val companyName: TextView = itemView.findViewById(R.id.tv_company_name)
        val companyIcon: ImageView = itemView.findViewById(R.id.img_company_icon)
        val listState: ImageView  = itemView.findViewById(R.id.img_list_state)
        val parentLayout: ConstraintLayout = itemView.findViewById(R.id.menu_layout)


        fun setItemTitle(group: ExpandableGroup<*>) {
            companyName.text = group.title
        }

        init {
            parentLayout.setOnClickListener {

            }
        }
    }

    inner class BillPaymentSubMenuItemViewHolder(itemView: View) :
        ChildViewHolder(itemView) {
        private val subCompanyName: TextView = itemView.findViewById(R.id.tv_sub_company_name)
        val subCompanyIcon: ImageView = itemView.findViewById(R.id.img_sub_company_icon)
        val childLayout: ConstraintLayout = itemView.findViewById(R.id.child_layout)

        fun setSubItemText(subItemText: String?) {
            subCompanyName.text = subItemText
        }

        init {
            childLayout.setOnClickListener {
            }
        }
    }

    interface ContactlessItemsListner {
        fun onSubItemSelected(scanFullReceipt: Boolean?)
        fun onItemSelected(itemSelected: String?)
    }

}