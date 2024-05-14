package com.example.webcontent.data.datasource.remote

import retrofit2.http.GET

interface ApiService {
    @GET("about/")
    suspend fun fetchWebPage(): String
}