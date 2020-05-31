package com.es.marocapp.model.requests

class ChangePasswordRequest (
    var context : String,
    var identity : String,
    var oldCredential : String,
    var newCredential : String,
    var credentialType : String
)