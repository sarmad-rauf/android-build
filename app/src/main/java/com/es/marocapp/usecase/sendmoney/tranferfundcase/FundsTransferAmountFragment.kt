package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.QuickAmountAdapter
import com.es.marocapp.databinding.FragmentFundsAmountSelectionBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.QuickAmountModel
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DecimalDigitsInputFilter
import com.es.marocapp.utils.DialogUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor
import kotlin.math.roundToInt

class FundsTransferAmountFragment : BaseFragment<FragmentFundsAmountSelectionBinding>(),
    FundsTrasnferClickLisntener, SeekBar.OnSeekBarChangeListener, TextWatcher {

    private lateinit var mActivityViewModel: SendMoneyViewModel

    private lateinit var mQuickAmountAdapter: QuickAmountAdapter

    private var mQuickAmountList: ArrayList<QuickAmountModel> = arrayListOf()

    var bFirstLoad = true

    override fun setLayout(): Int {
        return R.layout.fragment_funds_amount_selection
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
            listner = this@FundsTransferAmountFragment
            viewmodel = mActivityViewModel
        }

        (activity as SendMoneyActivity).setHeaderTitle(
            LanguageData.getStringValue("Amount").toString()
        )
        (activity as SendMoneyActivity).setHeaderVisibility(true)

        var userBalance =
            mActivityViewModel.mBalanceInforAndResponseObserver.get()!!.balance!!.toFloat()
        var userBalanceInt = userBalance.toInt()
        mDataBinding.plusAmountTotal.setOnClickListener {
            var currentBalance = mDataBinding.etAmountEntered.text.toString()
            if (currentBalance.isEmpty()) {
                currentBalance = "0.00"
            }
            var currentBalanceDouble = currentBalance.toDouble()
            var currentBalanceInt = currentBalanceDouble.toInt()
            var newAmount = currentBalanceInt + 1
            if (newAmount <= userBalance) {
                mDataBinding.etAmountEntered.setText(newAmount.toString())
            }
        }

        mDataBinding.minuAmountTotal.setOnClickListener {
            var currentBalance = mDataBinding.etAmountEntered.text.toString()
            if (currentBalance.isEmpty()) {
                currentBalance = "0.00"
            }
            var currentBalanceDouble = currentBalance.toDouble()
            var currentBalanceInt = currentBalanceDouble.toInt()
            var newAmount = 0
            if (currentBalanceInt != 0) {
                newAmount = currentBalanceInt - 1
            }

            mDataBinding.etAmountEntered.setText(newAmount.toString())
        }
        mDataBinding.AmountSeekBar.max = userBalanceInt
        mDataBinding.AmountSeekBar.progress = 0

        mDataBinding.AmountSeekBar.setOnSeekBarChangeListener(this)
        mDataBinding.etAmountEntered.setOnClickListener {
            mDataBinding.etAmountEntered.isCursorVisible = true
        }
        mDataBinding.etAmountEntered.addTextChangedListener(this@FundsTransferAmountFragment)

        mQuickAmountList.clear()
        for (quickAmount in Constants.quickAmountsList) {
            mQuickAmountList.add(QuickAmountModel(quickAmount, false))
        }

        mQuickAmountAdapter =
            QuickAmountAdapter(activity as SendMoneyActivity, userBalanceInt, mQuickAmountList,
                object : QuickAmountAdapter.QuickAmountAdpterListner {
                    override fun onAmountItemTypeClick(amount: String) {
                        mDataBinding.etAmountEntered.setText(amount)
                        mDataBinding.AmountSeekBar.progress = floor(amount.toDouble()).toInt()
                    }
                })

        mDataBinding.quickAmountRecycler.apply {
            adapter = mQuickAmountAdapter
            layoutManager = GridLayoutManager(activity, 3)
        }


        mActivityViewModel.popBackStackTo = R.id.fundsTransferMsisdnFragment

        mDataBinding.etAmountEntered.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(2))

        if (!mActivityViewModel.amountScannedFromQR.equals("0")) {
            mDataBinding.etAmountEntered.setText(mActivityViewModel.amountScannedFromQR)


            //for SeekBar
            var sAmount: String = mDataBinding.etAmountEntered.text.toString().trim { it <= ' ' }

            if (sAmount == "" || sAmount == ".") sAmount = "0"
            sAmount = sAmount.replace(",", ".")
            mDataBinding.etAmountEntered.setSelection(mDataBinding.etAmountEntered.text.length)
            mDataBinding.AmountSeekBar.progress = floor(sAmount.toDouble()).toInt()
        }

        setStrings()
        subscribeObserver()
    }

    private fun setStrings() {
        mDataBinding.tvAmountSelection.text = LanguageData.getStringValue("Amount")
        mDataBinding.tvQuickAmountTitle.text = LanguageData.getStringValue("QuickAmount")
        (activity as SendMoneyActivity).setHeaderTitle(
            LanguageData.getStringValue("Amount").toString()
        )

        mDataBinding.btnNext.text = LanguageData.getStringValue("BtnTitle_Next")
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@FundsTransferAmountFragment, Observer {
            DialogUtils.showErrorDialoge(activity as SendMoneyActivity, it)
        })

        mActivityViewModel.getFloatTransferQuoteResponseListner.observe(
            this@FundsTransferAmountFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.quoteList.isNotEmpty()) {
                        mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                        mActivityViewModel.qouteId = it.quoteList[0].quoteid
                    }
                    (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferAmountFragment_to_fundTransferConfirmationFragment)
                } else {
                    DialogUtils.showErrorDialoge(activity as SendMoneyActivity, it.description)
                }
            })

        mActivityViewModel.getTransferQouteResponseListner.observe(
            this@FundsTransferAmountFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.quoteList.isNotEmpty()) {
                        mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                        mActivityViewModel.qouteId = it.quoteList[0].quoteid
                    }
                    (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferAmountFragment_to_fundTransferConfirmationFragment)
                } else {
                    DialogUtils.showErrorDialoge(activity as SendMoneyActivity, it.description)
                }
            })

        mActivityViewModel.getMerchantQouteResponseListner.observe(this@FundsTransferAmountFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.quoteList.isNotEmpty()) {
                        mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                        mActivityViewModel.qouteId = it.quoteList[0].quoteid
                    }
                    (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferAmountFragment_to_fundTransferConfirmationFragment)
                } else {
                    DialogUtils.showErrorDialoge(activity as SendMoneyActivity, it.description)
                }
            })

        mActivityViewModel.getPaymentQouteResponseListner.observe(this@FundsTransferAmountFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.quoteList.isNotEmpty()) {
                        mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                        mActivityViewModel.qouteId = it.quoteList[0].quoteid
                    }
                    (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferAmountFragment_to_fundTransferConfirmationFragment)
                } else {
                    DialogUtils.showErrorDialoge(activity as SendMoneyActivity, it.description)
                }
            })
    }

    override fun onNextClickListner(view: View) {
        var sAmount: String = mDataBinding.etAmountEntered.text.toString().trim { it <= ' ' }
        sAmount = sAmount.replace(",", ".")

        val bill: Double = sAmount.toDouble()
        sAmount = String.format(
            Locale.US,
            "%.2f",
            (bill)
        )

        if (sAmount == "" || SumAmountEditText(sAmount) == "0" || sAmount == ".") {
            mDataBinding.etAmountEntered.error =
                LanguageData.getStringValue("PleaseEnterValidAmountToProceed.")
            return
        }

        if (Constants.IS_AGENT_USER) {
            if (mActivityViewModel.isFundTransferUseCase.get()!!) {
                mActivityViewModel.requestForFloatTransferQuoteApi(activity, sAmount)
            }

            if (mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!) {
                if (mActivityViewModel.isUserRegistered != null && mActivityViewModel.isUserRegistered.get() != null && mActivityViewModel.isUserRegistered.get()!!) {
                    if (mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!) {
                        mActivityViewModel.requestFoMerchantQouteApi(activity, sAmount)
                    }
                } else {
                    if (mActivityViewModel.isAccountHolderInformationFailed.get()!!) {
                        mActivityViewModel.requestForSimplePaymentQouteApi(
                            activity,
                            sAmount,
                            Constants.CURRENT_USER_MSISDN
                        )
                    } else {
                        mActivityViewModel.requestFoPaymentQouteApi(
                            activity,
                            sAmount,
                            Constants.CURRENT_USER_MSISDN
                        )
                    }
                }
            }
        } else {
            if (mActivityViewModel.isUserRegistered != null && mActivityViewModel.isUserRegistered.get() != null && mActivityViewModel.isUserRegistered.get()!!) {
                // If user is register on EWP
                if (mActivityViewModel.isFundTransferUseCase.get()!!) {
                    // Send Money
                    mActivityViewModel.requestFoTransferQouteApi(activity, sAmount)
                }

                if (mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!) {
                    // Merchant
                    mActivityViewModel.requestFoMerchantQouteApi(activity, sAmount)
                }
            } else {
                if (mActivityViewModel.isAccountHolderInformationFailed.get()!!) {
                    mActivityViewModel.requestForSimplePaymentQouteApi(
                        activity,
                        sAmount,
                        Constants.CURRENT_USER_MSISDN
                    )
                } else {
                    mActivityViewModel.requestFoPaymentQouteApi(
                        activity,
                        sAmount,
                        Constants.CURRENT_USER_MSISDN
                    )
                }
            }
        }
    }

    override fun onBackClickListner(view: View) {

    }

    override fun onProgressChanged(p0: SeekBar?, nVal: Int, p2: Boolean) {
        if (bFirstLoad) {
            mDataBinding.etAmountEntered.hint = "0"
            bFirstLoad = false
            return
        }

        val sAmount: String = nVal.toString()
        mDataBinding.etAmountEntered.setText(sAmount)

    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

    private fun SumAmountEditText(sAmount: String): String? {
        val dNum: Double =
            sAmount.trim { it <= ' ' }.toDouble()
        val nNum = dNum.roundToInt()
        return nNum.toString()
    }

    override fun afterTextChanged(p0: Editable?) {
        var sAmount: String = mDataBinding.etAmountEntered.text.toString().trim { it <= ' ' }

        if (sAmount == "" || sAmount == ".") sAmount = "0"
        sAmount = sAmount.replace(",", ".")
        mDataBinding.etAmountEntered.setSelection(mDataBinding.etAmountEntered.text.length)
        mDataBinding.AmountSeekBar.progress = floor(sAmount.toDouble()).toInt()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }
}
