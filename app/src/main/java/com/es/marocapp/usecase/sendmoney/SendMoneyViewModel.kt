package com.es.marocapp.usecase.sendmoney

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

class SendMoneyViewModel (application: Application) : AndroidViewModel(application){

    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()
    var getAccountHolderInformationResponseListner = SingleLiveEvent<GetAccountHolderInformationResponse>()
    var getAccountHolderAdditionalInfoResponseListner = SingleLiveEvent<AccountHolderAdditionalInformationResponse>()
    var getTransferResponseListner = SingleLiveEvent<TransferResponse>()
    var getTransferQouteResponseListner = SingleLiveEvent<TransferQouteResponse>()
    var getMerchantQouteResponseListner = SingleLiveEvent<MerchantPaymentQuoteResponse>()
    var getMerchantPaymentResponseListner = SingleLiveEvent<MerchantPaymentResponse>()
    var getPaymentQouteResponseListner = SingleLiveEvent<PaymentQuoteResponse>()
    var getPaymentResponseListner = SingleLiveEvent<PaymentResponse>()
    var getFloatTransferQuoteResponseListner = SingleLiveEvent<FloatTransferQuoteResponse>()
    var getFloatTransferResponseListner = SingleLiveEvent<FloatTransferResponse>()
    var getAddFavoritesResponseListner = SingleLiveEvent<AddContactResponse>()

    var trasferTypeSelected = ObservableField<String>()
    var isUserRegistered = ObservableField<Boolean>()
    var isUserUnRegisteredSpecialCase = ObservableField<Boolean>()
    var isFundTransferUseCase = ObservableField<Boolean>()
    var isInitiatePaymenetToMerchantUseCase = ObservableField<Boolean>()
    var isAccountHolderInformationFailed = ObservableField<Boolean>()
    var isUserSelectedFromFavorites = ObservableField<Boolean>()

    var transferdAmountTo = ""
    var amountToTransfer = ""
    var feeAmount = ""
    var qouteId = ""
    var transactionID = ""
    var senderBalanceAfter =""
    var ReceiverName =""
    var mBalanceInforAndResponseObserver = ObservableField<BalanceInfoAndLimitResponse>()
    var mAccountHolderInfoResponseObserver = ObservableField<GetAccountHolderInformationResponse>()

    var tranferAmountToWithAlias = ""

    var popBackStackTo = -1

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
                            mAccountHolderInfoResponseObserver.set(result)

                        }else if(result?.responseCode!=null && result.responseCode.equals(ApiConstant.API_FAILURE,true)){
                            // if response code is 1500 this means user  user isnot registered redirecting user to Registration flow
                            getAccountHolderInformationResponseListner.postValue(result)
                            mAccountHolderInfoResponseObserver.set(result)
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


    //Request For Get Account Holder Additional Information
    fun requestForAccountHolderAddtionalInformationApi(
        context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAccountHolderAddtionalInfoCall(
                GetAccountHolderInformationRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.getNumberMsisdn(transferdAmountTo))
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


    //Request For Trasnfer Qoute
    fun requestFoTransferQouteApi(context: Context?,
                                  amount : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            amountToTransfer = amount
            tranferAmountToWithAlias = Constants.getNumberMsisdn(transferdAmountTo)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getTransferQouteCall(
                TransferQouteRequest(amount,ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(transferdAmountTo))
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
                            getTransferQouteResponseListner.postValue(result)
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

    //Request For Trasnfer
    fun requestFoTransferApi(context: Context?,
                             qouteID : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            tranferAmountToWithAlias = Constants.getNumberMsisdn(transferdAmountTo)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getTransferCall(
                TransferRequest(amountToTransfer,ApiConstant.CONTEXT_AFTER_LOGIN,qouteID,Constants.getNumberMsisdn(transferdAmountTo))
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
                            getTransferResponseListner.postValue(result)
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

    //Request For Merchant Qoute
    fun requestFoMerchantQouteApi(context: Context?,
                                  amount : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            amountToTransfer = amount
            tranferAmountToWithAlias = Constants.getNumberMsisdn(transferdAmountTo)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getMerchantQouteCall(
                MerchantPaymentQuoteRequest(amount,ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(transferdAmountTo))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getMerchantQouteResponseListner.postValue(result)

                        } else {
                            getMerchantQouteResponseListner.postValue(result)
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

    //Request For Merchant
    fun requestFoMerchantApi(context: Context?,
                             sender : String,
                             qouteID : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            tranferAmountToWithAlias = Constants.getNumberMsisdn(transferdAmountTo)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getMerchantPaymentCall(
                MerchantPaymentRequest(amountToTransfer,ApiConstant.CONTEXT_AFTER_LOGIN,qouteID,Constants.getNumberMsisdn(sender),Constants.getNumberMsisdn(transferdAmountTo))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getMerchantPaymentResponseListner.postValue(result)

                        } else {
                            getMerchantPaymentResponseListner.postValue(result)
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

    //Request For Payment Qoute
    fun requestFoPaymentQouteApi(context: Context?,
                                  amount : String,
                                 sender :String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            amountToTransfer = amount

            var transferType = ""
            var receiver = ""

            if(isInitiatePaymenetToMerchantUseCase.get()!!){
                transferType = Constants.MERCHANT_TYPE_PAYMENT
            }else if(isFundTransferUseCase.get()!!){
                transferType = Constants.TRANSFER_TYPE_PAYMENT
            }

            if(Constants.IS_AGENT_USER || Constants.IS_MERCHANT_USER){
                receiver = Constants.getMerchantReceiverAlias(transferdAmountTo)
            }
            if(Constants.IS_CONSUMER_USER){
                receiver = Constants.getTransferReceiverAlias(transferdAmountTo)
            }


            tranferAmountToWithAlias = receiver

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getPaymentQouteCall(
                PaymentQuoteRequest(amount,ApiConstant.CONTEXT_AFTER_LOGIN,receiver,Constants.getNumberMsisdn(sender),transferType,Constants.balanceInfoAndResponse.profilename.toString())
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getPaymentQouteResponseListner.postValue(result)

                        } else {
                            getPaymentQouteResponseListner.postValue(result)
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

    //Request For Payment
    fun requestFoPayementApi(context: Context?,
                             qouteID : String,
                             sender: String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            var transferType = ""
            var receiver = ""

            if(isInitiatePaymenetToMerchantUseCase.get()!!){
                transferType = Constants.MERCHANT_TYPE_PAYMENT
            }else if(isFundTransferUseCase.get()!!){
                transferType = Constants.TRANSFER_TYPE_PAYMENT
            }

            if(Constants.IS_AGENT_USER || Constants.IS_MERCHANT_USER){
                receiver = Constants.getMerchantReceiverAlias(transferdAmountTo)
            }
            if(Constants.IS_CONSUMER_USER){
                receiver = Constants.getTransferReceiverAlias(transferdAmountTo)
            }


            tranferAmountToWithAlias = receiver


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getPaymentCall(
                PaymentRequest(amountToTransfer,ApiConstant.CONTEXT_AFTER_LOGIN,qouteID,receiver,Constants.getNumberMsisdn(sender),transferType,Constants.balanceInfoAndResponse.profilename.toString())

            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getPaymentResponseListner.postValue(result)

                        } else {
                            getPaymentResponseListner.postValue(result)
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


    //___________________----------------_______________-------------------__________________//

    //Request For Payment Qoute
    fun requestForSimplePaymentQouteApi(context: Context?,
                                 amount : String,
                                 sender :String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            amountToTransfer = amount

            var transferType = ""
            var receiver = ""

            if(isInitiatePaymenetToMerchantUseCase.get()!!){
                transferType = Constants.MERCHANT_TYPE_PAYMENT
             }else if(isFundTransferUseCase.get()!!){
                transferType = Constants.TRANSFER_TYPE_PAYMENT
            }

            if(Constants.IS_AGENT_USER || Constants.IS_MERCHANT_USER){
                receiver = Constants.getMerchantReceiverAlias(transferdAmountTo)
            }
            if(Constants.IS_CONSUMER_USER){
                receiver = Constants.getTransferReceiverAlias(transferdAmountTo)
            }


            tranferAmountToWithAlias = receiver


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getSimplePaymentQouteCall(
                SimplePaymentQuoteRequest(amount,ApiConstant.CONTEXT_AFTER_LOGIN,receiver,Constants.getNumberMsisdn(sender),transferType)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getPaymentQouteResponseListner.postValue(result)

                        } else {
                            getPaymentQouteResponseListner.postValue(result)
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

    //Request For Payment
    fun requestForSimplePayementApi(context: Context?,
                             qouteID : String,
                             sender: String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            var transferType = ""
            var receiver = ""

            if(isInitiatePaymenetToMerchantUseCase.get()!!){
                transferType = Constants.MERCHANT_TYPE_PAYMENT
            }else if(isFundTransferUseCase.get()!!){
                transferType = Constants.TRANSFER_TYPE_PAYMENT
            }

            if(Constants.IS_AGENT_USER || Constants.IS_MERCHANT_USER){
                receiver = Constants.getMerchantReceiverAlias(transferdAmountTo)
            }
            if(Constants.IS_CONSUMER_USER){
                receiver = Constants.getTransferReceiverAlias(transferdAmountTo)
            }


            tranferAmountToWithAlias = receiver


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getSimplePaymentCall(
                SimplePaymentRequest(amountToTransfer,ApiConstant.CONTEXT_AFTER_LOGIN,qouteID,receiver,Constants.getNumberMsisdn(sender),transferType)

            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getPaymentResponseListner.postValue(result)

                        } else {
                            getPaymentResponseListner.postValue(result)
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

    //Request For Payment
    fun requestForFloatTransferQuoteApi(context: Context?,
                                    amount : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            amountToTransfer = amount
            tranferAmountToWithAlias = Constants.getNumberMsisdn(transferdAmountTo)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getFloatTransferQuoteCall(
                FloatTransferQuoteRequest(amount,ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(transferdAmountTo))

            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getFloatTransferQuoteResponseListner.postValue(result)

                        } else {
                            getFloatTransferQuoteResponseListner.postValue(result)
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


    //Request For Trasnfer
    fun requestForFloatTransferApi(context: Context?,
                             qouteID : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            tranferAmountToWithAlias = Constants.getNumberMsisdn(transferdAmountTo)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getFloatTransferCall(
                FloatTransferRequest(amountToTransfer,ApiConstant.CONTEXT_AFTER_LOGIN,qouteID,Constants.getNumberMsisdn(transferdAmountTo))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getFloatTransferResponseListner.postValue(result)

                        } else {
                            getFloatTransferResponseListner.postValue(result)
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

    //Request For AddFavorites
    fun requestForAddFavoritesApi(context: Context?,
                                   contactName : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAddContact(
                AddContactRequest(tranferAmountToWithAlias,contactName,ApiConstant.CONTEXT_AFTER_LOGIN)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getAddFavoritesResponseListner.postValue(result)

                        } else {
                            getAddFavoritesResponseListner.postValue(result)
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