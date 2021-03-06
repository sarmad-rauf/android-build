package com.es.marocapp.usecase.approvals.approvalsuccess

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentApprovalSuccessBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.UserApprovalResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.approvals.ApprovalActivity
import com.es.marocapp.usecase.approvals.ApprovalFragment
import com.es.marocapp.usecase.approvals.ApprovalViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class ApprovalSuccessFragment : BaseFragment<FragmentApprovalSuccessBinding>(),
    ApprovalSuccessClickListener {

      private lateinit var approvalViewModel: ApprovalViewModel
    private lateinit var userApprovalData: UserApprovalResponse

    override fun setLayout(): Int {
        return R.layout.fragment_approval_success
    }

    override fun init(savedInstanceState: Bundle?) {
          approvalViewModel = ViewModelProvider(activity as ApprovalActivity).get(ApprovalViewModel::class.java)

        mDataBinding.apply {
              viewmodel = approvalViewModel
              listener = this@ApprovalSuccessFragment
        }

        //To get your arraylist

        //To get your arraylist
        // val extras = getIntent().extras
        userApprovalData =
            arguments?.getParcelable<UserApprovalResponse>(ApprovalFragment.USER_APPROVAL_KEY)!!


        setStrings()
        setUIData()

        subscribeForGetBalanceResponse()
        approvalViewModel.requestForGetBalanceApi(context)
    }

    private fun setStrings() {
        mDataBinding.tvSuccessTitle.text = LanguageData.getStringValue("MyApprovals")

        mDataBinding.tvContactNumTitle.text = LanguageData.getStringValue("RequestInitiator")
        mDataBinding.tvCompanyNameTitle.text = LanguageData.getStringValue("ApprovalType")
        mDataBinding.tvOwnerNameTitle.text = LanguageData.getStringValue("ApprovalID")

        mDataBinding.newBalanceTitle.text =  LanguageData.getStringValue("YourNewBalanceIs")
        mDataBinding.feeLabel.text = LanguageData.getStringValue("TransactionFee")
        mDataBinding.totalAmountLabel.text = LanguageData.getStringValue("Total")
        mDataBinding.tvContactNumTitle.text = LanguageData.getStringValue("RequestInitiator")
        mDataBinding.amountLabel.text = LanguageData.getStringValue("Amount")
    }

    private fun subscribeForGetBalanceResponse() {
        approvalViewModel.getBalanceResponseListner.observe(this, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
               mDataBinding.newBalanceVal.text=it.currnecy.plus(it.amount)

            }else{
                DialogUtils.showErrorDialoge(activity,it.description)
            }
        })
    }

    private fun setUIData() {

        mDataBinding.tvCompanyNameVal.text=userApprovalData.approvaltype
        mDataBinding.tvOwnerNameVal.text=userApprovalData.approvalid

        mDataBinding.feeValue.text=userApprovalData?.fee?.currency.plus(" ").plus(userApprovalData?.fee?.amount)

        mDataBinding.totalAmountValue.text=userApprovalData?.amount?.currency.plus(" ").plus(Constants.addAmountAndFee(userApprovalData?.amount?.amount!!.toDouble() , userApprovalData?.fee?.amount!!.toDouble()))

        mDataBinding.amountValue.text=userApprovalData?.amount?.currency.plus(" ").plus(userApprovalData?.amount?.amount)

        mDataBinding.tvContactNumVal.text=userApprovalData.initiatingaccountholderid
    }

    override fun onOkClickListener(view: View) {
        (activity as ApprovalActivity).finish()
    }

    override fun onBackClickListener(view: View) {
        (activity as ApprovalActivity).navController.navigateUp()
    }
}