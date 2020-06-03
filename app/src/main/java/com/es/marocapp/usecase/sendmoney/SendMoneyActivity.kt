package com.es.marocapp.usecase.sendmoney

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivitySendMoneyBinding
import com.es.marocapp.usecase.BaseActivity
import kotlinx.android.synthetic.main.layout_simple_header.view.*

class SendMoneyActivity : BaseActivity<ActivitySendMoneyBinding>() {

    lateinit var mActivityViewModel: SendMoneyViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this@SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_send_money_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setHeaderTitle(resources.getString(R.string.send_money))
    }

    override fun setLayout(): Int {
        return R.layout.activity_send_money
    }

    fun setHeaderTitle(title : String){
        mDataBinding.headerBillPayment.rootView.simpleHeaderTitle.text = title
    }

    fun setHeaderVisibility(isVisible: Boolean){
        if(isVisible){
            mDataBinding.headerBillPayment.visibility = View.VISIBLE
        }else{
            mDataBinding.headerBillPayment.visibility = View.GONE
        }
    }
}
