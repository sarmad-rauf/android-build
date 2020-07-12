package com.es.marocapp.usecase.favorites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFavoritesEnterNumberBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.utils.Constants

class FavoriteEnterContactFragment : BaseFragment<FragmentFavoritesEnterNumberBinding>(),
    FavoritesPaymentClickListener {

    private lateinit var mActivitViewModel: FavoritesViewModel

    var msisdnEntered = ""

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
            }else{
                mActivitViewModel.popBackStackTo = R.id.favoritesAddOrViewFragment
            }
        }else{
            mActivitViewModel.popBackStackTo = R.id.favoritesAddOrViewFragment
        }

        (activity as FavoritesActivity).setHeader(LanguageData.getStringValue("Add").toString())

        setStrings()
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
            (activity as FavoritesActivity).finish()
        }
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

                    msisdnEntered = mDataBinding.inputPhoneNumber.text.toString().trim()

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

                        msisdnEntered = userMSISDNwithPrefix
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

                    msisdnEntered = userMSISDNwithPrefix
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

}