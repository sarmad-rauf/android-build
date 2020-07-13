package com.es.marocapp.usecase.favorites

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.AddContactRequest
import com.es.marocapp.model.requests.BillPaymentFatoratiStepOneRequest
import com.es.marocapp.model.requests.DeleteContactRequest
import com.es.marocapp.model.responses.AddContactResponse
import com.es.marocapp.model.responses.BillPaymentFatoratiStepOneResponse
import com.es.marocapp.model.responses.BillPaymentFatoratiStepTwoResponse
import com.es.marocapp.model.responses.DeleteContactResponse
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class FavoritesViewModel(application: Application): AndroidViewModel(application){
    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()

    var popBackStackTo = -1
    var fatoratiTypeSelected = ""

    //Observerable Fileds
    var isPaymentSelected = ObservableField<Boolean>()
    var isFatoratiUsecaseSelected = ObservableField<Boolean>()
    var selectedFavoritesType = ObservableField<String>()
    var selectedFavoritesAction = ObservableField<String>()

    //Fatorati API Listner
    var getFatoratiStepOneResponseListner = SingleLiveEvent<BillPaymentFatoratiStepOneResponse>()
    var fatoratiStepOneObserver = ObservableField<BillPaymentFatoratiStepOneResponse>()

    var getAddFavoritesResponseListner = SingleLiveEvent<AddContactResponse>()
    var getDeleteFavoritesResponseListner = SingleLiveEvent<DeleteContactResponse>()


    //Request For FatoratiStepOne
    fun requestForFatoratiStepOneApi(context: Context?
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatoratiStepOne(
                BillPaymentFatoratiStepOneRequest(
                    ApiConstant.CONTEXT_AFTER_LOGIN,
                    Constants.OPERATION_TYPE_CREANCIER,
                    Constants.getFatoratiAlias(Constants.CURRENT_USER_MSISDN),
                    Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN))
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

    //Request For AddFavorites
    fun requestForAddFavoritesApi(context: Context?,
                                  contactName : String,
                                  contactNumber : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getAddContact(
                AddContactRequest(contactNumber,contactName,ApiConstant.CONTEXT_AFTER_LOGIN)
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
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as BillPaymentActivity, LoginActivity::class.java,
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


}