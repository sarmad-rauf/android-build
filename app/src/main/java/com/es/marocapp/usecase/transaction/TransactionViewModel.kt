package com.es.marocapp.usecase.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransactionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Transactions Fragment"
    }
    val text: LiveData<String> = _text
}