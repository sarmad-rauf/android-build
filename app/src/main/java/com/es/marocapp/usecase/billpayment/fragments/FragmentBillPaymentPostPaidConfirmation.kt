package com.es.marocapp.usecase.billpayment.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentBillPaymentConfimationBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.*
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentClickListner
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class FragmentBillPaymentPostPaidConfirmation : BaseFragment<FragmentBillPaymentConfimationBinding>(),
    BillPaymentClickListner {

    private lateinit var mActivityViewModel: BillPaymentViewModel
    private var selectedListOfInvoice = arrayListOf<InvoiceCustomModel>()
    private var selectedFatoratiListOfInvoice = arrayListOf<FatoratiCustomParamModel>()

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

        (activity as BillPaymentActivity).setHeaderVisibility(false)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(false)

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
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    var isWrongPasswordEntered = false
                    var listOfResponse : ArrayList<String> = arrayListOf()
                    for(i in it.indices){
                        if(it[i].responseCode.equals(ApiConstant.API_WRONG_PASSWORD)){
                            isWrongPasswordEntered = true
                        }else{
                            listOfResponse.add(it[i].responseCode)
                        }
                    }

                    mActivityViewModel.selectedIvoicesBillPaymentStatus.set(listOfResponse)

                    if(isWrongPasswordEntered){
                        DialogUtils.showErrorDialoge(activity,it[0].description)
                    }else{
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentPostPaidConfirmation_to_fragmentPostPaidBillPaymentSuccess)
                    }
                }else{
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    DialogUtils.showErrorDialoge(activity,LanguageData.getStringValue("SomethingWentWrong"))
                }
            }
        )

        mActivityViewModel.getPostPaidFatoratiResponseListner.observe(this@FragmentBillPaymentPostPaidConfirmation,
            Observer {
                if(!it.isNullOrEmpty()){
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    var isWrongPasswordEntered = false
                    var listOfResponse : ArrayList<String> = arrayListOf()
                    for(i in it.indices){
                        if(it[i].responseCode.equals(ApiConstant.API_WRONG_PASSWORD)){
                            isWrongPasswordEntered = true
                        }else{
                            listOfResponse.add(it[i].responseCode)
                        }
                    }

                    mActivityViewModel.selectedIvoicesBillPaymentStatus.set(listOfResponse)

                    if(isWrongPasswordEntered){
                        DialogUtils.showErrorDialoge(activity,it[0].description)
                    }else{
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentPostPaidConfirmation_to_fragmentPostPaidBillPaymentSuccess)
                    }

                }else{
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    DialogUtils.showErrorDialoge(activity,LanguageData.getStringValue("SomethingWentWrong"))
                }
            }
        )
    }

    private fun updateUI() {
        //        tvCompanyNameVal == ReceiverNumber
//        tvOwnerNameVal == ReceiverName
//        tvReceiptCodeVal = bill
//        tvDHVal == Fee
//
//        tvAmountVal == AmountTotal

        mDataBinding.tvSenderNameVal.text = Constants.balanceInfoAndResponse.firstname + Constants.balanceInfoAndResponse.surname
        mDataBinding.tvSenderNumberVal.text = Constants.CURRENT_USER_MSISDN

        if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
            mDataBinding.tvOwnerNameVal.visibility = View.GONE
            mDataBinding.tvOwnerNameTitle.visibility = View.GONE
            mDataBinding.divider2.visibility = View.GONE
        }

        if(mActivityViewModel.isBillUseCaseSelected.get()!!){
            mDataBinding.tvOwnerNameVal.visibility = View.VISIBLE
            mDataBinding.tvOwnerNameTitle.visibility = View.VISIBLE
            mDataBinding.divider2.visibility = View.VISIBLE
        }

        mDataBinding.tvCompanyNameVal.text = mActivityViewModel.transferdAmountTo.substringBefore("@")
//        var ReceiverName = Constants.balanceInfoAndResponse.firstname +" " +Constants.balanceInfoAndResponse.surname
        mActivityViewModel.ReceiverName = mActivityViewModel.custname
        mDataBinding.tvOwnerNameVal.text = mActivityViewModel.ReceiverName

        var totalFee = "0.00"
        if(mActivityViewModel.isBillUseCaseSelected.get()!!){
            for(i in mActivityViewModel.listOfPostPaidBillPaymentQuote.indices){
                var item = mActivityViewModel.listOfPostPaidBillPaymentQuote[i]
                if(item.quoteList.isNotEmpty()){
                    mActivityViewModel.listOfSelectedBillFee.add(item.quoteList[0].fee.amount.toString())
                    totalFee = (totalFee.toDouble()+item.quoteList[0].fee.amount).toString()
                }
            }
        }
        if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
            for(i in mActivityViewModel.listOfFatoratiQuote.indices){
                var item = mActivityViewModel.listOfFatoratiQuote[i]
                if(item.quoteList!=null && item.quoteList.isNotEmpty()){
                    mActivityViewModel.listOfSelectedBillFee.add(item.quoteList[0].fee.amount.toString())
                    totalFee = (totalFee.toDouble()+item.quoteList[0].fee.amount).toString()
                }
            }
        }

        mActivityViewModel.feeAmount = Constants.converValueToTwoDecimalPlace(totalFee.toDouble())

        var totalAmount = Constants.converValueToTwoDecimalPlace(mActivityViewModel.totalSelectedBillAmount.toDouble())
        mDataBinding.tvReceiptCodeVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + totalAmount
        mDataBinding.tvDHVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mActivityViewModel.feeAmount

        amountToTransfer = Constants.addAmountAndFee(
            mActivityViewModel.totalSelectedBillAmount.toDouble(),
            totalFee.toDouble()
        )

        var totalCost = Constants.converValueToTwoDecimalPlace(amountToTransfer.toDouble())
        mDataBinding.tvAmountVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + totalCost
    }

    private fun setStrings() {
        //        tvCompanyNameVal == ReceiverNumber
//        tvOwnerNameVal == ReceiverName
//        tvReceiptCodeVal = bill
//        tvDHVal == Fee
//
//        tvAmountVal == AmountTotal

        mDataBinding.tvCompanyNameTitle.text = LanguageData.getStringValue("ReceiverNumber")
        mDataBinding.tvOwnerNameTitle.text = LanguageData.getStringValue("ReceiverName")
        mDataBinding.tvReceiptCodeTitle.text = LanguageData.getStringValue("Bill")
        mDataBinding.tvDHTitle.text = LanguageData.getStringValue("TotalFee")
        mDataBinding.tvAmountTitle.text = LanguageData.getStringValue("TotalCost")

        mDataBinding.tvConfirmationTitle.text = LanguageData.getStringValue("Confirmation")

        mDataBinding.btnConfirmationCancel.text = LanguageData.getStringValue("BtnTitle_Cancel")
        mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_Pay")

        mDataBinding.tvSendNameTitle.text = LanguageData.getStringValue("SenderName")
        mDataBinding.tvSendNumberTitle.text = LanguageData.getStringValue("SenderNumber")
    }

    override fun onSubmitClickListner(view: View) {
        DialogUtils.showPasswordDialoge(activity,object : DialogUtils.OnPasswordDialogClickListner{
            override fun onDialogYesClickListner(password: String) {
                Constants.HEADERS_FOR_PAYEMNTS = true
                Constants.CURRENT_USER_CREDENTIAL = password

                if(mActivityViewModel.isBillUseCaseSelected.get()!!){
                    payPostPaidBills()
                }

                if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
                    payFatoratiBills()
                }
            }

        })
    }

    private fun payFatoratiBills() {
        selectedFatoratiListOfInvoice.addAll(mActivityViewModel.selectedFatoraitIvoicesList.get()!!)
        mActivityViewModel.totalBillSelected = selectedFatoratiListOfInvoice.size
        for(i in selectedFatoratiListOfInvoice.indices){

            mActivityViewModel.selectBillAmount = selectedFatoratiListOfInvoice[i].prixTTC
            if(!mActivityViewModel.selectedIvoicesQuoteList.get().isNullOrEmpty()){
                if(!mActivityViewModel.selectedIvoicesQuoteList.get()!![i].equals("-1")){
                    mActivityViewModel.requestForFatoratiApi(activity,selectedFatoratiListOfInvoice[i].prixTTC,selectedFatoratiListOfInvoice[i].idArticle,
                        selectedFatoratiListOfInvoice[i].prixTTC,selectedFatoratiListOfInvoice[i].typeArticle,mActivityViewModel.selectedIvoicesQuoteList.get()!![i])
                }else{
                    mActivityViewModel.listOfFatorati.add(
                        BillPaymentFatoratiResponse(arrayListOf(),"","Failed","","",
                            FatoratieFinancialReceiptResponse("",
                                Financialreceipt(FatoratieAmount(0.0,""),"","", FatoratieFee(0.0,""),
                                "","","","","","","",""
                                ),"")
                            ,"", "1500","","","","")
                    )
                }
            }
        }
    }

    private fun payPostPaidBills() {
        selectedListOfInvoice.addAll(mActivityViewModel.selectedIvoicesList.get()!!)
        mActivityViewModel.totalBillSelected = selectedListOfInvoice.size
        for(i in selectedListOfInvoice.indices){
            //Ohrefnum(16) + month (8) + OpenAmount (15) + OHXACT (38)
            var selectBillInvoice = selectedListOfInvoice[i].ohrefnum+selectedListOfInvoice[i].month+selectedListOfInvoice[i].openAmount+selectedListOfInvoice[i].ohxact
            mActivityViewModel.selectBillAmount = selectedListOfInvoice[i].openAmount
            if(!mActivityViewModel.selectedIvoicesQuoteList.get().isNullOrEmpty()){
                if(!mActivityViewModel.selectedIvoicesQuoteList.get()!![i].equals("-1")){
                    mActivityViewModel.requestForPostPaidBillPaymentApi(activity,selectedListOfInvoice[i].month,selectedListOfInvoice[i].ohrefnum,selectedListOfInvoice[i].ohxact,
                        selectedListOfInvoice[i].openAmount,mActivityViewModel.selectedIvoicesQuoteList.get()!![i])
                }else{
                    mActivityViewModel.listOfPostPaidBillPayment.add(
                        PostPaidBillPaymentResponse(arrayListOf(),"","Failed","","","","",
                            "1500","","","","")
                    )
                }
            }
        }
    }

    override fun onBackClickListner(view: View) {
        mActivityViewModel.isPostPaidMobileSelected.set(false)
        mActivityViewModel.isPostPaidFixSelected.set(false)
        mActivityViewModel.isInternetSelected.set(false)

        mActivityViewModel.isFatoratiUseCaseSelected.set(false)
        mActivityViewModel.isBillUseCaseSelected.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as BillPaymentActivity).startNewActivityAndClear(
            activity as BillPaymentActivity,
            MainActivity::class.java
        )
    }

}