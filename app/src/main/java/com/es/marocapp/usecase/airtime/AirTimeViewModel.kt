package com.es.marocapp.usecase.airtime

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.GetAirTimeUseCasesRequest
import com.es.marocapp.model.requests.InitiateTransferQuoteRequest
import com.es.marocapp.model.responses.GetAirTimeUseCasesResponse
import com.es.marocapp.model.responses.InitiateTransferQuoteResponse
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class AirTimeViewModel(application: Application) : AndroidViewModel(application){

    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()

    var popBackStackTo = -1

    var isRechargeFixeUseCase = ObservableField<Boolean>()
    var isRechargeMobileUseCase = ObservableField<Boolean>()
    var airTimeSelected = ObservableField<String>()

    var getAirTimeUseCasesResponseListner = SingleLiveEvent<GetAirTimeUseCasesResponse>()

    //Request For InitiateTrasnferQoute
    fun requestForAirTimeUseCasesApi(context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAirTimeUseCasesCall(
                GetAirTimeUseCasesRequest(ApiConstant.CONTEXT_AFTER_LOGIN)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getAirTimeUseCasesResponseListner.postValue(result)

                        } else {
                            getAirTimeUseCasesResponseListner.postValue(result)
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