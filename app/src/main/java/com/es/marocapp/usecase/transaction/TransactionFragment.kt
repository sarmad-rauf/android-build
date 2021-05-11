package com.es.marocapp.usecase.transaction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.LanguageCustomSpinnerAdapter
import com.es.marocapp.adapter.TransactionHistoryAdapter
import com.es.marocapp.databinding.FragmentTransactionBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.CustomModelHistoryItem
import com.es.marocapp.model.responses.History
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class TransactionFragment : BaseFragment<FragmentTransactionBinding>(), TransactionClickListeners {

    private lateinit var transactionViewModel: TransactionViewModel
    private var mTransactionsList : ArrayList<History> = ArrayList()
    private lateinit var mTransactionHistoryAdapter: TransactionHistoryAdapter
    lateinit var acountTypeSpinnerAdapter: LanguageCustomSpinnerAdapter

    override fun setLayout(): Int {
        return R.layout.fragment_transaction
    }

    override fun init(savedInstanceState: Bundle?) {
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        mDataBinding.apply {
            viewmodel = transactionViewModel
            listener = this@TransactionFragment
        }

        (activity as MainActivity).setHomeToolbarVisibility(false)

        mTransactionHistoryAdapter = TransactionHistoryAdapter(mTransactionsList,object : TransactionHistoryAdapter.HistoryDetailListner{
            override fun onHistoryDetailClickListner(customModelHistoryItem: History) {
                if (customModelHistoryItem != null) {
                    Constants.currentTransactionItem = customModelHistoryItem
                    startActivity(Intent(activity as MainActivity,TransactionDetailsActivity::class.java))
                }
            }

        })

        mDataBinding.transactionsRecyclerView.apply {
            adapter = mTransactionHistoryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        transactionViewModel.requestForGetTransactionHistoryApi(activity,Constants.CURRENT_USER_MSISDN,false)
        (activity as MainActivity).isTransactionFragmentNotVisible = false
        (activity as MainActivity).showTransactionsDetailsIndirectly = true

        //----------for handling Backpress of activity----------
        (activity as MainActivity).isGenerateQRFragmentShowing = false
        (activity as MainActivity).isFaqsFragmentShowing = false
        (activity as MainActivity).isSideMenuShowing = false
        (activity as MainActivity).isTranactionDetailsFragmentShowing = false
        (activity as MainActivity).isHomeFragmentShowing = false
        (activity as MainActivity).isTransacitonFragmentShowing = true

        subscribeObserver()
        setStrings()
        subscribeForSpinnerListner()
    }

    private fun setStrings() {
        mDataBinding.tvTransactionHistoryTitle.text = LanguageData.getStringValue("TransactionHistory")
        mDataBinding.tvNoDataFound.text = LanguageData.getStringValue("NoDataFound")
        mDataBinding.selectAcountTitile.text=LanguageData.getStringValue("SelectAccountType")
        Constants.acountTypeList.clear()
        if (Constants.IS_AGENT_USER) {
            if(Constants.acountTypeList.isEmpty()) {
            LanguageData.getStringValue("Wallet")?.let { Constants.acountTypeList.add(it) }
            for( i in Constants.getAccountsResponseArray.indices)
            {
                if(Constants.getAccountsResponseArray.get(i).profileName.equals(Constants.MERCHANT_AGENT_PROFILE_NAME)&&
                    Constants.getAccountsResponseArray.get(i).accountStatus.equals("ACTIVE"))
                {
                    LanguageData.getStringValue("Merchant")?.let { Constants.acountTypeList.add(it) }
                }
                if (Constants.getAccountsResponseArray[i].accountType.equals("COMMISSIONING")) {
                    LanguageData.getStringValue("Commissioning")
                        ?.let { Constants.acountTypeList.add(it) }
                }
            }
            }
            val acountTypeArray: Array<String> =
                Constants.acountTypeList.toArray(arrayOfNulls<String>(Constants.acountTypeList.size))
            acountTypeSpinnerAdapter =
                LanguageCustomSpinnerAdapter(
                    activity as MainActivity,
                    acountTypeArray,
                    (activity as MainActivity).resources.getColor(R.color.colorBlack),true
                )
            //  mDataBinding.acountTypeSpinner
            mDataBinding.acountTypeSpinner.apply {
                adapter = acountTypeSpinnerAdapter
            }
        }
        else{
            if(Constants.acountTypeList.isEmpty()) {
                LanguageData.getStringValue("Wallet")?.let { Constants.acountTypeList.add(it) }
            }
            //Constants.acountTypeList.add("Wallet")
            val acountTypeArray: Array<String> =
                Constants.acountTypeList.toArray(arrayOfNulls<String>(Constants.acountTypeList.size))
            acountTypeSpinnerAdapter =
                LanguageCustomSpinnerAdapter(
                    activity as MainActivity,
                    acountTypeArray,
                    (activity as MainActivity).resources.getColor(R.color.colorBlack),true
                )
            //  mDataBinding.acountTypeSpinner
            mDataBinding.acountTypeSpinner.apply {
                adapter = acountTypeSpinnerAdapter
            }
        }
    }

    private fun subscribeObserver() {
        transactionViewModel.getTransactionsResponseListner.observe(this@TransactionFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(!it.historyResponse.isNullOrEmpty()){
                        var mTransactionList = arrayListOf<CustomModelHistoryItem>()
                      /*  for(i in it.historyResponse.indices){
                            mTransactionList.add(CustomModelHistoryItem(0,it.historyResponse[i].date,
                                History("","","","","","","","",
                                "","","","","","","","","","","","",
                                    "","","","","","","","","","","","")
                            ))

                            for(j in it.historyResponse[i].historyList.indices){
                                mTransactionList.add(CustomModelHistoryItem(1,it.historyResponse[i].historyList[j].date,it.historyResponse[i].historyList[j]))
                            }
                        }*/

                        mDataBinding.tvNoDataFound.visibility = View.GONE
                        //Clearing Past Data if Any
                        mTransactionHistoryAdapter.updateHistoryList()
                        mTransactionHistoryAdapter.updateHistoryList(it.historyResponse)

                    }else{
                        mTransactionHistoryAdapter.updateHistoryList()
                        mDataBinding.tvNoDataFound.visibility = View.VISIBLE
                    }
                }else{
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            })
    }

    override fun onBackBtnClick(view: View) {
        (activity as MainActivity).showTransactionsDetailsIndirectly = false
        (activity as MainActivity).navController.popBackStack(R.id.navigation_home,false)
    }

    override fun onSortBtnClick(view: View) {
    }

    private fun subscribeForSpinnerListner() {

        // homeViewModel.requestForGetTransactionHistoryApi(activity,Constants.CURRENT_USER_MSISDN)
        mDataBinding.acountTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val lastSelection = Constants.CURRENT_ACOUNT_TYPE_SELECTED
                val currentSelection = Constants.acountTypeList.get(position)
                if(!lastSelection.equals(currentSelection)) {
                    Constants.CURRENT_ACOUNT_TYPE_SELECTED = currentSelection
                    if (lastSelection != null) {
                        Constants.LAST_ACOUNT_TYPE_SELECTED = lastSelection
                    }
                    if(currentSelection.equals(LanguageData.getStringValue("Merchant")))
                    {
                        for(i in Constants.getAccountsResponseArray.indices)
                        {
                            if(Constants.getAccountsResponseArray.get(i).profileName.equals(Constants.MERCHANT_AGENT_PROFILE_NAME)&&
                                Constants.getAccountsResponseArray.get(i).accountStatus.equals("ACTIVE"))
                            {
                                transactionViewModel.passNewFri(Constants.getAccountsResponseArray.get(i).accountFri)
                            }
                        }

                    }
                    else if(currentSelection.equals(LanguageData.getStringValue("Commissioning")))
                    {
                        for(i in Constants.getAccountsResponseArray.indices)
                        {
                            if(Constants.getAccountsResponseArray.get(i).accountType.equals("COMMISSIONING"))
                            {
                                transactionViewModel.passNewFri(Constants.getAccountsResponseArray.get(i).accountFri)
                            }
                        }
                    }
                    else{
                        transactionViewModel.requestForGetTransactionHistoryApi(activity,Constants.CURRENT_USER_MSISDN,false)
                    }
                }
            }
        }

        transactionViewModel.acountFri.observe(this, Observer {

            //excluding String Fri:
            val fri = it.substring(4,it.length)
            transactionViewModel.requestForGetTransactionHistoryApi(activity,fri,true)
        })
    }
}