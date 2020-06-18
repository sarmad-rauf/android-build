package com.es.marocapp.usecase.login.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentVerifyNumberBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.model.responses.RegisterUserResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.layout_login_header.view.*

/**
 * A simple [Fragment] subclass.
 */
class VerifyNumberFragment : BaseFragment<FragmentVerifyNumberBinding>(),
    VerifyOTPClickListner {

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_verify_number
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as LoginActivity).get(
            LoginActivityViewModel::class.java
        )

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@VerifyNumberFragment
        }

        mDataBinding.root.groupBack.visibility = View.VISIBLE

        mDataBinding.root.txtBack.setOnClickListener {
            (activity as LoginActivity).navController.navigateUp()
        }

        mDataBinding.root.imgBackButton.setOnClickListener {
            (activity as LoginActivity).navController.navigateUp()
        }

        mDataBinding.txtResend.setOnClickListener {
            mActivityViewModel.requestForGetOTPForRegistrationApi(context,mActivityViewModel.firstName,mActivityViewModel.lastName,mActivityViewModel.identificationNumber)
        }

        subscribeObserver()
        setStrings()

    }

    private fun setStrings() {
        mDataBinding.root.txtHeaderTitle.text = LanguageData.getStringValue("VerifyYourNumber")
        mDataBinding.root.txtBack.text= LanguageData.getStringValue("BtnTitle_Back")

        mDataBinding.inputLayoutVerifyOtp.hint = LanguageData.getStringValue("EnterOTP")
        mDataBinding.txtOtpNotRecieved.text = LanguageData.getStringValue("OTPNotRecieved")+ " "
        mDataBinding.txtResend.text = LanguageData.getStringValue("Resend")

        mDataBinding.btnVerifyOtp.text = LanguageData.getStringValue("BtnTitle_Verify")

    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@VerifyNumberFragment, Observer {
            DialogUtils.showErrorDialoge(activity as LoginActivity, it)
        })

        val mRegisterUserResonseObserver = Observer<RegisterUserResponse> {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_setYourPinFragment)
            } else {
                DialogUtils.showErrorDialoge(activity as LoginActivity, it.description)
            }
        }

        mActivityViewModel.getOtpForRegistrationResponseListner.observe(this@VerifyNumberFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_FAILURE)){
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            })

        mActivityViewModel.getRegisterUserResponseListner.observe(
            this,
            mRegisterUserResonseObserver
        )
    }

    override fun onOTPVerifyClick(view: View) {
        if (mDataBinding.inputVerifyOtp.text.isNullOrEmpty()) {
            mDataBinding.inputLayoutVerifyOtp.error = LanguageData.getStringValue("PleaseEnterValidOTP")
            mDataBinding.inputLayoutVerifyOtp.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutVerifyOtp.error = ""
            mDataBinding.inputLayoutVerifyOtp.isErrorEnabled = false

            mActivityViewModel.requestForRegisterUserApi(
                activity,
                Constants.CURRENT_NUMBER_DEVICE_ID,
                mDataBinding.inputVerifyOtp.text.toString().trim()
            )
        }
    }

}
