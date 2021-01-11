package com.es.marocapp.usecase.billpayment.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentBillPaymentConfimationBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.requests.FatoratiQuoteParam
import com.es.marocapp.model.requests.Param
import com.es.marocapp.model.responses.*
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentClickListner
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class FragmentBillPaymentPostPaidConfirmation :
    BaseFragment<FragmentBillPaymentConfimationBinding>(),
    BillPaymentClickListner {

    private lateinit var mActivityViewModel: BillPaymentViewModel
    private var selectedListOfInvoice = arrayListOf<InvoiceCustomModel>()
    private var selectedFatoratiListOfInvoice = arrayListOf<FatoratiCustomParamModel>()

    private var amountToTransfer = ""

    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_confimation
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as BillPaymentActivity).get(BillPaymentViewModel::class.java)
        mDataBinding.apply {
            listner = this@FragmentBillPaymentPostPaidConfirmation
            viewmodel = mActivityViewModel
        }

        mActivityViewModel.popBackStackTo = -1

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
        subcribeForPostPaidSequenceCall()
        subcribeForFatoratiSequenceCall()


    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(
            this@FragmentBillPaymentPostPaidConfirmation,
            Observer {
                DialogUtils.showErrorDialoge(activity, it)
            })

        mActivityViewModel.getPostPaidBillPaymentResponseListner.observe(this@FragmentBillPaymentPostPaidConfirmation,
            Observer {
                if (!it.isNullOrEmpty()) {
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    var isWrongPasswordEntered = false
                    var listOfResponse: ArrayList<String> = arrayListOf()
                    for (i in it.indices) {
                        if (it[i].responseCode.equals(ApiConstant.API_WRONG_PASSWORD)) {
                            isWrongPasswordEntered = true
                        } else {
                            listOfResponse.add(it[i].responseCode)
                        }
                    }

                    mActivityViewModel.selectedIvoicesBillPaymentStatus.set(listOfResponse)

                    if (isWrongPasswordEntered) {
                        DialogUtils.showErrorDialoge(activity, it[0].description)
                    } else {
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentPostPaidConfirmation_to_fragmentPostPaidBillPaymentSuccess)
                    }
                } else {
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    DialogUtils.showErrorDialoge(
                        activity,
                        LanguageData.getStringValue("SomethingWentWrong")
                    )
                }
            }
        )

        mActivityViewModel.getPostPaidFatoratiResponseListner.observe(this@FragmentBillPaymentPostPaidConfirmation,
            Observer {
                /* if(!it.isNullOrEmpty()){
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
                     }

                 }else{
                     Constants.HEADERS_FOR_PAYEMNTS = false
                     DialogUtils.showErrorDialoge(activity,LanguageData.getStringValue("SomethingWentWrong"))
                 }*/

                if (it.responseCode.equals(ApiConstant.API_WRONG_PASSWORD)) {
                    DialogUtils.showErrorDialoge(activity, it.description)
                } else {
                    (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentPostPaidConfirmation_to_fragmentPostPaidBillPaymentSuccess)
                }
            }
        )
    }

    private fun subcribeForPostPaidSequenceCall() {
        mActivityViewModel.triggerPostPaidNextCall.observe(this@FragmentBillPaymentPostPaidConfirmation,
            Observer {
                callPostPaidBillsInSequence(mActivityViewModel.postPaidCounter)
            }
        )
    }

    private fun subcribeForFatoratiSequenceCall() {
        mActivityViewModel.triggerFatoratiNextCall.observe(this@FragmentBillPaymentPostPaidConfirmation,
            Observer {
                callFatoratiBillsInSequence(mActivityViewModel.fatoratiCounter)
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

        if(mActivityViewModel.isBillUseCaseSelected.get()!!){
            mDataBinding.tvFatoratiFeeTitle.visibility = View.GONE
            mDataBinding.tvFatoratiFeeVal.visibility = View.GONE
        }else if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
            mDataBinding.tvFatoratiFeeTitle.visibility = View.VISIBLE
            mDataBinding.tvFatoratiFeeVal.visibility = View.VISIBLE

            mDataBinding.tvFatoratiFeeVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mActivityViewModel.fatoratiFeeAmountCalculated
        }

        mDataBinding.tvSenderNameVal.text =
            Constants.balanceInfoAndResponse?.firstname + " " + Constants.balanceInfoAndResponse?.surname
        mDataBinding.tvSenderNumberVal.text = Constants.CURRENT_USER_MSISDN

        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            mDataBinding.tvOwnerNameVal.visibility = View.GONE
            mDataBinding.tvOwnerNameTitle.visibility = View.GONE
            mDataBinding.divider2.visibility = View.GONE
        }

        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            mDataBinding.tvOwnerNameVal.visibility = View.GONE
            mDataBinding.tvOwnerNameTitle.visibility = View.GONE
            mDataBinding.divider2.visibility = View.GONE
        }

        mDataBinding.tvCompanyNameVal.text =
            mActivityViewModel.transferdAmountTo.substringBefore("@")
//        var ReceiverName = Constants.balanceInfoAndResponse.firstname +" " +Constants.balanceInfoAndResponse.surname
        mActivityViewModel.ReceiverName = mActivityViewModel.custname
        mDataBinding.tvOwnerNameVal.text = mActivityViewModel.ReceiverName

        var totalFee = "0.00"
        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            for (i in mActivityViewModel.listOfPostPaidBillPaymentQuote.indices) {
                var item = mActivityViewModel.listOfPostPaidBillPaymentQuote[i]
                if (item.quoteList != null && item.quoteList.isNotEmpty()) {
                    mActivityViewModel.listOfSelectedBillFee.add(item.quoteList[0].fee.amount.toString())
                    totalFee = (totalFee.toDouble() + item.quoteList[0].fee.amount).toString()
                }
            }
        }
        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            /*for(i in mActivityViewModel.listOfFatoratiQuote.indices){
                var item = mActivityViewModel.listOfFatoratiQuote[i]
                if(item.quoteList!=null && item.quoteList.isNotEmpty()){
                    mActivityViewModel.listOfSelectedBillFee.add(item.quoteList[0].fee.amount.toString())
                    totalFee = (totalFee.toDouble()+item.quoteList[0].fee.amount).toString()
                }
            }*/
            totalFee = mActivityViewModel.fatoratiFee
        }

        mActivityViewModel.feeAmount = Constants.converValueToTwoDecimalPlace(totalFee.toDouble())

        var totalAmount =
            Constants.converValueToTwoDecimalPlace(mActivityViewModel.totalSelectedBillAmount.toDouble())
        mDataBinding.tvReceiptCodeVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + totalAmount
        if(mActivityViewModel.isBillUseCaseSelected.get()!!){
            mDataBinding.tvDHVal.text =
                Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mActivityViewModel.feeAmount
        }else if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
            if(mActivityViewModel.feeAmount.equals("0.00") || mActivityViewModel.feeAmount.equals("0")){
                mDataBinding.tvDHTitle.visibility = View.GONE
                mDataBinding.tvDHVal.visibility = View.GONE
            }else{
                mDataBinding.tvDHTitle.visibility = View.VISIBLE
                mDataBinding.tvDHVal.visibility = View.VISIBLE
            }

            mDataBinding.tvDHVal.text =
                Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mActivityViewModel.feeAmount
        }

        if(mActivityViewModel.isBillUseCaseSelected.get()!!){
            amountToTransfer = Constants.addAmountAndFee(
                mActivityViewModel.totalSelectedBillAmount.toDouble(),
                totalFee.toDouble()
            )
        } else if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
            amountToTransfer = Constants.addAmountAndFee(
                mActivityViewModel.totalSelectedBillAmount.toDouble(),
                totalFee.toDouble()
            )

            amountToTransfer = Constants.addAmountAndFee(
                amountToTransfer.toDouble(),
                mActivityViewModel.fatoratiFeeAmountCalculated.toDouble()
            )
        }

        var totalCost = Constants.converValueToTwoDecimalPlace(amountToTransfer.toDouble())
        mDataBinding.tvAmountVal.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + totalCost

        mDataBinding.receiverNameGroup.visibility = View.GONE
    }

    private fun setStrings() {
        //        tvCompanyNameVal == ReceiverNumber
//        tvOwnerNameVal == ReceiverName
//        tvReceiptCodeVal = bill
//        tvDHVal == Fee
//
//        tvAmountVal == AmountTotal

        mDataBinding.tvCompanyNameTitle.text = LanguageData.getStringValue("BillPaymentBillerName")
        mDataBinding.tvOwnerNameTitle.text = LanguageData.getStringValue("ReceiverName")
        mDataBinding.tvReceiptCodeTitle.text = LanguageData.getStringValue("Bill")
        if(mActivityViewModel.isBillUseCaseSelected.get()!!){
            mDataBinding.tvDHTitle.text = LanguageData.getStringValue("TotalFee")
        }else if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
            mDataBinding.tvDHTitle.text = LanguageData.getStringValue("BillPaymentMTCashFee")
        }
        mDataBinding.tvAmountTitle.text = LanguageData.getStringValue("Amount")

        mDataBinding.tvConfirmationTitle.text = LanguageData.getStringValue("Confirmation")

        mDataBinding.btnConfirmationCancel.text = LanguageData.getStringValue("BtnTitle_Cancel")
        mDataBinding.btnConfirmationPay.text = LanguageData.getStringValue("BtnTitle_Pay")

        mDataBinding.tvSendNameTitle.text = LanguageData.getStringValue("SenderName")
        mDataBinding.tvSendNumberTitle.text = LanguageData.getStringValue("SenderNumber")

        mDataBinding.tvContactNumTitle.text = LanguageData.getStringValue("Source")
        mDataBinding.tvContactNumVal.text = LanguageData.getStringValue("Wallet")

        mDataBinding.tvFatoratiFeeTitle.text = LanguageData.getStringValue("TotalFee")
    }

    override fun onSubmitClickListner(view: View) {
        DialogUtils.showPasswordDialoge(activity,
            object : DialogUtils.OnPasswordDialogClickListner {
                override fun onDialogYesClickListner(password: String) {
                    Constants.HEADERS_FOR_PAYEMNTS = true
                    Constants.CURRENT_USER_CREDENTIAL = password

                    if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
                        payPostPaidBills()
                    }

                    if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                        payFatoratiBills()
                    }
                }

            })
    }

    private fun payFatoratiBills() {
        selectedFatoratiListOfInvoice.clear()
        selectedFatoratiListOfInvoice.addAll(mActivityViewModel.selectedFatoraitIvoicesList.get()!!)


        mActivityViewModel.totalBillSelected = selectedFatoratiListOfInvoice.size
        if (selectedFatoratiListOfInvoice != null && mActivityViewModel.selectedIvoicesQuoteHash != null) {
            callFatoratiBillsInSequence(mActivityViewModel.fatoratiCounter)
        }
    }

    private fun callFatoratiBillsInSequence(fatoratiCounter: Int) {

        /*var i = fatoratiCounter
            var quoteId: String?
            quoteId =
                mActivityViewModel.selectedIvoicesQuoteHash[selectedFatoratiListOfInvoice[i].idArticle]

            mActivityViewModel.selectBillAmount = selectedFatoratiListOfInvoice[i].prixTTC
            if (!mActivityViewModel.selectedIvoicesQuoteList.get().isNullOrEmpty()) {
                if (!mActivityViewModel.selectedIvoicesQuoteList.get()!![i].equals("-1")) {
                    mActivityViewModel.requestForFatoratiApi(
                        activity,
                        selectedFatoratiListOfInvoice[i].prixTTC,
                        selectedFatoratiListOfInvoice[i].idArticle,
                        selectedFatoratiListOfInvoice[i].prixTTC,
                        selectedFatoratiListOfInvoice[i].typeArticle,
                        quoteId!!,
                        mActivityViewModel.isMultipleBillSelected
                    )
                } else {
                    mActivityViewModel.listOfFatorati.add(
                        BillPaymentFatoratiResponse(
                            arrayListOf(), "", "Failed", "", "",
                            FatoratieFinancialReceiptResponse(
                                "",
                                Financialreceipt(
                                    FatoratieAmount(0.0, ""), "", "", FatoratieFee(0.0, ""),
                                    "", "", "", "", "", "", "", ""
                                ), ""
                            )
                            , "", "1500", "", "", "", ""
                        )
                    )
                }
            }
        mActivityViewModel.fatoratiCounter=mActivityViewModel.fatoratiCounter+1*/

        var listOfFatoratiParams: ArrayList<com.es.marocapp.model.requests.Param> = arrayListOf()
        var totalSelectedBillAmount = "0.00"
        for (i in selectedFatoratiListOfInvoice.indices) {

            var convertedBillAmount = selectedFatoratiListOfInvoice[i].prixTTC
//            mActivityViewModel.listOfSelectedBillAmount.add(convertedBillAmount)
            totalSelectedBillAmount =
                ((totalSelectedBillAmount.toDouble() + convertedBillAmount.toDouble())).toString()
            Log.i("TotalBillAmount", mActivityViewModel.totalSelectedBillAmount)

            listOfFatoratiParams.add(
                Param(
                    selectedFatoratiListOfInvoice[i].idArticle,
                    selectedFatoratiListOfInvoice[i].prixTTC,
                    selectedFatoratiListOfInvoice[i].typeArticle
                )
            )

            /*if(mActivityViewModel.fatoratiFeeAmountCaseImplemented){
                listOfFatoratiParams.add(
                    Param(
                        mActivityViewModel.fatoratiStepFourObserver.get()!!.typeFrais,
                        mActivityViewModel.fatoratiFeeAmountCalculated,
                        "1"
                    )
                )

            }
            Previously We are send idArticle which we are getting from backend in respnose of Step Four API now we are sending Frais hardcoded
            */
            if(mActivityViewModel.fatoratiFeeAmountCaseImplemented){
                listOfFatoratiParams.add(
                    Param(
                       "Frais",
                        mActivityViewModel.fatoratiFeeAmountCalculated,
                        "1"
                    )
                )

            }
        }
        var quoteId = ""
        if (mActivityViewModel.selectedIvoicesQuoteList.get()?.isNotEmpty()!!) {
            quoteId = mActivityViewModel.selectedIvoicesQuoteList.get()!![0]
        }

        var amountToSendInRequest = ""
        if(mActivityViewModel.fatoratiFeeAmountCaseImplemented){
            amountToSendInRequest = (mActivityViewModel.totalSelectedBillAmount.toDouble() + mActivityViewModel.fatoratiFeeAmountCalculated.toDouble()).toString()
        }else{
            amountToSendInRequest = mActivityViewModel.totalSelectedBillAmount
        }


        /*mActivityViewModel.requestForFatoratiApi(
            activity,
            mActivityViewModel.totalSelectedBillAmount,
            quoteId,
            mActivityViewModel.isMultipleBillSelected,
            listOfFatoratiParams
        )*/
        mActivityViewModel.requestForFatoratiApi(
            activity,
            amountToSendInRequest,
            quoteId,
            mActivityViewModel.isMultipleBillSelected,
            listOfFatoratiParams
        )
    }

    private fun payPostPaidBills() {
        selectedListOfInvoice.clear()
        selectedListOfInvoice.addAll(mActivityViewModel.selectedIvoicesList.get()!!)
        mActivityViewModel.totalBillSelected = selectedListOfInvoice.size
        if (selectedListOfInvoice != null && mActivityViewModel.selectedIvoicesQuoteHash != null) {
            callPostPaidBillsInSequence(mActivityViewModel.postPaidCounter)
        }
    }

    private fun callPostPaidBillsInSequence(postPaidCounter: Int) {
        var i = postPaidCounter
        var quoteId: String?
        quoteId =
            mActivityViewModel.selectedIvoicesQuoteHash[selectedListOfInvoice[i].ohrefnum]
        //Ohrefnum(16) + month (8) + OpenAmount (15) + OHXACT (38)
        var selectBillInvoice =
            selectedListOfInvoice[i].ohrefnum + selectedListOfInvoice[i].month + selectedListOfInvoice[i].openAmount + selectedListOfInvoice[i].ohxact
        mActivityViewModel.selectBillAmount = selectedListOfInvoice[i].openAmount
        if (!mActivityViewModel.selectedIvoicesQuoteList.get().isNullOrEmpty()) {
            if (!mActivityViewModel.selectedIvoicesQuoteList.get()!![i].equals("-1")) {
                mActivityViewModel.requestForPostPaidBillPaymentApi(
                    activity,
                    selectedListOfInvoice[i].month,
                    selectedListOfInvoice[i].ohrefnum,
                    selectedListOfInvoice[i].ohxact,
                    selectedListOfInvoice[i].openAmount,
                    quoteId!!,
                    mActivityViewModel.isMultipleBillSelected
                )
            } else {
                mActivityViewModel.listOfPostPaidBillPayment.add(
                    PostPaidBillPaymentResponse(
                        arrayListOf(), "", "Failed", "", "", "", "",
                        "1500", "", "", "", ""
                    )
                )
            }
        }

        mActivityViewModel.postPaidCounter = mActivityViewModel.postPaidCounter + 1

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