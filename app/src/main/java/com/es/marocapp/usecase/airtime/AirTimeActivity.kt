package com.es.marocapp.usecase.airtime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityAirTimeBinding
import com.es.marocapp.databinding.ActivityCashServicesBinding
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.cashservices.CashServicesViewModel
import kotlinx.android.synthetic.main.layout_activity_header.view.*
import kotlinx.android.synthetic.main.layout_simple_header.view.*

class AirTimeActivity : BaseActivity<ActivityAirTimeBinding>() {

    lateinit var mActivityViewModel: AirTimeViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(this@AirTimeActivity).get(AirTimeViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_air_time_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setHeaderTitle(resources.getString(R.string.air_time))

        mDataBinding.root.activityHeaderBack.setOnClickListener {
            if (mActivityViewModel.popBackStackTo == -1) {
                this@AirTimeActivity.finish()
            } else {
                navController.popBackStack(mActivityViewModel.popBackStackTo, false)
            }
        }

        setCompanyIconToolbarVisibility(false)
    }

    override fun setLayout(): Int {
        return R.layout.activity_air_time
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
}
