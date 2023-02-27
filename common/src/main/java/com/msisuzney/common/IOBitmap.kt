package com.msisuzney.common

import android.os.Parcel
import android.os.Parcelable
import android.os.SharedMemory

class IOBitmap(
    var bitmapWidth: Int = 0,
    var bitmapHeight: Int = 0,
    var bitmapMemory: SharedMemory? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readParcelable(SharedMemory::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(bitmapWidth)
        dest.writeInt(bitmapHeight)
        dest.writeParcelable(bitmapMemory, 0)
    }

    fun readFromParcel(inParcel: Parcel) {
        bitmapWidth = inParcel.readInt()
        bitmapHeight = inParcel.readInt()
        bitmapMemory = inParcel.readParcelable(SharedMemory::class.java.classLoader)
    }

    override fun toString(): String {
        return "IOBitmap(bitmapWidth=$bitmapWidth, bitmapHeight=$bitmapHeight, bitmapMemory=$bitmapMemory)"
    }

    companion object CREATOR : Parcelable.Creator<IOBitmap> {
        override fun createFromParcel(parcel: Parcel): IOBitmap {
            return IOBitmap(parcel)
        }

        override fun newArray(size: Int): Array<IOBitmap?> {
            return arrayOfNulls(size)
        }
    }


}