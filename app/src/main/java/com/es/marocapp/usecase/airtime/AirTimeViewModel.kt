package com.es.marocapp.usecase.airtime

import android.app.Application
import android.content.Context
import android.view.Gravity
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.model.requests.*
import com.es.marocapp.model.responses.*
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.Logger
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class AirTimeViewModel(application: Application) : AndroidViewModel(application){

    var totalTax: Double=0.0
    var isCurrentSelectedLanguageEng: Boolean=false
    var start  = Gravity.START
    var end = Gravity.END
    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()

    var popBackStackTo = -1

    var amountToTransfer = ""
    var transferdAmountTo = ""
    var feeAmount = "0"
    var qouteId = ""
    var transactionID = ""
    var senderBalanceAfter ="0.00"

    var tranferAmountToWithAlias = ""

    var isRechargeFixeUseCase = ObservableField<Boolean>()
    var isRechargeMobileUseCase = ObservableField<Boolean>()
    var isQuickRechargeUseCase = ObservableField<Boolean>()
    var airTimeSelected = ObservableField<String>()
    var airTimePlanSelected = ObservableField<String>()
    var airTimeSelectedPlanCodeSelected = ObservableField<String>()
    var airTimeAmountSelected = ObservableField<String>()

    var isTransactionFailed = ObservableField<Boolean>()
    var isTransactionPending = ObservableField<Boolean>()
    var airTimeResponse = ObservableField<AirTimeResponse>()

    var isUserSelectedFromFavorites = ObservableField<Boolean>()

    var mAirTimeUseCaseResponse = ObservableField<GetAirTimeUseCasesResponse>()
    var getAirTimeUseCasesResponseListner = SingleLiveEvent<GetAirTimeUseCasesResponse>()
    var getAirTimeQuoteResponseListner = SingleLiveEvent<AirTimeQuoteResponse>()
    var getAirTimeResponseListner = SingleLiveEvent<AirTimeResponse>()
    var getContactResponseListner = SingleLiveEvent<AddBillProviderContactResponse>()
    var getAddFavoritesResponseListner = SingleLiveEvent<AddContactResponse>()

    //Request For Favourites
    fun requestForGetFavouriteApi(
        context: Context?
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            Logger.debugLog("billPayment", "isLoading ${isLoading}")
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getContact(
                GetContactRequest(
                    ApiConstant.CONTEXT_AFTER_LOGIN,
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN)
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        Logger.debugLog("billPayment", "isLoading ${isLoading}")
                        if (result?.responseCode != null) {
                            when (result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {

                                    getContactResponseListner.postValue(result)

                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                    context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT
                                )
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                    context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID
                                )
                                else -> {
                                    getContactResponseListner.postValue(result)

                                }
                            }
                        } else {
                            getContactResponseListner.postValue(result)
                        }


                    },
                    { error ->
                        isLoading.set(false)

                        //Display Error Result Code with with Configure Message
                        try {
                            if (context != null && error != null) {
                                errorText.postValue(context.getString(R.string.error_msg_generic) + (error as HttpException).code())
                            }
                        } catch (e: Exception) { errorText.postValue(context!!.getString(R.string.error_msg_generic))
                        }

                    })


        } else {

            errorText.postValue(Constants.SHOW_INTERNET_ERROR)
        }

    }


    //Request For AirTimeUseCase
    fun requestForAirTimeUseCasesApi(context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            var userType = ""
            if(Constants.IS_CONSUMER_USER){
                userType = "consumer"
            }else if(Constants.IS_MERCHANT_USER){
                userType = "merchant"
            }else if(Constants.IS_AGENT_USER){
                userType = "agent"
            }


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAirTimeUseCasesCall(
                GetAirTimeUseCasesRequest(ApiConstant.CONTEXT_AFTER_LOGIN,LocaleManager.selectedLanguage,Constants.balanceInfoAndResponse?.profilename!!,userType)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getAirTimeUseCasesResponseListner.postValue(result)
                                    mAirTimeUseCaseResponse.set(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as AirTimeActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as AirTimeActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getAirTimeUseCasesResponseListner.postValue(result)
                                    mAirTimeUseCaseResponse.set(result)
                                }
                            }
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
            if(isRechargeFixeUseCase.get()!! || isQuickRechargeUseCase.get()!!){
                airTimeSelectedPlanCode = "0"
            }

            var userMsisdn = ""
//            if(Constants.IS_AGENT_USER || Constants.IS_MERCHANT_USER){
//                userMsisdn = Constants.getMerchantReceiverAlias(transferdAmountTo)
//            }
//            if(Constants.IS_CONSUMER_USER){
//                userMsisdn = Constants.getTransferReceiverAlias(transferdAmountTo)
//            }
            if(airTimeSelectedPlanCode.equals("99"))
            {
                userMsisdn = Constants.getAirTimePassStoreReceiverAlias(transferdAmountTo)
            }
            else{
            userMsisdn = Constants.getAirTimeReceiverAlias(transferdAmountTo)
            }

            tranferAmountToWithAlias = userMsisdn

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAirTimeQuoteCall(
                AirTimeQuoteRequest(amountToTransfer,ApiConstant.CONTEXT_AFTER_LOGIN,Constants.airtimeMaxNumOfRetries,airTimeSelectedPlanCode,userMsisdn,
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),Constants.TYPE_PAYMENT)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getAirTimeQuoteResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as AirTimeActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as AirTimeActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getAirTimeQuoteResponseListner.postValue(result)
                                }
                            }
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

            if(isRechargeFixeUseCase.get()!! || isQuickRechargeUseCase.get()!!){
                airTimeSelectedPlanCode = "0"
            }

            var userMsisdn = ""
//            if(Constants.IS_AGENT_USER || Constants.IS_MERCHANT_USER){
//                userMsisdn = Constants.getMerchantReceiverAlias(transferdAmountTo)
//            }
//            if(Constants.IS_CONSUMER_USER){
//                userMsisdn = Constants.getTransferReceiverAlias(transferdAmountTo)
//            }
            if(airTimeSelectedPlanCode.equals("99"))
            {
                userMsisdn = Constants.getAirTimePassStoreReceiverAlias(transferdAmountTo)
            }
            else{
                userMsisdn = Constants.getAirTimeReceiverAlias(transferdAmountTo)
            }


            tranferAmountToWithAlias = userMsisdn

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAirTimeCall(
                AirTimeRequest(amountToTransfer,ApiConstant.CONTEXT_AFTER_LOGIN,Constants.airtimeMaxNumOfRetries,airTimeSelectedPlanCode,userMsisdn,
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),Constants.TYPE_PAYMENT)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getAirTimeResponseListner.postValue(result)
                                    airTimeResponse.set(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as AirTimeActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as AirTimeActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    airTimeResponse.set(result)
                                    getAirTimeResponseListner.postValue(result)
                                }
                            }

                        } else {
                            getAirTimeResponseListner.postValue(result)
                            airTimeResponse.set(result)
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
                AddContactRequest(tranferAmountToWithAlias,contactName,ApiConstant.CONTEXT_AFTER_LOGIN,"","")
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getAddFavoritesResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as AirTimeActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as AirTimeActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getAddFavoritesResponseListner.postValue(result)
                                }
                            }
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