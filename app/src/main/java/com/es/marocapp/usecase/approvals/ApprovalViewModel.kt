package com.es.marocapp.usecase.approvals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ApprovalViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Approvals Fragment"
    }
    val text: LiveData<String> = _text
}