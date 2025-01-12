package com.se7en.market.Model

import android.os.Parcel
import android.os.Parcelable

data class AddressModel(
    val id: Int = 0,
    val user_id: Int = 0,
    var title: String? = null,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var address: String = "",
    var apartment: Int = 0,
    var unit: Int = 0,
    var is_default: Boolean = false,
    var is_telegram_default: Boolean = false,
    var recipient_name: String = "",
    var recipient_phone: String = "",
    val created_at: String = "",
    val updated_at: String = "",
    var active: Boolean = false,

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().orEmpty(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString().orEmpty(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(user_id)
        parcel.writeString(title)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(address)
        parcel.writeInt(apartment)
        parcel.writeInt(unit)
        parcel.writeString(recipient_name)
        parcel.writeString(recipient_phone)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
        parcel.writeByte(if (active) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddressModel> {
        override fun createFromParcel(parcel: Parcel): AddressModel {
            return AddressModel(parcel)
        }

        override fun newArray(size: Int): Array<AddressModel?> {
            return arrayOfNulls(size)
        }
    }
}
