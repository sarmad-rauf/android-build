package com.es.marocapp.usecase.login.login


import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentVerifyNumberBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.model.responses.GetAccountHolderInformationResponse
import com.es.marocapp.model.responses.GetOptResponse
import com.es.marocapp.model.responses.RegisterUserResponse
import com.es.marocapp.model.responses.ValidateOtpAndUpdateAliasesResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger
import java.util.regex.Pattern

/**
 * A simple [Fragment] subclass.
 */
class VerifyNumberFragment : BaseFragment<FragmentVerifyNumberBinding>(),
    VerifyOTPClickListner, TextWatcher, View.OnFocusChangeListener {

    lateinit var mActivityViewModel: LoginActivityViewModel
    var isRegFlow = false
    var isOTPRegexMatches = true


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

        mDataBinding.verifyNumberHeader.groupBack.visibility = View.VISIBLE

//        mDataBinding.root.txtBack.setOnClickListener {
//            (activity as LoginActivity).navController.navigateUp()
//        }

        mDataBinding.verifyNumberHeader.imgBackButton.setOnClickListener {
            (activity as LoginActivity).navController.navigateUp()
        }

        mDataBinding.txtOtpNotRecieved.setOnClickListener {
            if(mActivityViewModel.isDeviceChanged){
                mActivityViewModel.requestForGetOtpApi(activity)
            }else{
                //todo Registration Flow Changed OTP Calling
//                mActivityViewModel.requestForGetOTPForRegistrationApi(context,mActivityViewModel.firstName,mActivityViewModel.lastName,mActivityViewModel.identificationNumber)
                mActivityViewModel.requestForGetOtp(activity)
            }
        }


        mDataBinding.inputVerifyOtpBox.itemCount = Constants.APP_OTP_LENGTH
/*        var hint = ""
        for(i in 0 until Constants.APP_OTP_LENGTH){
            hint = "$hint-"
        }

        mDataBinding.inputVerifyOtpBox.hint = hint*/

        mDataBinding.inputVerifyOtpBox.addTextChangedListener(this)
        mDataBinding.inputVerifyOtpBox.setOnFocusChangeListener(this)

        mActivityViewModel.isSimplePopUp = true
        subscribeObserver()
        setStrings()

    }

    private fun setStrings() {
        mDataBinding.verifyNumberHeader.txtHeaderTitle.text = LanguageData.getStringValue("VerifyYourNumber")?.replace(Constants.OTP_LENGTH_PLACEHOLDER_TO_BE_REPLACED,Constants.APP_OTP_LENGTH.toString())
        mDataBinding.tvEnterOTPTitile.text= LanguageData.getStringValue("EnterOTP")

        mDataBinding.txtOtpNotRecieved.text = LanguageData.getStringValue("OTPNotRecieved")+ " "
        mDataBinding.txtResend.text = LanguageData.getStringValue("Resend")
        mDataBinding.txtResend.visibility=View.GONE
        mDataBinding.btnVerifyOtp.text = LanguageData.getStringValue("BtnTitle_Verify")

    }
    fun checkUserRegsitrationAndActicationSenario(response: GetAccountHolderInformationResponse) {
        if (response.accountHolderStatus.equals("ACTIVE", true)) {
            if(!response.profileName.isNullOrEmpty()){
                mActivityViewModel.accountHolderInfoUserProfile = response.profileName
            }
            if (response.credentialList.credentials.isNotEmpty()) {

                for (i in response.credentialList.credentials.indices) {
                    if (response.credentialList.credentials[i].credentialtype.equals(
                            "password",
                            true
                        ) && response.credentialList.credentials[i].credentialstatus.equals(
                            "ACTIVE",
                            true
                        )
                    ) {
                        //this check means that User is Register and in active state with password set for his account so direct login api is called
                        //LoginwithCert APi is called
                        mActivityViewModel.activeUserWithoutPasswordType.set(false)
                        mActivityViewModel.activeUserWithoutPassword.set(false)
                        (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_signUpNumberFragment)
                      //  (activity as LoginActivity).navController.navigate(R.id.action_loginFragment_to_signUpNumberFragment)
                    } else if (response.credentialList.credentials[i].credentialstatus.equals(
                            "BLOCKED",
                            true
                        ) || response.credentialList.credentials[i].credentialstatus.equals(
                            "BLOCK",
                            true
                        )
                    ) {
                        DialogUtils.showBlockedAccountDialog(activity,
                            LanguageData.getStringValue("BtnTitle_ResetPassword"),
                            LanguageData.getStringValue("BtnTitle_Cancel"),
                            LanguageData.getStringValue("BlockedAndResetAccount"),
                            "",
                            object : DialogUtils.OnCustomDialogListner {
                                override fun onCustomDialogOkClickListner() {
                                    mActivityViewModel.isFromLoginUserScreen.set(false)

                                    mActivityViewModel.requestForGetBalanceAndGenerateOtpApi(activity as LoginActivity,mActivityViewModel.accountHolderInfoUserProfile.toString(),
                                        mActivityViewModel.mUserMsisdn)
//                                    (activity as LoginActivity).navController.navigate(R.id.action_loginFragment_to_resetPasswordFragment)
                                }

                            }
                        )
                    } else {
                        // Create Crednetial Api is Called
                        //this check means user is register with state Active but didn't registered Password as his account having credetial type pin
                        mActivityViewModel.accountHolderInfoUserProfile = response.profileName
                        mActivityViewModel.activeUserWithoutPasswordType.set(true)
                        mActivityViewModel.activeUserWithoutPassword.set(false)

                      //  (activity as LoginActivity).navController.navigate(R.id.action_loginFragment_to_setYourPinFragment)
                        (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_setYourPinFragment)
                    }
                }
            } else {
                // Create Crednetial Api is Called
                //this check means user is register with state Active but didn't registered Password as his account having credetial type pin
                mActivityViewModel.accountHolderInfoUserProfile=response.profileName
                mActivityViewModel.activeUserWithoutPasswordType.set(true)
                mActivityViewModel.activeUserWithoutPassword.set(false)

             //   (activity as LoginActivity).navController.navigate(R.id.action_loginFragment_to_setYourPinFragment)
                (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_setYourPinFragment)
            }
        } else {
            mActivityViewModel.accountHolderInfoUserProfile=response.profileName
            //activation Api is called on next screens
            // This Check Means User Register Itself verifies OTP but Close App before setting his/her pin so user is redirected to setup Pin Fragment
            mActivityViewModel.activeUserWithoutPassword.set(true)
            mActivityViewModel.activeUserWithoutPasswordType.set(false)
           // (activity as LoginActivity).navController.navigate(R.id.action_loginFragment_to_setYourPinFragment)
            (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_setYourPinFragment)
        }
    }
    private fun subscribeObserver() {


        val mGetOtpResponseListner = Observer<GetOptResponse> {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
               // do nothing
            } else {
                DialogUtils.showErrorDialoge(activity as LoginActivity, it.description)
            }
        }

        mActivityViewModel.getOTPResponseListner.observe(this, mGetOtpResponseListner)

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

        val mAccountDetailResonseObserver = Observer<GetAccountHolderInformationResponse> {
            if (it.responseCode == ApiConstant.API_SUCCESS) {
                mActivityViewModel.isSignUpFlow.set(false)
                var deviceID = ""
                if (it.deviceId != null) {
                    deviceID = it.deviceId
                    deviceID = deviceID.removePrefix("ID:")
                    deviceID = deviceID.removeSuffix("@device/ALIAS")
                    deviceID = deviceID.trim()
                }

                if(!it.profileName.isNullOrEmpty()){
                    mActivityViewModel.accountHolderInfoUserProfile = it.profileName
                }

                //       if (deviceID.equals(Constants.CURRENT_NUMBER_DEVICE_ID)) {
                checkUserRegsitrationAndActicationSenario(it)
//                } else {
//                    if(checkIfUserIsBlocked(it)){
//                        mActivityViewModel.accountHolderInfoResponse = it
//
//                        mActivityViewModel.previousDeviceId = deviceID
                //                 mActivityViewModel.requestForGetOtpApi(activity)
//                    }
//                }

                if(!it.language.isNullOrEmpty()) {
                    LocaleManager.languageToBeChangedAfterAPI = it.language
                }
            }
            else if (it.responseCode == ApiConstant.API_FAILURE) {
                isRegFlow = true
                if(!it.profileName.isNullOrEmpty()){
                    mActivityViewModel.accountHolderInfoUserProfile = it.profileName
                }
                /* mActivityViewModel.isSignUpFlow.set(true)
                 mActivity.navController.navigate(R.id.action_loginFragment_to_signUpDetailFragment)*/
            }
            else if (it.responseCode == ApiConstant.API_ACCOUNT_BLOCKED)
            {
                if(!it.profileName.isNullOrEmpty()){
                    mActivityViewModel.accountHolderInfoUserProfile = it.profileName
                }
                val btnTxt = LanguageData.getStringValue("BtnTitle_OK")
                val titleTxt = LanguageData.getStringValue("AccountBlocked")
                var descriptionTxt = it.description
                val helpLineNumber = if(Constants.IS_AGENT_USER) Constants.HELPLINENUMBERAGENT else Constants.HELPLINE_NUMBER
                descriptionTxt = descriptionTxt.replace("<HELPLINE>",helpLineNumber)
                DialogUtils.showCustomDialogue(
                    activity,
                    btnTxt,
                    descriptionTxt,
                    titleTxt,
                    object : DialogUtils.OnCustomDialogListner {
                        override fun onCustomDialogOkClickListner() {
                        }


                    })
            }else if(it.responseCode.equals(ApiConstant.API_WRONG_ATTEMPT_BLOCKED)){
                if(!it.profileName.isNullOrEmpty()){
                    mActivityViewModel.accountHolderInfoUserProfile = it.profileName
                }
                DialogUtils.showBlockedAccountDialog(activity,LanguageData.getStringValue("BtnTitle_ResetPassword"),LanguageData.getStringValue("BtnTitle_Cancel"),
                    LanguageData.getStringValue("BlockedAndResetAccount"),"",object : DialogUtils.OnCustomDialogListner{
                        override fun onCustomDialogOkClickListner() {
                            mActivityViewModel.isFromLoginUserScreen.set(true)
                          //  mDataBinding.inputPin.setText("")

                            mActivityViewModel.requestForGetBalanceAndGenerateOtpApi(activity as LoginActivity,mActivityViewModel.accountHolderInfoUserProfile.toString(),
                                mActivityViewModel.mUserMsisdn)
//                            (activity as LoginActivity).navController.navigate(R.id.action_loginFragment_to_resetPasswordFragment)
                        }

                    }
                )
            }else if (it.responseCode == ApiConstant.DEVICE_ID_MIS_MATCHED)
            {
                mActivityViewModel.requestForGetOtpApi(activity)
            }
            else {
                DialogUtils.showErrorDialoge(activity, it.description)
            }
        }
        mActivityViewModel.getAccountDetailResponseListner.observe(
            this,
            mAccountDetailResonseObserver
        )
        //todo Registration Flow Changed Navigation From OTP to SignUpDetailsFragment
        mActivityViewModel.getVerifyOtpResponseListner.observe(this@VerifyNumberFragment, Observer {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_signUpDetailFragment)
            } else {
                DialogUtils.showErrorDialoge(activity as LoginActivity, it.description)
            }
        })

        mActivityViewModel.getOtpForRegistrationResponseListner.observe(this@VerifyNumberFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_FAILURE)){
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            })

        mActivityViewModel.getSimppleOtpForRegistrationResponseListner.observe(this@VerifyNumberFragment, Observer {
            if(it.responseCode.equals(ApiConstant.API_FAILURE)){
                DialogUtils.showErrorDialoge(activity,it.description)
            }
        })

        mActivityViewModel.getRegisterUserResponseListner.observe(
            this,
            mRegisterUserResonseObserver
        )

       mActivityViewModel.getValidateOtpAndUpdateAliasResponseListner.observe(this, Observer {
           if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
               checkUserRegsitrationAndActicationSenario()
           }else{
               DialogUtils.showErrorDialoge(activity as LoginActivity,it.description)
           }
       })
    }
    override fun onOTPVerifyClick(view: View) {
       /* if (mDataBinding.inputVerifyOtp.text.isNullOrEmpty()) {
            mDataBinding.inputLayoutVerifyOtp.error = LanguageData.getStringValue("PleaseEnterValidOTP")
            mDataBinding.inputLayoutVerifyOtp.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutVerifyOtp.error = ""
            mDataBinding.inputLayoutVerifyOtp.isErrorEnabled = false

            if(isOTPRegexMatches){
                mDataBinding.inputLayoutVerifyOtp.error = ""
                mDataBinding.inputLayoutVerifyOtp.isErrorEnabled = false
                if(mActivityViewModel.isDeviceChanged){
                    mActivityViewModel.requestForVerifyOtpAndUpdateAliaseAPI(
                        activity,
                        mActivityViewModel.previousDeviceId,
                        Constants.CURRENT_NUMBER_DEVICE_ID,
                        mDataBinding.inputVerifyOtp.text.toString().trim()
                    )
                }else{
                    mActivityViewModel.requestForRegisterUserApi(
                        activity,
                        Constants.CURRENT_NUMBER_DEVICE_ID,
                        mDataBinding.inputVerifyOtp.text.toString().trim()
                    )
                }
            }else{
                mDataBinding.inputLayoutVerifyOtp.error = LanguageData.getStringValue("PleaseEnterValidOTP")
                mDataBinding.inputLayoutVerifyOtp.isErrorEnabled = true
            }
        }*/

        if(isOTPRegexMatches){
            if(mActivityViewModel.isDeviceChanged){

                mActivityViewModel.requestForVerifyOtpAndUpdateAliaseAPI(
                    activity,
                    Constants.CURRENT_NUMBER_DEVICE_ID,
                    mDataBinding.inputVerifyOtpBox.text.toString().trim()
                )
            }else{
                //todo Registration Flow Changed OTP Verify Call
                mActivityViewModel.requestForVerifyOtp(
                    activity,
                    mDataBinding.inputVerifyOtpBox.text.toString().trim()
                )
                /*mActivityViewModel.requestForRegisterUserApi(
                    activity,
                    Constants.CURRENT_NUMBER_DEVICE_ID,
                    mDataBinding.inputVerifyOtpBox.text.toString().trim()
                )*/
            }
        }else{
            DialogUtils.showUpdateAPPDailog(activity,LanguageData.getStringValue("PleaseEnterValidOTP"),object : DialogUtils.OnCustomDialogListner{
                override fun onCustomDialogOkClickListner() {

                }

            },R.drawable.update_blue)
        }
    }

    fun checkUserRegsitrationAndActicationSenario() {
        val response = mActivityViewModel.accountHolderInfoResponse
        Logger.debugLog("Abro","result ${response.toString()}")
        if (response.accountHolderStatus.equals("ACTIVE", true)) {
            if (response.credentialList.credentials.isNotEmpty()) {

                for (i in response.credentialList.credentials.indices) {
                    if (response.credentialList.credentials[i].credentialtype.equals(
                            "password",
                            true
                        ) && response.credentialList.credentials[i].credentialstatus.equals(
                            "ACTIVE",
                            true
                        )
                    ) {
                        // this check means that User is Register and in active state with password set for his account so direct login api is called
                        //LoginwithCert APi is called
                        mActivityViewModel.activeUserWithoutPasswordType.set(false)
                        mActivityViewModel.activeUserWithoutPassword.set(false)

                        (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_signUpNumberFragment)
                    } else if(response.credentialList.credentials[i].credentialstatus.equals("BLOCKED",true) || response.credentialList.credentials[i].credentialstatus.equals("BLOCK",true)){
                        DialogUtils.showErrorDialoge(activity,"User is Blocked")
                    }
                    else{
                        // Create Crednetial Api is Called
                        //this check means user is register with state Active but didn't registered Password as his account having credetial type pin

                        mActivityViewModel.activeUserWithoutPasswordType.set(true)
                        mActivityViewModel.activeUserWithoutPassword.set(false)

                        (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_setYourPinFragment)
                    }
                }
            }else{
                // Create Crednetial Api is Called
                //this check means user is register with state Active but didn't registered Password as his account having credetial type pin

                mActivityViewModel.activeUserWithoutPasswordType.set(true)
                mActivityViewModel.activeUserWithoutPassword.set(false)

                (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_setYourPinFragment)
            }
        } else {
            //activation Api is called on next screens
            // This Check Means User Register Itself verifies OTP but Close App before setting his/her pin so user is redirected to setup Pin Fragment
            mActivityViewModel.activeUserWithoutPassword.set(true)
            mActivityViewModel.activeUserWithoutPasswordType.set(false)

            (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_setYourPinFragment)
        }
    }

    override fun afterTextChanged(p0: Editable?) {
        var otp = mDataBinding.inputVerifyOtpBox.text.toString().trim()
        var otpLenght = otp.length
        /*isOTPRegexMatches =
            (otpLenght > 0 && otpLenght==Constants.APP_OTP_LENGTH && Pattern.matches(Constants.APP_OTP_REGEX, otp))*/
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onFocusChange(p0: View?, hasFocus: Boolean) {
        if(hasFocus){
            mDataBinding.tvEnterOTPTitile.setTextColor(requireActivity().resources.getColor(R.color.colorCerulean))
        }else{
            mDataBinding.tvEnterOTPTitile.setTextColor(requireActivity().resources.getColor(R.color.colorBlack))
        }
    }

}
