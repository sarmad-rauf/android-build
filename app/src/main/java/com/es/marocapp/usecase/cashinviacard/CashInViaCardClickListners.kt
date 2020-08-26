package com.es.marocapp.usecase.cashinviacard

import android.view.View

interface CashInViaCardClickListners{
    fun onCashDepositClick(view : View)
    fun onBankCardClick(view : View)
    fun onNextButtonClick(view : View)
}