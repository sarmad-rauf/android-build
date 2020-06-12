package com.es.marocapp.usecase.qrcode


import android.os.Bundle
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentGenerateQrBinding
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.Tools
import kotlinx.android.synthetic.main.fragment_generate_qr.*


class GenerateQrActivity:BaseActivity<FragmentGenerateQrBinding>(){
    override fun setLayout(): Int {
        return R.layout.fragment_generate_qr

    }

    override fun init(savedInstanceState: Bundle?) {

        imgResult.setImageBitmap(Tools.generateQR(Constants.CURRENT_USER_MSISDN))
    }

}