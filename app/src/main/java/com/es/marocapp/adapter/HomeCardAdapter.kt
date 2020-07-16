package com.es.marocapp.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.es.marocapp.R
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.CardModel
import com.es.marocapp.model.responses.Account
import com.es.marocapp.model.responses.BalanceInfoAndLimitResponse
import com.es.marocapp.usecase.home.HomeBalanceFragment
import com.es.marocapp.utils.Constants

class HomeCardAdapter(
    fm: FragmentManager?
) :
    FragmentStatePagerAdapter(fm!!) {

    private lateinit var mbalanceInfoAndResonse: BalanceInfoAndLimitResponse
    private lateinit var mDummyAgnetBalanceFragment1: HomeBalanceFragment

    init {
        mbalanceInfoAndResonse = Constants.balanceInfoAndResponse
        if(Constants.IS_AGENT_USER && Constants.getAccountsResponseArray!=null ){
        for(i in Constants.getAccountsResponseArray.indices){
            //Constants.getAccountsResponseArray=it.accounts as ArrayList<Account>
             if(Constants.getAccountsResponseArray[i].accountType.equals(Constants.TYPE_COMMISSIONING,true)){
                 Constants.getAccountsResponse = Constants.getAccountsResponseArray[i]
                 mDummyAgnetBalanceFragment1 = HomeBalanceFragment(
                     CardModel(
                         R.drawable.ic_wallet_balance,
                         Constants.getAccountsResponse!!.accountType,
                         Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + Constants.getAccountsResponse!!.balance
                     )
                 )
             }
        }


        }
    }

    private var mDummyBalanceFragment1: HomeBalanceFragment = HomeBalanceFragment(
        CardModel(
            R.drawable.ic_wallet_balance,
            LanguageData.getStringValue("Balance").toString(),
            Constants.CURRENT_CURRENCY_TYPE_TO_SHOW + " " + mbalanceInfoAndResonse.balance
        )
    )


//    private var mDummyBalanceFragment2: HomeBalanceFragment = HomeBalanceFragment(CardModel(R.drawable.ic_wallet_balance,"Balance","200"))
//    private var mDummyBalanceFragment3: HomeBalanceFragment = HomeBalanceFragment(CardModel(R.drawable.ic_wallet_balance,"Balance","2,200"))

    private val TOTAL_BALANCE_FRAGMENTS_SINGLE = 1
    private val TOTAL_BALANCE_FRAGMENTS_AGENT = 2

    override fun getItem(position: Int): Fragment {
        when (position) {
            DUMMY_BALANCE_1 -> return mDummyBalanceFragment1
            DUMMY_BALANCE_2 -> return mDummyAgnetBalanceFragment1
        }
        return HomeBalanceFragment(CardModel(R.drawable.ic_wallet_balance, LanguageData.getStringValue("WalletBalance").toString(), "DH 0.00"))
    }

    override fun getCount(): Int {
        if(Constants.IS_AGENT_USER && Constants.getAccountsResponse!=null){
            return TOTAL_BALANCE_FRAGMENTS_AGENT
        }else{
            return TOTAL_BALANCE_FRAGMENTS_SINGLE
        }
    }

    companion object {
        private const val DUMMY_BALANCE_1 = 0
        private const val DUMMY_BALANCE_2 = 1
        private const val DUMMY_BALANCE_3 = 2
    }

}