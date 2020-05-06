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
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
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

        mTransactionsList.apply {
            this.add(CustomModelHistoryItem(0,"16/04/2020","","",""))
            this.add(CustomModelHistoryItem(1,"16/04/2020","Electricity Bill","Company A","DH2.800.00"))
            this.add(CustomModelHistoryItem(1,"16/04/2020","Transfer","Christian Foley","DH5.800.00"))
            this.add(CustomModelHistoryItem(1,"16/04/2020","Cash Out","Company B","DH3.800.00"))

            this.add(CustomModelHistoryItem(0,"17/04/2020","","",""))
            this.add(CustomModelHistoryItem(1,"17/04/2020","Electricity Bill","Company A","DH2.800.00"))
            this.add(CustomModelHistoryItem(1,"17/04/2020","Transfer","Christian Foley","DH5.800.00"))
            this.add(CustomModelHistoryItem(1,"17/04/2020","Cash Out","Company B","DH3.800.00"))

            this.add(CustomModelHistoryItem(0,"18/04/2020","","",""))
            this.add(CustomModelHistoryItem(1,"18/04/2020","Electricity Bill","Company A","DH2.800.00"))
            this.add(CustomModelHistoryItem(1,"18/04/2020","Transfer","Christian Foley","DH5.800.00"))
            this.add(CustomModelHistoryItem(1,"18/04/2020","Cash Out","Company B","DH3.800.00"))
        }

        mTransactionHistoryAdapter = TransactionHistoryAdapter(mTransactionsList,object : TransactionHistoryAdapter.HistoryDetailListner{
            override fun onHistoryDetailClickListner(customModelHistoryItem: CustomModelHistoryItem?) {
                Toast.makeText(context,"Item Clicked",Toast.LENGTH_SHORT).show()
            }

        })

        mDataBinding.transactionsRecyclerView.apply {
            adapter = mTransactionHistoryAdapter
            layoutManager = LinearLayoutManager(context)
        }


//        transactionViewModel.text.observe(this, Observer {
//            text_dashboard.text = it
//        })
    }

    override fun onBackBtnClick(view: View) {
        (activity as MainActivity).navController.popBackStack(R.id.navigation_home,false)
    }

    override fun onSortBtnClick(view: View) {
    }
}