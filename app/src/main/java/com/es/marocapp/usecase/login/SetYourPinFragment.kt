package com.es.marocapp.usecase.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentSetYourPinBinding
import com.es.marocapp.databinding.FragmentVerifyNumberBinding
import com.es.marocapp.model.responses.ActivateUserResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.utils.Constants
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
        mDataBinding.root.txtHeaderTitle.text = getString(R.string.set_your_password)

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

        subscribeObserver()
    }

    private fun subscribeObserver() {
        val mActivateUserObserver = Observer<ActivateUserResponse>{
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                Toast.makeText(activity as LoginActivity,"Success",Toast.LENGTH_SHORT).show()
                (activity as LoginActivity).navController.popBackStack(R.id.loginFragment,false)
            }
        }

        mActivityViewModel.getActivateUserResponseListner.observe(this@SetYourPinFragment,mActivateUserObserver)
    }

    override fun onPinOrSignUpClick(view: View) {
        //Without API Call Below Use Line
//        (activity as LoginActivity).navController.popBackStack(R.id.loginFragment,false)
        mActivityViewModel.requestForActivateUserApi(activity,mDataBinding.inputEnterPin.toString().trim())
    }



}
