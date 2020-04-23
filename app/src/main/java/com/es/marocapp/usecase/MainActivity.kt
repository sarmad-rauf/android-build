package com.es.marocapp.usecase

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class MainActivity : BaseActivity<ActivityMainBinding>(), MainActivityClickListeners {

    override fun init(savedInstanceState: Bundle?) {

        mDataBinding.apply {
            listener = this@MainActivity
        }

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

    override fun onDrawerMenuLogOutClick(view: View) {
        Toast.makeText(this, "Log Out clicked", Toast.LENGTH_SHORT).show()

    }
}
