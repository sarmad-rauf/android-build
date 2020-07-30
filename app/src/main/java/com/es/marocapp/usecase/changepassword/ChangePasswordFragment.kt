package com.es.marocapp.usecase.changepassword

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentChangepasswordBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.DialogUtils

class ChangePasswordFragment : BaseFragment<FragmentChangepasswordBinding>(), ChangePasswordClickListener {

    private lateinit var pinViewModel: ChangePasswordViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_changepassword
    }

    override fun init(savedInstanceState: Bundle?) {
        pinViewModel = ViewModelProvider(activity as MainActivity).get(ChangePasswordViewModel::class.java)

        mDataBinding.apply {
            viewmodel = pinViewModel
            listner = this@ChangePasswordFragment
        }

        mDataBinding.imgBackButton.setOnClickListener {
            (activity as MainActivity).navController.popBackStack(R.id.navigation_home,false)
        }

        (activity as MainActivity).setHomeToolbarVisibility(false)
        (activity as MainActivity).isDirectCallForTransaction = false
        (activity as MainActivity).isTransactionFragmentNotVisible = true


//        pinViewModel.text.observe(this, Observer {
//            text_notifications.text = it
//        })

        subscribeObserver()
        setStrings()
    }

    private fun setStrings() {
        mDataBinding.tvChangeTitle.text = LanguageData.getStringValue("ChangePassword")
        mDataBinding.inputLayoutOldPassword.hint = LanguageData.getStringValue("EnterOldPassword")
        mDataBinding.inputLayoutNewPassword.hint = LanguageData.getStringValue("EnterNewPassword")
        mDataBinding.inputLayoutConfirmPassword.hint = LanguageData.getStringValue("ConfirmNewPassword")

        mDataBinding.btnChangePassword.text = LanguageData.getStringValue("BtnTitle_Change")
    }

    private fun subscribeObserver() {
        pinViewModel.errorText.observe(this@ChangePasswordFragment, Observer {
            DialogUtils.showErrorDialoge(activity as MainActivity, it)
        })

        pinViewModel.getChangePassResponseListner.observe(this@ChangePasswordFragment, Observer {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                Toast.makeText(activity, "Password Changed Successfully", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).navController.navigate(R.id.navigation_home)
            } else {
                DialogUtils.showErrorDialoge(activity as MainActivity, it.description)
            }
        })
    }

    override fun onChangePasswordClickListner(view: View) {
        if (isValidForAll()) {
            if (mDataBinding.inputNewPassword.text.toString().trim()
                    .equals(mDataBinding.inputConfirmPassword.text.toString().trim())
            ) {
                mDataBinding.inputLayoutConfirmPassword.error = ""
                mDataBinding.inputLayoutConfirmPassword.isErrorEnabled = false
                pinViewModel.requestForChangePasswordAPI(
                    activity, mDataBinding.inputOldPassword.text.toString().trim(),
                    mDataBinding.inputNewPassword.text.toString().trim()
                )
            } else {
                mDataBinding.inputLayoutConfirmPassword.error = LanguageData.getStringValue("PasswordAndConfirmPasswordDoesntMatch")
                mDataBinding.inputLayoutConfirmPassword.isErrorEnabled = true
            }
        }
    }

    private fun isValidForAll(): Boolean {

        var isValidForAll = true

        if (mDataBinding.inputOldPassword.text.isNullOrEmpty()) {
            isValidForAll = false
            mDataBinding.inputLayoutOldPassword.error = LanguageData.getStringValue("PleaseEnterValidPassword")
            mDataBinding.inputLayoutOldPassword.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutOldPassword.error = ""
            mDataBinding.inputLayoutOldPassword.isErrorEnabled = false
        }

        if (mDataBinding.inputNewPassword.text.isNullOrEmpty()) {
            isValidForAll = false
            mDataBinding.inputLayoutNewPassword.error = LanguageData.getStringValue("PleaseEnterValidPassword")
            mDataBinding.inputLayoutNewPassword.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutNewPassword.error = ""
            mDataBinding.inputLayoutNewPassword.isErrorEnabled = false
        }

        if (mDataBinding.inputConfirmPassword.text.isNullOrEmpty()) {
            isValidForAll = false
            mDataBinding.inputLayoutConfirmPassword.error = LanguageData.getStringValue("PleaseEnterValidPassword")
            mDataBinding.inputLayoutConfirmPassword.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutConfirmPassword.error = ""
            mDataBinding.inputLayoutConfirmPassword.isErrorEnabled = false
        }

        return isValidForAll
    }
}