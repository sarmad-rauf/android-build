package com.es.marocapp.usecase.splash

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.es.marocapp.R
import com.es.marocapp.model.requests.GetPreLoginDataRequest
import com.es.marocapp.model.responses.GetPreLoginDataResponse
import com.es.marocapp.model.responses.translations.TranslationApiResponse
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException


class SplashActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val SPLASH_DISPLAY_LENGTH = 1000
    val mHandler = MutableLiveData<Boolean>()
    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = MutableLiveData<String>()
    var preLoginDataResponseListener = MutableLiveData<GetPreLoginDataResponse>()
    var translationApiResponseListener = MutableLiveData<TranslationApiResponse>()

    init {

//        postDelay()

    }

    fun requestForGetPreLoginDataApi(context: Context?, versionName: String) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getPreLoginData(
                GetPreLoginDataRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,versionName)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null ) {
                            preLoginDataResponseListener.postValue(result)
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

    fun requestForTranslationsApi(context: Context?) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getTranslations(

            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null )
                        {
                            translationApiResponseListener.postValue(result)

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

    private fun postDelay() {

        android.os.Handler().postDelayed(Runnable {

            mHandler.postValue(true)

        }, SPLASH_DISPLAY_LENGTH.toLong())


    }


}