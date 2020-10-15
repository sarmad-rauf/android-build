package com.es.marocapp.usecase.termsandcondiitons

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.es.marocapp.R
import com.es.marocapp.adapter.FaqsQuestionAnswerAdapter
import com.es.marocapp.databinding.FragmentFaqsBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.model.FaqsAnswers
import com.es.marocapp.model.FaqsQuestionModel
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.utils.DialogUtils
import java.io.File

class FAQsFragment : BaseFragment<FragmentFaqsBinding>(){

    private lateinit var mAcitivtyViewModel : TermsAndConditionsVIewModel

    private  var mFaqList : ArrayList<FaqsQuestionModel> = arrayListOf()

    private  var mFaqAnwer : ArrayList<FaqsAnswers> = arrayListOf()

    private lateinit var mFaqAdapter : FaqsQuestionAnswerAdapter


    override fun setLayout(): Int {
        return R.layout.fragment_faqs
    }

    override fun init(savedInstanceState: Bundle?) {
        mAcitivtyViewModel = ViewModelProvider(this).get(TermsAndConditionsVIewModel::class.java)

        mDataBinding.apply {
            viewmodel = mAcitivtyViewModel
        }

        (activity as MainActivity).setHomeToolbarVisibility(false)
        (activity as MainActivity).isDirectCallForTransaction = false
        (activity as MainActivity).isTransactionFragmentNotVisible = true
        (activity as MainActivity).showTransactionsDetailsIndirectly = true
        mAcitivtyViewModel.requestForGetFaqs(activity)
        mDataBinding.tvTransactionHistoryTitle.text = LanguageData.getStringValue("Faqs")
        mDataBinding.imgBackButton.setOnClickListener {
            (activity as MainActivity).showTransactionsDetailsIndirectly = false
            (activity as MainActivity).navController.popBackStack(R.id.navigation_home,false)
        }
        subsribeObserver()
    }

    private fun subsribeObserver() {
        mAcitivtyViewModel.getFaqsResponseLisnter.observe(this@FAQsFragment, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                for(i in it.faqList.indices){
                    var answerList = arrayListOf<FaqsAnswers>()
                    if(LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_EN)){
                        answerList.add(FaqsAnswers(it.faqList[i].answerEN))
                        mFaqList.add(FaqsQuestionModel(it.faqList[i].questionEN,answerList))
                    }else if(LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_AR)){
                        answerList.add(FaqsAnswers(it.faqList[i].answerAR))
                        mFaqList.add(FaqsQuestionModel(it.faqList[i].questionAR,answerList))
                    }
                    else if(LocaleManager.selectedLanguage.equals(LocaleManager.KEY_LANGUAGE_FR)){
                        answerList.add(FaqsAnswers(it.faqList[i].answerFR))
                        mFaqList.add(FaqsQuestionModel(it.faqList[i].questionFR,answerList))
                    }
                }

                mFaqAdapter = FaqsQuestionAnswerAdapter(activity as MainActivity,mFaqList)

                mDataBinding.faqRecycler.apply {
                    layoutManager = LinearLayoutManager(activity as MainActivity)
                    adapter = mFaqAdapter
                }

            }else{
                DialogUtils.showErrorDialoge(activity,it.description)
            }
        })

        mAcitivtyViewModel.errorText.observe(this@FAQsFragment, Observer {
            DialogUtils.showErrorDialoge(activity,it)
        })
    }

    private fun showPdfFromFile(file: File) {
       /* mDataBinding.pdfView.fromFile(file)
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
            .load()*/
    }

}