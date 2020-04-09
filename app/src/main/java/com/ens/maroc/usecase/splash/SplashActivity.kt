package com.ens.maroc.usecase.splash



import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ens.maroc.R
import com.ens.maroc.databinding.AcitivtySplashBinding
import com.ens.maroc.usecase.BaseActivity
import com.ens.maroc.usecase.dashboard.MainActivity


class SplashActivity : BaseActivity<AcitivtySplashBinding>() {

    override fun setLayout(): Int {
        return R.layout.acitivty_splash
    }

    lateinit var mActivityViewModel: SplashActivityViewModel


    override fun init(savedInstanceState: Bundle?) {

        mActivityViewModel =  ViewModelProvider(this).get(SplashActivityViewModel::class.java)

        mDataBinding.apply {

            viewmodel = mActivityViewModel

        }

        subscribe()

    }

    private fun subscribe() {
        val resultObserver = object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {

                startNewActivityAndClear(this@SplashActivity, MainActivity::class.java)
            }
        }
        mActivityViewModel.mHandler.observe(this, resultObserver)
    }



}