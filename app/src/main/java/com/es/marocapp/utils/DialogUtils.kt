package com.es.marocapp.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.es.marocapp.R
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
        tvMessage.text = description
        addDialog.show()
        addDialog.findViewById<View>(R.id.error_dialog_ok_btn).setOnClickListener {
            addDialog.dismiss()
        }

        val handler = Handler()
        val runnable = Runnable {
            if (addDialog != null && addDialog.isShowing) {
                try {
                    addDialog.dismiss()
                } catch (ex: Exception) {
                }
            }
        }
        addDialog.setOnDismissListener { handler.removeCallbacks(runnable) }
        handler.postDelayed(runnable, 7000)
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
        addDialog.findViewById<View>(R.id.password_dialog_no_btn).setOnClickListener {
            addDialog.dismiss()
        }
        var passwordField  = addDialog.findViewById<EditText>(R.id.password_dialog_input_enter_password)
        var passwordFieldInput  = addDialog.findViewById<TextInputLayout>(R.id.password_dialog_layout_enter_password)
        addDialog.findViewById<View>(R.id.password_dialog_yes_btn).setOnClickListener {
            var password = passwordField.text.toString().trim()
            if(password.equals("")){
                passwordFieldInput.error = "Please Enter Valid Password"
                passwordFieldInput.isErrorEnabled = true
            }else{
                passwordFieldInput.error = ""
                passwordFieldInput.isErrorEnabled = false
                listner.onDialogYesClickListner(password)
            }
        }
    }

    interface OnPasswordDialogClickListner{
        fun onDialogYesClickListner(password : String)
    }
}