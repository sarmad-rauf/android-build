package com.es.marocapp.usecase.home


import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.LayoutHomeScreenBalanceViewBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.model.CardModel
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.utils.Logger
import java.util.*


//viewType value 0 for Progress Balance View && 1 for Banner Advertisement View
class HomeBalanceFragment(var viewType: Int, cardDataModel: CardModel, var imageURL: Int) :
    BaseFragment<LayoutHomeScreenBalanceViewBinding>() {

    private lateinit var homeViewModel: HomeViewModel
    private var mCardModel: CardModel = cardDataModel

    private fun updateBalance() {
        //viewType value 0 for Progress Balance View && 1 for Banner Advertisement View
//        val conf: Configuration = context!!.resources.configuration
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            conf.setLayoutDirection(Locale("en"))
//        }
//        context!!.resources.updateConfiguration(
//            conf,
//            context!!.resources.displayMetrics
        //    )
        if (viewType == 0) {
            mDataBinding.progressGroup.visibility = android.view.View.VISIBLE
            mDataBinding.imgAdv.visibility = android.view.View.GONE
            mDataBinding.progressValueTitle.text = mCardModel.cardName
            mDataBinding.progressValue.text = mCardModel.cardBalance

            try {


                if (!mCardModel.userMax.isNullOrEmpty()) {
                    mDataBinding.arcSeekBar.maxProgress = mCardModel.userMax.toInt()
                    Logger.debugLog(
                        "Abro",
                        "arckSeekbar max progress ${mCardModel.userMax.toInt()}"
                    )
                    Logger.debugLog(
                        "Abro",
                        "arckSeekbar max progress ${
                            LanguageData.getStringValue("Balance").toString()
                        }"
                    )
                } else {
                    mDataBinding.arcSeekBar.maxProgress = 0
                    Logger.debugLog("Abro", "arckSeekbar max progress 0 ${0}")
                }

                if (!mCardModel.userCurrent.isNullOrEmpty()) {
                    var doubleVal = mCardModel.userCurrent.toDouble()
                    mDataBinding.arcSeekBar.progress = doubleVal.toInt()
                    Logger.debugLog("Abro", "arckSeekbar  progress ${doubleVal.toInt()}")
                } else {
                    mDataBinding.arcSeekBar.progress = 0
                    Logger.debugLog("Abro", "arckSeekbar  progress 0 ${0}")
                }
            } catch (e: Exception) {

            }

            mDataBinding.arcSeekBar.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                    return true
                }
            })

            mDataBinding.arcSeekBar.isClickable = false
            mDataBinding.arcSeekBar.isActivated = false
            mDataBinding.arcSeekBar.isFocusable = false


        } else if (viewType == 1) {
            mDataBinding.progressGroup.visibility = android.view.View.GONE
            mDataBinding.imgAdv.visibility = android.view.View.VISIBLE
            mDataBinding.imgAdv.setImageResource(imageURL)
        }
    }

    override fun setLayout(): Int {
        return R.layout.layout_home_screen_balance_view
    }

    override fun init(savedInstanceState: Bundle?) {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        mDataBinding.apply {
            viewmodel = homeViewModel
        }

        updateBalance()
    }

}
