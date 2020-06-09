package com.es.marocapp.usecase.termsandcondiitons

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityTermsAndConditionsBinding
import com.es.marocapp.usecase.BaseActivity

class TermsAndConditions : BaseActivity<ActivityTermsAndConditionsBinding>() {

    private lateinit var mActivityViewModel : TermsAndConditionsVIewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    lateinit var navGraph: NavGraph

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(TermsAndConditionsVIewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        var headerText = intent.getStringExtra("title")
        mDataBinding.tvTermConditionTitle.text = headerText

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_termandcondition_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navGraph = navController.navInflater.inflate(R.navigation.terms_and_condition_nav_graph)

        if(headerText == "FAQs"){
            navGraph.startDestination = R.id.FAQsFragment
        }else if(headerText == "Term & Conditions"){
            navGraph.startDestination = R.id.termsAndConditionFragment
        }else if(headerText == "Contact Us"){
            navGraph.startDestination = R.id.contactUsFragment
        }else{
            navGraph.startDestination = R.id.termsAndConditionFragment
        }

        navController.setGraph(navGraph)
    }

    override fun setLayout(): Int {
        return R.layout.activity_terms_and_conditions
    }

}
