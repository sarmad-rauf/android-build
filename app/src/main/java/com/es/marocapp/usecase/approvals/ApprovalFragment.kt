package com.es.marocapp.usecase.approvals

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.ApprovalsItemAdapter
import com.es.marocapp.databinding.FragmentApprovalBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.Approvaldetail
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.cashservices.CashServicesActivity
import com.es.marocapp.utils.DialogUtils

class ApprovalFragment : BaseFragment<FragmentApprovalBinding>() {

    companion object{
        const val SELECTED_APPROVAL_KEY="selected_approval"
        const val USER_APPROVAL_KEY="user_approval"
    }

    private lateinit var approvalViewModel: ApprovalViewModel
    private lateinit var mApprovalsItemAdapter: ApprovalsItemAdapter
    private var mApprovalsList : ArrayList<Approvaldetail> = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_approval
    }

    override fun init(savedInstanceState: Bundle?) {
        approvalViewModel = ViewModelProvider(activity as ApprovalActivity).get(ApprovalViewModel::class.java)

        mDataBinding.apply {
            viewmodel = approvalViewModel
        }

//        approvalViewModel.requestForApprovalsApi(context)

        approvalViewModel.requestForGetApprovalsApi(activity)

        mApprovalsItemAdapter = ApprovalsItemAdapter(mApprovalsList, object : ApprovalsItemAdapter.ApprovalItemClickListner{
            override fun onApprovalItemTypeClick(position: Int) {
              //  val bundle= bundleOf("data",mApprovalsList.get(0) as ArrayList<Approvaldetail>)
                val b = Bundle()
                approvalViewModel.selectedapproval=mApprovalsList.get(position)
                (activity as ApprovalActivity).navController.navigate(R.id.action_approvalFragment_to_approvalDetailFragment2,b)

            }

        })

        mDataBinding.mApprovalsRecycler.apply {
            adapter = mApprovalsItemAdapter
            layoutManager = LinearLayoutManager(activity as ApprovalActivity)
        }

        mDataBinding.imgBackButton.setOnClickListener {
            (activity as ApprovalActivity).finish()
        }

        subscribeForApprovalsResponse()
        setStrings()
    }

    private fun setStrings() {
        mDataBinding.allApprovalTitle.text = LanguageData.getStringValue("All Approvals")
        mDataBinding.tvTransactionHistoryTitle.text = LanguageData.getStringValue("MyApprovals")
        mDataBinding.tvNoDataFound.text = LanguageData.getStringValue("NoDataFound")
    }

    private fun subscribeForApprovalsResponse() {
        approvalViewModel.getApprovalResponseListner.observe(activity as ApprovalActivity, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
               // approvalViewModel.requestForUserApprovalsApi(activity,"01","true")
                if(it.approvaldetails.isNullOrEmpty()){

                    mDataBinding.tvNoDataFound.visibility = View.VISIBLE
                    mApprovalsList.clear()
                    mApprovalsItemAdapter.notifyDataSetChanged()
                }else{
                    mDataBinding.tvNoDataFound.visibility = View.GONE
                    mApprovalsList.clear()
                    mApprovalsList.apply {
                        addAll(it.approvaldetails as ArrayList<Approvaldetail>)
                        mApprovalsItemAdapter.notifyDataSetChanged()
                    }





                }
            }else{
              DialogUtils.showErrorDialoge(activity,it.description)
            }
        })

        approvalViewModel.getCashOutQuoteResponseListner.observe(this@ApprovalFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    approvalViewModel.totalTax=0.0
                    if (it.quoteList.isNotEmpty()) {
                        for(taxes in it.taxList.indices)
                        {
                            approvalViewModel.totalTax=approvalViewModel.totalTax+it.taxList[taxes].amount.amount.toString().toDouble()
                        }
//                        approvalViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
//                        approvalViewModel.qouteId = it.quoteList[0].quoteid
                        approvalViewModel.selectedTaxDetail=approvalViewModel.totalTax.toString()
                    }
                    (activity as ApprovalActivity).navController.navigate(R.id.action_approvalFragment_to_approvalDetailFragment2,approvalViewModel.bundle)
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })
    }
}