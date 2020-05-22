package com.es.marocapp.usecase.termsandcondiitons

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityTermsAndConditionsBinding
import com.es.marocapp.usecase.BaseActivity

class TermsAndConditions : BaseActivity<ActivityTermsAndConditionsBinding>() {

    private lateinit var mActivityViewModel : TermsAndConditionsVIewModel

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(TermsAndConditionsVIewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        var headerText = intent.getStringExtra("title")
        mDataBinding.tvTermConditionTitle.text = headerText
    }

    override fun setLayout(): Int {
        return R.layout.activity_terms_and_conditions
    }

}
