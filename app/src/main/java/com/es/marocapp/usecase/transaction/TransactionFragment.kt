package com.es.marocapp.usecase.transaction

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentTransactionBinding
import com.es.marocapp.usecase.BaseFragment
import kotlinx.android.synthetic.main.fragment_transaction.*

class TransactionFragment : BaseFragment<FragmentTransactionBinding>() {

    private lateinit var transactionViewModel: TransactionViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_transaction
    }

    override fun init(savedInstanceState: Bundle?) {
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        mDataBinding.apply {
            viewmodel = transactionViewModel
        }


        transactionViewModel.text.observe(this, Observer {
            text_dashboard.text = it
        })
    }
}