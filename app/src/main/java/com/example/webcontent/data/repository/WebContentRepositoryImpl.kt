package com.example.webcontent.data.repository

import com.example.webcontent.data.datasource.local.LocalDataSource
import com.example.webcontent.data.datasource.remote.ApiService

/**
 * Implementation of the WebContentRepository interface that manages web content data fetching.
 * This class handles both remote data fetching from a web service and local caching of data
 * for offline access.
 *
 * @property apiService The API service used for fetching data from the web.
 * @property localDataSource The local data source used for caching data locally.
 */
class WebContentRepositoryImpl(
    private val apiService: ApiService,
    private val localDataSource: LocalDataSource
) : WebContentRepository {

    /**
     * Fetches web content from the API or returns cached content in case of an error.
     * The method tries to fetch web content using the ApiService. If successful, it saves the content
     * to the local cache. If any exception occurs during the network call, it retrieves and returns
     * the cached content instead.
     *
     * @return The fetched or cached web content as a String.
     * @throws Exception if both the network call fails and no cached content is available.
     */
    override suspend fun fetchWebContent(): String {
        return try {
            // Attempt to fetch the content from the web using the API service.
            val content = apiService.fetchWebPage()
            // Save the fetched content to the local cache.
            localDataSource.saveContent("web_content", content)
            content
        } catch (e: Exception) {
            // In case of an exception, try to retrieve the content from the local cache.
            localDataSource.getContent("web_content")
        }
    }
}
