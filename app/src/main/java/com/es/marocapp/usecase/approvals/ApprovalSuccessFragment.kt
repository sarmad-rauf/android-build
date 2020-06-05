package com.es.marocapp.usecase.approvals

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentApprovalDetailsBinding
import com.es.marocapp.databinding.FragmentApprovalSuccessBinding
import com.es.marocapp.model.responses.Approvaldetail
import com.es.marocapp.model.responses.UserApprovalResponse
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import kotlinx.android.synthetic.main.fragment_approval_success.*

class ApprovalSuccessFragment : BaseFragment<FragmentApprovalSuccessBinding>() {

    //  private lateinit var approvalViewModel: ApprovalViewModel
    private lateinit var userApprovalData: UserApprovalResponse

    override fun setLayout(): Int {
        return R.layout.fragment_approval_success
    }

    override fun init(savedInstanceState: Bundle?) {
        //  approvalViewModel = ViewModelProvider(this).get(ApprovalViewModel::class.java)

        mDataBinding.apply {
            //  viewmodel = approvalViewModel
           // listener = this@ApprovalSuccessFragment
        }

        //To get your arraylist

        //To get your arraylist
        // val extras = getIntent().extras
        userApprovalData =
            arguments?.getParcelable<UserApprovalResponse>(ApprovalFragment.USER_APPROVAL_KEY)!!

          setUIData()

        btnConfirmationPay.setOnClickListener{
            (activity as MainActivity).navController.navigate(R.id.navigation_home)
        }


        //subsribeForApprovalsDataObserver()
    }

    private fun setUIData() {
        tvCompanyNameVal.text=userApprovalData.approvaltype
        tvOwnerNameVal.text=userApprovalData.approvalid

        tvOwnerNameVal2.text=userApprovalData.amount?.currency.plus(userApprovalData.amount?.amount)

        tvContactNumVal2.text=userApprovalData.fee?.currency.plus(userApprovalData.fee?.amount)
    }
}