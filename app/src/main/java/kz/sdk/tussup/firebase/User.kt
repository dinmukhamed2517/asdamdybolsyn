package kz.sdk.tussup.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.sdk.tussup.models.Order
import kz.sdk.tussup.models.Place

@Parcelize
data class User(
    var name:String? = null,
    var lastname:String?= null,
    var age:Int? = null,
    var email:String? =null,
    var phoneNumber:Long? =null,
    var place:Place?=  null,
    var pictureUrl: String? = null,
    var orders: Map<String, Order> = emptyMap(),
    ):Parcelable
