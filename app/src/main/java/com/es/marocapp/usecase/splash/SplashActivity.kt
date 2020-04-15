package com.es.marocapp.usecase.splash


import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.AcitivtySplashBinding
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.login.LoginActivity


class SplashActivity : BaseActivity<AcitivtySplashBinding>() {

    override fun setLayout(): Int {
        return R.layout.acitivty_splash
    }

    lateinit var mActivityViewModel: SplashActivityViewModel


    override fun init(savedInstanceState: Bundle?) {

        mActivityViewModel = ViewModelProvider(this).get(SplashActivityViewModel::class.java)

        mDataBinding.apply {

            viewmodel = mActivityViewModel

        }

        subscribe()

    }

    private fun subscribe() {
        val resultObserver = Observer<Boolean> {
            startNewActivityAndClear(this@SplashActivity, LoginActivity::class.java)
        }
        mActivityViewModel.mHandler.observe(this, resultObserver)
    }


}