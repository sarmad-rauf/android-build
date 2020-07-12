package com.es.marocapp.usecase.favorites

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFavoriteDetailsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment


class FavoriteDetailFragment : BaseFragment<FragmentFavoriteDetailsBinding>(),
    FavoritesPaymentClickListener {

    private lateinit var mActivitViewModel: FavoritesViewModel
    private var list_of_paymentType = arrayOf("Payment","Payment 1", "Payment 2", "Payment 3")
    private var list_of_billType = arrayOf("Bill","Bill 1", "Bill 2", "Bill 3")

    override fun setLayout(): Int {
        return R.layout.fragment_favorite_details
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivitViewModel = ViewModelProvider(activity as FavoritesActivity).get(FavoritesViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivitViewModel
            listener = this@FavoriteDetailFragment
        }

        val adapterPaymentType = ArrayAdapter<CharSequence>(activity as FavoritesActivity, R.layout.layout_favorites_spinner_text, list_of_paymentType)
        mDataBinding.spinnerSelectPayment.apply {
            adapter = adapterPaymentType
        }

        val adapterBillType = ArrayAdapter<CharSequence>(activity as FavoritesActivity, R.layout.layout_favorites_spinner_text, list_of_billType)
        mDataBinding.spinnerSelectBillType.apply {
            adapter = adapterBillType
        }


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

}