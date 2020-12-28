@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.es.marocapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.es.marocapp.model.responses.LoginWithCertResponse
import com.es.marocapp.utils.PrefUtils.PreKeywords.PREF_KEY_IS_FIRSTTIME
import com.squareup.moshi.Moshi
import org.json.JSONArray


/**
 * Utility class for saving and retrieving data from shared preferences
 *
 */

object PrefUtils {

    private val PREF_NAME = "martocTel.prefs"

    /**
     * Sharepreferences Keys
     */
    object PreKeywords {
        val PREF_KEY_USER_DATA = "key.pref.user.data"
        val PREF_KEY_USER_MSISDN = "key.pref.user.msisdn"
        val PREF_KEY_USER_NAME = "key.pref.user.name"
        val PREF_KEY_IS_FIRSTTIME = "key.pref.is.firsttime"
        val PREF_KEY_IS_SHOW_TUTORIALS = "key.pref.is.show.tutorial"
        var PREF_KEY_FIRST_TIME_LOGIN_AFTER_APP_INSTALL = "key.pref.first.time.login.after.app.install"

        // Locale
        val PREF_KEY_SELECTED_LANGUAGE_LOCALE = "key.selected.language.locale"

    }

    /**
     * This function for only add user data to shared preferences
     *
     * @param context
     * @param userModel
     */
    fun setUserDataToCache(context: Context?, userModel: LoginWithCertResponse?) {
        if (context == null) return
        try {


                // Save user's data into DB
                val adapter = Moshi.Builder().build().adapter<LoginWithCertResponse>(LoginWithCertResponse::class.java)
                var userDataJsonString = adapter.toJson(userModel)

           /*     if (Tools.hasValue(userDataJsonString)) {
                    userDataJsonString = EncryptionServices.encryptAESCBC(userDataJsonString, RootValues.getInstance().keyValueFromNdk)
                }*/
                addString(context, PreKeywords.PREF_KEY_USER_DATA, userDataJsonString)
        } catch (e: Exception) {
        }

    }

    /**
     * This function for getting only user data from shared preferences
     *
     * @param context
     * @return
     */
    fun getUserDataFromCache(context: Context?): LoginWithCertResponse? {

        var userDataModel: LoginWithCertResponse? = null

        var json = getString(context!!, PreKeywords.PREF_KEY_USER_DATA, "")

      /*  if (Tools.hasValue(json)) {
            json = EncryptionServices.decryptAESCBC(json!!, RootValues.getInstance().keyValueFromNdk)
        }*/

        if (Tools.hasValue(json)) {
            try {
                val adapter = Moshi.Builder().build().adapter<LoginWithCertResponse>(LoginWithCertResponse::class.java)
                adapter.lenient()
                userDataModel = adapter.fromJson(json)
            } catch (e: Exception) {
            }
        }

        return userDataModel
    }

    /**
     * Add value to shared prefs
     *
     * @param context context for shared prefs
     * @param key     key
     * @param value   value for given key
     */
    fun addString(context: Context, key: String, value: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putString(key, value)
                .apply()
    } // add

    /**
     * Add int value to shared prefs
     *
     * @param context context for shared prefs
     * @param key     key
     * @param value   value for given key
     */
    fun addInt(context: Context, key: String, value: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putInt(key, value)
                .apply()
    } // addInt

    /**
     * Add float value to shared prefs
     *
     * @param context context for shared prefs
     * @param key     key
     * @param value   value for given key
     */
    fun addFloat(context: Context, key: String, value: Float) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putFloat(key, value)
                .apply()
    } // addFloat


    /**
     * Add boolean value to shared prefs
     *
     * @param context
     * @param key
     * @param value
     */
    fun addBoolean(context: Context, key: String, value: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(key, value)
                .apply()
    } // addFloat


    /**
     * Get boolean from prefs with given default value
     *
     * @param context context for shared prefs
     * @param key     key for this field
     * @param def     default value
     * @return value as String
     */
    fun getBoolean(context: Context, key: String, def: Boolean): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(key, def)
    }


    /**
     * Get string from prefs with given default value
     *
     * @param context context for shared prefs
     * @param key     key for this field
     * @param def     default value
     * @return value as String
     */
    @JvmOverloads
    fun getString(context: Context, key: String, def: String = ""): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(key, def)
    }

    /**
     * Get value as given type
     *
     * @param context context for shared prefs
     * @param key     key for this field
     * @param def     default value
     * @param type    type to return
     * @param <T>     return type
     * @return T value parsed to given type
    </T> */
    fun <T> getAs(context: Context, key: String, def: String, type: Class<T>): T? {
        return type.cast(getString(context, key, def))
    }

    /**
     * @see PrefUtils.getAs
     */
    fun getAsInt(context: Context, key: String): Int {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(key, 0)
    }

    /**
     * @see PrefUtils.getAs
     */
    fun getAsFloat(context: Context, key: String): Float? {
        return getAs(context, key, "0.0", Float::class.javaPrimitiveType!!)
    }

    /**
     * ADD String Array list into the Sharedperences
     * @context The Context
     * @key Key value pair
     * @arrayList String array list the is going to save
     */
    fun addAsStringArrayList(context: Context, key: String, arrayList: ArrayList<String>) {

        var jsonArray = JSONArray(arrayList.toString())
        if (jsonArray != null) {
            addString(context, key, jsonArray.toString())
        }
    } // addStringArrayList

    /**
     * Get String Arraylist from Shareperences
     * @context the Context
     * @key The Key Value pair
     */
    fun getAsStringArrayList(context: Context, key: String): ArrayList<String> {

        var savedList = ArrayList<String>()

        var jsonString = getString(context, key, "")

        if (Tools.hasValue(jsonString)) {
            var jsonArray = JSONArray(jsonString)
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    savedList.add(jsonArray.getString(i));
                }
            }
        }

        return savedList;
    } // getStringArrayList




}