package com.es.marocapp.usecase.payments.billpayment

import android.view.View

interface BillPaymentClickListener {
    fun onBackClick(view : View)
    fun onValidateClick(view : View)
}