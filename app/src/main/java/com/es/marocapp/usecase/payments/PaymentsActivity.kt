package com.es.marocapp.usecase.payments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityPaymentsBinding
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel

class PaymentsActivity :BaseActivity<ActivityPaymentsBinding>() {

    lateinit var mActivityViewModel: PaymentsViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    override fun init(savedInstanceState: Bundle?) {

        mActivityViewModel = ViewModelProvider(this).get(PaymentsViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel

        }

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_payment_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

    }

    override fun setLayout(): Int {
        return R.layout.activity_payments
    }

    fun setCompanyIconToolbarVisibility(isVisible : Boolean){
        if(isVisible){
            mDataBinding.headerBillPayment.headerCompanyIconContainer.visibility = View.VISIBLE
        }
        else{
            mDataBinding.headerBillPayment.headerCompanyIconContainer.visibility = View.GONE
        }
    }

    fun setToolabarVisibility(isVisible : Boolean){
        if(isVisible){
            mDataBinding.headerBillPayment.root.visibility = View.VISIBLE
        }
        else{
            mDataBinding.headerBillPayment.root.visibility = View.GONE
        }
    }
}
