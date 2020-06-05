package com.es.marocapp.usecase.approvals

import android.content.Intent.getIntent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentApprovalDetailsBinding
import com.es.marocapp.model.responses.Approvaldetail
import com.es.marocapp.model.responses.UserApprovalResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.approvals.ApprovalFragment.Companion.SELECTED_APPROVAL_KEY
import com.es.marocapp.usecase.approvals.ApprovalFragment.Companion.USER_APPROVAL_KEY
import kotlinx.android.synthetic.main.fragment_approval_details.*


class ApprovalDetailFragment : BaseFragment<FragmentApprovalDetailsBinding>(),ApprovalClickListener{

    private lateinit var approvalViewModel: ApprovalViewModel
    private lateinit var selectedApprovalData:Approvaldetail

    override fun setLayout(): Int {
        return R.layout.fragment_approval_details
    }

    override fun init(savedInstanceState: Bundle?) {
        approvalViewModel = ViewModelProvider(this).get(ApprovalViewModel::class.java)

        mDataBinding.apply {
            viewmodel = approvalViewModel
            listener = this@ApprovalDetailFragment
        }

        //To get your arraylist

        //To get your arraylist
       // val extras = getIntent().extras
        selectedApprovalData = arguments?.getParcelable<Approvaldetail>(SELECTED_APPROVAL_KEY)!!

        setUIData()


        subscribeFoUserApprovalsResponse()
    }

    private fun setUIData() {
        tvRequestIndicatorVal.text=selectedApprovalData?.initiatingaccountholderid!!.split("/")[0]
        tvApprovalTypeVal.text=selectedApprovalData?.approvaltype
        tvApprovalIDVal.text=selectedApprovalData?.approvalid.toString()
        tvTransactionFeeVal.text=selectedApprovalData?.fee?.currency.plus(selectedApprovalData?.fee?.amount)
        tvTotalVal.text=selectedApprovalData?.amount?.currency.plus(selectedApprovalData?.amount?.amount)
    }

    private fun subscribeFoUserApprovalsResponse() {
        approvalViewModel.getUsersApprovalResponseListner.observe(this, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                val bundle = Bundle()
                bundle.putParcelable(USER_APPROVAL_KEY, it as UserApprovalResponse)
                (activity as MainActivity).navController.navigate(R.id.action_navigation_approvalDetails_to_approvalSuccessFragment,bundle)

            }else{
                //  Toast.makeText(activity,"Get Approvals APi Failed",Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onApproveButtonClick(view: View) {
        approvalViewModel.requestForUserApprovalsApi(activity,selectedApprovalData?.approvalid.toString(),"true")
    }

    override fun onCancelButtonClick(view: View) {
        (activity as MainActivity).navController.navigateUp()
    }

}