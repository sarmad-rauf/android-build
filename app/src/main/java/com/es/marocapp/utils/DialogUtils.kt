package com.es.marocapp.utils

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.es.marocapp.R
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.widgets.MarocButton
import com.google.android.material.textfield.TextInputLayout

object DialogUtils{

    fun showErrorDialoge(
        mContext: Context?,
        description: String?
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

    fun showPasswordDialoge(
        mContext: Context?,
        listner : OnPasswordDialogClickListner
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

        tvDescription.text = LanguageData.getStringValue("EnterPasswrodToProceed")
        tvTitle.text = LanguageData.getStringValue("DearCustomer")

        var passwordField  = addDialog.findViewById<EditText>(R.id.password_dialog_input_enter_password)
        var passwordFieldInput  = addDialog.findViewById<TextInputLayout>(R.id.password_dialog_layout_enter_password)
        passwordFieldInput.hint = LanguageData.getStringValue("EnterPassword")
        btnYes.setOnClickListener {
            var password = passwordField.text.toString().trim()
            if(password.equals("")){
                passwordFieldInput.error = LanguageData.getStringValue("PleaseEnterValidPassword")
                passwordFieldInput.isErrorEnabled = true
            }else{
                passwordFieldInput.error = ""
                passwordFieldInput.isErrorEnabled = false
                listner.onDialogYesClickListner(password)
                addDialog.dismiss()
            }
        }
    }

    interface OnPasswordDialogClickListner{
        fun onDialogYesClickListner(password : String)
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
        var tvConfirmation=addDialog.findViewById<TextView>(R.id.confirmation_dialog_description)
        var tvDialogTitle=addDialog.findViewById<TextView>(R.id.confirmation_dialog_title)
        var btnNO = addDialog.findViewById<Button>(R.id.confirmation_dialog_no_btn)
        var btnYes = addDialog.findViewById<Button>(R.id.confirmation_dialog_yes_btn)


        btnNO.text = LanguageData.getStringValue("BtnTitle_No")
        btnYes.text = LanguageData.getStringValue("BtnTitle_Yes")
        tvDialogTitle.text = LanguageData.getStringValue("DearCustomer")
        tvConfirmation.text=confirmationTxt

        addDialog.findViewById<View>(R.id.confirmation_dialog_no_btn).setOnClickListener {
            addDialog.dismiss()
        }
        addDialog.findViewById<View>(R.id.confirmation_dialog_yes_btn).setOnClickListener {
            listner.onDialogYesClickListner()
            addDialog.dismiss()
        }
    }

    interface OnConfirmationDialogClickListner{
        fun onDialogYesClickListner()
    }

    fun showOTPDialogue(
        mContext: Context?,
        listner : OnOTPDialogClickListner
    ) {
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
        var otpDialogFieldDescriotion  = addDialog.findViewById<TextView>(R.id.otp_dialog_description)
        var otpDialogFieldTitle  = addDialog.findViewById<TextView>(R.id.otp_dialog_title)

        btnNO.text = LanguageData.getStringValue("BtnTitle_No")
        btnYes.text = LanguageData.getStringValue("BtnTitle_Yes")
        otpDialogFieldTitle.text = LanguageData.getStringValue("DearCustomer")
        otpDialogFieldDescriotion.text = LanguageData.getStringValue("PleaseEnterOtpToProceedFurther")

        var otpField  = addDialog.findViewById<EditText>(R.id.otp_dialog_input_enter_otp)
        var otpFieldInput  = addDialog.findViewById<TextInputLayout>(R.id.otp_dialog_layout_enter_otp)
        otpFieldInput.hint = LanguageData.getStringValue("EnterOTP")

        addDialog.findViewById<View>(R.id.otp_dialog_yes_btn).setOnClickListener {
            var otp = otpField.text.toString().trim()
            if(otp.equals("")){
                otpFieldInput.error = LanguageData.getStringValue("PleaseEnterValidOTP")
                otpFieldInput.isErrorEnabled = true
            }else{
                otpFieldInput.error = ""
                otpFieldInput.isErrorEnabled = false
                listner.onOTPDialogYesClickListner(otp)
                addDialog.dismiss()
            }
        }
    }

    interface OnOTPDialogClickListner{
        fun onOTPDialogYesClickListner(password : String)
    }

    //0 for Success Dialogue & 1 For Failure Dialogue
    fun successFailureDialogue(
        mContext: Context?,
        description: String?,dialogueType:Int
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
        if(dialogueType==0) {
            image.setImageDrawable(
                ResourcesCompat.getDrawable(
                    mContext.resources,
                    R.drawable.success,
                    null
                )
            )
        }
        else{
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
}