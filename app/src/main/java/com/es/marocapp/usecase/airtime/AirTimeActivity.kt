package com.es.marocapp.usecase.airtime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityAirTimeBinding
import com.es.marocapp.databinding.ActivityCashServicesBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.cashservices.CashServicesViewModel
import kotlinx.android.synthetic.main.layout_activity_header.view.*
import kotlinx.android.synthetic.main.layout_simple_header.view.*

class AirTimeActivity : BaseActivity<ActivityAirTimeBinding>() {

    lateinit var mActivityViewModel: AirTimeViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    lateinit var navGraph: NavGraph

    var isQuickRechargeUseCase = false
    var quickRechargeAmount = ""

    override fun setLayout(): Int {
        return R.layout.activity_air_time
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(this@AirTimeActivity).get(AirTimeViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        isQuickRechargeUseCase = intent.getBooleanExtra("isQuickRechargeCase",false)
        if(isQuickRechargeUseCase){
            quickRechargeAmount = intent.getStringExtra("quickRechargeAmount")
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_air_time_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navGraph = navController.navInflater.inflate(R.navigation.air_time_nav_graph)

        setHeaderTitle(LanguageData.getStringValue("AirTime").toString())

        mDataBinding.root.activityHeaderBack.setOnClickListener {
            if (mActivityViewModel.popBackStackTo == -1) {
                this@AirTimeActivity.finish()
            } else {
                navController.popBackStack(mActivityViewModel.popBackStackTo, false)
            }
        }

        setCompanyIconToolbarVisibility(false)
        setFragmentToShow()
    }
    fun setFragmentToShow(){
        if(isQuickRechargeUseCase){
            mActivityViewModel.isQuickRechargeUseCase.set(true)
            mActivityViewModel.isRechargeFixeUseCase.set(false)
            mActivityViewModel.isRechargeMobileUseCase.set(false)
            mActivityViewModel.airTimeSelected.set(LanguageData.getStringValue("QuickRecharge"))
            mActivityViewModel.airTimeAmountSelected.set(quickRechargeAmount)
            navGraph.startDestination = R.id.airTimeConfirmationFragment
        }else{
            navGraph.startDestination = R.id.airTimeMainFragment
        }

        navController.setGraph(navGraph)
    }

    fun setHeaderTitle(title: String) {
        mDataBinding.headerAirTime.rootView.activityHeaderTitle.text = title
    }

    fun setCompanyIconToolbarVisibility(isVisible : Boolean){
        if(isVisible){
            mDataBinding.headerAirTime.rootView.headerCompanyIconContainer.visibility = View.VISIBLE
        }
        else{
            mDataBinding.headerAirTime.rootView.headerCompanyIconContainer.visibility = View.GONE
        }
    }

    fun setHeaderVisibility(isVisible: Boolean) {
        if (isVisible) {
            mDataBinding.headerAirTime.visibility = View.VISIBLE
        } else {
            mDataBinding.headerAirTime.visibility = View.GONE
        }
    }

    fun setVisibilityAndTextToImage(amount : String){
        mDataBinding.headerAirTime.rootView.img_company_icons.visibility = View.GONE
        mDataBinding.headerAirTime.rootView.first_letter_icons.visibility = View.VISIBLE

        mDataBinding.headerAirTime.rootView.first_letter_icons.text = amount
    }
}
