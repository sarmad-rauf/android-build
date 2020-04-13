package com.ens.maroc.usecase.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.ens.maroc.R
import com.ens.maroc.databinding.FragmentSetYourPinBinding
import com.ens.maroc.databinding.FragmentVerifyNumberBinding
import com.ens.maroc.usecase.BaseFragment
import kotlinx.android.synthetic.main.layout_login_header.view.*

/**
 * A simple [Fragment] subclass.
 */
class SetYourPinFragment : BaseFragment<FragmentSetYourPinBinding>(), EnterPinClickListner {

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_set_your_pin
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@SetYourPinFragment
        }

        mDataBinding.root.groupBack.visibility = View.VISIBLE
        mDataBinding.root.txtHeaderTitle.text = getString(R.string.set_your_pin)

        if(mActivityViewModel.isSignUpFlow.get()!!){
            mDataBinding.btnPinChange.text = getString(R.string.sign_up)
        }else{
            mDataBinding.btnPinChange.text = getString(R.string.change_pin)
        }

        mDataBinding.root.txtBack.setOnClickListener{
            (activity as LoginActivity).navController.navigateUp()
        }

        mDataBinding.root.imgBackButton.setOnClickListener{
            (activity as LoginActivity).navController.navigateUp()
        }

    }

    override fun onPinOrSignUpClick(view: View) {
        (activity as LoginActivity).navController.popBackStack(R.id.loginFragment,false)

    }


}
