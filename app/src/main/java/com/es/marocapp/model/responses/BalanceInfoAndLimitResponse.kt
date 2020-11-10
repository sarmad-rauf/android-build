package com.es.marocapp.model.responses

data class BalanceInfoAndLimitResponse(
    var balance: String?,
    var currnecy: String?,
    var description: String?,
    var firstname: String?,
    var limitsList: List<Limits>?,
    var profilename: String?,
    var responseCode: String?,
    var surname: String?,
    var email: String?
){

    fun copy(newVal : BalanceInfoAndLimitResponse) : BalanceInfoAndLimitResponse {
        //uses the fields name and property defined in the constructor
        return BalanceInfoAndLimitResponse(newVal.balance,newVal.currnecy,newVal.description,newVal.firstname,newVal.limitsList,newVal.profilename,newVal.responseCode,
            newVal.surname,newVal.email)
    }
}

data class Limits(
    var name: String?,
    var periodType: String?,
    var periodlength: String?,
    var threshhold: String?
)