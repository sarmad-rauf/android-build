package com.ens.maroc.usecase.login

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ens.maroc.R
import com.ens.maroc.databinding.ActivityLoginBinding
import com.ens.maroc.usecase.BaseActivity
import com.ens.maroc.usecase.splash.SplashActivityViewModel

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    lateinit var mActivityViewModel: LoginActivityViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    override fun setLayout(): Int {
        return R.layout.activity_login
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {

            viewmodel = mActivityViewModel

        }

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_login_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        subscribe()
        initListner()
    }

    private fun initListner() {
    }

    private fun subscribe() {

    }

}
