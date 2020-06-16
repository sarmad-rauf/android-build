package com.es.marocapp.usecase.airtime

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.AirTimeQuoteRequest
import com.es.marocapp.model.requests.AirTimeRequest
import com.es.marocapp.model.requests.GetAirTimeUseCasesRequest
import com.es.marocapp.model.responses.AirTimeQuoteResponse
import com.es.marocapp.model.responses.AirTimeResponse
import com.es.marocapp.model.responses.GetAirTimeUseCasesResponse
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

    var amountToTransfer = ""
    var transferdAmountTo = ""
    var feeAmount = ""
    var qouteId = ""
    var transactionID = ""
    var senderBalanceAfter ="0.00"

    var isRechargeFixeUseCase = ObservableField<Boolean>()
    var isRechargeMobileUseCase = ObservableField<Boolean>()
    var airTimeSelected = ObservableField<String>()
    var airTimePlanSelected = ObservableField<String>()
    var airTimeSelectedPlanCodeSelected = ObservableField<String>()
    var airTimeAmountSelected = ObservableField<String>()

    var mAirTimeUseCaseResponse = ObservableField<GetAirTimeUseCasesResponse>()

    var getAirTimeUseCasesResponseListner = SingleLiveEvent<GetAirTimeUseCasesResponse>()
    var getAirTimeQuoteResponseListner = SingleLiveEvent<AirTimeQuoteResponse>()
    var getAirTimeResponseListner = SingleLiveEvent<AirTimeResponse>()

    //Request For AirTimeUseCase
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
                            mAirTimeUseCaseResponse.set(result)
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

    //Request For AirTimeQuote
    fun requestForAirTimeQuoteApi(context: Context?,
                                  receiverMsisdn : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            amountToTransfer = airTimeAmountSelected.get()!!
            transferdAmountTo = receiverMsisdn

            var airTimeSelectedPlanCode = ""
            if(isRechargeMobileUseCase.get()!!){
                airTimeSelectedPlanCode = airTimeSelectedPlanCodeSelected.get()!!
            }
            if(isRechargeFixeUseCase.get()!!){
                airTimeSelectedPlanCode = "0"
            }

            var userMsisdn = ""
            if(Constants.IS_AGENT_USER || Constants.IS_MERCHANT_USER){
                userMsisdn = Constants.getMerchantReceiverAlias(transferdAmountTo)
            }
            if(Constants.IS_CONSUMER_USER){
                userMsisdn = Constants.getTransferReceiverAlias(transferdAmountTo)
            }

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAirTimeQuoteCall(
                AirTimeQuoteRequest(amountToTransfer,ApiConstant.CONTEXT_AFTER_LOGIN,"1",airTimeSelectedPlanCode,userMsisdn,
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),Constants.TYPE_PAYMENT)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getAirTimeQuoteResponseListner.postValue(result)
                        } else {
                            getAirTimeQuoteResponseListner.postValue(result)
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

    //Request For AirTime
    fun requestForAirTimeApi(context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            var airTimeSelectedPlanCode = ""
            if(isRechargeMobileUseCase.get()!!){
                airTimeSelectedPlanCode = airTimeSelectedPlanCodeSelected.get()!!
            }

            if(isRechargeFixeUseCase.get()!!){
                airTimeSelectedPlanCode = "0"
            }

            var userMsisdn = ""
            if(Constants.IS_AGENT_USER || Constants.IS_MERCHANT_USER){
                userMsisdn = Constants.getMerchantReceiverAlias(transferdAmountTo)
            }
            if(Constants.IS_CONSUMER_USER){
                userMsisdn = Constants.getTransferReceiverAlias(transferdAmountTo)
            }

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAirTimeCall(
                AirTimeRequest(amountToTransfer,ApiConstant.CONTEXT_AFTER_LOGIN,"1",airTimeSelectedPlanCode,qouteId,userMsisdn,
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),Constants.TYPE_PAYMENT)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getAirTimeResponseListner.postValue(result)
                        } else {
                            getAirTimeResponseListner.postValue(result)
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