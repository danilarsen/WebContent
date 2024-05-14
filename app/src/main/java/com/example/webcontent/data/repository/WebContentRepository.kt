package com.example.webcontent.data.repository

interface WebContentRepository {
    suspend fun fetchWebContent(): String
}