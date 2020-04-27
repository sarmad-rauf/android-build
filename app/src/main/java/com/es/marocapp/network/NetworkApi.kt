package com.es.marocapp.network


import com.es.marocapp.usecase.approvals.model.request.RequestApprovals
import com.es.marocapp.usecase.approvals.model.response.ResponseApprovals
import io.reactivex.Observable
import retrofit2.http.*


interface NetworkApi {

    // Request for Approvals Data
    @GET(EndPoints.approvals)
    fun getApprovals(): Observable<ResponseApprovals>



}