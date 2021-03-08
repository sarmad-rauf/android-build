package com.es.marocapp.usecase

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.usecase.login.LoginActivity.Companion.KEY_REDIRECT_USER
import com.es.marocapp.utils.Logger
import com.es.marocapp.utils.PrefUtils
import java.util.*

abstract class BaseActivity<T: ViewDataBinding>: AppCompatActivity()
{
    lateinit var mDataBinding:T
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setContentView(setLayout())


        setLocale()

        mDataBinding= DataBindingUtil.setContentView(this, setLayout())
    }

    private fun setLocale() {
        LocaleManager.setAppLanguage(this, LocaleManager.selectedLanguage)
        val configuration: Configuration = this.getResources()!!.getConfiguration()
        configuration.setLayoutDirection(Locale(LocaleManager.selectedLanguage))
        this.getResources().updateConfiguration(configuration, this.getResources().getDisplayMetrics())
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        init(savedInstanceState)
    }

    abstract fun init(savedInstanceState: Bundle?)

    abstract fun setLayout():Int

    /**`
     * Start new activity with having previous activity
     *
     * @param activity
     * @param clazz
     */
    fun startNewActivity(activity: Activity, clazz: Class<*>) {
        try {
          //  RootValues.getInstance().TIME_STAMP_FOR_INTENT_INJECTION = System.currentTimeMillis().toString()
            val intent = Intent(activity, clazz)
         /*   intent.putExtra(KEY_INJECTION_EXTRA_STRING,
                BASE_EXTRA_TEXT_FOR_INTENT_INJECTION + RootValues.getInstance().TIME_STAMP_FOR_INTENT_INJECTION)*/
            activity.startActivity(intent)
        } catch (e: Exception) {
            Logger.debugLog(Logger.TAG_CATCH_LOGS, e.message!!)
        }

    }

    /**
     * Start new activity and close previous activity
     *
     * @param activity
     * @param clazz
     */
    fun startNewActivityAndClear(activity: Activity, clazz: Class<*>) {
        try {
           // RootValues.getInstance().TIME_STAMP_FOR_INTENT_INJECTION = System.currentTimeMillis().toString()
            val intent = Intent(activity, clazz)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP)
          /*  intent.putExtra(KEY_INJECTION_EXTRA_STRING,
                BASE_EXTRA_TEXT_FOR_INTENT_INJECTION + RootValues.getInstance().TIME_STAMP_FOR_INTENT_INJECTION)*/
            activity.startActivity(intent)
            activity.finish()
        } catch (e: Exception) {
            Logger.debugLog(Logger.TAG_CATCH_LOGS, e.message!!)
        }

    }

    /**
     * Start new activity and close previous activity with extra data
     *
     * @param activity
     * @param clazz
     * @param bundle
     */
    fun startNewActivityAndClear(activity: Activity, clazz: Class<*>, bundle: Bundle) {
        try {
          //  RootValues.getInstance().TIME_STAMP_FOR_INTENT_INJECTION = System.currentTimeMillis().toString()
            val intent = Intent(activity, clazz)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            /*intent.putExtra(KEY_INJECTION_EXTRA_STRING,
                BASE_EXTRA_TEXT_FOR_INTENT_INJECTION + RootValues.getInstance().TIME_STAMP_FOR_INTENT_INJECTION)*/
            intent.putExtras(bundle)
            activity.startActivity(intent)
            activity.finish()
        } catch (e: Exception) {
            Logger.debugLog(Logger.TAG_CATCH_LOGS, e.message!!)
        }

    }

    /**
     * Start new Activity with adding bundle
     *
     * @param activity
     * @param clazz
     * @param bundle
     */
    fun startNewActivity(activity: Activity, clazz: Class<*>, bundle: Bundle) {
        try {
          //  RootValues.getInstance().TIME_STAMP_FOR_INTENT_INJECTION = System.currentTimeMillis().toString()
            val intent = Intent(activity, clazz)
          /*  intent.putExtra(KEY_INJECTION_EXTRA_STRING,
                BASE_EXTRA_TEXT_FOR_INTENT_INJECTION + RootValues.getInstance().TIME_STAMP_FOR_INTENT_INJECTION)*/
            intent.putExtras(bundle)
            activity.startActivity(intent)
        } catch (e: Exception) {
            Logger.debugLog(Logger.TAG_CATCH_LOGS, e.message!!)
        }

    }

    fun startActivityAfterLanguageChange(activity: Activity, clazz: Class<*>) {
        try {
            /*if (Tools.isActivityActive(activity)) {*/
                activity.finish()
                /*RootValues.getInstance().TIME_STAMP_FOR_INTENT_INJECTION = System.currentTimeMillis().toString()*/
                val intent = Intent(activity, clazz)
              /*  intent.putExtra(KEY_INJECTION_EXTRA_STRING,
                    BASE_EXTRA_TEXT_FOR_INTENT_INJECTION + RootValues.getInstance().TIME_STAMP_FOR_INTENT_INJECTION)*/
                activity.startActivity(intent)
            /*}*/
        } catch (e: Exception) {
            Logger.debugLog(Logger.TAG_CATCH_LOGS, e.message!!)
        }

    }

    fun logoutAndRedirectUserToLoginScreen(activity: Activity, clazz: Class<*>, value: String) {
        PrefUtils.addString(this, PrefUtils.PreKeywords.PREF_KEY_USER_MSISDN,"")
        try {
            //  RootValues.getInstance().TIME_STAMP_FOR_INTENT_INJECTION = System.currentTimeMillis().toString()
            val intent = Intent(activity, clazz)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            /*intent.putExtra(KEY_INJECTION_EXTRA_STRING,
                BASE_EXTRA_TEXT_FOR_INTENT_INJECTION + RootValues.getInstance().TIME_STAMP_FOR_INTENT_INJECTION)*/
            var bundle =Bundle()
            bundle.putString(KEY_REDIRECT_USER, value)
            intent.putExtras(bundle)
            activity.startActivity(intent)
            activity.finish()
        } catch (e: Exception) {
            Logger.debugLog(Logger.TAG_CATCH_LOGS, e.message!!)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    /*override fun onDestroy() {

        System.gc()//?
        System.runFinalization()//?
        super.onDestroy()
    }*/
}