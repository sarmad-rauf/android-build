package com.es.marocapp.usecase.login.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentVerifyNumberBinding
import com.es.marocapp.model.responses.RegisterUserResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.layout_login_header.view.*

/**
 * A simple [Fragment] subclass.
 */
class VerifyNumberFragment : BaseFragment<FragmentVerifyNumberBinding>(),
    VerifyOTPClickListner {

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_verify_number
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as LoginActivity).get(
            LoginActivityViewModel::class.java)

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

        subscribeObserver()

    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@VerifyNumberFragment, Observer {
            DialogUtils.showErrorDialoge(activity as LoginActivity,it)
        })

        val mRegisterUserResonseObserver = Observer<RegisterUserResponse>{
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_setYourPinFragment)
            }else{
                DialogUtils.showErrorDialoge(activity as LoginActivity,it.description)
            }
        }

        mActivityViewModel.getRegisterUserResponseListner.observe(this,mRegisterUserResonseObserver)
    }

    override fun onOTPVerifyClick(view: View) {
        mActivityViewModel.requestForRegisterUserApi(activity,Constants.CURRENT_NUMBER_DEVICE_ID,mDataBinding.inputVerifyOtp.text.toString().trim())
        //For Without API Calling Uncomment Below Line
//        (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_setYourPinFragment)
    }

}