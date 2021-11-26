package com.es.marocapp.usecase.transaction

import android.view.View

interface TransactionClickListeners {
    fun onBackBtnClick(view: View)
    fun onSortBtnClick(view: View)
}


interface TransactionDownloadRecipt {
    fun onDownloadReciptClickListner(view: View)
}