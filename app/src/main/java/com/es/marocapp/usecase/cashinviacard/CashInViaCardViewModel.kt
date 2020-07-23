package com.es.marocapp.usecase.cashinviacard

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.model.responses.BalanceInfoAndLimitResponse
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.SingleLiveEvent
import io.reactivex.disposables.Disposable

class CashInViaCardViewModel(application : Application) : AndroidViewModel(application){

    var mBalanceInforAndResponseObserver = ObservableField<BalanceInfoAndLimitResponse>()

    init {
        mBalanceInforAndResponseObserver.set(Constants.balanceInfoAndResponse)
    }

    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()

    var popBackStackTo = -1
    var showDialog = false

}