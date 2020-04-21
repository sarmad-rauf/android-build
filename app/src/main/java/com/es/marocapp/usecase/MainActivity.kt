package com.es.marocapp.usecase

import android.os.Bundle
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.es.marocapp.adapter.DuoSimpleAdapter
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle


class MainActivity : BaseActivity<ActivityMainBinding>(), DuoMenuView.OnMenuClickListener {

    private lateinit var mMenuAdapter: DuoSimpleAdapter

    private var mNavigationViewTitles = ArrayList<String>()
    private var mNavigationViewIcons = ArrayList<Int>()
    private lateinit var mDuoMenuView: DuoMenuView

    override fun init(savedInstanceState: Bundle?) {

        mNavigationViewTitles = ArrayList(
            listOf(
                *resources.getStringArray(R.array.navigation_drawer_options)
            )
        )
        mNavigationViewIcons = ArrayList(
            listOf(
                R.drawable.ic_notifications,R.drawable.ic_settings,R.drawable.login_btn
            )
        )

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

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

        // Handle menu actions
        handleMenu()

        // Handle drawer actions
        handleDrawer()

    }

    private fun handleDrawer() {
        val drawerToggle = DuoDrawerToggle(
            this, mDataBinding.drawer, mDataBinding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        mDataBinding.drawer.setDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    private fun handleMenu() {
        mMenuAdapter = DuoSimpleAdapter(mNavigationViewTitles,mNavigationViewIcons)
        mDuoMenuView = mDataBinding.drawer.menuView as DuoMenuView
        mDuoMenuView.setOnMenuClickListener(this)
        mDuoMenuView.adapter = mMenuAdapter
    }

    override fun setLayout(): Int {
        return R.layout.activity_main
    }

    override fun onOptionClicked(position: Int, objectClicked: Any?) {

        // Set the toolbar title
        title = mNavigationViewTitles[position]

        // Set the right options selected
//        mMenuAdapter.setViewSelected(position, true)

        // Navigate to the right fragment
        when (position) {
            0 -> Toast.makeText(this, "NotificationsClicked", Toast.LENGTH_SHORT).show()
            1 -> Toast.makeText(this, "SettingsClicked", Toast.LENGTH_SHORT).show()
            2 -> Toast.makeText(this, "LogOutClicked", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, "NotingClicked", Toast.LENGTH_SHORT).show()


        }

        // Close the drawer
        mDataBinding.drawer.closeDrawer()
    }

    override fun onHeaderClicked() {
        Toast.makeText(this, "onHeaderClicked", Toast.LENGTH_SHORT).show()
    }

    override fun onFooterClicked() {
        Toast.makeText(this, "onFooterClicked", Toast.LENGTH_SHORT).show()
    }
}
