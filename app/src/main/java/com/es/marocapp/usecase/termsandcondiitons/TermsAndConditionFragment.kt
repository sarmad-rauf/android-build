package com.es.marocapp.usecase.termsandcondiitons

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentTermsAndConditionBinding
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.favorites.FavoritesViewModel
import java.io.File

class TermsAndConditionFragment : BaseFragment<FragmentTermsAndConditionBinding>(){

    private lateinit var mAcitivtyViewModel : TermsAndConditionsVIewModel

    override fun setLayout(): Int {
        return R.layout.fragment_terms_and_condition
    }

    override fun init(savedInstanceState: Bundle?) {
        mAcitivtyViewModel = ViewModelProvider(this).get(TermsAndConditionsVIewModel::class.java)

        mDataBinding.apply {
            viewmodel = mAcitivtyViewModel
        }
        (activity as TermsAndConditions).setHeaderVisibilty(true)

        showPdfFromFile((activity as TermsAndConditions).downloadedFileFromURL)
    }

    private fun showPdfFromFile(file: File) {
        mDataBinding.pdfView.fromFile(file)
            .password(null)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .onPageError { page, _ ->
                Toast.makeText(
                    activity,
                    "Error at page: $page", Toast.LENGTH_LONG
                ).show()
            }
            .load()
    }
}