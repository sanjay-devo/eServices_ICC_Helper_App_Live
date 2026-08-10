package com.icc.eserviceshelper.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ApplyRequest(
    val id: String = "",
    val serviceTitle: String = "",
    val userName: String = "",
    val mobileNumber: String = "",
    val timestamp: Long = 0
)
