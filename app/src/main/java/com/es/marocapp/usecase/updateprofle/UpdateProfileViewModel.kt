package com.es.marocapp.usecase.updateprofle

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.es.marocapp.R
import com.es.marocapp.model.requests.*
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class UpdateProfileViewModel(application:Application):AndroidViewModel(application) {

    var popBackStackTo = -1
    var isLoading = ObservableField<Boolean>()
    var isPersonalDataChanged:Boolean=false
    var isCINChanged:Boolean=false
    var isEmailChanged:Boolean=false
    var isAdressChanged:Boolean=false
    var headerTitle = MutableLiveData<String>()
    lateinit var currentProfile :String
    lateinit var firstName :String
    lateinit var surName :String
    lateinit var dateOfBirth :String
    lateinit var identityNumber:String
    lateinit var email:String
    lateinit var adress:String
    lateinit var city:String
    lateinit var idType :String
    var errorText = SingleLiveEvent<String>()
    lateinit var disposable: Disposable
     var UpdateEmailResponseListner= MutableLiveData<UpdateProfileResponse>()
     var UpdateAdressResponseListner=MutableLiveData<UpdateProfileResponse>()
     var UpdateCINResponseListner=MutableLiveData<UpdateProfileResponse>()
     var UpdatePersonalInformationResponseListner=MutableLiveData<UpdateProfileResponse>()


    // API For UpdateEmail API
    fun requestForUpdateEmailAPI(
        context: Context?,
        email:String
    ) {

        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.updateEmailCall(
                UpdateEmailRequest(ApiConstant.CONTEXT_BEFORE_LOGIN,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),email)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null )
                        {
                            UpdateEmailResponseListner.postValue(result)

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

    // API For UpdateAdress API
    fun requestForUpdateAdressAPI(
        context: Context?,
        adress:String,
        city:String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.updateAdressCall(
                UpdateAdressRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),adress,city))
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->


                        if (result?.responseCode != null )
                        {
                            UpdateAdressResponseListner.postValue(result)
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

    // API For UpdateCIN API
    fun requestForUpdateCINAPI(
        context: Context?,
        cin:String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {
            isLoading.set(true)
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.updateCINCall(
                UpdateCINRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),cin)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null )
                        {
                            UpdateCINResponseListner.postValue(result)
                        } else {
                            errorText.postValue(context!!.getString(R.string.error_msg_generic))
                        }
                    },
                    { error ->

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

    // API For PersonalInformation API
    fun requestForUpdatePersonalInformationAPI(
        context: Context?,
        firstName:String,
        lastName:String,
        dateOfBirth:String
    ) {
        if (Tools.checkNetworkStatus(getApplication())) {
            isLoading.set(true)
            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.updatePersonalInformationCall(
                UpdatePersonalInformationRequest(ApiConstant.CONTEXT_AFTER_LOGIN,Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),firstName,lastName,dateOfBirth)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null )
                        {
                            UpdatePersonalInformationResponseListner.postValue(result)
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


    fun setHeaderText(title:String)
    {
        headerTitle.postValue(title)
    }
}