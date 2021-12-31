package com.es.marocapp.usecase.login.forgotpassword

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentForgotPasswordBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.ForgotPasswordResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger

import java.util.regex.Pattern

class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>(), ForgotPasswordClickListner,
    TextWatcher {

    lateinit var mActivityViewModel: LoginActivityViewModel

    var isOTPRegexMatches = false

    override fun setLayout(): Int {
        return R.layout.fragment_forgot_password
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as LoginActivity).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@ForgotPasswordFragment
        }

        mDataBinding.forgotPinHeader.groupBack.visibility = View.VISIBLE

//        mDataBinding.root.txtBack.setOnClickListener {
//            (activity as LoginActivity).navController.navigateUp()
//        }

        mDataBinding.inputForgotOtp.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.APP_OTP_LENGTH))

        mDataBinding.inputForgotPassword.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.APP_MAX_PASSWORD_LENGTH))

        mDataBinding.inputForgotConfirmPassword.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.APP_MAX_PASSWORD_LENGTH))


        mDataBinding.forgotPinHeader.imgBackButton.setOnClickListener {
            (activity as LoginActivity).navController.navigateUp()
        }

        if(mActivityViewModel.isForgotPasswordDialogToShow){
          //  mActivityViewModel.requestForGetProfileApi(activity,mActivityViewModel.currentUserMSISDN)
            if(Constants.IS_AGENT_USER)
            {
                DialogUtils.showCustomDialogue(activity,LanguageData.getStringValue("BtnTitle_OK"),LanguageData.getStringValue("PleaseCallCallcenterForGeneratingOTPAgent"),
                    LanguageData.getStringValue("OTP"),object : DialogUtils.OnCustomDialogListner{
                        override fun onCustomDialogOkClickListner() {
                        }
                    }
                )
            }
            else{
                DialogUtils.showCustomDialogue(activity,LanguageData.getStringValue("BtnTitle_OK"),LanguageData.getStringValue("PleaseCallCallcenterForGeneratingOTP"),
                    LanguageData.getStringValue("OTP"),object : DialogUtils.OnCustomDialogListner{
                        override fun onCustomDialogOkClickListner() {
                        }
                    }
                )
            }


        }

        mDataBinding.inputForgotOtp.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.APP_OTP_LENGTH))

        mDataBinding.inputForgotOtp.addTextChangedListener(this)

        mActivityViewModel.isSimplePopUp = true
        subsribeObserver()
        setStrings()

    }

    private fun setStrings() {
        mDataBinding.forgotPinHeader.txtHeaderTitle.text = LanguageData.getStringValue("ForgotPassword")
//        mDataBinding.root.txtBack.text= LanguageData.getStringValue("BtnTitle_Back")

        mDataBinding.inputLayoutOtp.hint = LanguageData.getStringValue("EnterOTP")
        mDataBinding.inputLayoutSetYourPassword.hint = LanguageData.getStringValue("EnterPassword")
        mDataBinding.inputLayoutConfirmPassword.hint = LanguageData.getStringValue("ConfirmPassword")

        mDataBinding.btnNext.text = LanguageData.getStringValue("BtnTitle_Validate")
    }

    private fun subsribeObserver() {
        mActivityViewModel.errorText.observe(this@ForgotPasswordFragment, Observer {
            DialogUtils.showErrorDialoge(activity as LoginActivity,it)
        })

        val mForgotPasswordListener = Observer<ForgotPasswordResponse>{
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                (activity as LoginActivity).navController.popBackStack(R.id.signUpNumberFragment,false)
            }else{
                DialogUtils.showErrorDialoge(activity as LoginActivity,it.description)
            }
        }

        mActivityViewModel.getForgotPasswordResponseListner.observe(this@ForgotPasswordFragment,mForgotPasswordListener)
    }

    override fun onChangePasswordClickListner(view: View) {
        if(isValidForAll()){
            if(mDataBinding.inputForgotPassword.text.toString().trim().equals(mDataBinding.inputForgotConfirmPassword.text.toString().trim())){
                mDataBinding.inputLayoutConfirmPassword.error = ""
                mDataBinding.inputLayoutConfirmPassword.isErrorEnabled = false
                mActivityViewModel.requestForForgotPasswordAPI(activity,mDataBinding.inputForgotPassword.text.toString().trim(),mDataBinding.inputForgotOtp.text.toString().trim())
            }else{
                mDataBinding.inputLayoutConfirmPassword.error = LanguageData.getStringValue("PasswordAndConfirmPasswordDoesntMatch")
                mDataBinding.inputLayoutConfirmPassword.isErrorEnabled = true
            }
        }
    }

    private fun isValidForAll(): Boolean {

        var isValidForAll = true

        if(mDataBinding.inputForgotOtp.text.isNullOrEmpty()){
            isValidForAll = false
            mDataBinding.inputLayoutOtp.error = LanguageData.getStringValue("PleaseEnterValidOTP")
            mDataBinding.inputLayoutOtp.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutOtp.error = ""
            mDataBinding.inputLayoutOtp.isErrorEnabled = false
            if(isOTPRegexMatches){
                mDataBinding.inputLayoutOtp.error = ""
                mDataBinding.inputLayoutOtp.isErrorEnabled = false

            }else{
                isValidForAll = false
                mDataBinding.inputLayoutOtp.error = LanguageData.getStringValue("PleaseEnterValidOTP")
                mDataBinding.inputLayoutOtp.isErrorEnabled = true
            }
        }

        if(mDataBinding.inputForgotPassword.text.isNullOrEmpty()){
            isValidForAll = false
            mDataBinding.inputLayoutSetYourPassword.error = LanguageData.getStringValue("PleaseEnterValidPassword")
            mDataBinding.inputLayoutSetYourPassword.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutSetYourPassword.error = ""
            mDataBinding.inputLayoutSetYourPassword.isErrorEnabled = false
        }

        if(mDataBinding.inputForgotConfirmPassword.text.isNullOrEmpty()){
            isValidForAll = false
            mDataBinding.inputLayoutConfirmPassword.error = LanguageData.getStringValue("PleaseEnterValidPassword")
            mDataBinding.inputLayoutConfirmPassword.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutConfirmPassword.error = ""
            mDataBinding.inputLayoutConfirmPassword.isErrorEnabled = false
        }

        return isValidForAll
    }

    override fun afterTextChanged(p0: Editable?) {
        var otp = mDataBinding.inputForgotOtp.text.toString().trim()
        var otpLenght = otp.length
        isOTPRegexMatches =
            (otpLenght > 0 && Pattern.matches(Constants.APP_OTP_REGEX, otp))
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }


}
