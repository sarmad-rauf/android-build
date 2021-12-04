package com.es.marocapp.usecase.updateprofle

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentUpdateProfleMainBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity

import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger
import java.util.*
import java.util.regex.Pattern


class UpdateProfleMainFragment : BaseFragment<FragmentUpdateProfleMainBinding>(),
    UpdateProfileOnClickListner,
    TextWatcher {

    lateinit var updateProfileViewModel: UpdateProfileViewModel
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    var isCnicMatches = false


    override fun setLayout(): Int {
        return R.layout.fragment_update_profle_main
    }

    override fun init(savedInstanceState: Bundle?) {
        updateProfileViewModel =
            ViewModelProvider(activity as UpdateProfileActivity).get(UpdateProfileViewModel::class.java)
        mDataBinding.apply {
            viewModel = updateProfileViewModel
        }

        mDataBinding.inputNationalID.addTextChangedListener(this)
        mDataBinding.btnNextDetailFragment.setOnClickListener {
            onNextButtonClicked()
        }
        mDataBinding.imgShowCalender.setOnClickListener {
            onCalenderCalenderClicked()
        }


        setStrings()
        setTextChangeListners()
        setViewsAvailability()
        setObservers()
    }

    private fun setTextChangeListners() {
        mDataBinding.inputEmail.addTextChangedListener(
        ) {
            updateProfileViewModel.isEmailChanged = true
            Logger.debugLog("updateProfile", "email Text changed")
        }
        mDataBinding.inputFirstName.addTextChangedListener() {
            updateProfileViewModel.isPersonalDataChanged = true
        }
        mDataBinding.inputLastName.addTextChangedListener() {
            updateProfileViewModel.isPersonalDataChanged = true
        }
        mDataBinding.inputDateOfBirth.addTextChangedListener(
        ) {
            updateProfileViewModel.isPersonalDataChanged = true
        }
        mDataBinding.inputNationalID.addTextChangedListener(
        ) {
            updateProfileViewModel.isCINChanged = true
        }
        mDataBinding.inputAddress.addTextChangedListener(
        ) {
            updateProfileViewModel.isAdressChanged = true
        }
        mDataBinding.inputCity.addTextChangedListener(
        ) {
            updateProfileViewModel.isAdressChanged = true
        }
    }

    private fun setObservers() {
        updateProfileViewModel.errorText.observe(this@UpdateProfleMainFragment, Observer {
            DialogUtils.showErrorDialoge(activity as UpdateProfileActivity, it)
        })

        updateProfileViewModel.UpdateEmailResponseListner.observe(
            this@UpdateProfleMainFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    updateProfileViewModel.isEmailChanged = false
                    Constants.CURRENT_USER_EMAIL = mDataBinding.inputEmail.text.toString()
                    Constants.shouldUpdate = true
                    Logger.debugLog("updatePrfile", "reflacting changess")
                    DialogUtils.successFailureDialogue(
                        activity as UpdateProfileActivity,
                        it.description,
                        0,
                        object : DialogUtils.OnYesClickListner {
                            override fun onDialogYesClickListner() {
                                (activity as UpdateProfileActivity).startNewActivityAndClear(
                                    activity as UpdateProfileActivity,
                                    MainActivity::class.java
                                )
                            }
                        })
                } else {
                    DialogUtils.successFailureDialogue(
                        activity as UpdateProfileActivity,
                        it.description,
                        1
                    )
                }
            })

        updateProfileViewModel.UpdateAdressResponseListner.observe(
            this@UpdateProfleMainFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    updateProfileViewModel.isAdressChanged = false
                    Constants.CURRENT_USER_ADRESS = mDataBinding.inputAddress.text.toString()
                    Constants.CURRENT_USER_CITY = mDataBinding.inputCity.text.toString()
                    if (updateProfileViewModel.isEmailChanged) {
                        updateProfileViewModel.requestForUpdateEmailAPI(
                            requireContext(),
                            mDataBinding.inputEmail.text.toString()
                        )
                    } else {
                        updateProfileViewModel.isLoading.set(false)
                        DialogUtils.successFailureDialogue(
                            activity as UpdateProfileActivity,
                            it.description,
                            0,
                            object : DialogUtils.OnYesClickListner {
                                override fun onDialogYesClickListner() {
                                    (activity as UpdateProfileActivity).startNewActivityAndClear(
                                        activity as UpdateProfileActivity,
                                        MainActivity::class.java
                                    )
                                }
                            })
                    }
                } else {
                    updateProfileViewModel.isLoading.set(false)
                    DialogUtils.successFailureDialogue(
                        activity as UpdateProfileActivity,
                        it.description,
                        1
                    )
                }
            })

        updateProfileViewModel.UpdateCINResponseListner.observe(
            this@UpdateProfleMainFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    updateProfileViewModel.isCINChanged = false
                    Constants.CURRENT_USER_CIN = mDataBinding.inputNationalID.text.toString()
                    if (updateProfileViewModel.isAdressChanged) {
                        updateProfileViewModel.requestForUpdateAdressAPI(
                            requireContext(),
                            mDataBinding.inputAddress.text.toString(),
                            mDataBinding.inputCity.text.toString()
                        )
                    } else if (updateProfileViewModel.isEmailChanged) {
                        updateProfileViewModel.requestForUpdateEmailAPI(
                            requireContext(),
                            mDataBinding.inputEmail.text.toString()
                        )
                    } else {
                        updateProfileViewModel.isLoading.set(false)
                        DialogUtils.successFailureDialogue(
                            activity as UpdateProfileActivity,
                            it.description,
                            0,
                            object : DialogUtils.OnYesClickListner {
                                override fun onDialogYesClickListner() {
                                    (activity as UpdateProfileActivity).startNewActivityAndClear(
                                        activity as UpdateProfileActivity,
                                        MainActivity::class.java
                                    )
                                }
                            })
                    }
                } else {
                    updateProfileViewModel.isLoading.set(false)
                    DialogUtils.successFailureDialogue(
                        activity as UpdateProfileActivity,
                        it.description,
                        1
                    )
                }
            })

        updateProfileViewModel.UpdatePersonalInformationResponseListner.observe(
            this@UpdateProfleMainFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    updateProfileViewModel.isPersonalDataChanged = false
                    Constants.CURRENT_USER_FIRST_NAME = mDataBinding.inputFirstName.text.toString()
                    Constants.CURRENT_USER_LAST_NAME = mDataBinding.inputLastName.text.toString()
                    Constants.CURRENT_USER_DATE_OF_BIRTH =
                        mDataBinding.inputDateOfBirth.text.toString()
                    Constants.shouldUpdate = true
                    if (updateProfileViewModel.isCINChanged) {
                        updateProfileViewModel.requestForUpdateCINAPI(
                            requireContext(),
                            mDataBinding.inputNationalID.text.toString()
                        )
                    } else if (updateProfileViewModel.isAdressChanged) {
                        updateProfileViewModel.requestForUpdateAdressAPI(
                            requireContext(),
                            mDataBinding.inputAddress.text.toString(),
                            mDataBinding.inputCity.text.toString()
                        )
                    } else if (updateProfileViewModel.isEmailChanged) {
                        updateProfileViewModel.requestForUpdateEmailAPI(
                            requireContext(),
                            mDataBinding.inputEmail.text.toString()
                        )
                    } else {
                        updateProfileViewModel.isLoading.set(false)
                        DialogUtils.successFailureDialogue(
                            activity as UpdateProfileActivity,
                            it.description,
                            0,
                            object : DialogUtils.OnYesClickListner {
                                override fun onDialogYesClickListner() {
                                    (activity as UpdateProfileActivity).startNewActivityAndClear(
                                        activity as UpdateProfileActivity,
                                        MainActivity::class.java
                                    )
                                }
                            })
                    }
                } else {
                    updateProfileViewModel.isLoading.set(false)
                    DialogUtils.successFailureDialogue(
                        activity as UpdateProfileActivity,
                        it.description,
                        1
                    )
                }
            })
    }

    private fun setViewsAvailability() {

        if(!updateProfileViewModel.currentProfile.contains("1")){
        if (updateProfileViewModel.currentProfile.contains("2")) {
            mDataBinding.inputFirstName.isEnabled = false
            mDataBinding.inputLastName.isEnabled = false
            mDataBinding.imgShowCalender.isEnabled = false
            mDataBinding.inputDateOfBirth.isEnabled = false
            mDataBinding.inputNationalID.isEnabled = false
            mDataBinding.inputIdType.isEnabled = false
        } else if (updateProfileViewModel.currentProfile.contains("3")) {
            mDataBinding.inputFirstName.isEnabled = false
            mDataBinding.inputLastName.isEnabled = false
            mDataBinding.imgShowCalender.isEnabled = false
            mDataBinding.inputDateOfBirth.isEnabled = false
            mDataBinding.inputNationalID.isEnabled = false
            mDataBinding.inputIdType.isEnabled = false
            mDataBinding.inputAddress.isEnabled = false
            mDataBinding.inputCity.isEnabled = false
        }
        }
    }

    private fun setStrings() {
        updateProfileViewModel.currentProfile =
            Constants.loginWithCertResponse.getAccountHolderInformationResponse.profileName
        updateProfileViewModel.firstName = Constants.CURRENT_USER_FIRST_NAME
        updateProfileViewModel.surName = Constants.CURRENT_USER_LAST_NAME
        updateProfileViewModel.email = getUserEmailAddress()
        updateProfileViewModel.dateOfBirth = Constants.CURRENT_USER_DATE_OF_BIRTH
        updateProfileViewModel.identityNumber = Constants.CURRENT_USER_CIN
        updateProfileViewModel.adress = Constants.CURRENT_USER_ADRESS
        updateProfileViewModel.city = Constants.CURRENT_USER_CITY


        //input data
        mDataBinding.btnNextDetailFragment.text = LanguageData.getStringValue("BtnTitle_Save")
        mDataBinding.inputFirstName.setText(updateProfileViewModel.firstName)
        mDataBinding.inputLastName.setText(updateProfileViewModel.surName)
        mDataBinding.inputDateOfBirth.setText(updateProfileViewModel.dateOfBirth)
        mDataBinding.inputNationalID.setText(updateProfileViewModel.identityNumber)
        mDataBinding.inputEmail.setText(updateProfileViewModel.email)
        mDataBinding.inputAddress.setText(updateProfileViewModel.adress)
        mDataBinding.inputCity.setText(updateProfileViewModel.city)


        mDataBinding.inputLayoutFirstName.hint = LanguageData.getStringValue("EnterFirstName")
        mDataBinding.inputLayoutLastName.hint = LanguageData.getStringValue("EnterLastName")
        mDataBinding.inputLayoutDateOfBirth.hint = LanguageData.getStringValue("EnterDateOfBirth")
        mDataBinding.inputLayoutNationalID.hint =
            LanguageData.getStringValue("EnterNationalIdentityNumber")
        mDataBinding.inputLayoutEmail.hint = LanguageData.getStringValue("EnterEmail")
        mDataBinding.inputLayoutAddress.hint = LanguageData.getStringValue("EnterAddress")
        mDataBinding.inputLayoutCity.hint = (LanguageData.getStringValue("EnterCity"))
    }

    override fun onNextButtonClick(view: View) {
        Logger.debugLog("updateProfile", "nextCliskListner")
        if (isValidForAll()) {
            Logger.debugLog("updateProfile", "validFor All")
            if (updateProfileViewModel.currentProfile.contains("3")) {
                Logger.debugLog("updateProfile", "level3")
                if (updateProfileViewModel.isEmailChanged) {
                    Logger.debugLog("updateProfile", "emailchanged")
                    updateProfileViewModel.requestForUpdateEmailAPI(
                        requireContext(),
                        mDataBinding.inputEmail.text.toString()
                    )
                }
            } else if (updateProfileViewModel.currentProfile.contains("2")) {
                if (updateProfileViewModel.isAdressChanged) {
                    updateProfileViewModel.requestForUpdateAdressAPI(
                        requireContext(),
                        mDataBinding.inputAddress.text.toString(),
                        mDataBinding.inputCity.text.toString()
                    )
                } else if (updateProfileViewModel.isEmailChanged) {
                    updateProfileViewModel.requestForUpdateEmailAPI(
                        requireContext(),
                        mDataBinding.inputEmail.text.toString()
                    )
                }
            } else {
                if (updateProfileViewModel.isPersonalDataChanged) {
                    updateProfileViewModel.requestForUpdatePersonalInformationAPI(
                        requireContext(),
                        mDataBinding.inputFirstName.text.toString(),
                        mDataBinding.inputLastName.text.toString(),
                        mDataBinding.inputDateOfBirth.text.toString()
                    )

                } else if (updateProfileViewModel.isCINChanged) {
                    updateProfileViewModel.requestForUpdateCINAPI(
                        requireContext(),
                        mDataBinding.inputNationalID.text.toString()
                    )
                } else if (updateProfileViewModel.isAdressChanged) {
                    updateProfileViewModel.requestForUpdateAdressAPI(
                        requireContext(),
                        mDataBinding.inputAddress.text.toString(),
                        mDataBinding.inputCity.text.toString()
                    )
                } else if (updateProfileViewModel.isEmailChanged) {
                    updateProfileViewModel.requestForUpdateEmailAPI(
                        requireContext(),
                        mDataBinding.inputEmail.text.toString()
                    )
                }
            }
        }
    }

    fun onNextButtonClicked() {
        if (isValidForAll()) {
            if (updateProfileViewModel.currentProfile.contains("3")) {
                if (updateProfileViewModel.isEmailChanged) {
                    updateProfileViewModel.requestForUpdateEmailAPI(
                        requireContext(),
                        mDataBinding.inputEmail.text.toString()
                    )
                } else {
                    DialogUtils.successFailureDialogue(
                        activity as UpdateProfileActivity,
                        LanguageData.getStringValue("UpdateProfile"),
                        1
                    )
                }
            } else if (updateProfileViewModel.currentProfile.contains("2")) {
                if (updateProfileViewModel.isAdressChanged) {
                    updateProfileViewModel.requestForUpdateAdressAPI(
                        requireContext(),
                        mDataBinding.inputAddress.text.toString(),
                        mDataBinding.inputCity.text.toString()
                    )
                } else if (updateProfileViewModel.isEmailChanged) {
                    updateProfileViewModel.requestForUpdateEmailAPI(
                        requireContext(),
                        mDataBinding.inputEmail.text.toString()
                    )
                } else {
                    DialogUtils.successFailureDialogue(
                        activity as UpdateProfileActivity,
                        LanguageData.getStringValue("UpdateProfile"),
                        1
                    )
                }
            } else {
                if (updateProfileViewModel.isPersonalDataChanged) {
                    updateProfileViewModel.requestForUpdatePersonalInformationAPI(
                        requireContext(),
                        mDataBinding.inputFirstName.text.toString(),
                        mDataBinding.inputLastName.text.toString(),
                        mDataBinding.inputDateOfBirth.text.toString()
                    )

                } else if (updateProfileViewModel.isCINChanged) {
                    updateProfileViewModel.requestForUpdateCINAPI(
                        requireContext(),
                        mDataBinding.inputNationalID.text.toString()
                    )
                } else if (updateProfileViewModel.isAdressChanged) {
                    updateProfileViewModel.requestForUpdateAdressAPI(
                        requireContext(),
                        mDataBinding.inputAddress.text.toString(),
                        mDataBinding.inputCity.text.toString()
                    )
                } else if (updateProfileViewModel.isEmailChanged) {
                    updateProfileViewModel.requestForUpdateEmailAPI(
                        requireContext(),
                        mDataBinding.inputEmail.text.toString()
                    )
                } else {
                    DialogUtils.successFailureDialogue(
                        activity as UpdateProfileActivity,
                        LanguageData.getStringValue("UpdateProfile"),
                        1
                    )
                }
            }
        }
    }

    override fun onCalenderCalenderClick(view: View) {
        showDatePickerDialog(updateProfileViewModel.dateOfBirth)
    }

    fun onCalenderCalenderClicked() {
        showDatePickerDialog(updateProfileViewModel.dateOfBirth)
    }

    private fun isValidForAll(): Boolean {

        var isValidForAll = true
        if ((updateProfileViewModel.currentProfile.contains("1"))) {
            if (mDataBinding.inputFirstName.text.isNullOrEmpty()) {
                isValidForAll = false
                mDataBinding.inputLayoutFirstName.error =
                    LanguageData.getStringValue("PleaseEnterFirstName")
                mDataBinding.inputLayoutFirstName.isErrorEnabled = true
            } else {
                mDataBinding.inputLayoutFirstName.error = ""
                mDataBinding.inputLayoutFirstName.isErrorEnabled = false
            }
        }


        if ((updateProfileViewModel.currentProfile.contains("1"))) {
            if (mDataBinding.inputLastName.text.isNullOrEmpty()) {
                isValidForAll = false
                mDataBinding.inputLayoutLastName.error =
                    LanguageData.getStringValue("PleaseEnterLastName")
                mDataBinding.inputLayoutLastName.isErrorEnabled = true
            } else {
                mDataBinding.inputLayoutLastName.error = ""
                mDataBinding.inputLayoutLastName.isErrorEnabled = false
            }
        }

        if ((updateProfileViewModel.currentProfile.contains("1"))) {
            if (mDataBinding.inputDateOfBirth.text.isNullOrEmpty()) {
                isValidForAll = false
                mDataBinding.inputLayoutDateOfBirth.error =
                    LanguageData.getStringValue("PleaseSelectDate")
                mDataBinding.inputLayoutDateOfBirth.isErrorEnabled = true
            } else {
                mDataBinding.inputLayoutDateOfBirth.error = ""
                mDataBinding.inputLayoutDateOfBirth.isErrorEnabled = false
            }
        }

        if ((updateProfileViewModel.currentProfile.contains("1"))) {
            if (mDataBinding.inputNationalID.text.isNullOrEmpty()) {
                isValidForAll = false
                mDataBinding.inputLayoutNationalID.error =
                    LanguageData.getStringValue("PleaseEnterIdentityNumber")
                mDataBinding.inputLayoutNationalID.isErrorEnabled = true
            } else {
                mDataBinding.inputLayoutNationalID.error = ""
                mDataBinding.inputLayoutNationalID.isErrorEnabled = false

                if (isCnicMatches) {
                    mDataBinding.inputLayoutNationalID.error = ""
                    mDataBinding.inputLayoutNationalID.isErrorEnabled = false
                } else {
                    isValidForAll = false
                    mDataBinding.inputLayoutNationalID.error =
                        LanguageData.getStringValue("PleaseEnterValidIdentityNumber")
                    mDataBinding.inputLayoutNationalID.isErrorEnabled = true
                }
            }
        }


        if (!mDataBinding.inputEmail.text.isNullOrEmpty()) {
            if (!mDataBinding.inputEmail.text.trim().matches(emailPattern)) {
                isValidForAll = false
                mDataBinding.inputLayoutEmail.error =
                    LanguageData.getStringValue("PleaseEnterEmailAddress")

                mDataBinding.inputLayoutEmail.isErrorEnabled = true
            } else {
                mDataBinding.inputLayoutEmail.error = ""
                mDataBinding.inputLayoutEmail.isErrorEnabled = false
            }
        } else {
            mDataBinding.inputLayoutEmail.error = ""
            mDataBinding.inputLayoutEmail.isErrorEnabled = false
        }
        if ((updateProfileViewModel.currentProfile.contains("1") || updateProfileViewModel.currentProfile.contains(
                "2"
            ))
        ) {
            if (mDataBinding.inputAddress.text.isNullOrEmpty()) {
                isValidForAll = false
                mDataBinding.inputLayoutAddress.error =
                    LanguageData.getStringValue("PleaseEnterAddress")
                mDataBinding.inputLayoutAddress.isErrorEnabled = true
            } else {
                mDataBinding.inputLayoutAddress.error = ""
                mDataBinding.inputLayoutAddress.isErrorEnabled = false
            }
        }

        if ((updateProfileViewModel.currentProfile.contains("1") || updateProfileViewModel.currentProfile.contains(
                "2"
            ))
        ) {
            if (mDataBinding.inputCity.text.isNullOrEmpty()) {
                isValidForAll = false
                mDataBinding.inputLayoutCity.error = LanguageData.getStringValue("PleaseEnterCity")
                mDataBinding.inputLayoutCity.isErrorEnabled = true
            } else {
                if (!Pattern.matches(Constants.CityNameRegex, mDataBinding.inputCity.text.trim())) {
                    isValidForAll = false
                    mDataBinding.inputLayoutCity.error =
                        LanguageData.getStringValue("PleaseEnterCity")
                    mDataBinding.inputLayoutCity.isErrorEnabled = true
                } else {
                    mDataBinding.inputLayoutCity.error = ""
                    mDataBinding.inputLayoutCity.isErrorEnabled = false
                }
            }
        }


        return isValidForAll
    }

    override fun afterTextChanged(p0: Editable?) {
        //  Logger.debugLog("updateProfile","id ${p0.get}  id2 ${mDataBinding.inputEmail}")
        var cnic = mDataBinding.inputNationalID.text.toString().trim()
        var cnicLength = cnic.length
        isCnicMatches = !(cnicLength > 0 && !Pattern.matches(Constants.APP_CN_REGEX, cnic))
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    private fun showDatePickerDialog(dateOfBirth: String) {
        val dateParts: List<String> = dateOfBirth.split("-")
        var  year =0
        var month =0
        var dayOfMonth=0
            if(dateOfBirth.isNullOrEmpty())
        {
            val calendar: Calendar = Calendar.getInstance()
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            dayOfMonth= calendar.get(Calendar.DAY_OF_MONTH)

        }else{
         dayOfMonth = dateParts[2].toInt()
         month = dateParts[1].toInt()
         year = dateParts[0].toInt()}



        val datePickerDialog = DatePickerDialog(
            activity as UpdateProfileActivity,
            DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                var monthVal = (month).toString()
                var selectedDate = "$year-$monthVal-$day"
                val c = Calendar.getInstance()
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, month)
                c.set(Calendar.DAY_OF_MONTH, day)

                selectedDate = DateFormat.format("yyyy-MM-dd", c.time).toString()
                mDataBinding.inputDateOfBirth.setText(selectedDate)
            }, year, month - 1, dayOfMonth
        )
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis())
        datePickerDialog.show()
    }

    fun getUserEmailAddress(): String {
        var email = ""
        //Constants.CURRENT_USER_EMAIL
        if (!Constants.CURRENT_USER_EMAIL.isNullOrEmpty()) {
            email = Constants?.CURRENT_USER_EMAIL
            email = email.removePrefix("ID:")
            email = email.substringAfter(":")
            email = email.substringBefore("/")
        }
        return email
    }

}