package com.es.marocapp.usecase.transaction

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.TransactionHistoryAdapter
import com.es.marocapp.databinding.FragmentTransactionBinding
import com.es.marocapp.model.CustomModelHistoryItem
import com.es.marocapp.model.responses.History
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.fragment_transaction.*

class TransactionFragment : BaseFragment<FragmentTransactionBinding>(), TransactionClickListeners {

    private lateinit var transactionViewModel: TransactionViewModel
    private var mTransactionsList : ArrayList<CustomModelHistoryItem> = ArrayList()
    private lateinit var mTransactionHistoryAdapter: TransactionHistoryAdapter

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
            override fun onHistoryDetailClickListner(customModelHistoryItem: CustomModelHistoryItem?) {
                Toast.makeText(context,"Item Clicked",Toast.LENGTH_SHORT).show()
            }

        })

        mDataBinding.transactionsRecyclerView.apply {
            adapter = mTransactionHistoryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        transactionViewModel.requestForGetTransactionHistoryApi(activity,Constants.CURRENT_USER_MSISDN)

        subscribeObserver()
    }

    private fun subscribeObserver() {
        transactionViewModel.getTransactionsResponseListner.observe(this@TransactionFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(!it.historyResponse.isNullOrEmpty()){
                        var mTransactionList = arrayListOf<CustomModelHistoryItem>()
                        for(i in it.historyResponse.indices){
                            mTransactionList.add(CustomModelHistoryItem(0,it.historyResponse[i].date,
                                History("","","","","","","","",
                                "","","","","","","","","","","","",
                                    "","","","","","","","","","","","")
                            ))

                            for(j in it.historyResponse[i].historyList.indices){
                                mTransactionList.add(CustomModelHistoryItem(1,it.historyResponse[i].date,it.historyResponse[i].historyList[j]))
                            }
                        }

                        mTransactionHistoryAdapter.updateHistoryList(mTransactionList)

                    }else{
                        Toast.makeText(activity,"List Empty",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            })
    }

    override fun onBackBtnClick(view: View) {
        (activity as MainActivity).navController.popBackStack(R.id.navigation_home,false)
    }

    override fun onSortBtnClick(view: View) {
    }
}