package com.es.marocapp.usecase.cashinviacard.fragments

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentCashInViaCardWebviewBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.cashinviacard.ActivityCashInViaCard
import com.es.marocapp.usecase.cashinviacard.CashInViaCardViewModel

class CashInViaCashWebViewFragment : BaseFragment<FragmentCashInViaCardWebviewBinding>(){

    private lateinit var mActivityViewModel: CashInViaCardViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_cash_in_via_card_webview
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
        ViewModelProvider(activity as ActivityCashInViaCard).get(CashInViaCardViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        (activity as ActivityCashInViaCard).setHeaderTitle(LanguageData.getStringValue("CashInViaCardCaps").toString())

        mDataBinding.cashInViaCardWebView.loadUrl("https://www.google.com/")

        mActivityViewModel.popBackStackTo = R.id.cashInViaCardAmountFragment

    }

}