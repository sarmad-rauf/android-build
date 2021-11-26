package com.es.marocapp.usecase.billpayment

import android.app.Application
import android.content.Context
import android.view.Gravity
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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BillPaymentViewModel(application: Application) : AndroidViewModel(application) {


    var selectedCompanyServiceProvider: String = ""
    var isCurrentSelectedLanguageEng: Boolean=false
    var start  = Gravity.START
    var end = Gravity.END
    var selectedTSAVSpinnerPosition: Int = 0
    var stepFourLydecSelected: Boolean = false
    var selectedCodeCreance: String = ""

    //Step 2 creances List
    var nomCreancierList: ArrayList<String> = ArrayList()

    //check for LYDEC flow
    var isSelectedBillMatchedwithfatouratiSeperateMenuBillNames: Boolean = false

    var TelecomeAddedOnce: Boolean = false
    var totalTax: Double = 0.0
    var showAutoDuMorocViews: Boolean = false

    //fatorati special type bil slection
    var specialMenuBillSelected: Boolean = false

    var validatedParams: ArrayList<ValidatedParam> = ArrayList()
    var demoParams: ArrayList<RecievededParam> = ArrayList()
    var recievedParams: ArrayList<RecievededParam> = ArrayList()
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
    var feeAmount = "0"
    var fatoratiFeeAmountCalculated = "0.00"
    var fatoratiFeeAmountCaseImplemented = false
    var senderBalanceAfter = "0.00"
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
    var isIamFatouratiSelected = false

    var isQuickRechargeCallForBillOrFatouratie = ObservableField<Boolean>()

    //Post PIad Bill Payment Observer

    var isPostPaidMobileSelected = ObservableField<Boolean>()
    var isPostPaidFixSelected = ObservableField<Boolean>()
    var isInternetSelected = ObservableField<Boolean>()
    var isUserSelectedFromFavorites = ObservableField<Boolean>()
    var billTypeSelected = ObservableField<String>()
    var PostPaidFinancialResourceInfoObserver =
        ObservableField<PostPaidFinancialResourceInfoResponse>()
    var selectedIvoicesList = ObservableField<ArrayList<InvoiceCustomModel>>()
    var selectedIvoicesQuoteList = ObservableField<ArrayList<String>>()
    var selectedIvoicesQuoteHash = HashMap<String, String>()
    var selectedIvoicesBillPaymentStatus = ObservableField<ArrayList<String>>()
    var selectedIvoicesBillPaymentResponseValue =
        ObservableField<ArrayList<PostPaidBillPaymentResponse>>()

    //Fatorati Observer
    var selectedCreancer = ObservableField<String>()
    var userSelectedCreancer = ""
    var userSelectedCreancerLogo = ""
    var creancesList = ObservableField<ArrayList<creances>>()
    var fatoratiStepOneObserver = ObservableField<BillPaymentFatoratiStepOneResponse>()
    var fatoratiTypeSelected = ObservableField<Creancier>()
    var fatoratiStepTwoObserver = ObservableField<BillPaymentFatoratiStepTwoResponse>()
    var fatoratiStepTwoThreeObserver = ObservableField<BillPaymentFatoratiStepThreeResponse>()
    var fatoratiStepThreeObserver = ObservableField<BillPaymentFatoratiStepThreeResponse>()
    var fatoratiStepFourObserver = ObservableField<BillPaymentFatoratiStepFourResponse>()

    var selectedFatoraitIvoicesList = ObservableField<ArrayList<FatoratiCustomParamModel>>()
    var billPaymentPostFatoratiResponseObserver =
        ObservableField<ArrayList<BillPaymentFatoratiResponse>>()

    var getDeleteFavoritesResponseListner = SingleLiveEvent<DeleteContactResponse>()

    var fatoratiFee = "0.00"


    //Post PIad Bill Payment API Listner
    var getPostPaidResourceInfoResponseListner =
        SingleLiveEvent<PostPaidFinancialResourceInfoResponse>()

    var listOfPostPaidBillPaymentQuote = arrayListOf<PostPaidBillPaymentQuoteResponse>()
    var getPostPaidBillPaymentQuoteResponseListner =
        SingleLiveEvent<ArrayList<PostPaidBillPaymentQuoteResponse>>()

    var listOfPostPaidBillPayment = arrayListOf<PostPaidBillPaymentResponse>()
    var getPostPaidBillPaymentResponseListner =
        SingleLiveEvent<ArrayList<PostPaidBillPaymentResponse>>()


    var getAddFavoritesResponseListner = SingleLiveEvent<AddContactResponse>()

    //BillPaymentCompnies API Response Listner
    var getBillPaymentCompaniesResponseListner = SingleLiveEvent<BillPaymentCompaniesResponse>()
    var getBillPaymentCompaniesResponseObserver = ObservableField<BillPaymentCompaniesResponse>()
    var getContactResponseListner = SingleLiveEvent<AddBillProviderContactResponse>()

    //Fatorati API Listner
    var getFatoratiStepOneResponseListner = SingleLiveEvent<BillPaymentFatoratiStepOneResponse>()
    var getFatoratiStepTwoResponseListner = SingleLiveEvent<BillPaymentFatoratiStepTwoResponse>()
    var getFatoratiStepTwothreeResponseListner =
        SingleLiveEvent<BillPaymentFatoratiStepThreeResponse>()
    var getFatoratiStepThreeResponseListner =
        SingleLiveEvent<BillPaymentFatoratiStepThreeResponse>()
    var getFatoratiStepFourResponseListner = SingleLiveEvent<BillPaymentFatoratiStepFourResponse>()

    var listOfFatoratiQuote = arrayListOf<BillPaymentFatoratiQuoteResponse>()
    var getPostPaidFatoratiQuoteResponseListner =
        SingleLiveEvent<BillPaymentFatoratiQuoteResponse>()

    var listOfFatorati = arrayListOf<BillPaymentFatoratiResponse>()
    var getPostPaidFatoratiResponseListner = SingleLiveEvent<BillPaymentFatoratiResponse>()

    var listOfSelectedBillAmount: ArrayList<String> = arrayListOf()
    var listOfSelectedBillFee: ArrayList<String> = arrayListOf()

    var postPaidCounter = 0
    var triggerPostPaidNextCall = SingleLiveEvent<Boolean>()

    var fatoratiCounter = 0
    var triggerFatoratiNextCall = SingleLiveEvent<Boolean>()


    //Request For PostPaidFinancialResourceInfo
    fun requestForPostPaidFinancialResourceInfoApi(
        context: Context?,
        code: String,
        receiver: String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            var mReceiver = ""
            if (isPostPaidMobileSelected.get()!!) {
                mReceiver = Constants.getPostPaidMobileDomainAlias(receiver)
                domain = "1"
            }
            if (isPostPaidFixSelected.get()!!) {
                mReceiver = Constants.getPostPaidFixedDomainAlias(receiver)
                domain = "2"
            }
            if (isInternetSelected.get()!!) {
                mReceiver = Constants.getPostPaidInternetDomainAlias(receiver)
                domain = "3"
            }
            transferdAmountTo = mReceiver
            mCodeEntered = code

            disposable =
                ApiClient.newApiClientInstance?.getServerAPI()?.getPostPaidFinancialResourceInfo(
                    PostPaidFinancialResourceInfoRequest(
                        code,
                        ApiConstant.CONTEXT_AFTER_LOGIN,
                        mReceiver,
                        Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN)
                    )
                )
                    .compose(applyIOSchedulers())
                    .subscribe(
                        { result ->
                            isLoading.set(false)

                            if (result?.responseCode != null) {
                                when (result?.responseCode) {
                                    ApiConstant.API_SUCCESS -> {
                                        getPostPaidResourceInfoResponseListner.postValue(result)
                                        PostPaidFinancialResourceInfoObserver.set(result)
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
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {
            isLoading.set(true)
            //   var convertedAmountValue = (selectBillAmount.toDouble()/Constants.AMOUNT_CONVERSION_VALUE.toDouble()).toString()

            var convertedAmountValue = ""
            var convertedOpenAmount = ""


            try {
                convertedAmountValue = String.format(
                    Locale.US,
                    "%.2f",
                    (selectBillAmount.toDouble() / Constants.AMOUNT_CONVERSION_VALUE.toDouble())

                )

                if (convertedAmountValue.contains(",")) {
                    convertedAmountValue = convertedAmountValue.replace(",", ".")
                }
                Logger.debugLog("convertedAmountValue", convertedAmountValue)
                val symbolsEN_US: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.US)
                convertedOpenAmount =
                    (DecimalFormat(
                        "#",
                        symbolsEN_US
                    ).format((convertedAmountValue.toDouble() * Constants.AMOUNT_CONVERSION_VALUE.toDouble()))).toString()

                if (convertedOpenAmount.contains(",")) {
                    convertedOpenAmount = convertedOpenAmount.replace(",", ".")
                }
                // convertedOpenAmount = String.format("%.0f", convertedOpenAmount)

                Logger.debugLog("convertedOpenAmount", convertedOpenAmount)

            } catch (e: Exception) {

            }

            disposable =
                ApiClient.newApiClientInstance?.getServerAPI()?.getPostPaidBillPaymentQuote(
                    PostPaidBillPaymentQuoteRequest(
                        convertedAmountValue,
                        mCodeEntered,
                        ApiConstant.CONTEXT_AFTER_LOGIN,
                        custId,
                        custname,
                        "1",
                        transferdAmountTo,
                        Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),
                        selectBillAmount,
                        Constants.TYPE_PAYMENT,
                        domain,
                        invoiceMonth,
                        ohrefnum,
                        ohxact,
                        convertedOpenAmount
                    )
                )
                    .compose(applyIOSchedulers())
                    .subscribe(
                        { result ->
                            isLoading.set(false)

                            if (result?.responseCode != null) {
                                when (result?.responseCode) {
                                    ApiConstant.API_SUCCESS -> {
                                        listOfPostPaidBillPaymentQuote.add(result)
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
                                        listOfPostPaidBillPaymentQuote.add(result)
                                    }
                                }
                            } else {
                                listOfPostPaidBillPaymentQuote.add(result)
                            }

                            if (listOfPostPaidBillPaymentQuote.size.equals(totalBillSelected)) {
                                getPostPaidBillPaymentQuoteResponseListner.postValue(
                                    listOfPostPaidBillPaymentQuote
                                )
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
        context: Context?, invoiceMonth: String,
        ohrefnum: String,
        ohxact: String,
        openAmount: String, mQuoteId: String,
        isMultipleBillSelected: String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {
            isLoading.set(true)
            var convertedAmountValue = ""
            var convertedOpenAmount = ""


            try {
                convertedAmountValue = String.format(
                    Locale.US,
                    "%.2f",
                    (selectBillAmount.toDouble() / Constants.AMOUNT_CONVERSION_VALUE.toDouble())
                )

                if (convertedAmountValue.contains(",")) {
                    convertedAmountValue = convertedAmountValue.replace(",", ".")
                }

                Logger.debugLog("convertedAmountValue", convertedAmountValue)
                val symbolsEN_US: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.US)

                convertedOpenAmount =
                    (DecimalFormat(
                        "#",
                        symbolsEN_US
                    ).format((convertedAmountValue.toDouble() * Constants.AMOUNT_CONVERSION_VALUE.toDouble()))).toString()

                // convertedOpenAmount = String.format("%.0f", convertedOpenAmount)
                if (convertedOpenAmount.contains(",")) {
                    convertedOpenAmount = convertedOpenAmount.replace(",", ".")
                }
                Logger.debugLog("convertedOpenAmount", convertedOpenAmount)

            } catch (e: Exception) {

            }

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getPostPaidBillPayment(
                PostPaidBillPaymentRequest(
                    convertedAmountValue,
                    mCodeEntered,
                    ApiConstant.CONTEXT_AFTER_LOGIN,
                    custId,
                    custname,
                    "1",
                    transferdAmountTo,
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),
                    selectBillAmount,
                    Constants.TYPE_PAYMENT,
                    domain,
                    invoiceMonth,
                    ohrefnum,
                    ohxact,
                    convertedOpenAmount,
                    isMultipleBillSelected
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when (result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    listOfPostPaidBillPayment.add(result)
                                    if (postPaidCounter < totalBillSelected) {
                                        triggerPostPaidNextCall.postValue(true)
                                    }
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
                                    listOfPostPaidBillPayment.add(result)
                                }
                            }

                        } else {
                            listOfPostPaidBillPayment.add(result)
                        }

                        if (listOfPostPaidBillPayment.size.equals(totalBillSelected)) {
                            selectedIvoicesBillPaymentResponseValue.set(listOfPostPaidBillPayment)
                            getPostPaidBillPaymentResponseListner.postValue(
                                listOfPostPaidBillPayment
                            )
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
    fun requestForAddFavoritesApi(
        context: Context?,
        contactName: String,
        tranferAmountToWithoutAlias: String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAddContact(
                AddContactRequest(
                    tranferAmountToWithoutAlias,
                    contactName,
                    ApiConstant.CONTEXT_AFTER_LOGIN,
                    "",
                    ""
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when (result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getAddFavoritesResponseListner.postValue(result)
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
    fun requestForFatoratiStepOneApi(
        context: Context?
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            Logger.debugLog("billPayment", "isLoading ${isLoading}")
            disposable =
                ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatoratiStepOne(
                    BillPaymentFatoratiStepOneRequest(
                        ApiConstant.CONTEXT_AFTER_LOGIN,
                        Constants.OPERATION_TYPE_CREANCIER,
                        Constants.getFatoratiAlias(Constants.CURRENT_USER_MSISDN),
                        Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN)
                    )
                )
                    .compose(applyIOSchedulers())
                    .subscribe(
                        { result ->
                            isLoading.set(false)
                            Logger.debugLog("billPayment", "isLoading ${isLoading}")
                            if (result?.responseCode != null) {
                                when (result?.responseCode) {
                                    ApiConstant.API_SUCCESS -> {
                                        fatoratiStepOneObserver.set(result)
                                        getFatoratiStepOneResponseListner.postValue(result)
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
    fun requestForFatoratiStepTwoApi(
        context: Context?,
        receiver: String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            transferdAmountTo = receiver
            var reciever: Any
            reciever = Constants.getFatoratiServiceProviderAlias(
                transferdAmountTo,
                selectedCompanyServiceProvider
            )
            disposable =
                ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatoratiStepTwo(
                    BillPaymentFatoratiStepTwoRequest(
                        ApiConstant.CONTEXT_AFTER_LOGIN,
                        fatoratiTypeSelected.get()!!.codeCreancier,
                        Constants.OPERATION_TYPE_CREANCE,
                        reciever,
                        Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN)
                    )
                )
                    .compose(applyIOSchedulers())
                    .subscribe(
                        { result ->


                            if (result?.responseCode != null) {
                                when (result?.responseCode) {
                                    ApiConstant.API_SUCCESS -> {
                                        if (result.creances.size > 1) {
                                            isLoading.set(false)
                                        }

                                        fatoratiStepTwoObserver.set(result)
                                        getFatoratiStepTwoResponseListner.postValue(result)
                                    }
                                    ApiConstant.API_SESSION_OUT -> {
                                        isLoading.set(false)
                                        (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                            context as BillPaymentActivity,
                                            LoginActivity::class.java,
                                            LoginActivity.KEY_REDIRECT_USER_SESSION_OUT
                                        )
                                    }
                                    ApiConstant.API_INVALID -> {
                                        isLoading.set(false)
                                        (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                            context as BillPaymentActivity,
                                            LoginActivity::class.java,
                                            LoginActivity.KEY_REDIRECT_USER_INVALID
                                        )
                                    }
                                    else -> {
                                        isLoading.set(false)
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

    //Request For FatoratiStepTwoThree ** we are getting step 3 response by caling this api **
    fun requestForFatoratiStepTwoThreeApi(
        context: Context?,
        receiver: String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {
            transferdAmountTo = receiver
            var reciever: Any
            reciever = Constants.getFatoratiServiceProviderAlias(
                transferdAmountTo,
                selectedCompanyServiceProvider
            )
            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getFatoratiStepTwoThree(
                BillPaymentFatoratiStepTwoRequest(
                    ApiConstant.CONTEXT_AFTER_LOGIN,
                    fatoratiTypeSelected.get()!!.codeCreancier,
                    Constants.OPERATION_TYPE_CREANCE,
                    reciever,
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN)
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {

                            //............below (639-644) test data for TGR multiple input flow..........

//                            val mparam = com.es.marocapp.model.responses.Param("codeBarre","Référence de l'avis","text")
//                            val lis :ArrayList<com.es.marocapp.model.responses.Param> = ArrayList()
//                            lis.add(mparam)
//                            val mBillPaymentFatoratiStepThreeResponse = BillPaymentFatoratiStepThreeResponse("Operation performed successfully",lis,"","0000")
//                            fatoratiStepThreeObserver.set(result)
//                            getFatoratiStepTwothreeResponseListner.postValue(mBillPaymentFatoratiStepThreeResponse)

                            when (result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    fatoratiStepThreeObserver.set(result)
                                    getFatoratiStepTwothreeResponseListner.postValue(result)
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
                                    fatoratiStepThreeObserver.set(result)
                                    getFatoratiStepTwothreeResponseListner.postValue(result)
                                }
                            }

                        } else {
                            getFatoratiStepTwothreeResponseListner.postValue(result)
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

    //Request For FatoratiStepThree
    fun requestForFatoratiStepThreeApi(
        context: Context?,
        receiver: String,
        codeCreance: String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {
            transferdAmountTo = receiver
            var reciever: Any
            reciever = Constants.getFatoratiServiceProviderAlias(
                transferdAmountTo,
                selectedCompanyServiceProvider
            )
            isLoading.set(true)


            disposable =
                ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatoratiStepThree(
                    BillPaymentFatoratiStepThreeRequest(
                        ApiConstant.CONTEXT_AFTER_LOGIN,
                        fatoratiTypeSelected.get()!!.codeCreancier,
                        "forms",
                        reciever,
                        Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),
                        codeCreance
                    )
                )
                    .compose(applyIOSchedulers())
                    .subscribe(
                        { result ->
                            isLoading.set(false)

                            if (result?.responseCode != null) {
                                when (result?.responseCode) {
                                    ApiConstant.API_SUCCESS -> {
                                        fatoratiStepThreeObserver.set(result)
                                        getFatoratiStepThreeResponseListner.postValue(result)
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
                                        fatoratiStepThreeObserver.set(result)
                                        getFatoratiStepThreeResponseListner.postValue(result)
                                    }
                                }

                            } else {
                                getFatoratiStepThreeResponseListner.postValue(result)
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
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {
            Logger.debugLog("lydec", "creanse ${stepFourLydecSelected}")
            var codeCreance = ""

            codeCreance = selectedCodeCreance
            transferdAmountTo = validatedParams[0].valChamp
            var reciever: Any
            reciever = Constants.getFatoratiServiceProviderAlias(
                transferdAmountTo,
                selectedCompanyServiceProvider
            )

            isLoading.set(true)

            disposable =
                ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatoratiStepFour(
                    BillPaymentFatoratiStepFourRequest(
                        codeCreance,
                        ApiConstant.CONTEXT_AFTER_LOGIN,
                        fatoratiTypeSelected.get()!!.codeCreancier,
                        validatedParams,
                        Constants.OPERATION_TYPE_IMPAYES,
                        reciever,
                        fatoratiStepThreeObserver.get()!!.refTxFatourati,
                        Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN)
                    )
                )

                    .compose(applyIOSchedulers())
                    .subscribe(
                        { result ->
                            isLoading.set(false)

                            if (result?.responseCode != null) {
                                when (result?.responseCode) {
                                    ApiConstant.API_SUCCESS -> {
                                        fatoratiStepFourObserver.set(result)
                                        getFatoratiStepFourResponseListner.postValue(result)
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
    fun requestForFatoratiQuoteApi(
        context: Context?,
        amount: String,
        refTxFatourati: String,
        totalAmount: String,
        paramsForFatoratiPayment: List<FatoratiQuoteParam>
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            var codeCreance = ""

            if (stepFourLydecSelected) {
                codeCreance = selectedCodeCreance
            } else {
                codeCreance = fatoratiTypeSelected.get()!!.codeCreance
            }
            var reciever: Any
            reciever = Constants.getFatoratiServiceProviderAlias(
                transferdAmountTo,
                selectedCompanyServiceProvider
            )

            Logger.debugLog("lydec", "value ${stepFourLydecSelected}=== ${codeCreance}")
            disposable =
                ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatoratiQuote(
                    BillPaymentFatoratiQuoteRequest(
                        Constants.converValueToTwoDecimalPlace(amount.toDouble()),
                        codeCreance,
                        ApiConstant.CONTEXT_AFTER_LOGIN,
                        fatoratiTypeSelected.get()!!.codeCreancier,
                        "true",
                        reciever,
                        Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),
                        Constants.TYPE_BILL_PAYMENT,
                        refTxFatourati,
                        totalAmount,
                        paramsForFatoratiPayment,
                        fatoratiTypeSelected.get()!!.nomCreancier
                    )
                )
                    .compose(applyIOSchedulers())
                    .subscribe(
                        { result ->
                            isLoading.set(false)

                            if (result?.responseCode != null) {
                                when (result?.responseCode) {
                                    ApiConstant.API_SUCCESS -> {
                                        getPostPaidFatoratiQuoteResponseListner.postValue(result)
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
    fun requestForFatoratiApi(
        context: Context?,
        amount: String,
        quoteId: String,
        isMultipleBillSelected: String,
        paramsForFatoratiPayment: List<Param>
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            var codeCreance = ""
            if (stepFourLydecSelected) {
                stepFourLydecSelected = false
                codeCreance = selectedCodeCreance
            } else {
                codeCreance = fatoratiTypeSelected.get()!!.codeCreance
            }
            var reciever: Any
//            if(selectedCreancer.get()?.contains("TSAV")!!)
//            {
//                reciever=Constants.getFatoratiServiceProviderAlias(transferdAmountTo,"Paiement_de_vignette_TSA")
//            }
//            else{
//                reciever=Constants.getFatoratiAlias(transferdAmountTo)
//            }
            reciever = Constants.getFatoratiServiceProviderAlias(
                transferdAmountTo,
                selectedCompanyServiceProvider
            )
            Logger.debugLog("lydec", "value ${stepFourLydecSelected}=== ${codeCreance}")
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatorati(
                BillPaymentFatoratiRequest(
                    Constants.converValueToTwoDecimalPlace(amount.toDouble()),
                    codeCreance,
                    ApiConstant.CONTEXT_AFTER_LOGIN,
                    fatoratiTypeSelected.get()!!.codeCreancier,
                    "true",
                    reciever,
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),
                    Constants.TYPE_BILL_PAYMENT,
                    isMultipleBillSelected,
                    fatoratiStepFourObserver.get()?.refTxFatourati.toString(),
                    fatoratiStepFourObserver.get()?.totalAmount.toString(),
                    paramsForFatoratiPayment,
                    fatoratiTypeSelected.get()!!.nomCreancier
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when (result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getPostPaidFatoratiResponseListner.postValue(result)
                                    /*if(fatoratiCounter<totalBillSelected){
                                        triggerFatoratiNextCall.postValue(true)
                                    }*/
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
    fun requestForDeleteFavoriteApi(
        context: Context?,
        contactIdentity: String,
        billprovidercontactid: Int
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getDeleteContact(
                DeleteContactRequest(
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),
                    ApiConstant.CONTEXT_AFTER_LOGIN,
                    billprovidercontactid.toString()
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->


                        if (result?.responseCode != null) {
                            when (result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getDeleteFavoritesResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> {
                                    isLoading.set(false)
                                    (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                        context as FavoritesActivity, LoginActivity::class.java,
                                        LoginActivity.KEY_REDIRECT_USER_SESSION_OUT
                                    )
                                }
                                ApiConstant.API_INVALID -> {
                                    isLoading.set(false)
                                    (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(
                                        context as FavoritesActivity, LoginActivity::class.java,
                                        LoginActivity.KEY_REDIRECT_USER_INVALID
                                    )
                                }
                                else -> {
                                    isLoading.set(false)
                                    getDeleteFavoritesResponseListner.postValue(result)
                                }
                            }

                        } else {
                            isLoading.set(false)
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
    fun requestForBillPaymentCompaniesApi(
        context: Context?
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            Logger.debugLog("billPayment", "isLoading ${isLoading}")
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentCompanies(
                BillPaymentCompaniesRequest(
                    ApiConstant.CONTEXT_AFTER_LOGIN,
                    Constants.OPERATION_TYPE_CREANCIER,
                    Constants.getFatoratiAlias(Constants.CURRENT_USER_MSISDN),
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN)
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->

                        var arraylistOfBills: ArrayList<Bill> = arrayListOf()
                        arraylistOfBills.addAll(result.bills)
                        val responseCode = result.responseCode
                        val description = result.description
                        val lastBill = arraylistOfBills[arraylistOfBills.size-1]
                        arraylistOfBills.removeAt(arraylistOfBills.size-1)
                        arraylistOfBills.add(0,lastBill)
                        val newResponse = BillPaymentCompaniesResponse(arraylistOfBills,description,responseCode)

                        isLoading.set(false)
                        Logger.debugLog("billPayment", "isLoading ${isLoading}")
                        if (newResponse?.responseCode != null) {
                            when (newResponse?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getBillPaymentCompaniesResponseListner.postValue(newResponse)
                                    getBillPaymentCompaniesResponseObserver.set(newResponse)
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
                                    getBillPaymentCompaniesResponseListner.postValue(newResponse)
                                    getBillPaymentCompaniesResponseObserver.set(newResponse)
                                }
                            }
                        } else {
                            getBillPaymentCompaniesResponseListner.postValue(newResponse)
                            getBillPaymentCompaniesResponseObserver.set(newResponse)
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

    //Request For BillCompaniesFavourites
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

    fun setCreancesList(creances: ArrayList<creances>) {
        creancesList.set(creances)
    }

}