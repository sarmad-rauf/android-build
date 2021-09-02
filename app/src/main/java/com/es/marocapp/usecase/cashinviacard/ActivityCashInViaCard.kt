package com.es.marocapp.usecase.cashinviacard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityCashInViaCardBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseActivity

class ActivityCashInViaCard : BaseActivity<ActivityCashInViaCardBinding>() {

    lateinit var mActivityViewModel: CashInViaCardViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    override fun setLayout(): Int {
        return R.layout.activity_cash_in_via_card
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(this@ActivityCashInViaCard).get(CashInViaCardViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_cash_in_via_card_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setHeaderTitle(LanguageData.getStringValue("CashInViaCard").toString())

        mDataBinding.headerCashInViaCard.simpleHeaderBack.setOnClickListener {
            if (mActivityViewModel.popBackStackTo == -1) {
                this@ActivityCashInViaCard.finish()
            } else {
                navController.popBackStack(mActivityViewModel.popBackStackTo, false)
            }
        }
    }

    fun setHeaderTitle(title: String) {
        mDataBinding.headerCashInViaCard.simpleHeaderTitle.text = title
    }

}
