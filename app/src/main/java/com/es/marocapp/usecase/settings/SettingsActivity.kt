package com.es.marocapp.usecase.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivitySettingsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Tools
import kotlinx.android.synthetic.main.activity_settings.view.*


class SettingsActivity : BaseActivity<ActivitySettingsBinding>(),
    SettingsClickListener{
/*    private val READ_PHONE_CALL_REQUEST_CODE = 115
    private val PERMISSION_TAG = "permissions"*/
    override fun setLayout(): Int {
        return R.layout.activity_settings
    }

    override fun init(savedInstanceState: Bundle?) {
        mDataBinding.apply {
        //    viewmodel = mActivityViewModel
            listener = this@SettingsActivity
        }
        setStrings()
    }

    private fun setStrings() {
        mDataBinding.root.activityHeaderTitle.text = LanguageData.getStringValue("Settings")
        mDataBinding.root.tvChangeLanguage.text = LanguageData.getStringValue("ChangeLanguage")
        mDataBinding.root.tvBlockAccount.text = LanguageData.getStringValue("BlockAccount")
        mDataBinding.root.btnUpdate.text = LanguageData.getStringValue("BtnTitle_Update")


        if(LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_EN)){
            mDataBinding.root.tvLanguage.text= resources.getString(R.string.language_english)
        }
        else if(LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_FR)){
            mDataBinding.root.tvLanguage.text= resources.getString(R.string.language_french)
        }
    }

    override fun onChangeLanguageClick(view: View) {
        DialogUtils.showChangeLanguageDialogue(this,object : DialogUtils.OnChangeLanguageClickListner{

            override fun onChangeLanguageDialogYesClickListner(selectedLanguage: String) {
                //Toast.makeText(this@SettingsActivity,selectedLanguage,Toast.LENGTH_LONG).show()
                mDataBinding.root.tvLanguage.text=selectedLanguage

                if(selectedLanguage.equals("English")){

                    LocaleManager.setLanguageAndUpdate(this@SettingsActivity,
                        LocaleManager.KEY_LANGUAGE_EN,
                        SettingsActivity::class.java)
                }
                else if(selectedLanguage.equals("French")) {

                    LocaleManager.setLanguageAndUpdate(this@SettingsActivity,
                        LocaleManager.KEY_LANGUAGE_FR,
                        SettingsActivity::class.java)
                }
            }

        })
    }

    override fun onBlockAccountClick(view: View) {
        val btnTxt = LanguageData.getStringValue("BtnTitle_Call")
        val titleTxt = LanguageData.getStringValue("BlockAccount")
        val descriptionTxt = LanguageData.getStringValue("CallToBlockAccount")?.replace("00000",
            Constants.HELPLINE_NUMBER)
        DialogUtils.showCustomDialogue(this,btnTxt,descriptionTxt,titleTxt,object : DialogUtils.OnCustomDialogListner{
            override fun onCustomDialogOkClickListner() {
                Tools.openDialerWithNumber(this@SettingsActivity)
            }


        })
    }

 /*   fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CALL_PHONE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(PERMISSION_TAG, "Permission to read phone state denied")
            makeRequestPermission()
        }else{
//            val telephonyManager =
//                application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
//            Constants.CURRENT_DEVICE_ID = telephonyManager!!.deviceId

        }
    }
    private fun makeRequestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CALL_PHONE),
            READ_PHONE_CALL_REQUEST_CODE
        )
    }*/

    override fun onUpdateClickListener(view: View) {

    }

    override fun onBackButtonClickListener(view: View) {

        onBackPressed()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        (this as BaseActivity<*>).startNewActivityAndClear(this, MainActivity::class.java)
    }

   /* @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_PHONE_CALL_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(PERMISSION_TAG, "Permission has been denied by user")
                } else {
//                    val telephonyManager =
//                        application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
                    openDialer()
                }
            }
        }

    }*/


}