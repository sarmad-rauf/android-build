package com.es.marocapp.usecase

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.accountdetails.AccountDetailsActivity
import com.es.marocapp.usecase.cashinviacard.ActivityCashInViaCard
import com.es.marocapp.usecase.changepassword.ChangePasswordActivity
import com.es.marocapp.usecase.favorites.FavoritesActivity
import com.es.marocapp.usecase.home.HomeFragment
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.qrcode.GenerateQrActivity
import com.es.marocapp.usecase.settings.SettingsActivity
import com.es.marocapp.usecase.termsandcondiitons.TermsAndConditions
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Tools
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.layout_side_menu_navigation.view.*
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.FocusShape


class MainActivity : BaseActivity<ActivityMainBinding>(), MainActivityClickListeners {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    lateinit var mActivityViewModel: MainActivityViewModel

    lateinit var homeFragment: HomeFragment

    var isDirectCallForTransaction = true
    var isTransactionFragmentNotVisible = true
    var showTransactionsDetailsIndirectly = false
    var isTransactionDetailsShowing = false

    var isGenerateQRFragmentShowing = false
    var isFaqsFragmentShowing = false
    var isSideMenuShowing = false
    var isTranactionDetailsFragmentShowing = false
    var isHomeFragmentShowing = false
    var isTransacitonFragmentShowing = false

    override fun setLayout(): Int {
        return R.layout.activity_main
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(this@MainActivity).get(MainActivityViewModel::class.java)

        mDataBinding.apply {
            listener = this@MainActivity
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
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

        mDataBinding.fab.setOnClickListener {
            if(Constants.isTutorialShowing){
                Constants.displayTuto(this@MainActivity,mDataBinding.fab,LanguageData.getStringValue("TransactionHistoryTutorial").toString())
            }else{
                if(!isTransacitonFragmentShowing){
                    onStatementClickLisnter(false)
                }else{
                    navController.popBackStack(R.id.navigation_home,false)
                    onStatementClickLisnter(false)
                }
            }
        }

        mDataBinding.dashboardCashInViaCard.setOnClickListener {
            if(Constants.isTutorialShowing){
                Constants.displayTuto(this@MainActivity,mDataBinding.callIconHomeScreen,LanguageData.getStringValue("CashInViaCardTutorial").toString(),
                R.drawable.ic_tutorial_home_cash_in_wallet)
            }else{
                startActivity(
                    Intent(
                        this@MainActivity,
                        ActivityCashInViaCard::class.java
                    )
                )
            }
        }
        mDataBinding.callIconHomeScreen.setOnClickListener {
            if(Constants.isTutorialShowing){
                Constants.displayTuto(this@MainActivity,mDataBinding.callIconHomeScreen,LanguageData.getStringValue("CallTutorial").toString())
            }else{
                Tools.openDialerWithNumber(this)
            }
        }

        mDataBinding.navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home-> {
                    //----------for handling Backpress of activity----------
                    isGenerateQRFragmentShowing = false
                    isFaqsFragmentShowing = false
                    isSideMenuShowing = false
                    isTranactionDetailsFragmentShowing = false
                    isHomeFragmentShowing = true
                    isTransacitonFragmentShowing = false

                    showTransactionsDetailsIndirectly = false
                    navController.navigate(R.id.navigation_home)
                    homeFragment.setTransacitonScreenVisisble(
                        isTransactionDetailsVisible = false,
                        directCallForTransaction = false,
                        transactionFragmentNotVisible = false
                    )

                }

                R.id.navigation_transaction-> {
                    navController.popBackStack(R.id.navigation_home,false)
                    navController.navigate(R.id.navigation_transaction)
                }
                R.id.FAQsFragment2-> {
                    navController.popBackStack(R.id.navigation_home,false)
                    navController.navigate(R.id.FAQsFragment2)
                }

                R.id.navigation_approval ->{
                    showTransactionsDetailsIndirectly = false
                    homeFragment.setTransacitonScreenVisisble(
                        isTransactionDetailsVisible = false,
                        directCallForTransaction = false,
                        transactionFragmentNotVisible = false
                    )
                    navController.popBackStack(R.id.navigation_home,false)
                    mDataBinding.drawerLayout.openDrawer(GravityCompat.START)
                }
            }
            false
        }

//        homeFragment = supportFragmentManager.findFragmentById(R.id.navigation_home) as HomeFragment

        /*val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        homeFragment = navFragment!!.childFragmentManager.primaryNavigationFragment as HomeFragment*/

        subscribeForUpdateLanguage()
        subscribeObserver()
        setStrings()
        setSideMenuStrings()
        setSideMenuListner()
        checkCashInViaCardFunctinalityAgainstUser()
    }

    private fun setSideMenuListner() {
        mDataBinding.navigationItem.nav_back_button.setOnClickListener {
            mDataBinding.drawerLayout.closeDrawers()
        }

        mDataBinding.navigationItem.rootView.statementsGroup.setOnClickListener{
            mDataBinding.drawerLayout.closeDrawers()
            onStatementClickLisnter(true)
        }

        mDataBinding.navigationItem.rootView.balanceAndAccountGroup.setOnClickListener {
            mDataBinding.drawerLayout.closeDrawers()
            startNewActivity(this@MainActivity, AccountDetailsActivity::class.java)
        }

        mDataBinding.navigationItem.rootView.cashInViaCardGroup.setOnClickListener {
            mDataBinding.drawerLayout.closeDrawers()
            startActivity(
                Intent(
                    this@MainActivity,
                    ActivityCashInViaCard::class.java
                )
            )
        }

        mDataBinding.navigationItem.rootView.changePasswordGroup.setOnClickListener {
            mDataBinding.drawerLayout.closeDrawers()
            startNewActivity(this@MainActivity, ChangePasswordActivity::class.java)
        }

        mDataBinding.navigationItem.rootView.beneficaryManagementGroup.setOnClickListener {
            mDataBinding.drawerLayout.closeDrawers()
            startActivity(Intent(this@MainActivity, FavoritesActivity::class.java))
        }

        mDataBinding.navigationItem.rootView.mtCashDefaulGroup.setOnClickListener {
            mDataBinding.drawerLayout.closeDrawers()
            startNewActivity(this@MainActivity, SettingsActivity::class.java)
        }

        mDataBinding.navigationItem.rootView.oppositionMTCashGroup.setOnClickListener {
            mDataBinding.drawerLayout.closeDrawers()

            val btnTxt = LanguageData.getStringValue("BtnTitle_Call")
            val titleTxt = LanguageData.getStringValue("BlockAccount")
            val descriptionTxt = LanguageData.getStringValue("CallToBlockAccount")?.replace("00000",
                Constants.HELPLINE_NUMBER)
            DialogUtils.showCustomDialogue(this,btnTxt,descriptionTxt,titleTxt,object : DialogUtils.OnCustomDialogListner{
                override fun onCustomDialogOkClickListner() {
                    Tools.openDialerWithNumber(this@MainActivity)
                }


            })
        }

        mDataBinding.navigationItem.rootView.changeLanguageGroup.setOnClickListener {
            mDataBinding.drawerLayout.closeDrawers()

            DialogUtils.showChangeLanguageDialogue(this,object : DialogUtils.OnChangeLanguageClickListner{

                override fun onChangeLanguageDialogYesClickListner(selectedLanguage: String) {
                    //Toast.makeText(this@SettingsActivity,selectedLanguage,Toast.LENGTH_LONG).show()
//                    mDataBinding.root.tvLanguage.text=selectedLanguage
                    LocaleManager.languageToBeChangedAfterAPI=selectedLanguage

                    var langParam=""
                    if(selectedLanguage.equals(LanguageData.getStringValue("DropDown_English"))){
                        langParam= LocaleManager.KEY_LANGUAGE_EN
                    }
                    else  if(selectedLanguage.equals(LanguageData.getStringValue("DropDown_French"))){
                        langParam= LocaleManager.KEY_LANGUAGE_FR
                    }

                    else  if(selectedLanguage.equals(LanguageData.getStringValue("DropDown_Arabic"))){
                        langParam= LocaleManager.KEY_LANGUAGE_AR
                    }

                    mActivityViewModel.requestForChangeLanguage(this@MainActivity,langParam)

                }

            })
        }

        mDataBinding.navigationItem.rootView.logOutGroup.setOnClickListener {
            val confirmationTxt =
                LanguageData.getStringValue("Message_WantToLogout")
            mDataBinding.drawerLayout.closeDrawer(GravityCompat.START)
            DialogUtils.showConfirmationDialogue(
                confirmationTxt!!,
                this@MainActivity,
                object : DialogUtils.OnConfirmationDialogClickListner {
                    override fun onDialogYesClickListner() {
                        mActivityViewModel.requestForLogOutUserApi(this@MainActivity)
                    }


                })
        }
    }

    fun onStatementClickLisnter(showTransactionFragment : Boolean){
        if(showTransactionFragment){
            isTransactionDetailsShowing = false
            if (isTransactionFragmentNotVisible) {
                if (isDirectCallForTransaction) {
                    navController.navigate(R.id.action_navigation_home_to_navigation_transaction)
                } else {
                    navController.navigateUp()
                    navController.navigate(R.id.action_navigation_home_to_navigation_transaction)
                }
            }

            //----------for handling Backpress of activity----------
            isGenerateQRFragmentShowing = false
            isFaqsFragmentShowing = false
            isSideMenuShowing = false
            isTranactionDetailsFragmentShowing = false
            isHomeFragmentShowing = true
            isTransacitonFragmentShowing = false

        }else{
            isTransactionDetailsShowing = true
            if(isDirectCallForTransaction){
                homeFragment.setTransacitonScreenVisisble(true,isDirectCallForTransaction,isTransactionFragmentNotVisible)
            }else{

                navController.popBackStack(R.id.navigation_home,false)
                homeFragment.setTransacitonScreenVisisble(true,isDirectCallForTransaction,isTransactionFragmentNotVisible)
            }

            //----------for handling Backpress of activity----------
            isGenerateQRFragmentShowing = false
            isFaqsFragmentShowing = false
            isSideMenuShowing = false
            isTranactionDetailsFragmentShowing = true
            isHomeFragmentShowing = false
            isTransacitonFragmentShowing = false
        }
    }

    private fun setSideMenuStrings() {
        mDataBinding.navigationItem.rootView.nav_title_name.text = LanguageData.getStringValue("MySpace")
        mDataBinding.navigationItem.rootView.personal_information_title.text = LanguageData.getStringValue("PersonalInformation")
        mDataBinding.navigationItem.rootView.nav_logged_in_user_name.text = Constants.balanceInfoAndResponse.firstname+" "+Constants.balanceInfoAndResponse.surname
        mDataBinding.navigationItem.rootView.nav_logged_in_user_detials.text = Constants.balanceInfoAndResponse.profilename
        mDataBinding.navigationItem.rootView.nav_logged_in_user_email.text = getUserEmailAddress()
        mDataBinding.navigationItem.rootView.complete_mt_title.text = LanguageData.getStringValue("MTCashAccount")
        mDataBinding.navigationItem.rootView.cashInViaCardTitle.text = LanguageData.getStringValue("CashInViaCard")
        mDataBinding.navigationItem.rootView.balanceAndAccountTitle.text = LanguageData.getStringValue("BalanceAndAccounts")
        mDataBinding.navigationItem.rootView.statementsTitle.text = LanguageData.getStringValue("Statements")
        mDataBinding.navigationItem.rootView.nav_third_container_title.text = LanguageData.getStringValue("ManageAccount")
        mDataBinding.navigationItem.rootView.changePasswordTitle.text = LanguageData.getStringValue("ChangePassword")
        mDataBinding.navigationItem.rootView.beneficaryManagementTitle.text = LanguageData.getStringValue("BeneficiaryManagement")
        mDataBinding.navigationItem.rootView.levelUpTitle.text = LanguageData.getStringValue("GoToLevel2")
        mDataBinding.navigationItem.rootView.mtCashDefaultTitle.text = LanguageData.getStringValue("MTCashWalletByDefault")
        mDataBinding.navigationItem.rootView.oppositionMTCashTitle.text = LanguageData.getStringValue("OppositionOnMyMWallet")
        mDataBinding.navigationItem.rootView.changeLanguageTitle.text = LanguageData.getStringValue("ChangeLanguage")
        mDataBinding.navigationItem.rootView.logOutTitle.text = LanguageData.getStringValue("LogOut")
    }

    fun getUserEmailAddress() : String{
        var email = ""
        if(!Constants.balanceInfoAndResponse.email.isNullOrEmpty()){
            email = Constants.balanceInfoAndResponse.email!!
            email = email.removePrefix("ID:")
            email = email.substringAfter(":")
            email = email.substringBefore("/")
        }
        return  email
    }

    private fun setStrings() {
        mDataBinding.toolbarName.text =
            "${LanguageData.getStringValue("Hi")} ${Constants.balanceInfoAndResponse.firstname}"

       /* mDataBinding.navigationHeader.drawer_header_name.text =
            "${LanguageData.getStringValue("Hi")} ${Constants.balanceInfoAndResponse.firstname} ${Constants.balanceInfoAndResponse.surname}"
        mDataBinding.navigationHeader.drawer_header_number.text = Constants.CURRENT_USER_MSISDN

        mDataBinding.textDrawerNotifications.text

        mDataBinding.textDrawerNotifications.text = LanguageData.getStringValue("Notifications")
        mDataBinding.textFAQs.text = LanguageData.getStringValue("Faqs")
        mDataBinding.textTermsandConditions.text = LanguageData.getStringValue("TermsAndConditions")
        mDataBinding.textClickToCall.text = LanguageData.getStringValue("ClickToCall")
        mDataBinding.textDrawerSettings.text = LanguageData.getStringValue("Settings")
        mDataBinding.textDrawerLogOut.text = LanguageData.getStringValue("LogOut")*/

        mDataBinding.toolbarWelcomeBack.text = LanguageData.getStringValue("WelcomeBack")

        val menu: Menu = mDataBinding.root.nav_view.getMenu()
        menu.get(0).title = LanguageData.getStringValue("HOME")
        menu.get(1).title = LanguageData.getStringValue("Transactions")
        menu.get(2).title = LanguageData.getStringValue("Password")
        menu.get(3).title = LanguageData.getStringValue("Approvals")

    }

    private fun subscribeObserver() {
        mActivityViewModel.getLogOutUserResponseListner.observe(this@MainActivity, Observer {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
//                PrefUtils.addString(this, PrefUtils.PreKeywords.PREF_KEY_USER_MSISDN,"")
                startNewActivityAndClear(this@MainActivity, LoginActivity::class.java)
            } else {
                DialogUtils.showErrorDialoge(this@MainActivity, it.description)
            }
        })
    }

    override fun onSideMenuDrawerIconClick(view: View) {
        showTransactionsDetailsIndirectly = false
        homeFragment.setTransacitonScreenVisisble(
            isTransactionDetailsVisible = false,
            directCallForTransaction = false,
            transactionFragmentNotVisible = false
        )
        mDataBinding.drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onDrawerMenuNotificationsClick(view: View) {
        Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show()
    }

    override fun onDrawerMenuSettingsClick(view: View) {
        startNewActivity(this@MainActivity, SettingsActivity::class.java)
    }

    override fun onDrawerMenuFavoritesClick(view: View) {
        startNewActivity(this@MainActivity, FavoritesActivity::class.java)
    }

    override fun onDrawerMenuContactUsClick(view: View) {
        mDataBinding.drawerLayout.closeDrawer(GravityCompat.START)
        var myBundle: Bundle = Bundle()
        myBundle.putString("title", LanguageData.getStringValue("ContactUs"))
        startNewActivity(this@MainActivity, TermsAndConditions::class.java, myBundle)
    }

    override fun onDrawerMenuFAQsClick(view: View) {
        mDataBinding.drawerLayout.closeDrawer(GravityCompat.START)


        var myBundle: Bundle = Bundle()
        myBundle.putString("title", LanguageData.getStringValue("Faqs"))
        startNewActivity(this@MainActivity, TermsAndConditions::class.java, myBundle)
    }

    override fun onDrawerMenuTermsAndConditionClick(view: View) {
        mDataBinding.drawerLayout.closeDrawer(GravityCompat.START)
        var myBundle: Bundle = Bundle()
        myBundle.putString("title", LanguageData.getStringValue("TermsAndConditions"))
        startNewActivity(this@MainActivity, TermsAndConditions::class.java, myBundle)
    }

    override fun onDrawerMenuClickToCallClick(view: View) {
        Tools.openDialerWithNumber(this@MainActivity)
    }

    override fun onDrawerMenuLogOutClick(view: View) {
        mDataBinding.drawerLayout.closeDrawer(GravityCompat.START)
        mActivityViewModel.requestForLogOutUserApi(this@MainActivity)
    }

    override fun onDrawerMenuGenerateQRClick(view: View) {
        startNewActivity(this@MainActivity, GenerateQrActivity::class.java)
    }

    override fun onAccountDetailClick(view: View) {
        startNewActivity(this@MainActivity, AccountDetailsActivity::class.java)
    }

    fun setHomeToolbarVisibility(isVisible: Boolean) {
        if (isVisible) {
            mDataBinding.toolbarContainer.visibility = View.VISIBLE
        } else {
            mDataBinding.toolbarContainer.visibility = View.GONE
        }
    }

    fun checkCashInViaCardFunctinalityAgainstUser() {
        if (Constants.loginWithCertResponse.allowedMenu.CashInViaCard != null) {
            mDataBinding.dashboardCashInViaCard.visibility = View.VISIBLE
            mDataBinding.navigationItem.rootView.cashInViaCardGroup.visibility = View.VISIBLE
        }else{
            mDataBinding.dashboardCashInViaCard.visibility = View.GONE
            mDataBinding.navigationItem.rootView.cashInViaCardGroup.visibility = View.GONE
        }
    }

    private fun subscribeForUpdateLanguage() {

        mActivityViewModel.updateLanguageResponseListener.observe(this,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if(LocaleManager.languageToBeChangedAfterAPI.equals(LanguageData.getStringValue("DropDown_English"))){

                        LocaleManager.setLanguageAndUpdate(this@MainActivity,
                            LocaleManager.KEY_LANGUAGE_EN,
                            MainActivity::class.java)
                    }
                    else if(LocaleManager.languageToBeChangedAfterAPI.equals(LanguageData.getStringValue("DropDown_French"))) {

                        LocaleManager.setLanguageAndUpdate(this@MainActivity,
                            LocaleManager.KEY_LANGUAGE_FR,
                            MainActivity::class.java)
                    }
                    else if(LocaleManager.languageToBeChangedAfterAPI.equals(LanguageData.getStringValue("DropDown_Arabic"))) {

                        LocaleManager.setLanguageAndUpdate(this@MainActivity,
                            LocaleManager.KEY_LANGUAGE_AR,
                            MainActivity::class.java)
                    }
                    // DialogUtils.successFailureDialogue(this@SettingsActivity,it.description,0)
                }else{
                    DialogUtils.successFailureDialogue(this@MainActivity,it.description,1)
                }
            }
        )
    }

    override fun onBackPressed() {
        /*showTransactionsDetailsIndirectly = false
        if(mDataBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            mDataBinding.drawerLayout.closeDrawers()
        }else if(isTransactionDetailsShowing){
            showTransactionsDetailsIndirectly = false
            homeFragment.setTransacitonScreenVisisble(
                isTransactionDetailsVisible = false,
                directCallForTransaction = false,
                transactionFragmentNotVisible = false
            )
        }else{
            super.onBackPressed()
        }*/
        /*isGenerateQRFragmentShowing
        isFaqsFragmentShowing
        isSideMenuShowing
        isTranactionDetailsFragmentShowing
        isHomeFragmentShowing
        isTransacitonFragmentShowing*/
        showTransactionsDetailsIndirectly = false
        if(mDataBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            mDataBinding.drawerLayout.closeDrawers()
        }else if(isTranactionDetailsFragmentShowing){
            showTransactionsDetailsIndirectly = false
            homeFragment.setTransacitonScreenVisisble(
                isTransactionDetailsVisible = false,
                directCallForTransaction = false,
                transactionFragmentNotVisible = false
            )
            isGenerateQRFragmentShowing = false
            isFaqsFragmentShowing = false
            isSideMenuShowing = false
            isTranactionDetailsFragmentShowing = false
            isHomeFragmentShowing = true
            isTransacitonFragmentShowing = false

        }else if(isHomeFragmentShowing){
            this@MainActivity.finish()
        }else if(isFaqsFragmentShowing || isGenerateQRFragmentShowing || isHomeFragmentShowing || isTransacitonFragmentShowing){
            super.onBackPressed()
        }
    }

}
