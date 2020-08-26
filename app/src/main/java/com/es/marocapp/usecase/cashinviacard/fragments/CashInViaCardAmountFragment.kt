package com.es.marocapp.usecase.cashinviacard.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.SeekBar
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.QuickAmountAdapter
import com.es.marocapp.databinding.FragmentCashInViaCardAmountBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.QuickAmountModel
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.cashinviacard.ActivityCashInViaCard
import com.es.marocapp.usecase.cashinviacard.CashInViaCardClickListners
import com.es.marocapp.usecase.cashinviacard.CashInViaCardViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import kotlin.math.floor
import kotlin.math.roundToInt

class CashInViaCardAmountFragment : BaseFragment<FragmentCashInViaCardAmountBinding>(),
    CashInViaCardClickListners, SeekBar.OnSeekBarChangeListener, TextWatcher {

    private lateinit var mActivityViewModel: CashInViaCardViewModel

    private lateinit var mQuickAmountAdapter: QuickAmountAdapter

    private var mQuickAmountList : ArrayList<QuickAmountModel> = arrayListOf()

    var bFirstLoad = true

    override fun setLayout(): Int {
       return R.layout.fragment_cash_in_via_card_amount
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as ActivityCashInViaCard).get(CashInViaCardViewModel::class.java)
        mDataBinding.apply {
            listner = this@CashInViaCardAmountFragment
            viewmodel = mActivityViewModel
        }

        var userBalance =
            mActivityViewModel.mBalanceInforAndResponseObserver.get()!!.balance!!.toFloat()
        var userBalanceInt = userBalance.toInt()
        mDataBinding.AmountSeekBar.max = userBalanceInt
        mDataBinding.AmountSeekBar.progress = 0

        mDataBinding.AmountSeekBar.setOnSeekBarChangeListener(this)
        mDataBinding.etAmountEntered.setOnClickListener {
            mDataBinding.etAmountEntered.isCursorVisible = true
        }
        mDataBinding.etAmountEntered.addTextChangedListener(this@CashInViaCardAmountFragment)

        mQuickAmountList.clear()
        for(quickAmount in Constants.quickAmountsList){
            mQuickAmountList.add(QuickAmountModel(quickAmount,false))
        }

        mQuickAmountAdapter = QuickAmountAdapter(activity as ActivityCashInViaCard,userBalanceInt,mQuickAmountList,
            object : QuickAmountAdapter.QuickAmountAdpterListner {
                override fun onAmountItemTypeClick(amount : String) {
                    mDataBinding.etAmountEntered.setText(amount)
                    mDataBinding.AmountSeekBar.progress = floor(amount.toDouble()).toInt()
                }
            })

        mDataBinding.quickAmountRecycler.apply {
            adapter = mQuickAmountAdapter
            layoutManager = GridLayoutManager(activity, 3)
        }


        mActivityViewModel.popBackStackTo = R.id.cashInViaCardEmailFragment

        setStrings()

        if(mActivityViewModel.showDialog){
            DialogUtils.showUpdateAPPDailog(activity,LanguageData.getStringValue("YourTransactionIsInProcess").toString(),object : DialogUtils.OnCustomDialogListner{
                override fun onCustomDialogOkClickListner() {
                    (activity as ActivityCashInViaCard).startNewActivityAndClear(
                        activity as ActivityCashInViaCard,
                        MainActivity::class.java
                    )
                }

            },R.drawable.ic_payment_pending,LanguageData.getStringValue("BtnTitle_OK").toString())
        }
    }

    private fun setStrings() {
        mDataBinding.tvAmountSelection.text = LanguageData.getStringValue("Amount")
        mDataBinding.tvQuickAmountTitle.text = LanguageData.getStringValue("QuickAmount")

        (activity as ActivityCashInViaCard).setHeaderTitle(LanguageData.getStringValue("CashInViaCardCaps").toString())

        mDataBinding.btnNext.text = LanguageData.getStringValue("BtnTitle_Next")
    }

    override fun onCashDepositClick(view: View) {

    }

    override fun onBankCardClick(view: View) {

    }

    override fun onNextButtonClick(view: View) {
        val sAmount: String = mDataBinding.etAmountEntered.text.toString().trim { it <= ' ' }

        if (sAmount == "" || SumAmountEditText() == "0" || sAmount == ".") {
            mDataBinding.etAmountEntered.error = LanguageData.getStringValue("PleaseEnterValidAmountToProceed.")
            return
        }

        mActivityViewModel.showDialog = true

        (activity as ActivityCashInViaCard).navController.navigate(R.id.action_cashInViaCardAmountFragment_to_cashInViaCashWebViewFragment)
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

    private fun SumAmountEditText(): String? {
        val dNum: Double =
            mDataBinding.etAmountEntered.text.toString().trim { it <= ' ' }.toDouble()
        val nNum = dNum.roundToInt()
        return nNum.toString()
    }

    override fun afterTextChanged(p0: Editable?) {
        var sAmount: String = mDataBinding.etAmountEntered.text.toString().trim { it <= ' ' }

        if (sAmount == "" || sAmount == ".") sAmount = "0"

        mDataBinding.etAmountEntered.setSelection(mDataBinding.etAmountEntered.text.length)
        mDataBinding.AmountSeekBar.progress = floor(sAmount.toDouble()).toInt()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}