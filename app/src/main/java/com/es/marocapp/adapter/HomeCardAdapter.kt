package com.es.marocapp.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.es.marocapp.R
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.CardModel
import com.es.marocapp.model.responses.BalanceInfoAndLimitResponse
import com.es.marocapp.usecase.home.HomeBalanceFragment
import com.es.marocapp.utils.Constants

class HomeCardAdapter(
    fm: FragmentManager?
) :
    FragmentStatePagerAdapter(fm!!) {

    private lateinit var mbalanceInfoAndResonse: BalanceInfoAndLimitResponse

    init {
        mbalanceInfoAndResonse = Constants.balanceInfoAndResponse
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

    private val TOTAL_BALANCE_FRAGMENTS = 3

    override fun getItem(position: Int): Fragment {
        when (position) {
            DUMMY_BALANCE_1 -> return mDummyBalanceFragment1
//            DUMMY_BALANCE_2 -> return mDummyBalanceFragment2
//            DUMMY_BALANCE_3 -> return mDummyBalanceFragment3
        }
        return HomeBalanceFragment(CardModel(R.drawable.ic_wallet_balance, LanguageData.getStringValue("WalletBalance").toString(), "1,200"))
    }

    override fun getCount(): Int {
        return TOTAL_BALANCE_FRAGMENTS
    }

    companion object {
        private const val DUMMY_BALANCE_1 = 0
        private const val DUMMY_BALANCE_2 = 1
        private const val DUMMY_BALANCE_3 = 2
    }

}