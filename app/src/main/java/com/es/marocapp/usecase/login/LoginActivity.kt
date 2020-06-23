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

    companion object{
        const val KEY_REDIRECT_USER="key_redirect_user"
        const val KEY_REDIRECT_USER_SESSION_OUT="key_redirect_user_session_out"
        const val KEY_REDIRECT_USER_INVALID="key_redirect_user_invalid"
    }


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

        checkIfFromAPIRedirection()
    }

    private fun checkIfFromAPIRedirection() {
        if(intent!=null && intent.extras!=null && intent.hasExtra(KEY_REDIRECT_USER)){
            var redirectionType=intent.getStringExtra(KEY_REDIRECT_USER)

            if(redirectionType.equals(KEY_REDIRECT_USER_SESSION_OUT)){
                navController.navigate(R.id.action_loginFragment_to_signUpNumberFragment)
            }
           /* else if(redirectionType.equals(KEY_REDIRECT_USER_INVALID)){

            }*/
        }
    }

    private fun initListner() {
    }

    private fun subscribe() {

    }

}
