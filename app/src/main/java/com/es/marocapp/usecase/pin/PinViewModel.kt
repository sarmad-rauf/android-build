package com.es.marocapp.usecase.pin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PinViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Pin Fragment"
    }
    val text: LiveData<String> = _text
}