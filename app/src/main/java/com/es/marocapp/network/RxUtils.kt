package com.es.marocapp.network

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Inline Method for Observablen Transformer for network request.
 * @return ObservableTransformer
 */

inline fun <T> applyIOSchedulers(): ObservableTransformer<T, T> {
    return object : ObservableTransformer<T, T> {
        override fun apply(upstream: Observable<T>): ObservableSource<T> {

            return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

        }
    }

}


/**
 * Inline Method for Observablen Transformer for network request.
 * @return ObservableTransformer
 */

inline fun <T> applyComputationSchedulers(): ObservableTransformer<T, T> {
    return object : ObservableTransformer<T, T> {
        override fun apply(upstream: Observable<T>): ObservableSource<T> {

            return upstream.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())

        }
    }

}