package com.es.marocapp.usecase.billpayment.fragments

import android.os.Bundle
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

class FragmentBillPaymentMsisdn : BaseFragment<FragmentBillPaymentMsisdnBinding>(),
    BillPaymentClickListner, AdapterView.OnItemSelectedListener {

    private lateinit var mActivityViewModel: BillPaymentViewModel

    private var list_of_favorites = arrayListOf<String>()

    var msisdnEntered = ""
    var code = ""


    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_msisdn
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as BillPaymentActivity).get(BillPaymentViewModel::class.java)
        mDataBinding.apply {
            listner = this@FragmentBillPaymentMsisdn
            viewmodel = mActivityViewModel
        }

        (activity as BillPaymentActivity).setHeaderTitle(
            LanguageData.getStringValue("BillPayment").toString()
        )

        mActivityViewModel.isUserSelectedFromFavorites.set(false)

        for(contacts in Constants.mContactListArray){
            var contactNumber = contacts.fri
            contactNumber = contactNumber.substringBefore("@")
            contactNumber = contactNumber.substringBefore("/")
            contactNumber = contactNumber.removePrefix(Constants.APP_MSISDN_PREFIX)
            contactNumber = "0$contactNumber"
            list_of_favorites.add(contactNumber)
        }
        list_of_favorites.add(0, LanguageData.getStringValue("SelectFavorite").toString())

        val adapterFavoriteType = ArrayAdapter<CharSequence>(activity as BillPaymentActivity, R.layout.layout_favorites_spinner_text,
            list_of_favorites as List<CharSequence>
        )
        mDataBinding.spinnerSelectFavorites.apply {
            adapter = adapterFavoriteType
        }
        mDataBinding.spinnerSelectFavorites.onItemSelectedListener = this@FragmentBillPaymentMsisdn
        (activity as BillPaymentActivity).setHeaderVisibility(true)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(true)

        (activity as BillPaymentActivity).mDataBinding.headerBillPayment.rootView.tv_company_title.text = mActivityViewModel.billTypeSelected.get()!!
        (activity as BillPaymentActivity).mDataBinding.headerBillPayment.rootView.img_company_icons.setImageResource(mActivityViewModel.billTypeSelectedIcon)

        mActivityViewModel.popBackStackTo = R.id.fragmentPostPaidBillType

        if(mActivityViewModel.isInternetSelected.get()!!){
            mDataBinding.inputLayoutCode.visibility = View.GONE
        }else{
            mDataBinding.inputLayoutCode.visibility = View.VISIBLE
        }

        setStrings()
        subscribeObserver()
    }

    private fun subscribeObserver() {
        mDataBinding.inputLayoutCode.hint = LanguageData.getStringValue("EnterCode")
        mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterContactNumber")
        mDataBinding.selectFavoriteTypeTitle.hint = LanguageData.getStringValue("SelectFavorite")
        mDataBinding.btnNext.text = LanguageData.getStringValue("Submit")
    }

    private fun setStrings() {
        mActivityViewModel.errorText.observe(this@FragmentBillPaymentMsisdn, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        }
        )

        mActivityViewModel.getPostPaidResourceInfoResponseListner.observe(this@FragmentBillPaymentMsisdn,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    mActivityViewModel.custId = it.response.custId
                    mActivityViewModel.custname = it.response.custname
                    mActivityViewModel.totalamount = it.response.totalamount
                    (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMsisdn_to_fragmentPostPaidBillDetails)
                }else{
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            }
        )
    }

    override fun onSubmitClickListner(view: View) {
        if(isValidForAll()){
            mActivityViewModel.requestForPostPaidFinancialResourceInfoApi(activity,code,msisdnEntered)
        }
    }

    override fun onBackClickListner(view: View) {

    }

    private fun isValidForAll(): Boolean {
        var isValidForAll = true

        //todo NUmber Lenght is Pending
        if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() || mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
            isValidForAll = false
            mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidMobileNumber")
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
                mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidMobileNumber")
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
            }
        }

        if(mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!){
            if(mDataBinding.inputCode.text.isNullOrEmpty() || mDataBinding.inputCode.text.toString().isEmpty()){
                isValidForAll = false
                mDataBinding.inputLayoutCode.error = LanguageData.getStringValue("PleaseEnterValidCode")
                mDataBinding.inputLayoutCode.isErrorEnabled = true
            }else{
                mDataBinding.inputLayoutCode.error = ""
                mDataBinding.inputLayoutCode.isErrorEnabled = false
                code = mDataBinding.inputCode.text.toString().trim()
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