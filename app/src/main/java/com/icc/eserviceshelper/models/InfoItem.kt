package com.icc.eserviceshelper.models

import androidx.annotation.DrawableRes

sealed class InfoItem {
    data class Section(
        val title: String,
        val content: String,
        @DrawableRes val icon: Int? = null,
        val isWarning: Boolean = false
    ) : InfoItem()

    data class Contact(
        val title: String,
        val value: String,
        @DrawableRes val icon: Int,
        val actionType: ActionType
    ) : InfoItem()

    data class Link(
        val title: String,
        val description: String,
        val url: String,
        @DrawableRes val icon: Int? = null
    ) : InfoItem()

    data class AppInfo(
        val label: String,
        val value: String,
        @DrawableRes val icon: Int
    ) : InfoItem()

    enum class ActionType {
        EMAIL, PHONE, WEB, MAP
    }
}