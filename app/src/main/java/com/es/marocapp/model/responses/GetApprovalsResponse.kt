package com.es.marocapp.model.responses

import android.os.Parcel
import android.os.Parcelable

data class GetApprovalsResponse(
    val approvaldetails: List<Approvaldetail>?,
    val description: String?,
    val responseCode: String?
)
//    :Parcelable {
//    constructor(parcel: Parcel) : this(
//        parcel.createTypedArrayList(Approvaldetail),
//        parcel.readString(),
//        parcel.readString()
//    ) {
//    }
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeTypedList(approvaldetails)
//        parcel.writeString(description)
//        parcel.writeString(responseCode)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<GetApprovalsResponse> {
//        override fun createFromParcel(parcel: Parcel): GetApprovalsResponse {
//            return GetApprovalsResponse(parcel)
//        }
//
//        override fun newArray(size: Int): Array<GetApprovalsResponse?> {
//            return arrayOfNulls(size)
//        }
//    }
//}

data class Approvaldetail(
    val amount: Amount?,
    val approvalexpirytime: String?,
    val approvalid: Int,
    val approvaltype: String?,
    val discount: Any,
    val fee: FeeAprroval?,
    val initiatingaccountholderid: String?,
    val message: String?,
    val offeridentities: Any,
    val status: String?,
    val taxList :List<DetailsList>
)


data class Amount(
    val amount: Double,
    val currency: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(amount)
        parcel.writeString(currency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Amount> {
        override fun createFromParcel(parcel: Parcel): Amount {
            return Amount(parcel)
        }

        override fun newArray(size: Int): Array<Amount?> {
            return arrayOfNulls(size)
        }
    }
}

data class FeeAprroval(
    val amount: Double,
    val currency: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(amount)
        parcel.writeString(currency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeeAprroval> {
        override fun createFromParcel(parcel: Parcel): FeeAprroval {
            return FeeAprroval(parcel)
        }

        override fun newArray(size: Int): Array<FeeAprroval?> {
            return arrayOfNulls(size)
        }
    }
}