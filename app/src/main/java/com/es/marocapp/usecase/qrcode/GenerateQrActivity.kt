package com.es.marocapp.usecase.qrcode


import android.os.Bundle
import android.util.Log
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentGenerateQrBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.Logger
import com.es.marocapp.utils.Tools


class GenerateQrActivity:BaseActivity<FragmentGenerateQrBinding>(){
    override fun setLayout(): Int {
        return R.layout.fragment_generate_qr

    }

    override fun init(savedInstanceState: Bundle?) {

       var qrString= Tools.generateEMVcoString(Constants.CURRENT_USER_MSISDN, "")
        Logger.debugLog("QRString",qrString)
        mDataBinding.imgResult.setImageBitmap(Tools.generateQR(qrString))
        mDataBinding.imgBackButton.setOnClickListener {
            this@GenerateQrActivity.finish()
        }

        setStrings()
    }

    private fun setStrings() {
        mDataBinding.tvGenerateQRTitle.text = LanguageData.getStringValue("GenerateQR")
    }

}