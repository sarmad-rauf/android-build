package com.es.marocapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import com.es.marocapp.security.EncryptionUtils
import com.es.marocapp.utils.PrefUtils.PreKeywords.PREF_KEY_IS_FIRSTTIME
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.fragment_generate_qr.*
import java.time.LocalDate

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

    fun generateEMVcoString(number: String):String{

        var Paid_Entity_Reference_VALUE=EncryptionUtils.encryptStringAESCBC(number)
        var Masked_Paid_Entity_Reference_VALUE=number

       var qrString=  Constants.EMVco.Payload_Format_Indicator_ID + Constants.EMVco.Payload_Format_Indicator_SIZE + Constants.EMVco.Payload_Format_Indicator_VALUE +
                Constants.EMVco.Point_Of_Initiation_Method_ID + Constants.EMVco.Point_Of_Initiation_Method_SIZE + Constants.EMVco.Point_Of_Initiation_Method_VALUE +
                Constants.EMVco.Merchant_Account_Information_ID + Constants.EMVco.Merchant_Account_Information_SIZE + Constants.EMVco.Merchant_Account_Information_Value +
                Constants.EMVco.Globally_Unique_Identifier_ID + Constants.EMVco.Globally_Unique_Identifier_SIZE + Constants.EMVco.Globally_Unique_Identifier_VALUE +
                Constants.EMVco.Encryption_Format_ID + Constants.EMVco.Encryption_Format_SIZE + Constants.EMVco.Encryption_Format_VALUE +
                Constants.EMVco.Paid_Entity_Reference_Format_ID + Constants.EMVco.Paid_Entity_Reference_Format_SIZE + Constants.EMVco.Paid_Entity_Reference_Format_VALUE +
                Constants.EMVco.Paid_Entity_Reference_ID + Constants.EMVco.Paid_Entity_Reference_SIZE + Paid_Entity_Reference_VALUE +
                Constants.EMVco.Masked_Paid_Entity_Reference_ID + Constants.EMVco.Masked_Paid_Entity_Reference_SIZE + Masked_Paid_Entity_Reference_VALUE

        return qrString
    }

    fun extractNumberFromEMVcoQR(text:String):String{
        var num=""
        try {
            if (text.contains(Constants.EMVco.Payload_Format_Indicator_ID + Constants.EMVco.Payload_Format_Indicator_SIZE + Constants.EMVco.Payload_Format_Indicator_VALUE)) {
                num =
                    text.split(Constants.EMVco.Paid_Entity_Reference_ID + Constants.EMVco.Paid_Entity_Reference_SIZE)[1]
                if (text.contains(Constants.EMVco.Masked_Paid_Entity_Reference_ID + Constants.EMVco.Masked_Paid_Entity_Reference_SIZE)) {
                    num = num.split(Constants.EMVco.Masked_Paid_Entity_Reference_ID + Constants.EMVco.Masked_Paid_Entity_Reference_SIZE)[0]
                    num = EncryptionUtils.decryptStringAESCBC(num)
                } else {
                    num = ""
                }
            }
        }
        catch (e:Exception){
            num=""
        }
        Log.d("DecryptedNumber",num)
        return num
    }

    fun openDialerWithNumber(context: Context){
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + Constants.HELPLINE_NUMBER)
            context.startActivity(intent)
    }

    fun isFirstTime(context: Context):Boolean{

        if (PrefUtils.getBoolean(context,PREF_KEY_IS_FIRSTTIME,true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time")

            // record the fact that the app has been started at least once
            PrefUtils.addBoolean(context, PREF_KEY_IS_FIRSTTIME, false)

            return true
        }
        else{
            return false
        }
    }
}