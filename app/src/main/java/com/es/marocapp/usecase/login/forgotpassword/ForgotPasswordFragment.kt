package com.es.marocapp.usecase.login.forgotpassword

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentForgotPasswordBinding
import com.es.marocapp.model.responses.ForgotPasswordResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import kotlinx.android.synthetic.main.layout_login_header.view.*

class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>(), ForgotPasswordClickListner{

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_forgot_password
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as LoginActivity).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@ForgotPasswordFragment
        }

        mDataBinding.root.txtHeaderTitle.text = getString(R.string.forgot_pinn)

        subsribeObserver()

    }

    private fun subsribeObserver() {

        val mForgotPasswordListener = Observer<ForgotPasswordResponse>{
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                (activity as LoginActivity).navController.popBackStack(R.id.forgotPasswordFragment,false)
            }else{
                Toast.makeText(activity,"API Failed",Toast.LENGTH_SHORT).show()
            }
        }

        mActivityViewModel.getForgotPasswordResponseListner.observe(this@ForgotPasswordFragment,mForgotPasswordListener)
    }

    override fun onChangePasswordClickListner(view: View) {
        mActivityViewModel.requestForForgotPasswordAPI(activity,mDataBinding.inputForgotPassword.text.toString().trim(),mDataBinding.inputForgotOtp.text.toString().trim())
    }


}
