package com.es.marocapp.usecase.billpayment

import android.os.Bundle
import android.view.View
import android.widget.ExpandableListView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.AirTimeDataAdpater
import com.es.marocapp.adapter.BillPaymentExpandableAdapter
import com.es.marocapp.adapter.BillPaymentFavoritesAdapter
import com.es.marocapp.databinding.FragmentBillPaymentMainTypeLayoutBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.billpaymentmodel.BillPaymentMenuModel
import com.es.marocapp.model.billpaymentmodel.BillPaymentSubMenuModel
import com.es.marocapp.model.responses.*
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger
import com.google.android.material.bottomsheet.BottomSheetBehavior


class FragmentBillPaymentMain : BaseFragment<FragmentBillPaymentMainTypeLayoutBinding>() {

    private lateinit var mActivityViewModel: BillPaymentViewModel
    private lateinit var mBillPaymentFavouritesAdapter: BillPaymentFavoritesAdapter
    private var mFavoritesList: ArrayList<Contact> = arrayListOf()

    private var mTelecomBillSubMenusData: ArrayList<String> = arrayListOf()
    private var mTelecomBillSubMenusInwiData: ArrayList<String> = arrayListOf()
    private lateinit var mTelecomBillSubMenusAdapter: AirTimeDataAdpater
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    lateinit var listDataHeader: ArrayList<BillPaymentMenuModel>
    var listDataChild: HashMap<String, ArrayList<BillPaymentSubMenuModel>>? = HashMap<String, ArrayList<BillPaymentSubMenuModel>>()
    lateinit var mExpandableRecyclerAdapter : BillPaymentExpandableAdapter

    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_main_type_layout
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(activity as BillPaymentActivity).get(
            BillPaymentViewModel::class.java
        )
        mDataBinding.apply {
        }

        sheetBehavior = BottomSheetBehavior.from(mDataBinding.bottomSheetAirTime)

        mDataBinding.tvPaymentType.text = LanguageData.getStringValue("PaymentType")
        (activity as BillPaymentActivity).setHeaderTitle(
            LanguageData.getStringValue("BillPayment").toString()
        )

        mDataBinding.tvManageFavorites.text = LanguageData.getStringValue("ManageFavorites")
        if (Constants.mContactListArray.isEmpty()) {
            mDataBinding.billPaymentMangeFavGroup.visibility = View.GONE
        } else {
            mDataBinding.billPaymentMangeFavGroup.visibility = View.VISIBLE
            mFavoritesList.clear()
            for (contacts in Constants.mContactListArray) {
                var contactName = contacts.contactName
                if (contactName.contains("Telec_Internet@") || contactName.contains("Telec_PostpaidMobile@") ||
                    contactName.contains("Telec_PostpaidFix@") || contactName.contains("Util_")
                ) {
                    if (contactName.contains("Util_")) {
                        if (contactName.contains(",")) {
                            mFavoritesList.add(contacts)
                        }
                    } else {
                        mFavoritesList.add(contacts)
                    }
                }
            }
            mActivityViewModel.stepFourLydecSelected=false
            mActivityViewModel.isSelectedBillMatchedwithfatouratiSeperateMenuBillNames=false
            if (mFavoritesList.isEmpty()) {
                mDataBinding.billPaymentMangeFavGroup.visibility = View.GONE
            } else {
                mDataBinding.billPaymentMangeFavGroup.visibility = View.VISIBLE
                mBillPaymentFavouritesAdapter = BillPaymentFavoritesAdapter(mFavoritesList,
                    object : BillPaymentFavoritesAdapter.BillPaymentFavoriteClickListner {
                        override fun onFavoriteItemTypeClick(selectedContact: Contact) {
                            if (selectedContact.contactName.contains("Util_")) {
                                mActivityViewModel.isBillUseCaseSelected.set(false)
                                mActivityViewModel.isFatoratiUseCaseSelected.set(true)

                                //TelecomBillPayment Fatourati Use Case
                                var contactName = selectedContact.contactName
                                var companyNameUtilString  = contactName.substringBefore("@")
                                var companyName = companyNameUtilString.substringAfter("_").trim()
                                Logger.debugLog("CompanyNameFatorati",companyName)
                                contactName = contactName.substringAfter("@")
                                var name = contactName.substringBefore(",")
                                var withoutNameCommaSepratedString = contactName.substringAfter(",")
                                var result: List<String> =
                                    withoutNameCommaSepratedString.split(",").map { it.trim() }
                                /*for(value in result){
                                    Log.d("dataFromString",value)
                                }*/
                                var creancier = Creancier(result[0], result[1], "", companyName)
                                mActivityViewModel.fatoratiTypeSelected.set(creancier)

//                                var stepTwoResponseDummy = BillPaymentFatoratiStepThreeResponse(
//                                    "",
//                                    Param("", result[2], ""), result[3], ""
//                                )
                         //       mActivityViewModel.fatoratiStepThreeObserver.set(stepTwoResponseDummy)

                                var number = selectedContact.fri
                                number = number.substringBefore("@")
                                number = number.substringBefore("/")

                                mActivityViewModel.isUserSelectedFromFavorites.set(true)
                                mActivityViewModel.isQuickRechargeCallForBillOrFatouratie.set(true)

                                mActivityViewModel.transferdAmountTo = number
                                mActivityViewModel.requestForFatoratiStepFourApi(activity)

                            } else if (selectedContact.contactName.contains("Telec_Internet@")) {
                                mActivityViewModel.isBillUseCaseSelected.set(true)
                                mActivityViewModel.isFatoratiUseCaseSelected.set(false)
                                mActivityViewModel.isPostPaidMobileSelected.set(false)
                                mActivityViewModel.isPostPaidFixSelected.set(false)
                                mActivityViewModel.isInternetSelected.set(true)

                                mActivityViewModel.isUserSelectedFromFavorites.set(true)
                                mActivityViewModel.isQuickRechargeCallForBillOrFatouratie.set(true)

                                var number = selectedContact.fri
                                number = number.substringBefore("@")
                                number = number.substringBefore("/")

                                var msisdnEntered = number
                                var code = ""
                                Logger.debugLog("abro","case selectes: ${selectedContact.toString()}")
                                mActivityViewModel.requestForPostPaidFinancialResourceInfoApi(
                                    activity,
                                    code,
                                    msisdnEntered
                                )

                                //TelecomBillPayment Internet Use Case
                            } else if (selectedContact.contactName.contains("Telec_PostpaidMobile@")) {
                                mActivityViewModel.isBillUseCaseSelected.set(true)
                                mActivityViewModel.isFatoratiUseCaseSelected.set(false)
                                mActivityViewModel.isPostPaidMobileSelected.set(true)
                                mActivityViewModel.isPostPaidFixSelected.set(false)
                                mActivityViewModel.isInternetSelected.set(false)

                                mActivityViewModel.isUserSelectedFromFavorites.set(true)
                                mActivityViewModel.isQuickRechargeCallForBillOrFatouratie.set(true)

                                var contactName = selectedContact.contactName
                                contactName = contactName.substringAfter("@")
                                contactName = contactName.substringAfter(",")

                                var number = selectedContact.fri
                                number = number.substringBefore("@")
                                number = number.substringBefore("/")

                                var msisdnEntered = number
                                var code = contactName

                                mActivityViewModel.requestForPostPaidFinancialResourceInfoApi(
                                    activity,
                                    code,
                                    msisdnEntered
                                )

                                //TelecomBillPayment PostPaidMobile Use Case
                            } else if (selectedContact.contactName.contains("Telec_PostpaidFix@")) {
                                mActivityViewModel.isBillUseCaseSelected.set(true)
                                mActivityViewModel.isFatoratiUseCaseSelected.set(false)
                                mActivityViewModel.isPostPaidMobileSelected.set(false)
                                mActivityViewModel.isPostPaidFixSelected.set(true)
                                mActivityViewModel.isInternetSelected.set(false)

                                mActivityViewModel.isUserSelectedFromFavorites.set(true)
                                mActivityViewModel.isQuickRechargeCallForBillOrFatouratie.set(true)

                                var contactName = selectedContact.contactName
                                contactName = contactName.substringAfter("@")
                                contactName = contactName.substringAfter(",")

                                var number = selectedContact.fri
                                number = number.substringBefore("@")
                                number = number.substringBefore("/")

                                var msisdnEntered = number
                                var code = contactName

                                mActivityViewModel.requestForPostPaidFinancialResourceInfoApi(
                                    activity,
                                    code,
                                    msisdnEntered
                                )

                                //TelecomBillPayment PostPaidFix Use Case
                            }
                        }

                        override fun onDeleteFavoriteItemTypeClick(selectedContact: Contact) {
                            mActivityViewModel.requestForDeleteFavoriteApi(
                                activity,
                                selectedContact.fri
                            )
                        }

                    })

                mDataBinding.manageFavRecycler.apply {
                    adapter = mBillPaymentFavouritesAdapter
                    layoutManager = LinearLayoutManager(
                        activity as BillPaymentActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                }
            }
        }


        (activity as BillPaymentActivity).setHeaderVisibility(true)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(false)

        mActivityViewModel.popBackStackTo = -1

        mActivityViewModel.requestForBillPaymentCompaniesApi(activity)
//        populateTelecomBillsSubMenusList()
//        prepareDataForBillPayment()
        setStrings()
        initListner()
        subscribeObserver()
        //todo need to set these value
/*      mActivityViewModel.isBillUseCaseSelected.set(false)
        mActivityViewModel.isFatoratiUseCaseSelected.set(true)
        mActivityViewModel.isQuickRechargeCallForBillOrFatouratie.set(false)*/
    }

    private fun setStrings() {
        mDataBinding.tvPaymentType.text = LanguageData.getStringValue("PaymentType")
        (activity as BillPaymentActivity).setHeaderTitle(
            LanguageData.getStringValue("BillPayment").toString()
        )
        mDataBinding.tvManageFavorites.text = LanguageData.getStringValue("ManageFavorites")
        mDataBinding.btnCancel.text = LanguageData.getStringValue("BtnTitle_Cancel")
    }

    private fun prepareDataForBillPayment() {
/*
        listDataHeader = arrayListOf()
        listDataHeader.apply {
            this!!.add(BillPaymentMenuModel("Telecom Bill Payment",R.drawable.telecom_bill_updated_icon))
            this!!.add(BillPaymentMenuModel("Water And Electricity",R.drawable.water_electricity_update_icon))
        }

        val myTelecomBillSubMenus : ArrayList<BillPaymentSubMenuModel> = arrayListOf()
        myTelecomBillSubMenus.apply {
            add(BillPaymentSubMenuModel("Post Paid Mobile",R.drawable.default_no_company_icon))
            add(BillPaymentSubMenuModel("Post Paid Fixe",R.drawable.default_no_company_icon))
            add(BillPaymentSubMenuModel("Internet",R.drawable.default_no_company_icon))
        }

        val myWaterAndElectricitySubMenus : ArrayList<BillPaymentSubMenuModel> = arrayListOf()
        myWaterAndElectricitySubMenus.apply {
            add(BillPaymentSubMenuModel("Redal",R.drawable.redal_company_icon))
            add(BillPaymentSubMenuModel("Redama",R.drawable.radeema_company_icon))
        }

        listDataChild?.put(listDataHeader?.get(0)?.companyTilte!!, myTelecomBillSubMenus)
        listDataChild?.put(listDataHeader?.get(1)?.companyTilte!!, myWaterAndElectricitySubMenus)
*/
        mExpandableRecyclerAdapter = BillPaymentExpandableAdapter(activity as BillPaymentActivity, listDataHeader, listDataChild
        )

        // setting list adapter
        mDataBinding.paymentTypeRecycler.setAdapter(mExpandableRecyclerAdapter)

        // Listview Group click listener
        mDataBinding.paymentTypeRecycler.setOnGroupClickListener { parent, v, groupPosition, id ->
            /*Toast.makeText(activity,
                "Group Clicked " + listDataHeader[groupPosition].companyTilte,
                Toast.LENGTH_SHORT).show()*/
            Logger.debugLog("BillPaymentTesting",listDataHeader[groupPosition].companyTilte)

            false
        }

        // Listview Group expanded listener
        mDataBinding.paymentTypeRecycler.setOnGroupExpandListener {
           /* Toast.makeText(activity,
                listDataHeader[it].companyTilte + " Expanded",
                Toast.LENGTH_SHORT).show()*/
            Logger.debugLog("BillPaymentTesting",listDataHeader[it].companyTilte)
        }

        // Listview Group collasped listener
        mDataBinding.paymentTypeRecycler.setOnGroupCollapseListener {
            /*Toast.makeText(activity,
                listDataHeader[it].companyTilte + " Collapsed",
                Toast.LENGTH_SHORT).show()*/
            Logger.debugLog("BillPaymentTesting",listDataHeader[it].companyTilte)
        }

        // Listview on child click listener
        mDataBinding.paymentTypeRecycler.setOnChildClickListener(object  : ExpandableListView.OnChildClickListener{
            override fun onChildClick(
                parent: ExpandableListView?,
                v: View?,
                groupPosition: Int,
                childPosition: Int,
                id: Long
            ): Boolean {
                /*Toast.makeText(
                    activity,
                    listDataHeader[groupPosition].companyTilte
                            + " : "
                            + listDataChild?.get(listDataHeader[groupPosition].companyTilte)?.get(
                        childPosition)?.subCompanyTitle, Toast.LENGTH_SHORT)
                    .show()*/

                Logger.debugLog("BillPaymentTesting",listDataHeader[groupPosition].companyTilte
                        + " : "
                        + listDataChild?.get(listDataHeader[groupPosition].companyTilte)?.get(
                    childPosition)?.subCompanyTitle)

                var currentSelectedBill = listDataChild?.get(listDataHeader[groupPosition].companyTilte)?.get(
                    childPosition)?.subCompanyTitle
                var currentSelectedBilLogo = listDataChild?.get(listDataHeader[groupPosition].companyTilte)?.get(
                    childPosition)?.subCompanyIcon

                if (currentSelectedBilLogo != null) {
                    mActivityViewModel.userSelectedCreancerLogo =
                        currentSelectedBilLogo
                }
                if (currentSelectedBill != null) {
                    mActivityViewModel.userSelectedCreancer =
                        currentSelectedBill
                    mActivityViewModel.selectedCreancer.set(currentSelectedBill)
                }

                //checking if selected bill is Telecom bill for which we have to run flow of fatourati
                for(b in Constants.iamBillsTriggerFatouratiFlow.indices)
                {
                    Logger.debugLog("billPayment","iamBillFatoratiList ${Constants.iamBillsTriggerFatouratiFlow[b]}")
                    if(Constants.iamBillsTriggerFatouratiFlow[b].equals(currentSelectedBill))
                    {
                       mActivityViewModel.isIamFatouratiSelected=true
                        break
                    }
                }


                //checking for LYDEC selection for change flow but changed flow for all companies now it will always be true
                mActivityViewModel.isSelectedBillMatchedwithfatouratiSeperateMenuBillNames=true


                Logger.debugLog("billPayment","isamBillFatorati ${mActivityViewModel.isIamFatouratiSelected}")


                 val selectedSubCompany= listDataChild?.get(listDataHeader[groupPosition].companyTilte)?.get(
                childPosition)?.subCompanyTitle
                if (selectedSubCompany != null) {
                    if(selectedSubCompany.equals(Constants.KEY_FOR_POST_PAID_TELECOM_BILL)||selectedSubCompany.toLowerCase().equals(Constants.BILLTYPEINWI.toLowerCase())){
                        val state =
                            if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                                BottomSheetBehavior.STATE_COLLAPSED
                            else
                                BottomSheetBehavior.STATE_EXPANDED
                        sheetBehavior.state = state
                        if(selectedSubCompany.equals(Constants.KEY_FOR_POST_PAID_TELECOM_BILL))
                        {

                            populateTelecomBillsSubMenusList(Constants.KEY_FOR_POST_PAID_TELECOM_BILL)
                        }
                        else{
                            populateTelecomBillsSubMenusList(Constants.BILLTYPEINWI.toLowerCase())
                        }
                        Logger.debugLog("BillPaymentTesting","expand sheet")
                        mActivityViewModel.isBillUseCaseSelected.set(true)
                        mActivityViewModel.isFatoratiUseCaseSelected.set(false)
                        mActivityViewModel.isQuickRechargeCallForBillOrFatouratie.set(false)
                    }
                    //fatourati Seperate flow for LYDEC now it will call for all companies 5/25/2021
                   else if( mActivityViewModel.isSelectedBillMatchedwithfatouratiSeperateMenuBillNames)
                    {
                        var billCompaniesList = mActivityViewModel.getBillPaymentCompaniesResponseObserver.get()?.bills?.get(groupPosition)?.companies
                        mActivityViewModel.fatoratiTypeSelected.set(Creancier(
                            billCompaniesList!![childPosition].codeCreance,billCompaniesList[childPosition].codeCreancier,
                            billCompaniesList[childPosition].nomCreance,billCompaniesList[childPosition].nomCreancier))
                        mActivityViewModel.requestForFatoratiStepTwoApi(
                            activity,
                            Constants.CURRENT_USER_MSISDN
                        )
                    }
                    else{
                        startFatouratiFlow()
                    }
                }
                return false
            }

        })
    }



    private fun initListner() {
        sheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                /*
                BottomSheetBehavior.STATE_EXPANDED -> "Close Persistent Bottom Sheet"
                BottomSheetBehavior.STATE_COLLAPSED -> "Open Persistent Bottom Sheet"
                else -> "Persistent Bottom Sheet"*/
            }

        })

        //TO Open Bottom Sheet
        /*val state =
            if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_COLLAPSED
            else
                BottomSheetBehavior.STATE_EXPANDED
        sheetBehavior.state = state*/

        mDataBinding.btnCancel.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            mActivityViewModel.isSelectedBillMatchedwithfatouratiSeperateMenuBillNames=false
        }

        mDataBinding.bottomSheetAirTime.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun populateTelecomBillsSubMenusList(companyType:String) {
        /*mTelecomBillSubMenusData.apply {
            add(LanguageData.getStringValue("PostpaidMobile").toString())
            add(LanguageData.getStringValue("PostpaidFix").toString())
            add(LanguageData.getStringValue("Internet").toString())
        }*/

         var telecomBillSubMenusData: ArrayList<String> = arrayListOf()
        if(companyType.toLowerCase().equals(Constants.KEY_FOR_POST_PAID_TELECOM_BILL.toLowerCase()))
        {
            //showing IAM companies
            telecomBillSubMenusData=mTelecomBillSubMenusData
        }
        else if(mActivityViewModel.isSelectedBillMatchedwithfatouratiSeperateMenuBillNames)
        {
            //showing LYDEC company NOM CREANCES
            telecomBillSubMenusData=mActivityViewModel.nomCreancierList
        }
        else{
            //showing INWI companies
            telecomBillSubMenusData=mTelecomBillSubMenusInwiData
        }

        mTelecomBillSubMenusAdapter =
            AirTimeDataAdpater(
                telecomBillSubMenusData,
                object : AirTimeDataAdpater.AirTimeDataClickLisnter {
                    override fun onSelectedAirTimeData(
                        selectedTelecomBillSubMenu: String,
                        position1: Int
                    ) {

                        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        for(b in Constants.iamBillsTriggerFatouratiFlow.indices)
                        {
                            Logger.debugLog("billPayment","iamBillFatoratiList ${Constants.iamBillsTriggerFatouratiFlow[b]}")
                            if(Constants.iamBillsTriggerFatouratiFlow[b].equals(
                                    selectedTelecomBillSubMenu
                                ))
                            {
                                mActivityViewModel.isIamFatouratiSelected=true
                                break
                            }
                        }


                            mActivityViewModel.userSelectedCreancer =
                                selectedTelecomBillSubMenu


                        if (selectedTelecomBillSubMenu.equals(
                                LanguageData.getStringValue("PostpaidMobile")
                            )
                        ) {
                            /*Toast.makeText(
                                activity,
                                "Telecom Bill Mobile Selected",
                                Toast.LENGTH_SHORT
                            ).show()*/

                            //Post Paid MObile
                            mActivityViewModel.billTypeSelected.set(LanguageData.getStringValue("PostpaidMobile"))
                            mActivityViewModel.billTypeSelectedIcon = R.drawable.postpaid_blue
                            mActivityViewModel.isPostPaidMobileSelected.set(true)
                            mActivityViewModel.isPostPaidFixSelected.set(false)
                            mActivityViewModel.isInternetSelected.set(false)

                            (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMain_to_fragmentBillPaymentMsisdn)


                        } else if (selectedTelecomBillSubMenu.equals(
                                LanguageData.getStringValue("PostpaidFix")
                            )
                        ) {
                            /*Toast.makeText(
                                activity,
                                "Telecom Bill Fixe Selected",
                                Toast.LENGTH_SHORT
                            ).show()*/

                            //Post Paid Fixe
                            mActivityViewModel.billTypeSelected.set(LanguageData.getStringValue("PostpaidFix"))
                            mActivityViewModel.billTypeSelectedIcon = R.drawable.postpaid_fix_blue
                            mActivityViewModel.isPostPaidMobileSelected.set(false)
                            mActivityViewModel.isPostPaidFixSelected.set(true)
                            mActivityViewModel.isInternetSelected.set(false)

                            (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMain_to_fragmentBillPaymentMsisdn)


                        } else if (selectedTelecomBillSubMenu.equals(
                                LanguageData.getStringValue("Internet")
                            )
                        ) {
                            /*Toast.makeText(
                                activity,
                                "Telecom Bill Internet Selected",
                                Toast.LENGTH_SHORT
                            ).show()*/

                            //Internet
                            mActivityViewModel.billTypeSelected.set(LanguageData.getStringValue("Internet"))
                            mActivityViewModel.billTypeSelectedIcon = R.drawable.internet_blue
                            mActivityViewModel.isPostPaidMobileSelected.set(false)
                            mActivityViewModel.isPostPaidFixSelected.set(false)
                            mActivityViewModel.isInternetSelected.set(true)

                            (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMain_to_fragmentBillPaymentMsisdn)
                        }else if (mActivityViewModel.isIamFatouratiSelected)
                         {
                            //IAM fatourati selected ....flow of fatourati shuld be call for this IAM bill

                             Logger.debugLog("billPayment","isamBillFatorati ${mActivityViewModel.isIamFatouratiSelected}")

                             startFatouratiFlow()
                        }else if (mActivityViewModel.isSelectedBillMatchedwithfatouratiSeperateMenuBillNames)
                        {
                            //LYDEC now Creance Selected
                            Logger.debugLog("billPayment","lydec ${mActivityViewModel.isSelectedBillMatchedwithfatouratiSeperateMenuBillNames}")
                            mActivityViewModel.selectedCodeCreance=
                                mActivityViewModel.creancesList.get()!![position1].codeCreance
                            startLydecFlow()
                        }

                    }
                })

        mDataBinding.billPaymentSubUseCaseRecycler.apply {
            adapter = mTelecomBillSubMenusAdapter
            layoutManager = LinearLayoutManager(activity as BillPaymentActivity)
        }
    }

    private fun subscribeObserver() {
        mActivityViewModel.getFatoratiStepTwoResponseListner.observe(this@FragmentBillPaymentMain,
            Observer {

                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    mActivityViewModel.setCreancesList(it.creances as ArrayList<creances>)
                    var nomCreancierList:ArrayList<String> = ArrayList()
                    for (i in it.creances .indices)
                    {
                        nomCreancierList.add(it.creances.get(i).nomCreance)
                    }
                    mActivityViewModel.nomCreancierList=nomCreancierList
                    if(it.creances.size>1)
                    {
                        //Show Popup for list of creances

                    val state =
                        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                            BottomSheetBehavior.STATE_COLLAPSED
                        else
                            BottomSheetBehavior.STATE_EXPANDED
                    sheetBehavior.state = state
                    populateTelecomBillsSubMenusList("LYDEC")
                    }
                    else{
                        // call step 3 directly on next screen
                        mActivityViewModel.selectedCodeCreance=
                            it.creances[0].codeCreance
                        startLydecFlow()

                    }
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )
        mActivityViewModel.getFatoratiStepFourResponseListner.observe(this@FragmentBillPaymentMain,
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
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMain_to_fragmentPostPaidBillDetails)
                    }
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )

        mActivityViewModel.getPostPaidResourceInfoResponseListner.observe(this@FragmentBillPaymentMain,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.response.custId != null) {
                        mActivityViewModel.custId = it.response.custId
                    }
                    if (it.response.custname != null) {
                        mActivityViewModel.custname = it.response.custname
                    }
                    mActivityViewModel.totalamount = it.response.totalamount
                    (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMain_to_fragmentPostPaidBillDetails)
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )

        mActivityViewModel.errorText.observe(this@FragmentBillPaymentMain, Observer {
            DialogUtils.showErrorDialoge(activity, it)
        }
        )

        mActivityViewModel.getDeleteFavoritesResponseListner.observe(this@FragmentBillPaymentMain,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    if(!it.contactList.isNullOrEmpty()){
                        Constants.mContactListArray.clear()
                        Constants.mContactListArray.addAll(it.contactList)
                        mDataBinding.billPaymentMangeFavGroup.visibility = View.VISIBLE

                        mFavoritesList.clear()
                        for(contacts in Constants.mContactListArray){
                            var contactName = contacts.contactName
                            if(contactName.contains("Telec_Internet@") || contactName.contains("Telec_PostpaidMobile@") ||
                                contactName.contains("Telec_PostpaidFix@") || contactName.contains("Util_")){
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

        mActivityViewModel.getBillPaymentCompaniesResponseListner.observe(this@FragmentBillPaymentMain, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                if(!it.bills.isNullOrEmpty()){
                    listDataHeader = arrayListOf()
                    var isFatouratiBillEnabled =false
                    var isTelecomBillEnabled =false
                    for(b in Constants.loginWithCertResponse.allowedMenu.BillPayment.indices)
                    {
                        if(Constants.loginWithCertResponse.allowedMenu.BillPayment[b].equals("WaterAndElectricity"))
                        {
                            isFatouratiBillEnabled=true
                        }
                        if(Constants.loginWithCertResponse.allowedMenu.BillPayment[b].equals("Bill"))
                        {
                            isTelecomBillEnabled=true

                        }
                    }
                    Logger.debugLog("billPayment","telecom Constant ${Constants.KEY_FOR_POST_PAID_TELECOM_BILL} ")
                    mTelecomBillSubMenusData.clear()
                    mTelecomBillSubMenusInwiData.clear()
                    for(i in it.bills.indices){
                        if(it.bills[i].name.equals(Constants.KEY_FOR_POST_PAID_TELECOM_BILL)||it.bills[i].name.toLowerCase().equals(Constants.BILLTYPEINWI.toLowerCase())){
                            Logger.debugLog("inwi","inwi ${it.bills[i].name}")
                            if(isTelecomBillEnabled)
                            {
                                var telecomCompanyTypeLogo = it.bills[i].logo
                                if(telecomCompanyTypeLogo.isNullOrEmpty()&&it.bills[i].name.equals(Constants.KEY_FOR_POST_PAID_TELECOM_BILL)){
                                    telecomCompanyTypeLogo = ""
                                    //listDataHeader should be populated by Telecom Company once and we have only one hardcoded Telecom
                                    // company type so list should be empty before adding one
                                        listDataHeader.add(
                                            BillPaymentMenuModel(
                                                LanguageData.getStringValue(
                                                    "BillPaymentTelecomBill"
                                                ).toString(), telecomCompanyTypeLogo
                                            )
                                        )

                                }
                                else if(it.bills[i].name.equals(Constants.KEY_FOR_POST_PAID_TELECOM_BILL)){
                                    //listDataHeader should be populated by Telecom Company once and we have only one hardcoded Telecom
                                    // company type so list should be empty before adding one
                                    listDataHeader.add(BillPaymentMenuModel(LanguageData.getStringValue("BillPaymentTelecomBill").toString(),telecomCompanyTypeLogo))

                                    Logger.debugLog("billPayment", "logo ${telecomCompanyTypeLogo}")
                                }



                            //Adding SubMenu
                                var arrayListOfSubMenu : ArrayList<BillPaymentSubMenuModel> = arrayListOf()
                                for(everyCompany in it.bills.indices)
                                {
                                    if(it.bills[everyCompany].name.equals(Constants.KEY_FOR_POST_PAID_TELECOM_BILL)||it.bills[everyCompany].name.toLowerCase().equals(Constants.BILLTYPEINWI.toLowerCase())){

                                        //picking Logo for IAM Company from one of the IAM companies
                                        var Logo=""
                                                Logo=it.bills[everyCompany].companies[0].logo
                                        if(Logo.isNullOrEmpty())
                                        {
                                            Logo=""
                                        }
                                        arrayListOfSubMenu.add(BillPaymentSubMenuModel(it.bills[everyCompany].name,Logo))
                                    }
                                }
                            listDataChild?.put(LanguageData.getStringValue("BillPaymentTelecomBill").toString(),arrayListOfSubMenu)


                            for(companyIndex in it.bills[i].companies.indices){
                                if(it.bills[i].name.equals(Constants.KEY_FOR_POST_PAID_TELECOM_BILL)){
                                        mTelecomBillSubMenusData.add(LanguageData.getStringValue(it.bills[i].companies[companyIndex].nomCreancier).toString())
                                }else{
                                    mTelecomBillSubMenusInwiData.add(LanguageData.getStringValue(it.bills[i].companies[companyIndex].nomCreancier).toString())
                                }


}
                            }
                            Logger.debugLog("inwi","mTelecomBillSubMenusData ${mTelecomBillSubMenusData.toString()}")
                        }else{
                            if(isFatouratiBillEnabled){
                                var companyTypeLogo = it.bills[i].logo

                                if(companyTypeLogo.isNullOrEmpty()){
                                    companyTypeLogo = ""
                                    listDataHeader.add(BillPaymentMenuModel(it.bills[i].name,companyTypeLogo))
                                }else{
                                    listDataHeader.add(BillPaymentMenuModel(it.bills[i].name,companyTypeLogo))
                                    Logger.debugLog("billPayment","logo ${companyTypeLogo}")
                                }

                            Logger.debugLog("bill"," ${it.bills[i].name}")
                            //Addding Sub Menu's
                            var arrayListOfSubMenu : ArrayList<BillPaymentSubMenuModel> = arrayListOf()
                            for(companyIndex in it.bills[i].companies.indices){
                                var logo = ""
                                if(it.bills[i].companies[companyIndex].logo.isNullOrEmpty()){
                                    logo = ""
                                }else{
                                    logo = it.bills[i].companies[companyIndex].logo
                                }
                                arrayListOfSubMenu.add(BillPaymentSubMenuModel(it.bills[i].companies[companyIndex].nomCreancier,logo))
                            }
                            listDataChild?.put(it.bills[i].name,arrayListOfSubMenu)
                            }
                        }
                    }
                    Logger.debugLog("inwi","inwi ${listDataChild.toString()}")
                    prepareDataForBillPayment()
                    populateTelecomBillsSubMenusList(Constants.KEY_FOR_POST_PAID_TELECOM_BILL)
                }

//                listDataChild?.put(listDataHeader?.get(0)?.companyTilte!!, myTelecomBillSubMenus)
                /*listDataHeader = arrayListOf()
                listDataHeader.apply {
                    this!!.add(BillPaymentMenuModel("Telecom Bill Payment",R.drawable.telecom_bill_updated_icon))
                    this!!.add(BillPaymentMenuModel("Water And Electricity",R.drawable.water_electricity_update_icon))
                }*/
            }else{
                DialogUtils.showErrorDialoge(activity,it.description)
            }
        })
    }
    private fun startFatouratiFlow() {
        mActivityViewModel.isBillUseCaseSelected.set(false)
        mActivityViewModel.isFatoratiUseCaseSelected.set(true)
        mActivityViewModel.isQuickRechargeCallForBillOrFatouratie.set(false)
        Logger.debugLog("BillPaymentTesting","else expand sheet")
        var selectedCreancer = mActivityViewModel?.userSelectedCreancer
        mActivityViewModel.selectedCreancer.set(selectedCreancer)
        if(!mActivityViewModel.getBillPaymentCompaniesResponseObserver.get()?.bills.isNullOrEmpty()){
            var billList = mActivityViewModel.getBillPaymentCompaniesResponseObserver.get()?.bills
            Logger.debugLog("BillPaymentTesting","else expand sheet1")
            for(i in billList!!.indices){
                Logger.debugLog("billsList","billsList ${billList[i].name} } ")
                if(!billList[i].name.equals(Constants.KEY_FOR_POST_PAID_TELECOM_BILL)){
                    var billCompaniesList = mActivityViewModel.getBillPaymentCompaniesResponseObserver.get()?.bills?.get(i)?.companies
                    for(j in billCompaniesList?.indices!!){

                        if(selectedCreancer?.equals(billCompaniesList[j].nomCreancier)!!){
                            Logger.debugLog("BillPaymentTesting","else expand sheet2")
                            mActivityViewModel.fatoratiTypeSelected.set(Creancier(billCompaniesList[j].codeCreance,billCompaniesList[j].codeCreancier,
                                billCompaniesList[j].nomCreance,billCompaniesList[j].nomCreancier))
                            Logger.debugLog("BillPaymentTesting",mActivityViewModel.fatoratiTypeSelected.get().toString())
//                                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMain_to_fragmentBillPaymentMsisdn)
                            (activity as BillPaymentActivity).navController?.navigateUp()
                            (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMain_to_fragmentBillPaymentMsisdn)
                        break
                        }
                    }
                }
                else {
                    if(mActivityViewModel.isIamFatouratiSelected)
                    {
                        mActivityViewModel.isIamFatouratiSelected=false
                        Logger.debugLog("BillPaymentTesting","Selected Creance ${mActivityViewModel.userSelectedCreancer}")
                        var billCompaniesList = mActivityViewModel.getBillPaymentCompaniesResponseObserver.get()?.bills?.get(i)?.companies
                        for(j in billCompaniesList?.indices!!){

                            if(selectedCreancer?.equals(billCompaniesList[j].nomCreancier)!!){

                                mActivityViewModel.fatoratiTypeSelected.set(Creancier(billCompaniesList[j].codeCreance,billCompaniesList[j].codeCreancier,
                                    billCompaniesList[j].nomCreance,billCompaniesList[j].nomCreancier))
                                Logger.debugLog("BillPaymentTesting",mActivityViewModel.fatoratiTypeSelected.get().toString())
//                                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMain_to_fragmentBillPaymentMsisdn)
                                (activity as BillPaymentActivity).navController?.navigateUp()
                                (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMain_to_fragmentBillPaymentMsisdn)
                                break
                            }
                        }
                    }
                }
            }
        }
    }
    private fun startLydecFlow() {
        mActivityViewModel.isBillUseCaseSelected.set(false)
        mActivityViewModel.isFatoratiUseCaseSelected.set(true)
        mActivityViewModel.isQuickRechargeCallForBillOrFatouratie.set(false)
        Logger.debugLog("BillPaymentTesting","else expand sheet")
        var selectedCreancer = mActivityViewModel?.userSelectedCreancer
        mActivityViewModel.selectedCreancer.set(selectedCreancer)
        if(!mActivityViewModel.getBillPaymentCompaniesResponseObserver.get()?.bills.isNullOrEmpty()){
            var billList = mActivityViewModel.getBillPaymentCompaniesResponseObserver.get()?.bills
            Logger.debugLog("BillPaymentTesting","else expand sheet1")


//                                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMain_to_fragmentBillPaymentMsisdn)
                (activity as BillPaymentActivity).navController?.navigateUp()
                (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMain_to_fragmentBillPaymentMsisdn)

        }
    }
}