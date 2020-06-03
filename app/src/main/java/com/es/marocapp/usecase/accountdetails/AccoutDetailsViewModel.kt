package com.es.marocapp.usecase.accountdetails

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.es.marocapp.model.responses.BalanceInfoAndLimitResponse
import com.es.marocapp.utils.Constants

class AccoutDetailsViewModel(application: Application) : AndroidViewModel(application){

    var mBalanceInforAndResponseObserver = ObservableField<BalanceInfoAndLimitResponse>()

    init {
        mBalanceInforAndResponseObserver.set(Constants.balanceInfoAndResponse)
    }

}