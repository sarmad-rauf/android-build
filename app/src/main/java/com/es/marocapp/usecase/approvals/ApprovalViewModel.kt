package com.es.marocapp.usecase.approvals

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.es.marocapp.R
import com.es.marocapp.model.requests.ChangePasswordRequest
import com.es.marocapp.model.requests.GetApprovalRequest
import com.es.marocapp.model.requests.UserApprovalRequest
import com.es.marocapp.model.responses.GetApprovalsResponse
import com.es.marocapp.model.responses.UserApprovalResponse
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.usecase.approvals.model.response.ResponseApprovals
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class ApprovalViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()
    var getApprovalResponseListner = SingleLiveEvent<GetApprovalsResponse>()
    var getUsersApprovalResponseListner = SingleLiveEvent<UserApprovalResponse>()


    //Request For Get Approvals
    fun requestForGetApprovalsApi(context: Context?)
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getApprovalsCall(
                GetApprovalRequest(ApiConstant.CONTEXT_AFTER_LOGIN)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getApprovalResponseListner.postValue(result)

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

    //Request For Get Approvals
    fun requestForUserApprovalsApi(
        context: Context?,
        approveID : String,
        approved : String
    )
    {
        if (Tools.checkNetworkStatus(getApplication())) {

            isLoading.set(true)


            disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getUserApprovalsCall(
                UserApprovalRequest(approveID,approved,ApiConstant.CONTEXT_AFTER_LOGIN)
            )
                .compose(applyIOSchedulers())
                .subscribe(
                    { result ->
                        isLoading.set(false)

                        if (result?.responseCode != null && result?.responseCode!!.equals(
                                ApiConstant.API_SUCCESS, true
                            )
                        ) {
                            getUsersApprovalResponseListner.postValue(result)

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