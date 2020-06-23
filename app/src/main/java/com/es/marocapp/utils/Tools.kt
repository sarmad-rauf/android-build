package com.es.marocapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.fragment_generate_qr.*

object Tools {

    fun hasValue(value: String?): Boolean {
        try {
            if (value != null && !value.equals("null", ignoreCase = true) && !value.equals("", ignoreCase = true)) {
                return true
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    fun checkNetworkStatus(context: Context): Boolean {
        var isNetwork = false
        try {
            if (context != null) {
                val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = connMgr?.getActiveNetworkInfo()
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                        isNetwork = true
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // connected to mobile data
                        isNetwork = true
                    }
                } else {
                    // not connected to the internet
                    isNetwork = false
                }
            }
        } catch (e: Exception) {
        }
        return isNetwork
    }

     fun generateQR(texto: String): Bitmap? {
        val writer = QRCodeWriter()
        try {
            val bitMatrix: BitMatrix = writer.encode(texto, BarcodeFormat.QR_CODE, 512, 512)
            val width = 512
            val height = 512
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (bitMatrix.get(x, y)) bmp.setPixel(x, y, Color.BLACK) else bmp.setPixel(
                        x,
                        y,
                        Color.WHITE
                    )
                }
            }
            return bmp;
        } catch (e: WriterException) {
            Log.e("QR ERROR", e.toString())
            return null
        }
    }

    fun openDialerWithNumber(context: Context){
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + Constants.HELPLINE_NUMBER)
            context.startActivity(intent)
    }
}