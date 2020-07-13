package com.es.marocapp.usecase.favorites

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFavoritesEnterNumberBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import java.util.regex.Pattern

class FavoriteEnterContactFragment : BaseFragment<FragmentFavoritesEnterNumberBinding>(),
    FavoritesPaymentClickListener, TextWatcher {

    private lateinit var mActivitViewModel: FavoritesViewModel

    var msisdnEntered = ""

    var isNumberRegexMatches = false

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


        setStrings()
        subscribeObserver()
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
                                (activity as FavoritesActivity).finish()
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
                mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterCilNumber")
            }else{
                mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterContactNumber")
            }
        }else{
            mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterContactNumber")
        }

        mDataBinding.inputLayoutName.hint = LanguageData.getStringValue("EnterName")
    }

    override fun onNextButtonClick(view: View) {
        if(isValidForAll()){
            var nickName = mDataBinding.inputName.text.toString().trim()
            if(mActivitViewModel.isPaymentSelected.get()!!){
                if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                    var fatoratiNickName = "BillPayment_Fatourati_${mActivitViewModel.fatoratiTypeSelected}@$nickName"
                    mActivitViewModel.requestForAddFavoritesApi(activity,fatoratiNickName,Constants.getFatoratiAlias(msisdnEntered))
                }else{
                    mActivitViewModel.requestForAddFavoritesApi(activity,nickName,Constants.getNumberMsisdn(msisdnEntered))
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
                    }

                }
            }else{
                if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() || mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
                    isValidForAll = false
                    mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
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
                        }
                    } else {
                        isValidForAll = false
                        mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                    }
                }
            }
        }else{
            if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() || mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
                isValidForAll = false
                mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
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
                    }
                } else {
                    isValidForAll = false
                    mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
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

    override fun afterTextChanged(p0: Editable?) {
        var msisdn = mDataBinding.inputPhoneNumber.text.toString().trim()
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
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

}