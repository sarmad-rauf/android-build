package com.es.marocapp.usecase.qrcode

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.GetAccountHolderInformationRequest
import com.es.marocapp.model.responses.AccountHolderAdditionalInformationResponse
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class GenerateQRViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()
    var getAccountHolderAdditionalInfoResponseListner =
        SingleLiveEvent<AccountHolderAdditionalInformationResponse>()

    fun requestForAccountHolderAddtionalInformationApi(
        context: Context?
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {
            isLoading.set(true)
            disposable =
                ApiClient.newApiClientInstance?.getServerAPI()?.getAccountHolderAddtionalInfoCall(
                    GetAccountHolderInformationRequest(
                        ApiConstant.CONTEXT_BEFORE_LOGIN,
                        Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN)
                    )
                ).compose(applyIOSchedulers())
                    .subscribe(
                        { result ->
                            isLoading.set(false)
                            if (result?.responseCode != null) {
                                when (result?.responseCode) {
                                    ApiConstant.API_SUCCESS -> {
                                        getAccountHolderAdditionalInfoResponseListner.postValue(
                                            result
                                        )
                                    }
                                    ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                        context as SendMoneyActivity, LoginActivity::class.java,
                                        LoginActivity.KEY_REDIRECT_USER_SESSION_OUT
                                    )
                                    ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                        context as SendMoneyActivity, LoginActivity::class.java,
                                        LoginActivity.KEY_REDIRECT_USER_INVALID
                                    )
                                    else -> {
                                        getAccountHolderAdditionalInfoResponseListner.postValue(
                                            result
                                        )
                                    }
                                }
                            } else {
                                errorText.postValue(Constants.SHOW_SERVER_ERROR)
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