package com.es.marocapp.usecase.transfercommision

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.QuickAmountAdapter
import com.es.marocapp.databinding.FragmentCommisionFundsAmountSelectionBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.QuickAmountModel
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.tranferfundcase.FundsTrasnferClickLisntener
import com.es.marocapp.usecase.transfercommision.TransferCommisionActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DecimalDigitsInputFilter
import com.es.marocapp.utils.DialogUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor
import kotlin.math.roundToInt

class TransferCommisionAmountFragment :
    BaseFragment<FragmentCommisionFundsAmountSelectionBinding>(),
    FundsTrasnferClickLisntener, SeekBar.OnSeekBarChangeListener, TextWatcher {

    private lateinit var mActivityViewModel: TransferCommisionViewModel

    private lateinit var mQuickAmountAdapter: QuickAmountAdapter

    lateinit var navController: NavController

    private var mQuickAmountList: ArrayList<QuickAmountModel> = arrayListOf()

    var bFirstLoad = true

    override fun setLayout(): Int {
        return R.layout.fragment_commision_funds_amount_selection
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as TransferCommisionActivity).get(TransferCommisionViewModel::class.java)
        mDataBinding.apply {
            listner = this@TransferCommisionAmountFragment
            this.viewmodel = this@TransferCommisionAmountFragment.mActivityViewModel
        }


        if (Constants.WALLETACCOUNTBALANCE.isNullOrEmpty()) {
            Constants.WALLETACCOUNTBALANCE = "0"
        }
        var userBalance =
            Constants.WALLETACCOUNTBALANCE.toFloat()
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
        mDataBinding.etAmountEntered.addTextChangedListener(this@TransferCommisionAmountFragment)

        mQuickAmountList.clear()
        for (quickAmount in Constants.quickAmountsList) {
            mQuickAmountList.add(QuickAmountModel(quickAmount, false))
        }

        mQuickAmountAdapter =
            QuickAmountAdapter(activity as TransferCommisionActivity,
                userBalanceInt,
                mQuickAmountList,
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


        mActivityViewModel.popBackStackTo = -1

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

        mDataBinding.btnNext.text = LanguageData.getStringValue("BtnTitle_Next")
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@TransferCommisionAmountFragment, Observer {
            DialogUtils.showErrorDialoge(activity as TransferCommisionActivity, it)
        })

        mActivityViewModel.getTransferResponseListner.observe(this,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    Constants.HEADERS_FOR_PAYEMNTS = false
                    mActivityViewModel.senderBalanceAfter = it.senderBalanceAfter
                    mActivityViewModel.transactionID = it.financialTransactionId
                    // (activity as TransferCommisionActivity).navController.navigate(R.id.action_fundTransferConfirmationFragment_to_fundsTrasnferSuccessFragment)
                    DialogUtils.successFailureDialogue(
                        activity as TransferCommisionActivity,
                        it.description,
                        0,
                        object : DialogUtils.OnYesClickListner {
                            override fun onDialogYesClickListner() {
                                mActivityViewModel.isUserRegistered.set(false)
                                mActivityViewModel.isFundTransferUseCase.set(false)
                                mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set(false)
                                Constants.HEADERS_FOR_PAYEMNTS = false
                                (activity as TransferCommisionActivity).startNewActivityAndClear(
                                    activity as TransferCommisionActivity,
                                    MainActivity::class.java
                                )
                            }
                        })
                } else if (it.responseCode.equals(ApiConstant.API_WRONG_PASSWORD)) {
                    DialogUtils.showErrorDialoge(activity, it.description)
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })


    }

    override fun onNextBtnClickListner(view: View) {
        var sAmount: String = mDataBinding.etAmountEntered.text.toString().trim { it <= ' ' }
        sAmount = sAmount.replace(",", ".")
        if (sAmount == "" || SumAmountEditText(sAmount) == "0" || sAmount == ".") {
            mDataBinding.etAmountEntered.error =
                LanguageData.getStringValue("PleaseEnterValidAmountToProceed.")
            return
        }
        val bill: Double = sAmount.toDouble()
        sAmount = String.format(
            Locale.US,
            "%.2f",
            (bill)
        )



        if (Constants.IS_AGENT_USER) {
            DialogUtils.showPasswordDialoge(activity as TransferCommisionActivity,
                object : DialogUtils.OnPasswordDialogClickListner {
                    override fun onDialogYesClickListner(password: String) {
                        Constants.HEADERS_FOR_PAYEMNTS = true
                        Constants.CURRENT_USER_CREDENTIAL = password
                        mActivityViewModel.requestForTransferCommisionApi(activity, sAmount)
                    }
                })
            //  mActivityViewModel.requestForTransferCommisionApi(activity, sAmount)
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
