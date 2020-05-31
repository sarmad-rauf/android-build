package com.es.marocapp.usecase.pin

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.BalanceInfoAndLimtRequest
import com.es.marocapp.model.requests.ChangePasswordRequest
import com.es.marocapp.model.responses.ChangePasswordResponse
import com.es.marocapp.model.responses.GetAccountHolderInformationResponse
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class PinViewModel(application: Application) : AndroidViewModel(application) {

    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()
    lateinit var disposable: Disposable

    var getChangePassResponseListner = SingleLiveEvent<ChangePasswordResponse>()


    // API For ChangeUserPassword
    fun requestForCahngePasswordAPI(
        context: Context?,
        oldCredential : String,
        newCredential : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            //todo remove hardcoded value
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getChangePasswordCall(
                ChangePasswordRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),
                oldCredential,newCredential,Constants.SECRET_TYPE)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getChangePassResponseListner.postValue(result)

                        } else {
                            errorText.postValue(Constants.SHOW_SERVER_ERROR)
                        }


                    },
                    { error ->
                        isLoading.set(false)

                        //Display Error Result Code with with Configure Message
                        try {
                            if (context != null && error != null) {
                                errorText.postValue(context.getString(R.string.error_msg_network) + (error as HttpException).code())
                            }
                        } catch (e: Exception) {
                            errorText.postValue(context!!.getString(R.string.error_msg_network))
                        }

                    })


        } else {

            errorText.postValue(Constants.SHOW_INTERNET_ERROR)
        }

    }
}