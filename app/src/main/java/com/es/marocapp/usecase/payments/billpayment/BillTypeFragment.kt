package com.es.marocapp.usecase.payments.billpayment


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.es.marocapp.R
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.databinding.FragmentBillTypeBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.payments.PaymentsActivity
import com.es.marocapp.usecase.payments.PaymentsViewModel

/**
 * A simple [Fragment] subclass.
 */
class BillTypeFragment : BaseFragment<FragmentBillTypeBinding>() {

    lateinit var mActivityViewModel: PaymentsViewModel
    private lateinit var mPaymentItemTypeAdapter: PaymentItemsAdapter
    private var mBillTypes: ArrayList<String>  = ArrayList()
    private var mBillTypesIcons: ArrayList<Int>  = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_bill_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(PaymentsViewModel::class.java)
        mDataBinding.apply {
        }

        (activity as PaymentsActivity).setCompanyIconToolbarVisibility(false)
        (activity as PaymentsActivity).setToolabarVisibility(true)

        mBillTypes.apply {
            add("Electricity")
            add("Water")
            add("Internet")
            add("Others")
        }

        mBillTypesIcons.apply {
            add(R.drawable.ic_favorite_payments)
            add(R.drawable.ic_favorite_transfers)
            add(R.drawable.ic_favorite_payments)
            add(R.drawable.ic_favorite_transfers)
        }

        mPaymentItemTypeAdapter = PaymentItemsAdapter(mBillTypes, mBillTypesIcons ,object : PaymentItemsAdapter.PaymentItemTypeClickListner{
            override fun onPaymentItemTypeClick(paymentItems: String) {
                (activity as PaymentsActivity).navController.navigate(R.id.action_billTypeFragment_to_companyTypeFragment)
            }

        })
        mDataBinding.billTypeRecycler.apply {
            adapter = mPaymentItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as PaymentsActivity)
        }

    }


}