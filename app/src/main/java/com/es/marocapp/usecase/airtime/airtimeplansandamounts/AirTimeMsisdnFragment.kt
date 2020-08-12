package com.es.marocapp.usecase.airtime.airtimeplansandamounts

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentAirTimeMsisdnBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.airtime.AirTimeActivity
import com.es.marocapp.usecase.airtime.AirTimeClickListner
import com.es.marocapp.usecase.airtime.AirTimeViewModel
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.layout_activity_header.view.*
import java.util.regex.Pattern

class AirTimeMsisdnFragment : BaseFragment<FragmentAirTimeMsisdnBinding>(), AirTimeClickListner,
    AdapterView.OnItemSelectedListener, TextWatcher {

    private lateinit var mActivityViewModel: AirTimeViewModel

    private var list_of_favorites = arrayListOf<String>()

    var msisdnEntered = ""
    var isNumberRegexMatches = false


    override fun setLayout(): Int {
        return R.layout.fragment_air_time_msisdn
    }

    override fun init(savedInstanceState: Bundle?) {

        mActivityViewModel = ViewModelProvider(activity as AirTimeActivity).get(
            AirTimeViewModel::class.java
        )
        mDataBinding.apply {
            listner = this@AirTimeMsisdnFragment
        }

        if (mActivityViewModel.isRechargeFixeUseCase.get()!!) {
            (activity as AirTimeActivity).setHeaderTitle(
                mActivityViewModel.airTimeSelected.get()!!
            )
        }

        if (mActivityViewModel.isRechargeMobileUseCase.get()!!) {
            (activity as AirTimeActivity).setHeaderTitle(
                mActivityViewModel.airTimePlanSelected.get()!!
            )
        }

        list_of_favorites.clear()
        for (contacts in Constants.mContactListArray) {
            var contactNumber = contacts.fri
            var contactName = contacts.contactName
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
        mDataBinding.spinnerSelectFavorites.onItemSelectedListener = this@AirTimeMsisdnFragment

        (activity as AirTimeActivity).mDataBinding.headerAirTime.rootView.tv_company_title.text =
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mActivityViewModel.airTimeAmountSelected.get()!!
        (activity as AirTimeActivity).mDataBinding.headerAirTime.rootView.img_company_icons.setImageResource(
            R.drawable.others_blue
        )

        (activity as AirTimeActivity).setHeaderVisibility(true)
        (activity as AirTimeActivity).setCompanyIconToolbarVisibility(true)


        mActivityViewModel.popBackStackTo = R.id.airTimeAmountFragment

        //todo also here remove lenght-2 check in max line
        mDataBinding.inputPhoneNumber.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Constants.APP_MSISDN_LENGTH.toInt() - 2
            )
        )

        mActivityViewModel.isUserSelectedFromFavorites.set(false)
        mDataBinding.inputPhoneNumber.addTextChangedListener(this)


        (activity as AirTimeActivity).setVisibilityAndTextToImage(mActivityViewModel.airTimeAmountSelected.get()!!)

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

        setStrings()
        subscribeObserver()

    }

    private fun setStrings() {
        mDataBinding.selectFavoriteTypeTitle.hint = LanguageData.getStringValue("SelectFavorite")
        mDataBinding.btnNext.text = LanguageData.getStringValue("Submit")
        mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
        mDataBinding.inputPhoneNumberHint.text =
            LanguageData.getStringValue("EnterReceiversMobileNumber")
    }

    private fun subscribeObserver() {
        mActivityViewModel.getAirTimeQuoteResponseListner.observe(this@AirTimeMsisdnFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.quoteList.isNotEmpty()) {
                        mActivityViewModel.feeAmount = it.quoteList[0].fee.amount.toString()
                        mActivityViewModel.qouteId = it.quoteList[0].quoteid
                    }
                    (activity as AirTimeActivity).navController.navigate(R.id.action_airTimeMsisdnFragment_to_airTimeConfirmationFragment)
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            })
    }

    override fun onNextClickListner(view: View) {
        if (isValidForAll()) {
            mActivityViewModel.requestForAirTimeQuoteApi(activity, msisdnEntered)
        }
    }

    override fun onBackClickListner(view: View) {

    }

    private fun isValidForAll(): Boolean {
        var isValidForAll = true

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

        return isValidForAll
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var selectedFavorites = mDataBinding.spinnerSelectFavorites.selectedItem.toString()
        if (!selectedFavorites.equals(LanguageData.getStringValue("SelectFavorite"))) {
            selectedFavorites = selectedFavorites.substringAfter("-")
            mDataBinding.inputPhoneNumber.setText(selectedFavorites)
            mActivityViewModel.isUserSelectedFromFavorites.set(true)
            mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
        } else {
            mDataBinding.inputPhoneNumber.setText("")
            mActivityViewModel.isUserSelectedFromFavorites.set(false)
            if(mDataBinding.inputLayoutPhoneNumber.isErrorEnabled){

            }else{
                mDataBinding.inputPhoneNumber.clearFocus()
                mDataBinding.inputPhoneNumberHint.visibility = View.VISIBLE
                mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
                mDataBinding.inputPhoneNumberHint.text = LanguageData.getStringValue("EnterReceiversMobileNumber")
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