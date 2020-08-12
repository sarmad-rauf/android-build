package com.es.marocapp.usecase.cashinviacard.fragments

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentCashInViaCardWebviewBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.cashinviacard.ActivityCashInViaCard
import com.es.marocapp.usecase.cashinviacard.CashInViaCardViewModel
import com.es.marocapp.utils.Constants

class CashInViaCashWebViewFragment : BaseFragment<FragmentCashInViaCardWebviewBinding>(){

    private lateinit var mActivityViewModel: CashInViaCardViewModel

    var web_url = "https://www.google.com/"

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

        if(!Constants.CASH_IN_VIA_CARD_URL.isNullOrEmpty()){
            web_url = Constants.CASH_IN_VIA_CARD_URL.replace("loggedInMsisdn",Constants.CURRENT_USER_MSISDN)
            web_url = web_url.replace("superUserContext",ApiConstant.CONTEXT_BEFORE_LOGIN)
            web_url = web_url.replace("loggedInUserLang",LocaleManager.selectedLanguage)
        }
        mDataBinding.cashInViaCardWebView.loadUrl(web_url)

//        mActivityViewModel.popBackStackTo = R.id.cashInViaCardAmountFragment
        mActivityViewModel.popBackStackTo = -1

    }

}