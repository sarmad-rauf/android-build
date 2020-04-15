package com.es.marocapp.usecase.login.signup


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentSignUpNumberBinding
import com.es.marocapp.databinding.FragmentVerifyNumberBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import kotlinx.android.synthetic.main.layout_login_header.view.*

/**
 * A simple [Fragment] subclass.
 */
class SignUpNumberFragment : BaseFragment<FragmentSignUpNumberBinding>(),SignUpClickListner {

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_sign_up_number
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@SignUpNumberFragment
        }

        mDataBinding.root.txtHeaderTitle.text = getString(R.string.create_your_account)

    }


    override fun onNextButtonClick(view: View) {
        (activity as LoginActivity).navController.navigate(R.id.action_signUpNumberFragment_to_signUpDetailFragment)
    }

    override fun onBackButtonClick(view: View) {
        (activity as LoginActivity).navController.navigateUp()
    }

    override fun onCalenderCalenderClick(view: View) {

    }

    override fun onGenderSelectionClick(view: View) {

    }

}
