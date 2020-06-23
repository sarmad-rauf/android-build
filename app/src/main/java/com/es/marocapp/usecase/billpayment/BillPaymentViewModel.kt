package com.es.marocapp.usecase.billpayment

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.AddContactRequest
import com.es.marocapp.model.requests.PostPaidBillPaymentQuoteRequest
import com.es.marocapp.model.requests.PostPaidBillPaymentRequest
import com.es.marocapp.model.requests.PostPaidFinancialResourceInfoRequest
import com.es.marocapp.model.responses.*
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class BillPaymentViewModel(application: Application) : AndroidViewModel(application){
    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()

    var popBackStackTo = -1
    var selectBillAmount = ""
    var transferdAmountTo = ""
    var mCodeEntered = ""
    var totalamount = ""
    var custId = ""
    var custname = ""
    var feeAmount = ""
    var senderBalanceAfter ="0.00"
    var mInvoiceOfSelectedBill = ""
    var billTypeSelectedIcon = R.drawable.others
    var domain = "-1"
    var ReceiverName = ""
    var qouteId = ""
    var transactionID = ""

    var totalSelectedBillAmount = "0.00"


    var totalBillSelected = -1


    var isPostPaidMobileSelected = ObservableField<Boolean>()
    var isPostPaidFixSelected = ObservableField<Boolean>()
    var isInternetSelected = ObservableField<Boolean>()
    var isUserSelectedFromFavorites = ObservableField<Boolean>()
    var billTypeSelected = ObservableField<String>()
    var PostPaidFinancialResourceInfoObserver = ObservableField<PostPaidFinancialResourceInfoResponse>()
    var selectedIvoicesList = ObservableField<ArrayList<InvoiceCustomModel>>()
    var selectedIvoicesQuoteList = ObservableField<ArrayList<String>>()
    var selectedIvoicesBillPaymentStatus = ObservableField<ArrayList<String>>()
    var selectedIvoicesBillPaymentResponseValue = ObservableField<ArrayList<PostPaidBillPaymentResponse>>()

    var getPostPaidResourceInfoResponseListner = SingleLiveEvent<PostPaidFinancialResourceInfoResponse>()

    var listOfPostPaidBillPaymentQuote = arrayListOf<PostPaidBillPaymentQuoteResponse>()
    var getPostPaidBillPaymentQuoteResponseListner = SingleLiveEvent<ArrayList<PostPaidBillPaymentQuoteResponse>>()

    var listOfPostPaidBillPayment = arrayListOf<PostPaidBillPaymentResponse>()
    var getPostPaidBillPaymentResponseListner = SingleLiveEvent<ArrayList<PostPaidBillPaymentResponse>>()


    var getAddFavoritesResponseListner = SingleLiveEvent<AddContactResponse>()

    var listOfSelectedBillAmount : ArrayList<String> = arrayListOf()
    var listOfSelectedBillFee : ArrayList<String> = arrayListOf()


    //Request For PostPaidFinancialResourceInfo
    fun requestForPostPaidFinancialResourceInfoApi(context: Context?,
                                                   code : String,
                                                   receiver : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            var mReceiver = ""
            if(isPostPaidMobileSelected.get()!!){
                mReceiver = Constants.getPostPaidMobileDomainAlias(receiver)
                domain = "1"
            }
            if(isPostPaidFixSelected.get()!!){
                mReceiver = Constants.getPostPaidFixedDomainAlias(receiver)
                domain = "2"
            }
            if(isInternetSelected.get()!!){
                mReceiver = Constants.getPostPaidInternetDomainAlias(receiver)
                domain = "3"
            }
            transferdAmountTo = mReceiver
            mCodeEntered = code

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getPostPaidFinancialResourceInfo(
                PostPaidFinancialResourceInfoRequest(
                  code,ApiConstant.CONTEXT_AFTER_LOGIN,mReceiver,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN)
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getPostPaidResourceInfoResponseListner.postValue(result)
                            PostPaidFinancialResourceInfoObserver.set(result)
                        } else {
                            getPostPaidResourceInfoResponseListner.postValue(result)
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


    //Request For PostPaidBillPaymentQuote
    fun requestForPostPaidBillPaymentQuoteApi(
        context: Context?,
        invoiceMonth: String,
        ohrefnum: String,
        ohxact: String,
        openAmount: String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            var convertedAmountValue = (selectBillAmount.toDouble()/Constants.AMOUNT_CONVERSION_VALUE.toDouble()).toString()

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getPostPaidBillPaymentQuote(
                PostPaidBillPaymentQuoteRequest(
                    convertedAmountValue,mCodeEntered,ApiConstant.CONTEXT_AFTER_LOGIN,custId,custname,"1",
                    transferdAmountTo,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),selectBillAmount,Constants.TYPE_PAYMENT,domain,invoiceMonth,ohrefnum,ohxact,openAmount
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            listOfPostPaidBillPaymentQuote.add(result)
                        } else {
                            listOfPostPaidBillPaymentQuote.add(result)
                        }

                        if(listOfPostPaidBillPaymentQuote.size.equals(totalBillSelected)){
                            getPostPaidBillPaymentQuoteResponseListner.postValue(listOfPostPaidBillPaymentQuote)
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

    //Request For PostPaidBillPayment
    fun requestForPostPaidBillPaymentApi(
        context: Context?,  invoiceMonth: String,
        ohrefnum: String,
        ohxact: String,
        openAmount: String, mQuoteId: String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {


            var convertedAmountValue = (selectBillAmount.toDouble()/Constants.AMOUNT_CONVERSION_VALUE.toDouble()).toString()

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getPostPaidBillPayment(
                PostPaidBillPaymentRequest(
                    convertedAmountValue,mCodeEntered,ApiConstant.CONTEXT_AFTER_LOGIN,custId,custname,"1",mQuoteId,transferdAmountTo,
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),selectBillAmount,Constants.TYPE_PAYMENT,domain,invoiceMonth,ohrefnum,ohxact,openAmount
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            listOfPostPaidBillPayment.add(result)
                        } else {
                            listOfPostPaidBillPayment.add(result)
                        }

                        if(listOfPostPaidBillPayment.size.equals(totalBillSelected)){
                            selectedIvoicesBillPaymentResponseValue.set(listOfPostPaidBillPayment)
                            getPostPaidBillPaymentResponseListner.postValue(listOfPostPaidBillPayment)
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

            var tranferAmountToWithAlias = transferdAmountTo.substringBefore("@")
            tranferAmountToWithAlias = tranferAmountToWithAlias.substringBefore("/")

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