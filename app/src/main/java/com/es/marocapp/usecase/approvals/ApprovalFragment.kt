package com.es.marocapp.usecase.approvals

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentApprovalBinding
import com.es.marocapp.usecase.BaseFragment
import kotlinx.android.synthetic.main.fragment_pin.*

class ApprovalFragment : BaseFragment<FragmentApprovalBinding>() {

    private lateinit var approvalViewModel: ApprovalViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_approval
    }

    override fun init(savedInstanceState: Bundle?) {
        approvalViewModel = ViewModelProvider(this).get(ApprovalViewModel::class.java)

        mDataBinding.apply {
            viewmodel = approvalViewModel
        }


        approvalViewModel.text.observe(this, Observer {
            text_notifications.text = it
        })
    }
}