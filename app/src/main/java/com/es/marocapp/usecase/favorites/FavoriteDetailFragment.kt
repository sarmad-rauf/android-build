package com.es.marocapp.usecase.favorites

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.FatoratiParamsItemAdapter
import com.es.marocapp.adapter.LanguageCustomSpinnerAdapter
import com.es.marocapp.databinding.FragmentFavoriteDetailsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.Creancier
import com.es.marocapp.model.responses.RecievededParam
import com.es.marocapp.model.responses.ValidatedParam
import com.es.marocapp.model.responses.creances
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.billpayment.BillPaymentActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger


class FavoriteDetailFragment : BaseFragment<FragmentFavoriteDetailsBinding>(),
    FavoritesPaymentClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var mFatoratiParamsItemAdapter: FatoratiParamsItemAdapter
    private lateinit var mActivitViewModel: FavoritesViewModel
    private var list_of_paymentType = arrayOf((LanguageData.getStringValue("Fatourati").toString()))
    private var list_of_billType: ArrayList<String> = arrayListOf()
    private var list_of_FatouratieType : ArrayList<Creancier> = arrayListOf()
    var applyValidation = false

    lateinit var acountTypeSpinnerAdapter: LanguageCustomSpinnerAdapter

    override fun setLayout(): Int {
        return R.layout.fragment_favorite_details
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivitViewModel =
            ViewModelProvider(activity as FavoritesActivity).get(FavoritesViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivitViewModel
            listener = this@FavoriteDetailFragment
        }

        list_of_billType.clear()
        list_of_billType.apply {
            var fatoratiType = mActivitViewModel.fatoratiStepOneObserver.get()!!.creanciers
            list_of_FatouratieType = fatoratiType as ArrayList<Creancier>
            if (fatoratiType.isNotEmpty()) {
                for (i in fatoratiType.indices) {
                    add(fatoratiType[i].nomCreancier)
                }
            }
        }

        val adapterPaymentType = ArrayAdapter<CharSequence>(
            activity as FavoritesActivity,
            R.layout.layout_favorites_spinner_text,
            list_of_paymentType
        )
        mDataBinding.spinnerSelectPayment.apply {
            adapter = adapterPaymentType
        }

        val adapterBillType = ArrayAdapter<CharSequence>(
            activity as FavoritesActivity,
            R.layout.layout_favorites_spinner_text,
            list_of_billType as List<CharSequence>
        )
        mDataBinding.spinnerSelectBillType.apply {
            adapter = adapterBillType
        }

        mDataBinding.spinnerSelectBillType.onItemSelectedListener = this@FavoriteDetailFragment

        (activity as FavoritesActivity).setHeader(LanguageData.getStringValue("Add").toString())

        mActivitViewModel.popBackStackTo = R.id.favoritesAddOrViewFragment

        setStrings()
        subscribeForSpinnerListner()
        subscribeObserver()

    }

    private fun subscribeObserver() {
        mActivitViewModel.getFatoratiStepTwoResponseListner.observe(this@FavoriteDetailFragment, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                    mDataBinding.acountTypeSpinner.visibility=View.VISIBLE
                   hideViews()
                    mActivitViewModel.setCreancesList(it.creances as ArrayList<creances>)
                    var nomCreancierList:ArrayList<String> = ArrayList()
                    for (i in it.creances .indices)
                    {
                        nomCreancierList.add(it.creances.get(i).nomCreance)
                    }
                    val acountTypeArray: Array<String> =
                        nomCreancierList.toArray(arrayOfNulls<String>(nomCreancierList.size))
                    acountTypeSpinnerAdapter =
                        LanguageCustomSpinnerAdapter(
                            activity as FavoritesActivity,
                            acountTypeArray,
                            (activity as FavoritesActivity).resources.getColor(R.color.colorBlack),true
                        )
                    //  mDataBinding.acountTypeSpinner
                    mDataBinding.acountTypeSpinner.apply {
                        adapter = acountTypeSpinnerAdapter
                    }

                    mDataBinding.inputPhoneNumber.isEnabled=false
                    mDataBinding.inputLayoutPhoneNumber.hint = mActivitViewModel.creancesList.get()
                        ?.get(0)?.nomCreance
                    mDataBinding.inputPhoneNumber.setText( mActivitViewModel.creancesList.get()
                        ?.get(0)?.codeCreance.toString())


            }else{
                DialogUtils.showErrorDialoge(activity,it.description)
            }
        })

        mActivitViewModel.getFatoratiStepThreeResponseListner.observe(this@FavoriteDetailFragment, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                showViews()
                mActivitViewModel.specialMenuBillSelected=false
                mActivitViewModel.refTxFatourati = it.refTxFatourati
                //mActivitViewModel.nomChamp = it.param.nomChamp
                mActivitViewModel.validatedParams.clear()
                mActivitViewModel.recievedParams.clear()
                for(i in it.params.indices){
                    var validatedParams = ValidatedParam("",it.params[i].nomChamp)
                    mActivitViewModel.validatedParams.add(validatedParams)
                    mActivitViewModel.recievedParams.add(
                        RecievededParam(it.params[i].libelle,it.params[i].nomChamp,it.params[i].typeChamp,"",
                        false,View.VISIBLE,"")
                    )
                }

                mFatoratiParamsItemAdapter = FatoratiParamsItemAdapter(mActivitViewModel.recievedParams,object :
                    FatoratiParamsItemAdapter.ParamTextChangedListner{
                    override fun onParamTextChangedClick(valChamp: String, position: Int) {
                        if(it.params[position].libelle.equals("CIL",false)){
                            applyValidation = true
                        } else {
                            applyValidation = false
                        }
                        mActivitViewModel.validatedParams.add(position,
                            ValidatedParam(valChamp,mActivitViewModel.recievedParams[position].nomChamp)
                        )
                    }
                })
                mDataBinding.mFieldsRecycler.apply {
                    adapter = mFatoratiParamsItemAdapter
                    layoutManager = LinearLayoutManager(activity)
                }

                Logger.debugLog("selectedFatouratiRefTx", mActivitViewModel.refTxFatourati)
                Logger.debugLog("selectedFatouratiNonCham", mActivitViewModel.nomChamp)
                (activity as FavoritesActivity).navController.navigate(R.id.action_favoriteDetailFragment_to_favoriteEnterContactFragment)
            }else{
                DialogUtils.showErrorDialoge(activity,it.description)
            }
        })

        mActivitViewModel.getFatoratiStepTwoThreeResponseListner.observe(this@FavoriteDetailFragment, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                showViews()
                mActivitViewModel.specialMenuBillSelected=false
                mActivitViewModel.refTxFatourati = it.refTxFatourati
                   //  mActivitViewModel.nomChamp = it.param.nomChamp

                Logger.debugLog("selectedFatouratiRefTx", mActivitViewModel.refTxFatourati)
                Logger.debugLog("selectedFatouratiNonCham", mActivitViewModel.nomChamp)
            }else{
                DialogUtils.showErrorDialoge(activity,it.description)
            }
        })

        mActivitViewModel.errorText.observe(this@FavoriteDetailFragment, Observer {
            DialogUtils.showErrorDialoge(requireActivity(),it)
        })
    }

    private fun subscribeForSpinnerListner() {

        // homeViewModel.requestForGetTransactionHistoryApi(activity,Constants.CURRENT_USER_MSISDN)
        mDataBinding.acountTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mDataBinding.inputPhoneNumber.isEnabled=false
                mDataBinding.inputLayoutPhoneNumber.hint = mActivitViewModel.creancesList.get()
                    ?.get(position)?.nomCreance
                mDataBinding.inputPhoneNumber.setText( mActivitViewModel.creancesList.get()
                    ?.get(position)?.codeCreance.toString())
                mActivitViewModel.specialMenuBillSelected=true

                Logger.debugLog("Abro","${mActivitViewModel.creancesList.get()
                    ?.get(position)?.nomCreance}  and  ${mActivitViewModel.creancesList.get()
                    ?.get(position)?.codeCreance}  selection ${mActivitViewModel.specialMenuBillSelected}  ")
            }
        }
    }

    private fun hideViews() {
        mDataBinding.inputLayoutPhoneNumber.isEnabled=false
        mDataBinding.acountTypeSpinner.visibility=View.VISIBLE
        mDataBinding.inputLayoutPhoneNumber.visibility=View.VISIBLE
        mDataBinding.selectPaymentTypeTitle.visibility=View.GONE
        mDataBinding.spinnerSelectPayment.visibility=View.GONE
        mDataBinding.selectBillTypeTitle.visibility=View.GONE
        mDataBinding.spinnerSelectBillType.visibility=View.GONE
    }
    private fun showViews() {
        mDataBinding.acountTypeSpinner.visibility=View.GONE
        mDataBinding.inputLayoutPhoneNumber.visibility=View.GONE
        mDataBinding.selectPaymentTypeTitle.visibility=View.VISIBLE
        mDataBinding.spinnerSelectPayment.visibility=View.VISIBLE
        mDataBinding.selectBillTypeTitle.visibility=View.VISIBLE
        mDataBinding.spinnerSelectBillType.visibility=View.VISIBLE
    }

    private fun setStrings() {
        mDataBinding.btnNext.text = LanguageData.getStringValue("BtnTitle_Next")
        mDataBinding.selectPaymentTypeTitle.text = LanguageData.getStringValue("SelectPaymentType")
        mDataBinding.selectBillTypeTitle.text = LanguageData.getStringValue("SelectBillType")
    }

    override fun onNextButtonClick(view: View) {
        if(mActivitViewModel.specialMenuBillSelected)
        {
            mActivitViewModel.requestForFatoratiStepThreeApi(activity,Constants.CURRENT_USER_MSISDN,mActivitViewModel.creancierID, mDataBinding.inputPhoneNumber.text.toString())
        }
        else{
            (activity as FavoritesActivity).navController.navigate(R.id.action_favoriteDetailFragment_to_favoriteEnterContactFragment)
        }

    }

    override fun onDeleteButtonClick(view: View) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        //Util_Redal@MyNickName,codeCreance,creancierID,nomChamp,refTxFatourati
        mActivitViewModel.fatoratiTypeSelected = mDataBinding.spinnerSelectBillType.selectedItem.toString()
        if(list_of_FatouratieType.isNotEmpty()){

            for(index in list_of_FatouratieType.indices){
                Logger.debugLog("abro", list_of_FatouratieType[index].nomCreancier)
                if(mActivitViewModel.fatoratiTypeSelected.equals(list_of_FatouratieType[index].nomCreancier)){
                    mActivitViewModel.codeCreance = list_of_FatouratieType[index].codeCreance
                    mActivitViewModel.creancierID = list_of_FatouratieType[index].codeCreancier

                    var isSelectedBillMatchedwithfatouratiSeperateMenuBillNames: Boolean = false
                    for (i in Constants.fatouratiSeperateMenuBillNames.indices) {

                        isSelectedBillMatchedwithfatouratiSeperateMenuBillNames =
                            mActivitViewModel.fatoratiTypeSelected.trim().toLowerCase().equals(Constants.fatouratiSeperateMenuBillNames[i].trim().toLowerCase())
                        if(isSelectedBillMatchedwithfatouratiSeperateMenuBillNames)
                        {
                            break
                        }
                    }

                    //fatouratiSeperateMenuBillNames
                    if(isSelectedBillMatchedwithfatouratiSeperateMenuBillNames)
                    {
                        mActivitViewModel.requestForFatoratiStepTwoApi(activity,Constants.CURRENT_USER_MSISDN,mActivitViewModel.creancierID)
                    }
                    else{
                        mActivitViewModel.requestForFatoratiStepTwoThreeApi(activity,Constants.CURRENT_USER_MSISDN,mActivitViewModel.creancierID)
                    }



                }
            }
        }
        Logger.debugLog("selectedFatourati", mActivitViewModel.fatoratiTypeSelected)
        Logger.debugLog("selectedFatouratiCodeCre", mActivitViewModel.codeCreance)
        Logger.debugLog("selectedFatouratiCreanID", mActivitViewModel.creancierID)
    }

}