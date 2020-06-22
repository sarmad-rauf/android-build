package com.es.marocapp.usecase.billpayment.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentBillPaymentConfimationBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.InvoiceCustomModel
import com.es.marocapp.model.responses.PostPaidBillPaymentResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentClickListner
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class FragmentBillPaymentPostPaidConfirmation : BaseFragment<FragmentBillPaymentConfimationBinding>(),
    BillPaymentClickListner {

    private lateinit var mActivityViewModel: BillPaymentViewModel
    private var selectedListOfInvoice = arrayListOf<InvoiceCustomModel>()

    private var amountToTransfer = ""

    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_confimation
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as BillPaymentActivity).get(BillPaymentViewModel::class.java)
        mDataBinding.apply {
            listner = this@FragmentBillPaymentPostPaidConfirmation
            viewmodel = mActivityViewModel
        }

        (activity as BillPaymentActivity).setHeaderVisibility(false)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(false)

        mDataBinding.imgBackButton.setOnClickListener {
            (activity as BillPaymentActivity).navController.popBackStack(
                R.id.fragmentPostPaidBillDetails,
                false
            )
        }

        setStrings()
        updateUI()
        subscribeObserver()


    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@FragmentBillPaymentPostPaidConfirmation, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        })

        mActivityViewModel.getPostPaidBillPaymentResponseListner.observe(this@FragmentBillPaymentPostPaidConfirmation,
            Observer {
                if(!it.isNullOrEmpty()){
                    var listOfResponse : ArrayList<String> = arrayListOf()
                    for(i in it.indices){
                        listOfResponse.add(it[i].responseCode)
                    }

                    mActivityViewModel.selectedIvoicesBillPaymentStatus.set(listOfResponse)

                }else{
                    DialogUtils.showErrorDialoge(activity,LanguageData.getStringValue("SomethingWentWrong"))
                }
            })
    }

    private fun updateUI() {
        //        tvCompanyNameVal == ReceiverNumber
//        tvOwnerNameVal == ReceiverName
//        tvReceiptCodeVal = bill
//        tvDHVal == Fee
//
//        tvAmountVal == AmountTotal

        mDataBinding.tvCompanyNameVal.text = mActivityViewModel.transferdAmountTo.substringBefore("@")
        var ReceiverName = Constants.balanceInfoAndResponse.firstname +" " +Constants.balanceInfoAndResponse.surname
        mActivityViewModel.ReceiverName = ReceiverName
        mDataBinding.tvOwnerNameVal.text = ReceiverName

        var totalFee = "0.00"

        for(i in mActivityViewModel.listOfPostPaidBillPaymentQuote.indices){
            var item = mActivityViewModel.listOfPostPaidBillPaymentQuote[i]
            if(item.quoteList.isNotEmpty()){
                totalFee = (totalFee.toDouble()+item.quoteList[0].fee.amount).toString()
            }
        }

        mDataBinding.tvReceiptCodeVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mActivityViewModel.totalSelectedBillAmount
        mDataBinding.tvDHVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + totalFee

        amountToTransfer = Constants.addAmountAndFee(
            mActivityViewModel.totalSelectedBillAmount.toDouble(),
            totalFee.toDouble()
        )
        mDataBinding.tvAmountVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + amountToTransfer
    }

    private fun setStrings() {
        //        tvCompanyNameVal == ReceiverNumber
//        tvOwnerNameVal == ReceiverName
//        tvReceiptCodeVal = bill
//        tvDHVal == Fee
//
//        tvAmountVal == AmountTotal

        mDataBinding.tvCompanyNameTitle.text = LanguageData.getStringValue("ReceiverNumber")
        mDataBinding.tvOwnerNameTitle.text = LanguageData.getStringValue("SenderName")
        mDataBinding.tvReceiptCodeTitle.text = LanguageData.getStringValue("Bill")
        mDataBinding.tvDHTitle.text = LanguageData.getStringValue("TotalFee")
        mDataBinding.tvAmountTitle.text = LanguageData.getStringValue("TotalCost")

        mDataBinding.tvConfirmationTitle.text = LanguageData.getStringValue("Confirmation")

        mDataBinding.btnConfirmationCancel.text = LanguageData.getStringValue("BtnTitle_Cancel")
        mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_Pay")
    }

    override fun onSubmitClickListner(view: View) {
        DialogUtils.showPasswordDialoge(activity,object : DialogUtils.OnPasswordDialogClickListner{
            override fun onDialogYesClickListner(password: String) {
                Constants.HEADERS_FOR_PAYEMNTS = true
                Constants.CURRENT_USER_CREDENTIAL = password

                selectedListOfInvoice.addAll(mActivityViewModel.selectedIvoicesList.get()!!)
                mActivityViewModel.totalBillSelected = selectedListOfInvoice.size
                for(i in selectedListOfInvoice.indices){
                    //Ohrefnum(16) + month (8) + OpenAmount (15) + OHXACT (38)
                    var selectBillInvoice = selectedListOfInvoice[i].ohrefnum+selectedListOfInvoice[i].month+selectedListOfInvoice[i].openAmount+selectedListOfInvoice[i].ohxact
                    mActivityViewModel.selectBillAmount = selectedListOfInvoice[i].openAmount
                    if(!mActivityViewModel.selectedIvoicesQuoteList.get().isNullOrEmpty()){
                        if(!mActivityViewModel.selectedIvoicesQuoteList.get()!![i].equals("-1")){
                            mActivityViewModel.requestForPostPaidBillPaymentApi(activity,selectBillInvoice,mActivityViewModel.selectedIvoicesQuoteList.get()!![i])
                        }else{
                            mActivityViewModel.listOfPostPaidBillPayment.add(
                                PostPaidBillPaymentResponse(arrayListOf(),"","Failed","","","","",
                                "1500","","","","")
                            )
                        }
                    }
                }
            }

        })
    }

    override fun onBackClickListner(view: View) {
        mActivityViewModel.isPostPaidMobileSelected.set(false)
        mActivityViewModel.isPostPaidFixSelected.set(false)
        mActivityViewModel.isInternetSelected.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as BillPaymentActivity).startNewActivityAndClear(
            activity as BillPaymentActivity,
            MainActivity::class.java
        )
    }

}