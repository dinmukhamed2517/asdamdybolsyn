package kz.sdk.tussup.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.sdk.tussup.firebase.User

@Parcelize
data class Place(
    val id:String? = null,
    val name:String? = null,
    val owner:String? = null,
    val BIN:String? = null,
    val userId:String? = null,
    val phoneNumber:String? = null,
    val email:String? = null,
    val img:String? = null,
    val longitude:Double? = null,
    val latitude:Double? = null,
    var orders: Map<String, Order> = emptyMap(),
):Parcelable
