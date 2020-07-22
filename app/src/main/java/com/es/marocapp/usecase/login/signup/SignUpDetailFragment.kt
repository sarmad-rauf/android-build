package com.es.marocapp.usecase.login.signup


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentSignUpDetailBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.GetInitialAuthDetailsReponse
import com.es.marocapp.model.responses.GetOtpForRegistrationResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import kotlinx.android.synthetic.main.layout_login_header.view.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class SignUpDetailFragment : BaseFragment<FragmentSignUpDetailBinding>(), SignUpClickListner,
    TextWatcher {

    lateinit var mActivityViewModel: LoginActivityViewModel
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    var isCnicMatches = false

    override fun setLayout(): Int {
        return R.layout.fragment_sign_up_detail
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as LoginActivity).get(LoginActivityViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
            listener = this@SignUpDetailFragment
        }

        mDataBinding.root.groupBack.visibility = View.VISIBLE

        mDataBinding.root.txtBack.setOnClickListener {
            (activity as LoginActivity).navController.navigateUp()
        }

        mDataBinding.root.imgBackButton.setOnClickListener {
            (activity as LoginActivity).navController.navigateUp()
        }
        mDataBinding.inputNationalID.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Constants.APP_CN_LENGTH.toInt()
            )
        )
        mDataBinding.inputNationalID.addTextChangedListener(this)

        mActivityViewModel.isSimplePopUp = true
        subscribeObserver()
        setStrings()

    }

    private fun setStrings() {
        mDataBinding.root.txtBack.text= LanguageData.getStringValue("BtnTitle_Back")
        mDataBinding.root.txtHeaderTitle.text= LanguageData.getStringValue("CreateYourAccount")
        mDataBinding.inputLayoutFirstName.hint = LanguageData.getStringValue("EnterFirstName")
        mDataBinding.inputLayoutLastName.hint = LanguageData.getStringValue("EnterLastName")
        mDataBinding.inputLayoutDateOfBirth.hint = LanguageData.getStringValue("EnterDateOfBirth")
        mDataBinding.inputLayoutNationalID.hint = LanguageData.getStringValue("EnterNationalIdentityNumber")
        mDataBinding.inputLayoutGender.hint = LanguageData.getStringValue("SelectGender")
        mDataBinding.inputLayoutEmail.hint = LanguageData.getStringValue("EnterEmail")
        mDataBinding.inputLayoutAddress.hint = LanguageData.getStringValue("EnterAddress")
        mDataBinding.btnNextDetailFragment.text = LanguageData.getStringValue("BtnTitle_Next")

    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@SignUpDetailFragment, Observer {
            DialogUtils.showErrorDialoge(activity as LoginActivity,it)
        })

        val mInitialAuthDetailsResonseObserver = Observer<GetInitialAuthDetailsReponse>{
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){

                mActivityViewModel.requestForGetOTPForRegistrationApi(activity,mDataBinding.inputFirstName.text.toString().trim(),mDataBinding.inputLastName.text.toString().trim()
                    ,mDataBinding.inputNationalID.text.toString().trim())
            }else{
                DialogUtils.showErrorDialoge(activity as LoginActivity,it.description)
            }
        }

        val mOTPForRegistrationResonseObserver = Observer<GetOtpForRegistrationResponse>{
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                (activity as LoginActivity).navController.navigate(R.id.action_signUpDetailFragment_to_verifyNumberFragment)
            }else{
                DialogUtils.showErrorDialoge(activity as LoginActivity,it.description)
            }
        }

        mActivityViewModel.getInitialAuthDetailsResponseListner.observe(this,mInitialAuthDetailsResonseObserver)
        mActivityViewModel.getOtpForRegistrationResponseListner.observe(this,mOTPForRegistrationResonseObserver)
    }

    private fun showDatePickerDialog() {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            activity as LoginActivity,
            OnDateSetListener { datePicker, year, month, day ->
                var monthVal = (month+1).toString()
                var selectedDate = "$year-$monthVal-$day"
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

    override fun onNextButtonClick(view: View) {

        if(isValidForAll()){
            mActivityViewModel.DOB = mDataBinding.inputDateOfBirth.text.toString().trim()
            mActivityViewModel.identificationNumber = mDataBinding.inputNationalID.text.toString().trim()
            mActivityViewModel.firstName = mDataBinding.inputFirstName.text.toString().trim()
            mActivityViewModel.gender = mDataBinding.inputGender.text.toString().trim()
            mActivityViewModel.postalAddress = mDataBinding.inputAddress.text.toString().trim()
            mActivityViewModel.lastName = mDataBinding.inputLastName.text.toString().trim()
            mActivityViewModel.email = mDataBinding.inputEmail.text.toString().trim()

            mActivityViewModel.requestForeGetInitialAuthDetailsApi(activity)
        }
        //For Without API Calling Uncomment Below Line
//        (activity as LoginActivity).navController.navigate(R.id.action_signUpDetailFragment_to_verifyNumberFragment)
    }

    override fun onBackButtonClick(view: View) {

    }

    override fun onCalenderCalenderClick(view: View) {
        showDatePickerDialog()
    }

    override fun onGenderSelectionClick(view: View) {
        mDataBinding.inputGender.setText("Male")
        showGenderDialog()
    }

    override fun afterTextChanged(p0: Editable?) {
        var cnic = mDataBinding.inputNationalID.text.toString().trim()
        var cnicLength = cnic.length
        isCnicMatches = !(cnicLength > 0 && !Pattern.matches(Constants.APP_CN_REGEX, cnic))
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    private fun isValidForAll(): Boolean {

        var isValidForAll = true

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
}
