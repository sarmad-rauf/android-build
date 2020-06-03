package com.es.marocapp.usecase.sendmoney

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.GetAccountHolderInformationRequest
import com.es.marocapp.model.requests.TransferQouteRequest
import com.es.marocapp.model.requests.TransferRequest
import com.es.marocapp.model.responses.*
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class SendMoneyViewModel (application: Application) : AndroidViewModel(application){

    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()
    var getAccountHolderInformationResponseListner = SingleLiveEvent<GetAccountHolderInformationResponse>()
    var getAccountHolderAdditionalInfoResponseListner = SingleLiveEvent<AccountHolderAdditionalInformationResponse>()
    var getTransferResponseListner = SingleLiveEvent<TransferResponse>()
    var getTransferQouteResponseListner = SingleLiveEvent<TransferQouteResponse>()

    var trasferTypeSelected = ObservableField<String>()

    var transferdAmountTo = ""
    var mBalanceInforAndResponseObserver = ObservableField<BalanceInfoAndLimitResponse>()

    init {
        mBalanceInforAndResponseObserver.set(Constants.balanceInfoAndResponse)
    }

    // API Called on Login Screen to check weather User is Registered or Not
    fun requestForGetAccountHolderInformationApi(
        context: Context?,
        userMsisdn: String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            transferdAmountTo = userMsisdn
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAccountHolderInformation(
                GetAccountHolderInformationRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.getNumberMsisdn(userMsisdn))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getAccountHolderInformationResponseListner.postValue(result)

                        }else if(result?.responseCode!=null && result.responseCode.equals(ApiConstant.API_FAILURE,true)){
                            // if response code is 1500 this means user  user isnot registered redirecting user to Registration flow
                            getAccountHolderInformationResponseListner.postValue(result)
                        }
                        else {
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


    //Request For Get Account Holder Additional Information
    fun requestForAccountHolderAddtionalInformationApi(
        context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAccountHolderAddtionalInfoCall(
                GetAccountHolderInformationRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(transferdAmountTo))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getAccountHolderAdditionalInfoResponseListner.postValue(result)

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


    //Request For Trasnfer Qoute
    fun requestFoTransferQouteApi(context: Context?,
                                  receierMsisdn : String,
                                  amount : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getTransferQouteCall(
                TransferQouteRequest(amount,ApiConstant.CONTEXT_AFTER_LOGIN,receierMsisdn)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getTransferQouteResponseListner.postValue(result)

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

    //Request For Trasnfer
    fun requestFoTransferApi(context: Context?,
                                  receierMsisdn : String,
                                  amount : String,
                             qouteID : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getTransferCall(
                TransferRequest(amount,ApiConstant.CONTEXT_AFTER_LOGIN,qouteID,receierMsisdn)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getTransferResponseListner.postValue(result)

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