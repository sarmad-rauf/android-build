package com.es.marocapp.usecase.airtime.airtimeplansandamounts

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.AirTimeDataAdpater
import com.es.marocapp.databinding.FragmentAirTimeMainBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.GetAirTimeUseCasesResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.airtime.AirTimeViewModel
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.regex.Pattern


class AirTimeMainFragment : BaseFragment<FragmentAirTimeMainBinding>(), TextWatcher,
    AdapterView.OnItemSelectedListener {

    private lateinit var mActivityViewModel: AirTimeViewModel

    var msisdnEntered = ""
    var isNumberRegexMatches = false

    private var mAirTimeRechargeData: ArrayList<String> = arrayListOf()
    private var mAirTimePlanData: ArrayList<String> = arrayListOf()
    private var mAirTimeAmountData: ArrayList<String> = arrayListOf()

    private lateinit var mAirTimeRechargeDataAdapter: AirTimeDataAdpater
    private lateinit var mAirTimePlanDataAdapter: AirTimeDataAdpater
    private lateinit var mAirTimeAmountDataAdapter: AirTimeDataAdpater

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var list_of_favorites = arrayListOf<String>()

    var valueSelectedFromType = false
    var valueSelectedFromPlan = false
    var valueSelectedFromAmount = false


    lateinit var airTimeResponse: GetAirTimeUseCasesResponse

    override fun setLayout(): Int {
        return R.layout.fragment_air_time_main
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as AirTimeActivity).get(
            AirTimeViewModel::class.java
        )
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        mActivityViewModel.popBackStackTo = -1

        mActivityViewModel.requestForAirTimeUseCasesApi(activity)

        sheetBehavior = BottomSheetBehavior.from(mDataBinding.bottomSheetAirTime)

        mDataBinding.inputPhoneNumber.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Constants.APP_MSISDN_LENGTH.toInt() - 2
            )
        )

        mDataBinding.inputPhoneNumber.addTextChangedListener(this)
        mDataBinding.inputPhoneNumber.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3
                if (event.getAction() === MotionEvent.ACTION_UP) {
                    if (event.getRawX() >=  mDataBinding.inputPhoneNumber.getRight() -  mDataBinding.inputPhoneNumber.getCompoundDrawables()
                            .get(DRAWABLE_RIGHT).getBounds().width()
                    ) {
                        // your action here
                        (activity as AirTimeActivity).openPhoneBook(  mDataBinding.inputPhoneNumber,
                            mDataBinding.inputLayoutPhoneNumber,
                            mDataBinding.inputPhoneNumberHint)
                        return true
                    }
                }
                return false
            }
        })

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
                } else {
                    if (mDataBinding.inputPhoneNumber.text.isEmpty()) {
                        mDataBinding.inputPhoneNumberHint.visibility =
                            View.VISIBLE
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("MSISDNPlaceholder")

                    } else {
                        mDataBinding.inputPhoneNumberHint.visibility =
                            View.GONE
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("EnterReceiversMobileNumber")
                    }
                }
            }
        }

        mAirTimeRechargeDataAdapter =
            AirTimeDataAdpater(
                mAirTimeRechargeData,
                object : AirTimeDataAdpater.AirTimeDataClickLisnter {
                    override fun onSelectedAirTimeData(airTimeData: String, position1: Int) {
                        airTimeResponse = mActivityViewModel.mAirTimeUseCaseResponse.get()!!
//                    if (valueSelectedFromType) {
                        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                        mDataBinding.inputLayoutRechargeType.error = ""
                        mDataBinding.inputLayoutRechargeType.isErrorEnabled = false

                        mAirTimeAmountData.clear()
                        mAirTimePlanData.clear()
                        mAirTimeAmountDataAdapter.notifyDataSetChanged()
                        mAirTimePlanDataAdapter.notifyDataSetChanged()
                        mDataBinding.inputRechargeAmount.setText("")
                        mDataBinding.inputRechargePlan.setText("")
                        when (airTimeData) {
                            airTimeResponse.rechargeFixe.titleName -> {
                                mActivityViewModel.isRechargeFixeUseCase.set(true)
                                mActivityViewModel.isRechargeMobileUseCase.set(false)
                                mActivityViewModel.isQuickRechargeUseCase.set(false)


                                mActivityViewModel.airTimeSelected.set(airTimeResponse.rechargeFixe.titleName)

                                mDataBinding.inputRechargeType.setText(airTimeData)

                                mDataBinding.inputLayoutRechargePlan.visibility = View.GONE
                                mDataBinding.inputPlanDropDown.visibility = View.GONE

                                mAirTimeAmountData.clear()
                                if (airTimeResponse.rechargeFixe.planList.isNotEmpty()) {
                                    for (index in airTimeResponse.rechargeFixe.planList.indices) {
                                        /*var airTimeAmount =
                                            airTimeResponse.rechargeFixe.planList[index].removeSuffix(
                                                "DH"
                                            )*/

                                        var airTimeAmount =
                                            airTimeResponse.rechargeFixe.planList[index]

                                        mAirTimeAmountData.add(airTimeAmount.trim())
                                    }
                                }

                                mAirTimeAmountDataAdapter.notifyDataSetChanged()
                            }

                            airTimeResponse.rechargeMobile.titleName -> {
                                mActivityViewModel.isRechargeFixeUseCase.set(false)
                                mActivityViewModel.isRechargeMobileUseCase.set(true)
                                mActivityViewModel.isQuickRechargeUseCase.set(false)


                                mActivityViewModel.airTimeSelected.set(airTimeResponse.rechargeMobile.titleName)

                                mDataBinding.inputRechargeType.setText(airTimeData)

                                mDataBinding.inputLayoutRechargePlan.visibility = View.VISIBLE
                                mDataBinding.inputPlanDropDown.visibility = View.VISIBLE

                                if (airTimeResponse.rechargeMobile.planList.isNotEmpty()) {
                                    mAirTimePlanData.clear()
                                    for (index in airTimeResponse.rechargeMobile.planList.indices) {
                                        mAirTimePlanData.add(airTimeResponse.rechargeMobile.planList[index].plan)
                                    }

                                    mAirTimePlanDataAdapter.notifyDataSetChanged()
                                }

                            }
                            else -> Toast.makeText(activity, "Nothing Clicked", Toast.LENGTH_SHORT)
                                .show()

                        }
                    }
                })

        mAirTimePlanDataAdapter = AirTimeDataAdpater(mAirTimePlanData,
            object : AirTimeDataAdpater.AirTimeDataClickLisnter {
                override fun onSelectedAirTimeData(airTimePlan: String, position1: Int) {
                    mAirTimeAmountData.clear()
                    mAirTimeAmountDataAdapter.notifyDataSetChanged()
                    mDataBinding.inputRechargeAmount.setText("")

                    mDataBinding.inputLayoutRechargePlan.error = ""
                    mDataBinding.inputLayoutRechargePlan.isErrorEnabled = false

                    airTimeResponse = mActivityViewModel.mAirTimeUseCaseResponse.get()!!
                    sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    mActivityViewModel.airTimePlanSelected.set(airTimePlan)
                    mDataBinding.inputRechargePlan.setText(airTimePlan)

                    mAirTimeAmountData.clear()
                    if (airTimeResponse.rechargeMobile.planList.isNotEmpty()) {
                        for (index in airTimeResponse.rechargeMobile.planList.indices) {
                            if (airTimeResponse.rechargeMobile.planList[index].plan.equals(
                                    mActivityViewModel.airTimePlanSelected.get()!!
                                )
                            ) {
                                mActivityViewModel.airTimeSelectedPlanCodeSelected.set(
                                    airTimeResponse.rechargeMobile.planList[index].code.toString()
                                )
                                for (amountIndex in airTimeResponse.rechargeMobile.planList[index].amounts.indices) {

                                    var airTimeAmount =
                                        airTimeResponse.rechargeMobile.planList[index].amounts[amountIndex].substringBefore(
                                            " "
                                        )
                                    /*airTimeAmount = airTimeAmount.removeSuffix("DH").trim()*/
                                    airTimeAmount = airTimeAmount.trim()
                                    mAirTimeAmountData.add(airTimeAmount.trim())
                                }
                            }
                        }
                    }

                    mAirTimeAmountDataAdapter.notifyDataSetChanged()
                }

            })

        mAirTimeAmountDataAdapter = AirTimeDataAdpater(mAirTimeAmountData,
            object : AirTimeDataAdpater.AirTimeDataClickLisnter {
                override fun onSelectedAirTimeData(airTimeAmount: String, position1: Int) {
                    airTimeResponse = mActivityViewModel.mAirTimeUseCaseResponse.get()!!
                    sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    mActivityViewModel.airTimeAmountSelected.set(airTimeAmount.removeSuffix("DH").trim())
                    mDataBinding.inputRechargeAmount.setText(airTimeAmount)

                    mDataBinding.inputLayoutRechargeAmount.error = ""
                    mDataBinding.inputLayoutRechargeAmount.isErrorEnabled = false
                }

            })


        mDataBinding.airTimeRechargeDataRecycler.apply {
            adapter = mAirTimeRechargeDataAdapter
            layoutManager = LinearLayoutManager(activity as AirTimeActivity)
        }

        mDataBinding.airTimeAmountDataRecycler.apply {
            adapter = mAirTimeAmountDataAdapter
            layoutManager = LinearLayoutManager(activity as AirTimeActivity)
        }


        mDataBinding.airTimePlanDataRecycler.apply {
            adapter = mAirTimePlanDataAdapter
            layoutManager = LinearLayoutManager(activity as AirTimeActivity)
        }

        mDataBinding.phonebook.setOnClickListener{
            (activity as AirTimeActivity).openPhoneBook(  mDataBinding.inputPhoneNumber,
                mDataBinding.inputLayoutPhoneNumber,
                mDataBinding.inputPhoneNumberHint)
        }

        list_of_favorites.clear()
        for (contacts in Constants.mContactListArray) {
            var contactNumber = contacts.customerreference
            var contactName = contacts.contactname
            contactNumber = contactNumber.substringBefore("@")
            contactNumber = contactNumber.substringBefore("/")
            contactNumber = contactNumber.removePrefix(Constants.APP_MSISDN_PREFIX)
            contactNumber = "0$contactNumber"
            //todo also here remove lenght-2 check in max line
            if (contactNumber.length.equals(Constants.APP_MSISDN_LENGTH.toInt() - 2)) {
                var name_number_favorite = "$contactName-$contactNumber"
                list_of_favorites.add(name_number_favorite)
            }
        }
        list_of_favorites.add(0, LanguageData.getStringValue("SelectFavorite").toString())

        val adapterFavoriteType = ArrayAdapter<CharSequence>(
            activity as AirTimeActivity, R.layout.layout_favorites_spinner_text,
            list_of_favorites as List<CharSequence>
        )
        mDataBinding.spinnerSelectFavorites.apply {
            adapter = adapterFavoriteType
        }
        mDataBinding.spinnerSelectFavorites.onItemSelectedListener = this@AirTimeMainFragment


        setStrings()
        subscribeObserver()
        initListner()

        mDataBinding.btnScanQR.setOnClickListener {
            (activity as AirTimeActivity).startQRScan(
                mDataBinding.inputPhoneNumber,
                mDataBinding.inputLayoutPhoneNumber,
                mDataBinding.inputPhoneNumberHint
            )

        }

    }

    private fun initListner() {
        sheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                /*
                BottomSheetBehavior.STATE_EXPANDED -> "Close Persistent Bottom Sheet"
                BottomSheetBehavior.STATE_COLLAPSED -> "Open Persistent Bottom Sheet"
                else -> "Persistent Bottom Sheet"*/
            }

        })

        mDataBinding.inputRechargeDropDown.setOnClickListener {
            valueSelectedFromType = true
            valueSelectedFromPlan = false
            valueSelectedFromAmount = false

            mDataBinding.airTimePlanDataRecycler.visibility = View.GONE
            mDataBinding.airTimeAmountDataRecycler.visibility = View.GONE
            mDataBinding.airTimeRechargeDataRecycler.visibility = View.VISIBLE

            val state =
                if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_COLLAPSED
                else
                    BottomSheetBehavior.STATE_EXPANDED
            sheetBehavior.state = state
        }

        mDataBinding.inputPlanDropDown.setOnClickListener {
            valueSelectedFromType = false
            valueSelectedFromPlan = true
            valueSelectedFromAmount = false
            if (mDataBinding.inputRechargeType.text.toString().equals("")) {
                return@setOnClickListener
            } else {
                mDataBinding.airTimePlanDataRecycler.visibility = View.VISIBLE
                mDataBinding.airTimeAmountDataRecycler.visibility = View.GONE
                mDataBinding.airTimeRechargeDataRecycler.visibility = View.GONE
                val state =
                    if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                        BottomSheetBehavior.STATE_COLLAPSED
                    else
                        BottomSheetBehavior.STATE_EXPANDED
                sheetBehavior.state = state
            }
        }

        mDataBinding.inputAmountDropDown.setOnClickListener {
            valueSelectedFromType = false
            valueSelectedFromPlan = false
            valueSelectedFromAmount = true
            if (mDataBinding.inputRechargeType.text.toString().equals("")) {
                return@setOnClickListener
            } else if (mDataBinding.inputRechargeType.text.toString()
                    .equals(airTimeResponse.rechargeMobile.titleName)
            ) {
                if (mDataBinding.inputRechargePlan.text.toString().equals("")) {

                } else {
                    mDataBinding.airTimePlanDataRecycler.visibility = View.GONE
                    mDataBinding.airTimeAmountDataRecycler.visibility = View.VISIBLE
                    mDataBinding.airTimeRechargeDataRecycler.visibility = View.GONE
                    val state =
                        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                            BottomSheetBehavior.STATE_COLLAPSED
                        else
                            BottomSheetBehavior.STATE_EXPANDED
                    sheetBehavior.state = state
                }
            } else {
                mDataBinding.airTimePlanDataRecycler.visibility = View.GONE
                mDataBinding.airTimeAmountDataRecycler.visibility = View.VISIBLE
                mDataBinding.airTimeRechargeDataRecycler.visibility = View.GONE
                val state =
                    if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                        BottomSheetBehavior.STATE_COLLAPSED
                    else
                        BottomSheetBehavior.STATE_EXPANDED
                sheetBehavior.state = state
            }
        }

        mDataBinding.btnCancel.setOnClickListener {
            if (valueSelectedFromType) {
                mDataBinding.inputRechargeType.setText("")
                mDataBinding.inputRechargePlan.setText("")
                mDataBinding.inputRechargeAmount.setText("")

                mDataBinding.inputLayoutRechargePlan.visibility = View.VISIBLE
                mDataBinding.inputPlanDropDown.visibility = View.VISIBLE

                mAirTimePlanData.clear()
                mAirTimeAmountData.clear()

                mAirTimePlanDataAdapter.notifyDataSetChanged()
                mAirTimeAmountDataAdapter.notifyDataSetChanged()
            }

            if (valueSelectedFromPlan) {
                mDataBinding.inputRechargePlan.setText("")
                mDataBinding.inputRechargeAmount.setText("")
                mAirTimeAmountData.clear()
                mAirTimeAmountDataAdapter.notifyDataSetChanged()
            }

            if (valueSelectedFromAmount) {
                mDataBinding.inputRechargeAmount.setText("")
            }
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        mDataBinding.bottomSheetAirTime.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        mDataBinding.btnNext.setOnClickListener {
            if(mDataBinding.inputRechargeType.text.equals("Recharge Fixe")){
                Constants.APP_AIR_TIME_FIXE_REGEX?.let { isRegexMached(it) }
            }
            if (isValidForAll()) {
                mActivityViewModel.requestForAirTimeQuoteApi(activity, msisdnEntered)
            }
        }
    }


    private fun subscribeObserver() {
        mActivityViewModel.getAirTimeUseCasesResponseListner.observe(this@AirTimeMainFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    airTimeResponse = mActivityViewModel.mAirTimeUseCaseResponse.get()!!

                    mAirTimeRechargeData.clear()

                    if (!it.rechargeFixe.planList.isNullOrEmpty()) {
                        mAirTimeRechargeData.add(it.rechargeFixe.titleName)
                    }

                    if (!it.rechargeMobile.planList.isNullOrEmpty()) {
                        mAirTimeRechargeData.add(it.rechargeMobile.titleName)
                    }

                    mAirTimeRechargeDataAdapter.notifyDataSetChanged()

                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })

        mActivityViewModel.getAirTimeQuoteResponseListner.observe(this@AirTimeMainFragment,
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
                    (activity as AirTimeActivity).navController.navigate(R.id.action_airTimeMainFragment_to_airTimeConfirmationFragment)
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })

        mActivityViewModel.errorText.observe(this@AirTimeMainFragment, Observer {
            DialogUtils.showErrorDialoge(activity, it)
        })
    }

    private fun setStrings() {
        mDataBinding.inputLayoutRechargeType.hint =
            LanguageData.getStringValue("PaymentType")
        mDataBinding.inputLayoutRechargePlan.hint =
            LanguageData.getStringValue("PlanType")
        mDataBinding.inputLayoutRechargeAmount.hint =
            LanguageData.getStringValue("Amount")
        mDataBinding.inputLayoutProfileName.hint =
            LanguageData.getStringValue("UserProfile")
        mDataBinding.inputProfileName.setText(Constants.balanceInfoAndResponse?.firstname + " " + Constants.balanceInfoAndResponse?.surname)
        mDataBinding.btnNext.text = LanguageData.getStringValue("Submit")
        mDataBinding.inputLayoutPhoneNumber.hint =
            LanguageData.getStringValue("MSISDNPlaceholder")
        mDataBinding.inputPhoneNumberHint.text =
            LanguageData.getStringValue("EnterReceiversMobileNumber")

        mDataBinding.generateQrTv.text = LanguageData.getStringValue("ScanQr")
        mDataBinding.btnCancel.text = LanguageData.getStringValue("BtnTitle_Cancel")
        mDataBinding.selectFavoriteTypeTitle.text = LanguageData.getStringValue("SelectFavorite")


    }

    private fun isValidForAll(): Boolean {
        var isValidForAll = true
        Logger.debugLog("ok","Fixe ${mDataBinding.inputRechargeType.text}")

        //todo NUmber Lenght is Pending
        if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() || mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
            isValidForAll = false
            mDataBinding.inputLayoutPhoneNumber.error =
                LanguageData.getStringValue("PleaseEnterValidMobileNumber")
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
            mDataBinding.inputLayoutPhoneNumber.hint =
                LanguageData.getStringValue("EnterReceiversMobileNumber")
            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
        } else {
            mDataBinding.inputLayoutPhoneNumber.error = ""
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
            var userMsisdn = mDataBinding.inputPhoneNumber.text.toString()
            if (userMsisdn.startsWith("0", false)) {
                checkNumberExistInFavorites(userMsisdn)
                mDataBinding.inputLayoutPhoneNumber.error = ""
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                var userMSISDNwithPrefix = userMsisdn.removePrefix("0")
                userMSISDNwithPrefix = Constants.APP_MSISDN_PREFIX + userMSISDNwithPrefix
                userMSISDNwithPrefix = userMSISDNwithPrefix.removePrefix("+")


                if (isNumberRegexMatches) {
                    mDataBinding.inputLayoutPhoneNumber.error = ""
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                    msisdnEntered = userMSISDNwithPrefix
                } else {
                    isValidForAll = false
                    mDataBinding.inputLayoutPhoneNumber.error =
                        LanguageData.getStringValue("PleaseEnterValidMobileNumber")
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterReceiversMobileNumber")
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                }
            } else {
                isValidForAll = false
                mDataBinding.inputLayoutPhoneNumber.error =
                    LanguageData.getStringValue("PleaseEnterValidMobileNumber")
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
            }
        }

        if (mDataBinding.inputRechargeType.text.toString().isEmpty()) {
            isValidForAll = false
            mDataBinding.inputLayoutRechargeType.error =
                LanguageData.getStringValue("PleaseSelectPackageType")
            mDataBinding.inputLayoutRechargeType.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutRechargeType.error = ""
            mDataBinding.inputLayoutRechargeType.isErrorEnabled = false
        }

        if (mDataBinding.inputRechargeType.text.toString()
                .equals(airTimeResponse.rechargeMobile.titleName)
        ) {
            if (mDataBinding.inputRechargePlan.text.toString().isEmpty()) {
                isValidForAll = false
                mDataBinding.inputLayoutRechargePlan.error =
                    LanguageData.getStringValue("PleaseSelectPlan")
                mDataBinding.inputLayoutRechargePlan.isErrorEnabled = true
            }else{
                mDataBinding.inputLayoutRechargePlan.error = ""
                mDataBinding.inputLayoutRechargePlan.isErrorEnabled = false
            }
        }

        if (mDataBinding.inputRechargeAmount.text.toString().isEmpty()) {
            isValidForAll = false
            mDataBinding.inputLayoutRechargeAmount.error =
                LanguageData.getStringValue("SelectAmount")
            mDataBinding.inputLayoutRechargeAmount.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutRechargeAmount.error = ""
            mDataBinding.inputLayoutRechargeAmount.isErrorEnabled = false
        }

        return isValidForAll
    }

    override fun afterTextChanged(p0: Editable?) {
        val msisdn = mDataBinding.inputPhoneNumber.text.toString().trim()
        val msisdnLenght = msisdn.length
        isNumberRegexMatches =
            !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_MSISDN_REGEX, msisdn))
    }

    fun isRegexMached(regex:String){
        val msisdn = mDataBinding.inputPhoneNumber.text.toString().trim()
        val msisdnLenght = msisdn.length
        isNumberRegexMatches =
            !(msisdnLenght > 0 && !Pattern.matches(regex, msisdn))
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var selectedFavorites = mDataBinding.spinnerSelectFavorites.selectedItem.toString()
        if (!selectedFavorites.equals(LanguageData.getStringValue("SelectFavorite"))) {
            selectedFavorites = selectedFavorites.substringAfter("-")
            mDataBinding.inputPhoneNumber.setText(selectedFavorites)
            mActivityViewModel.isUserSelectedFromFavorites.set(true)
            mDataBinding.inputLayoutPhoneNumber.hint =
                LanguageData.getStringValue("EnterReceiversMobileNumber")
            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
        } else {
            mDataBinding.inputPhoneNumber.setText("")
            mActivityViewModel.isUserSelectedFromFavorites.set(false)
            if (mDataBinding.inputLayoutPhoneNumber.isErrorEnabled) {

            } else {
                mDataBinding.inputPhoneNumber.clearFocus()
                mDataBinding.inputPhoneNumberHint.visibility = View.VISIBLE
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("MSISDNPlaceholder")
                mDataBinding.inputPhoneNumberHint.text =
                    LanguageData.getStringValue("EnterReceiversMobileNumber")
            }
        }
    }


    private fun checkNumberExistInFavorites(userMsisdn: String) {
        for (i in 0 until list_of_favorites.size) {
            var favoriteNumber = list_of_favorites[i].substringAfter("-")
            if (favoriteNumber.equals(userMsisdn)) {
                mActivityViewModel.isUserSelectedFromFavorites.set(true)
                Log.i("FavoritesCheck", "true")
                break
            } else {
                mActivityViewModel.isUserSelectedFromFavorites.set(false)
                Log.i("FavoritesCheck", "false")
            }
        }
    }

}