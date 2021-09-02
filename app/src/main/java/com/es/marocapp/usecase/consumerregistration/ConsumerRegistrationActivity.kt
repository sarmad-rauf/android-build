package com.es.marocapp.usecase.consumerregistration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityConsumerRegistrationBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseActivity

class ConsumerRegistrationActivity : BaseActivity<ActivityConsumerRegistrationBinding>() {
    lateinit var mActivityViewModel: ConsumerRegistrationViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this@ConsumerRegistrationActivity).get(ConsumerRegistrationViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_consumer_registration_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        mDataBinding.headerConsumerRegistration.simpleHeaderBack.setOnClickListener {
            if (mActivityViewModel.popBackStackTo == -1) {
                this@ConsumerRegistrationActivity.finish()
            } else {
                navController.popBackStack(mActivityViewModel.popBackStackTo, false)
            }
        }

        setHeaderTitle(LanguageData.getStringValue("ConsumerRegistration").toString())

    }

    override fun setLayout(): Int {
       return R.layout.activity_consumer_registration
    }

    fun setHeaderTitle(title : String){
        mDataBinding.headerConsumerRegistration.simpleHeaderTitle.text = title
    }

    fun setHeaderVisibility(isVisible: Boolean){
        if(isVisible){
            mDataBinding.headerConsumerRegistration.root.visibility = View.VISIBLE
        }else{
            mDataBinding.headerConsumerRegistration.root.visibility = View.GONE
        }
    }
}
