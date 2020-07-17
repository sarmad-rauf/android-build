package com.es.marocapp.usecase.consumerregistration.fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentConsumerRegistrationVerifyOtpBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.consumerregistration.ConsumerRegistrationActivity
import com.es.marocapp.usecase.consumerregistration.ConsumerRegistrationClickListner
import com.es.marocapp.usecase.consumerregistration.ConsumerRegistrationViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import java.util.regex.Pattern

class ConsumerRegistrationVerifyOtpFragment : BaseFragment<FragmentConsumerRegistrationVerifyOtpBinding>(),
    ConsumerRegistrationClickListner, TextWatcher {

    lateinit var mActivityViewModel: ConsumerRegistrationViewModel

    var isOTPRegexMatches = false


    override fun setLayout(): Int {
        return R.layout.fragment_consumer_registration_verify_otp
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as ConsumerRegistrationActivity).get(
            ConsumerRegistrationViewModel::class.java
        )

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listner = this@ConsumerRegistrationVerifyOtpFragment
        }

        mActivityViewModel.popBackStackTo = R.id.consumerRegistrationDetailFragment

        mDataBinding.txtResend.setOnClickListener {
            mActivityViewModel.requestForGetOTPForRegistrationApi(activity,mActivityViewModel.firstName,mActivityViewModel.lastName,mActivityViewModel.identificationNumber)
        }

        mDataBinding.inputVerifyOtp.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.APP_OTP_LENGTH))

        mDataBinding.inputVerifyOtp.addTextChangedListener(this)

        setStrings()
        subscribeObserver()
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@ConsumerRegistrationVerifyOtpFragment, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        })

        mActivityViewModel.getRegisterUserResponseListner.observe(this@ConsumerRegistrationVerifyOtpFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    DialogUtils.showSuccessDialog(activity,it.description,object : DialogUtils.OnConfirmationDialogClickListner{
                        override fun onDialogYesClickListner() {
                            (activity as ConsumerRegistrationActivity).startNewActivityAndClear(
                                activity as ConsumerRegistrationActivity,
                                MainActivity::class.java
                            )
                        }

                    })
                }else{
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            }
        )

        mActivityViewModel.getRegisterUserResponseListner.observe(this@ConsumerRegistrationVerifyOtpFragment,
            Observer {
                if(!it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            }
        )
    }

    private fun setStrings() {
        mDataBinding.inputLayoutVerifyOtp.hint = LanguageData.getStringValue("EnterOTP")
        mDataBinding.txtOtpNotRecieved.text = LanguageData.getStringValue("OTPNotRecieved")+ " "
        mDataBinding.txtResend.text = LanguageData.getStringValue("Resend")
        mDataBinding.btnVerifyOtp.text = LanguageData.getStringValue("BtnTitle_Verify")
    }

    override fun onSubmitClickListner(view: View) {
        if (mDataBinding.inputVerifyOtp.text.isNullOrEmpty()) {
            mDataBinding.inputLayoutVerifyOtp.error = LanguageData.getStringValue("PleaseEnterValidOTP")
            mDataBinding.inputLayoutVerifyOtp.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutVerifyOtp.error = ""
            mDataBinding.inputLayoutVerifyOtp.isErrorEnabled = false

            if(isOTPRegexMatches){
                mDataBinding.inputLayoutVerifyOtp.error = ""
                mDataBinding.inputLayoutVerifyOtp.isErrorEnabled = false

                mActivityViewModel.requestForRegisterUserApi(
                    activity,
                    "",
                    mDataBinding.inputVerifyOtp.text.toString().trim()
                )


            }else{
                mDataBinding.inputLayoutVerifyOtp.error = LanguageData.getStringValue("PleaseEnterValidOTP")
                mDataBinding.inputLayoutVerifyOtp.isErrorEnabled = true
            }
        }
    }

    override fun onCalenderCalenderClick(view: View) {
    }

    override fun onGenderSelectionClick(view: View) {
    }

    override fun afterTextChanged(p0: Editable?) {
        var otp = mDataBinding.inputVerifyOtp.text.toString().trim()
        var otpLenght = otp.length
        isOTPRegexMatches =
            (otpLenght > 0 && Pattern.matches(Constants.APP_OTP_REGEX, otp))
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}