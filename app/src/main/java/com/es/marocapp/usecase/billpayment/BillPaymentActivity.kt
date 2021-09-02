package com.es.marocapp.usecase.billpayment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityBillPaymentBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseActivity


class BillPaymentActivity : BaseActivity<ActivityBillPaymentBinding>() {

    lateinit var mActivityViewModel: BillPaymentViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment
    var dummy="dummy"

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(this@BillPaymentActivity).get(BillPaymentViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_bill_payment_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setHeaderTitle(LanguageData.getStringValue("BillPayment").toString())

        mDataBinding.headerBillPayment.activityHeaderBack.setOnClickListener {
            if (mActivityViewModel.popBackStackTo == -1) {
                this@BillPaymentActivity.finish()
            } else {
                navController.popBackStack(mActivityViewModel.popBackStackTo, false)
            }
        }
        setCompanyIconToolbarVisibility(false)
    }

    override fun setLayout(): Int {
        return R.layout.activity_bill_payment
    }

    fun setHeaderTitle(title: String) {
        mDataBinding.headerBillPayment.activityHeaderTitle.text = title
    }

    fun setCompanyIconToolbarVisibility(isVisible : Boolean){
        if(isVisible){
            mDataBinding.headerBillPayment.headerCompanyIconContainer.visibility = View.VISIBLE
        }
        else{
            mDataBinding.headerBillPayment.headerCompanyIconContainer.visibility = View.GONE
        }
    }

    fun setHeaderVisibility(isVisible: Boolean) {
        if (isVisible) {
            mDataBinding.headerBillPayment.root.visibility = View.VISIBLE
        } else {
            mDataBinding.headerBillPayment.root.visibility = View.GONE
        }
    }

    fun setLetterIconVisible(isVisible: Boolean,letter : String){
        if(isVisible){
            mDataBinding.headerBillPayment.firstLetterIcons.visibility = View.VISIBLE
            mDataBinding.headerBillPayment.firstLetterIcons.text = letter
            mDataBinding.headerBillPayment.imgCompanyIcons.visibility = View.GONE
        }else{
            mDataBinding.headerBillPayment.firstLetterIcons.visibility = View.GONE
            mDataBinding.headerBillPayment.imgCompanyIcons.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (mActivityViewModel.popBackStackTo == -1) {
            this@BillPaymentActivity.finish()
        } else {
            super.onBackPressed()
        }
    }

}
