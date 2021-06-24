package com.es.marocapp.usecase.billpayment.fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.FatoratiParamsItemAdapter
import com.es.marocapp.adapter.LanguageCustomSpinnerAdapter
import com.es.marocapp.databinding.FragmentBillPaymentMsisdnBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.*
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.usecase.billpayment.BillPaymentClickListner
import com.es.marocapp.usecase.billpayment.BillPaymentViewModel
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger
import kotlinx.android.synthetic.main.layout_activity_header.view.*
import java.util.regex.Pattern


class FragmentBillPaymentMsisdn : BaseFragment<FragmentBillPaymentMsisdnBinding>(),
    BillPaymentClickListner, AdapterView.OnItemSelectedListener, TextWatcher {

    private lateinit var mFatoratiParamsItemAdapter: FatoratiParamsItemAdapter
    private lateinit var mActivityViewModel: BillPaymentViewModel

    private var list_of_favorites = arrayListOf<String>()

    var msisdnEntered = ""
    var code = ""
    var isNumberRegexMatches = false
    var isCodeRegexMatches = false
    var applyValidation = false
    var cilLabel = ""

    lateinit var acountTypeSpinnerAdapter: LanguageCustomSpinnerAdapter


    override fun setLayout(): Int {
        return R.layout.fragment_bill_payment_msisdn
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(activity as BillPaymentActivity).get(BillPaymentViewModel::class.java)
        mDataBinding.apply {
            listner = this@FragmentBillPaymentMsisdn
            viewmodel = mActivityViewModel
        }

        (activity as BillPaymentActivity).setHeaderTitle(
            LanguageData.getStringValue("BillPayment").toString()
        )

        mActivityViewModel.isUserSelectedFromFavorites.set(false)

        list_of_favorites.clear()
        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            mDataBinding.inputPhoneNumber.setInputType(InputType.TYPE_CLASS_TEXT)
            val selectedFatorati =
                "Util_${mActivityViewModel.fatoratiTypeSelected.get()!!.nomCreancier}"
            for (contacts in Constants.mContactListArray) {
                var contactName = contacts.contactname
                if (contactName.contains(selectedFatorati)) {
                    contactName = contactName.substringAfter("@")
                    contactName = contactName.substringBefore(",")
                    list_of_favorites.add(contactName)
                }
            }
        }

        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            mDataBinding.inputPhoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER)
            var selectedBillType = ""
            if(mActivityViewModel.isInternetSelected.get()!!){
                selectedBillType = "Telec_Internet@"

            }else if(mActivityViewModel.isPostPaidMobileSelected.get()!!){
                selectedBillType = "Telec_PostpaidMobile@"
            }else if(mActivityViewModel.isPostPaidFixSelected.get()!!){
                selectedBillType = "Telec_PostpaidFix@"
            }
            for (contacts in Constants.mContactListArray) {
                val contactName = contacts.contactname
                val contactNameWithoutPrefix = contactName.substringAfter("@")
                val contactNameWithoutPostfix = contactNameWithoutPrefix.substringBefore(",")

                var contactNumber = contacts.customerreference
                if (contactName.contains(selectedBillType)) {
                    contactNumber = contactNumber.substringBefore("@")
                    contactNumber = contactNumber.substringBefore("/")
                    if(contactName.contains(Constants.APP_MSISDN_PREFIX)){
                        contactNumber = contactNumber.removePrefix(Constants.APP_MSISDN_PREFIX)
                        contactNumber = "0$contactNumber"
                    }
                    //todo also here remove lenght-2 check in max line
//                    if (contactNumber.length.equals(Constants.APP_MSISDN_LENGTH.toInt() - 2)) {
                        val name_number_favorite = "$contactNameWithoutPostfix-$contactNumber"
                        list_of_favorites.add(name_number_favorite)
//                    }
                }else{
                    contactNumber = contactNumber.substringBefore("@")
                    contactNumber = contactNumber.substringBefore("/")
                    if(contactNumber.length.equals(Constants.APP_MSISDN_LENGTH.toInt() - 2)){
                        val name_number_favorite = "$contactNameWithoutPostfix-$contactNumber"
                        list_of_favorites.add(name_number_favorite)
                    }
                }
            }
        }

        list_of_favorites.add(0, LanguageData.getStringValue("SelectFavorite").toString())

        val adapterFavoriteType = ArrayAdapter<CharSequence>(
            activity as BillPaymentActivity, R.layout.layout_favorites_spinner_text,
            list_of_favorites as List<CharSequence>
        )
        mDataBinding.spinnerSelectFavorites.apply {
            adapter = adapterFavoriteType
        }
        mDataBinding.spinnerSelectFavorites.onItemSelectedListener = this@FragmentBillPaymentMsisdn
        (activity as BillPaymentActivity).setHeaderVisibility(true)
        (activity as BillPaymentActivity).setCompanyIconToolbarVisibility(true)

        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            (activity as BillPaymentActivity).mDataBinding.headerBillPayment.rootView.tv_company_title.text =
                mActivityViewModel.billTypeSelected.get()!!
            (activity as BillPaymentActivity).mDataBinding.headerBillPayment.rootView.img_company_icons.setImageResource(
                mActivityViewModel.billTypeSelectedIcon
            )

            if (mActivityViewModel.isInternetSelected.get()!!) {
                mDataBinding.inputLayoutCode.visibility = View.GONE
            } else if(Constants.IS_AGENT_USER){
                mDataBinding.inputLayoutCode.visibility = View.GONE
            }
            else {
                mDataBinding.inputLayoutCode.visibility = View.VISIBLE
            }

            (activity as BillPaymentActivity).setLetterIconVisible(false, "")

            //todo also here remove lenght-2 check in max line
            mDataBinding.inputPhoneNumber.filters = arrayOf<InputFilter>(
                InputFilter.LengthFilter(
                    Constants.APP_MSISDN_LENGTH.toInt() - 2
                )
            )

            mDataBinding.inputPhoneNumber.inputType = InputType.TYPE_CLASS_PHONE

        }
        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            (activity as BillPaymentActivity).mDataBinding.headerBillPayment.rootView.tv_company_title.text =
                mActivityViewModel.fatoratiTypeSelected.get()!!.nomCreancier

            mDataBinding.inputLayoutCode.visibility = View.GONE

            (activity as BillPaymentActivity).setLetterIconVisible(
                true,
                mActivityViewModel.fatoratiTypeSelected.get()!!.nomCreancier[0].toString()
            )

            mDataBinding.inputPhoneNumber.filters =
                arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.APP_CIL_LENGTH.toInt()))

            mDataBinding.inputPhoneNumber.inputType = InputType.TYPE_CLASS_TEXT

        }

        mActivityViewModel.popBackStackTo = R.id.fragmentBillPaymentMain
        mDataBinding.inputPhoneNumber.addTextChangedListener(this)
      //  mDataBinding.inputCode.setText("")
     // mDataBinding.inputCode.visibility=View.GONE
          mDataBinding.inputCode.addTextChangedListener(this)

        mDataBinding.inputPhoneNumber.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setInputLayoutHint()
            } else {
                if (mDataBinding.inputLayoutPhoneNumber.isErrorEnabled) {
                    setInputLayoutHint()
                } else {
                    if (mDataBinding.inputPhoneNumber.text.isEmpty()) {
                        mDataBinding.inputPhoneNumberHint.visibility = View.VISIBLE
                        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                            mDataBinding.inputLayoutPhoneNumber.hint =
                                LanguageData.getStringValue("EnterCilNumber")
                            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                        }
                        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
                            if (mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!) {
                                mDataBinding.inputLayoutPhoneNumber.hint =
                                    LanguageData.getStringValue("MSISDNPlaceholder")
                                mDataBinding.inputPhoneNumberHint.text =
                                    LanguageData.getStringValue("PhoneNumber")
                            } else if (mActivityViewModel.isInternetSelected.get()!!) {
                                mDataBinding.inputLayoutPhoneNumber.hint =
                                    LanguageData.getStringValue("MSISDNPlaceholder")
                                mDataBinding.inputPhoneNumberHint.text =
                                    LanguageData.getStringValue("EnterPaymentIdentifier")
                            }
                        }
                    } else {
                        setInputLayoutHint()
                    }
                }
            }
        }
        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
//commented LYDEC flow
//            var isSelectedBillMatchedwithfatouratiSeperateMenuBillNames: Boolean = false
//            for (i in Constants.fatouratiSeperateMenuBillNames.indices) {
//
//                isSelectedBillMatchedwithfatouratiSeperateMenuBillNames =
//                    mActivityViewModel.selectedCreancer.get()?.trim()?.toLowerCase().equals(Constants.fatouratiSeperateMenuBillNames[i]?.trim()?.toLowerCase())
//                Logger.debugLog("Abro","${mActivityViewModel.selectedCreancer.get()?.trim()?.toLowerCase()} == ${Constants.fatouratiSeperateMenuBillNames[i]?.trim()?.toLowerCase()}")
//                if(isSelectedBillMatchedwithfatouratiSeperateMenuBillNames)
//                {
//                    break
//                }
//            }
//
            //fatouratiSeperateMenuBillNames
            if(mActivityViewModel.isSelectedBillMatchedwithfatouratiSeperateMenuBillNames)
            {
                mActivityViewModel.stepFourLydecSelected=true
                mActivityViewModel.isSelectedBillMatchedwithfatouratiSeperateMenuBillNames=false
                mActivityViewModel.requestForFatoratiStepThreeApi(   activity,
                    Constants.CURRENT_USER_MSISDN,mActivityViewModel.selectedCodeCreance
                )
            }
            else{
                mActivityViewModel.isSelectedBillMatchedwithfatouratiSeperateMenuBillNames=false
                mActivityViewModel.requestForFatoratiStepTwoThreeApi(
                    activity,
                    Constants.CURRENT_USER_MSISDN
                )
            }

        }





        setStrings()
        subscribeForSpinnerListner()
        subscribeObserver()
    }

    fun setInputLayoutHint() {
        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            mDataBinding.inputLayoutPhoneNumber.hint = cilLabel
        }
        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            if (mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!) {
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("PhoneNumber")
            } else if (mActivityViewModel.isInternetSelected.get()!!) {
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("EnterPaymentIdentifier")
            }
        }
    }

    private fun setStrings() {
        mDataBinding.inputLayoutCode.hint = LanguageData.getStringValue("FidelioCode")
        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("EnterCilNumber")
            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
        }
        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            if (mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!) {
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("MSISDNPlaceholder")
                mDataBinding.inputPhoneNumberHint.text =
                    LanguageData.getStringValue("PhoneNumber")
            } else if (mActivityViewModel.isInternetSelected.get()!!) {
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("MSISDNPlaceholder")
                mDataBinding.inputPhoneNumberHint.text =
                    LanguageData.getStringValue("EnterPaymentIdentifier")
            }
        }
        mDataBinding.selectFavoriteTypeTitle.hint = LanguageData.getStringValue("SelectFavorite")
        mDataBinding.btnNext.text = LanguageData.getStringValue("Submit")
    }

    private fun subscribeObserver() {
        mActivityViewModel.errorText.observe(this@FragmentBillPaymentMsisdn, Observer {
            DialogUtils.showErrorDialoge(activity, it)
        }
        )

        mActivityViewModel.getPostPaidResourceInfoResponseListner.observe(this@FragmentBillPaymentMsisdn,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.response.custId != null) {
                        mActivityViewModel.custId = it.response.custId
                    }
                    if (it.response.custname != null) {
                        mActivityViewModel.custname = it.response.custname
                    }
                    mActivityViewModel.totalamount = it.response.totalamount
                    (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMsisdn_to_fragmentPostPaidBillDetails)
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )

        mActivityViewModel.getFatoratiStepTwoResponseListner.observe(this@FragmentBillPaymentMsisdn,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                       mDataBinding.acountTypeSpinner.visibility=View.VISIBLE
                        mActivityViewModel.setCreancesList(it.creances as ArrayList<creances>)
                        val nomCreancierList:ArrayList<String> = ArrayList()
                        for (i in it.creances .indices)
                        {
                            nomCreancierList.add(it.creances.get(i).nomCreance)
                        }
                        val acountTypeArray: Array<String> =
                            nomCreancierList.toArray(arrayOfNulls<String>(nomCreancierList.size))
                        acountTypeSpinnerAdapter =
                            LanguageCustomSpinnerAdapter(
                                activity as BillPaymentActivity,
                                acountTypeArray,
                                (activity as BillPaymentActivity).resources.getColor(R.color.colorBlack),true
                            )
                        //  mDataBinding.acountTypeSpinner
                        mDataBinding.acountTypeSpinner.apply {
                            adapter = acountTypeSpinnerAdapter
                        }

                        Log.d("paiment" ,"${mActivityViewModel.creancesList.get()
                            ?.get(0)?.nomCreance}")
                        var hintLable= mActivityViewModel.creancesList.get()
                            ?.get(0)?.nomCreance?.toLowerCase()?.trim()?.replace("paiement par","")?.trim()
                        hintLable=hintLable?.replace("de","")
                        mDataBinding.billTypeInputLayout.visibility=View.VISIBLE
                        mDataBinding.billTypeInput.isEnabled=false
                        mDataBinding.billTypeInputLayout.hint = hintLable
                        mDataBinding.billTypeInput.setText( mActivityViewModel.creancesList.get()
                            ?.get(0)?.codeCreance.toString())
                        mDataBinding.selectAcountTitile.setText(LanguageData.getStringValue("BillType"))
                        mDataBinding.selectAcountTitile.visibility=View.VISIBLE
                    }

                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )

        mActivityViewModel.getFatoratiStepThreeResponseListner.observe(this@FragmentBillPaymentMsisdn,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    mActivityViewModel.specialMenuBillSelected=false
               //   mDataBinding.acountTypeSpinner.visibility=View.GONE
               //     mDataBinding.selectAcountTitile.visibility=View.GONE
                  //  cilLabel = it.param.libelle
                    if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                        mActivityViewModel.validatedParams.clear()
                        mActivityViewModel.recievedParams.clear()
                        mActivityViewModel.demoParams.clear()
                        mDataBinding.inputLayoutPhoneNumber.visibility=View.GONE
                        mDataBinding.inputPhoneNumberHint.visibility=View.GONE
                        mDataBinding.mFieldsRecycler.visibility=View.VISIBLE
                        for(i in it.params.indices){
                             val validatedParams = ValidatedParam("",it.params[i].nomChamp)
                            mActivityViewModel.validatedParams.add(validatedParams)
                            mActivityViewModel.recievedParams.add(RecievededParam(it.params[i].libelle,it.params[i].nomChamp,it.params[i].typeChamp,"",
                                false,View.VISIBLE,"",it.params[i].listVals,"",""))
                            //using for lable value restoring after error of invalid input
                            mActivityViewModel.demoParams.add(RecievededParam(it.params[i].libelle,it.params[i].nomChamp,it.params[i].typeChamp,"",
                                false,View.VISIBLE,"",it.params[i].listVals,"",""))
                        }
                        mFatoratiParamsItemAdapter = FatoratiParamsItemAdapter(activity ,mActivityViewModel.recievedParams,object :FatoratiParamsItemAdapter.ParamTextChangedListner{
                            override fun onParamTextChangedClick(valChamp: String, position: Int) {
                                Logger.debugLog("textChengeLListner","Value = ${valChamp}  == position = ${position}")
                                if (position <= it.params.size - 1) {
                                    if (it.params[position].libelle.equals("CIL", false)) {
                                        applyValidation = true
                                    } else {
                                        applyValidation = false
                                    }


//                                val editedText = valChamp
//                                val oldText = mActivityViewModel.validatedParams[position].valChamp
//                                if (editedText.contains(oldText)) {
                                    val typeChamp =
                                        mActivityViewModel.recievedParams[position].typeChamp
                                    val nomChamp =
                                        mActivityViewModel.recievedParams[position].nomChamp
                                    val listVals =
                                        mActivityViewModel.recievedParams[position].listVals

                                    //using different list value to restore correct value after invalid input error
                                    val lablei = mActivityViewModel.demoParams[position].libelle

                                    mActivityViewModel.recievedParams.set(
                                        position,
                                        RecievededParam(
                                            lablei, nomChamp, typeChamp, "",
                                            false, View.VISIBLE, valChamp, listVals,"",""
                                        )
                                    )


                                    mActivityViewModel.validatedParams.set(
                                        position,
                                        ValidatedParam(
                                            valChamp,
                                            mActivityViewModel.recievedParams[position].nomChamp
                                        )
                                    )
                            //    }
                            }
                            }

                            override fun onTsavTextChangedClick(
                                firstVal: String,
                                secondVal: String,
                                spinnerVal: String,
                                position: Int
                            ) {
                                Logger.debugLog("textChengeLListner","Value = ${firstVal+secondVal}  == position = ${position}")
                                val typeChamp=mActivityViewModel.recievedParams[position].typeChamp
                                val nomChamp=mActivityViewModel.recievedParams[position].nomChamp
                                val listVals=mActivityViewModel.recievedParams[position].listVals
                                var convertesSpinnerValue= Constants.convertSpinnerArabicValue(spinnerVal)
                                val valChamp=firstVal.plus(convertesSpinnerValue.plus(secondVal))

                                //using different list value to restore correct value after invalid input error
                                val lablei=mActivityViewModel.demoParams[position].libelle

                                mActivityViewModel.recievedParams.set(position,RecievededParam(lablei,nomChamp,typeChamp,"",
                                    false,View.VISIBLE,"",listVals,firstVal,secondVal))

                                mActivityViewModel.validatedParams.set(
                                    position,
                                    ValidatedParam(
                                        valChamp,
                                        mActivityViewModel.recievedParams[position].nomChamp
                                    )
                                )
                            }

                            override fun onSpinnerTextChangedClick(valChamp: String, position: Int) {
                             if(position<=it.params.size-1){
                                    if(it.params[position].libelle.equals("CIL",false)){
                                        applyValidation = true
                                    } else {
                                        applyValidation = false
                                    }

                                 Logger.debugLog("lydec","position  ${position} == ${mActivityViewModel.validatedParams.toString()}==${valChamp}")

                                    val typeChamp=mActivityViewModel.recievedParams[position].typeChamp
                                    val nomChamp=mActivityViewModel.recievedParams[position].nomChamp
                                    val listVals=mActivityViewModel.recievedParams[position].listVals

                                    //using different list value to restore correct value after invalid input error
                                    val lablei=mActivityViewModel.demoParams[position].libelle

                                    mActivityViewModel.recievedParams.set(
                                        position,RecievededParam(lablei,nomChamp,typeChamp,"",
                                        false,View.VISIBLE,valChamp,listVals,"",""))


                                    mActivityViewModel.validatedParams.set(
                                        position,
                                        ValidatedParam(
                                            valChamp,
                                            mActivityViewModel.recievedParams[position].nomChamp
                                        )
                                    )
                             }
                            }

                            override fun onTsavSpinnerTextChangedClick(
                                firstVal: String,
                                secondVal: String,
                                spinnerVal: String,
                                position: Int
                            ) {
                                Logger.debugLog("textChengeLListner","Value = ${firstVal+spinnerVal+secondVal}  == position = ${position}")

                                val typeChamp=mActivityViewModel.recievedParams[position].typeChamp
                                val nomChamp=mActivityViewModel.recievedParams[position].nomChamp
                                val listVals=mActivityViewModel.recievedParams[position].listVals
                                var convertedSpinnerValue= Constants.convertSpinnerArabicValue(spinnerVal)
                                val valChamp=firstVal.plus(convertedSpinnerValue.plus(secondVal))
                                //  Constants.selectedTSAVSpinnerPosition=position
                                //using different list value to restore correct value after invalid input error
                                val lablei=mActivityViewModel.demoParams[position].libelle

                                mActivityViewModel.recievedParams.set(position,RecievededParam(lablei,nomChamp,typeChamp,"",
                                    false,View.VISIBLE,"",listVals,firstVal,secondVal))

                                mActivityViewModel.validatedParams.set(
                                    position,
                                    ValidatedParam(
                                        valChamp,
                                        mActivityViewModel.recievedParams[position].nomChamp
                                    )
                                )
                            }
                        })
                        mDataBinding.mFieldsRecycler.apply {
                                adapter = mFatoratiParamsItemAdapter
                            layoutManager = LinearLayoutManager(activity)
                        }
                    }

                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )

        mActivityViewModel.getFatoratiStepTwothreeResponseListner.observe(this@FragmentBillPaymentMsisdn,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    mActivityViewModel.specialMenuBillSelected=false
                    mDataBinding.inputPhoneNumber.isEnabled=true
                 //   mDataBinding.acountTypeSpinner.visibility=View.GONE
                 //   mDataBinding.selectAcountTitile.visibility=View.GONE
                 //   cilLabel = it.param.libelle
                    if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                        if(it.params!=null)
                        {
                        mActivityViewModel.validatedParams.clear()
                        mActivityViewModel.recievedParams.clear()
                        mActivityViewModel.demoParams.clear()
                        mDataBinding.inputLayoutPhoneNumber.visibility=View.GONE
                        mDataBinding.inputPhoneNumberHint.visibility=View.GONE
                        mDataBinding.mFieldsRecycler.visibility=View.VISIBLE

                        for(i in it.params.indices){
                            var listVals:List<String> = ArrayList()
                            if(!it.params[i].listVals.isNullOrEmpty())
                            {
                                listVals=it.params[i].listVals
                            }
                            Constants.STEP2_3RESPONSE=it
                            val validatedParams = ValidatedParam("",it.params[i].nomChamp)
                            mActivityViewModel.validatedParams.add(validatedParams)
                            mActivityViewModel.recievedParams.add(RecievededParam(it.params[i].libelle,it.params[i].nomChamp,it.params[i].typeChamp,"",
                            false,View.VISIBLE,"",listVals,"",""))

                            //using for lable value restoring after error of invalid input
                            mActivityViewModel.demoParams.add(RecievededParam(it.params[i].libelle,it.params[i].nomChamp,it.params[i].typeChamp,"",
                                false,View.VISIBLE,"",listVals,"",""))
                        }

                        mFatoratiParamsItemAdapter = FatoratiParamsItemAdapter(
                            activity,
                            mActivityViewModel.recievedParams,
                            object :FatoratiParamsItemAdapter.ParamTextChangedListner{
                                override fun onParamTextChangedClick(valChamp: String, position: Int) {
                                   Logger.debugLog("textChengeLListner","Value = ${valChamp}  == position = ${position}")
                                    if(position<=it.params.size-1){
                                        if(it.params[position].libelle.equals("CIL",false)){
                                            applyValidation = true
                                        } else {
                                            applyValidation = false
                                        }



//                                    val editedText=valChamp
//                                    val oldText= mActivityViewModel.validatedParams[position].valChamp
//                                    if(editedText.contains(oldText)) {
                                        val typeChamp=mActivityViewModel.recievedParams[position].typeChamp
                                        val nomChamp=mActivityViewModel.recievedParams[position].nomChamp
                                        val listVals=mActivityViewModel.recievedParams[position].listVals

                                        //using different list value to restore correct value after invalid input error
                                        val lablei=mActivityViewModel.demoParams[position].libelle

                                        mActivityViewModel.recievedParams.set(position,RecievededParam(lablei,nomChamp,typeChamp,"",
                                            false,View.VISIBLE,valChamp,listVals,"",""))

                                        mActivityViewModel.validatedParams.set(
                                            position,
                                            ValidatedParam(
                                                valChamp,
                                                mActivityViewModel.recievedParams[position].nomChamp
                                            )
                                        )

                                //    }
                                    }
                                }

                                override fun onTsavTextChangedClick(
                                    firstVal: String,
                                    secondVal: String,
                                    spinnerVal: String,
                                    position: Int
                                ) {
                                    Logger.debugLog("textChengeLListner","Value = ${firstVal+secondVal}  == position = ${position}")
                                    val typeChamp=mActivityViewModel.recievedParams[position].typeChamp
                                    val nomChamp=mActivityViewModel.recievedParams[position].nomChamp
                                    val listVals=mActivityViewModel.recievedParams[position].listVals
                                    var convertesSpinnerValue= Constants.convertSpinnerArabicValue(spinnerVal)
                                    val valChamp=firstVal.plus(convertesSpinnerValue.plus(secondVal))

                                    //using different list value to restore correct value after invalid input error
                                    val lablei=mActivityViewModel.demoParams[position].libelle

                                    mActivityViewModel.recievedParams.set(position,RecievededParam(lablei,nomChamp,typeChamp,"",
                                        false,View.VISIBLE,"",listVals,firstVal,secondVal))

                                    mActivityViewModel.validatedParams.set(
                                        position,
                                        ValidatedParam(
                                            valChamp,
                                            mActivityViewModel.recievedParams[position].nomChamp
                                        )
                                    )
                                }

                                override fun onSpinnerTextChangedClick(valChamp: String, position: Int) {
                                    if (position <= it.params.size - 1) {
                                        if (it.params[position].libelle.equals("CIL", false)) {
                                            applyValidation = true
                                        } else {
                                            applyValidation = false
                                        }

                                        val typeChamp =
                                            mActivityViewModel.recievedParams[position].typeChamp
                                        val nomChamp =
                                            mActivityViewModel.recievedParams[position].nomChamp
                                        val listVals =
                                            mActivityViewModel.recievedParams[position].listVals

                                        //using different list value to restore correct value after invalid input error
                                        val lablei = mActivityViewModel.demoParams[position].libelle

                                        mActivityViewModel.recievedParams.set(
                                            position,
                                            RecievededParam(
                                                lablei, nomChamp, typeChamp, "",
                                                false, View.VISIBLE, valChamp, listVals,"",""
                                            )
                                        )


                                        mActivityViewModel.validatedParams.set(
                                            position,
                                            ValidatedParam(
                                                valChamp,
                                                mActivityViewModel.recievedParams[position].nomChamp
                                            )
                                        )
                                }
                                }

                                override fun onTsavSpinnerTextChangedClick(
                                    firstVal: String,
                                    secondVal: String,
                                    spinnerVal: String,
                                    position: Int
                                ) {
                                    Logger.debugLog("textChengeLListner","Value = ${firstVal+spinnerVal+secondVal}  == position = ${position}")

                                    val typeChamp=mActivityViewModel.recievedParams[position].typeChamp
                                    val nomChamp=mActivityViewModel.recievedParams[position].nomChamp
                                    val listVals=mActivityViewModel.recievedParams[position].listVals
                                    var convertedSpinnerValue= Constants.convertSpinnerArabicValue(spinnerVal)
                                    val valChamp=firstVal.plus(convertedSpinnerValue.plus(secondVal))
                                  //  Constants.selectedTSAVSpinnerPosition=position
                                    //using different list value to restore correct value after invalid input error
                                    val lablei=mActivityViewModel.demoParams[position].libelle

                                    mActivityViewModel.recievedParams.set(position,RecievededParam(lablei,nomChamp,typeChamp,"",
                                        false,View.VISIBLE,"",listVals,firstVal,secondVal))

                                    mActivityViewModel.validatedParams.set(
                                        position,
                                        ValidatedParam(
                                            valChamp,
                                            mActivityViewModel.recievedParams[position].nomChamp
                                        )
                                    )
                                }
                            }
                        )
                        mDataBinding.mFieldsRecycler.apply {
                            adapter = mFatoratiParamsItemAdapter
                            layoutManager = LinearLayoutManager(activity)
                        }
                      }
                    }
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )

        mActivityViewModel.getFatoratiStepFourResponseListner.observe(this@FragmentBillPaymentMsisdn,
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
                        (activity as BillPaymentActivity).navController.navigate(R.id.action_fragmentBillPaymentMsisdn_to_fragmentPostPaidBillDetails)
                    }
                } else {
                    DialogUtils.showErrorDialoge(activity, it.description)
                }
            }
        )
    }

    private fun subscribeForSpinnerListner() {

        // homeViewModel.requestForGetTransactionHistoryApi(activity,Constants.CURRENT_USER_MSISDN)
        mDataBinding.acountTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mDataBinding.billTypeInput.isEnabled=false
               mDataBinding.billTypeInputLayout.hint = mActivityViewModel.creancesList.get()
                    ?.get(position)?.nomCreance
                mDataBinding.billTypeInput.setText( mActivityViewModel.creancesList.get()
                    ?.get(position)?.codeCreance.toString())
                mDataBinding.billTypeInputLayout.visibility=View.VISIBLE
                mActivityViewModel.specialMenuBillSelected=true
                mActivityViewModel.requestForFatoratiStepThreeApi(   activity,
                    Constants.CURRENT_USER_MSISDN,mDataBinding.billTypeInput.text.toString()

                )

            Logger.debugLog("Abro","${mActivityViewModel.creancesList.get()
                ?.get(position)?.nomCreance}  and  ${mActivityViewModel.creancesList.get()
                ?.get(position)?.codeCreance}  selection ${mActivityViewModel.specialMenuBillSelected}  ")
            }
        }
    }
    override fun onSubmitClickListner(view: View) {
        if(mActivityViewModel.specialMenuBillSelected)
        {
            mActivityViewModel.requestForFatoratiStepThreeApi(   activity,
                Constants.CURRENT_USER_MSISDN,mDataBinding.inputPhoneNumber.text.toString()

            )
        } else {
            if (isValidForAll()) {
                if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
                    mActivityViewModel.requestForPostPaidFinancialResourceInfoApi(
                        activity,
                        code,
                        msisdnEntered
                    )
                }

                if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {

//                    Logger.debugLog("billPayment","${mActivityViewModel?.fatoratiTypeSelected?.get()?.codeCreance}," +
//                            "${mActivityViewModel?.fatoratiTypeSelected?.get()?.codeCreancier}," +
//                            "${mActivityViewModel?.fatoratiStepThreeObserver?.get()?.param?.nomChamp}," +
//                            "${mActivityViewModel. fatoratiStepThreeObserver?.get()?.param?.nomChamp},${Constants.OPERATION_TYPE_IMPAYES}," +
//                            "${Constants.getFatoratiAlias(mActivityViewModel?.transferdAmountTo)}," +
//                            "${mActivityViewModel?.fatoratiStepThreeObserver?.get()?.refTxFatourati},${Constants.getNumberMsisdn(Constants.CURRENT_USER_MSISDN)}")
                    mActivityViewModel.transferdAmountTo = msisdnEntered
                    mActivityViewModel.requestForFatoratiStepFourApi(activity)
                }
            }
        }
    }

    override fun onBackClickListner(view: View) {

    }

    private fun isValidForAll(): Boolean {
        var isValidForAll = true
        //todo NUmber Lenght is Pending
        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            //todo NUmber Lenght is Pending
            if (mActivityViewModel.isPostPaidFixSelected.get()!! || mActivityViewModel.isPostPaidMobileSelected.get()!!) {
                if (mDataBinding.inputPhoneNumber.text.isNotEmpty() && mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
                    mDataBinding.inputLayoutPhoneNumber.error =
                        LanguageData.getStringValue("EnterValidPhoneNumber")
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                    isValidForAll = false
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("PhoneNumber")
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                } else {
                    mDataBinding.inputLayoutPhoneNumber.error = ""
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                    val userMsisdn = mDataBinding.inputPhoneNumber.text.toString()
                    /*if (userMsisdn.startsWith("0", false)) {
                        checkNumberExistInFavorites(userMsisdn)
                        mDataBinding.inputLayoutPhoneNumber.error = ""
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                        var userMSISDNwithPrefix = userMsisdn.removePrefix("0")
                        userMSISDNwithPrefix = Constants.APP_MSISDN_PREFIX + userMSISDNwithPrefix
                        userMSISDNwithPrefix = userMSISDNwithPrefix.removePrefix("+")

                        if (isNumberRegexMatches) {
                            mDataBinding.inputLayoutPhoneNumber.error = ""
                            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                            msisdnEntered = userMSISDNwithPrefix
                        } else {
                            isValidForAll = false
                            mDataBinding.inputLayoutPhoneNumber.error =
                                LanguageData.getStringValue("EnterValidPhoneNumber")
                            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                            mDataBinding.inputLayoutPhoneNumber.hint =
                                LanguageData.getStringValue("PhoneNumber")
                            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                        }
                    } else {
                        mDataBinding.inputLayoutPhoneNumber.error =
                            LanguageData.getStringValue("EnterValidPhoneNumber")
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                        isValidForAll = false
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("PhoneNumber")
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    }*/

                    if (isNumberRegexMatches) {
                        mDataBinding.inputLayoutPhoneNumber.error = ""
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                        checkNumberExistInFavorites(userMsisdn)
                        msisdnEntered = userMsisdn
                    } else {
                        isValidForAll = false
                        mDataBinding.inputLayoutPhoneNumber.error =
                            LanguageData.getStringValue("EnterValidPhoneNumber")
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("PhoneNumber")
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    }
                }

            }

            if (mActivityViewModel.isInternetSelected.get()!!) {
                if (!mDataBinding.inputPhoneNumber.text.isNotEmpty()) {
                    mDataBinding.inputLayoutPhoneNumber.error =
                        LanguageData.getStringValue("EnterValidIdentifier")
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                    isValidForAll = false
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterPaymentIdentifier")
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                } else {
                    mDataBinding.inputLayoutPhoneNumber.error = ""
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false

                    val userMsisdn = mDataBinding.inputPhoneNumber.text.toString()

                    if (isNumberRegexMatches) {
                        mDataBinding.inputLayoutPhoneNumber.error = ""
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
                        checkNumberExistInFavorites(userMsisdn)
                        msisdnEntered = userMsisdn
                    } else {
                        isValidForAll = false
                        mDataBinding.inputLayoutPhoneNumber.error =
                            LanguageData.getStringValue("EnterValidIdentifier")
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("EnterPaymentIdentifier")
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    }

                }
            }
        }

        if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
            for(i in mActivityViewModel.validatedParams.indices){
                val typeChamp=mActivityViewModel.recievedParams[i].typeChamp
                val nomChamp=mActivityViewModel.recievedParams[i].nomChamp
                val valChamp=mActivityViewModel.recievedParams[i].inputValue
                val listVals=mActivityViewModel.recievedParams[i].listVals
                val firstVal=mActivityViewModel.recievedParams[i].firstValue
                val secondVal=mActivityViewModel.recievedParams[i].secondValue
                val allowedLength= Constants.STEP2_3RESPONSE?.params?.get(i)?.tailleMax
                val isTextValue =mActivityViewModel.recievedParams[i].typeChamp.equals("text")

               //using different list value to restore correct value after invalid input error
                val lablei=mActivityViewModel.demoParams[i].libelle

                Logger.debugLog("billpayment"," applyValidation = ${applyValidation}")
                Logger.debugLog("billpayment"," labelie = ${lablei}")
                if (applyValidation) {
                    val msisdn = mActivityViewModel.validatedParams[i].valChamp.toString().trim()
                    val msisdnLenght = msisdn.length

                        isNumberRegexMatches =
                            !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_CIL_REGEX, msisdn))

                    if (isNumberRegexMatches) {
                        mActivityViewModel.recievedParams.set(i,
                            RecievededParam(lablei,nomChamp,typeChamp,"",false,View.VISIBLE,valChamp,listVals,firstVal,secondVal))
                        mFatoratiParamsItemAdapter.notifyItemChanged(i)
                        msisdnEntered = mActivityViewModel.validatedParams[0].valChamp.toString().trim()

                        checkNumberExistInFavoritesForFatorati(msisdnEntered)
                    } else {
                        isValidForAll = false
                        mActivityViewModel.recievedParams.set(i,
                            RecievededParam(LanguageData.getStringValue("invalid")+ " " + lablei ,nomChamp,typeChamp,LanguageData.getStringValue("invalid") + " " + lablei,true,View.GONE,valChamp,listVals,firstVal,secondVal))
                        mFatoratiParamsItemAdapter.notifyItemChanged(i)
                    }
                } else {

    Logger.debugLog("billpayment","size 1 = ${mActivityViewModel.validatedParams.size}   size 2 = ${mActivityViewModel.recievedParams.size}")

                    //TSAV flow checking
                    if(lablei.equals("Immatriculation"))
                    {
                        Logger.debugLog("billpayment","Tsav8")
                        if(!firstVal.isEmpty()&&!secondVal.isEmpty()) {
                            Logger.debugLog("billpayment","immarticulateSelected = ${firstVal}   ${secondVal}")
                            Logger.debugLog("billpayment","Tsav9")


                            if (allowedLength != null) {
                                Logger.debugLog("billpayment","Tsav31")
                                if(mActivityViewModel.validatedParams[i].valChamp.length>allowedLength.toInt())
                                {
                                    Logger.debugLog("billpayment","Tsav41")
                                    isValidForAll = false
                                    mActivityViewModel.recievedParams
                                        .set(i,RecievededParam(LanguageData.getStringValue("invalid")+ " " + lablei ,nomChamp,typeChamp,LanguageData.getStringValue("invalid") + " " ,true,View.GONE,valChamp,listVals,firstVal,secondVal))
                                    mFatoratiParamsItemAdapter.notifyItemChanged(i)
                                }
                                else{
                                    Logger.debugLog("billpayment","Tsav51")
//                                    mActivityViewModel.recievedParams.set(i,RecievededParam(lablei, nomChamp, typeChamp, "", false, View.VISIBLE,valChamp,listVals,firstVal,secondVal))
//                                    mFatoratiParamsItemAdapter.notifyItemChanged(i)
//                                    msisdnEntered =
//                                        mActivityViewModel.validatedParams[0].valChamp.toString().trim()
//                                    checkNumberExistInFavoritesForFatorati(msisdnEntered)
                                }
                            }
                        }
                        else{
                            Logger.debugLog("billpayment","Tsav10")
                            isValidForAll = false
                            mActivityViewModel.recievedParams
                                .set(i,RecievededParam(lablei ,nomChamp,typeChamp,LanguageData.getStringValue("invalid") + " ",true,View.GONE,valChamp,listVals,firstVal,secondVal))
                            mFatoratiParamsItemAdapter.notifyItemChanged(i)
                        }
                    }
                    else{
                    //normal Flowchecking
                    if(!mActivityViewModel.validatedParams[i].valChamp.equals("")) {
                       Logger.debugLog("billpayment","value entered = ${mActivityViewModel.validatedParams[i].valChamp}")
//                       val isTsavSelected=mActivityViewModel.selectedCreancer.get()?.contains("TSAV")
//                        if(isTsavSelected!!)
//                        {
                            Logger.debugLog("billpayment","Tsav1")
                            if(isTextValue){
                                Logger.debugLog("billpayment","Tsav2")
                            if (allowedLength != null) {
                                Logger.debugLog("billpayment","Tsav3")
                                if(mActivityViewModel.validatedParams[i].valChamp.length>allowedLength.toInt())
                                {
                                    Logger.debugLog("billpayment","Tsav4")
                                    isValidForAll = false
                                    mActivityViewModel.recievedParams
                                        .set(i,RecievededParam(LanguageData.getStringValue("invalid")+ " " + lablei ,nomChamp,typeChamp,LanguageData.getStringValue("invalid") + " " + lablei,true,View.GONE,valChamp,listVals,firstVal,secondVal))
                                    mFatoratiParamsItemAdapter.notifyItemChanged(i)
                                }
                                else{
                                    Logger.debugLog("billpayment","Tsav5")
//                                    mActivityViewModel.recievedParams.set(i,RecievededParam(lablei, nomChamp, typeChamp, "", false, View.VISIBLE,valChamp,listVals,firstVal,secondVal))
//                                    mFatoratiParamsItemAdapter.notifyItemChanged(i)
//                                    msisdnEntered =
//                                        mActivityViewModel.validatedParams[0].valChamp.toString().trim()
//                                    checkNumberExistInFavoritesForFatorati(msisdnEntered)
                                }
                            }
                            }
                      //  }
                        else{
                            Logger.debugLog("billpayment","Tsav6")
//                            mActivityViewModel.recievedParams.set(i,RecievededParam(lablei, nomChamp, typeChamp, "", false, View.VISIBLE,valChamp,listVals,firstVal,secondVal))
//                            mFatoratiParamsItemAdapter.notifyItemChanged(i)
//                            msisdnEntered =
//                                mActivityViewModel.validatedParams[0].valChamp.toString().trim()
                            checkNumberExistInFavoritesForFatorati(msisdnEntered)
                        }
                   }
                    else{
                        Logger.debugLog("billpayment","Tsav7")
                       isValidForAll = false
                       mActivityViewModel.recievedParams
                           .set(i,RecievededParam(LanguageData.getStringValue("invalid")+ " " + lablei ,nomChamp,typeChamp,LanguageData.getStringValue("invalid") + " " + lablei,true,View.GONE,valChamp,listVals,firstVal,secondVal))
                       mFatoratiParamsItemAdapter.notifyItemChanged(i)
                    }
                    }
                }
            }








//Previous flow of single input Field
//            if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() /*|| mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_CIL_LENGTH.toInt()*/) {
//                isValidForAll = false
//                mDataBinding.inputLayoutPhoneNumber.error =
//                    LanguageData.getStringValue("invalid") + " " + cilLabel
//                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
//                mDataBinding.inputLayoutPhoneNumber.hint =
//                    LanguageData.getStringValue("invalid") + " " + cilLabel
//                mDataBinding.inputPhoneNumberHint.visibility = View.GONE
//            } else {
//                mDataBinding.inputLayoutPhoneNumber.error = ""
//                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
//
//                if (applyValidation) {
//                    if (isNumberRegexMatches) {
//                        mDataBinding.inputLayoutPhoneNumber.error = ""
//                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
//
//                        msisdnEntered = mDataBinding.inputPhoneNumber.text.toString().trim()
//
//                        checkNumberExistInFavoritesForFatorati(msisdnEntered)
//                    } else {
//                        isValidForAll = false
//                        mDataBinding.inputLayoutPhoneNumber.error =
//                            LanguageData.getStringValue("invalid") + " " + cilLabel
//                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
//                        mDataBinding.inputLayoutPhoneNumber.hint =
//                            LanguageData.getStringValue("invalid") + " " + cilLabel
//                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
//                    }
//                } else {
//                    mDataBinding.inputLayoutPhoneNumber.error = ""
//                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
//
//                    msisdnEntered = mDataBinding.inputPhoneNumber.text.toString().trim()
//
//                    checkNumberExistInFavoritesForFatorati(msisdnEntered)
//                }
//            }

            Logger.debugLog("billpayment","isValidForAll = ${isValidForAll}")
        }

        if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
            if (mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!) {
              if(!Constants.IS_AGENT_USER)
              {
                if (mDataBinding.inputCode.text.isNullOrEmpty() || mDataBinding.inputCode.text.toString()
                        .isEmpty()
                ) {
                    isValidForAll = false
                    mDataBinding.inputLayoutCode.error =
                        LanguageData.getStringValue("InvalidFidelioCode")
                    mDataBinding.inputLayoutCode.isErrorEnabled = true
                } else {
                    if (isCodeRegexMatches) {
                        mDataBinding.inputLayoutCode.error = ""
                        mDataBinding.inputLayoutCode.isErrorEnabled = false
                        code = mDataBinding.inputCode.text.toString().trim()
                    } else {
                        isValidForAll = false
                        mDataBinding.inputLayoutCode.error =
                            LanguageData.getStringValue("InvalidFidelioCode")
                        mDataBinding.inputLayoutCode.isErrorEnabled = true
                    }
                }
               }
            }
        }

        return isValidForAll
    }


    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var selectedFavorites = mDataBinding.spinnerSelectFavorites.selectedItem.toString()
        if (!selectedFavorites.equals(LanguageData.getStringValue("SelectFavorite"))) {
            setInputLayoutHint()
            if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
                if (mActivityViewModel.isInternetSelected.get()!!) {
                    selectedFavorites = selectedFavorites.substringAfter("-")
                    mDataBinding.inputPhoneNumber.setText(selectedFavorites)
                    mActivityViewModel.isUserSelectedFromFavorites.set(true)
                }
                if (mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!) {
                    selectedFavorites = selectedFavorites.substringAfter("-")
                    mDataBinding.inputPhoneNumber.setText(selectedFavorites)
                    mActivityViewModel.isUserSelectedFromFavorites.set(true)

                    var selectedBillType = ""
                    if(mActivityViewModel.isPostPaidMobileSelected.get()!!){
                        selectedBillType = "Telec_PostpaidMobile@"
                    }else if(mActivityViewModel.isPostPaidFixSelected.get()!!){
                        selectedBillType = "Telec_PostpaidFix@"
                    }
                    for (contacts in Constants.mContactListArray) {
                        val contactName = contacts.contactname
                        val contactNameWithoutPrefix = contactName.substringAfter("@")
                        val contactNumberCode =
                            contactNameWithoutPrefix.substringAfter(",")
                        if (contactName.contains(selectedBillType)) {
                            val contactNumber = contacts.customerreference.trim().plus(contacts.billproviderfri)
                            if(contactNumber.equals(selectedFavorites)){
                                if(!contactNumberCode.isNullOrEmpty()){
                                   // mDataBinding.inputCode.visibility=View.GONE
                                    mDataBinding.inputCode.setText(contactNumberCode)
                                   // mDataBinding.inputCode.setText("")
                                }
                            }
                        }
                    }
                }
            }

            if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                for (contacts in Constants.mContactListArray) {
                    var contactName = contacts.contactname
                    contactName = contactName.substringAfter("@")
                    contactName = contactName.substringBefore(",")
                    if (selectedFavorites.equals(contactName)) {
                        val selectedFri = contacts.customerreference.substringBefore("@")
                        mDataBinding.inputPhoneNumber.setText(selectedFri)
                        mActivityViewModel.isUserSelectedFromFavorites.set(true)
                        break
                    }

                }
            }

        } else {
            mDataBinding.inputPhoneNumber.setText("")
            mActivityViewModel.isUserSelectedFromFavorites.set(false)
            if (mDataBinding.inputLayoutPhoneNumber.isErrorEnabled) {

            } else {
                mDataBinding.inputPhoneNumber.clearFocus()
                mDataBinding.inputPhoneNumberHint.visibility = View.VISIBLE
                if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterCilNumber")
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                }
                if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
                    if (mActivityViewModel.isPostPaidMobileSelected.get()!! || mActivityViewModel.isPostPaidFixSelected.get()!!) {
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("MSISDNPlaceholder")
                        mDataBinding.inputPhoneNumberHint.text =
                            LanguageData.getStringValue("PhoneNumber")
                    } else if (mActivityViewModel.isInternetSelected.get()!!) {
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("MSISDNPlaceholder")
                        mDataBinding.inputPhoneNumberHint.text =
                            LanguageData.getStringValue("EnterPaymentIdentifier")
                    }
                }
            }
        }
    }

    private fun checkNumberExistInFavoritesForFatorati(msisdnEntered: String) {
        for (contacts in Constants.mContactListArray) {
            var contactNumber = contacts.customerreference
            contactNumber = contactNumber.substringBefore("@")
            if (msisdnEntered.equals(contactNumber)) {
                mActivityViewModel.isUserSelectedFromFavorites.set(true)
                break
            } else {
                mActivityViewModel.isUserSelectedFromFavorites.set(false)
                Log.i("FavoritesCheck", "false")
            }

        }
    }


    private fun checkNumberExistInFavorites(userMsisdn: String) {
        var updateMsisdn = ""
        if (!userMsisdn.startsWith("0", false)) {
            val appMsisdnPrefix = Constants.APP_MSISDN_PREFIX.removePrefix("+")
            if (userMsisdn.startsWith(appMsisdnPrefix)) {
                updateMsisdn = userMsisdn.replace(appMsisdnPrefix, "0")
            } else {
                updateMsisdn = userMsisdn
            }
        } else {
            updateMsisdn = userMsisdn
        }
        for (i in 0 until list_of_favorites.size) {
            val favoriteNumber = list_of_favorites[i].substringAfter("-")
            if (favoriteNumber.equals(updateMsisdn)) {
                mActivityViewModel.isUserSelectedFromFavorites.set(true)
                Log.i("FavoritesCheck", "true")
                break
            } else {
                mActivityViewModel.isUserSelectedFromFavorites.set(false)
                Log.i("FavoritesCheck", "false")
            }
        }
    }

    override fun afterTextChanged(editable: Editable?) {
        if (editable.hashCode() == mDataBinding.inputPhoneNumber.text.hashCode()) {
            val msisdn = mDataBinding.inputPhoneNumber.text.toString().trim()
            val msisdnLenght = msisdn.length

            if (mActivityViewModel.isFatoratiUseCaseSelected.get()!!) {
                isNumberRegexMatches =
                    !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_CIL_REGEX, msisdn))
            }
            if (mActivityViewModel.isBillUseCaseSelected.get()!!) {
                if (mActivityViewModel.isPostPaidMobileSelected.get()!!) {
                    isNumberRegexMatches =
                        !(msisdnLenght > 0 && !Pattern.matches(
                            Constants.APP_MSISDN_POSTPAIDBILL_MOBILE_REGEX,
                            msisdn
                        ))
                }
                if (mActivityViewModel.isPostPaidFixSelected.get()!!) {
                    isNumberRegexMatches =
                        !(msisdnLenght > 0 && !Pattern.matches(
                            Constants.APP_MSISDN_POSTPAIDBILL_FIXE_REGEX,
                            msisdn
                        ))
                }
                if (mActivityViewModel.isInternetSelected.get()!!) {
                    isNumberRegexMatches =
                        !(msisdnLenght > 0 && !Pattern.matches(
                            Constants.APP_MSISDN_POSTPAIDBILL_INTERNET_REGEX,
                            msisdn
                        ))
                }
            }
        } else if (editable.hashCode() == mDataBinding.inputCode.text.hashCode()) {
            val code = mDataBinding.inputCode.text.toString().trim()
            val codeLenght = code.length
            isCodeRegexMatches =
                !(codeLenght > 0 && !Pattern.matches(Constants.APP_BILL_PAYMENT_CODE_REGEX, code))
       }

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

}