package com.es.marocapp.model.responses

import android.os.Parcel
import android.os.Parcelable

data class UserApprovalResponse(
    val amount: Amount?,
    val approvalexpirytime: String?,
    val approvalid: String?,
    val approvaltype: String?,
    val description: String?,
    val discount: String?,
    val fee: FeeAprroval?,
    val initiatingaccountholderid: String?,
    val message: String?,
    val offeridentities: String?,
    val responseCode: String?,
    val status: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Amount::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(FeeAprroval::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(amount, flags)
        parcel.writeString(approvalexpirytime)
        parcel.writeString(approvalid)
        parcel.writeString(approvaltype)
        parcel.writeString(description)
        parcel.writeString(discount)
        parcel.writeParcelable(fee, flags)
        parcel.writeString(initiatingaccountholderid)
        parcel.writeString(message)
        parcel.writeString(offeridentities)
        parcel.writeString(responseCode)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserApprovalResponse> {
        override fun createFromParcel(parcel: Parcel): UserApprovalResponse {
            return UserApprovalResponse(parcel)
        }

        override fun newArray(size: Int): Array<UserApprovalResponse?> {
            return arrayOfNulls(size)
        }
    }
}

