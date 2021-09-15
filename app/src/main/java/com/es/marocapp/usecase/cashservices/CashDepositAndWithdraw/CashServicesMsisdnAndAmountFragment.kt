package com.es.marocapp.usecase.cashservices.CashDepositAndWithdraw

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentCashServicesNumberAmountBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.cashservices.CashServicesActivity
import com.es.marocapp.usecase.cashservices.CashServicesClickListner
import com.es.marocapp.usecase.cashservices.CashServicesViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DecimalDigitsInputFilter
import com.es.marocapp.utils.DialogUtils
import java.util.regex.Pattern
import kotlin.math.roundToInt

class CashServicesMsisdnAndAmountFragment : BaseFragment<FragmentCashServicesNumberAmountBinding>(),
    CashServicesClickListner, TextWatcher {

    private lateinit var mActivityViewModel: CashServicesViewModel

    var msisdnEntered = ""
    var isNumberRegexMatches = false
    var isErrorEnabled = false

    override fun setLayout(): Int {
        return R.layout.fragment_cash_services_number_amount
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as CashServicesActivity).get(CashServicesViewModel::class.java)
        mDataBinding.apply {
            listner = this@CashServicesMsisdnAndAmountFragment
            viewmodel = mActivityViewModel
        }

        mActivityViewModel.popBackStackTo = R.id.cashServicesTypeFragment

        mActivityViewModel.trasferTypeSelected.get()?.let {
            (activity as CashServicesActivity).setHeaderTitle(
                it
            )
        }
        (activity as CashServicesActivity).setHeaderVisibility(true)
        //todo also here remove lenght-2 check in max line
        mDataBinding.inputPhoneNumber.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Constants.APP_MSISDN_LENGTH.toInt() - 2
            )
        )

        mDataBinding.inputPhoneNumber.addTextChangedListener(this)

        mDataBinding.inputPhoneNumber.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("EnterReceiversMobileNumber")
                mDataBinding.inputPhoneNumberHint.visibility = View.GONE
            } else {
                if (mDataBinding.inputLayoutPhoneNumber.isErrorEnabled) {
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterReceiversMobileNumber")
                }else{
                    if (mDataBinding.inputPhoneNumber.text.isEmpty()) {
                        mDataBinding.inputPhoneNumberHint.visibility = View.VISIBLE
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("MSISDNPlaceholder")

                    } else {
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("EnterReceiversMobileNumber")
                    }
                }
            }
        }
        mDataBinding.inputAmount.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(2))

        subscribeObserver()
        setStrings()
    }

    private fun setStrings() {
        mDataBinding.inputLayoutAmount.hint = LanguageData.getStringValue("EnterAmount")
        mDataBinding.inputNote.hint = LanguageData.getStringValue("Note")
        mDataBinding.btnNext.text = LanguageData.getStringValue("BtnTitle_Next")
        mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
        mDataBinding.inputPhoneNumberHint.text =
            LanguageData.getStringValue("EnterReceiversMobileNumber")
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@CashServicesMsisdnAndAmountFragment, Observer {
            DialogUtils.showErrorDialoge(activity, it)
        })

        mActivityViewModel.getInitiateTrasnferQuoteResponseListner.observe(this@CashServicesMsisdnAndAmountFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    mActivityViewModel.totalTax=0.0
                    if (it.quoteList.isNotEmpty()) {
                        for(taxes in it.taxList.indices)
                        {
                            mActivityViewModel.totalTax=mActivityViewModel.totalTax+it.taxList[taxes].amount.amount.toString().toDouble()
                        }
                        mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                        mActivityViewModel.qouteId = it.quoteList[0].quoteid
                    }
                    mActivityViewModel.isOTPFlow.set(false)
                    (activity as CashServicesActivity).navController.navigate(R.id.action_cashMsisdnAndAmountFragment_to_cashServicesConfirmationFragment)
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })

        mActivityViewModel.getGenerateOtpResponseListner.observe(this@CashServicesMsisdnAndAmountFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    mActivityViewModel.isOTPFlow.set(true)
                    DialogUtils.showOTPDialogue(activity,false,
                        object : DialogUtils.OnOTPDialogClickListner {
                            override fun onOTPDialogYesClickListner(otp: String) {
                                mActivityViewModel.requestForCashInQouteApi(
                                    activity
                                )
                            }

                            override fun onOTPDialogNoClickListner() {

                            }

                        })
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })

        mActivityViewModel.getCashInWithOtpQuoteResponseListner.observe(this@CashServicesMsisdnAndAmountFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.quoteList.isNotEmpty()) {
                        mActivityViewModel.totalTax=0.0
                        for(taxes in it.taxList.indices)
                        {
                            mActivityViewModel.totalTax=mActivityViewModel.totalTax+it.taxList[taxes].amount.amount.toString().toDouble()
                        }
                        mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                        mActivityViewModel.qouteId = it.quoteList[0].quoteid
                    }

                    (activity as CashServicesActivity).navController.navigate(R.id.action_cashMsisdnAndAmountFragment_to_cashServicesConfirmationFragment)
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })
    }

    override fun onNextBtnClickListner(view: View) {
        if (isValidForAll()) {
            if (mActivityViewModel.isDepositUseCase.get()!!) {
//                mActivityViewModel.requestForGenerateOtpApi(
//                    activity, msisdnEntered, mDataBinding.inputAmount.text.toString().trim(),
//                    mDataBinding.inputNote.text.toString().trim()
//                )
                mActivityViewModel.setInputValues(msisdnEntered, mDataBinding.inputAmount.text.toString().trim(),
                   mDataBinding.inputNote.text.toString().trim())
                mActivityViewModel.requestForCashInQouteApi(
                    activity
                )
            }

            if (mActivityViewModel.isWithdrawUseCase.get()!!) {
                mActivityViewModel.requestForCashoutQouteApi(
                    activity, mDataBinding.inputAmount.text.toString().trim(),
                    msisdnEntered,
                    mDataBinding.inputNote.text.toString().trim()
                )
            }
        }
    }

    override fun onBackClickListner(view: View) {

    }

    private fun isValidForAll(): Boolean {
        var isValidForAll = true

        if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() && mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
            isValidForAll = false
            isErrorEnabled = isValidForAll
            mDataBinding.inputLayoutPhoneNumber.error =
                LanguageData.getStringValue("PleaseEnterValidMobileNumber")
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
            mDataBinding.inputLayoutPhoneNumber.hint =
                LanguageData.getStringValue("EnterReceiversMobileNumber")
            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
        } else {
            isErrorEnabled = isValidForAll
            mDataBinding.inputLayoutPhoneNumber.error = ""
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

            var userMsisdn = mDataBinding.inputPhoneNumber.text.toString()
            if (userMsisdn.startsWith("0", false)) {
                mDataBinding.inputLayoutPhoneNumber.error = ""
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                var userMSISDNwithPrefix = userMsisdn.removePrefix("0")
                userMSISDNwithPrefix = Constants.APP_MSISDN_PREFIX + userMSISDNwithPrefix
                userMSISDNwithPrefix = userMSISDNwithPrefix.removePrefix("+")

                if (isNumberRegexMatches) {
                    isErrorEnabled = isValidForAll
                    mDataBinding.inputLayoutPhoneNumber.error = ""
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                    msisdnEntered = userMSISDNwithPrefix
                } else {
                    isValidForAll = false
                    isErrorEnabled = isValidForAll
                    mDataBinding.inputLayoutPhoneNumber.error =
                        LanguageData.getStringValue("PleaseEnterValidMobileNumber")
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterReceiversMobileNumber")
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                }
            } else {
                isValidForAll = false
                isErrorEnabled = isValidForAll
                mDataBinding.inputLayoutPhoneNumber.error =
                    LanguageData.getStringValue("PleaseEnterValidMobileNumber")
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("EnterReceiversMobileNumber")
                mDataBinding.inputPhoneNumberHint.visibility = View.GONE
            }
        }

        if (mDataBinding.inputAmount.text.isNullOrEmpty()) {
            isValidForAll = false
            mDataBinding.inputLayoutAmount.error =
                LanguageData.getStringValue("PleaseEnterValidAmount")
            mDataBinding.inputLayoutAmount.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutAmount.error = ""
            mDataBinding.inputLayoutAmount.isErrorEnabled = false

            val sAmount: String = mDataBinding.inputAmount.text.toString().trim { it <= ' ' }

            if (sAmount == "" || SumAmountEditText() == "0" || sAmount == ".") {
                isValidForAll = false
                mDataBinding.inputLayoutAmount.error =
                    LanguageData.getStringValue("PleaseEnterValidAmountToProceed.")
                mDataBinding.inputLayoutAmount.isErrorEnabled = true
            } else {
                mDataBinding.inputLayoutAmount.error = ""
                mDataBinding.inputLayoutAmount.isErrorEnabled = false
            }
        }

        return isValidForAll
    }

    private fun SumAmountEditText(): String? {
        val dNum: Double =
            mDataBinding.inputAmount.text.toString().trim { it <= ' ' }.toDouble()
        val nNum = dNum.roundToInt()
        return nNum.toString()
    }

    override fun afterTextChanged(p0: Editable?) {
        var msisdn = mDataBinding.inputPhoneNumber.text.toString().trim()
        var msisdnLenght = msisdn.length
        isNumberRegexMatches =
            !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_MSISDN_REGEX, msisdn))
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}