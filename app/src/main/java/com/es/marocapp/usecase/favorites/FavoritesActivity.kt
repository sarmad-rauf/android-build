package com.es.marocapp.usecase.favorites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityFavoritesBinding
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.payments.PaymentsViewModel
import kotlinx.android.synthetic.main.layout_activity_header.view.*

class FavoritesActivity : BaseActivity<ActivityFavoritesBinding>() {

    lateinit var mActivityViewModel: FavoritesViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)

        mDataBinding.apply {
            viewmodel = mActivityViewModel

        }

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_favorites_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        mDataBinding.imgBackButton.setOnClickListener {
            if (mActivityViewModel.popBackStackTo == -1) {
                this@FavoritesActivity.finish()
            } else {
                navController.popBackStack(mActivityViewModel.popBackStackTo, false)
            }
        }

    }

    override fun setLayout(): Int {
        return R.layout.activity_favorites
    }

    fun setHeader(headerTitle : String){
        mDataBinding.tvFavoritesTitle.text = headerTitle
    }

}
