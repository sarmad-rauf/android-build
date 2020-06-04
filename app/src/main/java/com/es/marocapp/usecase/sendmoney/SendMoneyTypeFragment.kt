package com.es.marocapp.usecase.sendmoney

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.databinding.FragmentBillPaymentTypeBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.payments.PaymentsViewModel

class SendMoneyTypeFragment : BaseFragment<FragmentBillPaymentTypeBinding>() {

    lateinit var mActivityViewModel: SendMoneyViewModel
    private lateinit var mSendMoneyItemTypeAdapter: PaymentItemsAdapter
    private var mSendMoneyTypes: ArrayList<String> = ArrayList()
    private var mSendMoneyTypesIcon: ArrayList<Int>  = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
        }

        mDataBinding.tvPaymentType.text = activity!!.resources.getString(R.string.transfer_type)
        (activity as SendMoneyActivity).setHeaderTitle((activity as SendMoneyActivity).resources.getString(R.string.send_money))

        (activity as SendMoneyActivity).setHeaderVisibility(true)


        mSendMoneyTypes.apply {
            add("Funds Transfer")
            add("Push Bank")
            add("Initiate Purchase to Merchant")
        }

        mSendMoneyTypesIcon.apply {
            add(R.drawable.ic_favorite_transfers)
            add(R.drawable.push_bank)
            add(R.drawable.initiate_merchant)
        }

        mSendMoneyItemTypeAdapter = PaymentItemsAdapter(mSendMoneyTypes, mSendMoneyTypesIcon ,object : PaymentItemsAdapter.PaymentItemTypeClickListner{
            override fun onPaymentItemTypeClick(paymentItems: String) {
                when(paymentItems){
                    "Funds Transfer" -> {
                        mActivityViewModel.isFundTransferUseCase.set(true)
                        mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set(false)
                        mActivityViewModel.trasferTypeSelected.set("Funds Transfer")
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_sendMoneyTypeFragment_to_fundsTransferMsisdnFragment)
                    }
                    "Push Bank" -> {
                        mActivityViewModel.trasferTypeSelected.set("Push Bank")
                        Toast.makeText(activity,"Push Bank Clicked",Toast.LENGTH_SHORT).show()
                    }
                    "Initiate Purchase to Merchant" ->{
                        mActivityViewModel.isFundTransferUseCase.set(false)
                        mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set(true)
                        mActivityViewModel.trasferTypeSelected.set("Initiate Purchase to Merchant")
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_sendMoneyTypeFragment_to_fundsTransferMsisdnFragment)
                    }
                    else -> Toast.makeText(activity,"Nothing Clicked Clicked",Toast.LENGTH_SHORT).show()

                }
            }

        })
        mDataBinding.paymentTypeRecycler.apply {
            adapter = mSendMoneyItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as SendMoneyActivity)
        }

    }

}