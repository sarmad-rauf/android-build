package com.es.marocapp.usecase

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.GetApprovalRequest
import com.es.marocapp.model.requests.LogoutUserRequest
import com.es.marocapp.model.responses.GetApprovalsResponse
import com.es.marocapp.model.responses.LogOutUserResponse
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class MainActivityViewModel(application: Application) : AndroidViewModel(application){

    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()
    var getLogOutUserResponseListner = SingleLiveEvent<LogOutUserResponse>()

    //Request For Log Out User
    fun requestForLogOutUserApi(context: Context?)
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getLogOutUserCall(
                LogoutUserRequest(ApiConstant.CONTEXT_AFTER_LOGIN)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                      /*  if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getLogOutUserResponseListner.postValue(result)

                        } else {
                            getLogOutUserResponseListner.postValue(result)
                        }*/

                        if (result?.responseCode != null )
                         {
                            getLogOutUserResponseListner.postValue(result)

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