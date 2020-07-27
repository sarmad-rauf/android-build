package com.es.marocapp.usecase

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityMainBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.accountdetails.AccountDetailsActivity
import com.es.marocapp.usecase.favorites.FavoritesActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.qrcode.GenerateQrActivity
import com.es.marocapp.usecase.settings.SettingsActivity
import com.es.marocapp.usecase.termsandcondiitons.TermsAndConditions
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.PrefUtils
import com.es.marocapp.utils.Tools
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.layout_drawer_header.view.*


class MainActivity : BaseActivity<ActivityMainBinding>(), MainActivityClickListeners {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    lateinit var mActivityViewModel: MainActivityViewModel

    override fun setLayout(): Int {
        return R.layout.activity_main
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this@MainActivity).get(MainActivityViewModel::class.java)

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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        subscribeObserver()
        setStrings()
    }

    private fun setStrings() {
        mDataBinding.toolbarName.text = "${LanguageData.getStringValue("Hi")} ${Constants.balanceInfoAndResponse.firstname} ${Constants.balanceInfoAndResponse.surname}"

        mDataBinding.navigationHeader.drawer_header_name.text = "${LanguageData.getStringValue("Hi")} ${Constants.balanceInfoAndResponse.firstname} ${Constants.balanceInfoAndResponse.surname}"
        mDataBinding.navigationHeader.drawer_header_number.text=Constants.CURRENT_USER_MSISDN

        mDataBinding.textDrawerNotifications.text

        mDataBinding.textDrawerNotifications.text = LanguageData.getStringValue("Notifications")
        mDataBinding.textFAQs.text = LanguageData.getStringValue("Faqs")
        mDataBinding.textTermsandConditions.text = LanguageData.getStringValue("TermsAndConditions")
        mDataBinding.textClickToCall.text = LanguageData.getStringValue("ClickToCall")
        mDataBinding.textDrawerSettings.text = LanguageData.getStringValue("Settings")
        mDataBinding.textDrawerLogOut.text = LanguageData.getStringValue("LogOut")

        mDataBinding.toolbarWelcomeBack.text = LanguageData.getStringValue("WelcomeBack")

        val menu: Menu = mDataBinding.root.nav_view.getMenu()
        menu.get(0).title=LanguageData.getStringValue("HOME")
        menu.get(1).title=LanguageData.getStringValue("Transactions")
        menu.get(2).title=LanguageData.getStringValue("Password")
        menu.get(3).title=LanguageData.getStringValue("Approvals")

    }

    private fun subscribeObserver() {
        mActivityViewModel.getLogOutUserResponseListner.observe(this@MainActivity, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
//                PrefUtils.addString(this, PrefUtils.PreKeywords.PREF_KEY_USER_MSISDN,"")
                startNewActivityAndClear(this@MainActivity,LoginActivity::class.java)
            }else{
               DialogUtils.showErrorDialoge(this@MainActivity,it.description)
            }
        })
    }

    override fun onSideMenuDrawerIconClick(view: View) {
        mDataBinding.drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onDrawerMenuNotificationsClick(view: View) {
        Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show()
    }

    override fun onDrawerMenuSettingsClick(view: View) {
        startNewActivity(this@MainActivity,SettingsActivity::class.java)
    }

    override fun onDrawerMenuFavoritesClick(view: View) {
        startNewActivity(this@MainActivity,FavoritesActivity::class.java)
    }

    override fun onDrawerMenuContactUsClick(view: View) {
        mDataBinding.drawerLayout.closeDrawer(GravityCompat.START)
        var myBundle : Bundle = Bundle()
        myBundle.putString("title",LanguageData.getStringValue("ContactUs"))
        startNewActivity(this@MainActivity,TermsAndConditions::class.java,myBundle)
    }

    override fun onDrawerMenuFAQsClick(view: View) {
        mDataBinding.drawerLayout.closeDrawer(GravityCompat.START)


        var myBundle : Bundle = Bundle()
        myBundle.putString("title",LanguageData.getStringValue("Faqs"))
        startNewActivity(this@MainActivity,TermsAndConditions::class.java,myBundle)
    }

    override fun onDrawerMenuTermsAndConditionClick(view: View) {
        mDataBinding.drawerLayout.closeDrawer(GravityCompat.START)
        var myBundle : Bundle = Bundle()
        myBundle.putString("title",LanguageData.getStringValue("TermsAndConditions"))
        startNewActivity(this@MainActivity,TermsAndConditions::class.java,myBundle)
    }

    override fun onDrawerMenuClickToCallClick(view: View) {
       Tools.openDialerWithNumber(this@MainActivity)
    }

    override fun onDrawerMenuLogOutClick(view: View) {
        mDataBinding.drawerLayout.closeDrawer(GravityCompat.START)
        mActivityViewModel.requestForLogOutUserApi(this@MainActivity)
    }

    override fun onDrawerMenuGenerateQRClick(view: View) {
        startNewActivity(this@MainActivity,GenerateQrActivity::class.java)
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
