package com.es.marocapp.usecase.cashinviacard.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityCashInViaCardBinding
import com.es.marocapp.databinding.FragmentCashInViaCardEmailBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.cashinviacard.ActivityCashInViaCard
import com.es.marocapp.usecase.cashinviacard.CashInViaCardClickListners
import com.es.marocapp.usecase.cashinviacard.CashInViaCardViewModel
import com.es.marocapp.usecase.cashservices.CashServicesActivity
import com.es.marocapp.usecase.cashservices.CashServicesViewModel
import com.es.marocapp.utils.DialogUtils

class CashInViaCardEmailFragment : BaseFragment<FragmentCashInViaCardEmailBinding>(),
    CashInViaCardClickListners {

    lateinit var mActivityViewModel: CashInViaCardViewModel
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

    override fun setLayout(): Int {
        return R.layout.fragment_cash_in_via_card_email
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as ActivityCashInViaCard).get(CashInViaCardViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listner = this@CashInViaCardEmailFragment
        }

        mActivityViewModel.popBackStackTo = -1

        (activity as ActivityCashInViaCard).setHeaderTitle(LanguageData.getStringValue("CashInViaCardCaps").toString())

        setStrings()
    }

    private fun setStrings() {
       // mDataBinding.inputLayoutEnterEmail.hint = LanguageData.getStringValue("EnterYourEmail")
        mDataBinding.btnViaCashDeposit.text = LanguageData.getStringValue("ViaCashDeposit")
        mDataBinding.btnViaBankCard.text = LanguageData.getStringValue("ViaBankCard")
    }

    fun isEmailValid() : Boolean{

        var isValidEmail = true

        if(mDataBinding.inputEnterEmail.text.isNullOrEmpty() || !mDataBinding.inputEnterEmail.text.trim().matches(emailPattern)){
            isValidEmail = false
            if(mDataBinding.inputEnterEmail.text.isNullOrEmpty()) {
                mDataBinding.inputLayoutEnterEmail.error =
                    LanguageData.getStringValue("PleaseEnterEmailAddress")
            }
            else{
                mDataBinding.inputLayoutEnterEmail.error =
                    LanguageData.getStringValue("PleaseEnterValidEmail")
            }
            mDataBinding.inputLayoutEnterEmail.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutEnterEmail.error = ""
            mDataBinding.inputLayoutEnterEmail.isErrorEnabled = false
        }

        return  isValidEmail

    }



    override fun onCashDepositClick(view: View) {
        DialogUtils.showUpdateAPPDailog(activity,LanguageData.getStringValue("CashInViaCardAlertMessage"),object : DialogUtils.OnCustomDialogListner{
            override fun onCustomDialogOkClickListner() {

            }

        },R.drawable.update_blue)
    }

    override fun onBankCardClick(view: View) {
        (activity as ActivityCashInViaCard).navController.navigate(R.id.action_cashInViaCardEmailFragment_to_cashInViaCardWebviewFragment)
    }

    override fun onNextButtonClick(view: View) {

    }

}