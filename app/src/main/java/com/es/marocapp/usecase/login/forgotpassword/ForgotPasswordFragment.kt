package com.es.marocapp.usecase.login.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentForgotPasswordBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel

class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>(), ForgotPasswordClickListner{

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_forgot_password
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@ForgotPasswordFragment
        }

        mDataBinding.root.txtHeaderTitle.text = getString(R.string.forgot_pinn)

    }

    override fun onBackButtonClick(view: View) {
        (activity as LoginActivity).navController.navigateUp()
    }

    override fun onNextButtonClick(view: View) {
        (activity as LoginActivity).navController.navigate(R.id.action_forgotPasswordFragment_to_verifyNumberFragment)
    }


}
