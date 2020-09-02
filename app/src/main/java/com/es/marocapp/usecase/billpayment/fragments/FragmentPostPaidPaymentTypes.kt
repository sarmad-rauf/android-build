package com.es.marocapp.usecase.billpayment.fragments

import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.BillPaymentFavoritesAdapter
import com.es.marocapp.adapter.PaymentItemsAdapter
import com.es.marocapp.databinding.FragmentBillPaymentTypeBinding
import com.es.marocapp.databinding.LayoutBillPaymentTypeQuickRechargeBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.BillPaymentFatoratiStepTwoResponse
import com.es.marocapp.model.responses.Contact
import com.es.marocapp.model.responses.Creancier
import com.es.marocapp.model.responses.Param
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.airtime.AirTimeViewModel
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class FragmentPostPaidPaymentTypes : BaseFragment<LayoutBillPaymentTypeQuickRechargeBinding>() {

    private lateinit var mActivityViewModel : BillPaymentViewModel

    private lateinit var mBillPaymentItemTypeAdapter: PaymentItemsAdapter
    private lateinit var mBillPaymentFavouritesAdapter: BillPaymentFavoritesAdapter
    private var mFavoritesList : ArrayList<Contact> = arrayListOf()
    private var mBillPaymentTypes: ArrayList<String> = ArrayList()
    private var mBillPaymentTypesIcon: ArrayList<Int>  = ArrayList()

    override fun setLayout(): Int {
        return R.layout.layout_bill_payment_type_quick_recharge
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as BillPaymentActivity).get(
            BillPaymentViewModel::class.java
        )
        mDataBinding.apply {
        }

        mDataBinding.tvPaymentType.text = LanguageData.getStringValue("PaymentType")
        (activity as BillPaymentActivity).setHeaderTitle(
            LanguageData.getStringValue("BillPayment").toString()
        )

        mDataBinding.tvManageFavorites.text = LanguageData.getStringValue("ManageFavorites")
        if(Constants.mContactListArray.isEmpty()){
            mDataBinding.billPaymentMangeFavGroup.visibility = View.GONE
        }else{
            mDataBinding.billPaymentMangeFavGroup.visibility = View.VISIBLE
            mFavoritesList.clear()
            for(contacts in Constants.mContactListArray){
                var contactName = contacts.contactName
                if(contactName.contains("BillPayment_TelecomBill_Internet@") || contactName.contains("BillPayment_TelecomBill_PostpaidMobile@") ||
                        contactName.contains("BillPayment_TelecomBill_PostpaidFix@") || contactName.contains("BillPayment_Fatourati_")){
                    mFavoritesList.add(contacts)
                }
            }

            if(mFavoritesList.isEmpty()){
                mDataBinding.billPaymentMangeFavGroup.visibility = View.GONE
            }else{
                mDataBinding.billPaymentMangeFavGroup.visibility = View.VISIBLE

                mBillPaymentFavouritesAdapter = BillPaymentFavoritesAdapter(mFavoritesList,object : BillPaymentFavoritesAdapter.BillPaymentFavoriteClickListner{
                    override fun onFavoriteItemTypeClick(selectedContact: Contact) {
                        if(selectedContact.contactName.contains("BillPayment_Fatourati_")){
                            mActivityViewModel.isBillUseCaseSelected.set(false)
                            mActivityViewModel.isFatoratiUseCaseSelected.set(true)

                            //TelecomBillPayment Fatourati Use Case
                            var contactName = selectedContact.contactName
                            contactName = contactName.substringAfter("@")
                            var name = contactName.substringBefore(",")
                            var withoutNameCommaSepratedString = contactName.substringAfter(",")
                            var result: List<String> = withoutNameCommaSepratedString.split(",").map { it.trim() }
                            /*for(value in result){
                                Log.d("dataFromString",value)
                            }*/
                            var creancier = Creancier(result[0],result[1],"","")
                            mActivityViewModel.fatoratiTypeSelected.set(creancier)

                            var stepTwoResponseDummy = BillPaymentFatoratiStepTwoResponse("",
                                Param("",result[2],""),result[3],""
                            )
                            mActivityViewModel.fatoratiStepTwoObserver.set(stepTwoResponseDummy)

                            var number = selectedContact.fri
                            number = number.substringBefore("@")
                            number = number.substringBefore("/")

                            mActivityViewModel.isUserSelectedFromFavorites.set(true)
                            mActivityViewModel.isQuickRechargeCallForBillOrFatouratie.set(true)

                            mActivityViewModel.transferdAmountTo = number
                            mActivityViewModel.requestForFatoratiStepFourApi(activity)

                        }else if(selectedContact.contactName.contains("BillPayment_TelecomBill_Internet@")){
                            mActivityViewModel.isBillUseCaseSelected.set(true)
                            mActivityViewModel.isFatoratiUseCaseSelected.set(false)
                            mActivityViewModel.isPostPaidMobileSelected.set(false)
                            mActivityViewModel.isPostPaidFixSelected.set(false)
                            mActivityViewModel.isInternetSelected.set(true)

                            //TelecomBillPayment Internet Use Case
                        }else if(selectedContact.contactName.contains("BillPayment_TelecomBill_PostpaidMobile@")){
                            mActivityViewModel.isBillUseCaseSelected.set(true)
                            mActivityViewModel.isFatoratiUseCaseSelected.set(false)
                            mActivityViewModel.isPostPaidMobileSelected.set(true)
                            mActivityViewModel.isPostPaidFixSelected.set(false)
                            mActivityViewModel.isInternetSelected.set(false)

                            //TelecomBillPayment PostPaidMobile Use Case
                        }else if(selectedContact.contactName.contains("BillPayment_TelecomBill_PostpaidFix@")){
                            mActivityViewModel.isBillUseCaseSelected.set(true)
                            mActivityViewModel.isFatoratiUseCaseSelected.set(false)
                            mActivityViewModel.isPostPaidMobileSelected.set(false)
                            mActivityViewModel.isPostPaidFixSelected.set(true)
                            mActivityViewModel.isInternetSelected.set(false)

                            //TelecomBillPayment PostPaidFix Use Case
                        }
                    }

                    override fun onDeleteFavoriteItemTypeClick(selectedContact: Contact) {
                        mActivityViewModel.requestForDeleteFavoriteApi(activity,selectedContact.fri)
                    }

                })

                mDataBinding.manageFavRecycler.apply {
                    adapter = mBillPaymentFavouritesAdapter
                    layoutManager = LinearLayoutManager(activity as BillPaymentActivity,LinearLayoutManager.HORIZONTAL,false)
                }
            }
        }


        (activity as BillPaymentActivity).setHeaderVisibility(true)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(false)

        mActivityViewModel.popBackStackTo = -1

        mBillPaymentTypes.clear()
        mBillPaymentTypes.apply {
            add(LanguageData.getStringValue("Bill").toString())
            add(LanguageData.getStringValue("WaterAndElectricity").toString())
        }

        mBillPaymentTypesIcon.clear()
        mBillPaymentTypesIcon.apply {
            add(R.drawable.bill_blue)
            add(R.drawable.water_electricty_blue)
        }

        mBillPaymentItemTypeAdapter = PaymentItemsAdapter(mBillPaymentTypes, mBillPaymentTypesIcon,object : PaymentItemsAdapter.PaymentItemTypeClickListner{
            override fun onPaymentItemTypeClick(paymentItems: String) {
                when(paymentItems){
                    LanguageData.getStringValue("Bill") -> {
                        mActivityViewModel.isBillUseCaseSelected.set(true)
                        mActivityViewModel.isFatoratiUseCaseSelected.set(false)
                        mActivityViewModel.isQuickRechargeCallForBillOrFatouratie.set(false)
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidPaymentTypes_to_fragmentPostPaidServiceProvider)
                    }
                    LanguageData.getStringValue("WaterAndElectricity") -> {
                        mActivityViewModel.isBillUseCaseSelected.set(false)
                        mActivityViewModel.isFatoratiUseCaseSelected.set(true)
                        mActivityViewModel.isQuickRechargeCallForBillOrFatouratie.set(false)
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidPaymentTypes_to_fragmentPostPaidServiceProvider)
                    }
                    else -> Toast.makeText(activity,"Nothing Clicked Clicked", Toast.LENGTH_SHORT).show()

                }
            }

        })
        mDataBinding.paymentTypeRecycler.apply {
            adapter = mBillPaymentItemTypeAdapter
            layoutManager = LinearLayoutManager(activity as BillPaymentActivity)
        }

        subscribeObserver()
    }

    private fun subscribeObserver() {
        mActivityViewModel.getFatoratiStepFourResponseListner.observe(this@FragmentPostPaidPaymentTypes,
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
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentPostPaidPaymentTypes_to_fragmentPostPaidBillDetails)
                    }
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )

        mActivityViewModel.errorText.observe(this@FragmentPostPaidPaymentTypes, Observer {
            DialogUtils.showErrorDialoge(activity, it)
        }
        )

        mActivityViewModel.getDeleteFavoritesResponseListner.observe(this@FragmentPostPaidPaymentTypes,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(!it.contactList.isNullOrEmpty()){
                        Constants.mContactListArray.clear()
                        Constants.mContactListArray.addAll(it.contactList)
                        mDataBinding.billPaymentMangeFavGroup.visibility = View.VISIBLE

                        mFavoritesList.clear()
                        for(contacts in Constants.mContactListArray){
                            var contactName = contacts.contactName
                            if(contactName.contains("BillPayment_TelecomBill_Internet@") || contactName.contains("BillPayment_TelecomBill_PostpaidMobile@") ||
                                contactName.contains("BillPayment_TelecomBill_PostpaidFix@") || contactName.contains("BillPayment_Fatourati_")){
                                mFavoritesList.add(contacts)
                            }
                        }

                        if(mFavoritesList.isEmpty()){
                            mDataBinding.billPaymentMangeFavGroup.visibility = View.GONE
                        }else{
                            mDataBinding.billPaymentMangeFavGroup.visibility = View.VISIBLE
                            mBillPaymentFavouritesAdapter.notifyDataSetChanged()
                        }
                        }else{
                        Constants.mContactListArray.clear()
                        mDataBinding.billPaymentMangeFavGroup.visibility = View.GONE
                    }
                }else{
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            }
        )
    }

}