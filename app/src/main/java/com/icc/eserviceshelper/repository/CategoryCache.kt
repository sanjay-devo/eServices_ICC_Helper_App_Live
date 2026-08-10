package com.icc.eserviceshelper.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.icc.eserviceshelper.models.Category

class CategoryCache(context: Context) {
    private val prefs = context.getSharedPreferences("category_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getCachedCategories(): List<Category>? {
        val json = prefs.getString("categories_json", null) ?: return null
        return try {
            val type = object : TypeToken<List<Category>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun saveCategories(categories: List<Category>, version: Long) {
        val json = gson.toJson(categories)
        prefs.edit().apply {
            putString("categories_json", json)
            putLong("categories_version", version)
            apply()
        }
    }

    fun getCachedVersion(): Long {
        return prefs.getLong("categories_version", -1L)
    }
}
