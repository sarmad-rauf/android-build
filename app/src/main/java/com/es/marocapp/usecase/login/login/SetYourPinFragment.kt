package com.es.marocapp.usecase.login.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentSetYourPinBinding
import com.es.marocapp.model.responses.ActivateUserResponse
import com.es.marocapp.model.responses.CreateCredentialResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import kotlinx.android.synthetic.main.layout_login_header.view.*

/**
 * A simple [Fragment] subclass.
 */
class SetYourPinFragment : BaseFragment<FragmentSetYourPinBinding>(),
    EnterPinClickListner {

    lateinit var mActivityViewModel: LoginActivityViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_set_your_pin
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as LoginActivity).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@SetYourPinFragment
        }

        mDataBinding.root.groupBack.visibility = View.VISIBLE
        mDataBinding.root.txtHeaderTitle.text = getString(R.string.set_your_password)

        if(mActivityViewModel.isSignUpFlow.get()!!){
            mDataBinding.btnPinChange.text = getString(R.string.sign_up)
        }else{
            if(mActivityViewModel.activeUserWithoutPassword.get()!!){
                mDataBinding.btnPinChange.text = getString(R.string.set_passwordd)
            }else{
                mDataBinding.btnPinChange.text = getString(R.string.change_pin)
            }
        }

        mDataBinding.root.txtBack.setOnClickListener{
            (activity as LoginActivity).navController.navigateUp()
        }

        mDataBinding.root.imgBackButton.setOnClickListener{
            (activity as LoginActivity).navController.navigateUp()
        }

        subscribeObserver()
    }

    private fun subscribeObserver() {
        val mActivateUserObserver = Observer<ActivateUserResponse>{
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                (activity as LoginActivity).navController.popBackStack(R.id.loginFragment,false)
            }else{
                Toast.makeText(activity,"API Failed",Toast.LENGTH_SHORT).show()
            }
        }

        val mCreateCredentialObserver = Observer<CreateCredentialResponse>{
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                (activity as LoginActivity).navController.popBackStack(R.id.loginFragment,false)
            }else{
                Toast.makeText(activity,"API Failed",Toast.LENGTH_SHORT).show()
            }
        }

        mActivityViewModel.getActivateUserResponseListner.observe(this@SetYourPinFragment,mActivateUserObserver)
        mActivityViewModel.getCreateCredentialsResponseListner.observe(this@SetYourPinFragment,mCreateCredentialObserver)
    }

    override fun onPinOrSignUpClick(view: View) {
        //Without API Call Below Use Line
//        (activity as LoginActivity).navController.popBackStack(R.id.loginFragment,false)
        if(mActivityViewModel.isSignUpFlow.get()!! || mActivityViewModel.activeUserWithoutPassword.get()!!){
            mActivityViewModel.requestForActivateUserApi(activity,mDataBinding.inputEnterPin.text.toString().trim())
        }else{
            mActivityViewModel.requestForCreateCredentialsAPI(activity,mDataBinding.inputEnterPin.text.toString().trim())
        }
    }



}
