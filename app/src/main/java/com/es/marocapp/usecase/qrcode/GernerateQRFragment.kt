package com.es.marocapp.usecase.qrcode

import android.os.Bundle
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentGenerateQrBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.Tools
import kotlinx.android.synthetic.main.fragment_generate_qr.*

class GernerateQRFragment : BaseFragment<FragmentGenerateQrBinding>(){

    override fun setLayout(): Int {
        return R.layout.fragment_generate_qr

    }

    override fun init(savedInstanceState: Bundle?) {

        imgResult.setImageBitmap(Tools.generateQR(Constants.CURRENT_USER_MSISDN))
        mDataBinding.imgBackButton.setOnClickListener {
            (activity as MainActivity).navController.navigateUp()
        }

        (activity as MainActivity).setHomeToolbarVisibility(false)

        (activity as MainActivity).isDirectCallForTransaction = false
        setStrings()
    }

    private fun setStrings() {
        mDataBinding.tvGenerateQRTitle.text = LanguageData.getStringValue("GenerateQR")
    }
}