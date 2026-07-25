package com.icc.eserviceshelper.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
data class ServiceItem(
    val id: String = "",
    val title: String = "",
    val pdf_url: String = "",
    val keywords: List<String>? = null
) : Parcelable
