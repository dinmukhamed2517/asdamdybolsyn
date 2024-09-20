package kz.sdk.tussup.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Order(
    val id:Int? = null,
    val title:String? = null,
    val description:String? = null,
    val amount:Int? = null,
    val imageUrl:String? = null,
    val pickUpAddress:String? = null,
    val pickUpTimeFrom:String? = null,
    val pickUpTimeUntil:String? = null,
    val price:Double? = null,
    val pickUpDate:String? = null,
    val reserved:Boolean = false,
    val passkey:String?= null,
    val isActive:Boolean = false

):Parcelable