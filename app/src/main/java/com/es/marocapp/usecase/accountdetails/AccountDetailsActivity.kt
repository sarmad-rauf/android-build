package com.es.marocapp.usecase.accountdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.LayoutAccountDetailsBinding
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.login.LoginActivityViewModel
import com.es.marocapp.utils.Constants

class AccountDetailsActivity : BaseActivity<LayoutAccountDetailsBinding>(){

    lateinit var mActivityViewModel: AccoutDetailsViewModel

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this@AccountDetailsActivity).get(AccoutDetailsViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        var respone = mActivityViewModel.mBalanceInforAndResponseObserver.get()

        mDataBinding.accountDetailName.text = respone?.firstname+" "+respone?.surname+"!"
        mDataBinding.accountDetailAccountNum.text = Constants.CURRENT_USER_MSISDN
        mDataBinding.accountDetailCurrentBalance.text = respone?.currnecy+" "+respone?.balance
        mDataBinding.appNameTitleSenRevContainer.text = respone?.profilename

        //TODO add Reycler IN View and POpulate according to list
        if(!respone?.limitsList.isNullOrEmpty()){
            mDataBinding.tvYearlySendingLimitTitle.text = respone?.limitsList?.get(0)?.name
            mDataBinding.tvYearlySendingLimitVal.text = respone?.currnecy +" "+respone?.limitsList?.get(0)?.threshhold
        }

    }

    override fun setLayout(): Int {
        return R.layout.layout_account_details
    }

}
