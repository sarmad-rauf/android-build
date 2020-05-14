package com.es.marocapp.usecase.favorites

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentFavoriteDetailsBinding
import com.es.marocapp.usecase.BaseFragment


class FavoriteDetailFragment : BaseFragment<FragmentFavoriteDetailsBinding>(),
    FavoritesPaymentClickListener {

    private lateinit var mActivitViewModel: FavoritesViewModel
    private var list_of_paymentType = arrayOf("Payment","Payment 1", "Payment 2", "Payment 3")
    private var list_of_billType = arrayOf("Bill","Bill 1", "Bill 2", "Bill 3")
    private var list_of_companyType = arrayOf("Company","Company 1", "Company 2", "Company 3")

    override fun setLayout(): Int {
        return R.layout.fragment_favorite_details
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivitViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)

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

        val adapterCompanyType = ArrayAdapter<CharSequence>(activity as FavoritesActivity, R.layout.layout_favorites_spinner_text, list_of_companyType)
        mDataBinding.spinnerSelectCompany.apply {
            adapter = adapterCompanyType
        }

    }

    override fun onNextButtonClick(view: View) {
        (activity as FavoritesActivity).navController.navigate(R.id.action_favoriteDetailFragment_to_favoriteEnterContactFragment)
    }

}