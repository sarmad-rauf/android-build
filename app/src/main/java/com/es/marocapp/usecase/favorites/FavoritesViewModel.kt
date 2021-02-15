package com.es.marocapp.usecase.favorites

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.*
import com.es.marocapp.model.responses.*
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
    var codeCreance = ""
    var creancierID = ""
    var nomChamp = ""
    var refTxFatourati = ""

    //fatorati special type bil slection
    var specialMenuBillSelected: Boolean = false

    //Observerable Fileds
    var isPaymentSelected = ObservableField<Boolean>()
    var isFatoratiUsecaseSelected = ObservableField<Boolean>()
    var selectedFavoritesType = ObservableField<String>()
    var selectedFavoritesAction = ObservableField<String>()

    //Fatorati API Listner
    var creancesList = ObservableField<ArrayList<creances>>()
    var getFatoratiStepOneResponseListner = SingleLiveEvent<BillPaymentFatoratiStepOneResponse>()
    var fatoratiStepOneObserver = ObservableField<BillPaymentFatoratiStepOneResponse>()

    var getAddFavoritesResponseListner = SingleLiveEvent<AddContactResponse>()
    var getDeleteFavoritesResponseListner = SingleLiveEvent<DeleteContactResponse>()

    var fatoratiStepTwoObserver = ObservableField<BillPaymentFatoratiStepTwoResponse>()
    var fatoratiStepThreeObserver = ObservableField<BillPaymentFatoratiStepThreeResponse>()
    var fatoratiStepTwoThreeObserver = ObservableField<BillPaymentFatoratiStepThreeResponse>()
    var getFatoratiStepTwoResponseListner = SingleLiveEvent<BillPaymentFatoratiStepTwoResponse>()
    var getFatoratiStepThreeResponseListner = SingleLiveEvent<BillPaymentFatoratiStepThreeResponse>()
    var getFatoratiStepTwoThreeResponseListner = SingleLiveEvent<BillPaymentFatoratiStepThreeResponse>()


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
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as FavoritesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as FavoritesActivity, LoginActivity::class.java,
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
                                     receiver: String,
                                     codeCreancier : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatoratiStepTwo(
                BillPaymentFatoratiStepTwoRequest(ApiConstant.CONTEXT_AFTER_LOGIN,codeCreancier,Constants.OPERATION_TYPE_CREANCE,
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
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as FavoritesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as FavoritesActivity, LoginActivity::class.java,
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

    //Request For FatoratiStepTwoThree
    fun requestForFatoratiStepTwoThreeApi(context: Context?,
                                     receiver: String,
                                     codeCreancier : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getFatoratiStepTwoThree(
                BillPaymentFatoratiStepTwoRequest(ApiConstant.CONTEXT_AFTER_LOGIN,codeCreancier,Constants.OPERATION_TYPE_CREANCE,
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
                                    fatoratiStepTwoThreeObserver.set(result)
                                    getFatoratiStepTwoThreeResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as FavoritesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as FavoritesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
                                    fatoratiStepTwoThreeObserver.set(result)
                                    getFatoratiStepTwoThreeResponseListner.postValue(result)
                                }
                            }

                        } else {
                            getFatoratiStepTwoThreeResponseListner.postValue(result)
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
    fun requestForFatoratiStepThreeApi(context: Context?,
                                     receiver: String,
                                     codeCreancier : String,
                                       seprateBillCodeCreance:String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)

            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getBillPaymentFatoratiStepThree(
                BillPaymentFatoratiStepThreeRequest(ApiConstant.CONTEXT_AFTER_LOGIN,codeCreancier,"forms",
                    Constants.getFatoratiAlias(receiver),Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN),seprateBillCodeCreance)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null)
                        {
                            when(result?.responseCode) {
                                ApiConstant.API_SUCCESS -> {
                                    fatoratiStepThreeObserver.set(result)
                                    getFatoratiStepThreeResponseListner.postValue(result)
                                }
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as FavoritesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as FavoritesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_INVALID)
                                else ->  {
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
                                ApiConstant.API_SESSION_OUT -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as FavoritesActivity, LoginActivity::class.java,
                                    LoginActivity.KEY_REDIRECT_USER_SESSION_OUT)
                                ApiConstant.API_INVALID -> (context as BaseActivity<*>).logoutAndRedirectUserToLoginScreen(context as FavoritesActivity, LoginActivity::class.java,
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

    fun setCreancesList(creanceArrayList: ArrayList<creances>) {
      creancesList.set(creanceArrayList)
    }


}