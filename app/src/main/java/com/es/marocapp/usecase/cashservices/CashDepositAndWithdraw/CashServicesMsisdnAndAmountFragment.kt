package com.es.marocapp.usecase.cashservices.CashDepositAndWithdraw

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentCashServicesNumberAmountBinding
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.cashservices.CashServicesActivity
import com.es.marocapp.usecase.cashservices.CashServicesClickListner
import com.es.marocapp.usecase.cashservices.CashServicesViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import kotlin.math.roundToInt

class CashServicesMsisdnAndAmountFragment : BaseFragment<FragmentCashServicesNumberAmountBinding>(),
    CashServicesClickListner {

    private lateinit var mActivityViewModel: CashServicesViewModel

    var msisdnEntered = ""

    override fun setLayout(): Int {
        return R.layout.fragment_cash_services_number_amount
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as CashServicesActivity).get(CashServicesViewModel::class.java)
        mDataBinding.apply {
            listner = this@CashServicesMsisdnAndAmountFragment
            viewmodel = mActivityViewModel
        }

        mActivityViewModel.popBackStackTo = R.id.cashServicesTypeFragment

        mActivityViewModel.trasferTypeSelected.get()?.let {
            (activity as CashServicesActivity).setHeaderTitle(
                it
            )
        }
        (activity as CashServicesActivity).setHeaderVisibility(true)
        //todo also here remove lenght-2 check in max line
        mDataBinding.inputPhoneNumber.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Constants.APP_MSISDN_LENGTH.toInt() - 2
            )
        )

        subscribeObserver()
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@CashServicesMsisdnAndAmountFragment, Observer {
            DialogUtils.showErrorDialoge(activity, it)
        })

        mActivityViewModel.getInitiateTrasnferQuoteResponseListner.observe(this@CashServicesMsisdnAndAmountFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.quoteList.isNotEmpty()) {
                        mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                        mActivityViewModel.qouteId = it.quoteList[0].quoteid
                    }
                    mActivityViewModel.isOTPFlow.set(false)
                    (activity as CashServicesActivity).navController.navigate(R.id.action_cashMsisdnAndAmountFragment_to_cashServicesConfirmationFragment)
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })

        mActivityViewModel.getGenerateOtpResponseListner.observe(this@CashServicesMsisdnAndAmountFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    mActivityViewModel.isOTPFlow.set(true)
                    (activity as CashServicesActivity).navController.navigate(R.id.action_cashMsisdnAndAmountFragment_to_cashServicesVerifyOtpFragment)
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })
    }

    override fun onNextClickListner(view: View) {
        if (isValidForAll()) {
            if (mActivityViewModel.isDepositUseCase.get()!!) {
                mActivityViewModel.requestForGenerateOtpApi(activity, msisdnEntered,mDataBinding.inputAmount.text.toString().trim(),
                    mDataBinding.inputNote.text.toString().trim())
            }

            if (mActivityViewModel.isWithdrawUseCase.get()!!) {
                mActivityViewModel.requestForInitiateTransferQouteApi(
                    activity, mDataBinding.inputAmount.text.toString().trim(),
                    msisdnEntered,
                    mDataBinding.inputNote.text.toString().trim()
                )
            }
        }
    }

    override fun onBackClickListner(view: View) {

    }

    private fun isValidForAll(): Boolean {
        var isValidForAll = true

        if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() && mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
            isValidForAll = false
            mDataBinding.inputLayoutPhoneNumber.error = "Please Enter Valid Mobile Number"
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutPhoneNumber.error = ""
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

            var userMsisdn = mDataBinding.inputPhoneNumber.text.toString()
            if (userMsisdn.startsWith("0", false)) {
                mDataBinding.inputLayoutPhoneNumber.error = ""
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                var userMSISDNwithPrefix = userMsisdn.removePrefix("0")
                userMSISDNwithPrefix = Constants.APP_MSISDN_PREFIX + userMSISDNwithPrefix
                userMSISDNwithPrefix = userMSISDNwithPrefix.removePrefix("+")

                msisdnEntered = userMSISDNwithPrefix
            } else {
                isValidForAll = false
                mDataBinding.inputLayoutPhoneNumber.error = "Please Enter Valid Mobile Number"
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
            }
        }

        if (mDataBinding.inputAmount.text.isNullOrEmpty()) {
            isValidForAll = false
            mDataBinding.inputLayoutAmount.error = "Please Enter Valid Amount"
            mDataBinding.inputLayoutAmount.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutAmount.error = ""
            mDataBinding.inputLayoutAmount.isErrorEnabled = false

            val sAmount: String = mDataBinding.inputAmount.text.toString().trim { it <= ' ' }

            if (sAmount == "" || SumAmountEditText() == "0" || sAmount == ".") {
                isValidForAll = false
                mDataBinding.inputLayoutAmount.error = "Please enter valid amount to proceed."
                mDataBinding.inputLayoutAmount.isErrorEnabled = true
            } else {
                mDataBinding.inputLayoutAmount.error = ""
                mDataBinding.inputLayoutAmount.isErrorEnabled = false
            }
        }

        return isValidForAll
    }

    private fun SumAmountEditText(): String? {
        val dNum: Double =
            mDataBinding.inputAmount.text.toString().trim { it <= ' ' }.toDouble()
        val nNum = dNum.roundToInt()
        return nNum.toString()
    }
}