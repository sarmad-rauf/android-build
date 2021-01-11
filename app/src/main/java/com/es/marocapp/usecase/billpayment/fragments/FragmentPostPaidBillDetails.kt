package com.es.marocapp.usecase.billpayment.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.BillDetailFatoratiItemAdapter
import com.es.marocapp.adapter.BillDetailItemAdapter
import com.es.marocapp.databinding.FragmentBillPaymentBillDetailsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.requests.FatoratiQuoteParam
import com.es.marocapp.model.responses.*
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentClickListner
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger
import java.text.SimpleDateFormat

class FragmentPostPaidBillDetails : BaseFragment<FragmentBillPaymentBillDetailsBinding>(),
    BillPaymentClickListner {

    private lateinit var mActivityViewModel : BillPaymentViewModel

    private lateinit var mBillDetailsAdapter : BillDetailItemAdapter
    private lateinit var mFatoratiBillDetailsAdapter : BillDetailFatoratiItemAdapter

    private var listOfCustomInvoice = arrayListOf<InvoiceCustomModel>()
    private var selectedListOfInvoice = arrayListOf<InvoiceCustomModel>()

    private var listOfFatoratiCustomInvoice = arrayListOf<FatoratiCustomParamModel>()
    private var listOfFatoratiCustomDateInvoice = arrayListOf<FatoratiCustomDateParamModel>()
    private var selectedFatoratiListOfInvoice = arrayListOf<FatoratiCustomParamModel>()

    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_bill_details
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as BillPaymentActivity).get(BillPaymentViewModel::class.java)
        mDataBinding.apply {
            listner = this@FragmentPostPaidBillDetails
        }

        (activity as BillPaymentActivity).setHeaderTitle(
            LanguageData.getStringValue("BillDetails").toString()
        )

        (activity as BillPaymentActivity).setHeaderVisibility(true)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(false)

        if(mActivityViewModel.isQuickRechargeCallForBillOrFatouratie.get()!!){
            mActivityViewModel.popBackStackTo = R.id.fragmentBillPaymentMain
        }else{
            mActivityViewModel.popBackStackTo = R.id.fragmentBillPaymentMsisdn
        }

        listOfCustomInvoice.clear()
        if(mActivityViewModel.isBillUseCaseSelected.get()!!){
            for(i in mActivityViewModel.PostPaidFinancialResourceInfoObserver.get()!!.invoices.indices){
                var item = mActivityViewModel.PostPaidFinancialResourceInfoObserver.get()!!.invoices[i]
                listOfCustomInvoice.add(InvoiceCustomModel(false,item.month,item.ohrefnum,item.ohxact,item.openAmount))
            }
           if(mActivityViewModel.PostPaidFinancialResourceInfoObserver.get()!!.invoices!=null &&
               mActivityViewModel.PostPaidFinancialResourceInfoObserver.get()!!.invoices.size>0){
               mDataBinding.noDataTv.visibility=View.INVISIBLE
           }
        }

        listOfFatoratiCustomInvoice.clear()
        listOfFatoratiCustomDateInvoice.clear()
        if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
            for(i in mActivityViewModel.fatoratiStepFourObserver.get()!!.params.indices){
                var item = mActivityViewModel.fatoratiStepFourObserver.get()!!.params[i]
                listOfFatoratiCustomInvoice.add(FatoratiCustomParamModel(false,item.description,item.idArticle,item.prixTTC,item.typeArticle))
                listOfFatoratiCustomDateInvoice.add(FatoratiCustomDateParamModel(false,item.description,item.idArticle,item.prixTTC,item.typeArticle,getDateFromString(item.description)))
            }

            if(mActivityViewModel.fatoratiStepFourObserver.get()!!.params!=null &&
                mActivityViewModel.fatoratiStepFourObserver.get()!!.params.size>0){
                mDataBinding.noDataTv.visibility=View.INVISIBLE
            }
        }

        for(i in listOfFatoratiCustomDateInvoice.indices){
            Logger.debugLog("TestingDateList",listOfFatoratiCustomDateInvoice[i].date)
        }

        listOfFatoratiCustomDateInvoice.sortedByDescending { it.date }
        for(j in listOfFatoratiCustomDateInvoice.indices){
            Logger.debugLog("TestingDateListParsed",listOfFatoratiCustomDateInvoice[j].date )
        }

        mBillDetailsAdapter = BillDetailItemAdapter(listOfCustomInvoice)
        mFatoratiBillDetailsAdapter = BillDetailFatoratiItemAdapter(listOfFatoratiCustomInvoice)
        mDataBinding.mBillsRecycler.apply {
            if(mActivityViewModel.isBillUseCaseSelected.get()!!){
                adapter = mBillDetailsAdapter
            }
            if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
                adapter = mFatoratiBillDetailsAdapter
            }
            layoutManager = LinearLayoutManager(activity)
        }

        mDataBinding.selectAllCheckBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {

                    if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
                        for(i in listOfFatoratiCustomInvoice.indices){
                            listOfFatoratiCustomInvoice[i].isItemSelected = isChecked
                        }
                        mFatoratiBillDetailsAdapter.notifyDataSetChanged()
                    }

                    if(mActivityViewModel.isBillUseCaseSelected.get()!!){
                        for(i in listOfCustomInvoice.indices){
                            listOfCustomInvoice[i].isBillSelected = isChecked
                        }
                        mBillDetailsAdapter.notifyDataSetChanged()
                    }

            }

        })

        setStrings()
        subscribeObserver()

    }

    fun getDateFromString(description: String) : String{
        //"description":"NOM: Mohammed TEMSAMANI - ADRESSE:99000, Av., Hassan II, - DATE : 20170522" yyyyMMdd
        // Name - Address - Date
        return if(description.isNullOrEmpty()){
            Constants.getCurrentDate()
        }else{
            var withoutNameString = description.substringAfter("-")
            var withoutAddressNameString = withoutNameString.substringAfter("-")
            var withoutCollenDate  = withoutAddressNameString.substringAfter(":").trim()
            Logger.debugLog("TestingDate",withoutCollenDate)
            val date = Constants.parseDateFromString(withoutCollenDate)
            Logger.debugLog("TestingDateParsed",date)
            return date
        }
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@FragmentPostPaidBillDetails, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        })

        mActivityViewModel.getPostPaidBillPaymentQuoteResponseListner.observe(this@FragmentPostPaidBillDetails,
            Observer {
                if(!it.isNullOrEmpty()){
                    var isAllBillPaymentSucceed = false
                    var mQouteList : ArrayList<String> = arrayListOf()
                    var mQouteHash = HashMap<String,String>()
                    for(i in it.indices){
                        if(it[i].responseCode.equals(ApiConstant.API_SUCCESS)){
                            isAllBillPaymentSucceed = true
                            if(it[i].quoteList.isNotEmpty()){
                                mQouteList.add(it[i].quoteList[0].quoteid)
                                mQouteHash.put(it[i].invoiceOhrefnum,it[i].quoteList[0].quoteid)
                            }
                        }else{
                            mQouteList.add("-1")
                            mQouteHash.put("-1","-1")
                        }
                    }

                    if(isAllBillPaymentSucceed){
                        mActivityViewModel.selectedIvoicesQuoteList.set(mQouteList)
                        mActivityViewModel.selectedIvoicesQuoteHash=mQouteHash
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidBillDetails_to_fragmentBillPaymentPostPaidConfirmation)
                    }else{
                        DialogUtils.showErrorDialoge(activity,LanguageData.getStringValue("SomethingWentWrong"))
                    }
                }else{
                    DialogUtils.showErrorDialoge(activity,LanguageData.getStringValue("SomethingWentWrong"))
                }
            }
        )

        mActivityViewModel.getPostPaidFatoratiQuoteResponseListner.observe(this@FragmentPostPaidBillDetails,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(it.quoteList.isNotEmpty()){
                        mActivityViewModel.selectedIvoicesQuoteList.set(arrayListOf(it.quoteList[0].quoteid))
                        mActivityViewModel.fatoratiFee = it.quoteList[0].fee.amount.toString()
                    }
                    (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidBillDetails_to_fragmentBillPaymentPostPaidConfirmation)
                }else{
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
                /*if(!it.isNullOrEmpty()){
                    var isAllBillPaymentSucceed = false
                    var mQouteList : ArrayList<String> = arrayListOf()
                    var mQouteHash = HashMap<String,String>()
                    for(i in it.indices){
                        if(it[i].responseCode.equals(ApiConstant.API_SUCCESS)){
                            isAllBillPaymentSucceed = true
                            if(it[i].quoteList.isNotEmpty()){
                                mQouteList.add(it[i].quoteList[0].quoteid)
                                mQouteHash.put(it[i].idArticle,it[i].quoteList[0].quoteid)
                            }
                        }else{
                            mQouteList.add("-1")
                            mQouteHash.put("-1","-1")
                        }
                    }

                    if(isAllBillPaymentSucceed){
                        mActivityViewModel.selectedIvoicesQuoteList.set(mQouteList)
                        mActivityViewModel.selectedIvoicesQuoteHash=mQouteHash
                    }else{
                        DialogUtils.showErrorDialoge(activity,LanguageData.getStringValue("SomethingWentWrong"))
                    }
                }else{
                }*/
            }
        )
    }

    private fun setStrings() {
        mDataBinding.btnNext.text = LanguageData.getStringValue("BtnTitle_Pay")
        mDataBinding.noDataTv.text = LanguageData.getStringValue("NoDataFound")
        mDataBinding.selectInvocieLabel.text = LanguageData.getStringValue("BillPaymentSelectInvoicesToPay")
    }

    override fun onSubmitClickListner(view: View) {
        if(mActivityViewModel.isBillUseCaseSelected.get()!!){
            selectedListOfInvoice.clear()
            selectedListOfInvoice.addAll(mBillDetailsAdapter.getUpdateList())
            payPostPaidBillsCall()
        }

        if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
            selectedFatoratiListOfInvoice.clear()
            selectedFatoratiListOfInvoice.addAll(mFatoratiBillDetailsAdapter.getUpdateList())
            payFatoratiBillsCall()
        }


    }

    private fun payFatoratiBillsCall() {
        Log.i("SelectedBillCOunt",selectedFatoratiListOfInvoice.size.toString())
        if(selectedFatoratiListOfInvoice.size.equals(0)){
            mDataBinding.btnNext.isClickable = false
            mDataBinding.btnNext.isActivated = false
        }else{
            mDataBinding.btnNext.isClickable = true
            mDataBinding.btnNext.isActivated = true

            mActivityViewModel.totalBillSelected = selectedFatoratiListOfInvoice.size
            mActivityViewModel.selectedFatoraitIvoicesList.set(selectedFatoratiListOfInvoice)
            //check for Multiple Bill Invoices Fatorati Use Case
            if(selectedListOfInvoice.size>1){
                mActivityViewModel.isMultipleBillSelected = "true"
            }else{
                mActivityViewModel.isMultipleBillSelected = "false"
            }

            var listOfFatoratiParams : ArrayList<FatoratiQuoteParam> = arrayListOf()
            for(i in selectedFatoratiListOfInvoice.indices){

                var convertedBillAmount  = selectedFatoratiListOfInvoice[i].prixTTC
                mActivityViewModel.listOfSelectedBillAmount.add(convertedBillAmount)
                mActivityViewModel.totalSelectedBillAmount = ((mActivityViewModel.totalSelectedBillAmount.toDouble()+convertedBillAmount.toDouble())).toString()
                Log.i("TotalBillAmount",mActivityViewModel.totalSelectedBillAmount)

                listOfFatoratiParams.add(
                    FatoratiQuoteParam(selectedFatoratiListOfInvoice[i].idArticle,
                        selectedFatoratiListOfInvoice[i].prixTTC,
                        selectedFatoratiListOfInvoice[i].typeArticle)
                )
            }

            if(!mActivityViewModel.fatoratiStepFourObserver.get()?.typeFrais.isNullOrEmpty()){
                if(!mActivityViewModel.fatoratiStepFourObserver.get()?.valeurFrais.isNullOrEmpty()){
                    if(mActivityViewModel.fatoratiStepFourObserver.get()?.typeFrais.equals(Constants.BILL_PAYMENT_TYPE_FORFAIT_FACTURE)){
                        var forfaitFactureFeeToApplied  = mActivityViewModel.fatoratiStepFourObserver.get()?.valeurFrais?.toInt()
                        var fortaitFactureFee  = selectedListOfInvoice.size * forfaitFactureFeeToApplied!!
                        var fortaitFactureFeeInDouble = Constants.converValueToTwoDecimalPlace(fortaitFactureFee.toDouble())
                        var totalFeeAfterAddingForfaitFactureFee = mActivityViewModel.totalSelectedBillAmount.toDouble() + fortaitFactureFee

                        Logger.debugLog("forfaitFactureFeeToApplied",forfaitFactureFeeToApplied.toString())
                        Logger.debugLog("fortaitFactureFee",fortaitFactureFee.toString() + "Number Of Bill Selected = ${selectedListOfInvoice.size}")
                        Logger.debugLog("fortaitFactureFeeInDouble",fortaitFactureFeeInDouble)
                        Logger.debugLog("fortaitFactureFeeAfterAddingInTotal",totalFeeAfterAddingForfaitFactureFee.toString() + "Total Bill Amount is = ${mActivityViewModel.totalSelectedBillAmount}")

                       /* listOfFatoratiParams.add(
                            FatoratiQuoteParam(mActivityViewModel.fatoratiStepFourObserver.get()?.typeFrais!!,
                                fortaitFactureFee.toString(),
                                "1")
                        )*/
                        listOfFatoratiParams.add(
                            FatoratiQuoteParam("Frais",
                                fortaitFactureFee.toString(),
                                "1")
                        )

                        mActivityViewModel.fatoratiFeeAmountCalculated = fortaitFactureFee.toString()
                        mActivityViewModel.fatoratiFeeAmountCaseImplemented = true

                    }else if(mActivityViewModel.fatoratiStepFourObserver.get()?.typeFrais.equals(Constants.BILL_PAYMENT_TYPE_FORFAIT)){
                        var fortaitFeeToApplied = mActivityViewModel.fatoratiStepFourObserver.get()?.valeurFrais?.toDouble()
                        var forfaitFeeCalculated = mActivityViewModel.totalSelectedBillAmount.toDouble() + fortaitFeeToApplied!!

                        Logger.debugLog("fortaitFeeToApplied",fortaitFeeToApplied.toString())
                        Logger.debugLog("forfaitFeeCalculated",forfaitFeeCalculated.toString())

                        /*listOfFatoratiParams.add(
                            FatoratiQuoteParam(mActivityViewModel.fatoratiStepFourObserver.get()?.typeFrais!!,
                                fortaitFeeToApplied.toString(),
                                "1")
                        )*/
                        listOfFatoratiParams.add(
                            FatoratiQuoteParam("Frais",
                                fortaitFeeToApplied.toString(),
                                "1")
                        )

                        mActivityViewModel.fatoratiFeeAmountCalculated = fortaitFeeToApplied.toString()
                        mActivityViewModel.fatoratiFeeAmountCaseImplemented = true

                    }else if(mActivityViewModel.fatoratiStepFourObserver.get()?.typeFrais.equals(Constants.BILL_PAYMENT_TYPE_COMISSION)){
                        var commissionFee  = mActivityViewModel.fatoratiStepFourObserver.get()?.valeurFrais?.toDouble()
                        var commissionFeeCalculated: Double = ((mActivityViewModel.totalSelectedBillAmount.toDouble()* commissionFee!!)/100)
                        commissionFeeCalculated = Constants.converValueToTwoDecimalPlace(commissionFeeCalculated).toDouble()
                        var commissionFeeCalculatedAddedInTotalAmount = commissionFeeCalculated.toDouble() + mActivityViewModel.totalSelectedBillAmount.toDouble()

                        Logger.debugLog("commissionFee",commissionFee.toString())
                        Logger.debugLog("commissionFeeCalculated",commissionFeeCalculated.toString())
                        Logger.debugLog("commissionFeeCalculatedAddedInTotalAmount",commissionFeeCalculatedAddedInTotalAmount.toString())

                        /*listOfFatoratiParams.add(
                            FatoratiQuoteParam(mActivityViewModel.fatoratiStepFourObserver.get()?.typeFrais!!,
                                commissionFeeCalculated.toString(),
                                "1")
                        )
                        Previously We are send idArticle which we are getting from backend in respnose of Step Four API now we are sending Frais hardcoded
                        */
                        listOfFatoratiParams.add(
                            FatoratiQuoteParam("Frais",
                                commissionFeeCalculated.toString(),
                                "1")
                        )

                        mActivityViewModel.fatoratiFeeAmountCalculated = commissionFeeCalculated.toString()
                        mActivityViewModel.fatoratiFeeAmountCaseImplemented = true
                    }
                }else{
                    mActivityViewModel.fatoratiFeeAmountCalculated = "0.00"
                    mActivityViewModel.fatoratiFeeAmountCaseImplemented = false
                }
            }else {
                mActivityViewModel.fatoratiFeeAmountCalculated = "0.00"
                mActivityViewModel.fatoratiFeeAmountCaseImplemented = false
            }

            var amountToSendInRequest = ""
            if(mActivityViewModel.fatoratiFeeAmountCaseImplemented){
                amountToSendInRequest = (mActivityViewModel.totalSelectedBillAmount.toDouble() + mActivityViewModel.fatoratiFeeAmountCalculated.toDouble()).toString()
            }else{
                amountToSendInRequest = mActivityViewModel.totalSelectedBillAmount
            }

            /*mActivityViewModel.requestForFatoratiQuoteApi(activity,mActivityViewModel.totalSelectedBillAmount,mActivityViewModel.fatoratiStepFourObserver.get()?.refTxFatourati.toString(),
                mActivityViewModel.fatoratiStepFourObserver.get()?.totalAmount.toString(),listOfFatoratiParams)*/
            mActivityViewModel.requestForFatoratiQuoteApi(activity,amountToSendInRequest,mActivityViewModel.fatoratiStepFourObserver.get()?.refTxFatourati.toString(),
                mActivityViewModel.fatoratiStepFourObserver.get()?.totalAmount.toString(),listOfFatoratiParams)
        }
    }

    private fun payPostPaidBillsCall() {
        if(selectedListOfInvoice.size.equals(0)){
            mDataBinding.btnNext.isClickable = false
            mDataBinding.btnNext.isActivated = false
        }else{
            mDataBinding.btnNext.isClickable = true
            mDataBinding.btnNext.isActivated = true
            mActivityViewModel.totalBillSelected = selectedListOfInvoice.size
            mActivityViewModel.selectedIvoicesList.set(selectedListOfInvoice)
            //check for Multiple Bill Invoices Bill Use Case
            if(selectedListOfInvoice.size>1){
                mActivityViewModel.isMultipleBillSelected = "true"
            }else{
                mActivityViewModel.isMultipleBillSelected = "false"
            }
            for(i in selectedListOfInvoice.indices){
                //Ohrefnum(16) + month (8) + OpenAmount (15) + OHXACT (38)
                var convertedBillAmount  = (selectedListOfInvoice[i].openAmount.toDouble()/Constants.AMOUNT_CONVERSION_VALUE.toDouble()).toString()
                mActivityViewModel.listOfSelectedBillAmount.add(convertedBillAmount)
                mActivityViewModel.totalSelectedBillAmount = ((mActivityViewModel.totalSelectedBillAmount.toDouble()+convertedBillAmount.toDouble())).toString()
                Log.i("TotalBillAmount",mActivityViewModel.totalSelectedBillAmount)
                var selectBillInvoice = selectedListOfInvoice[i].ohrefnum+selectedListOfInvoice[i].month+selectedListOfInvoice[i].openAmount+selectedListOfInvoice[i].ohxact
                mActivityViewModel.selectBillAmount = selectedListOfInvoice[i].openAmount
                mActivityViewModel.requestForPostPaidBillPaymentQuoteApi(activity,selectedListOfInvoice[i].month,selectedListOfInvoice[i].ohrefnum,selectedListOfInvoice[i].ohxact,
                    selectedListOfInvoice[i].openAmount)
            }
        }
    }

    override fun onBackClickListner(view: View) {

    }

}