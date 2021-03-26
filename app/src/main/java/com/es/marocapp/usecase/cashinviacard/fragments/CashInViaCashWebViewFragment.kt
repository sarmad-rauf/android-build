package com.es.marocapp.usecase.cashinviacard.fragments

import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentCashInViaCardWebviewBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.cashinviacard.ActivityCashInViaCard
import com.es.marocapp.usecase.cashinviacard.CashInViaCardViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.Logger


class CashInViaCashWebViewFragment : BaseFragment<FragmentCashInViaCardWebviewBinding>(){

    private lateinit var mActivityViewModel: CashInViaCardViewModel

    var web_url = ""

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
           /* web_url = Constants.CASH_IN_VIA_CARD_URL.replace("loggedInMsisdn",Constants.CURRENT_USER_MSISDN.replace("212","0"))
            web_url = web_url.replace("superUserContext",ApiConstant.CONTEXT_BEFORE_LOGIN)
            web_url = web_url.replace("loggedInUserLang",LocaleManager.selectedLanguage)*/

            web_url=Constants.CASH_IN_VIA_CARD_URL.plus("&msisdn=").plus(Constants.CURRENT_USER_MSISDN)
                .plus("&lang=").plus(LocaleManager.selectedLanguage)
                .plus("&name=").plus(Constants.CURRENT_USER_NAME)
                .plus("&email=").plus(Constants.CURRENT_USER_EMAIL)

        }

        mDataBinding.cashInViaCardWebView.getSettings().setJavaScriptEnabled(true)
        mDataBinding.cashInViaCardWebView.getSettings().setLoadWithOverviewMode(true)
        mDataBinding.cashInViaCardWebView.getSettings().setUseWideViewPort(true)
        mDataBinding.cashInViaCardWebView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                mDataBinding.progressBar.visibility= View.VISIBLE
                view.loadUrl(url)
                Logger.debugLog("urlLoaded","url ${url}")
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                mDataBinding.progressBar.visibility= View.GONE
            }
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError?
            ) {
                mDataBinding.progressBar.visibility= View.GONE
                handler.proceed() // Ignore SSL certificate errors
            }
        })

        mDataBinding.cashInViaCardWebView.loadUrl(web_url)
        Logger.debugLog("CashInViaCashURL",web_url)
      //  mDataBinding.cashInViaCardWebView.loadUrl(web_url)

//        mActivityViewModel.popBackStackTo = R.id.cashInViaCardAmountFragment
        mActivityViewModel.popBackStackTo = -1

    }

}