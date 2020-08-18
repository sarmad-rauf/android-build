package com.es.marocapp.model

import android.os.Parcel
import android.os.Parcelable
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

data class FaqsQuestionModel(val question: String?,val answers: List<FaqsAnswers?>?) :
    ExpandableGroup<FaqsAnswers?>(question, answers)


data class FaqsAnswers(val answer: String?) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(answer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FaqsAnswers> {
        override fun createFromParcel(parcel: Parcel): FaqsAnswers {
            return FaqsAnswers(parcel)
        }

        override fun newArray(size: Int): Array<FaqsAnswers?> {
            return arrayOfNulls(size)
        }
    }
}