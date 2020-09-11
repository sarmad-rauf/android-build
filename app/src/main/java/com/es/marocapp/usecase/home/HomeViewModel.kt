package com.es.marocapp.usecase.home

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.es.marocapp.R
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.model.requests.GetAccountHolderInformationRequest
import com.es.marocapp.model.requests.SetDefaultAccountRequest
import com.es.marocapp.model.requests.VerifyOTPForDefaultAccountRequest
import com.es.marocapp.model.responses.AccountHolderAdditionalInformationResponse
import com.es.marocapp.model.responses.SetDefaultAccountResponse
import com.es.marocapp.model.responses.VerifyOTPForDefaultAccountResponse
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.security.EncryptionUtils
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()
    var getAccountHolderAdditionalInfoResponseListner = SingleLiveEvent<AccountHolderAdditionalInformationResponse>()
    var setDefaultAccountResponseListener = SingleLiveEvent<SetDefaultAccountResponse>()
    var verifyOTPForDefaultAccountResponseListener = SingleLiveEvent<VerifyOTPForDefaultAccountResponse>()

    lateinit var disposable: Disposable

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    // API For CheckDefaultAccountStatus API
    fun requestForAccountHolderAddtionalInformationApi(
        context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAccountHolderAddtionalInfoCall(
                GetAccountHolderInformationRequest(ApiConstant.CONTEXT_BEFORE_LOGIN, Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getAccountHolderAdditionalInfoResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getAccountHolderAdditionalInfoResponseListner.postValue(result)
                                }
                            }
                            

                        } else {
                            errorText.postValue(result?.description)
                        }


                    },
                    { error ->
                        isLoading.set(false)

                        //Display Error Result Code with with Configure Message
                        try {
                            if (context != null && error != null) {
                                errorText.postValue(context.getString(R.string.error_msg_generic) + (error as HttpException).code())
                            }
                        } catch (e: Exception) {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
                        }

                    })


        } else {

            errorText.postValue(Constants.SHOW_INTERNET_ERROR)
        }

    }

    // API For SetDefaultAccount
    fun requestForSetDefaultAccount(context: Context?)
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            var receiver=""
            if(Constants.IS_MERCHANT_USER) {
                receiver=Constants.getMerchantReceiverAlias(Constants.CURRENT_USER_MSISDN)
            }
            else if(Constants.IS_AGENT_USER){
                receiver=Constants.getAgentReceiverAlias(Constants.CURRENT_USER_MSISDN)
            }
            else if (Constants.IS_CONSUMER_USER){
                receiver=Constants.getTransferReceiverAlias(Constants.CURRENT_USER_MSISDN)
            }

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.setDefaultAccountStatus(
                SetDefaultAccountRequest(ApiConstant.CONTEXT_AFTER_LOGIN, receiver,"enrollment",
                    LocaleManager.selectedLanguage,Constants?.balanceInfoAndResponse?.profilename,Constants.CURRENT_USER_FIRST_NAME,Constants.CURRENT_USER_LAST_NAME)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    setDefaultAccountResponseListener.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    setDefaultAccountResponseListener.postValue(result)
                                }
                            }

                        } else {
                            errorText.postValue(result?.description)
                        }


                    },
                    { error ->
                        isLoading.set(false)

                        //Display Error Result Code with with Configure Message
                        try {
                            if (context != null && error != null) {
                                errorText.postValue(context.getString(R.string.error_msg_generic) + (error as HttpException).code())
                            }
                        } catch (e: Exception) {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
                        }

                    })


        } else {

            errorText.postValue(Constants.SHOW_INTERNET_ERROR)
        }

    }

    // API For VerifyOTPForSetDefaultAccount
    fun requestForVerifyOTPForSetDefaultAccount(
        context: Context?,
        referenceNumber: String,
        otp: String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            var receiver=""
            if(Constants.IS_MERCHANT_USER) {
                receiver=Constants.getMerchantReceiverAlias(Constants.CURRENT_USER_MSISDN)
            }
            else if(Constants.IS_AGENT_USER){
                receiver=Constants.getAgentReceiverAlias(Constants.CURRENT_USER_MSISDN)
            }
            else if (Constants.IS_CONSUMER_USER){
                receiver=Constants.getTransferReceiverAlias(Constants.CURRENT_USER_MSISDN)
            }

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.verifyOTPforSetDefaultAccountStatus(
                VerifyOTPForDefaultAccountRequest(ApiConstant.CONTEXT_AFTER_LOGIN, receiver,"confirm",LocaleManager.selectedLanguage,Constants?.balanceInfoAndResponse?.profilename,referenceNumber,
                    EncryptionUtils.encryptString(otp),Constants.CURRENT_USER_FIRST_NAME,Constants.CURRENT_USER_LAST_NAME)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    verifyOTPForDefaultAccountResponseListener.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    verifyOTPForDefaultAccountResponseListener.postValue(result)
                                }
                            }
                        } else {
                            errorText.postValue(result?.description)
                        }


                    },
                    { error ->
                        isLoading.set(false)

                        //Display Error Result Code with with Configure Message
                        try {
                            if (context != null && error != null) {
                                errorText.postValue(context.getString(R.string.error_msg_generic) + (error as HttpException).code())
                            }
                        } catch (e: Exception) {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
                        }

                    })


        } else {

            errorText.postValue(Constants.SHOW_INTERNET_ERROR)
        }

    }
}