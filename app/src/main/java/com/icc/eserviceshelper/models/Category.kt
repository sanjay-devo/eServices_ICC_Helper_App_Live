package com.icc.eserviceshelper.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
data class Category(
    val id: String = "",
    val title: String = "",
    val icon_url: String = "",
    val items: Map<String, ServiceItem>? = null
) : Parcelable
