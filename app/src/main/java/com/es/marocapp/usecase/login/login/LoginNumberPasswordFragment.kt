package com.es.marocapp.usecase.login.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.BuildConfig

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentLoginNumberPasswordBinding
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
import com.es.marocapp.utils.PrefUtils
import com.es.marocapp.utils.PrefUtils.PreKeywords.PREF_KEY_USER_MSISDN
import kotlinx.android.synthetic.main.layout_login_header.view.*

/**
 * A simple [Fragment] subclass.
 */
class LoginNumberPasswordFragment : BaseFragment<FragmentLoginNumberPasswordBinding>(), LoginClickListener {

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_login_number_password
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as LoginActivity).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@LoginNumberPasswordFragment
        }

        var numberToShow = mActivityViewModel.mUserMsisdn
        numberToShow = numberToShow.substringAfter(Constants.APP_MSISDN_PREFIX)
        numberToShow = numberToShow.substringAfter("212")
        numberToShow = numberToShow.substringAfter("+212")
        numberToShow = "0$numberToShow"
        mDataBinding.inputPhoneNumber.setText(numberToShow)
        mActivityViewModel.isNewUserRegisterd.set(false)

        mActivityViewModel.isSimplePopUp = false
        subscribeObserver()
        setStrings()

    }

    private fun setStrings() {
        mDataBinding.root.txtHeaderTitle.text= LanguageData.getStringValue("LoginIntoAccount")
        mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterMobileNumber")
        mDataBinding.inputLayoutPin.hint = LanguageData.getStringValue("Password")
        mDataBinding.txtForgotPin.text = LanguageData.getStringValue("ForgotPasswordQuestion")

        mDataBinding.btnLoginIN.text = LanguageData.getStringValue("BtnTitle_Login")
    }

    private fun subscribeObserver() {

        mActivityViewModel.errorText.observe(this@LoginNumberPasswordFragment, Observer {
            DialogUtils.showErrorDialoge(activity as LoginActivity,it)
        })

        mActivityViewModel.getAccountsResponseListner.observe(this@LoginNumberPasswordFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    for(i in it.accounts.indices){
                        if(it.accounts[i].accountType.equals(Constants.TYPE_COMMISSIONING,true)){
                            Constants.getAccountsResponse = it.accounts[i]
                        }
                    }
                    PrefUtils.addString(activity!!,PREF_KEY_USER_MSISDN,Constants.CURRENT_USER_MSISDN)
                    (activity as LoginActivity).startNewActivityAndClear(
                        activity as LoginActivity,
                        MainActivity::class.java
                    )
                }else{
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            }
        )

        val mBalanceInfoAndLimtListner = Observer<BalanceInfoAndLimitResponse> {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                if(Constants.IS_AGENT_USER){
                    Constants.balanceInfoAndResponse = it
                    mActivityViewModel.requestForGetAccountsAPI(activity)
                }else{
                    Constants.balanceInfoAndResponse = it
                    PrefUtils.addString(activity!!,PREF_KEY_USER_MSISDN,Constants.CURRENT_USER_MSISDN)
                    (activity as LoginActivity).startNewActivityAndClear(
                        activity as LoginActivity,
                        MainActivity::class.java
                    )
                }
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
                if(Constants.loginWithCertResponse.contactList!=null) {
                    Constants.mContactListArray.addAll(Constants.loginWithCertResponse.contactList)
                }
                //setting cookie for use in header
                if (it.setCookie.isNotEmpty()) {
                    var cookie = it.setCookie
                    Constants.setBase64EncodedString(cookie)
                    Constants.LOGGED_IN_USER_COOKIE= EncryptionUtils.encryptString(Constants.LOGGED_IN_USER_COOKIE)
                    Constants.LOGGED_IN_USER = mActivityViewModel.mUserMsisdn
                }
                Constants.HEADERS_AFTER_LOGINS = true
                mActivityViewModel.requestForBalanceInfoAndLimtsAPI(activity)
            }else if(it.responseCode == ApiConstant.API_ACCOUNT_BLOCKED){
                val btnTxt = LanguageData.getStringValue("BtnTitle_OK")
                val titleTxt = LanguageData.getStringValue("AccountBlocked")
                val descriptionTxt =it.description
                DialogUtils.showCustomDialogue(activity,btnTxt,descriptionTxt,titleTxt,object : DialogUtils.OnCustomDialogListner{
                    override fun onCustomDialogOkClickListner() {
                        (activity as LoginActivity).navController.popBackStack(R.id.loginFragment,false)
                    }
                })
            }  else if(it.responseCode.equals(ApiConstant.API_WRONG_ATTEMPT_BLOCKED)){
                DialogUtils.showBlockedAccountDialog(activity,LanguageData.getStringValue("BtnTitle_ResetPassword"),LanguageData.getStringValue("BtnTitle_Cancel"),
                LanguageData.getStringValue("BlockedAndResetAccount"),LanguageData.getStringValue("AccountBlocked"),object : DialogUtils.OnCustomDialogListner{
                        override fun onCustomDialogOkClickListner() {
                            mActivityViewModel.isFromLoginUserScreen.set(true)
                            mDataBinding.inputPin.setText("")
                            (activity as LoginActivity).navController.navigate(R.id.action_signUpNumberFragment_to_resetPasswordFragment)
                        }

                    }
                )
            } else {
                Constants.HEADERS_AFTER_LOGINS = false
                Constants.LOGGED_IN_USER = ""
                DialogUtils.showErrorDialoge(activity as LoginActivity,it.description)
            }
        }

        mActivityViewModel.getBalanceInforAndLimitResponseListner.observe(
            this@LoginNumberPasswordFragment,
            mBalanceInfoAndLimtListner
        )
        mActivityViewModel.getLoginWithCertResponseListner.observe(
            this@LoginNumberPasswordFragment,
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
