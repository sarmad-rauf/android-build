package com.es.marocapp.usecase.login.forgotpassword

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentForgotPasswordBinding
import com.es.marocapp.model.responses.ForgotPasswordResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.layout_login_header.view.*

class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>(), ForgotPasswordClickListner{

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_forgot_password
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as LoginActivity).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@ForgotPasswordFragment
        }

        mDataBinding.root.txtHeaderTitle.text = getString(R.string.forgot_pinn)

        subsribeObserver()

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
                mDataBinding.inputLayoutConfirmPassword.error = "Please Enter Same Password"
                mDataBinding.inputLayoutConfirmPassword.isErrorEnabled = true
            }
        }
    }

    private fun isValidForAll(): Boolean {

        var isValidForAll = true

        if(mDataBinding.inputForgotOtp.text.isNullOrEmpty()){
            isValidForAll = false
            mDataBinding.inputLayoutOtp.error = "Please enter OTP sent to your mobile number."
            mDataBinding.inputLayoutOtp.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutOtp.error = ""
            mDataBinding.inputLayoutOtp.isErrorEnabled = false
        }

        if(mDataBinding.inputForgotPassword.text.isNullOrEmpty()){
            isValidForAll = false
            mDataBinding.inputLayoutSetYourPassword.error = "Please enter valid password"
            mDataBinding.inputLayoutSetYourPassword.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutSetYourPassword.error = ""
            mDataBinding.inputLayoutSetYourPassword.isErrorEnabled = false
        }

        if(mDataBinding.inputForgotConfirmPassword.text.isNullOrEmpty()){
            isValidForAll = false
            mDataBinding.inputLayoutConfirmPassword.error = "Please enter valid password"
            mDataBinding.inputLayoutConfirmPassword.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutConfirmPassword.error = ""
            mDataBinding.inputLayoutConfirmPassword.isErrorEnabled = false
        }

        return isValidForAll
    }


}
