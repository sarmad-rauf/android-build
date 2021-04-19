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
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class CashServicesViewModel(application: Application) : AndroidViewModel(application){

    var totalTax: Double=0.0
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
    var transactionID = ""
    var senderBalanceAfter ="0.00"
    var mOTP = ""

    var isTransactionFailed = ObservableField<Boolean>()
    var isTransactionPending = ObservableField<Boolean>()
    var cashServiceFailureOrPendingDescription = ObservableField<String>()

    var popBackStackTo = -1
    var isOTPFlow = ObservableField<Boolean>()

    var getInitiateTrasnferQuoteResponseListner = SingleLiveEvent<InitiateTransferQuoteResponse>()
    var getInitiateTrasnferResponseListner = SingleLiveEvent<InitiateTransferResponse>()
    var getCashInWithOtpQuoteResponseListner = SingleLiveEvent<CashInWithOtpQuoteResponse>()
    var getCashInWithOtpResponseListner = SingleLiveEvent<CashInWithOtpResponse>()
    var getGenerateOtpResponseListner = SingleLiveEvent<GenerateOtpResponse>()
    var getBalanceResponseListner = SingleLiveEvent<GetBalanceResponse>()

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

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getInitiateTrasnferQuoteResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as CashServicesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as CashServicesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getInitiateTrasnferQuoteResponseListner.postValue(result)
                                }
                            }

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

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getInitiateTrasnferResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as CashServicesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as CashServicesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getInitiateTrasnferResponseListner.postValue(result)
                                }
                            }
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

    //Request For CashInQoute
    fun requestForCashInQouteApi(context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getCashInQuoteCall(
                CashInQuoteRequest(amountToTransfer, ApiConstant.CONTEXT_AFTER_LOGIN,
                    Constants.getNumberMsisdn(transferdAmountTo),noteToSend)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getCashInWithOtpQuoteResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as CashServicesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as CashServicesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getCashInWithOtpQuoteResponseListner.postValue(result)
                                }
                            }
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
    fun requestForCashInApi(context: Context?,
                                      qouteID : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getCashInCall(
                CashInRequest(amountToTransfer,
                    ApiConstant.CONTEXT_AFTER_LOGIN,
                    Constants.getNumberMsisdn(transferdAmountTo),qouteID,noteToSend)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getCashInWithOtpResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as CashServicesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as CashServicesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getCashInWithOtpResponseListner.postValue(result)
                                }
                            }
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

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getGenerateOtpResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as CashServicesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as CashServicesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getGenerateOtpResponseListner.postValue(result)
                                }
                            }

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

    //Request For Get Updated Balance
    fun requestForGetBalanceApi(
        context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBalance(
                BalanceInfoAndLimtRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getBalanceResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as CashServicesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as CashServicesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getBalanceResponseListner.postValue(result)
                                }
                            }
                        } else {
                            getBalanceResponseListner.postValue(result)
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

    fun setInputValues(msisdnEntered: String, inputAmount: String, inputNote: String) {
        transferdAmountTo = msisdnEntered
        amountToTransfer = inputAmount
        noteToSend = inputNote
    }
}