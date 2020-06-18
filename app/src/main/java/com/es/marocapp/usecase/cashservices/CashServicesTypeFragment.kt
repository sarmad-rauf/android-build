package com.es.marocapp.usecase.cashservices

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.databinding.FragmentBillPaymentTypeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment
class CashServicesTypeFragment : BaseFragment<FragmentBillPaymentTypeBinding>() {

    lateinit var mActivityViewModel: CashServicesViewModel
    private lateinit var mCashServicesItemTypeAdapter: PaymentItemsAdapter
    private var mCashServicesTypes: ArrayList<String> = ArrayList()
    private var mCashServicesTypesIcon: ArrayList<Int>  = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as CashServicesActivity).get(CashServicesViewModel::class.java)
        mDataBinding.apply {
        }

        (activity as CashServicesActivity).setHeaderVisibility(true)

        mActivityViewModel.popBackStackTo = -1

        mCashServicesTypes.apply {
            add(LanguageData.getStringValue("Deposit").toString())
            add(LanguageData.getStringValue("Withdraw").toString())
        }

        mCashServicesTypesIcon.apply {
            add(R.drawable.ic_deposit)
            add(R.drawable.ic_withdraw)
        }

        mCashServicesItemTypeAdapter = PaymentItemsAdapter(mCashServicesTypes, mCashServicesTypesIcon ,object : PaymentItemsAdapter.PaymentItemTypeClickListner{
            override fun onPaymentItemTypeClick(paymentItems: String) {
                when(paymentItems){
                    LanguageData.getStringValue("Deposit").toString() -> {
                        mActivityViewModel.isDepositUseCase.set(true)
                        mActivityViewModel.isWithdrawUseCase.set(false)
                        mActivityViewModel.trasferTypeSelected.set(LanguageData.getStringValue("Deposit").toString())
                        (activity as CashServicesActivity).navController.navigate(R.id.action_cashServicesTypeFragment_to_cashMsisdnAndAmountFragment)
                    }
                    LanguageData.getStringValue("Withdraw").toString() ->{
                        mActivityViewModel.isDepositUseCase.set(false)
                        mActivityViewModel.isWithdrawUseCase.set(true)
                        mActivityViewModel.trasferTypeSelected.set(LanguageData.getStringValue("Withdraw").toString())
                        (activity as CashServicesActivity).navController.navigate(R.id.action_cashServicesTypeFragment_to_cashMsisdnAndAmountFragment)
                    }
                    else -> Toast.makeText(activity,"Nothing Clicked", Toast.LENGTH_SHORT).show()

                }
            }

        })
        mDataBinding.paymentTypeRecycler.apply {
            adapter = mCashServicesItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as CashServicesActivity)
        }

        setStrings()

    }

    private fun setStrings() {
        mDataBinding.tvPaymentType.text = LanguageData.getStringValue("TransferType")

        (activity as CashServicesActivity).setHeaderTitle(LanguageData.getStringValue("CashService").toString())
    }


}

