package com.es.marocapp.usecase.billpayment

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.*
import com.es.marocapp.model.requests.Param
import com.es.marocapp.model.responses.*
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.favorites.FavoritesActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.Logger
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.HashMap

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
    var fatoratiFeeAmountCalculated = "0.00"
    var fatoratiFeeAmountCaseImplemented = false
    var senderBalanceAfter ="0.00"
    var mInvoiceOfSelectedBill = ""
    var billTypeSelectedIcon = R.drawable.others
    var domain = "-1"
    var ReceiverName = ""
    var qouteId = ""
    var transactionID = ""
    var totalSelectedBillAmount = "0.00"
    var totalBillSelected = -1

    var isMultipleBillSelected = "false"

    //UseCase Observer
    var isBillUseCaseSelected = ObservableField<Boolean>()
    var isFatoratiUseCaseSelected = ObservableField<Boolean>()

    var isQuickRechargeCallForBillOrFatouratie = ObservableField<Boolean>()

    //Post PIad Bill Payment Observer
    var isPostPaidMobileSelected = ObservableField<Boolean>()
    var isPostPaidFixSelected = ObservableField<Boolean>()
    var isInternetSelected = ObservableField<Boolean>()
    var isUserSelectedFromFavorites = ObservableField<Boolean>()
    var billTypeSelected = ObservableField<String>()
    var PostPaidFinancialResourceInfoObserver = ObservableField<PostPaidFinancialResourceInfoResponse>()
    var selectedIvoicesList = ObservableField<ArrayList<InvoiceCustomModel>>()
    var selectedIvoicesQuoteList = ObservableField<ArrayList<String>>()
    var selectedIvoicesQuoteHash = HashMap<String,String>()
    var selectedIvoicesBillPaymentStatus = ObservableField<ArrayList<String>>()
    var selectedIvoicesBillPaymentResponseValue = ObservableField<ArrayList<PostPaidBillPaymentResponse>>()

    //Fatorati Observer
    var fatoratiStepOneObserver = ObservableField<BillPaymentFatoratiStepOneResponse>()
    var fatoratiTypeSelected = ObservableField<Creancier>()
    var fatoratiStepTwoObserver = ObservableField<BillPaymentFatoratiStepTwoResponse>()
    var fatoratiStepFourObserver = ObservableField<BillPaymentFatoratiStepFourResponse>()

    var selectedFatoraitIvoicesList = ObservableField<ArrayList<FatoratiCustomParamModel>>()
    var billPaymentPostFatoratiResponseObserver = ObservableField<ArrayList<BillPaymentFatoratiResponse>>()

    var getDeleteFavoritesResponseListner = SingleLiveEvent<DeleteContactResponse>()

    var fatoratiFee = "0.00"


    //Post PIad Bill Payment API Listner
    var getPostPaidResourceInfoResponseListner = SingleLiveEvent<PostPaidFinancialResourceInfoResponse>()

    var listOfPostPaidBillPaymentQuote = arrayListOf<PostPaidBillPaymentQuoteResponse>()
    var getPostPaidBillPaymentQuoteResponseListner = SingleLiveEvent<ArrayList<PostPaidBillPaymentQuoteResponse>>()

    var listOfPostPaidBillPayment = arrayListOf<PostPaidBillPaymentResponse>()
    var getPostPaidBillPaymentResponseListner = SingleLiveEvent<ArrayList<PostPaidBillPaymentResponse>>()


    var getAddFavoritesResponseListner = SingleLiveEvent<AddContactResponse>()

    //BillPaymentCompnies API Response Listner
    var getBillPaymentCompaniesResponseListner = SingleLiveEvent<BillPaymentCompaniesResponse>()
    var getBillPaymentCompaniesResponseObserver = ObservableField<BillPaymentCompaniesResponse>()

    //Fatorati API Listner
    var getFatoratiStepOneResponseListner = SingleLiveEvent<BillPaymentFatoratiStepOneResponse>()
    var getFatoratiStepTwoResponseListner = SingleLiveEvent<BillPaymentFatoratiStepTwoResponse>()
    var getFatoratiStepFourResponseListner = SingleLiveEvent<BillPaymentFatoratiStepFourResponse>()

    var listOfFatoratiQuote = arrayListOf<BillPaymentFatoratiQuoteResponse>()
    var getPostPaidFatoratiQuoteResponseListner = SingleLiveEvent<BillPaymentFatoratiQuoteResponse>()

    var listOfFatorati = arrayListOf<BillPaymentFatoratiResponse>()
    var getPostPaidFatoratiResponseListner = SingleLiveEvent<BillPaymentFatoratiResponse>()

    var listOfSelectedBillAmount : ArrayList<String> = arrayListOf()
    var listOfSelectedBillFee : ArrayList<String> = arrayListOf()

    var postPaidCounter=0
    var triggerPostPaidNextCall = SingleLiveEvent<Boolean>()

    var fatoratiCounter=0
    var triggerFatoratiNextCall = SingleLiveEvent<Boolean>()


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

                        if (result?.responseCode != null) {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getPostPaidResourceInfoResponseListner.postValue(result)
                                    PostPaidFinancialResourceInfoObserver.set(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getPostPaidResourceInfoResponseListner.postValue(result)
                                    PostPaidFinancialResourceInfoObserver.set(result)
                                }
                            }
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
            isLoading.set(true)
         //   var convertedAmountValue = (selectBillAmount.toDouble()/Constants.AMOUNT_CONVERSION_VALUE.toDouble()).toString()

            var convertedAmountValue=""
            var convertedOpenAmount=""


            try {
                convertedAmountValue = String.format(Locale.US,
                    "%.2f",
                    (selectBillAmount.toDouble() / Constants.AMOUNT_CONVERSION_VALUE.toDouble())

                )

                if(convertedAmountValue.contains(",")){
                    convertedAmountValue=convertedAmountValue.replace(",",".")
                }
                Logger.debugLog("convertedAmountValue", convertedAmountValue)
                val symbolsEN_US: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.US)
                convertedOpenAmount =
                    (DecimalFormat("#",symbolsEN_US).format((convertedAmountValue.toDouble() * Constants.AMOUNT_CONVERSION_VALUE.toDouble()))).toString()

                if(convertedOpenAmount.contains(",")){
                    convertedOpenAmount=convertedOpenAmount.replace(",",".")
                }
                // convertedOpenAmount = String.format("%.0f", convertedOpenAmount)

                Logger.debugLog("convertedOpenAmount", convertedOpenAmount)

            }
            catch (e:Exception){

            }

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getPostPaidBillPaymentQuote(
                PostPaidBillPaymentQuoteRequest(
                    convertedAmountValue,mCodeEntered,ApiConstant.CONTEXT_AFTER_LOGIN,custId,custname,"1",
                    transferdAmountTo,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),selectBillAmount,Constants.TYPE_PAYMENT,domain,invoiceMonth,ohrefnum,ohxact,convertedOpenAmount
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    listOfPostPaidBillPaymentQuote.add(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    listOfPostPaidBillPaymentQuote.add(result)
                                }
                            }
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
        openAmount: String, mQuoteId: String,
        isMultipleBillSelected : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {
            isLoading.set(true)
            var convertedAmountValue=""
            var convertedOpenAmount=""


            try {
                convertedAmountValue = String.format(Locale.US,
                    "%.2f",
                    (selectBillAmount.toDouble() / Constants.AMOUNT_CONVERSION_VALUE.toDouble())
                )

                if(convertedAmountValue.contains(",")){
                    convertedAmountValue=convertedAmountValue.replace(",",".")
                }

                Logger.debugLog("convertedAmountValue", convertedAmountValue)
                val symbolsEN_US: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.US)

                convertedOpenAmount =
                    (DecimalFormat("#", symbolsEN_US).format((convertedAmountValue.toDouble() * Constants.AMOUNT_CONVERSION_VALUE.toDouble()))).toString()

               // convertedOpenAmount = String.format("%.0f", convertedOpenAmount)
                if(convertedOpenAmount.contains(",")){
                    convertedOpenAmount=convertedOpenAmount.replace(",",".")
                }
                Logger.debugLog("convertedOpenAmount", convertedOpenAmount)

            }
            catch (e:Exception){

            }

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getPostPaidBillPayment(
                PostPaidBillPaymentRequest(
                    convertedAmountValue,mCodeEntered,ApiConstant.CONTEXT_AFTER_LOGIN,custId,custname,"1",mQuoteId,transferdAmountTo,
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),selectBillAmount,Constants.TYPE_PAYMENT,domain,invoiceMonth,ohrefnum,ohxact,convertedOpenAmount,isMultipleBillSelected
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    listOfPostPaidBillPayment.add(result)
                                    if(postPaidCounter<totalBillSelected){
                                        triggerPostPaidNextCall.postValue(true)
                                    }
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    listOfPostPaidBillPayment.add(result)
                                }
                            }

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
                                  contactName : String,
                                  tranferAmountToWithoutAlias : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAddContact(
                AddContactRequest(tranferAmountToWithoutAlias,contactName,ApiConstant.CONTEXT_AFTER_LOGIN)
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
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
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

    //Request For FatoratiStepOne
    fun requestForFatoratiStepOneApi(context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatoratiStepOne(
                BillPaymentFatoratiStepOneRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.OPERATION_TYPE_CREANCIER,
                    Constants.getFatoratiAlias(Constants.CURRENT_USER_MSISDN),Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    fatoratiStepOneObserver.set(result)
                                    getFatoratiStepOneResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    fatoratiStepOneObserver.set(result)
                                    getFatoratiStepOneResponseListner.postValue(result)
                                }
                            }

                        } else {
                            getFatoratiStepOneResponseListner.postValue(result)
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

    //Request For FatoratiStepTwo
    fun requestForFatoratiStepTwoApi(context: Context?,
                                    receiver: String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            transferdAmountTo = receiver

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatoratiStepTwo(
                BillPaymentFatoratiStepTwoRequest(ApiConstant.CONTEXT_AFTER_LOGIN,fatoratiTypeSelected.get()!!.codeCreancier,Constants.OPERATION_TYPE_CREANCE,
                    Constants.getFatoratiAlias(receiver),Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    fatoratiStepTwoObserver.set(result)
                                    getFatoratiStepTwoResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    fatoratiStepTwoObserver.set(result)
                                    getFatoratiStepTwoResponseListner.postValue(result)
                                }
                            }

                        } else {
                            getFatoratiStepTwoResponseListner.postValue(result)
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

    //Request For FatoratiStepFour
    fun requestForFatoratiStepFourApi(
        context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)



                disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatoratiStepFour(
                    BillPaymentFatoratiStepFourRequest(fatoratiTypeSelected.get()!!.codeCreance,ApiConstant.CONTEXT_AFTER_LOGIN,fatoratiTypeSelected.get()!!.codeCreancier,
                        fatoratiStepTwoObserver.get()!!.param.nomChamp,Constants.OPERATION_TYPE_IMPAYES,Constants.getFatoratiAlias(transferdAmountTo),
                        fatoratiStepTwoObserver.get()!!.refTxFatourati,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN))
                )
                    .compose(applyIOSchedulers())
                    .subscribe(
                        { result ->
                            isLoading.set(false)

                            if (result?.responseCode != null)
                            {
                                when(result?.responseCode) {
                                    ApiConstant.API_SUCCESS -> {
                                        fatoratiStepFourObserver.set(result)
                                        getFatoratiStepFourResponseListner.postValue(result)
                                    }
                                    ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                        LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                    ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                        LoginActivity.KEY_REDIRECT_USER_INVALID)
                                    else ->  {
                                        fatoratiStepFourObserver.set(result)
                                        getFatoratiStepFourResponseListner.postValue(result)
                                    }
                                }

                            } else {
                                getFatoratiStepFourResponseListner.postValue(result)
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

    //Request For FatoratiQuotePayment
    fun requestForFatoratiQuoteApi(context: Context?,
                                   amount: String,
                                   refTxFatourati : String,
                                   totalAmount : String,
                                   paramsForFatoratiPayment : List<FatoratiQuoteParam>
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatoratiQuote(
                BillPaymentFatoratiQuoteRequest(Constants.converValueToTwoDecimalPlace(amount.toDouble()),fatoratiTypeSelected.get()!!.codeCreance,ApiConstant.CONTEXT_AFTER_LOGIN,fatoratiTypeSelected.get()!!.codeCreancier,
                    "true",Constants.getFatoratiAlias(transferdAmountTo),
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),Constants.TYPE_BILL_PAYMENT,refTxFatourati,totalAmount,paramsForFatoratiPayment,fatoratiTypeSelected.get()!!.nomCreancier
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getPostPaidFatoratiQuoteResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getPostPaidFatoratiQuoteResponseListner.postValue(result)
                                }
                            }

                        } else {
                            getPostPaidFatoratiQuoteResponseListner.postValue(result)
                        }

                        /*if(listOfFatoratiQuote.size.equals(totalBillSelected)){
                            getPostPaidFatoratiQuoteResponseListner.postValue(listOfFatoratiQuote)
                        }*/


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


    //Request For FatoratiPayment
    fun requestForFatoratiApi(context: Context?,
                              amount: String,
                              quoteId : String,
                              isMultipleBillSelected: String,
                              paramsForFatoratiPayment : List<Param>
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatorati(
                BillPaymentFatoratiRequest(Constants.converValueToTwoDecimalPlace(amount.toDouble()),fatoratiTypeSelected.get()!!.codeCreance,ApiConstant.CONTEXT_AFTER_LOGIN,fatoratiTypeSelected.get()!!.codeCreancier,
                    "true", quoteId,Constants.getFatoratiAlias(transferdAmountTo),
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),Constants.TYPE_BILL_PAYMENT,isMultipleBillSelected,fatoratiStepFourObserver.get()?.refTxFatourati.toString(),
                    fatoratiStepFourObserver.get()?.totalAmount.toString(),paramsForFatoratiPayment,fatoratiTypeSelected.get()!!.nomCreancier
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getPostPaidFatoratiResponseListner.postValue(result)
                                    /*if(fatoratiCounter<totalBillSelected){
                                        triggerFatoratiNextCall.postValue(true)
                                    }*/
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getPostPaidFatoratiResponseListner.postValue(result)
                                }
                            }

                        } else {
                            getPostPaidFatoratiResponseListner.postValue(result)
                        }

                        /*if(listOfFatorati.size.equals(totalBillSelected)){
                            billPaymentPostFatoratiResponseObserver.set(listOfFatorati)
                            getPostPaidFatoratiResponseListner.postValue(listOfFatorati)
                        }*/


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

    //Request For DeleteFavorite
    fun requestForDeleteFavoriteApi(context: Context?,
                                    contactIdentity : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getDeleteContact(
                DeleteContactRequest(contactIdentity,ApiConstant.CONTEXT_AFTER_LOGIN)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getDeleteFavoritesResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as FavoritesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as FavoritesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getDeleteFavoritesResponseListner.postValue(result)
                                }
                            }

                        } else {
                            getDeleteFavoritesResponseListner.postValue(result)
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

    //Request For BillPaymentCompanies
    fun requestForBillPaymentCompaniesApi(context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentCompanies(
                BillPaymentCompaniesRequest(
                    ApiConstant.CONTEXT_AFTER_LOGIN,Constants.OPERATION_TYPE_CREANCIER,
                    Constants.getFatoratiAlias(Constants.CURRENT_USER_MSISDN),Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN)
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getBillPaymentCompaniesResponseListner.postValue(result)
                                    getBillPaymentCompaniesResponseObserver.set(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getBillPaymentCompaniesResponseListner.postValue(result)
                                    getBillPaymentCompaniesResponseObserver.set(result)
                                }
                            }
                        } else {
                            getBillPaymentCompaniesResponseListner.postValue(result)
                            getBillPaymentCompaniesResponseObserver.set(result)
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