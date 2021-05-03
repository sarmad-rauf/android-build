package com.es.marocapp.usecase.approvals

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentApprovalDetailsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.Approvaldetail
import com.es.marocapp.model.responses.UserApprovalResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.approvals.ApprovalFragment.Companion.SELECTED_APPROVAL_KEY
import com.es.marocapp.usecase.approvals.ApprovalFragment.Companion.USER_APPROVAL_KEY
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.fragment_approval_details.*


class ApprovalDetailFragment : BaseFragment<FragmentApprovalDetailsBinding>(),ApprovalClickListener{

    private lateinit var approvalViewModel: ApprovalViewModel
    private lateinit var selectedApprovalData:Approvaldetail

    override fun setLayout(): Int {
        return R.layout.fragment_approval_details
    }

    override fun init(savedInstanceState: Bundle?) {
        approvalViewModel = ViewModelProvider(activity as ApprovalActivity).get(ApprovalViewModel::class.java)

        mDataBinding.apply {
            viewmodel = approvalViewModel
            listener = this@ApprovalDetailFragment
        }

        //To get your arraylist

        //To get your arraylist
       // val extras = getIntent().extras
        selectedApprovalData = arguments?.getParcelable<Approvaldetail>(SELECTED_APPROVAL_KEY)!!
        approvalViewModel.selectedTaxDetail= selectedApprovalData.message?.substringAfter(Constants.TAX_DETALS,"0")
        setUIData()
        setStrings()

        subscribeFoUserApprovalsResponse()
    }

    private fun setStrings() {

        mDataBinding.tvApprovalDetailsTitle.text = LanguageData.getStringValue("Details")

        mDataBinding.tvRequestIndicatorTitle.text = LanguageData.getStringValue("RequestInitiator")
        mDataBinding.tvIndicatorName.text = LanguageData.getStringValue("InitiatorName")
        mDataBinding.tvApprovalTypeTitle.text = LanguageData.getStringValue("ApprovalType")
        mDataBinding.tvApprovalIDTitle.text = LanguageData.getStringValue("ApprovalID")
        mDataBinding.tvExourtTitle.text = LanguageData.getStringValue("Exourt")
        mDataBinding.tvTransactionFeeTitle.text = LanguageData.getStringValue("TransactionFee")
        mDataBinding.tvTotalTitle.text = LanguageData.getStringValue("Total")
        mDataBinding.tvAmountTitle.text = LanguageData.getStringValue("Amount")
        mDataBinding.btnConfirmationCancel.text = LanguageData.getStringValue("Reject")
        mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("Approve")

    }

    private fun setUIData() {
        tvRequestIndicatorVal.text=selectedApprovalData?.initiatingaccountholderid!!
        tvApprovalTypeVal.text=selectedApprovalData?.approvaltype
        tvApprovalIDVal.text=selectedApprovalData?.approvalid.toString()
       if(approvalViewModel?.selectedTaxDetail.isNullOrEmpty())
       {
           approvalViewModel?.selectedTaxDetail="0"
       }
        val transactionFee =
            selectedApprovalData?.fee?.amount?.let { approvalViewModel?.selectedTaxDetail?.toDouble()?.let { it1 ->
                Constants.addAmountAndFee(it,
                    it1
                )
            } }

        tvTransactionFeeVal.text=selectedApprovalData?.fee?.currency.plus(" ").plus(transactionFee?.toDouble()?.let {
            Constants.converValueToTwoDecimalPlace(it)
        })
        tvAmountVal.text=selectedApprovalData?.amount?.currency.plus(" ").plus(selectedApprovalData?.amount?.amount)
        tvExourtVal.text=Constants.getZoneFormattedDateAndTime(selectedApprovalData?.approvalexpirytime.toString())
        mDataBinding.tvTotalVal.text=selectedApprovalData?.amount?.currency.plus(" ").plus(
            transactionFee?.toDouble()?.let {
                Constants.addAmountAndFee(selectedApprovalData?.amount?.amount!!.toDouble() ,
                    it
                )
            })
    }

    private fun subscribeFoUserApprovalsResponse() {
        approvalViewModel.getUsersApprovalResponseListner.observe(this, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                if(approvalViewModel.isApproved) {
                    approvalViewModel.isApproved=false
                    val bundle = Bundle()
                    bundle.putParcelable(USER_APPROVAL_KEY, it as UserApprovalResponse)
                    //  (activity as ApprovalActivity).navController.navigate(R.id.action_approvalDetailFragment2_to_approvalSuccessFragment2,bundle)
                    DialogUtils.successFailureDialogue(
                        activity as ApprovalActivity,
                        it.description,
                        0,
                        object : DialogUtils.OnYesClickListner {
                            override fun onDialogYesClickListner() {
                                (activity as ApprovalActivity).finish()
                            }
                        })
                }
                else{
                    (activity as ApprovalActivity).navController.navigateUp()
                }

            }else{
                DialogUtils.showErrorDialoge(activity,it.description)
            }
        })
    }

    override fun onApproveButtonClick(view: View) {
        approvalViewModel.isApproved=true
        approvalViewModel.requestForUserApprovalsApi(activity,selectedApprovalData?.approvalid.toString(),"true")
    }

    override fun onCancelButtonClick(view: View) {
        approvalViewModel.isApproved=false
        approvalViewModel.requestForUserApprovalsApi(activity,selectedApprovalData?.approvalid.toString(),"false")
    }

    override fun onBackButtonClick(view: View) {
        (activity as ApprovalActivity).navController.navigateUp()
    }

}