package com.es.marocapp.usecase.dashboard

import android.os.Bundle
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityMainBinding
import com.es.marocapp.usecase.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {


    override fun init(savedInstanceState: Bundle?) {

    }

    override fun setLayout(): Int {
        return R.layout.activity_main
    }
}
