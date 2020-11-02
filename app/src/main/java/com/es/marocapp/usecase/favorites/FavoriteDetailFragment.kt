package com.es.marocapp.usecase.favorites

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFavoriteDetailsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.responses.Creancier
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger


class FavoriteDetailFragment : BaseFragment<FragmentFavoriteDetailsBinding>(),
    FavoritesPaymentClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var mActivitViewModel: FavoritesViewModel
    private var list_of_paymentType = arrayOf((LanguageData.getStringValue("Fatourati").toString()))
    private var list_of_billType: ArrayList<String> = arrayListOf()
    private var list_of_FatouratieType : ArrayList<Creancier> = arrayListOf()

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

        subscribeObserver()

    }

    private fun subscribeObserver() {
        mActivitViewModel.getFatoratiStepTwoResponseListner.observe(this@FavoriteDetailFragment, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                mActivitViewModel.refTxFatourati = it.refTxFatourati
                mActivitViewModel.nomChamp = it.param.nomChamp

                Logger.debugLog("selectedFatouratiRefTx", mActivitViewModel.refTxFatourati)
                Logger.debugLog("selectedFatouratiNonCham", mActivitViewModel.nomChamp)
            }else{
                DialogUtils.showErrorDialoge(activity,it.description)
            }
        })

        mActivitViewModel.errorText.observe(this@FavoriteDetailFragment, Observer {
            DialogUtils.showErrorDialoge(activity!!,it)
        })
    }

    private fun setStrings() {
        mDataBinding.btnNext.text = LanguageData.getStringValue("BtnTitle_Next")
        mDataBinding.selectPaymentTypeTitle.text = LanguageData.getStringValue("SelectPaymentType")
        mDataBinding.selectBillTypeTitle.text = LanguageData.getStringValue("SelectBillType")
    }

    override fun onNextButtonClick(view: View) {
        (activity as FavoritesActivity).navController.navigate(R.id.action_favoriteDetailFragment_to_favoriteEnterContactFragment)
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
                if(mActivitViewModel.fatoratiTypeSelected.equals(list_of_FatouratieType[index].nomCreancier)){
                    mActivitViewModel.codeCreance = list_of_FatouratieType[index].codeCreance
                    mActivitViewModel.creancierID = list_of_FatouratieType[index].codeCreancier

                    mActivitViewModel.requestForFatoratiStepTwoApi(activity,Constants.CURRENT_USER_MSISDN,mActivitViewModel.creancierID)
                }
            }
        }
        Logger.debugLog("selectedFatourati", mActivitViewModel.fatoratiTypeSelected)
        Logger.debugLog("selectedFatouratiCodeCre", mActivitViewModel.codeCreance)
        Logger.debugLog("selectedFatouratiCreanID", mActivitViewModel.creancierID)
    }

}