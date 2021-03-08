package com.es.marocapp.usecase.favorites

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
import com.es.marocapp.databinding.FragmentFavoritesEnterNumberBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger
import java.util.regex.Pattern

class FavoriteEnterContactFragment : BaseFragment<FragmentFavoritesEnterNumberBinding>(),
    FavoritesPaymentClickListener, TextWatcher, AdapterView.OnItemSelectedListener {

    private lateinit var mActivitViewModel: FavoritesViewModel

    var msisdnEntered = ""
    var code = ""

    var isNumberRegexMatches = false
    var isCodeRegexMatches = false

    private var list_of_paymentType_bill : ArrayList<String> = ArrayList()

    private var isInternetTypeSelected = false
    private var isMobileUseCaseSelected = false
    private var isFixeUseCaseSelected = false

    override fun setLayout(): Int {
        return R.layout.fragment_favorites_enter_number
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivitViewModel = ViewModelProvider(activity as FavoritesActivity).get(FavoritesViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivitViewModel
            listener = this@FavoriteEnterContactFragment
        }

        if(mActivitViewModel.isPaymentSelected.get()!!){
            if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                mActivitViewModel.popBackStackTo = R.id.favoriteDetailFragment
                mDataBinding.inputPhoneNumber.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.APP_CIL_LENGTH.toInt()))
            }else{
                mActivitViewModel.popBackStackTo = R.id.favoritesAddOrViewFragment
                mDataBinding.inputPhoneNumber.filters = arrayOf<InputFilter>(
                    InputFilter.LengthFilter(
                    Constants.APP_MSISDN_LENGTH.toInt() - 2
                ))
            }
        }else{
            mActivitViewModel.popBackStackTo = R.id.favoritesAddOrViewFragment
            mDataBinding.inputPhoneNumber.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(
                Constants.APP_MSISDN_LENGTH.toInt() - 2
            ))
        }

        (activity as FavoritesActivity).setHeader(LanguageData.getStringValue("Add").toString())
        mDataBinding.inputPhoneNumber.addTextChangedListener(this)
        mDataBinding.inputCode.addTextChangedListener(this)

        mDataBinding.inputPhoneNumber.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setInputHint()
            } else {
                if (mDataBinding.inputLayoutPhoneNumber.isErrorEnabled) {
                    setInputHint()
                }else{
                    if (mDataBinding.inputPhoneNumber.text.isEmpty()) {
                        mDataBinding.inputPhoneNumberHint.visibility = View.VISIBLE
                        if(mActivitViewModel.isPaymentSelected.get()!!){
                            if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                                mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("CINPlaceholder")
                                mDataBinding.inputPhoneNumberHint.text =
                                    LanguageData.getStringValue("EnterCilNumber")
                            }else{
                                mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
                                mDataBinding.inputPhoneNumberHint.text =
                                    LanguageData.getStringValue("EnterContactNumber")
                            }
                        }else{
                            mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
                            mDataBinding.inputPhoneNumberHint.text =
                                LanguageData.getStringValue("EnterContactNumber")
                        }

                    } else {
                        setInputHint()
                    }
                }
            }
        }

        list_of_paymentType_bill.clear()
        list_of_paymentType_bill.apply {
            add(LanguageData.getStringValue("PostpaidMobile").toString())
            add(LanguageData.getStringValue("PostpaidFix").toString())
            add(LanguageData.getStringValue("Internet").toString())
        }

        val adapterFavoriteType = ArrayAdapter<CharSequence>(
            activity as FavoritesActivity, R.layout.layout_favorites_spinner_text,
            list_of_paymentType_bill as List<CharSequence>
        )
        mDataBinding.spinnerSelectBillType.apply {
            adapter = adapterFavoriteType
        }
        mDataBinding.spinnerSelectBillType.onItemSelectedListener = this@FavoriteEnterContactFragment

        setVisibility()
        setStrings()
        subscribeObserver()
    }

    fun setInputHint(){
        if(mActivitViewModel.isPaymentSelected.get()!!){
            if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("EnterCilNumber")
            }else{
                /*mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("EnterContactNumber")*/
                if (isInternetTypeSelected) {
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("PhoneNumber")
                } else if (isMobileUseCaseSelected || isFixeUseCaseSelected) {
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterPaymentIdentifier")
                }else{
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterContactNumber")
                }
            }
        }else{
            mDataBinding.inputLayoutPhoneNumber.hint =
                LanguageData.getStringValue("EnterContactNumber")
        }
        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
    }

    private fun subscribeObserver() {
        mActivitViewModel.errorText.observe(this@FavoriteEnterContactFragment, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        })

        mActivitViewModel.getAddFavoritesResponseListner.observe(this@FavoriteEnterContactFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(!it.contactList.isNullOrEmpty()){
                        Constants.mContactListArray.clear()
                        Constants.mContactListArray.addAll(it.contactList)
                        DialogUtils.showSuccessDialog(activity,it.description,object : DialogUtils.OnConfirmationDialogClickListner{
                            override fun onDialogYesClickListner() {
                                (activity as FavoritesActivity).navController.popBackStack(R.id.favoriteTypesFragment,false)
                            }

                            override fun onDialogNoClickListner() {

                            }
                        })
                    }
                }else{
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            }
        )
    }

    private fun setStrings() {
        mDataBinding.btnAddToFavorites.text = LanguageData.getStringValue("BtnTitle_AddToFavorites")
        if(mActivitViewModel.isPaymentSelected.get()!!){
            if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("CINPlaceholder")
                mDataBinding.inputPhoneNumberHint.text =
                    LanguageData.getStringValue("EnterCilNumber")
            }else{
                /*mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
                mDataBinding.inputPhoneNumberHint.text =
                    LanguageData.getStringValue("EnterContactNumber")*/
                if (isMobileUseCaseSelected || isFixeUseCaseSelected) {
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("MSISDNPlaceholder")
                    mDataBinding.inputPhoneNumberHint.text =
                        LanguageData.getStringValue("PhoneNumber")
                } else if (isInternetTypeSelected) {
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("MSISDNPlaceholder")
                    mDataBinding.inputPhoneNumberHint.text =
                        LanguageData.getStringValue("EnterPaymentIdentifier")
                }else{
                    mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
                    mDataBinding.inputPhoneNumberHint.text =
                        LanguageData.getStringValue("EnterContactNumber")
                }
            }
        }else{
            mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
            mDataBinding.inputPhoneNumberHint.text =
                LanguageData.getStringValue("EnterContactNumber")
        }

        mDataBinding.inputLayoutName.hint = LanguageData.getStringValue("EnterName")
        mDataBinding.selectBillTypeTypeTitle.hint = LanguageData.getStringValue("SelectBillType")
        //mDataBinding.inputLayoutCode.hint = LanguageData.getStringValue("EnterCode")
    }

    override fun onNextButtonClick(view: View) {
        if(isValidForAll()){
            var nickName = mDataBinding.inputName.text.toString().trim()
            if(mActivitViewModel.isPaymentSelected.get()!!){
                if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                    //Util_Redal@MyNickName,codeCreance,creancierID,nomChamp,refTxFatourati
                    var fatoratiNickName = "Util_${mActivitViewModel.fatoratiTypeSelected}@$nickName,${mActivitViewModel.codeCreance},${mActivitViewModel.creancierID}," +
                            "${mActivitViewModel.nomChamp},${mActivitViewModel.refTxFatourati}"
                    mActivitViewModel.requestForAddFavoritesApi(activity,fatoratiNickName,Constants.getFatoratiAlias(msisdnEntered))
                }else{
                    if(isInternetTypeSelected){
                        nickName = "Telec_Internet@$nickName"
                        msisdnEntered = Constants.getPostPaidInternetDomainAlias(msisdnEntered)
                    }else if(isMobileUseCaseSelected){
                        nickName = "Telec_PostpaidMobile@$nickName,${code}"
                        msisdnEntered = Constants.getPostPaidMobileDomainAlias(msisdnEntered)
                        Logger.debugLog("BillPaymentCode",code)

                    }else if(isFixeUseCaseSelected){
                        nickName = "Telec_PostpaidFix@$nickName,${code}"
                        msisdnEntered = Constants.getPostPaidFixedDomainAlias(msisdnEntered)
                        Logger.debugLog("BillPaymentCode",code)
                    }

                    Logger.debugLog("BillPaymentNickName",nickName)
                    Logger.debugLog("BillPaymentMsisdn",msisdnEntered)
                    mActivitViewModel.requestForAddFavoritesApi(activity,nickName,msisdnEntered)
                }
            }else{
                mActivitViewModel.requestForAddFavoritesApi(activity,nickName,Constants.getNumberMsisdn(msisdnEntered))
            }

        }
    }

    override fun onDeleteButtonClick(view: View) {

    }

    private fun isValidForAll(): Boolean {
        var isValidForAll = true

        if(mActivitViewModel.isPaymentSelected.get()!!){
            if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                //todo NUmber Lenght is Pending
                if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() || mDataBinding.inputPhoneNumber.text.toString().length < 6) {
                    isValidForAll = false
                    mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidCILNumber")
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterCilNumber")
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                } else {
                    mDataBinding.inputLayoutPhoneNumber.error = ""
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                    if(isNumberRegexMatches){
                        mDataBinding.inputLayoutPhoneNumber.error = ""
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                        msisdnEntered = mDataBinding.inputPhoneNumber.text.toString().trim()
                    }else{
                        isValidForAll = false
                        mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidCILNumber")
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("EnterCilNumber")
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    }

                }
            }else{

                if (isMobileUseCaseSelected || isFixeUseCaseSelected) {
                    if (mDataBinding.inputPhoneNumber.text.isNotEmpty() && mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
                        mDataBinding.inputLayoutPhoneNumber.error =
                            LanguageData.getStringValue("EnterValidPhoneNumber")
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                        isValidForAll = false
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("PhoneNumber")
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    } else {
                        mDataBinding.inputLayoutPhoneNumber.error = ""
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                        var userMsisdn = mDataBinding.inputPhoneNumber.text.toString()

                        if (isNumberRegexMatches) {
                            mDataBinding.inputLayoutPhoneNumber.error = ""
                            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                            msisdnEntered = userMsisdn
                        } else {
                            isValidForAll = false
                            mDataBinding.inputLayoutPhoneNumber.error =
                                LanguageData.getStringValue("EnterValidPhoneNumber")
                            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                            mDataBinding.inputLayoutPhoneNumber.hint =
                                LanguageData.getStringValue("PhoneNumber")
                            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                        }
                    }

                    if(Constants.IS_AGENT_USER) {
                        if (mDataBinding.inputCode.text.isNullOrEmpty() || mDataBinding.inputCode.text.toString()
                                .isEmpty()
                        ) {
                            isValidForAll = false
                            mDataBinding.inputLayoutCode.error =
                                LanguageData.getStringValue("PleaseEnterValidCode")
                            mDataBinding.inputLayoutCode.isErrorEnabled = true
                        } else {
                            if (isCodeRegexMatches) {
                                mDataBinding.inputLayoutCode.error = ""
                                mDataBinding.inputLayoutCode.isErrorEnabled = false
                                code = mDataBinding.inputCode.text.toString().trim()
                            } else {
                                isValidForAll = false
                                mDataBinding.inputLayoutCode.error =
                                    LanguageData.getStringValue("PleaseEnterValidCode")
                                mDataBinding.inputLayoutCode.isErrorEnabled = true
                            }
                        }
                    }

                }

                if (isInternetTypeSelected) {
                    if (!mDataBinding.inputPhoneNumber.text.isNotEmpty()) {
                        mDataBinding.inputLayoutPhoneNumber.error =
                            LanguageData.getStringValue("EnterValidIdentifier")
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                        isValidForAll = false
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("EnterPaymentIdentifier")
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    } else {
                        mDataBinding.inputLayoutPhoneNumber.error = ""
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                        var userMsisdn = mDataBinding.inputPhoneNumber.text.toString()

                        if (isNumberRegexMatches) {
                            mDataBinding.inputLayoutPhoneNumber.error = ""
                            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                            msisdnEntered = userMsisdn
                        } else {
                            isValidForAll = false
                            mDataBinding.inputLayoutPhoneNumber.error =
                                LanguageData.getStringValue("EnterValidIdentifier")
                            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                            mDataBinding.inputLayoutPhoneNumber.hint =
                                LanguageData.getStringValue("EnterPaymentIdentifier")
                            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                        }

                    }
                }
                /*if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() || mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
                    isValidForAll = false
                    mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterContactNumber")
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                } else {
                    mDataBinding.inputLayoutPhoneNumber.error = ""
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                    var userMsisdn = mDataBinding.inputPhoneNumber.text.toString()
                    if (userMsisdn.startsWith("0", false)) {
                        mDataBinding.inputLayoutPhoneNumber.error = ""
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                        var userMSISDNwithPrefix = userMsisdn.removePrefix("0")
                        userMSISDNwithPrefix = Constants.APP_MSISDN_PREFIX + userMSISDNwithPrefix
                        userMSISDNwithPrefix = userMSISDNwithPrefix.removePrefix("+")

                        if(isNumberRegexMatches){
                            mDataBinding.inputLayoutPhoneNumber.error = ""
                            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                            msisdnEntered = userMSISDNwithPrefix
                        }else{
                            isValidForAll = false
                            mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                            mDataBinding.inputLayoutPhoneNumber.hint =
                                LanguageData.getStringValue("EnterContactNumber")
                            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                        }
                    } else {
                        isValidForAll = false
                        mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("EnterContactNumber")
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    }
                }*/
            }
        }else{
            if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() || mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
                isValidForAll = false
                mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("EnterContactNumber")
                mDataBinding.inputPhoneNumberHint.visibility = View.GONE
            } else {
                mDataBinding.inputLayoutPhoneNumber.error = ""
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                var userMsisdn = mDataBinding.inputPhoneNumber.text.toString()
                if (userMsisdn.startsWith("0", false)) {
                    mDataBinding.inputLayoutPhoneNumber.error = ""
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                    var userMSISDNwithPrefix = userMsisdn.removePrefix("0")
                    userMSISDNwithPrefix = Constants.APP_MSISDN_PREFIX + userMSISDNwithPrefix
                    userMSISDNwithPrefix = userMSISDNwithPrefix.removePrefix("+")

                    if(isNumberRegexMatches){
                        mDataBinding.inputLayoutPhoneNumber.error = ""
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                        msisdnEntered = userMSISDNwithPrefix
                    }else{
                        isValidForAll = false
                        mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("EnterContactNumber")
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    }
                } else {
                    isValidForAll = false
                    mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterContactNumber")
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                }
            }
        }

        if (mDataBinding.inputName.text.isNullOrEmpty()) {
            isValidForAll = false
            mDataBinding.inputLayoutName.error = LanguageData.getStringValue("PleaseEnterName")
            mDataBinding.inputLayoutName.isErrorEnabled = true
        } else {
            mDataBinding.inputLayoutName.error = ""
            mDataBinding.inputLayoutName.isErrorEnabled = false
        }

        return isValidForAll
    }

    override fun afterTextChanged(editable : Editable?) {
        /*var msisdn = mDataBinding.inputPhoneNumber.text.toString().trim()
        var msisdnLenght = msisdn.length

        if(mActivitViewModel.isPaymentSelected.get()!!){
            if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                isNumberRegexMatches =
                    !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_CIL_REGEX, msisdn))
            }else{
                isNumberRegexMatches =
                    !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_MSISDN_REGEX, msisdn))
            }
        }else{
            isNumberRegexMatches =
                !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_MSISDN_REGEX, msisdn))
        }*/
        if (editable.hashCode() == mDataBinding.inputPhoneNumber.text.hashCode()) {
            var msisdn = mDataBinding.inputPhoneNumber.text.toString().trim()
            var msisdnLenght = msisdn.length

            if(mActivitViewModel.isPaymentSelected.get()!!){
                if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                    isNumberRegexMatches =
                        !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_CIL_REGEX, msisdn))
                }else{
                    if(isFixeUseCaseSelected){
                        isNumberRegexMatches =
                            !(msisdnLenght > 0 && !Pattern.matches(
                                Constants.APP_MSISDN_POSTPAIDBILL_FIXE_REGEX,
                                msisdn
                            ))
                    }

                    if(isMobileUseCaseSelected){
                        isNumberRegexMatches =
                            !(msisdnLenght > 0 && !Pattern.matches(
                                Constants.APP_MSISDN_POSTPAIDBILL_MOBILE_REGEX,
                                msisdn
                            ))
                    }
                    if(isInternetTypeSelected){
                        isNumberRegexMatches =
                            !(msisdnLenght > 0 && !Pattern.matches(
                                Constants.APP_MSISDN_POSTPAIDBILL_INTERNET_REGEX,
                                msisdn
                            ))
                    }
                }
            }else{
                isNumberRegexMatches =
                    !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_MSISDN_REGEX, msisdn))
            }


        } else if (editable.hashCode() == mDataBinding.inputCode.text.hashCode()) {
            var code = mDataBinding.inputCode.text.toString().trim()
            var codeLenght = code.length
            isCodeRegexMatches =
                !(codeLenght > 0 && !Pattern.matches(Constants.APP_BILL_PAYMENT_CODE_REGEX, code))
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var selectedPaymentType = mDataBinding.spinnerSelectBillType.selectedItem.toString()
        if(selectedPaymentType.equals(LanguageData.getStringValue("Internet").toString())){
            mDataBinding.inputLayoutCode.visibility = View.GONE
            isInternetTypeSelected = true
            isMobileUseCaseSelected = false
            isFixeUseCaseSelected = false
            mDataBinding.inputPhoneNumber.clearFocus()
            mDataBinding.inputPhoneNumber.setText("")
            mDataBinding.inputCode.setText("")
            setStrings()
        }else if(selectedPaymentType.equals(LanguageData.getStringValue("PostpaidFix").toString())){
          //  mDataBinding.inputLayoutCode.visibility = View.VISIBLE
          //  mDataBinding.inputLayoutCode.visibility = View.GONE
           // mDataBinding.inputCode.setText("")
            isInternetTypeSelected = false
            isMobileUseCaseSelected = false
            isFixeUseCaseSelected = true
            mDataBinding.inputPhoneNumber.clearFocus()
            mDataBinding.inputPhoneNumber.setText("")
            mDataBinding.inputCode.setText("")
            setStrings()
        }else if(selectedPaymentType.equals(LanguageData.getStringValue("PostpaidMobile").toString())){
           // mDataBinding.inputLayoutCode.visibility = View.VISIBLE
        //    mDataBinding.inputLayoutCode.visibility = View.GONE
          //  mDataBinding.inputCode.setText("")
            isInternetTypeSelected = false
            isMobileUseCaseSelected = true
            isFixeUseCaseSelected = false
            mDataBinding.inputPhoneNumber.clearFocus()
            mDataBinding.inputCode.setText("")
            setStrings()
        }
    }

    fun setVisibility(){
        if(mActivitViewModel.isPaymentSelected.get()!!){
            if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                mDataBinding.spinnerSelectBillType.visibility = View.GONE
                mDataBinding.inputLayoutCode.visibility = View.GONE
                mDataBinding.selectBillTypeTypeTitle.visibility = View.GONE

            }else{
                mDataBinding.spinnerSelectBillType.visibility = View.VISIBLE
                mDataBinding.selectBillTypeTypeTitle.visibility = View.VISIBLE

                if(Constants.IS_AGENT_USER) {

                    mDataBinding.inputLayoutCode.visibility = View.GONE
                }
                Logger.debugLog("beneficeryManagment","fadilo gone 1")
            }
        }else{
            mDataBinding.spinnerSelectBillType.visibility = View.GONE
            mDataBinding.inputLayoutCode.visibility = View.GONE
            mDataBinding.selectBillTypeTypeTitle.visibility = View.GONE
            if(Constants.IS_AGENT_USER) {

                mDataBinding.inputLayoutCode.visibility = View.GONE
            }
            Logger.debugLog("beneficeryManagment","fadilo gone 2")
        }
    }
}