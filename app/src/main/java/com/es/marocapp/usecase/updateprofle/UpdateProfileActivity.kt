package com.es.marocapp.usecase.updateprofle

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityUpdateProfileBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseActivity
import kotlinx.android.synthetic.main.activity_update_profile.*
import kotlinx.android.synthetic.main.layout_activity_header.view.*

class UpdateProfileActivity : BaseActivity<ActivityUpdateProfileBinding>() {

    lateinit var updateProfileViewModel: UpdateProfileViewModel
    lateinit var navConroller: NavController
    lateinit var navHostFragment: NavHostFragment

    override fun setLayout(): Int {
        return R.layout.activity_update_profile
    }

    override fun init(savedInstanceState: Bundle?) {
        updateProfileViewModel = ViewModelProvider(this).get(UpdateProfileViewModel::class.java)
        mDataBinding.apply { viewmodel = updateProfileViewModel }
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_update_profile_host_fragment) as NavHostFragment
        navConroller = navHostFragment.navController
        mDataBinding.root.activityHeaderBack.setOnClickListener {
            if (updateProfileViewModel.popBackStackTo == -1) {
                this.finish()
            } else {
                navConroller.popBackStack(updateProfileViewModel.popBackStackTo, false)
            }
        }
        setHeaderChangeObserver()
        LanguageData.getStringValue("UpdateProfile")
            ?.let { updateProfileViewModel.setHeaderText(it) }
    }


    private fun setHeaderChangeObserver() {
        updateProfileViewModel.headerTitle.observe(this, Observer {
            headerUpdateProfle.rootView.activityHeaderTitle.text = it
        })
    }


}