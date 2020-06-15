package com.es.marocapp.usecase.airtime

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.CustomizeIconsAdapter
import com.es.marocapp.databinding.FragmentBillPaymentTypeBinding
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.utils.DialogUtils

class AirTimeTypeFragment : BaseFragment<FragmentBillPaymentTypeBinding>() {

    lateinit var mActivityViewModel: AirTimeViewModel
    private lateinit var mAirTimeItemTypeAdapter: CustomizeIconsAdapter
    private var mAirTimeTypes: ArrayList<String> = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_type
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as AirTimeActivity).get(
            AirTimeViewModel::class.java
        )
        mDataBinding.apply {
        }

        mDataBinding.tvPaymentType.text = activity!!.resources.getString(R.string.payment_type)
        (activity as AirTimeActivity).setHeaderTitle(
            (activity as AirTimeActivity).resources.getString(
                R.string.air_time
            )
        )

        (activity as AirTimeActivity).setHeaderVisibility(true)

        mActivityViewModel.popBackStackTo = -1

        mActivityViewModel.requestForAirTimeUseCasesApi(activity)

        mAirTimeItemTypeAdapter = CustomizeIconsAdapter(mAirTimeTypes,
            object : CustomizeIconsAdapter.CustomizeItemClickListner {
                override fun onCustomizeItemTypeClick(paymentItems: String) {
                    when (paymentItems) {
                        "Recharge Fixe" -> {
                            mActivityViewModel.isRechargeFixeUseCase.set(true)
                            mActivityViewModel.isRechargeMobileUseCase.set(false)
                            mActivityViewModel.airTimeSelected.set("Recharge Fixe")
//                            (activity as AirTimeActivity).navController.navigate(R.id.action_cashServicesTypeFragment_to_cashMsisdnAndAmountFragment)
                        }
                        "Recharge Mobile" -> {
                            mActivityViewModel.isRechargeFixeUseCase.set(false)
                            mActivityViewModel.isRechargeMobileUseCase.set(true)
                            mActivityViewModel.airTimeSelected.set("Recharge Mobile")
//                            (activity as AirTimeActivity).navController.navigate(R.id.action_cashServicesTypeFragment_to_cashMsisdnAndAmountFragment)
                        }
                        else -> Toast.makeText(activity, "Nothing Clicked", Toast.LENGTH_SHORT)
                            .show()

                    }
                }

            })
        mDataBinding.paymentTypeRecycler.apply {
            adapter = mAirTimeItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as AirTimeActivity)
        }

        subscribeObserver()

    }

    private fun subscribeObserver() {
        mActivityViewModel.getAirTimeUseCasesResponseListner.observe(this@AirTimeTypeFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (!it.rechargeFixe.isNullOrEmpty()) {
                        mAirTimeTypes.add("Recharge Fixe")
                    }

                    if (!it.rechargeMobile.isNullOrEmpty()) {
                        mAirTimeTypes.add("Recharge Mobile")
                    }

                    mAirTimeItemTypeAdapter.notifyDataSetChanged()
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })
    }

}