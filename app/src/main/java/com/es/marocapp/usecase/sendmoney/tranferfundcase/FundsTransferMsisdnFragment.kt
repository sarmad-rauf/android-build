package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFundsTransferEnterMsisdnBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.qrcode.ScanQRActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_funds_transfer_enter_msisdn.*
import java.util.regex.Pattern


class FundsTransferMsisdnFragment : BaseFragment<FragmentFundsTransferEnterMsisdnBinding>(),
    FundsTrasnferClickLisntener, AdapterView.OnItemSelectedListener, TextWatcher {

    private lateinit var mActivityViewModel: SendMoneyViewModel
     var profileName:String=""

    private var list_of_favorites = arrayListOf<String>()
    var isNumberRegexMatches = false

    override fun setLayout(): Int {
        return R.layout.fragment_funds_transfer_enter_msisdn
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
            listener = this@FundsTransferMsisdnFragment
        }
        Logger.debugLog("Abro", "Mechant agent Acount ${Constants.MERCHANT_AGENT_PROFILE_NAME}")
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
            activity as SendMoneyActivity, R.layout.layout_favorites_spinner_text,
            list_of_favorites as List<CharSequence>
        )
        mDataBinding.spinnerSelectFavorites.apply {
            adapter = adapterFavoriteType
        }
        mDataBinding.spinnerSelectFavorites.onItemSelectedListener =
            this@FundsTransferMsisdnFragment
        mActivityViewModel.isFundTransferUseCase.set(
            (activity as SendMoneyActivity).intent.getBooleanExtra(
                "isFundTransferUseCase",
                false
            )
        )
        mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set(
            (activity as SendMoneyActivity).intent.getBooleanExtra(
                "isInitiatePaymenetToMerchantUseCase",
                false
            )
        )
        mActivityViewModel.trasferTypeSelected.set(
            (activity as SendMoneyActivity).intent.getStringExtra(
                "useCaseType"
            )
        )


        (activity as SendMoneyActivity).setHeaderVisibility(true)
        //todo also here remove lenght-2 check in max line
        mDataBinding.inputPhoneNumber.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Constants.APP_MSISDN_LENGTH.toInt() - 2
            )
        )

        mDataBinding.btnScanQR.setOnClickListener {
            (activity as SendMoneyActivity).startQRScan(
                mDataBinding.inputPhoneNumber,
                mDataBinding.inputLayoutPhoneNumber,
                mDataBinding.inputPhoneNumberHint
            )

        }

        mActivityViewModel.popBackStackTo = -1
        mActivityViewModel.isUserSelectedFromFavorites.set(false)
        mDataBinding.inputPhoneNumber.addTextChangedListener(this)
        (activity as SendMoneyActivity).mInputField = mDataBinding.inputPhoneNumber
        (activity as SendMoneyActivity).mInputFieldLayout = mDataBinding.inputLayoutPhoneNumber
        (activity as SendMoneyActivity).mInputHint = mDataBinding.inputPhoneNumberHint
        setStrings()
        subscribeObserver()
        subScribeForAcountsResponse()

        mDataBinding.phonebook.setOnClickListener {
            (activity as SendMoneyActivity).openPhoneBook()
        }
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
    }

    private fun setStrings() {
//        mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
        mDataBinding.selectFavoriteTypeTitle.hint = LanguageData.getStringValue("SelectFavorite")
        mDataBinding.generateQrTv.text = LanguageData.getStringValue("ScanQr")

        mDataBinding.btnNext.text = LanguageData.getStringValue("BtnTitle_Next")

        mActivityViewModel.trasferTypeSelected.get()?.let {
            (activity as SendMoneyActivity).setHeaderTitle(
                it
            )
        }

        mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
        mDataBinding.inputPhoneNumberHint.text =
            LanguageData.getStringValue("EnterReceiversMobileNumber")

        mDataBinding.inputPhoneNumber.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setInputLayoutHint()
            } else {
                if (mDataBinding.inputLayoutPhoneNumber.isErrorEnabled) {
                    setInputLayoutHint()
                } else {
                    if (mDataBinding.inputPhoneNumber.text.isEmpty()) {
                        mDataBinding.inputPhoneNumberHint.visibility = View.VISIBLE
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("EnterReceiversMobileNumber")
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    } else {
                        setInputLayoutHint()
                    }
                }
            }
        }
    }

    fun setInputLayoutHint() {
        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
        mDataBinding.inputLayoutPhoneNumber.hint =
            LanguageData.getStringValue("EnterReceiversMobileNumber")
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@FundsTransferMsisdnFragment, Observer {
            DialogUtils.showErrorDialoge(activity as SendMoneyActivity, it)
        })

        mActivityViewModel.getAccountHolderAdditionalInfoResponseListner.observe(this@FundsTransferMsisdnFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.additionalinformation.isNullOrEmpty()) {
                        mActivityViewModel.isUserRegistered.set(false)
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                    } else {
                        if (it.additionalinformation[0].value.equals("TRUE", true)) {
                            mActivityViewModel.isUserRegistered.set(true)
                            var isProfileNameMatched: Boolean = false
                            for (i in Constants.MERCHENTAGENTPROFILEARRAY.indices) {
                                isProfileNameMatched =
                                    profileName.equals(Constants.MERCHENTAGENTPROFILEARRAY[i])
                                if(isProfileNameMatched)
                                {
                                    break
                                }
                            }

                            if (isProfileNameMatched) {
                                mActivityViewModel.requestForGetAccountsuAPI(requireContext())
                            }
                            else {
                                (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                            }
                        } else {
                            mActivityViewModel.isUserRegistered.set(false)
                            (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                        }
                    }
                } else {
                    DialogUtils.showErrorDialoge(activity as SendMoneyActivity, it.description)
                }
            }
        )

        mActivityViewModel.getRecieverInformationResponseListner.observe(this@FundsTransferMsisdnFragment,
            Observer {
                Logger.debugLog("Abro", "Acount reciever info respnse  ${it.toString()}")

                //Merchant payment Usacese
                if (mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!) {
                    if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                        mActivityViewModel.isAccountHolderInformationFailed.set(false)
                        //calling Acount reciever API
                         profileName=it.profileName
                        mActivityViewModel.requestForAccountHolderAddtionalInformationApi(
                            activity
                        )
                    }
                    else {

                        mActivityViewModel.isAccountHolderInformationFailed.set(true)
                        mActivityViewModel.isUserRegistered.set(false)
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                    }
                } else {
                    if (Constants.IS_AGENT_USER) {
                        //senMoney Usecase
                        if (mActivityViewModel.isFundTransferUseCase.get()!!) {
                            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                                (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                            } else {
                                DialogUtils.showErrorDialoge(activity, it.description)
                            }
                        }

                    } else {
                        if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                            mActivityViewModel.isAccountHolderInformationFailed.set(false)
                            mActivityViewModel.requestForAccountHolderAddtionalInformationApi(
                                activity
                            )
                        } else {
                            mActivityViewModel.isAccountHolderInformationFailed.set(true)
                            mActivityViewModel.isUserRegistered.set(false)
                            (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                        }
                    }
                }
            }
        )
    }

    fun subScribeForAcountsResponse() {
        mActivityViewModel.getAccountsResponseListner.observe(
            this@FundsTransferMsisdnFragment,
            Observer {
                //  Constants.MERCHANT_AGENT_PROFILE_NAME
                for (i in it.accounts.indices) {
                    Logger.debugLog("Abro", "Mechant agent Acount ${Constants.MERCHANT_AGENT_PROFILE_NAME}")
                    if (it.accounts.get(i).profileName.equals(Constants.MERCHANT_AGENT_PROFILE_NAME)) {
                        var fri = it.accounts.get(i).accountFri.substring(4, it.accounts.get(i).accountFri.length)
                        mActivityViewModel.addFri(fri)
                        //mActivityViewModel.transferdAcountFri = fri
                         (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                      break
                    }
                    it.toString()
                  //  (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                    Logger.debugLog("Abro", "Reciever Acount FRI ${mActivityViewModel.transferdAcountFri} ")
                }
            })
    }

    override fun onNextClickListner(view: View) {
        //TODO MSISDN Lenght Check
        if (mDataBinding.inputPhoneNumber.text.isNotEmpty() && mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
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

                    mActivityViewModel.requestForGetAccountRecieverInformationApi(
                        activity,
                        userMSISDNwithPrefix
                    )
                } else {
                    mDataBinding.inputLayoutPhoneNumber.error =
                        LanguageData.getStringValue("PleaseEnterValidMobileNumber")
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterReceiversMobileNumber")
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                }
            } else {
                mDataBinding.inputLayoutPhoneNumber.error =
                    LanguageData.getStringValue("PleaseEnterValidMobileNumber")
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("EnterReceiversMobileNumber")
                mDataBinding.inputPhoneNumberHint.visibility = View.GONE
            }
        }
    }

    private fun checkNumberExistInFavorites(userMsisdn: String) {
        for (i in 0 until list_of_favorites.size) {
            var favoriteNumber = list_of_favorites[i].substringAfter("-")
            if (favoriteNumber.equals(userMsisdn)) {
                mActivityViewModel.isUserSelectedFromFavorites.set(true)
                break
            } else {
                mActivityViewModel.isUserSelectedFromFavorites.set(false)
                Logger.debugLog("FavoritesCheck", "false")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                DialogUtils.showErrorDialoge(
                    activity,
                    LanguageData.getStringValue("PleaseScanValidQRDot")
                )
            } else {
                var sResult = result.contents
                sResult = StringBuilder(sResult!!).insert(2, "-").insert(6, "-").toString()
                input_phone_number.setText(sResult)
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data)
            DialogUtils.showErrorDialoge(
                activity,
                LanguageData.getStringValue("PleaseScanValidQRDot")
            )
        }
    }

    override fun onBackClickListner(view: View) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
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


//    android:ellipsize="end"
}