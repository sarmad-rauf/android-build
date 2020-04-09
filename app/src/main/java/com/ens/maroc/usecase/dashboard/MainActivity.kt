package com.ens.maroc.usecase.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ens.maroc.R
import com.ens.maroc.databinding.ActivityMainBinding
import com.ens.maroc.usecase.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {


    override fun init(savedInstanceState: Bundle?) {

    }

    override fun setLayout(): Int {
        return R.layout.activity_main
    }
}
