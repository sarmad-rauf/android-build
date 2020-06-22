package com.es.marocapp.model.responses

data class PostPaidFinancialResourceInfoResponse(
    var description: String,
    var invoices: List<Invoice>,
    var response: Response,
    var responseCode: String
)

data class Invoice(
    var month: String,
    var ohrefnum: String,
    var ohxact: String,
    var openAmount: String
)

data class Response(
    var custId: String,
    var custname: String,
    var totalamount: String
)

data class InvoiceCustomModel(
    var isBillSelected : Boolean,
    var month: String,
    var ohrefnum: String,
    var ohxact: String,
    var openAmount: String
)
