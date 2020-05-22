package com.es.marocapp.usecase.payments.billpayment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.adapter.PaymentItemsCustomImageAdapter
import com.es.marocapp.databinding.FragmentCompanyTypeBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.payments.PaymentsActivity
import com.es.marocapp.usecase.payments.PaymentsViewModel

/**
 * A simple [Fragment] subclass.
 */
class CompanyTypeFragment  : BaseFragment<FragmentCompanyTypeBinding>() {

    lateinit var mActivityViewModel: PaymentsViewModel
    private lateinit var mPaymentItemTypeAdapter: PaymentItemsAdapter
    private var mPaymentTypes: ArrayList<String>  = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_company_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(PaymentsViewModel::class.java)
        mDataBinding.apply {
        }

        (activity as PaymentsActivity).setCompanyIconToolbarVisibility(false)
        (activity as PaymentsActivity).setToolabarVisibility(true)

        mPaymentTypes.apply {
            add("Company A")
            add("Company B")
            add("Company C")
        }

        mPaymentItemTypeAdapter = PaymentItemsAdapter(mPaymentTypes, object : PaymentItemsAdapter.PaymentItemTypeClickListner{
            override fun onPaymentItemTypeClick() {
                (activity as PaymentsActivity).navController.navigate(R.id.action_companyTypeFragment_to_enterContactFragment)
            }

        })
        mDataBinding.companyTypeRecycler.apply {
            adapter = mPaymentItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as PaymentsActivity)
        }

    }


}
