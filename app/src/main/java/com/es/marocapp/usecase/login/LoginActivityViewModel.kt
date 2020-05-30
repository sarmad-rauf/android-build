package com.es.marocapp.usecase.login

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
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

class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val SPLASH_DISPLAY_LENGTH = 1000
    val mHandler = MutableLiveData<Boolean>()
    var isSignUpFlow : ObservableField<Boolean> = ObservableField(false)
    var isLoading = ObservableField<Boolean>()
    var errorText = MutableLiveData<String>()
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

    var getAccountHolderInformationResponseListner = SingleLiveEvent<GetAccountHolderInformationResponse>()
    var getInitialAuthDetailsResponseListner = SingleLiveEvent<GetInitialAuthDetailsReponse>()
    var getOtpForRegistrationResponseListner = SingleLiveEvent<GetOtpForRegistrationResponse>()
    var getRegisterUserResponseListner = SingleLiveEvent<RegisterUserResponse>()
    var getActivateUserResponseListner = SingleLiveEvent<ActivateUserResponse>()
    var getOTPResponseListner = SingleLiveEvent<GetOptResponse>()
    var getValidateOtpAndUpdateAliasResponseListner = SingleLiveEvent<ValidateOtpAndUpdateAliasesResponse>()

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

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getAccountHolderInformationResponseListner.postValue(result)

                        }else if(result?.responseCode!=null && result.responseCode.equals(ApiConstant.API_FAILURE,true)){
                            // if response code is 1500 this means user  user isnot registered redirecting user to Registration flow
                            getAccountHolderInformationResponseListner.postValue(result)
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
                                errorText.postValue(context.getString(R.string.error_msg_network) + (error as HttpException).code())
                            }
                        } catch (e: Exception) {
                            errorText.postValue(context!!.getString(R.string.error_msg_network))
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
                            errorText.postValue(Constants.SHOW_SERVER_ERROR)
                        }


                    },
                    { error ->
                        isLoading.set(false)

                        //Display Error Result Code with with Configure Message
                        try {
                            if (context != null && error != null) {
                                errorText.postValue(context.getString(R.string.error_msg_network) + (error as HttpException).code())
                            }
                        } catch (e: Exception) {
                            errorText.postValue(context!!.getString(R.string.error_msg_network))
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
                GetOtpForRegistrationRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,"NzgzMzU3MTI0",mEncryptedNonce!!,
                firstName,identificationNumber,Constants.IDENTIFICATION_TYPE,Constants.getNumberMsisdn(mUserMsisdn),lastName)

            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getOtpForRegistrationResponseListner.postValue(result)

                        } else {
                            errorText.postValue(Constants.SHOW_SERVER_ERROR)
                        }


                    },
                    { error ->
                        isLoading.set(false)

                        //Display Error Result Code with with Configure Message
                        try {
                            if (context != null && error != null) {
                                errorText.postValue(context.getString(R.string.error_msg_network) + (error as HttpException).code())
                            }
                        } catch (e: Exception) {
                            errorText.postValue(context!!.getString(R.string.error_msg_network))
                        }

                    })


        } else {

            errorText.postValue(Constants.SHOW_INTERNET_ERROR)
        }

    }

    // API Called on SignUp Detail Fragment For Registration Purpose
    fun requestForRegisterUserApi(
        context: Context?,
        otp : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            //TODO hardcoded Value need to Resolve
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getRegisterUser(
                RegisterUserRequest(Accountholder(DOB,identificationNumber,firstName,gender,postalAddress,lastName),
                    ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.CURRENT_DEVICE_ID,email,Constants.getNumberMsisdn(mUserMsisdn),otp)

            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getRegisterUserResponseListner.postValue(result)

                        } else {
                            errorText.postValue(Constants.SHOW_SERVER_ERROR)
                        }


                    },
                    { error ->
                        isLoading.set(false)

                        //Display Error Result Code with with Configure Message
                        try {
                            if (context != null && error != null) {
                                errorText.postValue(context.getString(R.string.error_msg_network) + (error as HttpException).code())
                            }
                        } catch (e: Exception) {
                            errorText.postValue(context!!.getString(R.string.error_msg_network))
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

            //TODO hardcoded Value need to Resolve
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getActivateUser(
                ActivateUserRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(mUserMsisdn),seceret,Constants.SECRET_TYPE)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getActivateUserResponseListner.postValue(result)

                        } else {
                            errorText.postValue(Constants.SHOW_SERVER_ERROR)
                        }


                    },
                    { error ->
                        isLoading.set(false)

                        //Display Error Result Code with with Configure Message
                        try {
                            if (context != null && error != null) {
                                errorText.postValue(context.getString(R.string.error_msg_network) + (error as HttpException).code())
                            }
                        } catch (e: Exception) {
                            errorText.postValue(context!!.getString(R.string.error_msg_network))
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

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getOTPResponseListner.postValue(result)

                        } else {
                            errorText.postValue(Constants.SHOW_SERVER_ERROR)
                        }


                    },
                    { error ->
                        isLoading.set(false)

                        //Display Error Result Code with with Configure Message
                        try {
                            if (context != null && error != null) {
                                errorText.postValue(context.getString(R.string.error_msg_network) + (error as HttpException).code())
                            }
                        } catch (e: Exception) {
                            errorText.postValue(context!!.getString(R.string.error_msg_network))
                        }

                    })


        } else {

            errorText.postValue(Constants.SHOW_INTERNET_ERROR)
        }

    }

    // API Called on LOGIN Fragment To Add New Device ID
    fun requestForVerifyOtpAndUpdateAliaseAPI(
        context: Context?,
        deviceID : String,
        otp : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            //TODO hardcoded Value need to Resolve
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getValidateOtpAndUpdateAliases(
                ValidateOtpAndUpdateAliasesRequest(deviceID,ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.getNumberMsisdn(mUserMsisdn),otp)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getValidateOtpAndUpdateAliasResponseListner.postValue(result)

                        } else {
                            errorText.postValue(Constants.SHOW_SERVER_ERROR)
                        }


                    },
                    { error ->
                        isLoading.set(false)

                        //Display Error Result Code with with Configure Message
                        try {
                            if (context != null && error != null) {
                                errorText.postValue(context.getString(R.string.error_msg_network) + (error as HttpException).code())
                            }
                        } catch (e: Exception) {
                            errorText.postValue(context!!.getString(R.string.error_msg_network))
                        }

                    })


        } else {

            errorText.postValue(Constants.SHOW_INTERNET_ERROR)
        }

    }


}