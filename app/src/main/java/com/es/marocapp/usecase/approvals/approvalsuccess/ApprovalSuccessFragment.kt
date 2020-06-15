package com.es.marocapp.usecase.approvals.approvalsuccess

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentApprovalSuccessBinding
import com.es.marocapp.model.responses.UserApprovalResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.approvals.ApprovalFragment
import com.es.marocapp.usecase.approvals.ApprovalViewModel
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.fragment_approval_success.*

class ApprovalSuccessFragment : BaseFragment<FragmentApprovalSuccessBinding>(),
    ApprovalSuccessClickListener {

      private lateinit var approvalViewModel: ApprovalViewModel
    private lateinit var userApprovalData: UserApprovalResponse

    override fun setLayout(): Int {
        return R.layout.fragment_approval_success
    }

    override fun init(savedInstanceState: Bundle?) {
          approvalViewModel = ViewModelProvider(this).get(ApprovalViewModel::class.java)

        mDataBinding.apply {
              viewmodel = approvalViewModel
              listener = this@ApprovalSuccessFragment
        }

        //To get your arraylist

        //To get your arraylist
        // val extras = getIntent().extras
        userApprovalData =
            arguments?.getParcelable<UserApprovalResponse>(ApprovalFragment.USER_APPROVAL_KEY)!!

          setUIData()

        subscribeForGetBalanceResponse()
        approvalViewModel.requestForGetBalanceApi(context)
    }

    private fun subscribeForGetBalanceResponse() {
        approvalViewModel.getBalanceResponseListner.observe(this, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                newBalanceVal.text=it.currnecy.plus(it.amount)

            }else{
                DialogUtils.showErrorDialoge(activity,it.description)
            }
        })
    }

    private fun setUIData() {
        tvCompanyNameVal.text=userApprovalData.approvaltype
        tvOwnerNameVal.text=userApprovalData.approvalid

        tvOwnerNameVal2.text=userApprovalData.amount?.currency.plus(userApprovalData.amount?.amount)

        tvContactNumVal2.text=userApprovalData.fee?.currency.plus(userApprovalData.fee?.amount)
    }

    override fun onOkClickListener(view: View) {
        (activity as MainActivity).navController.navigate(R.id.navigation_home)
    }

    override fun onBackClickListener(view: View) {
        (activity as MainActivity).navController.navigateUp()
    }
}