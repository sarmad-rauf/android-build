package com.es.marocapp.usecase.favorites

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.FatoratiParamsItemAdapter
import com.es.marocapp.adapter.FavoriteParamsItemAdapter
import com.es.marocapp.databinding.FragmentFavoritesEnterNumberBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.BillPaymentFatoratiStepThreeResponse
import com.es.marocapp.model.responses.RecievededParam
import com.es.marocapp.model.responses.ValidatedParam
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger
import com.google.gson.Gson
import java.util.regex.Pattern

class FavoriteEnterContactFragment : BaseFragment<FragmentFavoritesEnterNumberBinding>(),
    FavoritesPaymentClickListener, TextWatcher, AdapterView.OnItemSelectedListener {

    private var applyValidation: Boolean = false
    private lateinit var mActivitViewModel: FavoritesViewModel
    private lateinit var mFavoriteParamsItemAdapter: FavoriteParamsItemAdapter
    var msisdnEntered = ""
    var code = ""

    var isNumberRegexMatches = false
    var isCodeRegexMatches = false

    private var list_of_paymentType_bill : ArrayList<String> = ArrayList()

    private var isInternetTypeSelected = false
    private var isMobileUseCaseSelected = false
    private var isFixeUseCaseSelected = false

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
        mDataBinding.inputCode.addTextChangedListener(this)

        mDataBinding.inputPhoneNumber.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setInputHint()
            } else {
                if (mDataBinding.inputLayoutPhoneNumber.isErrorEnabled) {
                    setInputHint()
                }else{
                    if (mDataBinding.inputPhoneNumber.text.isEmpty()) {
                        mDataBinding.inputPhoneNumberHint.visibility = View.VISIBLE
                        if(mActivitViewModel.isPaymentSelected.get()!!){
                            if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                                mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("CINPlaceholder")
                                mDataBinding.inputPhoneNumberHint.text =
                                    LanguageData.getStringValue("EnterCilNumber")
                                mDataBinding.inputLayoutPhoneNumber.visibility=View.GONE
                                mDataBinding.inputPhoneNumberHint.visibility=View.GONE
                        //        showDynamicListOfParams(mActivitViewModel.fatoratiStepThreeObserver.get()!!)
                            }else{
                                mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
                                mDataBinding.inputPhoneNumberHint.text =
                                    LanguageData.getStringValue("EnterContactNumber")
                            }
                        }else{
                            mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
                            mDataBinding.inputPhoneNumberHint.text =
                                LanguageData.getStringValue("EnterContactNumber")
                        }

                    } else {
                        setInputHint()
                    }
                }
            }
        }

        list_of_paymentType_bill.clear()
        list_of_paymentType_bill.apply {
            add(LanguageData.getStringValue("PostpaidMobile").toString())
            add(LanguageData.getStringValue("PostpaidFix").toString())
            add(LanguageData.getStringValue("Internet").toString())
        }

        val adapterFavoriteType = ArrayAdapter<CharSequence>(
            activity as FavoritesActivity, R.layout.layout_favorites_spinner_text,
            list_of_paymentType_bill as List<CharSequence>
        )
        mDataBinding.spinnerSelectBillType.apply {
            adapter = adapterFavoriteType
        }
        mDataBinding.spinnerSelectBillType.onItemSelectedListener = this@FavoriteEnterContactFragment

        setVisibility()
        setStrings()
        subscribeObserver()
    }

    private fun showDynamicListOfParams(it: BillPaymentFatoratiStepThreeResponse?) {
        if (it != null) {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                mActivitViewModel.specialMenuBillSelected=false
                //   mDataBinding.acountTypeSpinner.visibility=View.GONE
                //     mDataBinding.selectAcountTitile.visibility=View.GONE
                //  cilLabel = it.param.libelle
                if (mActivitViewModel.isFatoratiUsecaseSelected.get()!!) {
                    mActivitViewModel.refTxFatourati = it.refTxFatourati
                    mActivitViewModel.validatedParams.clear()
                    mActivitViewModel.recievedParams.clear()
                    mActivitViewModel.demoParams.clear()
                    mDataBinding.inputLayoutPhoneNumber.visibility=View.GONE
                    mDataBinding.inputPhoneNumberHint.visibility=View.GONE
                    mDataBinding.mFieldsRecycler.visibility=View.VISIBLE
                    for(i in it.params.indices){
                        val validatedParams = ValidatedParam("",it.params[i].nomChamp)
                        mActivitViewModel.validatedParams.add(validatedParams)
                        mActivitViewModel.recievedParams.add(
                            RecievededParam(it.params[i].libelle,it.params[i].nomChamp,it.params[i].typeChamp,"",
                                false,View.VISIBLE,"",it.params[i].listVals,"","")
                        )
                        //using for lable value restoring after error of invalid input
                        mActivitViewModel.demoParams.add(
                            RecievededParam(it.params[i].libelle,it.params[i].nomChamp,it.params[i].typeChamp,"",
                                false,View.VISIBLE,"",it.params[i].listVals,"","")
                        )
                    }
                    mFavoriteParamsItemAdapter = FavoriteParamsItemAdapter(activity ,mActivitViewModel.recievedParams,object :
                        FavoriteParamsItemAdapter.ParamTextChangedListner{
                        override fun onParamTextChangedClick(valChamp: String, position: Int) {
                            Logger.debugLog("textChengeLListner","Value = ${valChamp}  == position = ${position}")
                            if (position <= it.params.size - 1) {
                                if (it.params[position].libelle.equals("CIL", false)) {
                                    applyValidation = true
                                } else {
                                    applyValidation = false
                                }


    //                                val editedText = valChamp
    //                                val oldText = mActivitViewModel.validatedParams[position].valChamp
    //                                if (editedText.contains(oldText)) {
                                val typeChamp =
                                    mActivitViewModel.recievedParams[position].typeChamp
                                val nomChamp =
                                    mActivitViewModel.recievedParams[position].nomChamp
                                val listVals =
                                    mActivitViewModel.recievedParams[position].listVals

                                //using different list value to restore correct value after invalid input error
                                val lablei = mActivitViewModel.demoParams[position].libelle

                                mActivitViewModel.recievedParams.set(
                                    position,
                                    RecievededParam(
                                        lablei, nomChamp, typeChamp, "",
                                        false, View.VISIBLE, valChamp, listVals,"",""
                                    )
                                )


                                mActivitViewModel.validatedParams.set(
                                    position,
                                    ValidatedParam(
                                        valChamp,
                                        mActivitViewModel.recievedParams[position].nomChamp
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
                            val typeChamp=mActivitViewModel.recievedParams[position].typeChamp
                            val nomChamp=mActivitViewModel.recievedParams[position].nomChamp
                            val listVals=mActivitViewModel.recievedParams[position].listVals
                            var convertesSpinnerValue= Constants.convertSpinnerArabicValue(spinnerVal)
                            val valChamp=firstVal.plus(convertesSpinnerValue.plus(secondVal))

                            //using different list value to restore correct value after invalid input error
                            val lablei=mActivitViewModel.demoParams[position].libelle

                            mActivitViewModel.recievedParams.set(position,
                                RecievededParam(lablei,nomChamp,typeChamp,"",
                                    false,View.VISIBLE,"",listVals,firstVal,secondVal)
                            )

                            mActivitViewModel.validatedParams.set(
                                position,
                                ValidatedParam(
                                    valChamp,
                                    mActivitViewModel.recievedParams[position].nomChamp
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

                                Logger.debugLog("lydec","position  ${position} == ${mActivitViewModel.validatedParams.toString()}==${valChamp}")

                                val typeChamp=mActivitViewModel.recievedParams[position].typeChamp
                                val nomChamp=mActivitViewModel.recievedParams[position].nomChamp
                                val listVals=mActivitViewModel.recievedParams[position].listVals

                                //using different list value to restore correct value after invalid input error
                                val lablei=mActivitViewModel.demoParams[position].libelle

                                mActivitViewModel.recievedParams.set(
                                    position, RecievededParam(lablei,nomChamp,typeChamp,"",
                                        false,View.VISIBLE,valChamp,listVals,"","")
                                )


                                mActivitViewModel.validatedParams.set(
                                    position,
                                    ValidatedParam(
                                        valChamp,
                                        mActivitViewModel.recievedParams[position].nomChamp
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

                            val typeChamp=mActivitViewModel.recievedParams[position].typeChamp
                            val nomChamp=mActivitViewModel.recievedParams[position].nomChamp
                            val listVals=mActivitViewModel.recievedParams[position].listVals
                            var convertedSpinnerValue= Constants.convertSpinnerArabicValue(spinnerVal)
                            val valChamp=firstVal.plus(convertedSpinnerValue.plus(secondVal))
                            //  Constants.selectedTSAVSpinnerPosition=position
                            //using different list value to restore correct value after invalid input error
                            val lablei=mActivitViewModel.demoParams[position].libelle

                            mActivitViewModel.recievedParams.set(position,
                                RecievededParam(lablei,nomChamp,typeChamp,"",
                                    false,View.VISIBLE,"",listVals,firstVal,secondVal)
                            )

                            mActivitViewModel.validatedParams.set(
                                position,
                                ValidatedParam(
                                    valChamp,
                                    mActivitViewModel.recievedParams[position].nomChamp
                                )
                            )
                        }
                    })
                    mDataBinding.mFieldsRecycler.visibility=View.VISIBLE
                    mDataBinding.mFieldsRecycler.apply {
                        adapter = mFavoriteParamsItemAdapter
                        layoutManager = LinearLayoutManager(activity)
                    }
                }

            }
        }
    }

    fun setInputHint(){
        if(mActivitViewModel.isPaymentSelected.get()!!){
            if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("EnterCilNumber")
                mDataBinding.inputLayoutPhoneNumber.visibility=View.GONE
                showDynamicListOfParams(mActivitViewModel.fatoratiStepThreeObserver.get())
            }else{
                /*mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("EnterContactNumber")*/
                if (isInternetTypeSelected) {
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("PhoneNumber")
                } else if (isMobileUseCaseSelected || isFixeUseCaseSelected) {
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterPaymentIdentifier")
                }else{
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterContactNumber")
                }
            }
        }else{
            mDataBinding.inputLayoutPhoneNumber.hint =
                LanguageData.getStringValue("EnterContactNumber")

        }
        mDataBinding.inputPhoneNumberHint.visibility = View.GONE

    }

    private fun subscribeObserver() {
        mActivitViewModel.errorText.observe(this@FavoriteEnterContactFragment, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        })

        mActivitViewModel.getAddFavoritesResponseListner.observe(this@FavoriteEnterContactFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                      //  if(!it.contactList.isNullOrEmpty()){
//                        Constants.mContactListArray.clear()
//                        Constants.mContactListArray.addAll(it.contactList)
                        DialogUtils.showSuccessDialog(activity,it.description,object : DialogUtils.OnConfirmationDialogClickListner{
                            override fun onDialogYesClickListner() {
                                (activity as FavoritesActivity).navController.popBackStack(R.id.favoriteTypesFragment,false)
                            }

                            override fun onDialogNoClickListner() {

                            }
                        })
                  //  }
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
                mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("CINPlaceholder")
                mDataBinding.inputPhoneNumberHint.text =
                    LanguageData.getStringValue("EnterCilNumber")
            }else{
                /*mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
                mDataBinding.inputPhoneNumberHint.text =
                    LanguageData.getStringValue("EnterContactNumber")*/
                if (isMobileUseCaseSelected || isFixeUseCaseSelected) {
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("MSISDNPlaceholder")
                    mDataBinding.inputPhoneNumberHint.text =
                        LanguageData.getStringValue("PhoneNumber")
                } else if (isInternetTypeSelected) {
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("MSISDNPlaceholder")
                    mDataBinding.inputPhoneNumberHint.text =
                        LanguageData.getStringValue("EnterPaymentIdentifier")
                }else{
                    mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
                    mDataBinding.inputPhoneNumberHint.text =
                        LanguageData.getStringValue("EnterContactNumber")
                }
            }
        }else{
            mDataBinding.inputLayoutPhoneNumber.hint = LanguageData.getStringValue("MSISDNPlaceholder")
            mDataBinding.inputPhoneNumberHint.text =
                LanguageData.getStringValue("EnterContactNumber")
        }

        mDataBinding.inputLayoutName.hint = LanguageData.getStringValue("EnterName")
        mDataBinding.selectBillTypeTypeTitle.hint = LanguageData.getStringValue("SelectBillType")
        mDataBinding.inputLayoutCode.hint = LanguageData.getStringValue("EnterCode")

        if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!)
        {
            setInputHint()
        }
    }

    override fun onNextButtonClick(view: View) {
        if(isValidForAll()){
            var nickName = mDataBinding.inputName.text.toString().trim()
            if(mActivitViewModel.isPaymentSelected.get()!!){
                if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                   // Util_<billTypeName>@<Favorite nickName>,<logo>,<codeCreance>,<codeCreancier>,(<nomChamp>:<valueChamp>),<refTxFatourati>

                        val stringParams = Constants.convertListToJson(mActivitViewModel.validatedParams)
                    val logoPath=mActivitViewModel.selectedCompanyLogo.replace(Constants.marocFatouratiLogoPath,"").trim()

                    val fatoratiNickName = "Util_${mActivitViewModel.fatoratiTypeSelected}@$nickName,${logoPath},${mActivitViewModel.selectedCodeCreance},${mActivitViewModel.creancierID}," +
                            "${stringParams},${mActivitViewModel.refTxFatourati}"
                    val reciever:String=Constants.getFavouriteAlias(mActivitViewModel.validatedParams[0].valChamp)

                    mActivitViewModel.requestForAddFavoritesApi(activity,fatoratiNickName,reciever)
                }else{
                    if(isInternetTypeSelected){
                        nickName = "Telec_Internet@$nickName"
                        msisdnEntered = Constants.getPostPaidInternetDomainAlias(msisdnEntered)
                        mActivitViewModel.selectedCompanySPName=Constants.getPostPaidInternetDomainAlias("")
                    }else if(isMobileUseCaseSelected){
                        nickName = "Telec_PostpaidMobile@$nickName,${code}"
                        msisdnEntered = Constants.getPostPaidMobileDomainAlias(msisdnEntered)
                        mActivitViewModel.selectedCompanySPName=Constants.getPostPaidMobileDomainAlias("")
                        Logger.debugLog("BillPaymentCode",code)

                    }else if(isFixeUseCaseSelected){
                        nickName = "Telec_PostpaidFix@$nickName,${code}"
                        msisdnEntered = Constants.getPostPaidFixedDomainAlias(msisdnEntered)
                        mActivitViewModel.selectedCompanySPName=Constants.getPostPaidFixedDomainAlias("")
                        Logger.debugLog("BillPaymentCode",code)
                    }

                    Logger.debugLog("BillPaymentNickName",nickName)
                    Logger.debugLog("BillPaymentMsisdn",msisdnEntered)
                    mActivitViewModel.requestForAddFavoritesApi(activity,nickName,msisdnEntered)
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
                if (mActivitViewModel.isFatoratiUsecaseSelected.get()!!) {
                    for(i in mActivitViewModel.validatedParams.indices){
                        val typeChamp=mActivitViewModel.recievedParams[i].typeChamp
                        val nomChamp=mActivitViewModel.recievedParams[i].nomChamp
                        val valChamp=mActivitViewModel.recievedParams[i].inputValue
                        val listVals=mActivitViewModel.recievedParams[i].listVals
                        val firstVal=mActivitViewModel.recievedParams[i].firstValue
                        val secondVal=mActivitViewModel.recievedParams[i].secondValue
                        val allowedLength= Constants.STEP2_3RESPONSE?.params?.get(i)?.tailleMax
                        val isTextValue =mActivitViewModel.recievedParams[i].typeChamp.equals("text")

                        //using different list value to restore correct value after invalid input error
                        val lablei=mActivitViewModel.demoParams[i].libelle

                        Logger.debugLog("billpayment"," applyValidation = ${applyValidation}")
                        Logger.debugLog("billpayment"," labelie = ${lablei}")
                        if (applyValidation) {
                            val msisdn = mActivitViewModel.validatedParams[i].valChamp.toString().trim()
                            val msisdnLenght = msisdn.length

                            isNumberRegexMatches =
                                !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_CIL_REGEX, msisdn))

                            if (isNumberRegexMatches) {
                                mActivitViewModel.recievedParams.set(i,
                                    RecievededParam(lablei,nomChamp,typeChamp,"",false,View.VISIBLE,valChamp,listVals,firstVal,secondVal))
                                mFavoriteParamsItemAdapter.notifyItemChanged(i)
                                msisdnEntered = mActivitViewModel.validatedParams[0].valChamp.toString().trim()

                            } else {
                                isValidForAll = false
                                mActivitViewModel.recievedParams.set(i,
                                    RecievededParam(LanguageData.getStringValue("invalid")+ " " + lablei ,nomChamp,typeChamp,LanguageData.getStringValue("invalid") + " " + lablei,true,View.GONE,valChamp,listVals,firstVal,secondVal))
                                mFavoriteParamsItemAdapter.notifyItemChanged(i)
                            }
                        } else {

                            Logger.debugLog("billpayment","size 1 = ${mActivitViewModel.validatedParams.size}   size 2 = ${mActivitViewModel.recievedParams.size}")

                            //TSAV flow checking
                            if(lablei.equals("Immatriculation"))
                            {
                                Logger.debugLog("billpayment","Tsav8")
                                if(!firstVal.isEmpty()&&!secondVal.isEmpty()) {
                                    Logger.debugLog("billpayment","immarticulateSelected = ${firstVal}   ${secondVal}")
                                    Logger.debugLog("billpayment","Tsav9")


                                    if (allowedLength != null) {
                                        Logger.debugLog("billpayment","Tsav31")
                                        if(mActivitViewModel.validatedParams[i].valChamp.length>allowedLength.toInt())
                                        {
                                            Logger.debugLog("billpayment","Tsav41")
                                            isValidForAll = false
                                            mActivitViewModel.recievedParams
                                                .set(i,RecievededParam(LanguageData.getStringValue("invalid")+ " " + lablei ,nomChamp,typeChamp,LanguageData.getStringValue("invalid") + " " ,true,View.GONE,valChamp,listVals,firstVal,secondVal))
                                            mFavoriteParamsItemAdapter.notifyItemChanged(i)
                                        }
                                        else{
                                            Logger.debugLog("billpayment","Tsav51")
//                                    mActivitViewModel.recievedParams.set(i,RecievededParam(lablei, nomChamp, typeChamp, "", false, View.VISIBLE,valChamp,listVals,firstVal,secondVal))
//                                    mFatoratiParamsItemAdapter.notifyItemChanged(i)
//                                    msisdnEntered =
//                                        mActivitViewModel.validatedParams[0].valChamp.toString().trim()
//                                    checkNumberExistInFavoritesForFatorati(msisdnEntered)
                                        }
                                    }
                                }
                                else{
                                    Logger.debugLog("billpayment","Tsav10")
                                    isValidForAll = false
                                    mActivitViewModel.recievedParams
                                        .set(i,RecievededParam(lablei ,nomChamp,typeChamp,LanguageData.getStringValue("invalid") + " ",true,View.GONE,valChamp,listVals,firstVal,secondVal))
                                    mFavoriteParamsItemAdapter.notifyItemChanged(i)
                                }
                            }
                            else{
                                //normal Flowchecking
                                if(!mActivitViewModel.validatedParams[i].valChamp.equals("")) {
                                    Logger.debugLog("billpayment","value entered = ${mActivitViewModel.validatedParams[i].valChamp}")
//                       val isTsavSelected=mActivitViewModel.selectedCreancer.get()?.contains("TSAV")
//                        if(isTsavSelected!!)
//                        {
                                    Logger.debugLog("billpayment","Tsav1")
                                    if(isTextValue){
                                        Logger.debugLog("billpayment","Tsav2")
                                        if (allowedLength != null) {
                                            Logger.debugLog("billpayment","Tsav3")
                                            if(mActivitViewModel.validatedParams[i].valChamp.length>allowedLength.toInt())
                                            {
                                                Logger.debugLog("billpayment","Tsav4")
                                                isValidForAll = false
                                                mActivitViewModel.recievedParams
                                                    .set(i,RecievededParam(LanguageData.getStringValue("invalid")+ " " + lablei ,nomChamp,typeChamp,LanguageData.getStringValue("invalid") + " " + lablei,true,View.GONE,valChamp,listVals,firstVal,secondVal))
                                                mFavoriteParamsItemAdapter.notifyItemChanged(i)
                                            }
                                            else{
                                                Logger.debugLog("billpayment","Tsav5")
//                                    mActivitViewModel.recievedParams.set(i,RecievededParam(lablei, nomChamp, typeChamp, "", false, View.VISIBLE,valChamp,listVals,firstVal,secondVal))
//                                    mFatoratiParamsItemAdapter.notifyItemChanged(i)
//                                    msisdnEntered =
//                                        mActivitViewModel.validatedParams[0].valChamp.toString().trim()
//                                    checkNumberExistInFavoritesForFatorati(msisdnEntered)
                                            }
                                        }
                                    }
                                    //  }
                                    else{
                                        Logger.debugLog("billpayment","Tsav6")
//                            mActivitViewModel.recievedParams.set(i,RecievededParam(lablei, nomChamp, typeChamp, "", false, View.VISIBLE,valChamp,listVals,firstVal,secondVal))
//                            mFatoratiParamsItemAdapter.notifyItemChanged(i)
//                            msisdnEntered =
//                                mActivitViewModel.validatedParams[0].valChamp.toString().trim()
                                    }
                                }
                                else{
                                    Logger.debugLog("billpayment","Tsav7")
                                    isValidForAll = false
                                    mActivitViewModel.recievedParams
                                        .set(i,RecievededParam(LanguageData.getStringValue("invalid")+ " " + lablei ,nomChamp,typeChamp,LanguageData.getStringValue("invalid") + " " + lablei,true,View.GONE,valChamp,listVals,firstVal,secondVal))
                                    mFavoriteParamsItemAdapter.notifyItemChanged(i)
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
//                if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() || mDataBinding.inputPhoneNumber.text.toString().length < 6) {
//                    isValidForAll = false
//                    mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidCILNumber")
//                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
//                    mDataBinding.inputLayoutPhoneNumber.hint =
//                        LanguageData.getStringValue("EnterCilNumber")
//                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
//                } else {
//                    mDataBinding.inputLayoutPhoneNumber.error = ""
//                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
//
//                    if(isNumberRegexMatches){
//                        mDataBinding.inputLayoutPhoneNumber.error = ""
//                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
//
//                        msisdnEntered = mDataBinding.inputPhoneNumber.text.toString().trim()
//                    }else{
//                        isValidForAll = false
//                        mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidCILNumber")
//                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
//                        mDataBinding.inputLayoutPhoneNumber.hint =
//                            LanguageData.getStringValue("EnterCilNumber")
//                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
//                    }
//
//                }
            }else{

                if (isMobileUseCaseSelected || isFixeUseCaseSelected) {
                    if (!mDataBinding.inputPhoneNumber.text.isNotEmpty() && mDataBinding.inputPhoneNumber.text.toString().length > Constants.APP_MSISDN_LENGTH.toInt() - 2) {
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

                        var userMsisdn = mDataBinding.inputPhoneNumber.text.toString()

                        if (isNumberRegexMatches) {
                            mDataBinding.inputLayoutPhoneNumber.error = ""
                            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
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

                    if(!Constants.IS_AGENT_USER) {
                        if (mDataBinding.inputCode.text.isNullOrEmpty() || mDataBinding.inputCode.text.toString()
                                .isEmpty()
                        ) {
                            isValidForAll = false
                            mDataBinding.inputLayoutCode.error =
                                LanguageData.getStringValue("PleaseEnterValidCode")
                            mDataBinding.inputLayoutCode.isErrorEnabled = true
                        } else {
                            if (isCodeRegexMatches) {
                                mDataBinding.inputLayoutCode.error = ""
                                mDataBinding.inputLayoutCode.isErrorEnabled = false
                                code = mDataBinding.inputCode.text.toString().trim()
                            } else {
                                isValidForAll = false
                                mDataBinding.inputLayoutCode.error =
                                    LanguageData.getStringValue("PleaseEnterValidCode")
                                mDataBinding.inputLayoutCode.isErrorEnabled = true
                            }
                        }
                    }

                }

                if (isInternetTypeSelected) {
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

                        var userMsisdn = mDataBinding.inputPhoneNumber.text.toString()

                        if (isNumberRegexMatches) {
                            mDataBinding.inputLayoutPhoneNumber.error = ""
                            mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = false
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
                /*if (mDataBinding.inputPhoneNumber.text.isNullOrEmpty() || mDataBinding.inputPhoneNumber.text.toString().length < Constants.APP_MSISDN_LENGTH.toInt() - 2) {
                    isValidForAll = false
                    mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterContactNumber")
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
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
                            mDataBinding.inputLayoutPhoneNumber.hint =
                                LanguageData.getStringValue("EnterContactNumber")
                            mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                        }
                    } else {
                        isValidForAll = false
                        mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                        mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("EnterContactNumber")
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    }
                }*/
            }
        }else{
            
            if (!mDataBinding.inputPhoneNumber.text.isNullOrEmpty() || mDataBinding.inputPhoneNumber.text.toString().length > Constants.APP_MSISDN_LENGTH.toInt() - 2) {
                isValidForAll = false
                mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                mDataBinding.inputLayoutPhoneNumber.hint =
                    LanguageData.getStringValue("EnterContactNumber")
                mDataBinding.inputPhoneNumberHint.visibility = View.GONE
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
                        mDataBinding.inputLayoutPhoneNumber.hint =
                            LanguageData.getStringValue("EnterContactNumber")
                        mDataBinding.inputPhoneNumberHint.visibility = View.GONE
                    }
                } else {
                    isValidForAll = false
                    mDataBinding.inputLayoutPhoneNumber.error = LanguageData.getStringValue("PleaseEnterValidContactNumber")
                    mDataBinding.inputLayoutPhoneNumber.isErrorEnabled = true
                    mDataBinding.inputLayoutPhoneNumber.hint =
                        LanguageData.getStringValue("EnterContactNumber")
                    mDataBinding.inputPhoneNumberHint.visibility = View.GONE
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

    override fun afterTextChanged(editable : Editable?) {
        /*var msisdn = mDataBinding.inputPhoneNumber.text.toString().trim()
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
        }*/
        if (editable.hashCode() == mDataBinding.inputPhoneNumber.text.hashCode()) {
            var msisdn = mDataBinding.inputPhoneNumber.text.toString().trim()
            var msisdnLenght = msisdn.length

            if(mActivitViewModel.isPaymentSelected.get()!!){
                if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                    isNumberRegexMatches =
                        !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_CIL_REGEX, msisdn))
                }else{
                    if(isFixeUseCaseSelected){
                        isNumberRegexMatches =
                            !(msisdnLenght > 0 && !Pattern.matches(
                                Constants.APP_MSISDN_POSTPAIDBILL_FIXE_REGEX,
                                msisdn
                            ))
                    }

                    if(isMobileUseCaseSelected){
                        isNumberRegexMatches =
                            !(msisdnLenght > 0 && !Pattern.matches(
                                Constants.APP_MSISDN_POSTPAIDBILL_MOBILE_REGEX,
                                msisdn
                            ))
                    }
                    if(isInternetTypeSelected){
                        isNumberRegexMatches =
                            !(msisdnLenght > 0 && !Pattern.matches(
                                Constants.APP_MSISDN_POSTPAIDBILL_INTERNET_REGEX,
                                msisdn
                            ))
                    }
                }
            }else{
                isNumberRegexMatches =
                    !(msisdnLenght > 0 && !Pattern.matches(Constants.APP_MSISDN_REGEX, msisdn))
            }


        } else if (editable.hashCode() == mDataBinding.inputCode.text.hashCode()) {
            var code = mDataBinding.inputCode.text.toString().trim()
            var codeLenght = code.length
            isCodeRegexMatches =
                !(codeLenght > 0 && !Pattern.matches(Constants.APP_BILL_PAYMENT_CODE_REGEX, code))
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var selectedPaymentType = mDataBinding.spinnerSelectBillType.selectedItem.toString()
        if(selectedPaymentType.equals(LanguageData.getStringValue("Internet").toString())){
            mDataBinding.inputLayoutCode.visibility = View.GONE
            isInternetTypeSelected = true
            isMobileUseCaseSelected = false
            isFixeUseCaseSelected = false
            mDataBinding.inputPhoneNumber.clearFocus()
            mDataBinding.inputPhoneNumber.setText("")
            mDataBinding.inputCode.setText("")
            setStrings()
        }else if(selectedPaymentType.equals(LanguageData.getStringValue("PostpaidFix").toString())){
            mDataBinding.inputLayoutCode.visibility = View.VISIBLE
           mDataBinding.inputCode.setText("")
            isInternetTypeSelected = false
            isMobileUseCaseSelected = false
            isFixeUseCaseSelected = true
            mDataBinding.inputPhoneNumber.clearFocus()
            mDataBinding.inputPhoneNumber.setText("")
            mDataBinding.inputCode.setText("")
            setStrings()
        }else if(selectedPaymentType.equals(LanguageData.getStringValue("PostpaidMobile").toString())){
            mDataBinding.inputLayoutCode.visibility = View.VISIBLE
            mDataBinding.inputCode.setText("")
            isInternetTypeSelected = false
            isMobileUseCaseSelected = true
            isFixeUseCaseSelected = false
            mDataBinding.inputPhoneNumber.clearFocus()
            mDataBinding.inputCode.setText("")
            setStrings()
        }
    }

    fun setVisibility(){
        if(mActivitViewModel.isPaymentSelected.get()!!){
            if(mActivitViewModel.isFatoratiUsecaseSelected.get()!!){
                mDataBinding.spinnerSelectBillType.visibility = View.GONE
                mDataBinding.inputLayoutCode.visibility = View.GONE
                mDataBinding.selectBillTypeTypeTitle.visibility = View.GONE

            }else{
                mDataBinding.spinnerSelectBillType.visibility = View.VISIBLE
                mDataBinding.selectBillTypeTypeTitle.visibility = View.VISIBLE

                if(Constants.IS_AGENT_USER) {

                    mDataBinding.inputLayoutCode.visibility = View.GONE
                }
                Logger.debugLog("beneficeryManagment","fadilo gone 1")
            }
        }else{
            mDataBinding.spinnerSelectBillType.visibility = View.GONE
            mDataBinding.inputLayoutCode.visibility = View.GONE
            mDataBinding.selectBillTypeTypeTitle.visibility = View.GONE
            if(Constants.IS_AGENT_USER) {

                mDataBinding.inputLayoutCode.visibility = View.GONE
            }
            Logger.debugLog("beneficeryManagment","fadilo gone 2")
        }
    }
}