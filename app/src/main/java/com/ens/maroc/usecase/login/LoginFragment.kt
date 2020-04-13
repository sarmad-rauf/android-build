package com.ens.maroc.usecase.login


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

import com.ens.maroc.R
import com.ens.maroc.databinding.FragmentLoginBinding
import com.ens.maroc.usecase.BaseFragment
import com.ens.maroc.usecase.dashboard.MainActivity
import kotlinx.android.synthetic.main.layout_login_header.view.*


class LoginFragment : BaseFragment<FragmentLoginBinding>(), AdapterView.OnItemSelectedListener,LoginClickListener {

    lateinit var mActivityViewModel: LoginActivityViewModel
    lateinit var mActivity : LoginActivity

    override fun setLayout(): Int {
        return R.layout.fragment_login
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@LoginFragment
        }

        mDataBinding.root.languageSpinner.visibility = View.VISIBLE
        mDataBinding.root.languageSpinner.onItemSelectedListener = this

        //initListner()

        mActivity = activity as LoginActivity

    }

    private fun initListner() {
        mDataBinding.txtSignUp.setOnClickListener{
            mActivityViewModel.isSignUpFlow.set(true)
            mActivity.navController.navigate(R.id.action_loginFragment_to_signUpNumberFragment)
        }

        mDataBinding.txtForgotPin.setOnClickListener{
            mActivityViewModel.isSignUpFlow.set(false)
            mActivity.navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment)

        }

        mDataBinding.btnLogin.setOnClickListener{
            startActivity(Intent(activity,MainActivity::class.java))
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val mySpinnerText = parent?.getChildAt(0)as TextView?
        mySpinnerText?.setTextColor(Color.WHITE)
    }

    override fun onLoginButtonClick(view: View) {
        startActivity(Intent(activity,MainActivity::class.java))
    }

    override fun onForgotPinClick(view: View) {
        mActivityViewModel.isSignUpFlow.set(false)
        mActivity.navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }

    override fun onSignUpClick(view: View) {
        mActivityViewModel.isSignUpFlow.set(true)
        mActivity.navController.navigate(R.id.action_loginFragment_to_signUpNumberFragment)
    }


}
