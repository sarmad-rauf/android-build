package com.ens.maroc.usecase.login.forgotpassword

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.ens.maroc.R
import com.ens.maroc.databinding.FragmentForgotPasswordBinding
import com.ens.maroc.usecase.BaseFragment
import com.ens.maroc.usecase.login.LoginActivity
import com.ens.maroc.usecase.login.LoginActivityViewModel
import kotlinx.android.synthetic.main.layout_login_header.view.*

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
