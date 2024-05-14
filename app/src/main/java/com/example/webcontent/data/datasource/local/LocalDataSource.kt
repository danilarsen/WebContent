package com.example.webcontent.data.datasource.local

import android.content.SharedPreferences

class LocalDataSource(private val sharedPreferences: SharedPreferences) {

    fun saveContent(key: String, content: String) {
        sharedPreferences.edit().putString(key, content).apply()
    }

    fun getContent(key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }
}