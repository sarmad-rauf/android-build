package com.es.marocapp.usecase.splash


import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.AcitivtySplashBinding
import com.es.marocapp.model.responses.GetPreLoginDataResponse
import com.es.marocapp.network.ApiConstant
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

//        mActivityViewModel.requestForGetPreLoginDataApi(this@SplashActivity)

        subscribe()

    }

    private fun subscribe() {
        val resultObserver = Observer<Boolean> {
            startNewActivityAndClear(this@SplashActivity, LoginActivity::class.java)
        }

        val preLoginDataObserver = Observer<GetPreLoginDataResponse> {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS, true)){
                startNewActivityAndClear(this@SplashActivity, LoginActivity::class.java)
            }else{
                startNewActivityAndClear(this@SplashActivity, LoginActivity::class.java)
            }
            Toast.makeText(this@SplashActivity,"Success",Toast.LENGTH_SHORT).show()

        }

        val errorText = Observer<String>{
//            startNewActivityAndClear(this@SplashActivity, LoginActivity::class.java)
            Toast.makeText(this@SplashActivity,"Error",Toast.LENGTH_SHORT).show()

        }

        mActivityViewModel.mHandler.observe(this, resultObserver)
        mActivityViewModel.preLoginDataResponseListener.observe(this, preLoginDataObserver)
        mActivityViewModel.errorText.observe(this, errorText)
    }


}