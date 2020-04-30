package com.es.marocapp.usecase.accountdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.es.marocapp.R
import com.es.marocapp.databinding.LayoutAccountDetailsBinding
import com.es.marocapp.usecase.BaseActivity

class AccountDetailsActivity : BaseActivity<LayoutAccountDetailsBinding>(){

    override fun init(savedInstanceState: Bundle?) {
        mDataBinding.apply {
        }

    }

    override fun setLayout(): Int {
        return R.layout.layout_account_details
    }

}
