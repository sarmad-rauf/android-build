package com.es.marocapp.usecase.approvals

import android.os.Bundle
import androidx.core.os.bundleOf
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
import com.es.marocapp.usecase.MainActivity
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
        approvalViewModel = ViewModelProvider(activity as MainActivity).get(ApprovalViewModel::class.java)

        mDataBinding.apply {
            viewmodel = approvalViewModel
        }

//        approvalViewModel.requestForApprovalsApi(context)

        approvalViewModel.requestForGetApprovalsApi(activity)

        mApprovalsItemAdapter = ApprovalsItemAdapter(mApprovalsList, object : ApprovalsItemAdapter.ApprovalItemClickListner{
            override fun onApprovalItemTypeClick() {
              //  val bundle= bundleOf("data",mApprovalsList.get(0) as ArrayList<Approvaldetail>)
                val b = Bundle()
                b.putParcelable(SELECTED_APPROVAL_KEY, mApprovalsList.get(0) as Approvaldetail)
                (activity as MainActivity).navController.navigate(R.id.action_navigation_approval_to_approvalDetailFragment,b)
            }

        })

        mDataBinding.mApprovalsRecycler.apply {
            adapter = mApprovalsItemAdapter
            layoutManager = LinearLayoutManager(activity as MainActivity)
        }

        (activity as MainActivity).setHomeToolbarVisibility(false)

        subscribeForApprovalsResponse()
        setStrings()
    }

    private fun setStrings() {
        mDataBinding.allApprovalTitle.text = LanguageData.getStringValue("All Approvals")
        mDataBinding.tvTransactionHistoryTitle.text = LanguageData.getStringValue("MyApprovals")
    }

    private fun subscribeForApprovalsResponse() {
        approvalViewModel.getApprovalResponseListner.observe(this, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
               // approvalViewModel.requestForUserApprovalsApi(activity,"01","true")
                mApprovalsList.clear()
                mApprovalsList.apply {
                    addAll(it.approvaldetails as ArrayList<Approvaldetail>)
                    mApprovalsItemAdapter.notifyDataSetChanged()
                }
            }else{
              DialogUtils.showErrorDialoge(activity,it.description)
            }
        })
    }
}