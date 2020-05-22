package com.es.marocapp.usecase.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentVerifyNumberBinding
import com.es.marocapp.model.responses.GetOtpForRegistrationResponse
import com.es.marocapp.model.responses.RegisterUserResponse
import com.es.marocapp.usecase.BaseFragment
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

        subscribeObserver()

    }

    private fun subscribeObserver() {
        val mRegisterUserResonseObserver = Observer<RegisterUserResponse>{

        }

        mActivityViewModel.getRegisterUserResponseListner.observe(this,mRegisterUserResonseObserver)
    }

    override fun onOTPVerifyClick(view: View) {
        mActivityViewModel.requestForRegisterUserApi(activity,"John","Smith","12345688","1993-08-10","male",
        "Street 11","abc@gmail.com","11111")
        //For Without API Calling Uncomment Below Line
//        (activity as LoginActivity).navController.navigate(R.id.action_verifyNumberFragment_to_setYourPinFragment)
    }

}
