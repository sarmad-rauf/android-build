package com.es.marocapp.usecase.approvals

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.es.marocapp.R
import com.es.marocapp.network.ApiClient
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.network.applyIOSchedulers
import com.es.marocapp.usecase.approvals.model.response.ResponseApprovals
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.Tools
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

class ApprovalViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = MutableLiveData<String>()
    var approvalsData = MutableLiveData<List<ResponseApprovals>?>()


    private val _text = MutableLiveData<String>().apply {
        value = "This is Approvals Fragment"
    }
    val text: LiveData<String> = _text

    init {

    }

    fun requestForApprovalsApi(context: Context?)
    {

        if (Tools.checkNetworkStatus(getApplication())) {

                isLoading.set(true)

                disposable = ApiClient.newApiClientInstance?.getServerAPI()?.getApprovals()
                    .compose(applyIOSchedulers())
                    .subscribe(
                        { result ->
                            isLoading.set(false)

                          /*  if (result?.resultCode != null && result?.resultCode!!.equals(
                                    ApiConstant.API_SUCCESS, true)) {*/

                                _text.postValue(result.title)

/*

                            } else {
                                errorText.postValue(Constants.SHOW_SERVER_ERROR)

                            }
*/


                        },
                        { error ->
                            isLoading.set(false)

                            //Display Error Result Code with with Configure Message
                            try {
                                if (context != null && error != null) {
                                    errorText.postValue(context.getString(R.string.error_msg_network) +  (error as HttpException).code())
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