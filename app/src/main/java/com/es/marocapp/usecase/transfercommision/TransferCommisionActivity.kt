package com.es.marocapp.usecase.updateprofle

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityTransferCommisionBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.transfercommision.TransferCommisionViewModel
import kotlinx.android.synthetic.main.activity_transfer_commision.*
import kotlinx.android.synthetic.main.activity_update_profile.*
import kotlinx.android.synthetic.main.activity_update_profile.headerUpdateProfle
import kotlinx.android.synthetic.main.layout_activity_header.view.*

class TransferCommisionActivity : BaseActivity<ActivityTransferCommisionBinding>() {

    lateinit var transferCommisionViewModel: TransferCommisionViewModel
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment

    override fun setLayout(): Int {
        return R.layout.activity_transfer_commision
    }

    override fun init(savedInstanceState: Bundle?) {
        transferCommisionViewModel = ViewModelProvider(this@TransferCommisionActivity).get(TransferCommisionViewModel::class.java)
        mDataBinding.apply { this.viewmodel=transferCommisionViewModel }
        navHostFragment = supportFragmentManager.findFragmentById(R.id.transfer_commision_host_fragment) as NavHostFragment
        navController=navHostFragment.navController
        mDataBinding.root.activityHeaderBack.setOnClickListener {
            if (transferCommisionViewModel.popBackStackTo == -1) {
                this@TransferCommisionActivity.finish()
            } else {
                navController.popBackStack(transferCommisionViewModel.popBackStackTo, false)
            }
        }
        setHeaderChangeObserver()
        LanguageData.getStringValue("TransferCommission")?.let { transferCommisionViewModel.setHeaderText(it) }
    }


    private fun setHeaderChangeObserver() {
        transferCommisionViewModel.headerTitle.observe(this@TransferCommisionActivity,Observer {
            headerTransferCommision.rootView.activityHeaderTitle.text=it
        })
    }


}