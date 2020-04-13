package com.ens.maroc.usecase.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.ens.maroc.R
import com.ens.maroc.databinding.FragmentLoginBindingImpl
import com.ens.maroc.databinding.FragmentVerifyNumberBinding
import com.ens.maroc.usecase.BaseFragment
import kotlinx.android.synthetic.main.layout_login_header.view.*

/**
 * A simple [Fragment] subclass.
 */
class VerifyNumberFragment : BaseFragment<FragmentVerifyNumberBinding>(), VerifyOTPClickListner {

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_verify_number
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@VerifyNumberFragment
        }

        mDataBinding.root.groupBack.visibility = View.VISIBLE
        mDataBinding.root.txtHeaderTitle.text = getString(R.string.verify_your_number)
        mDataBinding.root.txtBack.setOnClickListener{
            (activity as LoginActivity).navController.navigateUp()
        }

        mDataBinding.root.imgBackButton.setOnClickListener{
            (activity as LoginActivity).navController.navigateUp()
        }

    }

    override fun onOTPVerifyClick(view: View) {
        (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_setYourPinFragment)
    }

}
