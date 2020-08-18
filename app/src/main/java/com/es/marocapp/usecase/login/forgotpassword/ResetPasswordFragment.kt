package com.es.marocapp.usecase.login.forgotpassword

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
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
import kotlinx.android.synthetic.main.layout_login_header.view.*
import java.util.regex.Pattern

class ResetPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>(), ForgotPasswordClickListner,
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
            listener = this@ResetPasswordFragment
        }

        mDataBinding.root.groupBack.visibility = View.VISIBLE

//        mDataBinding.root.txtBack.setOnClickListener {
//            (activity as LoginActivity).navController.navigateUp()
//        }

        mDataBinding.root.imgBackButton.setOnClickListener {
            (activity as LoginActivity).navController.navigateUp()
        }

        DialogUtils.showCustomDialogue(activity,
            LanguageData.getStringValue("BtnTitle_OK"),
            LanguageData.getStringValue("PleaseCallCallcenterForGeneratingOTP"),
            LanguageData.getStringValue("OTP"),object : DialogUtils.OnCustomDialogListner{
                override fun onCustomDialogOkClickListner() {

                }

            }
        )

        mDataBinding.inputForgotOtp.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.APP_OTP_LENGTH))

        mDataBinding.inputForgotOtp.addTextChangedListener(this)

        mActivityViewModel.isSimplePopUp = true
        subsribeObserver()
        setStrings()

    }

    private fun setStrings() {
        mDataBinding.root.txtHeaderTitle.text = LanguageData.getStringValue("BtnTitle_ResetPassword")
//        mDataBinding.root.txtBack.text= LanguageData.getStringValue("BtnTitle_Back")

        mDataBinding.inputLayoutOtp.hint = LanguageData.getStringValue("EnterOTP")
        mDataBinding.inputLayoutSetYourPassword.hint = LanguageData.getStringValue("EnterPassword")
        mDataBinding.inputLayoutConfirmPassword.hint = LanguageData.getStringValue("ConfirmPassword")

        mDataBinding.btnNext.text = LanguageData.getStringValue("BtnTitle_Reset")
    }

    private fun subsribeObserver() {
        mActivityViewModel.errorText.observe(this@ResetPasswordFragment, Observer {
            DialogUtils.showErrorDialoge(activity as LoginActivity,it)
        })

        val mForgotPasswordListener = Observer<ForgotPasswordResponse>{
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                if(mActivityViewModel.isFromLoginUserScreen.get()!!){
                    mActivityViewModel.isResetPassowrdFlow = true
                    (activity as LoginActivity).navController.navigateUp()
                }else{
                    (activity as LoginActivity).navController.navigate(R.id.action_resetPasswordFragment_to_signUpNumberFragment)
                }
            }else{
                DialogUtils.showErrorDialoge(activity as LoginActivity,it.description)
            }
        }

        mActivityViewModel.getForgotPasswordResponseListner.observe(this@ResetPasswordFragment,mForgotPasswordListener)
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
