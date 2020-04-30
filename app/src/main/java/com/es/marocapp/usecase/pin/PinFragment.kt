package com.es.marocapp.usecase.pin

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentPinBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import kotlinx.android.synthetic.main.fragment_pin.*

class PinFragment : BaseFragment<FragmentPinBinding>() {

    private lateinit var pinViewModel: PinViewModel

    override fun setLayout(): Int {
        return R.layout.fragment_pin
    }

    override fun init(savedInstanceState: Bundle?) {
        pinViewModel = ViewModelProvider(this).get(PinViewModel::class.java)

        mDataBinding.apply {
            viewmodel = pinViewModel
        }

        (activity as MainActivity).setHomeToolbarVisibility(false)

        pinViewModel.text.observe(this, Observer {
            text_notifications.text = it
        })
    }
}