package com.es.marocapp.usecase.sendmoney.tranferfundcase

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFundsTransferEnterMsisdnBinding
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.favorites.FavoritesActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class FundsTransferMsisdnFragment : BaseFragment<FragmentFundsTransferEnterMsisdnBinding>(),
    FundsTrasnferClickLisntener {

    private lateinit var mActivityViewModel : SendMoneyViewModel

    private var list_of_favorites = arrayOf("Select Favorite","Favorite 1", "Favorite 2", "Favorite 3")

    override fun setLayout(): Int {
        return R.layout.fragment_funds_transfer_enter_msisdn
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
            listener = this@FundsTransferMsisdnFragment
        }

        val adapterFavoriteType = ArrayAdapter<CharSequence>(activity as SendMoneyActivity, R.layout.layout_favorites_spinner_text, list_of_favorites)
        mDataBinding.spinnerSelectFavorites.apply {
            adapter = adapterFavoriteType
        }

        mActivityViewModel.trasferTypeSelected.get()?.let {
            (activity as SendMoneyActivity).setHeaderTitle(
                it
            )
        }
        (activity as SendMoneyActivity).setHeaderVisibility(true)
        //todo also here remove lenght-2 check in max line
        mDataBinding.inputPhoneNumber.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(
                Constants.APP_MSISDN_LENGTH.toInt() - 2
            )
        )

        subscribeObserver()
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
            mDataBinding.inputLayoutPhoneNumber.error = "Please Enter Valid Mobile Number"
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
        }else{
            mDataBinding.inputLayoutPhoneNumber.error = ""
            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

            var userMsisdn = mDataBinding.inputPhoneNumber.text.toString()
            if(userMsisdn.startsWith("0",false)){
                mDataBinding.inputLayoutPhoneNumber.error = ""
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                var userMSISDNwithPrefix = userMsisdn.removePrefix("0")
                userMSISDNwithPrefix = Constants.APP_MSISDN_PREFIX + userMSISDNwithPrefix
                userMSISDNwithPrefix = userMSISDNwithPrefix.removePrefix("+")

                mActivityViewModel.requestForGetAccountHolderInformationApi(activity,userMSISDNwithPrefix)
            }else{
                mDataBinding.inputLayoutPhoneNumber.error = "Please Enter Valid Mobile Number"
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
            }
        }
    }

    override fun onBackClickListner(view: View) {

    }

}