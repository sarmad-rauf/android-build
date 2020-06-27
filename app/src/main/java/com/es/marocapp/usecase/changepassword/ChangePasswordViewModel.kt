package com.es.marocapp.usecase.changepassword

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.ChangePasswordRequest
import com.es.marocapp.model.responses.ChangePasswordResponse
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

class ChangePasswordViewModel(application: Application) : AndroidViewModel(application) {

    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()
    lateinit var disposable: Disposable

    var getChangePassResponseListner = SingleLiveEvent<ChangePasswordResponse>()


    // API For ChangeUserPassword
    fun requestForChangePasswordAPI(
        context: Context?,
        oldCredential : String,
        newCredential : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getChangePasswordCall(
                ChangePasswordRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),
                    EncryptionUtils.encryptString(oldCredential),
                    EncryptionUtils.encryptString(newCredential),Constants.SECRET_TYPE)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) 
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getChangePassResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getChangePassResponseListner.postValue(result)
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