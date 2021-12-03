package com.es.marocapp.usecase.login

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.es.marocapp.R
import com.es.marocapp.model.requests.*
import com.es.marocapp.model.responses.*
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.security.EncryptionUtils
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.Logger
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    var selectedFileBackPath:String = ""
    var selectedFileFrontPath:String = ""
    var accountHolderEnterdData = ObservableField<Accountholder>()
    var profileSelected: String = "Level 1"
    var getContactResponseListner = SingleLiveEvent<AddBillProviderContactResponse>()
    var currentUserMSISDN: String=""
    private val SPLASH_DISPLAY_LENGTH = 1000
    val mHandler = MutableLiveData<Boolean>()
    var isSignUpFlow : ObservableField<Boolean> = ObservableField(false)
    var activeUserWithoutPassword : ObservableField<Boolean> = ObservableField(false)
    var activeUserWithoutPasswordType : ObservableField<Boolean> = ObservableField(false)
    var isNewUserRegisterd : ObservableField<Boolean> = ObservableField(false)
    var isFromLoginUserScreen : ObservableField<Boolean> = ObservableField(false)
    lateinit var accountHolderInfoResponse : GetAccountHolderInformationResponse
    var accountHolderInfoUserProfile : String? =null
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()
    lateinit var disposable: Disposable

    var mUserMsisdn = ""
    var mEncryptedNonce : String? = ""
    var DOB = ""
    var identificationNumber  = ""
    var firstName = ""
    var gender = ""
    var postalAddress = ""

    var lastName = ""
    var email = ""
    var city = ""
    var previousDeviceId = ""
    var isDeviceChanged = false
    var isSimplePopUp = true
    var isResetPassowrdFlow = false
    var isForgotPasswordDialogToShow = true

    var isUserToShowProfile = false

    var getAccountDetailResponseListner = SingleLiveEvent<GetAccountHolderInformationResponse>()
    var getProfileResponseListner = SingleLiveEvent<GetProfileResponse>()
    var getPinResetStatusListner = SingleLiveEvent<UpgradeProfileResponse>()
    var getInitialAuthDetailsResponseListner = SingleLiveEvent<GetInitialAuthDetailsReponse>()
    var getOtpForRegistrationResponseListner = SingleLiveEvent<GetOtpForRegistrationResponse>()
    var getSimppleOtpForRegistrationResponseListner = SingleLiveEvent<GetOtpSimpleResponse>()
    var getVerifyOtpResponseListner = SingleLiveEvent<VerifyOtpResponse>()
    var getRegisterUserResponseListner = SingleLiveEvent<RegisterUserResponse>()
    var getActivateUserResponseListner = SingleLiveEvent<ActivateUserResponse>()
    var getOTPResponseListner = SingleLiveEvent<GetOptResponse>()
    var getValidateOtpAndUpdateAliasResponseListner = SingleLiveEvent<ValidateOtpAndUpdateAliasesResponse>()
    var getForgotPasswordResponseListner = SingleLiveEvent<ForgotPasswordResponse>()
    var getCreateCredentialsResponseListner = SingleLiveEvent<CreateCredentialResponse>()
    var getLoginWithCertResponseListner = SingleLiveEvent<LoginWithCertResponse>()
    var getAccountHolderEmailResponseListner = SingleLiveEvent<AccountHolderEmailResponse>()
    var getBalanceInforAndLimitResponseListner = SingleLiveEvent<BalanceInfoAndLimitResponse>()
    var getAccountsResponseListner = SingleLiveEvent<GetAccountsResponse>()
    var getBalanceAndGenerateOtpResponseListner = SingleLiveEvent<GetBalanceAndGenerateOtpResponse>()
    var upgradeProfileFileUploadResponseListener = SingleLiveEvent<UploadFileResponse>()
    var upgradeProfileResponseListener = SingleLiveEvent<UpgradeProfileResponse>()

    fun requestForLevelTwoProfileRegistration(
        appContext: Context,
        deviceID_UserMsisdn : String,
        frontImage: String,
        backImage: String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {
            isLoading.set(true)
            disposable =
                ApiClient.newApiClientInstance?.getServerAPI()?.LevelTwoRegistration(
                    LevelTwoRegistrationRequest(
                        Accountholder(DOB,identificationNumber,firstName,gender,postalAddress,lastName,city),
                        ApiConstant.CONTEXT_BEFORE_LOGIN,deviceID_UserMsisdn,email,Constants.getNumberMsisdn(mUserMsisdn),
                        FileData(
                            frontImage,
                            ProviderUploadDocumentRequest(
                                "front image",
                                "1111",
                                Constants.CURRENT_USER_MSISDN + "-front-" + System.currentTimeMillis() + ".jpg"
                            )
                        ),
                        FileData(
                            backImage,
                            ProviderUploadDocumentRequest(
                                "back image",
                                "1111",
                                Constants.CURRENT_USER_MSISDN + "-back-" + System.currentTimeMillis() + ".jpg"
                            )
                        )
                    )
                ).compose(applyIOSchedulers())
                    .subscribe(
                        { result ->
                            isLoading.set(false)
                            if (result?.responseCode != null) {
                                getRegisterUserResponseListner.postValue(result)

                            } else {
                                errorText.postValue(appContext!!.getString(R.string.error_msg_generic))
                            }
                        },
                        { error ->
                            isLoading.set(false)
                            //Display Error Result Code with with Configure Message
                            try {
                                if (appContext  != null && error != null) {
                                    errorText.postValue(appContext.getString(R.string.error_msg_generic) + (error as HttpException).code())
                                }
                            } catch (e: Exception) {
                                errorText.postValue(appContext!!.getString(R.string.error_msg_generic))
                            }
                        })
        } else {
            errorText.postValue(Constants.SHOW_INTERNET_ERROR)
        }
    }

    private fun postDelay() {

        android.os.Handler().postDelayed(Runnable {

            mHandler.postValue(true)

        }, SPLASH_DISPLAY_LENGTH.toLong())


    }

    // API For GETACCOUNTS API
    fun requestForGetAccountsAPI(
        context: Context?
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAccountsCall(
                GetAccountsRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(mUserMsisdn))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null )
                        {
                            getAccountsResponseListner.postValue(result)

                        } else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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

    // API Called on reset password scree to update pin reset status
    fun requestForupdatePinResetStatusApi(
        context: Context?,
        userMsisdn: String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            mUserMsisdn = userMsisdn

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getUpdatePinResetStatus(
                GetProfileRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.getNumberMsisdn(userMsisdn))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->

                        isLoading.set(false)

                        if (result?.responseCode != null )
                         {
                             getPinResetStatusListner.postValue(result)
                        }else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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


    // API Called on Login Screen to get profile name
    fun requestForGetProfileApi(
        context: Context?,
        userMsisdn: String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            mUserMsisdn = userMsisdn

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getProfile(
                GetProfileRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.getNumberMsisdn(userMsisdn))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->

                        isLoading.set(false)

                        if (result?.responseCode != null )
                        {
                            Logger.debugLog("Abro"," userTypeResults ${result.userType}")
                            if(result.responseCode.equals(ApiConstant.API_SUCCESS))
                            {
                                if(result.userType.contains("agent"))
                                {

                                    Constants.IS_AGENT_USER=true
                                    Logger.debugLog("userType"," isAgentUser ${Constants.IS_AGENT_USER}")
                                }
                            }

                            getProfileResponseListner.postValue(result)
                        }else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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


     //getAcount detail api
    fun requestForGetAccountDetailApi(
        context: Context?,
        userMsisdn: String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            mUserMsisdn = userMsisdn

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAccountDetail(
                GetAccountDetailRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.getNumberMsisdn(userMsisdn),Constants.CURRENT_NUMBER_DEVICE_ID)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null )
                        {
                            getAccountDetailResponseListner.postValue(result)
                        }else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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

    // API Called on SignUp Detail Fragment For Registration Purpose
    fun requestForeGetInitialAuthDetailsApi(
        context: Context?
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            //TODO NonceKey Hardcoded need to resolve
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getInitialAuthDetials(
                GetInitialAuthDetailsRequest(Constants.getNumberMsisdn(mUserMsisdn),"0f0fd1e1a9c07932c350368910f8871d230cdf0bf52c550b1f4f03bee9a7b68a")
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            mEncryptedNonce = result.encryptedNonce
                            getInitialAuthDetailsResponseListner.postValue(result)

                        } else {
                            getInitialAuthDetailsResponseListner.postValue(result)
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

    // API Called on SignUp Detail Fragment For Registration Purpose
    fun requestForGetOTPForRegistrationApi(
        context: Context?,
        firstName : String,
        lastName : String,
        identificationNumber : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            //TODO authorization & MSISDN parameter pending
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getOTPForRegistration(
                GetOtpForRegistrationRequest(ApiConstant.CONTEXT_BEFORE_LOGIN, firstName,identificationNumber,Constants.IDENTIFICATION_TYPE,
                    Constants.getNumberMsisdn(mUserMsisdn),lastName)

            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            getOtpForRegistrationResponseListner.postValue(result)

                        } else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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


    // API Called on OTP Fragment For Registration Purpose OTP
    fun requestForGetOtp(
        context: Context?
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            //TODO authorization & MSISDN parameter pending
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getSimpleOTPForRegistration(
                GetOtpSimpleRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,
                    Constants.getNumberMsisdn(mUserMsisdn))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            getSimppleOtpForRegistrationResponseListner.postValue(result)

                        } else {
                            errorText.postValue(result.description)
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

    // API Called on Verify OTP Fragment For Registration Purpose OTP
    fun requestForVerifyOtp(
        context: Context?,
        otp : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            //TODO authorization & MSISDN parameter pending
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getVerifyOtp(
                VerifyOtpRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,
                    Constants.getNumberMsisdn(mUserMsisdn),EncryptionUtils.encryptString(otp))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            getVerifyOtpResponseListner.postValue(result)

                        } else {
                            errorText.postValue(result.description)
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

    // API Called on SignUp Detail Fragment For Registration Purpose
    fun requestForRegisterUserApi(
        context: Context?,
        deviceID_UserMsisdn : String
    ) {


        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getRegisterUser(
                RegisterUserRequest(Accountholder(DOB,identificationNumber,firstName,gender,postalAddress,lastName,city),
                    ApiConstant.CONTEXT_BEFORE_LOGIN,deviceID_UserMsisdn,email,Constants.getNumberMsisdn(mUserMsisdn),Constants.REASON_FOR_UPDATE_PROFILE)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            getRegisterUserResponseListner.postValue(result)

                        } else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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


    // API Called on SetYourPin Fragment For Pin Registration Purpose
    fun requestForActivateUserApi(
        context: Context?,
        seceret : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            if(accountHolderInfoUserProfile.isNullOrEmpty()){
                accountHolderInfoUserProfile=""
            }

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getActivateUser(
                ActivateUserRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(mUserMsisdn),EncryptionUtils.encryptString(seceret),Constants.SECRET_TYPE,accountHolderInfoUserProfile!!)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            getActivateUserResponseListner.postValue(result)

                        } else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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

    // API Called on LOGIN Fragment IF Device ID Is CHanged or not Matched
    fun requestForGetOtpApi(
        context: Context?
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            //TODO hardcoded Value need to Resolve
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getOTP(
                GetOptRequest("NzgzMzU3MTI0",ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.getNumberMsisdn(mUserMsisdn))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null )
                        {
                            getOTPResponseListner.postValue(result)

                        } else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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

    // API Called on LOGIN Fragment To Add New Device ID
    fun requestForVerifyOtpAndUpdateAliaseAPI(
        context: Context?,
        newDeviceID : String,
        otp : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getValidateOtpAndUpdateAliases(
                ValidateOtpAndUpdateAliasesRequest(newDeviceID,ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.getNumberMsisdn(mUserMsisdn),EncryptionUtils.encryptString(otp))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null )
                        {
                            if(result?.responseCode==ApiConstant.API_SUCCESS)
                            {
                                Logger.debugLog("Abro","alias result ${result.toString()}")
                                getAccountDetailResponseListner.postValue(result.getaccountholderinfo)
                               // getValidateOtpAndUpdateAliasResponseListner.postValue(result)
                            }
                            else {
                                getValidateOtpAndUpdateAliasResponseListner.postValue(result)
                            }
                        } else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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

    // API For Forgot Password
    fun requestForForgotPasswordAPI(
        context: Context?,
        newSecret : String,
        otp : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getForgotPasswordCall(
                ForgotPasswordRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,ApiConstant.APP_CREDENTIAL_TYPE,Constants.getNumberMsisdn(mUserMsisdn),EncryptionUtils.encryptString(newSecret),EncryptionUtils.encryptString(otp),EncryptionUtils.encryptString(newSecret))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            getForgotPasswordResponseListner.postValue(result)

                        } else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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

    // API For Create Credential API
    fun requestForCreateCredentialsAPI(
        context: Context?,
        newSecret : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getCreateCredentialCall(
                CreateCredentialRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.getNumberMsisdn(mUserMsisdn),EncryptionUtils.encryptString(newSecret),ApiConstant.APP_CREDENTIAL_TYPE)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            getCreateCredentialsResponseListner.postValue(result)

                        } else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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

    // API For Login With Cert API
    fun requestForLogigWithCertAPI(
        context: Context?,
        secret : String,
        versionName : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            //todo remove hardcoded value
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getLoginWithCertCall(
                LoginWithCertRequest(versionName,"MzU3ODc2Nzgz",ApiConstant.CONTEXT_AFTER_LOGIN,Constants.CURRENT_NUMBER_DEVICE_ID,Constants.getNumberMsisdn(mUserMsisdn),
                    EncryptionUtils.encryptString(secret),ApiConstant.APP_CREDENTIAL_TYPE,"CODE_LOGIN_S2_")
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        Logger.debugLog("userType","cert  ${result.toString()}")

                        if(result?.responseCode != null){
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS ->  getLoginWithCertResponseListner.postValue(result)
                                ApiConstant.API_SESSION_OUT ->
                                {  isLoading.set(false)
                                    (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as LoginActivity, LoginActivity::class.java,LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)}
                                ApiConstant.API_INVALID ->{isLoading.set(false)
                                    (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as LoginActivity, LoginActivity::class.java,LoginActivity.KEY_REDIRECT_USER_INVALID)}
                                else -> {
                                    isLoading.set(false)
                                    getLoginWithCertResponseListner.postValue(result)}
                            }
                        }
                        else{
                            getLoginWithCertResponseListner.postValue(result)
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

    // API For getting Email
    fun requestForAccountholderDefaultNotificationEmailAPI(
        context: Context?
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAccountHolderEmailCall(
                AccountHolderEmailequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(mUserMsisdn))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        Logger.debugLog("email","email  ${result.toString()}")
                        if(result?.responseCode != null){
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS ->  getAccountHolderEmailResponseListner.postValue(result)
                             //   ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as LoginActivity, LoginActivity::class.java,LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                             //   ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as LoginActivity, LoginActivity::class.java,LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  getAccountHolderEmailResponseListner.postValue(result)
                            }
                        }
                        else{
                            getAccountHolderEmailResponseListner.postValue(result)
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

    // API For BalanceInfoAndLimit API
    fun requestForBalanceInfoAndLimtsAPI(
        context: Context?
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBalancesInfoAndLimtCall(
                BalanceInfoAndLimtRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(mUserMsisdn))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->


                        if (result?.responseCode != null )
                        {
                            getBalanceInforAndLimitResponseListner.postValue(result)

                        } else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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






    fun requestForGetBalanceAndGenerateOtpApi(
        context: Context?,
        profileName : String,
        currentUserMsisdn : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBalancesAndGenerateOtp(
                GetBalanceAndGenerateOtpRequest(ApiConstant.CONTEXT_BEFORE_LOGIN, profileName,Constants.getNumberMsisdn(currentUserMsisdn))

            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            getBalanceAndGenerateOtpResponseListner.postValue(result)

                        } else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
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
    fun requestForGetFavouriteApi(context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {


            Logger.debugLog("billPayment","isLoading ${isLoading}")
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getContact(
                GetContactRequest(
                    ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN)
                )
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        Logger.debugLog("billPayment","isLoading ${isLoading}")
                        if (result?.responseCode != null) {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {

                                    getContactResponseListner.postValue(result)
                                    if(result.contactsList!=null) {
                                        Constants.mContactListArray.clear()
                                        Constants.mContactListArray.addAll(result.contactsList)
                                    }

                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
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
                        } catch (e: Exception) {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
                        }

                    })


        } else {

            errorText.postValue(Constants.SHOW_INTERNET_ERROR)
        }

    }

}