package com.es.marocapp.usecase.transaction

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.TransactionHistoryRequest
import com.es.marocapp.model.responses.TransactionHistoryResponse
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()
    var getTransactionsResponseListner = SingleLiveEvent<TransactionHistoryResponse>()

    //Request For Get Transactions
    fun requestForGetTransactionHistoryApi(context: Context?,
                                           identity : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            var currentDate = ""
            var previousDateForTransactions = ""

            currentDate = Constants.getCurrentDate()
            previousDateForTransactions = Constants.getPreviousFromCurrentDate(currentDate,Constants.PREVIOUS_DAYS_TRANSACTION_COUNT.toInt())
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getTrasactionHistoryCall(
                TransactionHistoryRequest(ApiConstant.CONTEXT_AFTER_LOGIN,currentDate,Constants.getNumberMsisdn(identity),"0",previousDateForTransactions)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getTransactionsResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as MainActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getTransactionsResponseListner.postValue(result)
                                }
                            }

                        } else {
                            getTransactionsResponseListner.postValue(result)
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