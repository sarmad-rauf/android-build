package com.es.marocapp.usecase.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.es.marocapp.R
import com.es.marocapp.adapter.HomeCardAdapter
import com.es.marocapp.adapter.HomeUseCasesAdapter
import com.es.marocapp.databinding.FragmentHomeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.model.HomeUseCasesModel
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.cashservices.CashServicesActivity
import com.es.marocapp.usecase.consumerregistration.ConsumerRegistrationActivity
import com.es.marocapp.usecase.payments.PaymentsActivity
import com.es.marocapp.usecase.qrcode.GenerateQrActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class HomeFragment : BaseFragment<FragmentHomeBinding>(), ViewPager.OnPageChangeListener,
    HomeFragmentClickListners {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mCardAdapter: HomeCardAdapter
    private lateinit var mUseCasesAdapter: HomeUseCasesAdapter
    var referenceNumber="";
    var quickRechargeSelectedAmount = ""

    override fun setLayout(): Int {
        return R.layout.fragment_home
    }

    override fun init(savedInstanceState: Bundle?) {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        mDataBinding.apply {
            viewmodel = homeViewModel
            listener = this@HomeFragment
        }

        homeViewModel.text.observe(this, Observer {
        })

        (activity as MainActivity).setHomeToolbarVisibility(true)

        populateHomeCardView()
        populateHomeUseCase()

        if(Constants.IS_CONSUMER_USER || Constants.IS_MERCHANT_USER){
        homeViewModel.requestForAccountHolderAddtionalInformationApi(context)
        subscribeForDefaultAccountStatus()
        subscribeForSetDefaultAccountStatus()
        subscribeForVerifyOTPForSetDefaultAccountStatus()
        }

        setStrings()
        setQuickAmountListner()
    }

    private fun setQuickAmountListner() {
        mDataBinding.btnQuickRecharge1.setOnClickListener {
            mDataBinding.btnQuickRecharge1.setBackgroundResource(R.drawable.button_quick_recharge_state)
            mDataBinding.btnQuickRecharge2.setBackgroundResource(R.drawable.button_quick_recharge)
            mDataBinding.btnQuickRecharge3.setBackgroundResource(R.drawable.button_quick_recharge)
            quickRechargeSelectedAmount = mDataBinding.btnQuickRecharge1.text.toString().removePrefix("DH").trim()
        }

        mDataBinding.btnQuickRecharge2.setOnClickListener {
            mDataBinding.btnQuickRecharge2.setBackgroundResource(R.drawable.button_quick_recharge_state)
            mDataBinding.btnQuickRecharge1.setBackgroundResource(R.drawable.button_quick_recharge)
            mDataBinding.btnQuickRecharge3.setBackgroundResource(R.drawable.button_quick_recharge)
            quickRechargeSelectedAmount = mDataBinding.btnQuickRecharge2.text.toString().removePrefix("DH").trim()
        }

        mDataBinding.btnQuickRecharge3.setOnClickListener {
            mDataBinding.btnQuickRecharge3.setBackgroundResource(R.drawable.button_quick_recharge_state)
            mDataBinding.btnQuickRecharge1.setBackgroundResource(R.drawable.button_quick_recharge)
            mDataBinding.btnQuickRecharge2.setBackgroundResource(R.drawable.button_quick_recharge)
            quickRechargeSelectedAmount = mDataBinding.btnQuickRecharge3.text.toString().removePrefix("DH").trim()
        }

        mDataBinding.btnQuickRecharge4.setOnClickListener {
            var intent = Intent(
                activity as MainActivity,
                AirTimeActivity::class.java
            )
            intent.putExtra("isQuickRechargeCase",true)
            intent.putExtra("quickRechargeAmount",quickRechargeSelectedAmount)
            startActivity(intent)
        }
    }

    private fun setStrings() {
        mDataBinding.textTitleQuickRecharge.text = LanguageData.getStringValue("QuickRecharge")
        mDataBinding.btnQuickRecharge4.text = LanguageData.getStringValue("Recharge")

        if(Constants.quickRechargeAmountsList.isNotEmpty()){
            for(i in Constants.quickAmountsList.indices){
                var quickAmount = Constants.quickRechargeAmountsList[i].substringBefore("DH")
                quickAmount = quickAmount.substringBefore(" ").trim()
                quickAmount = quickAmount.removeSuffix("DH")
                when(i){
                    0 -> mDataBinding.btnQuickRecharge1.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+quickAmount
                    1 -> mDataBinding.btnQuickRecharge2.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+quickAmount
                    2 -> mDataBinding.btnQuickRecharge3.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW+quickAmount
                }

            }
        }
    }

    private fun subscribeForDefaultAccountStatus() {

        homeViewModel.getAccountHolderAdditionalInfoResponseListner.observe(this@HomeFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.additionalinformation.isNullOrEmpty()) {
                        showPopUp()
                    } else {
                        if (it.additionalinformation[0].value.equals("FALSE", true)) {
                            showPopUp()
                        }
                    }
                }else{
                    DialogUtils.showErrorDialoge(activity ,it.description)
                }
            }
        )
    }

    private fun subscribeForSetDefaultAccountStatus() {

        homeViewModel.setDefaultAccountResponseListener.observe(this@HomeFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    referenceNumber=it.referenceNumber
                  showOTPdialogue()
                }else{
                    DialogUtils.showErrorDialoge(activity ,it.description)
                }
            }
        )
    }

    private fun subscribeForVerifyOTPForSetDefaultAccountStatus() {

        homeViewModel.verifyOTPForDefaultAccountResponseListener.observe(this@HomeFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    DialogUtils.successFailureDialogue(context,LanguageData.getStringValue("OperationPerformedSuccessfullyDot"),0)
                }else{
                    DialogUtils.successFailureDialogue(context,LanguageData.getStringValue("FailedToPerformOperationDot"),1)
                }
            }
        )
    }

    private fun showPopUp() {
        val confirmationTxt= LanguageData.getStringValue("DoYouWantToChooseThisMwalletMtCashDefaultForDoingOperationsQuestion")
        DialogUtils.showConfirmationDialogue(confirmationTxt!!,activity,object : DialogUtils.OnConfirmationDialogClickListner{
            override fun onDialogYesClickListner() {
                homeViewModel.requestForSetDefaultAccount(context)
            }


        })
    }

    private fun showOTPdialogue() {
        DialogUtils.showOTPDialogue(activity,object : DialogUtils.OnOTPDialogClickListner{

            override fun onOTPDialogYesClickListner(otp: String) {
                homeViewModel.requestForVerifyOTPForSetDefaultAccount(context,referenceNumber,otp)
            }

        })
    }

    private fun populateHomeUseCase() {
        val useCases = ArrayList<HomeUseCasesModel>().apply {
            if(Constants.loginWithCertResponse.allowedMenu.MerchantPayment !=null){
                this.add(HomeUseCasesModel(LanguageData.getStringValue("MerchantPayment").toString(), R.drawable.ic_payment))
            }
            if(Constants.loginWithCertResponse.allowedMenu.AirTime!=null){
                this.add(HomeUseCasesModel(LanguageData.getStringValue("AirTime").toString(), R.drawable.ic_recharge))
            }
            if(Constants.loginWithCertResponse.allowedMenu.SendMoney!=null){
                this.add(HomeUseCasesModel(LanguageData.getStringValue("SendMoney").toString(),R.drawable.ic_send_money))
            }
            if(Constants.loginWithCertResponse.allowedMenu.BillPayment!=null){
                this.add(HomeUseCasesModel(LanguageData.getStringValue("BillPayment").toString(), R.drawable.ic_bill_payment))
            }
            if(Constants.loginWithCertResponse.allowedMenu.GenerateQR!=null){
                this.add(HomeUseCasesModel(LanguageData.getStringValue("GenerateQR").toString(), R.drawable.ic_qr_white))
            }
            if(Constants.loginWithCertResponse.allowedMenu.CashService!=null){
                this.add(HomeUseCasesModel(LanguageData.getStringValue("CashService").toString(), R.drawable.ic_transfer))
            }
            if(Constants.loginWithCertResponse.allowedMenu.ConsumerRegistration!=null){
                this.add(HomeUseCasesModel(LanguageData.getStringValue("ConsumerRegistration").toString(),R.drawable.ic_consumer))
            }
        }

        mUseCasesAdapter =
            HomeUseCasesAdapter(useCases, object : HomeUseCasesAdapter.HomeUseCasesClickListner {
                override fun onHomeUseCaseClick(position: Int, useCaseSelected : String) {
                    when (useCaseSelected) {

                        LanguageData.getStringValue("MerchantPayment").toString()-> {
                            val intent = Intent(activity as MainActivity,
                                SendMoneyActivity::class.java)
                            intent.putExtra("isFundTransferUseCase", false)
                            intent.putExtra("isInitiatePaymenetToMerchantUseCase", true)
                            intent.putExtra("useCaseType", LanguageData.getStringValue("InitiatePurchaseToMerchant"))

                            startActivity(intent)
                        }

                        LanguageData.getStringValue("AirTime").toString()-> {
                            var intent = Intent(
                                activity as MainActivity,
                                AirTimeActivity::class.java
                            )
                            intent.putExtra("isQuickRechargeCase",false)
                            startActivity(intent)
                        }

                        LanguageData.getStringValue("SendMoney").toString() -> {
                            val intent = Intent(activity as MainActivity,
                                SendMoneyActivity::class.java)

                            intent.putExtra("isFundTransferUseCase", true)
                            intent.putExtra("isInitiatePaymenetToMerchantUseCase", false)
                            intent.putExtra("useCaseType", LanguageData.getStringValue("FundsTransfer"))

                            startActivity(intent)
                        }

                        LanguageData.getStringValue("BillPayment").toString() -> {
                            (activity as MainActivity).startNewActivity(activity as MainActivity,
                                BillPaymentActivity ::class.java)
                        }

                        LanguageData.getStringValue("GenerateQR").toString() -> {
                            (activity as MainActivity).startNewActivity(activity as MainActivity,
                                GenerateQrActivity::class.java)
                        }

                        LanguageData.getStringValue("CashService").toString() -> {
                            startActivity(
                            Intent(
                                activity as MainActivity,
                                CashServicesActivity::class.java
                            )
                            )
                        }

                        LanguageData.getStringValue("ConsumerRegistration").toString()->{
                            startActivity(
                                Intent(
                                    activity as MainActivity,
                                    ConsumerRegistrationActivity::class.java
                                )
                            )
                        }

                        else -> {
                            startActivity(
                            Intent(
                                activity as MainActivity,
                                PaymentsActivity::class.java
                            )
                            )
                        }
                    }
                }

            }, activity as MainActivity)
        mDataBinding.useCasesRecyclerView.apply {
            adapter = mUseCasesAdapter
            layoutManager = GridLayoutManager(activity as MainActivity, 3)
        }
    }

    private fun populateHomeCardView() {
        mCardAdapter = HomeCardAdapter((activity as MainActivity).supportFragmentManager)
        mDataBinding.viewpager.apply {
            adapter = mCardAdapter
            pageMargin = 16
            addOnPageChangeListener(this@HomeFragment)
        }

        mDataBinding.flexibleIndicator.initViewPager(mDataBinding.viewpager)

        setRightLeftNavigationVisibility()
    }

    private fun setRightLeftNavigationVisibility() {
        if(Constants.IS_AGENT_USER){
            mDataBinding.leftNav.visibility = View.VISIBLE
            mDataBinding.rightNav.visibility = View.VISIBLE
            mDataBinding.flexibleIndicator.visibility = View.VISIBLE
        }else{
            mDataBinding.leftNav.visibility = View.GONE
            mDataBinding.rightNav.visibility = View.GONE
            mDataBinding.flexibleIndicator.visibility = View.GONE
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        when (position) {
            0 -> {
                mDataBinding.leftNav.visibility = View.GONE
                mDataBinding.rightNav.visibility = View.VISIBLE
            }
            1 ->{
                mDataBinding.leftNav.visibility = View.VISIBLE
                mDataBinding.rightNav.visibility = View.GONE
            }
            else -> {
                mDataBinding.leftNav.visibility = View.VISIBLE
                mDataBinding.rightNav.visibility = View.VISIBLE
            }
        }
    }

    override fun onPageSelected(position: Int) {
    }

    override fun onNextBalanceCardClick(view: View) {
        val nPosition: Int = mDataBinding.viewpager.currentItem
        mDataBinding.viewpager.currentItem = nPosition + 1
    }

    override fun onPreviousBalanceCardClick(view: View) {
        val nPosition: Int = mDataBinding.viewpager.currentItem
        mDataBinding.viewpager.currentItem = nPosition - 1
    }
}