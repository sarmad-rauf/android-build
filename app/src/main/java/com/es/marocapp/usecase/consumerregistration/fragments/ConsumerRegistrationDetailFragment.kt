package com.es.marocapp.usecase.consumerregistration.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentConsumerRegistrationDetailsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.consumerregistration.ConsumerRegistrationActivity
import com.es.marocapp.usecase.consumerregistration.ConsumerRegistrationClickListner
import com.es.marocapp.usecase.consumerregistration.ConsumerRegistrationViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.layout_login_header.view.*
import java.util.*
import java.util.regex.Pattern

class ConsumerRegistrationDetailFragment : BaseFragment<FragmentConsumerRegistrationDetailsBinding>(),
    ConsumerRegistrationClickListner, TextWatcher {

    lateinit var mActivityViewModel: ConsumerRegistrationViewModel
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    var isCnicMatches = false
    var consumerMsisdnEntered = ""
    var isNumberRegexMatches = false


    override fun setLayout(): Int {
        return R.layout.fragment_consumer_registration_details
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as ConsumerRegistrationActivity).get(ConsumerRegistrationViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@ConsumerRegistrationDetailFragment
        }

        mActivityViewModel.popBackStackTo = -1

        mDataBinding.inputNationalID.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Constants.APP_CN_LENGTH.toInt()
            )
        )

        //todo also here remove lenght-2 check in max line
        mDataBinding.inputConsumerNumber.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Constants.APP_MSISDN_LENGTH.toInt() - 2
            )
        )

        mDataBinding.inputConsumerNumber.addTextChangedListener(this)
        mDataBinding.inputNationalID.addTextChangedListener(this)

        mDataBinding.inputConsumerNumber.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                mDataBinding.inputLayoutConsumerNumber.hint =
                    LanguageData.getStringValue("EnterConsumerNumber")
                mDataBinding.inputPhoneNumberHint.visibility = View.GONE
            } else {
                if (mDataBinding.inputLayoutConsumerNumber.isErrorEnabled) {
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    mDataBinding.inputLayoutConsumerNumber.hint =
                        LanguageData.getStringValue("EnterConsumerNumber")
                }else{
                    if (mDataBinding.inputConsumerNumber.text.isEmpty()) {
                        mDataBinding.inputPhoneNumberHint.visibility = View.VISIBLE
                        mDataBinding.inputLayoutConsumerNumber.hint =
                            LanguageData.getStringValue("MSISDNPlaceholder")

                    } else {
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                        mDataBinding.inputLayoutConsumerNumber.hint =
                            LanguageData.getStringValue("EnterConsumerNumber")
                    }
                }
            }
        }

        subscribeObserver()
        setStrings()
    }

    private fun setStrings() {
        mDataBinding.inputLayoutConsumerNumber.hint = LanguageData.getStringValue("EnterConsumerNumber")
        mDataBinding.inputLayoutFirstName.hint = LanguageData.getStringValue("EnterFirstName")
        mDataBinding.inputLayoutLastName.hint = LanguageData.getStringValue("EnterLastName")
        mDataBinding.inputLayoutDateOfBirth.hint = LanguageData.getStringValue("EnterDateOfBirth")
        mDataBinding.inputLayoutNationalID.hint = LanguageData.getStringValue("EnterNationalIdentityNumber")
        mDataBinding.inputLayoutGender.hint = LanguageData.getStringValue("SelectGender")
        mDataBinding.inputLayoutEmail.hint = LanguageData.getStringValue("EnterEmail")
        mDataBinding.inputLayoutAddress.hint = LanguageData.getStringValue("EnterAddress")
        mDataBinding.btnNextDetailFragment.text = LanguageData.getStringValue("BtnTitle_Next")

        mDataBinding.inputLayoutConsumerNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
        mDataBinding.inputPhoneNumberHint.text =
            LanguageData.getStringValue("EnterConsumerNumber")

    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@ConsumerRegistrationDetailFragment, androidx.lifecycle.Observer {
            DialogUtils.showErrorDialoge(activity as ConsumerRegistrationActivity,it)
        })

        mActivityViewModel.getInitialAuthDetailsResponseListner.observe(this@ConsumerRegistrationDetailFragment,
            androidx.lifecycle.Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){

                    mActivityViewModel.requestForGetOTPForRegistrationApi(activity,mDataBinding.inputFirstName.text.toString().trim(),mDataBinding.inputLastName.text.toString().trim()
                        ,mDataBinding.inputNationalID.text.toString().trim())
                }else{
                    DialogUtils.showErrorDialoge(activity as ConsumerRegistrationActivity,it.description)
                }
            }
        )

        mActivityViewModel.getOtpForRegistrationResponseListner.observe(this@ConsumerRegistrationDetailFragment,Observer{
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                (activity as ConsumerRegistrationActivity).navController.navigate(R.id.action_consumerRegistrationDetailFragment_to_consumerRegistrationVerifyOtpFragment)
            }else{
                DialogUtils.showErrorDialoge(activity as ConsumerRegistrationActivity,it.description)
            }
        })
    }

    override fun onSubmitClickListner(view: View) {
        if(isValidForAll()){
            if(isNumberRegexMatches){
                mDataBinding.inputLayoutConsumerNumber.error = ""
                mDataBinding.inputLayoutConsumerNumber.isErrorEnabled = false

                mActivityViewModel.DOB = mDataBinding.inputDateOfBirth.text.toString().trim()
                mActivityViewModel.identificationNumber = mDataBinding.inputNationalID.text.toString().trim()
                mActivityViewModel.firstName = mDataBinding.inputFirstName.text.toString().trim()
                mActivityViewModel.gender = mDataBinding.inputGender.text.toString().trim()
                mActivityViewModel.postalAddress = mDataBinding.inputAddress.text.toString().trim()
                mActivityViewModel.lastName = mDataBinding.inputLastName.text.toString().trim()
                mActivityViewModel.email = mDataBinding.inputEmail.text.toString().trim()

//                mActivityViewModel.requestForeGetInitialAuthDetailsApi(activity,consumerMsisdnEntered)
                mActivityViewModel.requestForGetOTPForRegistrationApi(activity,mDataBinding.inputFirstName.text.toString().trim(),mDataBinding.inputLastName.text.toString().trim()
                    ,mDataBinding.inputNationalID.text.toString().trim())
            }else{
                mDataBinding.inputLayoutConsumerNumber.error = LanguageData.getStringValue("PleaseEnterValidMobileNumber")
                mDataBinding.inputLayoutConsumerNumber.isErrorEnabled = true
            }
        }
    }

    private fun isValidForAll(): Boolean {

        var isValidForAll = true

        //todo NUmber Lenght is Pending
        if (mDataBinding.inputConsumerNumber.text.isNullOrEmpty() || mDataBinding.inputConsumerNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
            isValidForAll = false
            mDataBinding.inputLayoutConsumerNumber.error = LanguageData.getStringValue("PleaseEnterValidConsumerNumber")
            mDataBinding.inputLayoutConsumerNumber.isErrorEnabled = true
            mDataBinding.inputLayoutConsumerNumber.hint =
                LanguageData.getStringValue("EnterConsumerNumber")
            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
        } else {
            mDataBinding.inputLayoutConsumerNumber.error = ""
            mDataBinding.inputLayoutConsumerNumber.isErrorEnabled = false

            var userMsisdn = mDataBinding.inputConsumerNumber.text.toString()
            if (userMsisdn.startsWith("0", false)) {
                mDataBinding.inputLayoutConsumerNumber.error = ""
                mDataBinding.inputLayoutConsumerNumber.isErrorEnabled = false
                var userMSISDNwithPrefix = userMsisdn.removePrefix("0")
                userMSISDNwithPrefix = Constants.APP_MSISDN_PREFIX + userMSISDNwithPrefix
                userMSISDNwithPrefix = userMSISDNwithPrefix.removePrefix("+")

                consumerMsisdnEntered = userMSISDNwithPrefix
            } else {
                isValidForAll = false
                mDataBinding.inputLayoutConsumerNumber.error = LanguageData.getStringValue("PleaseEnterValidConsumerNumber")
                mDataBinding.inputLayoutConsumerNumber.isErrorEnabled = true
                mDataBinding.inputLayoutConsumerNumber.hint =
                    LanguageData.getStringValue("EnterConsumerNumber")
                mDataBinding.inputPhoneNumberHint.visibility = View.GONE
            }
        }

        if(mDataBinding.inputFirstName.text.isNullOrEmpty()){
            isValidForAll = false
            mDataBinding.inputLayoutFirstName.error = LanguageData.getStringValue("PleaseEnterFirstName")
            mDataBinding.inputLayoutFirstName.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutFirstName.error = ""
            mDataBinding.inputLayoutFirstName.isErrorEnabled = false
        }

        if(mDataBinding.inputLastName.text.isNullOrEmpty()){
            isValidForAll = false
            mDataBinding.inputLayoutLastName.error = LanguageData.getStringValue("PleaseEnterLastName")
            mDataBinding.inputLayoutLastName.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutLastName.error = ""
            mDataBinding.inputLayoutLastName.isErrorEnabled = false
        }

        if(mDataBinding.inputDateOfBirth.text.isNullOrEmpty()){
            isValidForAll = false
            mDataBinding.inputLayoutDateOfBirth.error = LanguageData.getStringValue("PleaseSelectDate")
            mDataBinding.inputLayoutDateOfBirth.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutDateOfBirth.error = ""
            mDataBinding.inputLayoutDateOfBirth.isErrorEnabled = false
        }

        if(mDataBinding.inputNationalID.text.isNullOrEmpty()){
            isValidForAll = false
            mDataBinding.inputLayoutNationalID.error = LanguageData.getStringValue("PleaseEnterIdentityNumber")
            mDataBinding.inputLayoutNationalID.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutNationalID.error = ""
            mDataBinding.inputLayoutNationalID.isErrorEnabled = false

            if(isCnicMatches){
                mDataBinding.inputLayoutNationalID.error = ""
                mDataBinding.inputLayoutNationalID.isErrorEnabled = false
            }else{
                isValidForAll = false
                mDataBinding.inputLayoutNationalID.error = LanguageData.getStringValue("PleaseEnterValidIdentityNumber")
                mDataBinding.inputLayoutNationalID.isErrorEnabled = true
            }
        }

        if(mDataBinding.inputGender.text.isNullOrEmpty()){
            isValidForAll = false
            mDataBinding.inputLayoutGender.error = LanguageData.getStringValue("PleaseSelectGender")
            mDataBinding.inputLayoutGender.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutGender.error = ""
            mDataBinding.inputLayoutGender.isErrorEnabled = false
        }

        if(mDataBinding.inputEmail.text.isNullOrEmpty() || !mDataBinding.inputEmail.text.trim().matches(emailPattern)){
            isValidForAll = false
            if(mDataBinding.inputEmail.text.isNullOrEmpty()) {
                mDataBinding.inputLayoutEmail.error =
                    LanguageData.getStringValue("PleaseEnterEmailAddress")
            }
            else{
                mDataBinding.inputLayoutEmail.error =
                    LanguageData.getStringValue("PleaseEnterValidEmail")
            }
            mDataBinding.inputLayoutEmail.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutEmail.error = ""
            mDataBinding.inputLayoutEmail.isErrorEnabled = false
        }

        if(mDataBinding.inputAddress.text.isNullOrEmpty()){
            isValidForAll = false
            mDataBinding.inputLayoutAddress.error = LanguageData.getStringValue("PleaseEnterAddress")
            mDataBinding.inputLayoutAddress.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutAddress.error = ""
            mDataBinding.inputLayoutAddress.isErrorEnabled = false
        }


        return isValidForAll
    }

    private fun showDatePickerDialog() {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            activity as ConsumerRegistrationActivity,
            DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                var monthVal = (month + 1).toString()
                var selectedDate = "$year-$monthVal-$day"
                val c = Calendar.getInstance()
                c.set(Calendar.YEAR,year)
                c.set(Calendar.MONTH,month)
                c.set(Calendar.DAY_OF_MONTH,day)

                selectedDate= DateFormat.format("yyyy-MM-dd", c.time).toString()
                mDataBinding.inputDateOfBirth.setText(selectedDate)
            }, year, month, dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun showGenderDialog(){
        val singleChoiceItems: ArrayList<String> = arrayListOf()
        singleChoiceItems.apply {
            add(LanguageData.getStringValue("Male").toString())
            add(LanguageData.getStringValue("Female").toString())
            add(LanguageData.getStringValue("Other").toString())
        }

        var list = singleChoiceItems.toTypedArray()
        val itemSelected = 0
        AlertDialog.Builder(activity)
            .setTitle(LanguageData.getStringValue("SelectGender"))
            .setSingleChoiceItems(
                list,
                itemSelected,
                DialogInterface.OnClickListener { dialogInterface, selectedIndex ->
                    when(selectedIndex){
                        0-> mDataBinding.inputGender.setText(LanguageData.getStringValue("Male"))
                        1-> mDataBinding.inputGender.setText(LanguageData.getStringValue("Female"))
                        2-> mDataBinding.inputGender.setText(LanguageData.getStringValue("Other"))
                    }
                })
            .setPositiveButton(LanguageData.getStringValue("BtnTitle_OK"), null)
            .show()
    }

    override fun onCalenderCalenderClick(view: View) {
        showDatePickerDialog()
    }

    override fun onGenderSelectionClick(view: View) {
        mDataBinding.inputGender.setText(LanguageData.getStringValue("Male"))
        showGenderDialog()
    }

    override fun afterTextChanged(editable: Editable?) {
        if(editable.hashCode() == mDataBinding.inputNationalID.text.hashCode()){
            var cnic = mDataBinding.inputNationalID.text.toString().trim()
            var cnicLength = cnic.length
            isCnicMatches = !(cnicLength > 0 && !Pattern.matches(Constants.APP_CN_REGEX, cnic))
        }else if(editable.hashCode() == mDataBinding.inputConsumerNumber.text.hashCode()){
            var msisdn = mDataBinding.inputConsumerNumber.text.toString().trim()
            var msisdnLenght = msisdn.length
            isNumberRegexMatches =
                !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_MSISDN_REGEX, msisdn))
        }

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

}