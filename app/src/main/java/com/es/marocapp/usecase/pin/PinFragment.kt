package com.es.marocapp.usecase.pin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentPinBinding
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.fragment_pin.*

class PinFragment : BaseFragment<FragmentPinBinding>(), ChangePasswordClickListener {

    private lateinit var pinViewModel: PinViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_pin
    }

    override fun init(savedInstanceState: Bundle?) {
        pinViewModel = ViewModelProvider(activity as MainActivity).get(PinViewModel::class.java)

        mDataBinding.apply {
            viewmodel = pinViewModel
            listner = this@PinFragment
        }

        (activity as MainActivity).setHomeToolbarVisibility(false)

//        pinViewModel.text.observe(this, Observer {
//            text_notifications.text = it
//        })

        subscribeObserver()
    }

    private fun subscribeObserver() {
        pinViewModel.errorText.observe(this@PinFragment, Observer {
            DialogUtils.showErrorDialoge(activity as MainActivity,it)
        })

        pinViewModel.getChangePassResponseListner.observe(this@PinFragment, Observer {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                Toast.makeText(activity, "Password Changed Successfully", Toast.LENGTH_SHORT).show()
            } else {
                DialogUtils.showErrorDialoge(activity as MainActivity,it.description)
            }
        })
    }

    override fun onChangePasswordClickListner(view: View) {
        pinViewModel.requestForCahngePasswordAPI(
            activity, mDataBinding.inputOldPassword.text.toString().trim(),
            mDataBinding.inputNewPassword.text.toString().trim()
        )
    }
}