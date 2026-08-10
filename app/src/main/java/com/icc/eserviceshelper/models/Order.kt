package com.icc.eserviceshelper.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Order(
    val id: String = "",
    val service: String = "",
    val subservice: String = "",
    val userName: String = "",
    val mobileNumber: String = "",
    val timestamp: Long = 0,
    val status: String = "Pending"
) {
    companion object {
        const val STATUS_PENDING = "Pending"
        const val STATUS_PROCESSING = "Processing"
        const val STATUS_COMPLETED = "Completed"
        const val STATUS_CANCELLED = "Cancelled"
    }
}
