package com.es.marocapp.usecase.cashservices

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityCashServicesBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseActivity
import kotlinx.android.synthetic.main.layout_simple_header.view.*

class CashServicesActivity : BaseActivity<ActivityCashServicesBinding>() {

    lateinit var mActivityViewModel: CashServicesViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(this@CashServicesActivity).get(CashServicesViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_cash_services_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setHeaderTitle(LanguageData.getStringValue("CashService").toString())

        mDataBinding.root.simpleHeaderBack.setOnClickListener {
            if (mActivityViewModel.popBackStackTo == -1) {
                this@CashServicesActivity.finish()
            } else {
                navController.popBackStack(mActivityViewModel.popBackStackTo, false)
            }
        }
    }

    override fun setLayout(): Int {
        return R.layout.activity_cash_services
    }

    fun setHeaderTitle(title: String) {
        mDataBinding.headerCashServices.rootView.simpleHeaderTitle.text = title
    }

    fun setHeaderVisibility(isVisible: Boolean) {
        if (isVisible) {
            mDataBinding.headerCashServices.visibility = View.VISIBLE
        } else {
            mDataBinding.headerCashServices.visibility = View.GONE
        }
    }
}
