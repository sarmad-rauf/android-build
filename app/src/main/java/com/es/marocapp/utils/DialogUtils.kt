package com.es.marocapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.es.marocapp.R
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.widgets.MarocButton
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern


object DialogUtils {

    fun showErrorDialoge(
        mContext: Context?,
        description: String?,
        okBtnText : String = "BtnTitle_OK"
    ) {
        val addDialog = Dialog(mContext!!)
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addDialog.setContentView(R.layout.layout_error_dialog)

        val dialogWindow = addDialog.window
        val layoutParams = dialogWindow!!.attributes
        layoutParams.x = Gravity.CENTER_HORIZONTAL
        layoutParams.y = Gravity.CENTER_VERTICAL
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = layoutParams

        val tvMessage = addDialog.findViewById<TextView>(R.id.error_dialog_description)
        val tvTitle = addDialog.findViewById<TextView>(R.id.error_dialog_title)
        val btnOK = addDialog.findViewById<Button>(R.id.error_dialog_ok_btn)

        if(okBtnText.equals("BtnTitle_OK")){
            btnOK.text = LanguageData.getStringValue(okBtnText)
        }else{
            btnOK.text = okBtnText
        }
        tvTitle.text = LanguageData.getStringValue("Alert")
        tvMessage.text = description

        addDialog.show()
        btnOK.setOnClickListener {
            addDialog.dismiss()
        }

//        val handler = Handler()
//        val runnable = Runnable {
//            if (addDialog != null && addDialog.isShowing) {
//                try {
//                    addDialog.dismiss()
//                } catch (ex: Exception) {
//                }
//            }
//        }
//        addDialog.setOnDismissListener { handler.removeCallbacks(runnable) }
//        handler.postDelayed(runnable, 7000)
    }

    fun showUpdateAPPDailog(
        mContext: Context?,
        description: String?,
        listner : DialogUtils.OnCustomDialogListner,
        okBtnText : String = "BtnTitle_OK"
    ) {
        val addDialog = Dialog(mContext!!)
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addDialog.setContentView(R.layout.dialog_udpate_app_layout)

        val dialogWindow = addDialog.window
        val layoutParams = dialogWindow!!.attributes
        layoutParams.x = Gravity.CENTER_HORIZONTAL
        layoutParams.y = Gravity.CENTER_VERTICAL
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = layoutParams

        val tvMessage = addDialog.findViewById<TextView>(R.id.updateDialogDescription)
        val btnOK = addDialog.findViewById<Button>(R.id.updateDialogBtn)

        if(okBtnText.equals("BtnTitle_OK")){
            btnOK.text = LanguageData.getStringValue(okBtnText)
        }else{
            btnOK.text = okBtnText
        }
        tvMessage.text = description

        addDialog.show()
        btnOK.setOnClickListener {
            addDialog.dismiss()
        }
    }

    fun showPasswordDialoge(
        mContext: Context?,
        listner: OnPasswordDialogClickListner
    ) {
        val addDialog = Dialog(mContext!!)
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addDialog.setContentView(R.layout.layout_password_dialog)

        val dialogWindow = addDialog.window
        val layoutParams = dialogWindow!!.attributes
        layoutParams.x = Gravity.CENTER_HORIZONTAL
        layoutParams.y = Gravity.CENTER_VERTICAL
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = layoutParams

        addDialog.show()

        var btnNO = addDialog.findViewById<Button>(R.id.password_dialog_no_btn)
        var btnYes = addDialog.findViewById<Button>(R.id.password_dialog_yes_btn)


        btnNO.text = LanguageData.getStringValue("BtnTitle_No")
        btnYes.text = LanguageData.getStringValue("BtnTitle_Yes")

        btnNO.setOnClickListener {
            addDialog.dismiss()
        }

        var tvDescription = addDialog.findViewById<TextView>(R.id.password_dialog_description)
        var tvTitle = addDialog.findViewById<TextView>(R.id.password_dialog_title)

        tvDescription.text = LanguageData.getStringValue("EnterPasswordToProceed")
        tvTitle.text = LanguageData.getStringValue("DearCustomer")

        var passwordField =
            addDialog.findViewById<EditText>(R.id.password_dialog_input_enter_password)
        var passwordFieldInput =
            addDialog.findViewById<TextInputLayout>(R.id.password_dialog_layout_enter_password)
        passwordFieldInput.hint = LanguageData.getStringValue("EnterPassword")
        btnYes.setOnClickListener {
            var password = passwordField.text.toString().trim()
            if (password.equals("")) {
                passwordFieldInput.error = LanguageData.getStringValue("PleaseEnterValidPassword")
                passwordFieldInput.isErrorEnabled = true
            } else {
                passwordFieldInput.error = ""
                passwordFieldInput.isErrorEnabled = false
                listner.onDialogYesClickListner(password)
                addDialog.dismiss()
            }
        }
    }

    fun showAddToFavoriteDialoge(
        mContext: Context?,
        listner: OnAddToFavoritesDialogClickListner
    ) {
        val addDialog = Dialog(mContext!!)
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addDialog.setContentView(R.layout.layout_dialog_add_to_favorite)

        val dialogWindow = addDialog.window
        val layoutParams = dialogWindow!!.attributes
        layoutParams.x = Gravity.CENTER_HORIZONTAL
        layoutParams.y = Gravity.CENTER_VERTICAL
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = layoutParams

        addDialog.show()

        var btnNO = addDialog.findViewById<Button>(R.id.favorites_dialog_cancel_btn)
        var btnYes = addDialog.findViewById<Button>(R.id.favorite_dialog_yes_btn)
        var otpDialogFieldDescriotion =
            addDialog.findViewById<TextView>(R.id.add_toFavorite_dialog_description)
        var otpDialogFieldTitle = addDialog.findViewById<TextView>(R.id.add_toFavorite_dialog_title)


        btnNO.setOnClickListener {
            listner.onDialogNoClickListner()
            addDialog.dismiss()
        }

        btnNO.text = LanguageData.getStringValue("BtnTitle_Cancel")
        btnYes.text = LanguageData.getStringValue("Submit")
        otpDialogFieldTitle.text = LanguageData.getStringValue("DearCustomer")
        otpDialogFieldDescriotion.text =
            LanguageData.getStringValue("PleaseAssignNickForTheNewFavorite")

        var nickNameField =
            addDialog.findViewById<EditText>(R.id.favorite_dialog_input_enter_nickName)
        var nickNameFieldInput =
            addDialog.findViewById<TextInputLayout>(R.id.favorite_dialog_layout_enter_nick)
        nickNameFieldInput.hint = LanguageData.getStringValue("AddNick")

        btnYes.setOnClickListener {
            var nickName = nickNameField.text.toString().trim()
            if (nickName.equals("")) {
                nickNameFieldInput.error = LanguageData.getStringValue("PleaseEnterNickName")
                nickNameFieldInput.isErrorEnabled = true
            } else {
                nickNameFieldInput.error = ""
                nickNameFieldInput.isErrorEnabled = false
                listner.onDialogYesClickListner(nickName)
                addDialog.dismiss()
            }
        }
    }

    interface OnAddToFavoritesDialogClickListner {
        fun onDialogYesClickListner(nickName: String)
        fun onDialogNoClickListner()

    }

    interface OnPasswordDialogClickListner {
        fun onDialogYesClickListner(password: String)
    }

    fun showConfirmationDialogue(
        confirmationTxt: String,
        mContext: Context?,
        listner: OnConfirmationDialogClickListner
    ) {
        val addDialog = Dialog(mContext!!)
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addDialog.setContentView(R.layout.layout_confirmation_dialog)

        val dialogWindow = addDialog.window
        val layoutParams = dialogWindow!!.attributes
        layoutParams.x = Gravity.CENTER_HORIZONTAL
        layoutParams.y = Gravity.CENTER_VERTICAL
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = layoutParams

        addDialog.show()
        var tvConfirmation = addDialog.findViewById<TextView>(R.id.confirmation_dialog_description)
        var tvDialogTitle = addDialog.findViewById<TextView>(R.id.confirmation_dialog_title)
        var btnNO = addDialog.findViewById<Button>(R.id.confirmation_dialog_no_btn)
        var btnYes = addDialog.findViewById<Button>(R.id.confirmation_dialog_yes_btn)


        btnNO.text = LanguageData.getStringValue("BtnTitle_No")
        btnYes.text = LanguageData.getStringValue("BtnTitle_Yes")
        tvDialogTitle.text = LanguageData.getStringValue("DearCustomer")
        tvConfirmation.text = confirmationTxt

        addDialog.findViewById<View>(R.id.confirmation_dialog_no_btn).setOnClickListener {
            addDialog.dismiss()
        }
        addDialog.findViewById<View>(R.id.confirmation_dialog_yes_btn).setOnClickListener {
            listner.onDialogYesClickListner()
            addDialog.dismiss()
        }
    }

    fun showSuccessDialog(
        mContext: Context?,
        description: String?,
        listner: OnConfirmationDialogClickListner
    ) {
        val addDialog = Dialog(mContext!!)
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addDialog.setContentView(R.layout.layout_error_dialog)

        val dialogWindow = addDialog.window
        val layoutParams = dialogWindow!!.attributes
        layoutParams.x = Gravity.CENTER_HORIZONTAL
        layoutParams.y = Gravity.CENTER_VERTICAL
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = layoutParams

        val tvMessage = addDialog.findViewById<TextView>(R.id.error_dialog_description)
        val tvTitle = addDialog.findViewById<TextView>(R.id.error_dialog_title)
        val btnOK = addDialog.findViewById<Button>(R.id.error_dialog_ok_btn)

        btnOK.text = LanguageData.getStringValue("BtnTitle_OK")
        tvTitle.text = LanguageData.getStringValue("DearCustomer")
        tvMessage.text = description

        addDialog.show()
        btnOK.setOnClickListener {
            listner.onDialogYesClickListner()
            addDialog.dismiss()
        }

//        val handler = Handler()
//        val runnable = Runnable {
//            if (addDialog != null && addDialog.isShowing) {
//                try {
//                    addDialog.dismiss()
//                } catch (ex: Exception) {
//                }
//            }
//        }
//        addDialog.setOnDismissListener { handler.removeCallbacks(runnable) }
//        handler.postDelayed(runnable, 7000)
    }

    interface OnConfirmationDialogClickListner {
        fun onDialogYesClickListner()
    }

    fun showOTPDialogue(
        mContext: Context?,
        listner: OnOTPDialogClickListner
    ) {
        var isOTPRegexMatches = false
        val addDialog = Dialog(mContext!!)
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addDialog.setContentView(R.layout.layout_otp_dialog)

        val dialogWindow = addDialog.window
        val layoutParams = dialogWindow!!.attributes
        layoutParams.x = Gravity.CENTER_HORIZONTAL
        layoutParams.y = Gravity.CENTER_VERTICAL
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = layoutParams

        addDialog.show()

        var btnNO = addDialog.findViewById<Button>(R.id.otp_dialog_no_btn)
        var btnYes = addDialog.findViewById<Button>(R.id.otp_dialog_yes_btn)


        addDialog.findViewById<View>(R.id.otp_dialog_no_btn).setOnClickListener {
            addDialog.dismiss()
        }
        var otpDialogFieldDescriotion =
            addDialog.findViewById<TextView>(R.id.otp_dialog_description)
        var otpDialogFieldTitle = addDialog.findViewById<TextView>(R.id.otp_dialog_title)

        btnNO.text = LanguageData.getStringValue("BtnTitle_No")
        btnYes.text = LanguageData.getStringValue("BtnTitle_Yes")
        otpDialogFieldTitle.text = LanguageData.getStringValue("DearCustomer")
        otpDialogFieldDescriotion.text =
            LanguageData.getStringValue("PleaseEnterOtpToProceedFurther")

        var otpField = addDialog.findViewById<EditText>(R.id.otp_dialog_input_enter_otp)
        var otpFieldInput =
            addDialog.findViewById<TextInputLayout>(R.id.otp_dialog_layout_enter_otp)
        otpFieldInput.hint = LanguageData.getStringValue("EnterOTP")

        otpField.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.APP_OTP_LENGTH))
        otpField.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                var otp = otpField.text.toString().trim()
                var otpLenght = otp.length
                isOTPRegexMatches =
                    (otpLenght > 0 && Pattern.matches(Constants.APP_OTP_REGEX, otp))
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        addDialog.findViewById<View>(R.id.otp_dialog_yes_btn).setOnClickListener {
            var otp = otpField.text.toString().trim()
            if (otp.equals("")) {
                otpFieldInput.error = LanguageData.getStringValue("PleaseEnterValidOTP")
                otpFieldInput.isErrorEnabled = true
            } else {
                otpFieldInput.error = ""
                otpFieldInput.isErrorEnabled = false
                if(isOTPRegexMatches){
                    otpFieldInput.error = ""
                    otpFieldInput.isErrorEnabled = false

                    listner.onOTPDialogYesClickListner(otp)
                    addDialog.dismiss()
                }else{
                    otpFieldInput.error = LanguageData.getStringValue("PleaseEnterValidOTP")
                    otpFieldInput.isErrorEnabled = true
                }
            }
        }
    }

    interface OnOTPDialogClickListner {
        fun onOTPDialogYesClickListner(password: String)
    }

    //0 for Success Dialogue & 1 For Failure Dialogue
    fun successFailureDialogue(
        mContext: Context?,
        description: String?, dialogueType: Int
    ) {
        val addDialog = Dialog(mContext!!)
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addDialog.setContentView(R.layout.layout_success_failure_dialog)

        val dialogWindow = addDialog.window
        val layoutParams = dialogWindow!!.attributes
        layoutParams.x = Gravity.CENTER_HORIZONTAL
        layoutParams.y = Gravity.CENTER_VERTICAL
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = layoutParams

        val btnOk = addDialog.findViewById<MarocButton>(R.id.dialog_ok_btn)
        btnOk.text = LanguageData.getStringValue("BtnTitle_OK")

        val tvMessage = addDialog.findViewById<TextView>(R.id.dialog_description)
        tvMessage.text = description
        val image = addDialog.findViewById<ImageView>(R.id.dialog_img)
        if (dialogueType == 0) {
            image.setImageDrawable(
                ResourcesCompat.getDrawable(
                    mContext.resources,
                    R.drawable.success,
                    null
                )
            )
        } else {
            image.setImageDrawable(
                ResourcesCompat.getDrawable(
                    mContext.resources,
                    R.drawable.failed,
                    null
                )
            )
        }

        addDialog.show()
        addDialog.findViewById<View>(R.id.dialog_ok_btn).setOnClickListener {
            addDialog.dismiss()
        }
    }

    fun showChangeLanguageDialogue(
        mContext: Context?,
        listner: OnChangeLanguageClickListner
    ) {
        val addDialog = Dialog(mContext!!)
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addDialog.setContentView(R.layout.layout_change_language_dialog)

        val dialogWindow = addDialog.window
        val layoutParams = dialogWindow!!.attributes
        layoutParams.x = Gravity.CENTER_HORIZONTAL
        layoutParams.y = Gravity.CENTER_VERTICAL
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = layoutParams

        addDialog.show()
        var tvMsg = addDialog.findViewById<TextView>(R.id.language_dialog_description)
        var tvDialogTitle = addDialog.findViewById<TextView>(R.id.language_dialog_title)

        var btnYes = addDialog.findViewById<Button>(R.id.language_dialog_yes_btn)
        var rbEnglish =  addDialog.findViewById<RadioButton>(R.id.rb_English)
        var rbFrench =  addDialog.findViewById<RadioButton>(R.id.rb_French)
        var rbArabic =  addDialog.findViewById<RadioButton>(R.id.rb_Arabic)

        var radioGrp = addDialog.findViewById<RadioGroup>(R.id.language_dialog_radiogroup)
        var selectedLanguage = ""
        if (LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_EN)) {
            radioGrp.check(R.id.rb_English)
            selectedLanguage = mContext.resources.getString(R.string.language_english)
        } else if (LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_FR)) {
            radioGrp.check(R.id.rb_French)
            selectedLanguage = mContext.resources.getString(R.string.language_french)
        }
        else if (LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_AR)) {
            radioGrp.check(R.id.rb_Arabic)
            selectedLanguage = mContext.resources.getString(R.string.language_arabic)
        }


        radioGrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton =
                group.findViewById<View>(checkedId) as RadioButton
            val isChecked = checkedRadioButton.isChecked
            if (isChecked) {
                selectedLanguage = checkedRadioButton.text.toString()
            }
        })


        btnYes.text = LanguageData.getStringValue("BtnTitle_OK")
        tvDialogTitle.text = LanguageData.getStringValue("ChangeLanguage")
        tvMsg.text = LanguageData.getStringValue("PleaseChooseyourLanguage")
        rbEnglish.text = LanguageData.getStringValue("DropDown_English")
        rbFrench.text = LanguageData.getStringValue("DropDown_French")
        rbArabic.text = LanguageData.getStringValue("DropDown_Arabic")

        addDialog.findViewById<View>(R.id.language_dialog_yes_btn).setOnClickListener {
            listner.onChangeLanguageDialogYesClickListner(selectedLanguage)
            addDialog.dismiss()
        }
    }

    interface OnChangeLanguageClickListner {
        fun onChangeLanguageDialogYesClickListner(selectedLanguage: String)
    }


    fun showCustomDialogue(
        mContext: Context?,
        btnTxt: String?,
        confirmationTxt: String?,
        titleTxt: String?,
        listner: OnCustomDialogListner
    ) {
        val addDialog = Dialog(mContext!!)
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addDialog.setContentView(R.layout.layout_generic_dialog)

        val dialogWindow = addDialog.window
        val layoutParams = dialogWindow!!.attributes
        layoutParams.x = Gravity.CENTER_HORIZONTAL
        layoutParams.y = Gravity.CENTER_VERTICAL
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = layoutParams

        addDialog.show()
        var tvConfirmation = addDialog.findViewById<TextView>(R.id.custom_dialog_description)
        var tvDialogTitle = addDialog.findViewById<TextView>(R.id.custom_dialog_title)
        var btnYes = addDialog.findViewById<Button>(R.id.custom_dialog_yes_btn)


        btnYes.text = btnTxt
        tvDialogTitle.text = titleTxt
        tvConfirmation.text = confirmationTxt


        addDialog.findViewById<View>(R.id.custom_dialog_yes_btn).setOnClickListener {
            listner.onCustomDialogOkClickListner()
            addDialog.dismiss()
        }
    }

    fun showBlockedAccountDialog(
        mContext: Context?,
        btnTxtYes: String?,
        btnTxtNo: String?,
        confirmationTxt: String?,
        titleTxt: String?,
        listner: OnCustomDialogListner
    ) {
        val addDialog = Dialog(mContext!!)
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addDialog.setContentView(R.layout.dialog_blocked_account_layout)

        val dialogWindow = addDialog.window
        val layoutParams = dialogWindow!!.attributes
        layoutParams.x = Gravity.CENTER_HORIZONTAL
        layoutParams.y = Gravity.CENTER_VERTICAL
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogWindow.attributes = layoutParams

        addDialog.show()
        var tvConfirmation = addDialog.findViewById<TextView>(R.id.custom_dialog_description)
        var tvDialogTitle = addDialog.findViewById<TextView>(R.id.custom_dialog_title)
        var btnYes = addDialog.findViewById<Button>(R.id.custom_dialog_yes_btn)
        var btnNo = addDialog.findViewById<Button>(R.id.custom_dialog_no_btn)


        btnYes.text = btnTxtYes
        btnNo.text = btnTxtNo
        tvDialogTitle.text = titleTxt
        tvConfirmation.text = confirmationTxt


        addDialog.findViewById<View>(R.id.custom_dialog_yes_btn).setOnClickListener {
            listner.onCustomDialogOkClickListner()
            addDialog.dismiss()
        }

        addDialog.findViewById<View>(R.id.custom_dialog_no_btn).setOnClickListener {
            addDialog.dismiss()
        }
    }

    interface OnCustomDialogListner {
        fun onCustomDialogOkClickListner()
    }

    fun customToast(context: Activity) {
        val inflater: LayoutInflater = context.layoutInflater
        val layout: View = inflater.inflate(
            R.layout.toast_layout,
            context.findViewById(R.id.toast_layout_root) as ViewGroup?
        )

        val text = layout.findViewById<View>(R.id.text) as TextView
        text.text = context.getString(R.string.agree_terms)

        val toast =
            Toast(context.getApplicationContext())
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }

}