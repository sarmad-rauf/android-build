package com.es.marocapp.usecase.billpayment.fragments

import android.R.attr.password
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentBillPaymentMsisdnBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentClickListner
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.layout_activity_header.view.*
import java.util.regex.Pattern


class FragmentBillPaymentMsisdn : BaseFragment<FragmentBillPaymentMsisdnBinding>(),
    BillPaymentClickListner, AdapterView.OnItemSelectedListener, TextWatcher {

    private lateinit var mActivityViewModel: BillPaymentViewModel

    private var list_of_favorites = arrayListOf<String>()

    var msisdnEntered = ""
    var code = ""
    var isNumberRegexMatches = false
    var isCodeRegexMatches = false
    var applyValidation = false
    var cilLabel = ""

    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_msisdn
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as BillPaymentActivity).get(BillPaymentViewModel::class.java)
        mDataBinding.apply {
            listner = this@FragmentBillPaymentMsisdn
            viewmodel = mActivityViewModel
        }

        (activity as BillPaymentActivity).setHeaderTitle(
            LanguageData.getStringValue("BillPayment").toString()
        )

        mActivityViewModel.isUserSelectedFromFavorites.set(false)

        list_of_favorites.clear()
        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            mDataBinding.inputPhoneNumber.setInputType(InputType.TYPE_CLASS_TEXT);
            var selectedFatorati =
                "Util_${mActivityViewModel.fatoratiTypeSelected.get()!!.nomCreancier}"
            for (contacts in Constants.mContactListArray) {
                var contactName = contacts.contactName
                if (contactName.contains(selectedFatorati)) {
                    contactName = contactName.substringAfter("@")
                    contactName = contactName.substringBefore(",")
                    list_of_favorites.add(contactName)
                }
            }
        }

        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            mDataBinding.inputPhoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER)
            var selectedBillType = ""
            if(mActivityViewModel.isInternetSelected.get()!!){
                selectedBillType = "Telec_Internet@"

            }else if(mActivityViewModel.isPostPaidMobileSelected.get()!!){
                selectedBillType = "Telec_PostpaidMobile@"
            }else if(mActivityViewModel.isPostPaidFixSelected.get()!!){
                selectedBillType = "Telec_PostpaidFix@"
            }
            for (contacts in Constants.mContactListArray) {
                var contactName = contacts.contactName
                var contactNameWithoutPrefix = contactName.substringAfter("@")
                var contactNameWithoutPostfix = contactNameWithoutPrefix.substringBefore(",")

                var contactNumber = contacts.fri
                if (contactName.contains(selectedBillType)) {
                    var contactNumber = contacts.fri
                    contactNumber = contactNumber.substringBefore("@")
                    contactNumber = contactNumber.substringBefore("/")
                    if(contactName.contains(Constants.APP_MSISDN_PREFIX)){
                        contactNumber = contactNumber.removePrefix(Constants.APP_MSISDN_PREFIX)
                        contactNumber = "0$contactNumber"
                    }
                    //todo also here remove lenght-2 check in max line
//                    if (contactNumber.length.equals(Constants.APP_MSISDN_LENGTH.toInt() - 2)) {
                        var name_number_favorite = "$contactNameWithoutPostfix-$contactNumber"
                        list_of_favorites.add(name_number_favorite)
//                    }
                }else{
                    contactNumber = contactNumber.substringBefore("@")
                    contactNumber = contactNumber.substringBefore("/")
                    if(contactNumber.length.equals(Constants.APP_MSISDN_LENGTH.toInt() - 2)){
                        var name_number_favorite = "$contactNameWithoutPostfix-$contactNumber"
                        list_of_favorites.add(name_number_favorite)
                    }
                }
            }
        }

        list_of_favorites.add(0, LanguageData.getStringValue("SelectFavorite").toString())

        val adapterFavoriteType = ArrayAdapter<CharSequence>(
            activity as BillPaymentActivity, R.layout.layout_favorites_spinner_text,
            list_of_favorites as List<CharSequence>
        )
        mDataBinding.spinnerSelectFavorites.apply {
            adapter = adapterFavoriteType
        }
        mDataBinding.spinnerSelectFavorites.onItemSelectedListener = this@FragmentBillPaymentMsisdn
        (activity as BillPaymentActivity).setHeaderVisibility(true)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(true)

        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            (activity as BillPaymentActivity).mDataBinding.headerBillPayment.rootView.tv_company_title.text =
                mActivityViewModel.billTypeSelected.get()!!
            (activity as BillPaymentActivity).mDataBinding.headerBillPayment.rootView.img_company_icons.setImageResource(
                mActivityViewModel.billTypeSelectedIcon
            )

            if (mActivityViewModel.isInternetSelected.get()!!) {
                mDataBinding.inputLayoutCode.visibility = View.GONE
            } else {
                mDataBinding.inputLayoutCode.visibility = View.VISIBLE
            }

            (activity as BillPaymentActivity).setLetterIconVisible(false, "")

            //todo also here remove lenght-2 check in max line
            mDataBinding.inputPhoneNumber.filters = arrayOf<InputFilter>(
                InputFilter.LengthFilter(
                    Constants.APP_MSISDN_LENGTH.toInt() - 2
                )
            )

            mDataBinding.inputPhoneNumber.inputType = InputType.TYPE_CLASS_PHONE

        }
        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            (activity as BillPaymentActivity).mDataBinding.headerBillPayment.rootView.tv_company_title.text =
                mActivityViewModel.fatoratiTypeSelected.get()!!.nomCreancier

            mDataBinding.inputLayoutCode.visibility = View.GONE

            (activity as BillPaymentActivity).setLetterIconVisible(
                true,
                mActivityViewModel.fatoratiTypeSelected.get()!!.nomCreancier[0].toString()
            )

            mDataBinding.inputPhoneNumber.filters =
                arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.APP_CIL_LENGTH.toInt()))

            mDataBinding.inputPhoneNumber.inputType = InputType.TYPE_CLASS_TEXT

        }

        mActivityViewModel.popBackStackTo = R.id.fragmentPostPaidBillType
        mDataBinding.inputPhoneNumber.addTextChangedListener(this)
        mDataBinding.inputCode.addTextChangedListener(this)

        mDataBinding.inputPhoneNumber.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setInputLayoutHint()
            } else {
                if (mDataBinding.inputLayoutPhoneNumber.isErrorEnabled) {
                    setInputLayoutHint()
                } else {
                    if (mDataBinding.inputPhoneNumber.text.isEmpty()) {
                        mDataBinding.inputPhoneNumberHint.visibility = View.VISIBLE
                        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                            mDataBinding.inputLayoutPhoneNumber.hint =
                                LanguageData.getStringValue("EnterCilNumber")
                            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                        }
                        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
                            if (mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!) {
                                mDataBinding.inputLayoutPhoneNumber.hint =
                                    LanguageData.getStringValue("MSISDNPlaceholder")
                                mDataBinding.inputPhoneNumberHint.text =
                                    LanguageData.getStringValue("PhoneNumber")
                            } else if (mActivityViewModel.isInternetSelected.get()!!) {
                                mDataBinding.inputLayoutPhoneNumber.hint =
                                    LanguageData.getStringValue("MSISDNPlaceholder")
                                mDataBinding.inputPhoneNumberHint.text =
                                    LanguageData.getStringValue("EnterPaymentIdentifier")
                            }
                        }
                    } else {
                        setInputLayoutHint()
                    }
                }
            }
        }
        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            mActivityViewModel.requestForFatoratiStepTwoApi(
                activity,
                Constants.CURRENT_USER_MSISDN
            )
        }

        setStrings()
        subscribeObserver()
    }

    fun setInputLayoutHint() {
        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            mDataBinding.inputLayoutPhoneNumber.hint = cilLabel
        }
        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            if (mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!) {
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("PhoneNumber")
            } else if (mActivityViewModel.isInternetSelected.get()!!) {
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("EnterPaymentIdentifier")
            }
        }
    }

    private fun setStrings() {
        mDataBinding.inputLayoutCode.hint = LanguageData.getStringValue("EnterCode")
        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterCilNumber")
            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
        }
        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            if (mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!) {
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("MSISDNPlaceholder")
                mDataBinding.inputPhoneNumberHint.text =
                    LanguageData.getStringValue("PhoneNumber")
            } else if (mActivityViewModel.isInternetSelected.get()!!) {
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("MSISDNPlaceholder")
                mDataBinding.inputPhoneNumberHint.text =
                    LanguageData.getStringValue("EnterPaymentIdentifier")
            }
        }
        mDataBinding.selectFavoriteTypeTitle.hint = LanguageData.getStringValue("SelectFavorite")
        mDataBinding.btnNext.text = LanguageData.getStringValue("Submit")
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@FragmentBillPaymentMsisdn, Observer {
            DialogUtils.showErrorDialoge(activity, it)
        }
        )

        mActivityViewModel.getPostPaidResourceInfoResponseListner.observe(this@FragmentBillPaymentMsisdn,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.response.custId != null) {
                        mActivityViewModel.custId = it.response.custId
                    }
                    if (it.response.custname != null) {
                        mActivityViewModel.custname = it.response.custname
                    }
                    mActivityViewModel.totalamount = it.response.totalamount
                    (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMsisdn_to_fragmentPostPaidBillDetails)
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )

        mActivityViewModel.getFatoratiStepTwoResponseListner.observe(this@FragmentBillPaymentMsisdn,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    cilLabel = it.param.libelle
                    if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                        mDataBinding.inputLayoutPhoneNumber.hint = cilLabel
                        mDataBinding.inputPhoneNumberHint.text =
                            cilLabel
                    }
                    if (it.param.libelle.equals("CIL", false)) {
                        applyValidation = true
                    } else {
                        applyValidation = false
                    }
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )

        mActivityViewModel.getFatoratiStepFourResponseListner.observe(this@FragmentBillPaymentMsisdn,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.params == null || it.params.isNullOrEmpty() || it.params.size < 1) {
                        //  DialogUtils.showErrorDialoge(activity, it.message)
                        val btnTxt = LanguageData.getStringValue("BtnTitle_OK")
                        val titleTxt = LanguageData.getStringValue("Error")
                        DialogUtils.showCustomDialogue(
                            activity,
                            btnTxt,
                            it.message,
                            titleTxt,
                            object : DialogUtils.OnCustomDialogListner {
                                override fun onCustomDialogOkClickListner() {

                                }
                            })
                    } else {
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMsisdn_to_fragmentPostPaidBillDetails)
                    }
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )
    }

    override fun onSubmitClickListner(view: View) {
        if (isValidForAll()) {
            if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
                mActivityViewModel.requestForPostPaidFinancialResourceInfoApi(
                    activity,
                    code,
                    msisdnEntered
                )
            }

            if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                mActivityViewModel.transferdAmountTo = msisdnEntered
                mActivityViewModel.requestForFatoratiStepFourApi(activity)
            }
        }
    }

    override fun onBackClickListner(view: View) {

    }

    private fun isValidForAll(): Boolean {
        var isValidForAll = true
        //todo NUmber Lenght is Pending
        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            //todo NUmber Lenght is Pending
            if (mActivityViewModel.isPostPaidFixSelected.get()!! || mActivityViewModel.isPostPaidMobileSelected.get()!!) {
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
                    /*if (userMsisdn.startsWith("0", false)) {
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
                                LanguageData.getStringValue("EnterValidPhoneNumber")
                            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                            mDataBinding.inputLayoutPhoneNumber.hint =
                                LanguageData.getStringValue("PhoneNumber")
                            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                        }
                    } else {
                        mDataBinding.inputLayoutPhoneNumber.error =
                            LanguageData.getStringValue("EnterValidPhoneNumber")
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                        isValidForAll = false
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("PhoneNumber")
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    }*/

                    if (isNumberRegexMatches) {
                        mDataBinding.inputLayoutPhoneNumber.error = ""
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                        checkNumberExistInFavorites(userMsisdn)
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

            }

            if (mActivityViewModel.isInternetSelected.get()!!) {
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
                        checkNumberExistInFavorites(userMsisdn)
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
        }
        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() /*|| mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_CIL_LENGTH.toInt()*/) {
                isValidForAll = false
                mDataBinding.inputLayoutPhoneNumber.error =
                    LanguageData.getStringValue("invalid") + " " + cilLabel
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("invalid") + " " + cilLabel
                mDataBinding.inputPhoneNumberHint.visibility = View.GONE
            } else {
                mDataBinding.inputLayoutPhoneNumber.error = ""
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                if (applyValidation) {
                    if (isNumberRegexMatches) {
                        mDataBinding.inputLayoutPhoneNumber.error = ""
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                        msisdnEntered = mDataBinding.inputPhoneNumber.text.toString().trim()

                        checkNumberExistInFavoritesForFatorati(msisdnEntered)
                    } else {
                        isValidForAll = false
                        mDataBinding.inputLayoutPhoneNumber.error =
                            LanguageData.getStringValue("invalid") + " " + cilLabel
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("invalid") + " " + cilLabel
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    }
                } else {
                    mDataBinding.inputLayoutPhoneNumber.error = ""
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                    msisdnEntered = mDataBinding.inputPhoneNumber.text.toString().trim()

                    checkNumberExistInFavoritesForFatorati(msisdnEntered)
                }
            }
        }

        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            if (mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!) {
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

        return isValidForAll
    }


    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var selectedFavorites = mDataBinding.spinnerSelectFavorites.selectedItem.toString()
        if (!selectedFavorites.equals(LanguageData.getStringValue("SelectFavorite"))) {
            setInputLayoutHint()
            if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
                if (mActivityViewModel.isInternetSelected.get()!!) {
                    selectedFavorites = selectedFavorites.substringAfter("-")
                    mDataBinding.inputPhoneNumber.setText(selectedFavorites)
                    mActivityViewModel.isUserSelectedFromFavorites.set(true)
                }
                if (mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!) {
                    selectedFavorites = selectedFavorites.substringAfter("-")
                    mDataBinding.inputPhoneNumber.setText(selectedFavorites)
                    mActivityViewModel.isUserSelectedFromFavorites.set(true)

                    var selectedBillType = ""
                    if(mActivityViewModel.isPostPaidMobileSelected.get()!!){
                        selectedBillType = "Telec_PostpaidMobile@"
                    }else if(mActivityViewModel.isPostPaidFixSelected.get()!!){
                        selectedBillType = "Telec_PostpaidFix@"
                    }
                    for (contacts in Constants.mContactListArray) {
                        var contactName = contacts.contactName
                        var contactNameWithoutPrefix = contactName.substringAfter("@")
                        var contactNumberCode =
                            contactNameWithoutPrefix.substringAfter(",")
                        if (contactName.contains(selectedBillType)) {
                            var contactNumber = contacts.fri
                            if(contactNumber.equals(selectedFavorites)){
                                if(!contactNumberCode.isNullOrEmpty()){
                                    mDataBinding.inputCode.setText(contactNumberCode)
                                }
                            }
                        }
                    }
                }
            }

            if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                for (contacts in Constants.mContactListArray) {
                    var contactName = contacts.contactName
                    contactName = contactName.substringAfter("@")
                    contactName = contactName.substringBefore(",")
                    if (selectedFavorites.equals(contactName)) {
                        var selectedFri = contacts.fri.substringBefore("@")
                        mDataBinding.inputPhoneNumber.setText(selectedFri)
                        mActivityViewModel.isUserSelectedFromFavorites.set(true)
                        break
                    }

                }
            }

        } else {
            mDataBinding.inputPhoneNumber.setText("")
            mActivityViewModel.isUserSelectedFromFavorites.set(false)
            if (mDataBinding.inputLayoutPhoneNumber.isErrorEnabled) {

            } else {
                mDataBinding.inputPhoneNumber.clearFocus()
                mDataBinding.inputPhoneNumberHint.visibility = View.VISIBLE
                if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterCilNumber")
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                }
                if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
                    if (mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!) {
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("MSISDNPlaceholder")
                        mDataBinding.inputPhoneNumberHint.text =
                            LanguageData.getStringValue("PhoneNumber")
                    } else if (mActivityViewModel.isInternetSelected.get()!!) {
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("MSISDNPlaceholder")
                        mDataBinding.inputPhoneNumberHint.text =
                            LanguageData.getStringValue("EnterPaymentIdentifier")
                    }
                }
            }
        }
    }

    private fun checkNumberExistInFavoritesForFatorati(msisdnEntered: String) {
        for (contacts in Constants.mContactListArray) {
            var contactNumber = contacts.fri
            contactNumber = contactNumber.substringBefore("@")
            if (msisdnEntered.equals(contactNumber)) {
                mActivityViewModel.isUserSelectedFromFavorites.set(true)
                break
            } else {
                mActivityViewModel.isUserSelectedFromFavorites.set(false)
                Log.i("FavoritesCheck", "false")
            }

        }
    }


    private fun checkNumberExistInFavorites(userMsisdn: String) {
        var updateMsisdn = ""
        if (!userMsisdn.startsWith("0", false)) {
            var appMsisdnPrefix = Constants.APP_MSISDN_PREFIX.removePrefix("+")
            if (userMsisdn.startsWith(appMsisdnPrefix)) {
                updateMsisdn = userMsisdn.replace(appMsisdnPrefix, "0")
            } else {
                updateMsisdn = userMsisdn
            }
        } else {
            updateMsisdn = userMsisdn
        }
        for (i in 0 until list_of_favorites.size) {
            var favoriteNumber = list_of_favorites[i].substringAfter("-")
            if (favoriteNumber.equals(updateMsisdn)) {
                mActivityViewModel.isUserSelectedFromFavorites.set(true)
                Log.i("FavoritesCheck", "true")
                break
            } else {
                mActivityViewModel.isUserSelectedFromFavorites.set(false)
                Log.i("FavoritesCheck", "false")
            }
        }
    }

    override fun afterTextChanged(editable: Editable?) {
        if (editable.hashCode() == mDataBinding.inputPhoneNumber.text.hashCode()) {
            var msisdn = mDataBinding.inputPhoneNumber.text.toString().trim()
            var msisdnLenght = msisdn.length

            if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                isNumberRegexMatches =
                    !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_CIL_REGEX, msisdn))
            }
            if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
                if (mActivityViewModel.isPostPaidMobileSelected.get()!!) {
                    isNumberRegexMatches =
                        !(msisdnLenght > 0 && !Pattern.matches(
                            Constants.APP_MSISDN_POSTPAIDBILL_MOBILE_REGEX,
                            msisdn
                        ))
                }
                if (mActivityViewModel.isPostPaidFixSelected.get()!!) {
                    isNumberRegexMatches =
                        !(msisdnLenght > 0 && !Pattern.matches(
                            Constants.APP_MSISDN_POSTPAIDBILL_FIXE_REGEX,
                            msisdn
                        ))
                }
                if (mActivityViewModel.isInternetSelected.get()!!) {
                    isNumberRegexMatches =
                        !(msisdnLenght > 0 && !Pattern.matches(
                            Constants.APP_MSISDN_POSTPAIDBILL_INTERNET_REGEX,
                            msisdn
                        ))
                }
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

}