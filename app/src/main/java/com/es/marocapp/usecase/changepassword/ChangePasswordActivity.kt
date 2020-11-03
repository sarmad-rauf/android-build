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
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.consumerregistration.ConsumerRegistrationActivity
import com.es.marocapp.utils.DialogUtils

class ChangePasswordActivity : BaseActivity<FragmentChangepasswordBinding>(),
    ChangePasswordClickListener {

    private lateinit var pinViewModel: ChangePasswordViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_changepassword
    }

    override fun init(savedInstanceState: Bundle?) {
        pinViewModel = ViewModelProvider(this@ChangePasswordActivity).get(ChangePasswordViewModel::class.java)

        mDataBinding.apply {
            viewmodel = pinViewModel
            listner = this@ChangePasswordActivity
        }

        mDataBinding.imgBackButton.setOnClickListener {
            (this@ChangePasswordActivity).finish()
        }

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
        pinViewModel.errorText.observe(this@ChangePasswordActivity, Observer {
            DialogUtils.showErrorDialoge(this@ChangePasswordActivity, it)
        })

        pinViewModel.getChangePassResponseListner.observe(this@ChangePasswordActivity, Observer {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                DialogUtils.showSuccessDialog(this,it.description,object : DialogUtils.OnConfirmationDialogClickListner{
                    override fun onDialogYesClickListner() {
                        (this@ChangePasswordActivity).finish()
                    }

                    override fun onDialogNoClickListner() {

                    }

                })

            } else {
                DialogUtils.showErrorDialoge(this@ChangePasswordActivity, it.description)
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
                    this@ChangePasswordActivity, mDataBinding.inputOldPassword.text.toString().trim(),
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
