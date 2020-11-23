package com.es.marocapp.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.es.marocapp.R
import com.es.marocapp.model.billpaymentmodel.BillPaymentMenuModel
import com.es.marocapp.model.billpaymentmodel.BillPaymentSubMenuModel
import com.squareup.picasso.Picasso


class BillPaymentExpandableAdapter(
    context: Context, listDataHeader: ArrayList<BillPaymentMenuModel>,
    listChildData: HashMap<String, ArrayList<BillPaymentSubMenuModel>>?
) : BaseExpandableListAdapter() {
    private val _context: Context = context
    private val _listDataHeader // header titles
            : ArrayList<BillPaymentMenuModel> = listDataHeader

    // child data in format of header title, child title
    private val _listDataChild: HashMap<String, ArrayList<BillPaymentSubMenuModel>>? = listChildData

    override fun getChild(groupPosition: Int, childPosititon: Int): BillPaymentSubMenuModel? {
        return _listDataChild?.get(_listDataHeader[groupPosition].companyTilte)?.get(childPosititon)
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup?
    ): View? {
        var convertView: View? = convertView
        val childText = getChild(groupPosition, childPosition) as BillPaymentSubMenuModel
        if (convertView == null) {
            val infalInflater = _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.bill_payment_sub_menu_row_layout, null)
        }
        val childCompanyName = convertView?.findViewById(R.id.tv_sub_company_name) as TextView
        val childCompanyIcon = convertView?.findViewById(R.id.img_sub_company_icon) as ImageView

        childCompanyName.text = childText.subCompanyTitle
        //todo need to Set Image From URL
        if(childText.subCompanyIcon.isEmpty()){
            childCompanyIcon.setImageResource(R.drawable.default_no_company_icon)
        }else{
            Picasso.with(_context).load(childText.subCompanyIcon).placeholder(_context.resources.getDrawable(R.drawable.default_no_company_icon)).error(_context.resources.getDrawable(R.drawable.default_no_company_icon))
                .into(childCompanyIcon)
        }
//        childCompanyIcon.setImageResource(childText.subCompanyIcon)
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return _listDataChild?.get(_listDataHeader[groupPosition].companyTilte)!!.size
    }

    override fun getGroup(groupPosition: Int): BillPaymentMenuModel {
        return _listDataHeader[groupPosition]
    }

    override fun getGroupCount(): Int {
        return _listDataHeader.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup?
    ): View? {
        var convertView: View? = convertView
        val headerTitle = getGroup(groupPosition) as BillPaymentMenuModel
        if (convertView == null) {
            val infalInflater = _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.bill_payment_menu_row_layout, null)
        }
        val parentCompanyName = convertView?.findViewById(R.id.tv_company_name) as TextView
        val parentCompanyIcon = convertView?.findViewById(R.id.img_company_icon) as ImageView
        val groupState = convertView?.findViewById(R.id.img_list_state) as ImageView

        if (isExpanded) {
            groupState.setImageResource(R.drawable.list_open_icon)
        } else {
            groupState.setImageResource(R.drawable.list_closed_icon)
        }

        parentCompanyName.text = headerTitle.companyTilte
        parentCompanyIcon.setImageResource(headerTitle.companyIcon)

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}