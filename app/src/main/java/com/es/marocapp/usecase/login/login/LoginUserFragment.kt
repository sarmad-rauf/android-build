package com.es.marocapp.usecase.login.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.BuildConfig

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentSignUpNumberBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.BalanceInfoAndLimitResponse
import com.es.marocapp.model.responses.LoginWithCertResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.security.EncryptionUtils
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.layout_login_header.view.*

/**
 * A simple [Fragment] subclass.
 */
class LoginUserFragment : BaseFragment<FragmentSignUpNumberBinding>(), LoginClickListener {

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_sign_up_number
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as LoginActivity).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@LoginUserFragment
        }

        mDataBinding.inputPhoneNumber.setText(mActivityViewModel.mUserMsisdn)

        subscribeObserver()
        setStrings()

    }

    private fun setStrings() {
        mDataBinding.root.txtHeaderTitle.text= LanguageData.getStringValue("LoginIntoAccount")
        mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterMobileNumber")
        mDataBinding.inputLayoutPin.hint = LanguageData.getStringValue("Password")
        mDataBinding.txtForgotPin.hint = LanguageData.getStringValue("ForgotPasswordQuestion")

        mDataBinding.btnLoginIN.text = LanguageData.getStringValue("BtnTitle_Login")
    }

    private fun subscribeObserver() {

        mActivityViewModel.errorText.observe(this@LoginUserFragment, Observer {
            DialogUtils.showErrorDialoge(activity as LoginActivity,it)
        })

        val mBalanceInfoAndLimtListner = Observer<BalanceInfoAndLimitResponse> {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                Constants.balanceInfoAndResponse = it
                (activity as LoginActivity).startNewActivityAndClear(
                    activity as LoginActivity,
                    MainActivity::class.java
                )
            } else {
                DialogUtils.showErrorDialoge(activity as LoginActivity,it.description)
            }
        }

        val mloginWithCertListner = Observer<LoginWithCertResponse> {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                //setting User TYpe
                Constants.IS_AGENT_USER = it.profile.agentUser
                Constants.IS_CONSUMER_USER = it.profile.consumerUser
                Constants.IS_MERCHANT_USER = it.profile.merchantUser

                Constants.loginWithCertResponse = it
                Constants.mContactListArray.addAll(Constants.loginWithCertResponse.contactList)
                //setting cookie for use in header
                if (it.setCookie.isNotEmpty()) {
                    var cookie = it.setCookie
                    Constants.setBase64EncodedString(cookie)
                    Constants.LOGGED_IN_USER_COOKIE= EncryptionUtils.encryptString(Constants.LOGGED_IN_USER_COOKIE)
                    Constants.LOGGED_IN_USER = mActivityViewModel.mUserMsisdn
                }
                Constants.HEADERS_AFTER_LOGINS = true
                mActivityViewModel.requestForBalanceInfoAndLimtsAPI(activity)
            } else {
                Constants.HEADERS_AFTER_LOGINS = false
                Constants.LOGGED_IN_USER = ""
                DialogUtils.showErrorDialoge(activity as LoginActivity,it.description)
            }
        }

        mActivityViewModel.getBalanceInforAndLimitResponseListner.observe(
            this@LoginUserFragment,
            mBalanceInfoAndLimtListner
        )
        mActivityViewModel.getLoginWithCertResponseListner.observe(
            this@LoginUserFragment,
            mloginWithCertListner
        )
    }

    override fun onLoginButtonClick(view: View) {
        if (mDataBinding.inputPin.text.toString() == "") {
            mDataBinding.inputLayoutPin.error = LanguageData.getStringValue("PleaseEnterValidPassword")
            mDataBinding.inputLayoutPin.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutPin.error = ""
            mDataBinding.inputLayoutPin.isErrorEnabled = false

            //For Testing For without API Calling moving to Next Fragment uncomment below LineE
//            (activity as LoginActivity).startNewActivityAndClear(activity as LoginActivity, MainActivity::class.java)

            mActivityViewModel.requestForLogigWithCertAPI(
                activity,
                mDataBinding.inputPin.text.toString().trim(),
                BuildConfig.VERSION_NAME
            )
        }
    }

    override fun onForgotPinClick(view: View) {
        (activity as LoginActivity).navController.navigate(R.id.action_signUpNumberFragment_to_forgotPasswordFragment)
    }

    override fun onSignUpClick(view: View) {

    }

}
