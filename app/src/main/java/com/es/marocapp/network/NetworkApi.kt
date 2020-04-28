package com.es.marocapp.network


import com.es.marocapp.model.requests.GetPreLoginDataRequest
import com.es.marocapp.model.responses.GetPreLoginDataResponse
import com.es.marocapp.usecase.approvals.model.response.ResponseApprovals
import io.reactivex.Observable
import retrofit2.http.*


interface NetworkApi {

    // Request for Approvals Data
    @GET(EndPoints.approvals)
    fun getApprovals(): Observable<ResponseApprovals>

    //Request for GetPreLoginData
    @POST(EndPoints.getprelogindata)
    fun getPreLoginData(@Body request : GetPreLoginDataRequest) : Observable<GetPreLoginDataResponse>


}