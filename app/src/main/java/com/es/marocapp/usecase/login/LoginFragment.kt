package com.es.marocapp.usecase.login


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.es.marocapp.R
import com.es.marocapp.adapter.LanguageCustomSpinnerAdapter
import com.es.marocapp.databinding.FragmentLoginBinding
import com.es.marocapp.model.responses.GetAccountHolderInformationResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import kotlinx.android.synthetic.main.layout_login_header.view.*


class LoginFragment : BaseFragment<FragmentLoginBinding>(), AdapterView.OnItemSelectedListener,LoginClickListener {

    lateinit var mActivityViewModel: LoginActivityViewModel
    lateinit var mActivity : LoginActivity
    lateinit var mLanguageSpinnerAdapter : LanguageCustomSpinnerAdapter

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

        mActivity = activity as LoginActivity

        val languageItems = arrayOf("English", "Arabic", "Spanish", "Urdu")
        mLanguageSpinnerAdapter = LanguageCustomSpinnerAdapter(activity as LoginActivity,languageItems)
        mDataBinding.root.languageSpinner.apply {
            adapter = mLanguageSpinnerAdapter
        }

        subscribe()

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onLoginButtonClick(view: View) {
        //For Proper Flow un Comment all this section
        if(mDataBinding.inputPhoneNumber.text.toString() == "" || mDataBinding.inputPhoneNumber.text.length<12){
            mDataBinding.inputLayoutPhoneNumber.error = "Please Enter Valid Mobile Number"
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutPhoneNumber.error = ""
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

            var userMsisdn = mDataBinding.inputPhoneNumber.text.toString()
            mActivityViewModel.requestForGetAccountHolderInformationApi(context,userMsisdn)
        }

        //For Checking Flow or New Screen UnComment This Line
//        (activity as LoginActivity).startNewActivityAndClear(activity as LoginActivity,MainActivity::class.java)
    }

    override fun onForgotPinClick(view: View) {
        mActivityViewModel.isSignUpFlow.set(false)
        mActivity.navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }

    override fun onSignUpClick(view: View) {
//        mActivityViewModel.isSignUpFlow.set(true)
//        mActivity.navController.navigate(R.id.action_loginFragment_to_signUpNumberFragment)
    }

    fun subscribe(){
        val mAccountHolderInfoResonseObserver = Observer<GetAccountHolderInformationResponse>{
            if(it.responseCode == ApiConstant.API_SUCCESS){
                (activity as LoginActivity).startNewActivityAndClear(activity as LoginActivity, MainActivity::class.java)
            }else{
                mActivityViewModel.isSignUpFlow.set(true)
                Log.d("Value",mActivityViewModel.isSignUpFlow.toString())
                mActivity.navController.navigate(R.id.action_loginFragment_to_signUpDetailFragment)
            }
        }

        mActivityViewModel.getAccountHolderInformationResponseListner.observe(this,mAccountHolderInfoResonseObserver)
    }

}
