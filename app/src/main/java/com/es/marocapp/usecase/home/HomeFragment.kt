package com.es.marocapp.usecase.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.es.marocapp.R
import com.es.marocapp.adapter.HomeCardAdapter
import com.es.marocapp.adapter.HomeUseCasesAdapter
import com.es.marocapp.databinding.FragmentHomeBinding
import com.es.marocapp.model.HomeUseCasesModel
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.cashservices.CashServicesActivity
import com.es.marocapp.usecase.payments.PaymentsActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils

class HomeFragment : BaseFragment<FragmentHomeBinding>(), ViewPager.OnPageChangeListener,
    HomeFragmentClickListners {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mCardAdapter: HomeCardAdapter
    private lateinit var mUseCasesAdapter: HomeUseCasesAdapter
    var referenceNumber="";

    override fun setLayout(): Int {
        return R.layout.fragment_home
    }

    override fun init(savedInstanceState: Bundle?) {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        mDataBinding.apply {
            viewmodel = homeViewModel
            listener = this@HomeFragment
        }

        homeViewModel.text.observe(this, Observer {
        })

        (activity as MainActivity).setHomeToolbarVisibility(true)

        populateHomeCardView()
        populateHomeUseCase()

        if(Constants.IS_CONSUMER_USER || Constants.IS_MERCHANT_USER){
        homeViewModel.requestForAccountHolderAddtionalInformationApi(context)
        subscribeForDefaultAccountStatus()
        subscribeForSetDefaultAccountStatus()
        subscribeForVerifyOTPForSetDefaultAccountStatus()
        }
    }

    private fun subscribeForDefaultAccountStatus() {

        homeViewModel.getAccountHolderAdditionalInfoResponseListner.observe(this@HomeFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    if (it.additionalinformation.isNullOrEmpty()) {
                        showPopUp()
                    } else {
                        if (it.additionalinformation[0].value.equals("FALSE", true)) {
                            showPopUp()
                        }
                    }
                }else{
                    DialogUtils.showErrorDialoge(activity ,it.description)
                }
            }
        )
    }

    private fun subscribeForSetDefaultAccountStatus() {

        homeViewModel.setDefaultAccountResponseListener.observe(this@HomeFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    referenceNumber=it.referenceNumber
                  showOTPdialogue()
                }else{
                    DialogUtils.showErrorDialoge(activity ,it.description)
                }
            }
        )
    }

    private fun subscribeForVerifyOTPForSetDefaultAccountStatus() {

        homeViewModel.verifyOTPForDefaultAccountResponseListener.observe(this@HomeFragment,
            Observer {
                if(it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    DialogUtils.successFailureDialogue(context,resources.getString(R.string.operation_success),0)
                }else{
                    DialogUtils.successFailureDialogue(context,resources.getString(R.string.operation_failure),1)
                }
            }
        )
    }

    private fun showPopUp() {
        val confirmationTxt=context?.resources?.getString(R.string.default_account_confirmation)
        DialogUtils.showConfirmationDialogue(confirmationTxt!!,activity,object : DialogUtils.OnConfirmationDialogClickListner{
            override fun onDialogYesClickListner() {
                homeViewModel.requestForSetDefaultAccount(context)
            }


        })
    }

    private fun showOTPdialogue() {
        DialogUtils.showOTPDialogue(activity,object : DialogUtils.OnOTPDialogClickListner{

            override fun onOTPDialogYesClickListner(otp: String) {
                homeViewModel.requestForVerifyOTPForSetDefaultAccount(context,referenceNumber,otp)
            }

        })
    }

    private fun populateHomeUseCase() {
        val useCases = ArrayList<HomeUseCasesModel>().apply {
            this.add(HomeUseCasesModel(getString(R.string.payments_newline), R.drawable.ic_payment))
            this.add(HomeUseCasesModel(getString(R.string.mobile_recharge), R.drawable.ic_recharge))
            this.add(
                HomeUseCasesModel(
                    getString(R.string.send_money_newLine),
                    R.drawable.ic_send_money
                )
            )
            this.add(HomeUseCasesModel(getString(R.string.cash_service), R.drawable.ic_transfer))
            this.add(HomeUseCasesModel(getString(R.string.qr_newLine), R.drawable.ic_qr_white))
            this.add(
                HomeUseCasesModel(
                    getString(R.string.accounts_newLine),
                    R.drawable.ic_accounts
                )
            )
        }

        mUseCasesAdapter =
            HomeUseCasesAdapter(useCases, object : HomeUseCasesAdapter.HomeUseCasesClickListner {
                override fun onHomeUseCaseClick(position: Int) {
                    when (position) {
                        2 -> startActivity(
                            Intent(
                                activity as MainActivity,
                                SendMoneyActivity::class.java
                            )
                        )

                        3 ->startActivity(
                            Intent(
                                activity as MainActivity,
                                CashServicesActivity::class.java
                            )
                        )
                        else -> startActivity(
                            Intent(
                                activity as MainActivity,
                                PaymentsActivity::class.java
                            )
                        )

                    }
                }

            }, activity as MainActivity)
        mDataBinding.useCasesRecyclerView.apply {
            adapter = mUseCasesAdapter
            layoutManager = GridLayoutManager(activity as MainActivity, 3)
        }
    }

    private fun populateHomeCardView() {
        mCardAdapter = HomeCardAdapter((activity as MainActivity).supportFragmentManager)
        mDataBinding.viewpager.apply {
            adapter = mCardAdapter
            pageMargin = 16
        }

        mDataBinding.flexibleIndicator.initViewPager(mDataBinding.viewpager)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> mDataBinding.leftNav.visibility = View.GONE
            2 -> mDataBinding.rightNav.visibility = View.GONE
            else -> {
                mDataBinding.leftNav.visibility = View.VISIBLE
                mDataBinding.rightNav.visibility = View.VISIBLE
            }
        }
    }

    override fun onNextBalanceCardClick(view: View) {
        val nPosition: Int = mDataBinding.viewpager.currentItem
        mDataBinding.viewpager.currentItem = nPosition + 1
    }

    override fun onPreviousBalanceCardClick(view: View) {
        val nPosition: Int = mDataBinding.viewpager.currentItem
        mDataBinding.viewpager.currentItem = nPosition - 1
    }
}