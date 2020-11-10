package com.es.marocapp.usecase

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.BalanceInfoAndLimtRequest
import com.es.marocapp.model.requests.GetApprovalRequest
import com.es.marocapp.model.requests.LogoutUserRequest
import com.es.marocapp.model.requests.UpdateLanguageRequest
import com.es.marocapp.model.responses.BalanceInfoAndLimitResponse
import com.es.marocapp.model.responses.GetApprovalsResponse
import com.es.marocapp.model.responses.LogOutUserResponse
import com.es.marocapp.model.responses.UpdateLanguageResponse
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class MainActivityViewModel(application: Application) : AndroidViewModel(application){

    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()
    var getLogOutUserResponseListner = SingleLiveEvent<LogOutUserResponse>()
    var updateLanguageResponseListener = SingleLiveEvent<UpdateLanguageResponse>()
    var getBalanceInforAndLimitResponseListner = SingleLiveEvent<BalanceInfoAndLimitResponse>()

    //Request For Log Out User
    fun requestForLogOutUserApi(context: Context?)
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getLogOutUserCall(
                LogoutUserRequest(ApiConstant.CONTEXT_AFTER_LOGIN)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if(result?.responseCode != null){
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS ->  getLogOutUserResponseListner.postValue(result)
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  getLogOutUserResponseListner.postValue(result)
                            }
                        }
                        else{
                            getLogOutUserResponseListner.postValue(result)
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

    // API For CheckDefaultAccountStatus API
    fun requestForChangeLanguage(context: Context?,languageToBeUpdated:String)
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.updateLanguage(
                UpdateLanguageRequest(ApiConstant.CONTEXT_BEFORE_LOGIN, Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),"changing language",languageToBeUpdated)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    updateLanguageResponseListener.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    updateLanguageResponseListener.postValue(result)
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

    // API For BalanceInfoAndLimit API
    fun requestForBalanceInfoAndLimtsAPI(
        context: Context?
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBalancesInfoAndLimtCall(
                BalanceInfoAndLimtRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null )
                        {
                            getBalanceInforAndLimitResponseListner.postValue(result)

                        } else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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