package com.es.marocapp.usecase.login

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityLoginBinding
import com.es.marocapp.usecase.BaseActivity

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
