package com.es.marocapp.usecase.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivitySettingsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.locale.LocaleManager.KEY_LANGUAGE_AR
import com.es.marocapp.locale.LocaleManager.KEY_LANGUAGE_EN
import com.es.marocapp.locale.LocaleManager.KEY_LANGUAGE_FR
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.favorites.FavoritesActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Tools
import kotlinx.android.synthetic.main.activity_settings.view.*


class SettingsActivity : BaseActivity<ActivitySettingsBinding>(),
    SettingsClickListener{
/*    private val READ_PHONE_CALL_REQUEST_CODE = 115
    private val PERMISSION_TAG = "permissions"*/

    private lateinit var settingsViewModel: SettingsViewModel
    var referenceNumber="";

    override fun setLayout(): Int {
        return R.layout.activity_settings
    }

    override fun init(savedInstanceState: Bundle?) {
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)


        mDataBinding.apply {
            viewmodel = settingsViewModel
            listener = this@SettingsActivity
        }
        setStrings()

        subscribeForUpdateLanguage()

        if(Constants.IS_CONSUMER_USER || Constants.IS_MERCHANT_USER){
            mDataBinding.cvDefaultAccount.visibility=View.VISIBLE
            /*settingsViewModel.requestForAccountHolderAddtionalInformationApi(this)
            subscribeForDefaultAccountStatus()*/
            if(Constants.IS_DEFAULT_ACCOUNT_SET){
                mDataBinding.defaultAccountSwitch.setChecked(true);
            }
            subscribeForSetDefaultAccountStatus()
            subscribeForVerifyOTPForSetDefaultAccountStatus()
            subscribeForUnRegisterDefaultAccountStatus()
        }
        else{
            mDataBinding.cvDefaultAccount.visibility=View.GONE
        }

        mDataBinding.defaultAccountSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            if(isChecked) {
               // showPopUp()
                settingsViewModel.requestForSetDefaultAccount(this@SettingsActivity)
            }
            else{
                settingsViewModel.requestForUnRegisterDefaultAccount(this@SettingsActivity)
            }
        })
    }

    private fun setStrings() {
        mDataBinding.root.activityHeaderTitle.text = LanguageData.getStringValue("Settings")
        mDataBinding.root.tvChangeLanguage.text = LanguageData.getStringValue("ChangeLanguage")
        mDataBinding.root.tvManageFavorites.text = LanguageData.getStringValue("ManageFavorites")
        mDataBinding.root.tvBlockAccount.text = LanguageData.getStringValue("BlockAccount")
        mDataBinding.root.btnUpdate.text = LanguageData.getStringValue("BtnTitle_Update")
        mDataBinding.root.tvDefaultAccount.text = LanguageData.getStringValue("SetAsDefault")




        if(LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_EN)){
            mDataBinding.root.tvLanguage.text= resources.getString(R.string.language_english)
        }
        else if(LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_FR)){
            mDataBinding.root.tvLanguage.text= resources.getString(R.string.language_french)
        }
        else if(LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_AR)){
            mDataBinding.root.tvLanguage.text= resources.getString(R.string.language_arabic)
        }
    }

 /*   private fun subscribeForDefaultAccountStatus() {

        settingsViewModel.getAccountHolderAdditionalInfoResponseListner.observe(this,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.additionalinformation.isNullOrEmpty()) {
                        showPopUp()
                    } else {
                        if (it.additionalinformation[0].value.equals("FALSE", true)) {
                            showPopUp()
                        }
                    }
                }else{
                    DialogUtils.showErrorDialoge(this ,it.description)
                }
            }
        )
    }*/

    private fun subscribeForSetDefaultAccountStatus() {

        settingsViewModel.setDefaultAccountResponseListener.observe(this,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    referenceNumber=it.referenceNumber
                    showOTPdialogue()
                }else{
                    DialogUtils.showErrorDialoge(this ,it.description)
                }
            }
        )
    }

    private fun subscribeForVerifyOTPForSetDefaultAccountStatus() {

        settingsViewModel.verifyOTPForDefaultAccountResponseListener.observe(this,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    Constants.IS_DEFAULT_ACCOUNT_SET=true
                    DialogUtils.successFailureDialogue(this@SettingsActivity,LanguageData.getStringValue("OperationPerformedSuccessfullyDot"),0)
                }else{
                    DialogUtils.successFailureDialogue(this@SettingsActivity,LanguageData.getStringValue("FailedToPerformOperationDot"),1)
                }
            }
        )
    }

    private fun subscribeForUnRegisterDefaultAccountStatus() {

        settingsViewModel.unregisterDefaultAccountResponseListener.observe(this,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    Constants.IS_DEFAULT_ACCOUNT_SET=false
                    DialogUtils.successFailureDialogue(this@SettingsActivity,it.description,0)
                }else{
                    DialogUtils.successFailureDialogue(this@SettingsActivity,it.description,1)
                }
            }
        )
    }
    private fun subscribeForUpdateLanguage() {

        settingsViewModel.updateLanguageResponseListener.observe(this,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if(LocaleManager.languageToBeChangedAfterAPI.equals(LanguageData.getStringValue("DropDown_English"))){

                        LocaleManager.setLanguageAndUpdate(this@SettingsActivity,
                            LocaleManager.KEY_LANGUAGE_EN,
                            SettingsActivity::class.java)
                    }
                    else if(LocaleManager.languageToBeChangedAfterAPI.equals(LanguageData.getStringValue("DropDown_French"))) {

                        LocaleManager.setLanguageAndUpdate(this@SettingsActivity,
                            LocaleManager.KEY_LANGUAGE_FR,
                            SettingsActivity::class.java)
                    }
                    else if(LocaleManager.languageToBeChangedAfterAPI.equals(LanguageData.getStringValue("DropDown_Arabic"))) {

                        LocaleManager.setLanguageAndUpdate(this@SettingsActivity,
                            LocaleManager.KEY_LANGUAGE_AR,
                            SettingsActivity::class.java)
                    }
                   // DialogUtils.successFailureDialogue(this@SettingsActivity,it.description,0)
                }else{
                    DialogUtils.successFailureDialogue(this@SettingsActivity,it.description,1)
                }
            }
        )
    }

    private fun showPopUp() {
        val confirmationTxt= LanguageData.getStringValue("DoYouWantToChooseThisMwalletMtCashDefaultForDoingOperationsQuestion")
        DialogUtils.showConfirmationDialogue(confirmationTxt!!,this,object : DialogUtils.OnConfirmationDialogClickListner{
            override fun onDialogYesClickListner() {
                settingsViewModel.requestForSetDefaultAccount(this@SettingsActivity)
            }


        })
    }

    private fun showPopUpForUnregister() {
        val confirmationTxt= /*LanguageData.getStringValue("DoYouWantToChooseThisMwalletMtCashDefaultForDoingOperationsQuestion")*/"UnRegister"
        DialogUtils.showConfirmationDialogue(confirmationTxt!!,this,object : DialogUtils.OnConfirmationDialogClickListner{
            override fun onDialogYesClickListner() {
                settingsViewModel.requestForUnRegisterDefaultAccount(this@SettingsActivity)
            }


        })
    }

    private fun showOTPdialogue() {
        DialogUtils.showOTPDialogue(this,object : DialogUtils.OnOTPDialogClickListner{

            override fun onOTPDialogYesClickListner(otp: String) {
                settingsViewModel.requestForVerifyOTPForSetDefaultAccount(this@SettingsActivity,referenceNumber,otp)
            }

        })
    }

    override fun onChangeLanguageClick(view: View) {
        DialogUtils.showChangeLanguageDialogue(this,object : DialogUtils.OnChangeLanguageClickListner{

            override fun onChangeLanguageDialogYesClickListner(selectedLanguage: String) {
                //Toast.makeText(this@SettingsActivity,selectedLanguage,Toast.LENGTH_LONG).show()
                mDataBinding.root.tvLanguage.text=selectedLanguage
                LocaleManager.languageToBeChangedAfterAPI=selectedLanguage

                var langParam=""
                if(selectedLanguage.equals(LanguageData.getStringValue("DropDown_English"))){
                    langParam=KEY_LANGUAGE_EN
                }
                else  if(selectedLanguage.equals(LanguageData.getStringValue("DropDown_French"))){
                    langParam= KEY_LANGUAGE_FR
                }

                else  if(selectedLanguage.equals(LanguageData.getStringValue("DropDown_Arabic"))){
                    langParam= KEY_LANGUAGE_AR
                }

                settingsViewModel.requestForChangeLanguage(this@SettingsActivity,langParam)

            }

        })
    }

    override fun onManageFavoritesClick(view: View) {
        startActivity(Intent(this@SettingsActivity, FavoritesActivity::class.java))
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

    override fun onSetDefaultAccountClick(view: View) {

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