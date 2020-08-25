package com.es.marocapp.usecase.approvals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityApprovalBinding
import com.es.marocapp.usecase.BaseActivity

class ApprovalActivity : BaseActivity<ActivityApprovalBinding>(){

    private lateinit var mActivityViewModel : ApprovalViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    override fun setLayout(): Int {
        return R.layout.activity_approval
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this@ApprovalActivity).get(ApprovalViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_approval_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

    }

}
