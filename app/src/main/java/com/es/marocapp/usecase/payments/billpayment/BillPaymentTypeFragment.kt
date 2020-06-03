package com.es.marocapp.usecase.payments.billpayment


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.es.marocapp.R
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.databinding.FragmentBillPaymentTypeBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.payments.PaymentsActivity
import com.es.marocapp.usecase.payments.PaymentsViewModel

/**
 * A simple [Fragment] subclass.
 */
class BillPaymentTypeFragment : BaseFragment<FragmentBillPaymentTypeBinding>() {

    lateinit var mActivityViewModel: PaymentsViewModel
    private lateinit var mPaymentItemTypeAdapter: PaymentItemsAdapter
    private var mPaymentTypes: ArrayList<String> = ArrayList()
    private var mPaymentTypesIcons: ArrayList<Int> = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as PaymentsActivity).get(PaymentsViewModel::class.java)
        mDataBinding.apply {
        }

        (activity as PaymentsActivity).setCompanyIconToolbarVisibility(false)
        (activity as PaymentsActivity).setToolabarVisibility(true)

        mPaymentTypes.apply {
            add("Bill")
            add("Invoice")
            add("Merchant")
            add("Ticket")
        }

        mPaymentTypesIcons.apply {
            add(R.drawable.ic_favorite_payments)
            add(R.drawable.ic_favorite_transfers)
            add(R.drawable.ic_favorite_payments)
            add(R.drawable.ic_favorite_transfers)
        }

        mPaymentItemTypeAdapter = PaymentItemsAdapter(mPaymentTypes, mPaymentTypesIcons,object : PaymentItemsAdapter.PaymentItemTypeClickListner{
            override fun onPaymentItemTypeClick(paymentItems: String) {
                (activity as PaymentsActivity).navController.navigate(R.id.action_billPaymentTypeFragment_to_billTypeFragment)
            }

        })
        mDataBinding.paymentTypeRecycler.apply {
            adapter = mPaymentItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as PaymentsActivity)
        }

    }


}
