package com.es.marocapp.usecase.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.es.marocapp.R
import com.es.marocapp.adapter.HomeCardAdapter
import com.es.marocapp.adapter.HomeUseCasesAdapter
import com.es.marocapp.adapter.LanguageCustomSpinnerAdapter
import com.es.marocapp.adapter.TransactionHistoryAdapter
import com.es.marocapp.databinding.FragmentHomeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.model.CardModel
import com.es.marocapp.model.HomeUseCasesModel
import com.es.marocapp.model.responses.Account
import com.es.marocapp.model.responses.History
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.approvals.ApprovalActivity
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.cashservices.CashServicesActivity
import com.es.marocapp.usecase.consumerregistration.ConsumerRegistrationActivity
import com.es.marocapp.usecase.payments.PaymentsActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.transaction.TransactionDetailsActivity
import com.es.marocapp.usecase.transfercommision.TransferCommisionActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger


class HomeFragment : BaseFragment<FragmentHomeBinding>(), ViewPager.OnPageChangeListener,
    HomeFragmentClickListners {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mCardAdapter: HomeCardAdapter
    private lateinit var mUseCasesAdapter: HomeUseCasesAdapter
    private lateinit var mUseCaseGridLayoutManager: GridLayoutManager
    lateinit var mLanguageSpinnerAdapter: LanguageCustomSpinnerAdapter
    lateinit var acountTypeSpinnerAdapter: LanguageCustomSpinnerAdapter
    private var mTransactionsList: ArrayList<History> = ArrayList()
    private lateinit var mTransactionHistoryAdapter: TransactionHistoryAdapter
    var referenceNumber = "";
    var quickRechargeSelectedAmount = ""

    var isBtnQuickOneChecked = false
    var isBtnQuickTwoChecked = false
    var isBtnQuickThreeChecked = false

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

        (activity as MainActivity).homeFragment = this@HomeFragment

        populateHomeCardView(true, "")
        populateHomeUseCase()

        homeViewModel.requestForGetAccountHolderPersonalInformationApi(
            requireContext(),
            Constants.CURRENT_USER_MSISDN
        )

        mTransactionHistoryAdapter = TransactionHistoryAdapter(mTransactionsList,
            object : TransactionHistoryAdapter.HistoryDetailListner {
                override fun onHistoryDetailClickListner(customModelHistoryItem: History) {
                    Constants.currentTransactionItem = customModelHistoryItem
                    startActivity(
                        Intent(
                            activity as MainActivity,
                            TransactionDetailsActivity::class.java
                        )
                    )
                }

            })

        mDataBinding.transactionsRecyclerView.apply {
            adapter = mTransactionHistoryAdapter
            layoutManager = LinearLayoutManager(context)
        }

        if (Constants.IS_FIRST_TIME) {
            if (Constants.IS_CONSUMER_USER || Constants.IS_MERCHANT_USER) {
                homeViewModel.requestForAccountHolderAddtionalInformationApi(context)
                Constants.IS_FIRST_TIME = false
                subscribeForDefaultAccountStatus()
                subscribeForSetDefaultAccountStatus()
                subscribeForVerifyOTPForSetDefaultAccountStatus()
            } else {
                val currentProfile =
                    Constants.loginWithCertResponse.getAccountHolderInformationResponse.profileName

                var isProfileNameMatched: Boolean = false
                for (i in Constants.MERCHENTAGENTPROFILEARRAY.indices) {
                    isProfileNameMatched =
                        currentProfile.equals(Constants.MERCHENTAGENTPROFILEARRAY[i])
                    if (isProfileNameMatched) {
                        break
                    }
                }
                if (isProfileNameMatched) {
                    homeViewModel.requestForAccountHolderAddtionalInformationApi(context)
                    Constants.IS_FIRST_TIME = false
                    subscribeForDefaultAccountStatus()
                    subscribeForSetDefaultAccountStatus()
                    subscribeForVerifyOTPForSetDefaultAccountStatus()
                } else {
                    (activity as MainActivity).startTutorialsTrail()
                }
            }

        }

        (activity as MainActivity).isDirectCallForTransaction = true
        (activity as MainActivity).isTransactionFragmentNotVisible = true
        setStrings()
        setQuickAmountListner()
        subscribeForSpinnerListner()
        subscribeForGetBalanceAndGetAccountsResponse()
        subsribeForTransactionHistoryResponse()
        if ((activity as MainActivity).showTransactionsDetailsIndirectly) {
            setTransacitonScreenVisisble(
                true, (activity as MainActivity).isDirectCallForTransaction,
                (activity as MainActivity).isTransactionFragmentNotVisible
            )
            (activity as MainActivity).isTransactionDetailsShowing = true

            //----------for handling Backpress of activity----------
            (activity as MainActivity).isGenerateQRFragmentShowing = false
            (activity as MainActivity).isFaqsFragmentShowing = false
            (activity as MainActivity).isSideMenuShowing = false
            (activity as MainActivity).isTranactionDetailsFragmentShowing = true
            (activity as MainActivity).isHomeFragmentShowing = false
            (activity as MainActivity).isTransacitonFragmentShowing = false
        } else {
            if (Constants.IS_AGENT_USER) {
                homeViewModel.requestForGetAccountsAPI(activity)
            } else {
                homeViewModel.requestForGetBalanceApi(activity)
            }
            (activity as MainActivity).isTransactionDetailsShowing = false

            //----------for handling Backpress of activity----------
            (activity as MainActivity).isGenerateQRFragmentShowing = false
            (activity as MainActivity).isFaqsFragmentShowing = false
            (activity as MainActivity).isSideMenuShowing = false
            (activity as MainActivity).isTranactionDetailsFragmentShowing = false
            (activity as MainActivity).isHomeFragmentShowing = true
            (activity as MainActivity).isTransacitonFragmentShowing = false
        }
        //homeViewModel.requestForGetBalanceApi(activity)
        if (LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_AR)) {
            mDataBinding.quickRechargeSpinner.setBackgroundResource(R.drawable.spinner_icon_background_white_left)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).homeFragment = this@HomeFragment
    }

    private fun setQuickAmountListner() {
        /*mDataBinding.btnQuickRecharge1.setOnClickListener {
            if(isBtnQuickOneChecked){
                isBtnQuickOneChecked = false
                mDataBinding.btnQuickRecharge1.setBackgroundResource(R.drawable.button_quick_recharge)
                mDataBinding.btnQuickRecharge2.setBackgroundResource(R.drawable.button_quick_recharge)
                mDataBinding.btnQuickRecharge3.setBackgroundResource(R.drawable.button_quick_recharge)
                quickRechargeSelectedAmount = "-1"
            }else{
                isBtnQuickOneChecked = true
                isBtnQuickTwoChecked= false
                isBtnQuickThreeChecked = false
                mDataBinding.btnQuickRecharge1.setBackgroundResource(R.drawable.button_quick_recharge_state)
                mDataBinding.btnQuickRecharge2.setBackgroundResource(R.drawable.button_quick_recharge)
                mDataBinding.btnQuickRecharge3.setBackgroundResource(R.drawable.button_quick_recharge)
                quickRechargeSelectedAmount = mDataBinding.btnQuickRecharge1.text.toString().removePrefix("DH").trim()
            }

        }

        mDataBinding.btnQuickRecharge2.setOnClickListener {
            if(isBtnQuickTwoChecked){
                isBtnQuickTwoChecked = false
                mDataBinding.btnQuickRecharge1.setBackgroundResource(R.drawable.button_quick_recharge)
                mDataBinding.btnQuickRecharge2.setBackgroundResource(R.drawable.button_quick_recharge)
                mDataBinding.btnQuickRecharge3.setBackgroundResource(R.drawable.button_quick_recharge)
                quickRechargeSelectedAmount = "-1"
            }else{
                isBtnQuickTwoChecked = true
                isBtnQuickOneChecked= false
                isBtnQuickThreeChecked = false
                mDataBinding.btnQuickRecharge2.setBackgroundResource(R.drawable.button_quick_recharge_state)
                mDataBinding.btnQuickRecharge1.setBackgroundResource(R.drawable.button_quick_recharge)
                mDataBinding.btnQuickRecharge3.setBackgroundResource(R.drawable.button_quick_recharge)
                quickRechargeSelectedAmount = mDataBinding.btnQuickRecharge2.text.toString().removePrefix("DH").trim()
            }
        }

        mDataBinding.btnQuickRecharge3.setOnClickListener {
            if(isBtnQuickThreeChecked){
                isBtnQuickThreeChecked = false
                mDataBinding.btnQuickRecharge1.setBackgroundResource(R.drawable.button_quick_recharge)
                mDataBinding.btnQuickRecharge2.setBackgroundResource(R.drawable.button_quick_recharge)
                mDataBinding.btnQuickRecharge3.setBackgroundResource(R.drawable.button_quick_recharge)
                quickRechargeSelectedAmount = "-1"
            }else{
                isBtnQuickThreeChecked = true
                isBtnQuickTwoChecked= false
                isBtnQuickOneChecked = false
                mDataBinding.btnQuickRecharge3.setBackgroundResource(R.drawable.button_quick_recharge_state)
                mDataBinding.btnQuickRecharge1.setBackgroundResource(R.drawable.button_quick_recharge)
                mDataBinding.btnQuickRecharge2.setBackgroundResource(R.drawable.button_quick_recharge)
                quickRechargeSelectedAmount = mDataBinding.btnQuickRecharge3.text.toString().removePrefix("DH").trim()
            }
        }*/

        Constants.tutorialQuickRechargeContainer = mDataBinding.quickRechargeContainer

        mDataBinding.btnQuickRecharge4.setOnClickListener {
            if (Constants.isTutorialShowing) {
                /*Constants.displayTutorial(activity!!,mDataBinding.quickRechargeContainer,LanguageData.getStringValue("QuickRechargeTutorial").toString()
                    ,R.drawable.ic_tutorial_home_quick_recharge)*/
            } else {
                val itemPos = mDataBinding.quickRechargeSpinner.selectedItemPosition
                var amount = Constants.quickRechargeAmountsList.get(itemPos)
                amount = amount.removePrefix("DH")
                amount = amount.substringBefore("DH")
                quickRechargeSelectedAmount = amount.trim()
                val intent = Intent(
                    activity as MainActivity,
                    AirTimeActivity::class.java
                )
                intent.putExtra("isQuickRechargeCase", true)
                intent.putExtra("quickRechargeAmount", quickRechargeSelectedAmount)
                startActivity(intent)
            }
        }
    }

    private fun setStrings() {
        mDataBinding.textTitleQuickRecharge.text = LanguageData.getStringValue("QuickRecharge")
        mDataBinding.btnQuickRecharge4.text = LanguageData.getStringValue("Recharge")
        mDataBinding.transactionHistoryTitile.text =
            LanguageData.getStringValue("TransactionHistory")
        mDataBinding.tvNoDataFound.text = LanguageData.getStringValue("NoDataFound")
        mDataBinding.textTitleMtCashQuickRecharge.text =
            LanguageData.getStringValue("MTCashQuickRecharge")
        mDataBinding.selectAcountTitile.text = LanguageData.getStringValue("SelectAccountType")


        if (Constants.quickRechargeAmountsList.isNotEmpty()) {
            val languageItems = Constants.quickRechargeAmountsList.toTypedArray()
            mLanguageSpinnerAdapter =
                LanguageCustomSpinnerAdapter(
                    activity as MainActivity,
                    languageItems,
                    (activity as MainActivity).resources.getColor(R.color.colorWhite), false
                )
            mDataBinding.quickRechargeContainer
        }

        mDataBinding.quickRechargeSpinner.apply {
            adapter = mLanguageSpinnerAdapter
        }

        Constants.acountTypeList.clear()

        if (Constants.IS_AGENT_USER) {

            if (Constants.acountTypeList.isEmpty()) {
                LanguageData.getStringValue("Wallet")?.let { Constants.acountTypeList.add(it) }
                for (i in Constants.getAccountsResponseArray.indices) {
                    if (Constants.getAccountsResponseArray.get(i).profileName.equals(Constants.MERCHANT_AGENT_PROFILE_NAME) &&
                        Constants.getAccountsResponseArray.get(i).accountStatus.equals(Constants.ACTIVE)
                    ) {
                        LanguageData.getStringValue("Merchant")
                            ?.let { Constants.acountTypeList.add(it) }
                    }
                    if (Constants.getAccountsResponseArray[i].accountType.equals("COMMISSIONING")) {
                        LanguageData.getStringValue("Commissioning")
                            ?.let { Constants.acountTypeList.add(it) }
                    }
                }

            }
            val acountTypeArray: Array<String> =
                Constants.acountTypeList.toArray(arrayOfNulls<String>(Constants.acountTypeList.size))
            acountTypeSpinnerAdapter =
                LanguageCustomSpinnerAdapter(
                    activity as MainActivity,
                    acountTypeArray,
                    (activity as MainActivity).resources.getColor(R.color.colorBlack), true
                )
            //  mDataBinding.acountTypeSpinner
            mDataBinding.acountTypeSpinner.apply {
                adapter = acountTypeSpinnerAdapter
            }
        } else {
            if (Constants.acountTypeList.isEmpty()) {
                LanguageData.getStringValue("Wallet")?.let { Constants.acountTypeList.add(it) }
            }
            //Constants.acountTypeList.add("Wallet")
            val acountTypeArray: Array<String> =
                Constants.acountTypeList.toArray(arrayOfNulls<String>(Constants.acountTypeList.size))
            acountTypeSpinnerAdapter =
                LanguageCustomSpinnerAdapter(
                    activity as MainActivity,
                    acountTypeArray,
                    (activity as MainActivity).resources.getColor(R.color.colorBlack), true
                )
            //  mDataBinding.acountTypeSpinner
            mDataBinding.acountTypeSpinner.apply {
                adapter = acountTypeSpinnerAdapter
            }
        }


    }

    private fun subscribeForDefaultAccountStatus() {

        val currentProfile =
            Constants.loginWithCertResponse.getAccountHolderInformationResponse.profileName

        var isProfileNameMatched: Boolean = false
        for (i in Constants.MERCHENTAGENTPROFILEARRAY.indices) {
            isProfileNameMatched =
                currentProfile.equals(Constants.MERCHENTAGENTPROFILEARRAY[i])
            if (isProfileNameMatched) {
                break
            }
        }

        homeViewModel.getAccountHolderPersonalInformationApiResponseListner.observe(
            this@HomeFragment,
            Observer {
                if (it.responseCode == ApiConstant.API_SUCCESS) {
                    Constants.CURRENT_USER_DATE_OF_BIRTH = it?.dob.toString()
                    Constants.CURRENT_USER_CITY = it?.city.toString()
                    Constants.CURRENT_USER_CIN = it?.cin.toString()
                    Constants.CURRENT_USER_ADRESS = it?.address.toString()
                }
            })

        homeViewModel.getAccountHolderAdditionalInfoResponseListner.observe(this@HomeFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.additionalinformation.isNullOrEmpty()) {
                        showPopUp()
                    } else {
                        //  if (it.additionalinformation[0].value.equals("FALSE", true)|| (it.additionalinformation[0].value.equals("FALSE", true)&&isProfileNameMatched)) {
                        if (it.additionalinformation[0].value.equals("FALSE", true)) {
                            showPopUp()
                            Constants.IS_DEFAULT_ACCOUNT_SET = false
                            (activity as MainActivity).setViewsVisibility()
                        } else {
                            Constants.IS_DEFAULT_ACCOUNT_SET = true
                            (activity as MainActivity).setViewsVisibility()
                            (activity as MainActivity).startTutorialsTrail()
                        }
                    }
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                    (activity as MainActivity).startTutorialsTrail()
                }
            }
        )
    }

    private fun subscribeForSetDefaultAccountStatus() {

        homeViewModel.setDefaultAccountResponseListener.observe(this@HomeFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    referenceNumber = it.referenceNumber
                    showOTPdialogue()
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                    (activity as MainActivity).startTutorialsTrail()
                }
            }
        )
    }

    private fun subscribeForVerifyOTPForSetDefaultAccountStatus() {

        homeViewModel.verifyOTPForDefaultAccountResponseListener.observe(this@HomeFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    Constants.IS_DEFAULT_ACCOUNT_SET = true
                    DialogUtils.successFailureDialogue(
                        context,
                        it.description,
                        0
                    )
                    (activity as MainActivity).startTutorialsTrail()
                } else {
                    DialogUtils.successFailureDialogue(
                        context,
                        it.description,
                        1
                    )
                    (activity as MainActivity).startTutorialsTrail()
                }
            }
        )
    }

    private fun showPopUp() {
        val confirmationTxt =
            LanguageData.getStringValue("DoYouWantToChooseThisMwalletMtCashDefaultForDoingOperationsQuestion")
        DialogUtils.showConfirmationDialogue(
            confirmationTxt!!,
            activity,
            object : DialogUtils.OnConfirmationDialogClickListner {
                override fun onDialogYesClickListner() {
                    homeViewModel.requestForSetDefaultAccount(context)
                }

                override fun onDialogNoClickListner() {
                    (activity as MainActivity).startTutorialsTrail()
                }


            })
    }

    private fun showOTPdialogue() {
        DialogUtils.showDefaultAccountOTPDialogue(
            activity,
            object : DialogUtils.OnOTPDialogClickListner {

                override fun onOTPDialogYesClickListner(password: String) {
                    homeViewModel.requestForVerifyOTPForSetDefaultAccount(
                        context,
                        referenceNumber,
                        password
                    )
                }

                override fun onOTPDialogNoClickListner() {
                    (activity as MainActivity).startTutorialsTrail()
                }

            })
    }

    private fun populateHomeUseCase() {
        val useCases = ArrayList<HomeUseCasesModel>().apply {
            if (Constants.loginWithCertResponse.allowedMenu.MyApprovals != null) {
                this.add(
                    HomeUseCasesModel(
                        LanguageData.getStringValue("MyApprovals").toString(),
                        R.drawable.approval_home_use_case_icon_new
                    )
                )
            }
            if (Constants.loginWithCertResponse.allowedMenu.TransferCommission != null) {
                this.add(
                    HomeUseCasesModel(
                        LanguageData.getStringValue("TransferCommission").toString(),
                        R.drawable.icons_tc
                    )
                )
            }

            if (Constants.loginWithCertResponse.allowedMenu.SendMoney != null) {
                this.add(
                    HomeUseCasesModel(
                        LanguageData.getStringValue("SendMoney").toString(),
                        R.drawable.home_send_money_new
                    )
                )
            }

            if (Constants.loginWithCertResponse.allowedMenu.MerchantPayment != null) {
                this.add(
                    HomeUseCasesModel(
                        LanguageData.getStringValue("MerchantPayment").toString(),
                        R.drawable.home_merchant_payment_new
                    )
                )
            }
            if (Constants.loginWithCertResponse.allowedMenu.BillPayment != null) {
                this.add(
                    HomeUseCasesModel(
                        LanguageData.getStringValue("BillPayment").toString(),
                        R.drawable.home_bill_payment
                    )
                )
            }

            if (Constants.loginWithCertResponse.allowedMenu.AirTime != null) {
                this.add(
                    HomeUseCasesModel(
                        LanguageData.getStringValue("AirTime").toString(),
                        R.drawable.home_air_time_new
                    )
                )
            }


            /* if (Constants.loginWithCertResponse.allowedMenu.GenerateQR != null) {
                 this.add(
                     HomeUseCasesModel(
                         LanguageData.getStringValue("GenerateQR").toString(),
                         R.drawable.home_qr
                     )
                 )
             }*/
            if (Constants.loginWithCertResponse.allowedMenu.CashService != null) {
                this.add(
                    HomeUseCasesModel(
                        LanguageData.getStringValue("CashService").toString(),
                        R.drawable.home_cash_services
                    )
                )
            }
            if (Constants.loginWithCertResponse.allowedMenu.ConsumerRegistration != null) {
                this.add(
                    HomeUseCasesModel(
                        LanguageData.getStringValue("ConsumerRegistration").toString(),
                        R.drawable.home_consumer_registration
                    )
                )
            }


            /*if (Constants.loginWithCertResponse.allowedMenu.CashInViaCard != null) {
                this.add(
                    HomeUseCasesModel(
                        LanguageData.getStringValue("CashInViaCard").toString(),
                        R.drawable.home_cash_via_card
                    )
                )
            }*/
        }

        mUseCasesAdapter =
            HomeUseCasesAdapter(useCases, object : HomeUseCasesAdapter.HomeUseCasesClickListner {
                override fun onHomeUseCaseClick(position: Int, useCaseSelected: String) {
                    when (useCaseSelected) {

                        LanguageData.getStringValue("MerchantPayment").toString() -> {
                            val intent = Intent(
                                activity as MainActivity,
                                SendMoneyActivity::class.java
                            )
                            intent.putExtra("isFundTransferUseCase", false)
                            intent.putExtra("isInitiatePaymenetToMerchantUseCase", true)
                            intent.putExtra(
                                "useCaseType",
                                LanguageData.getStringValue("InitiatePurchaseToMerchant")
                            )

                            startActivity(intent)
                        }
                        LanguageData.getStringValue("TransferCommission").toString() -> {
                            val intent = Intent(
                                activity as MainActivity,
                                TransferCommisionActivity::class.java
                            )
                            startActivity(intent)
                        }

                        LanguageData.getStringValue("AirTime").toString() -> {
                            val intent = Intent(
                                activity as MainActivity,
                                AirTimeActivity::class.java
                            )
                            intent.putExtra("isQuickRechargeCase", false)
                            startActivity(intent)
                        }

                        LanguageData.getStringValue("SendMoney").toString() -> {
                            if (Constants.isTutorialShowing) {
                                /* Constants.displayTutorial(activity as MainActivity,mUseCaseGridLayoutManager.findViewByPosition(position)!!.findViewById(R.id.useCasesParentLayout),
                                 LanguageData.getStringValue("SendMoneyTutorial").toString())*/
                            } else {
                                val intent = Intent(
                                    activity as MainActivity,
                                    SendMoneyActivity::class.java
                                )

                                intent.putExtra("isFundTransferUseCase", true)
                                intent.putExtra("isInitiatePaymenetToMerchantUseCase", false)
                                intent.putExtra(
                                    "useCaseType",
                                    LanguageData.getStringValue("SendMoney")
                                )

                                startActivity(intent)
                            }
                        }

                        LanguageData.getStringValue("BillPayment").toString() -> {
                            (activity as MainActivity).startNewActivity(
                                activity as MainActivity,
                                BillPaymentActivity::class.java
                            )
                        }

                        /*LanguageData.getStringValue("GenerateQR").toString() -> {
                            (activity as MainActivity).startNewActivity(
                                activity as MainActivity,
                                GenerateQrActivity::class.java
                            )
                        }*/

                        LanguageData.getStringValue("CashService").toString() -> {
                            startActivity(
                                Intent(
                                    activity as MainActivity,
                                    CashServicesActivity::class.java
                                )
                            )
                        }

                        LanguageData.getStringValue("ConsumerRegistration").toString() -> {
                            startActivity(
                                Intent(
                                    activity as MainActivity,
                                    ConsumerRegistrationActivity::class.java
                                )
                            )
                        }

                        LanguageData.getStringValue("MyApprovals").toString() -> {
                            startActivity(
                                Intent(
                                    activity as MainActivity,
                                    ApprovalActivity::class.java
                                )
                            )
                        }

                        /*LanguageData.getStringValue("CashInViaCard").toString() -> {
                            startActivity(
                                Intent(
                                    activity as MainActivity,
                                    ActivityCashInViaCard::class.java
                                )
                            )
                        }*/

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
        mUseCaseGridLayoutManager = GridLayoutManager(activity as MainActivity, 3)
        mDataBinding.useCasesRecyclerView.apply {
            adapter = mUseCasesAdapter
            layoutManager = mUseCaseGridLayoutManager
        }

        if ((activity as MainActivity).isHomeFragmentShowing) {
            mDataBinding.useCasesRecyclerView.postDelayed(Runnable {
                for (i in 0 until useCases.size) {
                    if (useCases[i].useCaseTitle == LanguageData.getStringValue("SendMoney")
                            .toString()
                    ) {
                        val holder =
                            mDataBinding.useCasesRecyclerView.findViewHolderForAdapterPosition(i) as? RecyclerView.ViewHolder
                        Constants.tutorialSendMoney =
                            holder?.itemView?.findViewById<ConstraintLayout>(R.id.useCasesParentLayout)
                        break
                    }
                }
            }, 50)
        }
    }

    private fun populateHomeCardView(updateBalance: Boolean, amount: String?) {
        var mbalanceInfoAndResonse = Constants.balanceInfoAndResponse
        var maxUserBalance = "0"

        if (!mbalanceInfoAndResonse?.limitsList.isNullOrEmpty()) {
            for (index in mbalanceInfoAndResonse?.limitsList!!.indices) {
                for (i in Constants.KEY_FOR_WALLET_BALANCE_MAX.indices) {
                    if (mbalanceInfoAndResonse.limitsList!![index].name.equals(Constants.KEY_FOR_WALLET_BALANCE_MAX[i])) {
                        maxUserBalance = mbalanceInfoAndResonse.limitsList!![index].threshhold!!
                        maxUserBalance = maxUserBalance.removePrefix("DH").trim()

                        break
                    }
                }

            }
        }
        var listOfFragment: ArrayList<HomeBalanceFragment> = arrayListOf()
        if (!updateBalance) {
            listOfFragment.add(
                HomeBalanceFragment(
                    0, CardModel(
                        R.drawable.ic_wallet_balance,
                        LanguageData.getStringValue("Balance").toString(),
                        Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mbalanceInfoAndResonse?.balance,
                        maxUserBalance,
                        mbalanceInfoAndResonse?.balance!!
                    ), -1
                )
            )
            Constants.WALLETACCOUNTBALANCE = mbalanceInfoAndResonse.balance!!
        } else {
            listOfFragment.add(
                HomeBalanceFragment(
                    0, CardModel(
                        R.drawable.ic_wallet_balance,
                        LanguageData.getStringValue("Balance").toString(),
                        Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + amount,
                        maxUserBalance,
                        amount!!
                    ), -1
                )
            )
            Constants.WALLETACCOUNTBALANCE = amount
        }
        addAgentBalanceCard(listOfFragment)
        populateBanners(listOfFragment)
        mCardAdapter = HomeCardAdapter(this@HomeFragment.childFragmentManager, listOfFragment)
        mDataBinding.viewpager.apply {
            adapter = mCardAdapter
            pageMargin = 16
            addOnPageChangeListener(this@HomeFragment)
        }

        mDataBinding.flexibleIndicator.initViewPager(mDataBinding.viewpager)

//        setRightLeftNavigationVisibility()
    }

    private fun populateBanners(listOfFragment: ArrayList<HomeBalanceFragment>) {
        listOfFragment.add(
            HomeBalanceFragment(
                1, CardModel(-1, "", "", "", ""), R.drawable.banner1
            )
        )
        listOfFragment.add(
            HomeBalanceFragment(
                1, CardModel(-1, "", "", "", ""), R.drawable.banner2
            )
        )
        listOfFragment.add(
            HomeBalanceFragment(
                1, CardModel(-1, "", "", "", ""), R.drawable.banner3
            )
        )
    }

    private fun addAgentBalanceCard(listOfFragment: ArrayList<HomeBalanceFragment>) {

        if (Constants.IS_AGENT_USER && Constants.getAccountsResponseArray != null) {
            for (i in Constants.getAccountsResponseArray.indices) {

                if (Constants.getAccountsResponseArray[i].accountType.equals(
                        Constants.TYPE_COMMISSIONING,
                        true
                    )
                ) {
                    Constants.getAccountsResponse = Constants.getAccountsResponseArray[i]
                    listOfFragment.add(
                        HomeBalanceFragment(
                            0, CardModel(
                                R.drawable.ic_wallet_balance,
                                LanguageData.getStringValue("Commission").toString(),
                                Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + Constants.getAccountsResponse!!.balance,
                                "0",
                                "0"
                            ), -1
                        )
                    )
                    Constants.COMMISIONACCOUNTBALANCE = Constants.getAccountsResponse!!.balance
                    val fri = Constants.getAccountsResponse!!.accountFri.replace("FRI:", "").trim()
                    Constants.COMMISIONACCOUNTFRI = fri
                }

                if (Constants.getAccountsResponseArray[i].profileName.equals(
                        Constants.MERCHANT_AGENT_PROFILE_NAME
                    ) && Constants.getAccountsResponseArray[i].accountStatus.equals(
                        Constants.ACTIVE
                    )
                ) {
                    var acountName = LanguageData.getStringValue("Merchant")
                    Constants.getAccountsResponse = Constants.getAccountsResponseArray[i]
                    listOfFragment.add(
                        HomeBalanceFragment(
                            0, CardModel(
                                R.drawable.ic_wallet_balance,
                                acountName!!,
                                Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + Constants.getAccountsResponse!!.balance,
                                "0",
                                "0"
                            ), -1
                        )
                    )


                }
            }
            if (Constants.COMMISIONACCOUNTBALANCE.isNullOrEmpty()) {
                Constants.COMMISIONACCOUNTBALANCE = "0"
            }
            if (Constants.WALLETACCOUNTBALANCE.isNullOrEmpty()) {
                Constants.WALLETACCOUNTBALANCE = "0"
            }
            val totalBalance = Constants.converValueToTwoDecimalPlace(
                Constants.addTwoValues(
                    Constants.COMMISIONACCOUNTBALANCE.toDouble(),
                    Constants.WALLETACCOUNTBALANCE.toDouble()
                )
            )
            listOfFragment.add(
                0, HomeBalanceFragment(
                    0, CardModel(
                        R.drawable.ic_wallet_balance,
                        LanguageData.getStringValue("TotalBalance").toString(),
                        Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + totalBalance, "0", "0"
                    ), -1
                )
            )
        }
    }

    private fun setRightLeftNavigationVisibility() {
        if (Constants.IS_AGENT_USER && Constants.getAccountsResponse != null) {
            mDataBinding.leftNav.visibility = View.VISIBLE
            mDataBinding.rightNav.visibility = View.VISIBLE
            mDataBinding.flexibleIndicator.visibility = View.VISIBLE
        } else {
            mDataBinding.leftNav.visibility = View.GONE
            mDataBinding.rightNav.visibility = View.GONE
            mDataBinding.flexibleIndicator.visibility = View.GONE
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        /*var totalItemCount = mDataBinding.viewpager.adapter!!.count
        if (totalItemCount != null) {
            if (totalItemCount > 1) {
                when (position) {
                    0 -> {
                        mDataBinding.leftNav.visibility = View.GONE
                        mDataBinding.rightNav.visibility = View.VISIBLE
                    }
                    totalItemCount - 1 -> {
                        mDataBinding.leftNav.visibility = View.VISIBLE
                        mDataBinding.rightNav.visibility = View.GONE
                    }
                    else -> {
                        mDataBinding.leftNav.visibility = View.VISIBLE
                        mDataBinding.rightNav.visibility = View.VISIBLE
                    }
                }
            }
        }*/
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

    private fun subscribeForGetBalanceAndGetAccountsResponse() {
        homeViewModel.getBalanceResponseListner.observe(this, Observer {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                populateHomeCardView(true, it?.amount)
            } else {
                DialogUtils.showErrorDialoge(activity, it.description)
            }
        })

        homeViewModel.getAccountsResponseListner.observe(this@HomeFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    for (i in it.accounts.indices) {
                        Constants.getAccountsResponseArray = it.accounts as ArrayList<Account>
                        homeViewModel.requestForGetBalanceApi(activity)
                    }
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )
    }


    fun setTransacitonScreenVisisble(
        isTransactionDetailsVisible: Boolean,
        directCallForTransaction: Boolean,
        transactionFragmentNotVisible: Boolean
    ) {
        if (isTransactionDetailsVisible) {

            homeViewModel.requestForGetTransactionHistoryApi(
                activity,
                Constants.CURRENT_USER_MSISDN,
                false
            )
            if (transactionFragmentNotVisible) {
                if (directCallForTransaction) {
                    //setTransactionViewVisible
                    mDataBinding.transactionViewGroup.visibility = View.VISIBLE
                    mDataBinding.useCasesRecyclerView.visibility = View.GONE
                } else {
                    //setTransactionViewVisible
                    mDataBinding.transactionViewGroup.visibility = View.VISIBLE
                    mDataBinding.useCasesRecyclerView.visibility = View.GONE
                }
            }
        } else {
            mDataBinding.transactionViewGroup.visibility = View.GONE
            mDataBinding.useCasesRecyclerView.visibility = View.VISIBLE
        }
    }

    fun subsribeForTransactionHistoryResponse() {
        homeViewModel.getTransactionsResponseListner.observe(this@HomeFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (!it.historyResponse.isNullOrEmpty()) {
                        mDataBinding.tvNoDataFound.visibility = View.GONE

                        //Clearing Past Data if Any
                        mTransactionHistoryAdapter.updateHistoryList()
                        mTransactionHistoryAdapter.updateHistoryList(it.historyResponse)
                    } else {
                        mTransactionHistoryAdapter.updateHistoryList()
                        mDataBinding.tvNoDataFound.visibility = View.VISIBLE
                    }
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })
    }

    private fun subscribeForSpinnerListner() {

        // homeViewModel.requestForGetTransactionHistoryApi(activity,Constants.CURRENT_USER_MSISDN)
        mDataBinding.acountTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val lastSelection = Constants.CURRENT_ACOUNT_TYPE_SELECTED
                    val currentSelection = Constants.acountTypeList.get(position)
                    if (!lastSelection.equals(currentSelection)) {
                        Constants.CURRENT_ACOUNT_TYPE_SELECTED = currentSelection
                        if (lastSelection != null) {
                            Constants.LAST_ACOUNT_TYPE_SELECTED = lastSelection
                        }
                        if (currentSelection.equals(LanguageData.getStringValue("Merchant"))) {
                            for (i in Constants.getAccountsResponseArray.indices) {
                                if (Constants.getAccountsResponseArray.get(i).profileName.equals(
                                        Constants.MERCHANT_AGENT_PROFILE_NAME
                                    ) &&
                                    Constants.getAccountsResponseArray.get(i).accountStatus.equals(
                                        Constants.ACTIVE
                                    )
                                ) {
                                    homeViewModel.passNewFri(
                                        Constants.getAccountsResponseArray.get(
                                            i
                                        ).accountFri
                                    )
                                }
                            }
                        } else if (currentSelection.equals(LanguageData.getStringValue("Commissioning"))) {
                            for (i in Constants.getAccountsResponseArray.indices) {
                                if (Constants.getAccountsResponseArray.get(i).accountType.equals("COMMISSIONING")) {
                                    homeViewModel.passNewFri(
                                        Constants.getAccountsResponseArray.get(
                                            i
                                        ).accountFri
                                    )
                                }
                            }
                        } else {
                            homeViewModel.requestForGetTransactionHistoryApi(
                                activity,
                                Constants.CURRENT_USER_MSISDN,
                                false
                            )
                        }
                    }
                }
            }

        homeViewModel.acountFri.observe(this, Observer {

            //excluding String Fri:
            var fri = it.substring(4, it.length)
            homeViewModel.requestForGetTransactionHistoryApi(activity, fri, true)
        })
    }
}