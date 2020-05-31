package com.es.marocapp.usecase.approvals

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.ApprovalsItemAdapter
import com.es.marocapp.databinding.FragmentApprovalBinding
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity

class ApprovalFragment : BaseFragment<FragmentApprovalBinding>() {

    private lateinit var approvalViewModel: ApprovalViewModel
    private lateinit var mApprovalsItemAdapter: ApprovalsItemAdapter
    private var mApprovalName : ArrayList<String> = ArrayList()
    private var mApprovalType : ArrayList<String> = ArrayList()

    override fun setLayout(): Int {
        return R.layout.fragment_approval
    }

    override fun init(savedInstanceState: Bundle?) {
        approvalViewModel = ViewModelProvider(activity as MainActivity).get(ApprovalViewModel::class.java)

        mDataBinding.apply {
            viewmodel = approvalViewModel
        }

//        approvalViewModel.requestForApprovalsApi(context)

        mApprovalName.apply {
            add("Marketplace")
            add("Operations")
            add("Marketplace")
        }

        mApprovalType.apply {
            add("DEBIT")
            add("Withdrawal")
            add("DEBIT")
        }

        approvalViewModel.requestForGetApprovalsApi(activity)

        mApprovalsItemAdapter = ApprovalsItemAdapter(mApprovalName,mApprovalType, object : ApprovalsItemAdapter.ApprovalItemClickListner{
            override fun onApprovalItemTypeClick() {
                (activity as MainActivity).navController.navigate(R.id.action_navigation_approval_to_approvalDetailFragment)
            }

        })

        mDataBinding.mApprovalsRecycler.apply {
            adapter = mApprovalsItemAdapter
            layoutManager = LinearLayoutManager(activity as MainActivity)
        }

        (activity as MainActivity).setHomeToolbarVisibility(false)

        subscribeObserver()

    }

    private fun subscribeObserver() {
        approvalViewModel.getApprovalResponseListner.observe(this, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                approvalViewModel.requestForUserApprovalsApi(activity,"01","true")
            }else{
                Toast.makeText(activity,"Get Approvals APi Failed",Toast.LENGTH_SHORT).show()
            }
        })
    }
}