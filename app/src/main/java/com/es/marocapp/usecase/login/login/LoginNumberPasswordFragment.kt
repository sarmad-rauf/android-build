package com.es.marocapp.usecase.login.login


import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Paint
import android.os.Bundle
import android.text.InputFilter
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.BuildConfig

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentLoginNumberPasswordBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.model.responses.Account
import com.es.marocapp.model.responses.BalanceInfoAndLimitResponse
import com.es.marocapp.model.responses.LoginWithCertResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.security.EncryptionUtils
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger
import com.es.marocapp.utils.PrefUtils
import com.es.marocapp.utils.PrefUtils.PreKeywords.PREF_KEY_USER_MSISDN

/**
 * A simple [Fragment] subclass.
 */
class LoginNumberPasswordFragment : BaseFragment<FragmentLoginNumberPasswordBinding>(), LoginClickListener {

    lateinit var mActivityViewModel: LoginActivityViewModel
    var isPasswordVisible = false
    var isRememberMeEnabled=true

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
        (activity as LoginActivity).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        var numberToShow = mActivityViewModel.mUserMsisdn
        numberToShow = numberToShow.substringAfter(Constants.APP_MSISDN_PREFIX)
        numberToShow = numberToShow.substringAfter("212")
        numberToShow = numberToShow.substringAfter("+212")
        numberToShow = "0$numberToShow"
        mDataBinding.inputPhoneNumber.setText(numberToShow)
        mActivityViewModel.isNewUserRegisterd.set(false)

        mDataBinding.imgPasswordStatusIcon.setImageResource(R.drawable.ic_hide_password)
        mDataBinding.imgPasswordStatusIcon.setOnClickListener {
            if(isPasswordVisible){
                mDataBinding.inputPin.transformationMethod = HideReturnsTransformationMethod.getInstance()
                mDataBinding.imgPasswordStatusIcon.setImageResource(R.drawable.ic_show_password)
                isPasswordVisible = false
            }else{
                mDataBinding.inputPin.transformationMethod = PasswordTransformationMethod.getInstance()
                mDataBinding.imgPasswordStatusIcon.setImageResource(R.drawable.ic_hide_password)
                isPasswordVisible = true
            }
        }

//        mDataBinding.root.groupBack.visibility = View.VISIBLE
//        mDataBinding.root.imgBackButton.setOnClickListener {
//            (activity as LoginActivity).finish()
//        }


        mDataBinding.inputPin.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.APP_MAX_PASSWORD_LENGTH))

        if(mActivityViewModel.isUserToShowProfile){
            mDataBinding.loginHeader.currentLoggedInUserGroup.visibility = View.VISIBLE
            mDataBinding.loginHeader.LoggedInHeaderTitle.visibility = View.GONE
            mDataBinding.rememberMeToggle.visibility=View.GONE
            mDataBinding.rememberMeTv.visibility=View.GONE

            if(Constants.CURRENT_USER_NAME.isNullOrEmpty()){
                mDataBinding.loginHeader.currentLoggedInUser.text = ""
            }else{
                mDataBinding.loginHeader.currentLoggedInUser.text = Constants.CURRENT_USER_NAME
            }

            mDataBinding.phoneNumberFiedlGroup.visibility = View.GONE
        }else{
            mDataBinding.loginHeader.currentLoggedInUserGroup.visibility = View.GONE
            mDataBinding.loginHeader.LoggedInHeaderTitle.visibility = View.VISIBLE

            mDataBinding.phoneNumberFiedlGroup.visibility = View.VISIBLE
            mDataBinding.rememberMeToggle.visibility=View.VISIBLE
            mDataBinding.rememberMeTv.visibility=View.VISIBLE

        }

        mDataBinding.userAnotherAccountTitle.setOnClickListener {
            (activity as LoginActivity).navController.navigate(R.id.loginFragment)
        }

        mDataBinding.userAnotherAccountIcon.setOnClickListener {
            (activity as LoginActivity).navController.navigate(R.id.loginFragment)
        }

        mDataBinding.rememberMeToggle.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                isRememberMeEnabled=isChecked
            }
            else{
                isRememberMeEnabled=isChecked
            }
        })
        mDataBinding.rememberMeToggle.isChecked=true

        mActivityViewModel.isSimplePopUp = false
        subscribeObserver()
        setStrings()

        //for Testing Purpose
       /* DialogUtils.showDefaultAccountOTPDialogue(activity,object : DialogUtils.OnOTPDialogClickListner{
            override fun onOTPDialogYesClickListner(password: String) {
                Toast.makeText(activity as LoginActivity,"Successfully Passed use case",Toast.LENGTH_SHORT).show()
            }

            override fun onOTPDialogNoClickListner() {

            }

        })*/

    }

    private fun setStrings() {
        mDataBinding.userAnotherAccountTitle.text= LanguageData.getStringValue("UseAnotheMtcashAccount")
        mDataBinding.loginHeader.LoggedInHeaderTitle.text= LanguageData.getStringValue("LoginIntoAccount")
        mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterMobileNumber")
        mDataBinding.inputLayoutPin.hint = LanguageData.getStringValue("Password")
        mDataBinding.txtForgotPin.paintFlags = mDataBinding.txtForgotPin.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        mDataBinding.txtForgotPin.text = LanguageData.getStringValue("ForgotPasswordQuestion")
        mDataBinding.txtAreYouNew.paintFlags = mDataBinding.txtAreYouNew.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        mDataBinding.txtAreYouNew.text = LanguageData.getStringValue("AreYouNew")
        mDataBinding.rememberMeTv.text = LanguageData.getStringValue("RememberMe")
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
                        Constants.getAccountsResponseArray=it.accounts as ArrayList<Account>
                       /* if(it.accounts[i].accountType.equals(Constants.TYPE_COMMISSIONING,true)){
                            Constants.getAccountsResponse = it.accounts[i]
                        }*/
                    }
                    if(isRememberMeEnabled){
                        PrefUtils.addString(requireActivity(),PREF_KEY_USER_MSISDN,Constants.CURRENT_USER_MSISDN)
                        PrefUtils.addString(
                            requireActivity(),
                            PrefUtils.PreKeywords.PREF_KEY_USER_NAME,
                            Constants.CURRENT_USER_NAME
                        )
                    }

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
                var userName = it?.firstname + " " + it?.surname
                Constants.CURRENT_USER_NAME = userName
                if(Constants.IS_AGENT_USER){
                    Constants.balanceInfoAndResponse = it
                    mActivityViewModel.requestForGetAccountsAPI(activity)
                }else{
                    Constants.balanceInfoAndResponse = it
                    if(isRememberMeEnabled) {
                        PrefUtils.addString(
                            requireActivity(),
                            PREF_KEY_USER_MSISDN,
                            Constants.CURRENT_USER_MSISDN
                        )
                        PrefUtils.addString(
                            requireActivity(),
                            PrefUtils.PreKeywords.PREF_KEY_USER_NAME,
                            Constants.CURRENT_USER_NAME
                        )
                    }
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
                    Constants.mContactListArray.clear()
                    Constants.mContactListArray.addAll(Constants.loginWithCertResponse.contactList)
                }
                //setting cookie for use in header
                if (it.setCookie.isNotEmpty()) {
                    Logger.debugLog("login","setting Login Cookie")
                    var cookie = it.setCookie
                    Constants.setBase64EncodedString(cookie)
                    Constants.LOGGED_IN_USER_COOKIE= EncryptionUtils.encryptString(Constants.LOGGED_IN_USER_COOKIE)
                    Constants.LOGGED_IN_USER = mActivityViewModel.mUserMsisdn
                }
                Constants.HEADERS_AFTER_LOGINS = true
                Constants.HEADERS_FOR_PAYEMNTS=false
              if(  it.getAccountHolderInformationResponse.language!=null){
                LocaleManager.languageToBeChangedAfterAPI = it.getAccountHolderInformationResponse?.language
              }
                if(!LocaleManager.languageToBeChangedAfterAPI.isNullOrEmpty()){
                    LocaleManager.selectedLanguage=LocaleManager.languageToBeChangedAfterAPI
                    LocaleManager.languageToBeChangedAfterAPI=""
                }
                if(it.getAccountHolderInformationResponse!=null){
                    if(!it.getAccountHolderInformationResponse.firstName.isNullOrEmpty()){
                        Constants.CURRENT_USER_NAME=it.getAccountHolderInformationResponse.firstName
                        Constants.CURRENT_USER_FIRST_NAME=it.getAccountHolderInformationResponse.firstName
                        if(!it.getAccountHolderInformationResponse.sureName.isNullOrEmpty()){
                            Constants.CURRENT_USER_NAME=it.getAccountHolderInformationResponse.firstName+ it.getAccountHolderInformationResponse.sureName
                            Constants.CURRENT_USER_LAST_NAME=it.getAccountHolderInformationResponse.sureName
                        }
                    }

                    //new default notification Email getting
                        mActivityViewModel.requestForAccountholderDefaultNotificationEmailAPI(activity)
                }
                mActivityViewModel.requestForBalanceInfoAndLimtsAPI(activity)
                mActivityViewModel.requestForGetFavouriteApi(activity)
            }
            else if(it.responseCode == ApiConstant.API_ACCOUNT_BLOCKED){
                val btnTxt = LanguageData.getStringValue("BtnTitle_Okay")
                val titleTxt = LanguageData.getStringValue("AccountBlocked")
                val descriptionTxt =it.description
                DialogUtils.showCustomDialogue(activity,btnTxt,descriptionTxt,titleTxt,object : DialogUtils.OnCustomDialogListner{
                    override fun onCustomDialogOkClickListner() {
                        (activity as LoginActivity).navController.popBackStack(R.id.loginFragment,false)
                    }
                })

//                DialogUtils.showBlockedAccountDialog(activity,btnTxt,LanguageData.getStringValue("BtnTitle_Cancel"),
//                    descriptionTxt,titleTxt,object : DialogUtils.OnCustomDialogListner{
//                        override fun onCustomDialogOkClickListner() {
//                            (activity as LoginActivity).navController.popBackStack(R.id.loginFragment,false)
//                        }
//                    }
//                )
            }
            else if(it.responseCode.equals(ApiConstant.API_WRONG_ATTEMPT_BLOCKED)){
                // BtnTitle_Okay
                DialogUtils.showBlockedAccountDialog(activity,LanguageData.getStringValue("BtnTitle_Okay"),LanguageData.getStringValue("BtnTitle_Cancel"),
                LanguageData.getStringValue("BlockedAndResetAccount"),"",object : DialogUtils.OnCustomDialogListner{
                        override fun onCustomDialogOkClickListner() {
                            mActivityViewModel.isFromLoginUserScreen.set(true)
                            mDataBinding.inputPin.setText("")
                            mActivityViewModel.requestForGetProfileApi(activity as LoginActivity, mActivityViewModel.mUserMsisdn)
                        //  (activity as LoginActivity).navController.navigate(R.id.action_signUpNumberFragment_to_resetPasswordFragment)
                        }
                    }
                )
            }
            else {
                Constants.HEADERS_AFTER_LOGINS = false
                Constants.LOGGED_IN_USER = ""
                DialogUtils.showErrorDialoge(activity as LoginActivity,it.description)
            }
        }

        mActivityViewModel.getProfileResponseListner.observe(this@LoginNumberPasswordFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(!it.profileName.isNullOrEmpty()){
                        mActivityViewModel.accountHolderInfoUserProfile = it.profileName
                        Constants.UserProfileName=it.profileName
                        if(it.userType.contains("agent"))
                        {
                            Constants.IS_AGENT_USER=true
                        }
                    }
                    mActivityViewModel.requestForGetBalanceAndGenerateOtpApi(activity as LoginActivity,mActivityViewModel.accountHolderInfoUserProfile.toString(),
                        mActivityViewModel.mUserMsisdn)

                }else{
                    DialogUtils.showErrorDialoge(activity as LoginActivity,it.description)
                }
            })

        mActivityViewModel.getBalanceAndGenerateOtpResponseListner.observe(this@LoginNumberPasswordFragment, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                mActivityViewModel.isForgotPasswordDialogToShow = false
                (activity as LoginActivity).navController.navigate(R.id.action_signUpNumberFragment_to_forgotPasswordFragment)
            }else{
                mActivityViewModel.isForgotPasswordDialogToShow = true
                (activity as LoginActivity).navController.navigate(R.id.action_signUpNumberFragment_to_forgotPasswordFragment)
            }
        })
        mActivityViewModel.getAccountHolderEmailResponseListner.observe(this@LoginNumberPasswordFragment, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                if(!it.email.isNullOrEmpty()) {
                    Constants.CURRENT_USER_EMAIL = it.email
                    Constants.CURRENT_USER_EMAIL = Constants.CURRENT_USER_EMAIL.replace("ID:", "")
                    Constants.CURRENT_USER_EMAIL =
                        Constants.CURRENT_USER_EMAIL.replace("/EMAIL", "")
                }else  Constants.CURRENT_USER_EMAIL = ""
            }else{
                Constants.CURRENT_USER_EMAIL = ""
            }
        })
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
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
            mActivityViewModel.requestForLogigWithCertAPI(
                activity,
                mDataBinding.inputPin.text.toString().trim(),
                BuildConfig.VERSION_NAME
            )
        }
    }

    override fun onForgotPinClick(view: View) {
        mActivityViewModel.requestForGetProfileApi(activity as LoginActivity, mActivityViewModel.mUserMsisdn)
//        (activity as LoginActivity).navController.navigate(R.id.action_signUpNumberFragment_to_forgotPasswordFragment)
    }

    override fun onSignUpClick(view: View) {
    }

    override fun onAreYouNewClick(view: View) {
        (activity as LoginActivity).navController.navigate(R.id.loginFragment)
    }
    override fun onTermsConditionsClick(view: View) {
    }

}
