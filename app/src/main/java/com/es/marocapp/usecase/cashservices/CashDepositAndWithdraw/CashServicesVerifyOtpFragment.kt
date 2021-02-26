package com.es.marocapp.usecase.cashservices.CashDepositAndWithdraw

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentCashServicesVerifyOtpBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.cashservices.CashServicesActivity
import com.es.marocapp.usecase.cashservices.CashServicesClickListner
import com.es.marocapp.usecase.cashservices.CashServicesViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class CashServicesVerifyOtpFragment : BaseFragment<FragmentCashServicesVerifyOtpBinding>(),
    CashServicesClickListner {

    private lateinit var mActivityViewModel: CashServicesViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_cash_services_verify_otp
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as CashServicesActivity).get(CashServicesViewModel::class.java)
        mDataBinding.apply {
            listner = this@CashServicesVerifyOtpFragment
            viewmodel = mActivityViewModel
        }

        mActivityViewModel.trasferTypeSelected.get()?.let {
            (activity as CashServicesActivity).setHeaderTitle(
                it
            )
        }
        (activity as CashServicesActivity).setHeaderVisibility(true)

        mActivityViewModel.popBackStackTo = R.id.cashMsisdnAndAmountFragment


        mDataBinding.txtResend.setOnClickListener {
            mActivityViewModel.requestForGenerateOtpApi(activity,mActivityViewModel.transferdAmountTo,mActivityViewModel.amountToTransfer,mActivityViewModel.noteToSend)
        }

        setStrings()
        subscribeObserver()
    }

    private fun setStrings() {
        mDataBinding.inputLayoutVerifyOtp.hint = LanguageData.getStringValue("EnterOTP")
        mDataBinding.txtOtpNotRecieved.text = LanguageData.getStringValue("OTPNotRecieved")+ " "
        mDataBinding.txtResend.text = LanguageData.getStringValue("Resend")
        mDataBinding.btnVerifyOtp.text = LanguageData.getStringValue("BtnTitle_Verify")
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@CashServicesVerifyOtpFragment, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        })

        mActivityViewModel.getCashInWithOtpQuoteResponseListner.observe(this@CashServicesVerifyOtpFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.quoteList.isNotEmpty()) {
                        mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                        mActivityViewModel.qouteId = it.quoteList[0].quoteid
                    }

                    (activity as CashServicesActivity).navController.navigate(R.id.action_cashServicesVerifyOtpFragment_to_cashServicesConfirmationFragment)
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })

        mActivityViewModel.getGenerateOtpResponseListner.observe(this@CashServicesVerifyOtpFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {

                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })
    }

    override fun onNextClickListner(view: View) {
        if (mDataBinding.inputVerifyOtp.text.isNullOrEmpty()) {
            mDataBinding.inputLayoutVerifyOtp.error = "Please Enter Valid OTP"
            mDataBinding.inputLayoutVerifyOtp.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutVerifyOtp.error = ""
            mDataBinding.inputLayoutVerifyOtp.isErrorEnabled = false

            mActivityViewModel.requestForCashInQouteApi(
                activity
                //,
              //  mDataBinding.inputVerifyOtp.text.toString().trim()
            )
        }
    }

    override fun onBackClickListner(view: View) {
    }

}