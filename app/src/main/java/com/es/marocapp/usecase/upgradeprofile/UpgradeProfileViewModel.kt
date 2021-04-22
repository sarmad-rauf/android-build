package com.es.marocapp.usecase.upgradeprofile

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.*
import com.es.marocapp.model.responses.UpgradeProfileResponse
import com.es.marocapp.model.responses.UploadFileResponse
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class UpgradeProfileViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var disposable: Disposable

    var isLoading = ObservableField<Boolean>()

    var errorText = SingleLiveEvent<String>()
    var upgradeProfileFileUploadResponseListener = SingleLiveEvent<UploadFileResponse>()
    var upgradeProfileResponseListener = SingleLiveEvent<UpgradeProfileResponse>()

    fun requestForUpgradeUserProfileFileUploadImage(
        appContext: Context,
        context: String,
        identity: String,
        profile: String,
        reason: String,
        frontImage: String,
        backImage: String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {
            isLoading.set(true)
            disposable =
                ApiClient.newApiClientInstance?.getServerAPI()?.upgradeProfileFileUploadImage(
                    UploadFileRequest(
                        identity,
                        profile,
                        reason,
                        context,
                        FileData(
                            frontImage,
                            ProviderUploadDocumentRequest(
                                "front image",
                                "1111",
                                Constants.CURRENT_USER_MSISDN + "-front-" + System.currentTimeMillis() + ".jpg"
                            )
                        ),
                        FileData(
                            backImage,
                            ProviderUploadDocumentRequest(
                                "back image",
                                "1111",
                                Constants.CURRENT_USER_MSISDN + "-back-" + System.currentTimeMillis() + ".jpg"
                            )
                        )
                    )
                ).compose(applyIOSchedulers())
                    .subscribe(
                        { result ->
                            isLoading.set(false)
                            if (result?.responseCode != null) {
                                when (result?.responseCode) {
                                    ApiConstant.API_SUCCESS -> {
                                        upgradeProfileFileUploadResponseListener.postValue(result)
                                    }
                                    ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                        appContext as MainActivity, LoginActivity::class.java,
                                        LoginActivity.KEY_REDIRECT_USER_SESSION_OUT
                                    )
                                    ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                        appContext as MainActivity, LoginActivity::class.java,
                                        LoginActivity.KEY_REDIRECT_USER_INVALID
                                    )
                                    else -> {
                                        upgradeProfileFileUploadResponseListener.postValue(result)
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
                                    errorText.postValue(appContext.getString(R.string.error_msg_generic) + (error as HttpException).code())
                                }
                            } catch (e: Exception) {
                                errorText.postValue(appContext!!.getString(R.string.error_msg_generic))
                            }
                        })
        } else {
            errorText.postValue(Constants.SHOW_INTERNET_ERROR)
        }
    }

    fun requestForUpgradeUserProfile(context: Context?, reason: String, currentProfile: String) {
        if (Tools.checkNetworkStatus(getApplication())) {
            isLoading.set(true)
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.upgradeProfile(
                UpgradeUserProfileRequest(
                    ApiConstant.CONTEXT_BEFORE_LOGIN, Constants.getNumberMsisdn(
                        Constants.CURRENT_USER_MSISDN
                    ), reason, currentProfile
                )
            ).compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)
                        if (result?.responseCode != null) {
                            when (result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    upgradeProfileResponseListener.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                    context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT
                                )
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                    context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID
                                )
                                else -> {
                                    upgradeProfileResponseListener.postValue(result)
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