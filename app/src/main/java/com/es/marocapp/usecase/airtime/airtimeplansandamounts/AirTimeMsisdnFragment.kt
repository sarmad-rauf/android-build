package com.es.marocapp.usecase.airtime.airtimeplansandamounts

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

class AirTimeMsisdnFragment : BaseFragment<FragmentAirTimeMsisdnBinding>(), AirTimeClickListner,
    AdapterView.OnItemSelectedListener {

    private lateinit var mActivityViewModel: AirTimeViewModel

    private var list_of_favorites = arrayListOf<String>()

    var msisdnEntered = ""

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

        if(mActivityViewModel.isRechargeFixeUseCase.get()!!){
            (activity as AirTimeActivity).setHeaderTitle(
                mActivityViewModel.airTimeSelected.get()!!
            )
        }

        if(mActivityViewModel.isRechargeMobileUseCase.get()!!){
            (activity as AirTimeActivity).setHeaderTitle(
                mActivityViewModel.airTimePlanSelected.get()!!
            )
        }

        for(contacts in Constants.mContactListArray){
            var contactNumber = contacts.fri
            contactNumber = contactNumber.substringBefore("@")
            contactNumber = contactNumber.substringBefore("/")
            contactNumber = contactNumber.removePrefix(Constants.APP_MSISDN_PREFIX)
            contactNumber = "0$contactNumber"
            list_of_favorites.add(contactNumber)
        }
        list_of_favorites.add(0,LanguageData.getStringValue("SelectFavorite").toString())

        val adapterFavoriteType = ArrayAdapter<CharSequence>(activity as AirTimeActivity, R.layout.layout_favorites_spinner_text,
            list_of_favorites as List<CharSequence>
        )
        mDataBinding.spinnerSelectFavorites.apply {
            adapter = adapterFavoriteType
        }
        mDataBinding.spinnerSelectFavorites.onItemSelectedListener = this@AirTimeMsisdnFragment

        (activity as AirTimeActivity).mDataBinding.headerAirTime.rootView.tv_company_title.text = mActivityViewModel.airTimeAmountSelected.get()!!
        (activity as AirTimeActivity).mDataBinding.headerAirTime.rootView.img_company_icons.setImageResource(R.drawable.others)

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

        setStrings()
        subscribeObserver()

    }

    private fun setStrings() {
        mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
        mDataBinding.selectFavoriteTypeTitle.hint = LanguageData.getStringValue("SelectFavorite")
        mDataBinding.btnNext.text = LanguageData.getStringValue("Submit")
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
            mActivityViewModel.requestForAirTimeQuoteApi(activity,msisdnEntered)
        }
    }

    override fun onBackClickListner(view: View) {

    }

    private fun isValidForAll(): Boolean {
        var isValidForAll = true

        if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() && mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
            isValidForAll = false
            mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidAmount")
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
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

                msisdnEntered = userMSISDNwithPrefix
            } else {
                isValidForAll = false
                mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidAmount")
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
            }
        }

        return isValidForAll
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var selectedFavorites = mDataBinding.spinnerSelectFavorites.selectedItem.toString()
        if(!selectedFavorites.equals(LanguageData.getStringValue("SelectFavorite"))){
            mDataBinding.inputPhoneNumber.setText(selectedFavorites)
            mActivityViewModel.isUserSelectedFromFavorites.set(true)
        }
    }

    private fun checkNumberExistInFavorites(userMsisdn: String) {
        for(i in 0 until list_of_favorites.size){
            if(list_of_favorites[i].equals(userMsisdn)){
                mActivityViewModel.isUserSelectedFromFavorites.set(true)
                break
            }
        }
    }

}