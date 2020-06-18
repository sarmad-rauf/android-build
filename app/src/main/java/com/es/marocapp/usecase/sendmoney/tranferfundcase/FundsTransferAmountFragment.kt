package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
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
import com.es.marocapp.utils.DialogUtils
import kotlin.math.floor
import kotlin.math.roundToInt

class FundsTransferAmountFragment : BaseFragment<FragmentFundsAmountSelectionBinding>(),
    FundsTrasnferClickLisntener, SeekBar.OnSeekBarChangeListener, TextWatcher {

    private lateinit var mActivityViewModel: SendMoneyViewModel

    private lateinit var mQuickAmountAdapter: QuickAmountAdapter

    private var mQuickAmountList : ArrayList<QuickAmountModel> = arrayListOf()

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
            (activity as SendMoneyActivity).resources.getString(
                R.string.amount
            )
        )
        (activity as SendMoneyActivity).setHeaderVisibility(true)

        var userBalance =
            mActivityViewModel.mBalanceInforAndResponseObserver.get()!!.balance!!.toFloat()
        var userBalanceInt = userBalance.toInt()
        mDataBinding.AmountSeekBar.max = userBalanceInt
        mDataBinding.AmountSeekBar.progress = 0

        mDataBinding.AmountSeekBar.setOnSeekBarChangeListener(this)
        mDataBinding.etAmountEntered.setOnClickListener {
            mDataBinding.etAmountEntered.isCursorVisible = true
        }
        mDataBinding.etAmountEntered.addTextChangedListener(this@FundsTransferAmountFragment)

        for(quickAmount in Constants.quickAmountsList){
            mQuickAmountList.add(QuickAmountModel(quickAmount,false))
        }

        mQuickAmountAdapter = QuickAmountAdapter(activity as SendMoneyActivity,userBalanceInt,mQuickAmountList,
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


        mActivityViewModel.popBackStackTo = R.id.fundsTransferMsisdnFragment

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
            DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it)
        })

        mActivityViewModel.getFloatTransferQuoteResponseListner.observe(this@FundsTransferAmountFragment, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                if(it.quoteList.isNotEmpty()){
                    mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                    mActivityViewModel.qouteId = it.quoteList[0].quoteid
                }
                (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferAmountFragment_to_fundTransferConfirmationFragment)
            }else{
                DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it.description)
            }
        })

        mActivityViewModel.getTransferQouteResponseListner.observe(this@FundsTransferAmountFragment, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                if(it.quoteList.isNotEmpty()){
                   mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                    mActivityViewModel.qouteId = it.quoteList[0].quoteid
                }
                (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferAmountFragment_to_fundTransferConfirmationFragment)
            }else{
                DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it.description)
            }
        })

        mActivityViewModel.getMerchantQouteResponseListner.observe(this@FundsTransferAmountFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(it.quoteList.isNotEmpty()){
                        mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                        mActivityViewModel.qouteId = it.quoteList[0].quoteid
                    }
                    (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferAmountFragment_to_fundTransferConfirmationFragment)
                }else{
                    DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it.description)
                }
            })

        mActivityViewModel.getPaymentQouteResponseListner.observe(this@FundsTransferAmountFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(it.quoteList.isNotEmpty()){
                        mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                        mActivityViewModel.qouteId = it.quoteList[0].quoteid
                    }
                    (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferAmountFragment_to_fundTransferConfirmationFragment)
                }else{
                    DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it.description)
                }
            })
    }

    override fun onNextClickListner(view: View) {
        val sAmount: String = mDataBinding.etAmountEntered.text.toString().trim { it <= ' ' }

        if (sAmount == "" || SumAmountEditText() == "0" || sAmount == ".") {
            mDataBinding.etAmountEntered.error = "Please enter valid amount to proceed."
            return
        }
        if(Constants.IS_AGENT_USER){
            if(mActivityViewModel.isFundTransferUseCase.get()!!){
                mActivityViewModel.requestForFloatTransferQuoteApi(activity,sAmount)
            }

            if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
                if(mActivityViewModel.isUserRegistered.get()!!){
                    if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
                        mActivityViewModel.requestFoMerchantQouteApi(activity,sAmount)
                    }
                }else{
                    if(mActivityViewModel.isAccountHolderInformationFailed.get()!!){
                        mActivityViewModel.requestForSimplePaymentQouteApi(activity,sAmount,Constants.CURRENT_USER_MSISDN)
                    }else{
                        mActivityViewModel.requestFoPaymentQouteApi(activity,sAmount,Constants.CURRENT_USER_MSISDN)
                    }
                }
            }
        }else{
            if(mActivityViewModel.isUserRegistered.get()!!){
                if(mActivityViewModel.isFundTransferUseCase.get()!!){
                    mActivityViewModel.requestFoTransferQouteApi(activity,sAmount)
                }

                if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
                    mActivityViewModel.requestFoMerchantQouteApi(activity,sAmount)
                }
            }else{
                if(mActivityViewModel.isAccountHolderInformationFailed.get()!!){
                    mActivityViewModel.requestForSimplePaymentQouteApi(activity,sAmount,Constants.CURRENT_USER_MSISDN)
                }else{
                    mActivityViewModel.requestFoPaymentQouteApi(activity,sAmount,Constants.CURRENT_USER_MSISDN)
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
