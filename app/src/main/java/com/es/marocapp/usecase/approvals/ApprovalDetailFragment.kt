package com.es.marocapp.usecase.approvals

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentApprovalBinding
import com.es.marocapp.databinding.FragmentApprovalDetailsBinding
import com.es.marocapp.usecase.BaseFragment

class ApprovalDetailFragment : BaseFragment<FragmentApprovalDetailsBinding>(){

    private lateinit var approvalViewModel: ApprovalViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_approval_details
    }

    override fun init(savedInstanceState: Bundle?) {
        approvalViewModel = ViewModelProvider(this).get(ApprovalViewModel::class.java)

        mDataBinding.apply {
            viewmodel = approvalViewModel
        }

    }

}