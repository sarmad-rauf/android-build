package com.es.marocapp.usecase.cashservices

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.*
import com.es.marocapp.model.responses.*
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class CashServicesViewModel(application: Application) : AndroidViewModel(application){

    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()

    var isDepositUseCase = ObservableField<Boolean>()
    var isWithdrawUseCase = ObservableField<Boolean>()
    var trasferTypeSelected = ObservableField<String>()

    var amountToTransfer = ""
    var transferdAmountTo = ""
    var noteToSend = ""
    var feeAmount = ""
    var qouteId = ""
    var mOTP = ""
    var transactionID = ""
    var senderBalanceAfter ="0.00"

    var popBackStackTo = -1
    var isOTPFlow = ObservableField<Boolean>()

    var getInitiateTrasnferQuoteResponseListner = SingleLiveEvent<InitiateTransferQuoteResponse>()
    var getInitiateTrasnferResponseListner = SingleLiveEvent<InitiateTransferResponse>()
    var getCashInWithOtpQuoteResponseListner = SingleLiveEvent<CashInWithOtpQuoteResponse>()
    var getCashInWithOtpResponseListner = SingleLiveEvent<CashInWithOtpResponse>()
    var getGenerateOtpResponseListner = SingleLiveEvent<GenerateOtpResponse>()

    //Request For InitiateTrasnferQoute
    fun requestForInitiateTransferQouteApi(context: Context?,
                                  amount : String,
                                  userMsisdn : String,
                                           message: String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            amountToTransfer = amount
            transferdAmountTo = userMsisdn
            noteToSend = message

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getInitiateTransferQuoteCall(
                InitiateTransferQuoteRequest(amount, ApiConstant.CONTEXT_AFTER_LOGIN, Constants.getNumberMsisdn(transferdAmountTo),message)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getInitiateTrasnferQuoteResponseListner.postValue(result)

                        } else {
                            getInitiateTrasnferQuoteResponseListner.postValue(result)
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

    //Request For InitiateTrasnfer
    fun requestForInitiateTransferApi(context: Context?,
                             qouteID : String,
                                      message : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getInitiateTransferCall(
                InitiateTransferRequest(amountToTransfer,
                    ApiConstant.CONTEXT_AFTER_LOGIN,
                    Constants.getNumberMsisdn(transferdAmountTo),qouteID,message)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getInitiateTrasnferResponseListner.postValue(result)

                        } else {
                            getInitiateTrasnferResponseListner.postValue(result)
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

    //Request For CashInWithOtpQoute
    fun requestForCashInWithOtpQouteApi(context: Context?,
                                           otp : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            mOTP = otp

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getCashInWithOtpQuoteCall(
                CashInWithOtpQuoteRequest(amountToTransfer, ApiConstant.CONTEXT_AFTER_LOGIN,otp, Constants.getNumberMsisdn(transferdAmountTo),noteToSend)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getCashInWithOtpQuoteResponseListner.postValue(result)

                        } else {
                            getCashInWithOtpQuoteResponseListner.postValue(result)
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

    //Request For CashInWithOtp
    fun requestForCashInWithOtpApi(context: Context?,
                                      qouteID : String,
                                   otp : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getCashInWithOtpCall(
                CashInWithOtpRequest(amountToTransfer,
                    ApiConstant.CONTEXT_AFTER_LOGIN,otp,
                    Constants.getNumberMsisdn(transferdAmountTo),qouteID,noteToSend)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getCashInWithOtpResponseListner.postValue(result)

                        } else {
                            getCashInWithOtpResponseListner.postValue(result)
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

    //Request For GenerateOtp
    fun requestForGenerateOtpApi(context: Context?,
                                userMsisdn: String,
                                amount: String,
                                 note : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            transferdAmountTo = userMsisdn
            amountToTransfer = amount
            noteToSend = note

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getGenerateOtpCall(
                GenerateOtpRequest(
                    ApiConstant.CONTEXT_AFTER_LOGIN,
                    Constants.getNumberMsisdn(userMsisdn),Constants.TYPE_CASH_IN)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getGenerateOtpResponseListner.postValue(result)

                        } else {
                            getGenerateOtpResponseListner.postValue(result)
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