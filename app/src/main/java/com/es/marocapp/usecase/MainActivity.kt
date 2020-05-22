package com.es.marocapp.usecase

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityMainBinding
import com.es.marocapp.usecase.accountdetails.AccountDetailsActivity
import com.es.marocapp.usecase.favorites.FavoritesActivity
import com.es.marocapp.usecase.termsandcondiitons.TermsAndConditions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics


class MainActivity : BaseActivity<ActivityMainBinding>(), MainActivityClickListeners {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    override fun init(savedInstanceState: Bundle?) {

        mDataBinding.apply {
            listener = this@MainActivity
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_transaction,
                R.id.navigation_pin,
                R.id.navigation_approval
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    override fun setLayout(): Int {
        return R.layout.activity_main
    }


    override fun onSideMenuDrawerIconClick(view: View) {
        mDataBinding.drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onDrawerMenuNotificationsClick(view: View) {
        Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onDrawerMenuSettingsClick(view: View) {
        Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
    }

    override fun onDrawerMenuFavoritesClick(view: View) {
        startNewActivity(this@MainActivity,FavoritesActivity::class.java)
    }

    override fun onDrawerMenuContactUsClick(view: View) {
        var myBundle : Bundle = Bundle()
        myBundle.putString("title","Contact Us")
        startNewActivity(this@MainActivity,TermsAndConditions::class.java,myBundle)
    }

    override fun onDrawerMenuFAQsClick(view: View) {
        var myBundle : Bundle = Bundle()
        myBundle.putString("title","FAQs")
        startNewActivity(this@MainActivity,TermsAndConditions::class.java,myBundle)
    }

    override fun onDrawerMenuTermsAndConditionClick(view: View) {
        var myBundle : Bundle = Bundle()
        myBundle.putString("title","Term & Conditions")
        startNewActivity(this@MainActivity,TermsAndConditions::class.java,myBundle)
    }

    override fun onDrawerMenuLogOutClick(view: View) {
        Toast.makeText(this, "Log Out clicked", Toast.LENGTH_SHORT).show()

    }

    override fun onAccountDetailClick(view: View) {
        startNewActivity(this@MainActivity,AccountDetailsActivity::class.java)
    }

    fun setHomeToolbarVisibility(isVisible : Boolean){
        if(isVisible){
            mDataBinding.toolbarContainer.visibility = View.VISIBLE
        }else{
            mDataBinding.toolbarContainer.visibility = View.GONE
        }
    }

}
