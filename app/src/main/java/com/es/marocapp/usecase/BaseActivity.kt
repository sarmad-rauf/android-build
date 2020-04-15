package com.es.marocapp.usecase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.es.marocapp.utils.Logger

abstract class BaseActivity<T: ViewDataBinding>: AppCompatActivity()
{
    lateinit var mDataBinding:T
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(setLayout())

        mDataBinding= DataBindingUtil.setContentView(this, setLayout())
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
    /*override fun onDestroy() {

        System.gc()//?
        System.runFinalization()//?
        super.onDestroy()
    }*/
}