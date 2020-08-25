package com.es.marocapp.usecase.login

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
import com.es.marocapp.security.EncryptionUtils
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val SPLASH_DISPLAY_LENGTH = 1000
    val mHandler = MutableLiveData<Boolean>()
    var isSignUpFlow : ObservableField<Boolean> = ObservableField(false)
    var activeUserWithoutPassword : ObservableField<Boolean> = ObservableField(false)
    var activeUserWithoutPasswordType : ObservableField<Boolean> = ObservableField(false)
    var isNewUserRegisterd : ObservableField<Boolean> = ObservableField(false)
    var isFromLoginUserScreen : ObservableField<Boolean> = ObservableField(false)
    lateinit var accountHolderInfoResponse : GetAccountHolderInformationResponse
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
    var previousDeviceId = ""
    var isDeviceChanged = false
    var isSimplePopUp = true
    var isResetPassowrdFlow = false

    var isUserToShowProfile = false

    var getAccountHolderInformationResponseListner = SingleLiveEvent<GetAccountHolderInformationResponse>()
    var getInitialAuthDetailsResponseListner = SingleLiveEvent<GetInitialAuthDetailsReponse>()
    var getOtpForRegistrationResponseListner = SingleLiveEvent<GetOtpForRegistrationResponse>()
    var getRegisterUserResponseListner = SingleLiveEvent<RegisterUserResponse>()
    var getActivateUserResponseListner = SingleLiveEvent<ActivateUserResponse>()
    var getOTPResponseListner = SingleLiveEvent<GetOptResponse>()
    var getValidateOtpAndUpdateAliasResponseListner = SingleLiveEvent<ValidateOtpAndUpdateAliasesResponse>()
    var getForgotPasswordResponseListner = SingleLiveEvent<ForgotPasswordResponse>()
    var getCreateCredentialsResponseListner = SingleLiveEvent<CreateCredentialResponse>()
    var getLoginWithCertResponseListner = SingleLiveEvent<LoginWithCertResponse>()
    var getBalanceInforAndLimitResponseListner = SingleLiveEvent<BalanceInfoAndLimitResponse>()
    var getAccountsResponseListner = SingleLiveEvent<GetAccountsResponse>()

    private fun postDelay() {

        android.os.Handler().postDelayed(Runnable {

            mHandler.postValue(true)

        }, SPLASH_DISPLAY_LENGTH.toLong())


    }

    // API Called on Login Screen to check weather User is Registered or Not
    fun requestForGetAccountHolderInformationApi(
        context: Context?,
        userMsisdn: String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            mUserMsisdn = userMsisdn

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAccountHolderInformation(
                GetAccountHolderInformationRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.getNumberMsisdn(userMsisdn))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null )
                         {
                            getAccountHolderInformationResponseListner.postValue(result)

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

    // API Called on SignUp Detail Fragment For Registration Purpose
    fun requestForRegisterUserApi(
        context: Context?,
        deviceID_UserMsisdn : String,
        otp : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getRegisterUser(
                RegisterUserRequest(Accountholder(DOB,identificationNumber,firstName,gender,postalAddress,lastName),
                    ApiConstant.CONTEXT_BEFORE_LOGIN,deviceID_UserMsisdn,email,Constants.getNumberMsisdn(mUserMsisdn),EncryptionUtils.encryptString(otp))

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

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getActivateUser(
                ActivateUserRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(mUserMsisdn),EncryptionUtils.encryptString(seceret),Constants.SECRET_TYPE)
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
        previousDeviceID : String,
        newDeviceID : String,
        otp : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getValidateOtpAndUpdateAliases(
                ValidateOtpAndUpdateAliasesRequest(previousDeviceID,newDeviceID,ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.getNumberMsisdn(mUserMsisdn),EncryptionUtils.encryptString(otp))
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null )
                        {
                            getValidateOtpAndUpdateAliasResponseListner.postValue(result)

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
                        isLoading.set(false)
                        if(result?.responseCode != null){
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS ->  getLoginWithCertResponseListner.postValue(result)
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as LoginActivity, LoginActivity::class.java,LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as LoginActivity, LoginActivity::class.java,LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  getLoginWithCertResponseListner.postValue(result)
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
                        isLoading.set(false)

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



}