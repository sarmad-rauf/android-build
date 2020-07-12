package com.es.marocapp.usecase.favorites

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.es.marocapp.utils.SingleLiveEvent
import io.reactivex.disposables.Disposable

class FavoritesViewModel: ViewModel(){
    lateinit var disposable: Disposable
    var isLoading = ObservableField<Boolean>()
    var errorText = SingleLiveEvent<String>()

    var popBackStackTo = -1

    //Observerable Fileds
    var isPaymentSelected = ObservableField<Boolean>()
    var isFatoratiUsecaseSelected = ObservableField<Boolean>()
    var selectedFavoritesType = ObservableField<String>()
    var selectedFavoritesAction = ObservableField<String>()


}