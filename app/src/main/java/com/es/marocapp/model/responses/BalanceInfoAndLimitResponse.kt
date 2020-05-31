package com.es.marocapp.model.responses

data class BalanceInfoAndLimitResponse(
    val balance: String,
    val currnecy: String,
    val dailycreditavailablelimit: String,
    val dailycreditremaininglimit: String,
    val dailydebitavailablelimit: String,
    val dailydebitremaininglimit: String,
    val description: String,
    val firstname: String,
    val monthlycreditavailablelimit: String,
    val monthlycreditremaininglimit: String,
    val monthlydebitavailablelimit: String,
    val monthlydebitremaininglimit: String,
    val profilename: String,
    val responseCode: String,
    val surname: String,
    val yearlycreditavailablelimit: String,
    val yearlycreditremaininglimit: String,
    val yearlydebitavailablelimit: String,
    val yearlydebitremaininglimit: String
)