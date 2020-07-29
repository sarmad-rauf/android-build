package com.es.marocapp.usecase.home


import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentHomeBalanceLayoutBinding
import com.es.marocapp.databinding.LayoutHomeScreenBalanceViewBinding
import com.es.marocapp.model.CardModel
import com.es.marocapp.usecase.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*

//viewType value 0 for Progress Balance View && 1 for Banner Advertisement View
class HomeBalanceFragment(var viewType : Int,cardDataModel : CardModel,var imageURL : Int) : BaseFragment<LayoutHomeScreenBalanceViewBinding>() {

    private lateinit var homeViewModel: HomeViewModel
    private  var mCardModel: CardModel = cardDataModel

    private fun updateBalance() {
        //viewType value 0 for Progress Balance View && 1 for Banner Advertisement View

        if(viewType==0){
            mDataBinding.progressGroup.visibility = android.view.View.VISIBLE
            mDataBinding.imgAdv.visibility = android.view.View.GONE
            mDataBinding.progressValueTitle.text = mCardModel.cardName
            mDataBinding.progressValue.text = mCardModel.cardBalance

            if(!mCardModel.userMax.isNullOrEmpty()){
                mDataBinding.arcSeekBar.maxProgress = mCardModel.userMax.toInt()
            }else{
                mDataBinding.arcSeekBar.maxProgress = 0
            }

            if(!mCardModel.userCurrent.isNullOrEmpty()){
                var doubleVal = mCardModel.userCurrent.toDouble()
                mDataBinding.arcSeekBar.progress = doubleVal.toInt()
            }else{
                mDataBinding.arcSeekBar.progress = 0
            }

            mDataBinding.arcSeekBar.isClickable = false
            mDataBinding.arcSeekBar.isActivated = false
            mDataBinding.arcSeekBar.isFocusable = false


        }else if(viewType==1){
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
