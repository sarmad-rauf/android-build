package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
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
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_funds_transfer_enter_msisdn.*


class FundsTransferMsisdnFragment : BaseFragment<FragmentFundsTransferEnterMsisdnBinding>(),
    FundsTrasnferClickLisntener, AdapterView.OnItemSelectedListener {

    private lateinit var mActivityViewModel : SendMoneyViewModel

    private var list_of_favorites = arrayListOf<String>()

    override fun setLayout(): Int {
        return R.layout.fragment_funds_transfer_enter_msisdn
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
            listener = this@FundsTransferMsisdnFragment
        }

        list_of_favorites.clear()
        for(contacts in Constants.mContactListArray){
            var contactNumber = contacts.fri
            contactNumber = contactNumber.substringBefore("@")
            contactNumber = contactNumber.substringBefore("/")
            contactNumber = contactNumber.removePrefix(Constants.APP_MSISDN_PREFIX)
            contactNumber = "0$contactNumber"
            //todo also here remove lenght-2 check in max line
            if(contactNumber.length.equals(Constants.APP_MSISDN_LENGTH.toInt() - 2)){
                list_of_favorites.add(contactNumber)
            }
        }
        list_of_favorites.add(0,LanguageData.getStringValue("SelectFavorite").toString())

        val adapterFavoriteType = ArrayAdapter<CharSequence>(activity as SendMoneyActivity, R.layout.layout_favorites_spinner_text,
            list_of_favorites as List<CharSequence>
        )
        mDataBinding.spinnerSelectFavorites.apply {
            adapter = adapterFavoriteType
        }
        mDataBinding.spinnerSelectFavorites.onItemSelectedListener = this@FundsTransferMsisdnFragment
        mActivityViewModel.isFundTransferUseCase.set((activity as SendMoneyActivity).intent.getBooleanExtra("isFundTransferUseCase",false))
        mActivityViewModel.isInitiatePaymenetToMerchantUseCase.set((activity as SendMoneyActivity).intent.getBooleanExtra("isInitiatePaymenetToMerchantUseCase",false))
        mActivityViewModel.trasferTypeSelected.set((activity as SendMoneyActivity).intent.getStringExtra("useCaseType"))


        (activity as SendMoneyActivity).setHeaderVisibility(true)
        //todo also here remove lenght-2 check in max line
        mDataBinding.inputPhoneNumber.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Constants.APP_MSISDN_LENGTH.toInt() - 2
            )
        )

        mDataBinding.btnScanQR.setOnClickListener{
            val integrator = IntentIntegrator(activity)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            integrator.setPrompt("")
            integrator.setOrientationLocked(false)
            integrator.setCameraId(0)
            integrator.setBeepEnabled(false)
            integrator.setBarcodeImageEnabled(false)
            integrator.initiateScan()
        }

        mActivityViewModel.popBackStackTo = -1
        mActivityViewModel.isUserSelectedFromFavorites.set(false)
        setStrings()
        subscribeObserver()
    }

    private fun setStrings() {
        mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
        mDataBinding.selectFavoriteTypeTitle.hint = LanguageData.getStringValue("SelectFavorite")
        mDataBinding.btnScanQR.text = LanguageData.getStringValue("ScanQr")

        mDataBinding.btnNext.text = LanguageData.getStringValue("BtnTitle_Next")

        mActivityViewModel.trasferTypeSelected.get()?.let {
            (activity as SendMoneyActivity).setHeaderTitle(
                it
            )
        }

    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@FundsTransferMsisdnFragment, Observer {
            DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it)
        })

        mActivityViewModel.getAccountHolderAdditionalInfoResponseListner.observe(this@FundsTransferMsisdnFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(it.additionalinformation.isNullOrEmpty()){
                        mActivityViewModel.isUserRegistered.set(false)
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                    }else{
                        if(it.additionalinformation[0].value.equals("TRUE",true)){
                            mActivityViewModel.isUserRegistered.set(true)
                            (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                        }else{
                            mActivityViewModel.isUserRegistered.set(false)
                            (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                        }
                    }
                }else{
                    DialogUtils.showErrorDialoge(activity as SendMoneyActivity,it.description)
                }
            }
        )

        mActivityViewModel.getAccountHolderInformationResponseListner.observe(this@FundsTransferMsisdnFragment,
            Observer {
                if(Constants.IS_AGENT_USER){
                    if(mActivityViewModel.isFundTransferUseCase.get()!!){
                        if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                            (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                        }else{
                            DialogUtils.showErrorDialoge(activity,it.description)
                        }
                    }
                    if(mActivityViewModel.isInitiatePaymenetToMerchantUseCase.get()!!){
                        if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                            mActivityViewModel.isAccountHolderInformationFailed.set(false)
                            mActivityViewModel.requestForAccountHolderAddtionalInformationApi(activity)
                        }else{
                            mActivityViewModel.isAccountHolderInformationFailed.set(true)
                            mActivityViewModel.isUserRegistered.set(false)
                            (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                        }
                    }
                }else{
                    if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                        mActivityViewModel.isAccountHolderInformationFailed.set(false)
                        mActivityViewModel.requestForAccountHolderAddtionalInformationApi(activity)
                    }else{
                        mActivityViewModel.isAccountHolderInformationFailed.set(true)
                        mActivityViewModel.isUserRegistered.set(false)
                        (activity as SendMoneyActivity).navController.navigate(R.id.action_fundsTransferMsisdnFragment_to_fundsTransferAmountFragment)
                    }
                }
            }
        )
    }

    override fun onNextClickListner(view: View) {
        //TODO MSISDN Lenght Check
        if(mDataBinding.inputPhoneNumber.text.isNotEmpty() && mDataBinding.inputPhoneNumber.text.toString().length< Constants.APP_MSISDN_LENGTH.toInt()-2){
            mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidMobileNumber")
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutPhoneNumber.error = ""
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

            var userMsisdn = mDataBinding.inputPhoneNumber.text.toString()
            if(userMsisdn.startsWith("0",false)){
                checkNumberExistInFavorites(userMsisdn)
                mDataBinding.inputLayoutPhoneNumber.error = ""
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                var userMSISDNwithPrefix = userMsisdn.removePrefix("0")
                userMSISDNwithPrefix = Constants.APP_MSISDN_PREFIX + userMSISDNwithPrefix
                userMSISDNwithPrefix = userMSISDNwithPrefix.removePrefix("+")

                mActivityViewModel.requestForGetAccountHolderInformationApi(activity,userMSISDNwithPrefix)
            }else{
                mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidMobileNumber")
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                DialogUtils.showErrorDialoge(activity, LanguageData.getStringValue("PleaseScanValidQRDot"))
            } else {
                var sResult = result.contents
                sResult = StringBuilder(sResult!!).insert(2, "-").insert(6, "-").toString()
                input_phone_number.setText(sResult)
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data)
            DialogUtils.showErrorDialoge(activity, LanguageData.getStringValue("PleaseScanValidQRDot"))
        }
    }

    override fun onBackClickListner(view: View) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        var selectedFavorites = mDataBinding.spinnerSelectFavorites.selectedItem.toString()
        if(!selectedFavorites.equals(LanguageData.getStringValue("SelectFavorite"))){
            mDataBinding.inputPhoneNumber.setText(selectedFavorites)
            mActivityViewModel.isUserSelectedFromFavorites.set(true)
        }
    }

}