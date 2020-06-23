package com.es.marocapp.usecase.billpayment.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.BillDetailItemAdapter
import com.es.marocapp.databinding.FragmentBillPaymentBillDetailsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.InvoiceCustomModel
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentClickListner
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class FragmentPostPaidBillDetails : BaseFragment<FragmentBillPaymentBillDetailsBinding>(),
    BillPaymentClickListner {

    private lateinit var mActivityViewModel : BillPaymentViewModel

    private lateinit var mBillDetailsAdapter : BillDetailItemAdapter

    private var listOfCustomInvoice = arrayListOf<InvoiceCustomModel>()

    private var selectedListOfInvoice = arrayListOf<InvoiceCustomModel>()

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

        mActivityViewModel.popBackStackTo = R.id.fragmentBillPaymentMsisdn

        for(i in mActivityViewModel.PostPaidFinancialResourceInfoObserver.get()!!.invoices.indices){
            var item = mActivityViewModel.PostPaidFinancialResourceInfoObserver.get()!!.invoices[i]
            listOfCustomInvoice.add(InvoiceCustomModel(false,item.month,item.ohrefnum,item.ohxact,item.openAmount))
        }

        mBillDetailsAdapter = BillDetailItemAdapter(listOfCustomInvoice)
        mDataBinding.mBillsRecycler.apply {
            adapter = mBillDetailsAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        mDataBinding.selectAllCheckBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    for(i in listOfCustomInvoice.indices){
                        listOfCustomInvoice[i].isBillSelected = isChecked
                    }
                    mBillDetailsAdapter.notifyDataSetChanged()
                }else{
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

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@FragmentPostPaidBillDetails, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        })

        mActivityViewModel.getPostPaidBillPaymentQuoteResponseListner.observe(this@FragmentPostPaidBillDetails,
            Observer {
                if(!it.isNullOrEmpty()){
                    var isAllBillPaymentSucceed = false
                    var mQouteList : ArrayList<String> = arrayListOf()
                    for(i in it.indices){
                        if(it[i].responseCode.equals(ApiConstant.API_SUCCESS)){
                            isAllBillPaymentSucceed = true
                            if(it[i].quoteList.isNotEmpty()){
                                mQouteList.add(it[i].quoteList[0].quoteid)
                            }
                        }else{
                            mQouteList.add("-1")
                        }
                    }

                    if(isAllBillPaymentSucceed){
                        mActivityViewModel.selectedIvoicesQuoteList.set(mQouteList)
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidBillDetails_to_fragmentBillPaymentPostPaidConfirmation)
                    }else{
                        DialogUtils.showErrorDialoge(activity,LanguageData.getStringValue("SomethingWentWrong"))
                    }
                }else{
                    DialogUtils.showErrorDialoge(activity,LanguageData.getStringValue("SomethingWentWrong"))
                }
            })
    }

    private fun setStrings() {
        mDataBinding.btnNext.text = LanguageData.getStringValue("BtnTitle_Pay")
    }

    override fun onSubmitClickListner(view: View) {
        selectedListOfInvoice.addAll(mBillDetailsAdapter.getUpdateList())

        mActivityViewModel.totalBillSelected = selectedListOfInvoice.size
        mActivityViewModel.selectedIvoicesList.set(selectedListOfInvoice)
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

    override fun onBackClickListner(view: View) {

    }

}