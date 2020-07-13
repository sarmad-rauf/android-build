package com.es.marocapp.usecase.favorites

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFavoriteDetailsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment


class FavoriteDetailFragment : BaseFragment<FragmentFavoriteDetailsBinding>(),
    FavoritesPaymentClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var mActivitViewModel: FavoritesViewModel
    private var list_of_paymentType = arrayOf((LanguageData.getStringValue("Fatourati").toString()))
    private var list_of_billType: ArrayList<String> = arrayListOf()


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
        mActivitViewModel.fatoratiTypeSelected = mDataBinding.spinnerSelectBillType.selectedItem.toString()
    }

}