package com.es.marocapp.usecase.billpayment.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.FatoratiPaidBillAdapter
import com.es.marocapp.adapter.PaidBillStatusAdpater
import com.es.marocapp.databinding.FragmentBillPaymentSuccessBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentClickListner
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class FragmentPostPaidBillPaymentSuccess : BaseFragment<FragmentBillPaymentSuccessBinding>(),
    BillPaymentClickListner {

    private lateinit var mActivityViewModel : BillPaymentViewModel

    private lateinit var mBillPaidAdapter : PaidBillStatusAdpater
    private lateinit var mFatoratiBillPaidAdapter : FatoratiPaidBillAdapter


    override fun setLayout(): Int {
       return R.layout.fragment_bill_payment_success
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as BillPaymentActivity).get(BillPaymentViewModel::class.java)
        mDataBinding.apply {
            listner = this@FragmentPostPaidBillPaymentSuccess
            viewmodel = mActivityViewModel
        }

        mDataBinding.addToFavoriteCheckBox.setOnClickListener {
            DialogUtils.showAddToFavoriteDialoge(activity as BillPaymentActivity,object : DialogUtils.OnAddToFavoritesDialogClickListner{
                override fun onDialogYesClickListner(nickName: String) {
                    mDataBinding.addToFavoriteCheckBox.isActivated = true
                    mDataBinding.addToFavoriteCheckBox.isChecked = true
                    mDataBinding.addToFavoriteCheckBox.isClickable = false

                    var tranferAmountToWithoutAlias = mActivityViewModel.transferdAmountTo.substringBefore("@")
                    tranferAmountToWithoutAlias = tranferAmountToWithoutAlias.substringBefore("/")

                    if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){


                        /*Storing Fatorai bill Fav as
                        Util_Redal@MyNickName,codeCreance,creancierID,nomChamp,refTxFatourati
*/
    //below code need to be updated from old to new format of nickname ....refrence to favouriteEnterContactFragment

//                        var fatoratiNickName = "Util_${mActivityViewModel.fatoratiTypeSelected.get()!!.nomCreancier}@$nickName" +
//                                ",${mActivityViewModel.fatoratiTypeSelected.get()!!.codeCreance}," +
//                                mActivityViewModel.fatoratiTypeSelected.get()!!.codeCreancier+","+mActivityViewModel.fatoratiStepThreeObserver.get()!!.param.nomChamp+","+
//                                mActivityViewModel.fatoratiStepThreeObserver.get()!!.refTxFatourati

                        var fatoratiNickName = "Util_${mActivityViewModel.fatoratiTypeSelected.get()!!.nomCreancier}@$nickName" +
                                ",${mActivityViewModel.fatoratiTypeSelected.get()!!.codeCreance}," +
                                mActivityViewModel.fatoratiTypeSelected.get()!!.codeCreancier+","+mActivityViewModel.validatedParams+","+
                                mActivityViewModel.fatoratiStepThreeObserver.get()!!.refTxFatourati

                        mActivityViewModel.requestForAddFavoritesApi(activity,fatoratiNickName,Constants.getFatoratiAlias(mActivityViewModel.transferdAmountTo))
                    }else if(mActivityViewModel.isBillUseCaseSelected.get()!!){

                        var billPaymentNickName = ""
                        var billPaymentNumber = ""

                        if(mActivityViewModel.isInternetSelected.get()!!){
                           // billPaymentNickName = "BillPayment_TelecomBill_Internet@$nickName"
                            billPaymentNickName = "Telec_Internet@$nickName"
                            billPaymentNumber = Constants.getPostPaidInternetDomainAlias(mActivityViewModel.transferdAmountTo)
                        }else if(mActivityViewModel.isPostPaidMobileSelected.get()!!){
                            billPaymentNickName = "Telec_PostpaidMobile@$nickName,${mActivityViewModel.mCodeEntered}"
                            billPaymentNumber = Constants.getPostPaidMobileDomainAlias(mActivityViewModel.transferdAmountTo)
                        }else if(mActivityViewModel.isPostPaidFixSelected.get()!!){
                            billPaymentNickName = "Telec_PostpaidFix@$nickName,${mActivityViewModel.mCodeEntered}"
                            billPaymentNumber = Constants.getPostPaidFixedDomainAlias(mActivityViewModel.transferdAmountTo)
                        }
                        mActivityViewModel.requestForAddFavoritesApi(activity,billPaymentNickName,billPaymentNumber)
                    }
                }

                override fun onDialogNoClickListner() {
                    mDataBinding.addToFavoriteCheckBox.isActivated = false
                    mDataBinding.addToFavoriteCheckBox.isChecked = false
                    mDataBinding.addToFavoriteCheckBox.isClickable = true
                }

            })
        }

        if(mActivityViewModel.isUserSelectedFromFavorites.get()!!){
            mDataBinding.addToFavoriteCheckBox.isActivated = true
            mDataBinding.addToFavoriteCheckBox.isChecked = true
            mDataBinding.addToFavoriteCheckBox.isClickable = false
        }else{
            mDataBinding.addToFavoriteCheckBox.isActivated = false
            mDataBinding.addToFavoriteCheckBox.isChecked = false
            mDataBinding.addToFavoriteCheckBox.isClickable = true
        }

        mActivityViewModel.popBackStackTo = -1

        (activity as BillPaymentActivity).setHeaderVisibility(false)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(false)

        mDataBinding.tvSuccessTitle.text = LanguageData.getStringValue("BillPayment")

        if(mActivityViewModel.isBillUseCaseSelected.get()!!){
            populatePaidBillList()

            mDataBinding.fatoratiFeeGroup.visibility = View.GONE
        }

        if(mActivityViewModel.isFatoratiUseCaseSelected.get()!!){
            populateFatoratiBillList()
            mDataBinding.fatoratiFeeGroup.visibility = View.VISIBLE

            mDataBinding.tvFatoratiFeeTitle.text = LanguageData.getStringValue("FatouratiTotalFeeCharged")
            mDataBinding.tvFatoratiFeeVal.text = Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mActivityViewModel.fatoratiFeeAmountCalculated
        }

        subscribeObserver()
    }

    private fun populateFatoratiBillList() {
        var receiverNumber = mActivityViewModel.transferdAmountTo.substringBefore("@")
        receiverNumber = receiverNumber.substringBefore("/")
        mFatoratiBillPaidAdapter = FatoratiPaidBillAdapter(
            mActivityViewModel.getPostPaidFatoratiResponseListner.value!!,mActivityViewModel.listOfSelectedBillAmount,mActivityViewModel.fatoratiFee,
            receiverNumber)

        mDataBinding.mPaidBillsRecycler.apply {
            adapter = mFatoratiBillPaidAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun populatePaidBillList() {
        var receiverNumber = mActivityViewModel.transferdAmountTo.substringBefore("@")
        receiverNumber = receiverNumber.substringBefore("/")
        mBillPaidAdapter = PaidBillStatusAdpater(mActivityViewModel.listOfPostPaidBillPayment,mActivityViewModel.listOfSelectedBillAmount,mActivityViewModel.listOfSelectedBillFee,
            receiverNumber,mActivityViewModel.ReceiverName)

        mDataBinding.mPaidBillsRecycler.apply {
            adapter = mBillPaidAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun subscribeObserver() {
        mActivityViewModel.getAddFavoritesResponseListner.observe(this@FragmentPostPaidBillPaymentSuccess,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(!it.contactList.isNullOrEmpty()){
                        Constants.mContactListArray.clear()
                        Constants.mContactListArray.addAll(it.contactList)
                        DialogUtils.showErrorDialoge(activity,it.description)
                    }
                }else{
                    mDataBinding.addToFavoriteCheckBox.isActivated = false
                    mDataBinding.addToFavoriteCheckBox.isChecked = false
                    mDataBinding.addToFavoriteCheckBox.isClickable = true
                    DialogUtils.showErrorDialoge(activity,it.description)
                }
            }
        )
    }


    override fun onSubmitClickListner(view: View) {
        mActivityViewModel.isPostPaidMobileSelected.set(false)
        mActivityViewModel.isPostPaidFixSelected.set(false)
        mActivityViewModel.isInternetSelected.set(false)
        mActivityViewModel.isBillUseCaseSelected.set(false)
        mActivityViewModel.isFatoratiUseCaseSelected.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as BillPaymentActivity).startNewActivityAndClear(
            activity as BillPaymentActivity,
            MainActivity::class.java
        )
    }

    override fun onBackClickListner(view: View) {
        mActivityViewModel.isPostPaidMobileSelected.set(false)
        mActivityViewModel.isPostPaidFixSelected.set(false)
        mActivityViewModel.isInternetSelected.set(false)
        mActivityViewModel.isBillUseCaseSelected.set(false)
        mActivityViewModel.isFatoratiUseCaseSelected.set(false)
        Constants.HEADERS_FOR_PAYEMNTS = false
        (activity as BillPaymentActivity).startNewActivityAndClear(
            activity as BillPaymentActivity,
            MainActivity::class.java
        )
    }

}