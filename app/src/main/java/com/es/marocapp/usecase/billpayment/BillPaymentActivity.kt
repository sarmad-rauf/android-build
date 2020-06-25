package com.es.marocapp.usecase.billpayment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityBillPaymentBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.airtime.AirTimeViewModel
import kotlinx.android.synthetic.main.layout_activity_header.view.*

class BillPaymentActivity : BaseActivity<ActivityBillPaymentBinding>() {

    lateinit var mActivityViewModel: BillPaymentViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

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

        mDataBinding.root.activityHeaderBack.setOnClickListener {
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
        mDataBinding.headerBillPayment.rootView.activityHeaderTitle.text = title
    }

    fun setCompanyIconToolbarVisibility(isVisible : Boolean){
        if(isVisible){
            mDataBinding.headerBillPayment.rootView.headerCompanyIconContainer.visibility = View.VISIBLE
        }
        else{
            mDataBinding.headerBillPayment.rootView.headerCompanyIconContainer.visibility = View.GONE
        }
    }

    fun setHeaderVisibility(isVisible: Boolean) {
        if (isVisible) {
            mDataBinding.headerBillPayment.visibility = View.VISIBLE
        } else {
            mDataBinding.headerBillPayment.visibility = View.GONE
        }
    }

    fun setLetterIconVisible(isVisible: Boolean,letter : String){
        if(isVisible){
            mDataBinding.headerBillPayment.rootView.first_letter_icons.visibility = View.VISIBLE
            mDataBinding.headerBillPayment.rootView.first_letter_icons.text = letter
            mDataBinding.headerBillPayment.rootView.img_company_icons.visibility = View.GONE
        }else{
            mDataBinding.headerBillPayment.rootView.first_letter_icons.visibility = View.GONE
            mDataBinding.headerBillPayment.rootView.img_company_icons.visibility = View.VISIBLE
        }
    }

}
