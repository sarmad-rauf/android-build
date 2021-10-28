package com.es.marocapp.usecase.transfercommision

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.es.marocapp.R
import com.es.marocapp.model.requests.*
import com.es.marocapp.model.responses.*
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.transfercommision.TransferCommisionActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class TransferCommisionViewModel(application: Application) : AndroidViewModel(application) {

    var totalTax: Double = 0.0
    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()
    var getRecieverInformationResponseListner =
        SingleLiveEvent<GetAccountHolderInformationResponse>()
    var getAccountHolderAdditionalInfoResponseListner =
        SingleLiveEvent<AccountHolderAdditionalInformationResponse>()
    var getTransferResponseListner = SingleLiveEvent<TransferResponse>()
    var getTransferQouteResponseListner = SingleLiveEvent<TransferQouteResponse>()
    var getMerchantQouteResponseListner = SingleLiveEvent<MerchantPaymentQuoteResponse>()
    var getMerchantPaymentResponseListner = SingleLiveEvent<MerchantPaymentResponse>()
    var getPaymentQouteResponseListner = SingleLiveEvent<PaymentQuoteResponse>()
    var getPaymentResponseListner = SingleLiveEvent<PaymentResponse>()
    var getFloatTransferQuoteResponseListner = SingleLiveEvent<FloatTransferQuoteResponse>()
    var getFloatTransferResponseListner = SingleLiveEvent<FloatTransferResponse>()
    var getAddFavoritesResponseListner = SingleLiveEvent<AddContactResponse>()
    var getAccountsResponseListner = SingleLiveEvent<GetAccountsResponse>()


    var isTransactionFailed = ObservableField<Boolean>()
    var isTransactionPending = ObservableField<Boolean>()
    var sendMoneyFailureOrPendingDescription = ObservableField<String>()
    var headerTitle = MutableLiveData<String>()

    var trasferTypeSelected = ObservableField<String>()
    var isUserRegistered = ObservableField<Boolean>()
    var isUserUnRegisteredSpecialCase = ObservableField<Boolean>()
    var isFundTransferUseCase = ObservableField<Boolean>()
    var isInitiatePaymenetToMerchantUseCase = ObservableField<Boolean>()
    var isAccountHolderInformationFailed = ObservableField<Boolean>()
    var isUserSelectedFromFavorites = ObservableField<Boolean>()

    var transferdAmountTo = ""
    var amountToTransfer = ""
    var amountScannedFromQR = "0"
    var feeAmount = "0"
    var qouteId = ""
    var transactionID = ""

    var senderBalanceAfter: String? = ""
    var ReceiverName = ""
    var popBackStackTo = -1
    var tranferAmountToWithAlias = ""


    //Request For Trasnfer
    fun requestForTransferCommisionApi(
        context: Context?,
        amount: String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {
            isLoading.set(true)
            tranferAmountToWithAlias = Constants.getNumberMsisdn(transferdAmountTo)

            if (Constants.COMMISIONACCOUNTFRI.isNullOrEmpty()) {
                Constants.COMMISIONACCOUNTFRI = ""
            }
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getTransferCommisionCall(
                TransferCommisionRequest(
                    amount,
                    ApiConstant.CONTEXT_AFTER_LOGIN,
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),
                    Constants.COMMISIONACCOUNTFRI
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when (result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getTransferResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                    context as TransferCommisionActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT
                                )
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                    context as TransferCommisionActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID
                                )
                                else -> {
                                    getTransferResponseListner.postValue(result)
                                }
                            }

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

    fun setHeaderText(title: String) {
        headerTitle.postValue(title)
    }

}