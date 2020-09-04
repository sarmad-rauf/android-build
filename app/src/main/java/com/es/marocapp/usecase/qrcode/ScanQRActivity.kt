package com.es.marocapp.usecase.qrcode

import android.R.id.message
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.budiyev.android.codescanner.*
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityScanQrBinding
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity.Companion.KEY_SCANNED_DATA
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity.Companion.SCAN_QR


class ScanQRActivity : BaseActivity<ActivityScanQrBinding>() {
    private lateinit var codeScanner: CodeScanner


    override fun init(savedInstanceState: Bundle?) {
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                //Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                val intent = Intent()
                intent.putExtra(KEY_SCANNED_DATA, it.text)
                setResult(SCAN_QR, intent)
                finish()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }


    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun setLayout(): Int {
       return R.layout.activity_scan_qr
    }

}