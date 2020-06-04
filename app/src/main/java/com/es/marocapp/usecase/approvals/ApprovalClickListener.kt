package com.es.marocapp.usecase.approvals

import android.view.View

interface ApprovalClickListener {

    fun onApproveButtonClick(view: View)

    fun onCancelButtonClick(view: View)
}