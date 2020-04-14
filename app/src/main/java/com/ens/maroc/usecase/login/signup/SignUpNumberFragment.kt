package com.ens.maroc.usecase.login.signup


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.ens.maroc.R
import com.ens.maroc.databinding.FragmentSignUpNumberBinding
import com.ens.maroc.databinding.FragmentVerifyNumberBinding
import com.ens.maroc.usecase.BaseFragment
import com.ens.maroc.usecase.login.LoginActivity
import com.ens.maroc.usecase.login.LoginActivityViewModel
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
