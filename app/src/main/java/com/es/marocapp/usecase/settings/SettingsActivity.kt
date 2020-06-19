package com.es.marocapp.usecase.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivitySettingsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.activity_settings.view.*


class SettingsActivity : BaseActivity<ActivitySettingsBinding>() , SettingsClickListener{

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
        val descriptionTxt = LanguageData.getStringValue("CallToBlockAccount")
        DialogUtils.showCustomDialogue(this,btnTxt,descriptionTxt,titleTxt,object : DialogUtils.OnCustomDialogListner{
            override fun onCustomDialogOkClickListner() {

            }


        })
    }

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


}