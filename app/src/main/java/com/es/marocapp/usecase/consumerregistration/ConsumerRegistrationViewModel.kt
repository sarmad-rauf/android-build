package com.es.marocapp.usecase.consumerregistration

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.*
import com.es.marocapp.model.responses.GetInitialAuthDetailsReponse
import com.es.marocapp.model.responses.GetOtpForRegistrationResponse
import com.es.marocapp.model.responses.RegisterUserResponse
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.security.EncryptionUtils
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class ConsumerRegistrationViewModel(application: Application) : AndroidViewModel(application){
    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()

    var getInitialAuthDetailsResponseListner = SingleLiveEvent<GetInitialAuthDetailsReponse>()
    var getOtpForRegistrationResponseListner = SingleLiveEvent<GetOtpForRegistrationResponse>()
    var getRegisterUserResponseListner = SingleLiveEvent<RegisterUserResponse>()

    var popBackStackTo = -1

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

    // API Called on SignUp Detail Fragment For Registration Purpose
    fun requestForeGetInitialAuthDetailsApi(
        context: Context?,
        consumerNumber : String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)
            mUserMsisdn = consumerNumber

            //TODO NonceKey Hardcoded need to resolve
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getInitialAuthDetials(
                GetInitialAuthDetailsRequest(Constants.getNumberMsisdn(consumerNumber),"0f0fd1e1a9c07932c350368910f8871d230cdf0bf52c550b1f4f03bee9a7b68a")
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    mEncryptedNonce = result.encryptedNonce
                                    getInitialAuthDetailsResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as ConsumerRegistrationActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as ConsumerRegistrationActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getInitialAuthDetailsResponseListner.postValue(result)
                                }
                            }
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
                GetOtpForRegistrationRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,
                    firstName,identificationNumber,Constants.IDENTIFICATION_TYPE,Constants.getNumberMsisdn(mUserMsisdn),lastName)

            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getOtpForRegistrationResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as ConsumerRegistrationActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as ConsumerRegistrationActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getOtpForRegistrationResponseListner.postValue(result)
                                }
                            }

                        } else {
                            getOtpForRegistrationResponseListner.postValue(result)
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

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getRegisterConsumerUser(
                RegisterConsumerUserRequest(
                    Accountholder(DOB,identificationNumber,firstName,gender,postalAddress,lastName,city),
                    ApiConstant.CONTEXT_BEFORE_LOGIN,deviceID_UserMsisdn,email,Constants.getNumberMsisdn(mUserMsisdn),
                    EncryptionUtils.encryptString(otp),Constants.REASON_FOR_UPDATE_PROFILE)

            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null) {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    getRegisterUserResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as ConsumerRegistrationActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as ConsumerRegistrationActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    getRegisterUserResponseListner.postValue(result)
                                }
                            }
                        } else {
                            getRegisterUserResponseListner.postValue(result)
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