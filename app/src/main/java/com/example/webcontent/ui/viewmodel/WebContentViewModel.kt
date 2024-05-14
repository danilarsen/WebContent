package com.example.webcontent.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.webcontent.data.repository.WebContentRepository
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

/**
 * ViewModel for fetching and processing web content.
 * It interacts with the WebContentRepository to perform data operations.
 *
 * @property repository Instance of WebContentRepository used for data operations.
 */
class WebContentViewModel(private val repository: WebContentRepository) : ViewModel() {

    // LiveData holding the processed every tenth character text.
    val everyTenthCharacter = MutableLiveData<String>()

    // LiveData holding the processed word count.
    val wordCount: MutableLiveData<String> = MutableLiveData()

    /**
     * Fetches web content from the repository, processes it, and updates LiveData.
     * It first fetches raw HTML content, then parses it to plain text and processes
     * it to extract specific data.
     */
    fun fetchData() {
        viewModelScope.launch {
            val htmlContent = repository.fetchWebContent()
            val document = Jsoup.parse(htmlContent)
            val textContent = document.text()

            processEveryTenthCharacter(textContent)
            processWordCount(textContent)
        }
    }

    /**
     * Processes the text content to extract every tenth character from non-whitespace characters.
     *
     * @param content The plain text content to be processed.
     */
    private fun processEveryTenthCharacter(content: String) {
        val result = StringBuilder()
        content.filter { !it.isWhitespace() }.forEachIndexed { index, c ->
            if ((index + 1) % 10 == 0) result.append(c)
        }
        val processedContent = result.toString()
        everyTenthCharacter.postValue(processedContent)
    }

    /**
     * Processes the text content to count occurrences of each word and posts the top 100 words.
     *
     * @param content The plain text content to be processed.
     */
    private fun processWordCount(content: String) {
        val words = content.split("\\s+".toRegex()).map { it.lowercase() }
        val wordCountMap = words.groupingBy { it }.eachCount()
            .toList().sortedByDescending { it.second }.take(100).toMap()

        val formattedWordCount = formatWordCountMap(wordCountMap)
        wordCount.postValue(formattedWordCount)
    }

    /**
     * Formats a map of word counts into a newline-separated string.
     *
     * @param map The map of words to their respective counts.
     * @return A string representation of the word count map.
     */
    private fun formatWordCountMap(map: Map<String, Int>): String {
        return map.entries.joinToString(separator = "\n") { "${it.key}=${it.value}" }
    }

    /**
     * Factory for creating instances of WebContentViewModel with a given repository.
     */
    class Factory(private val repository: WebContentRepository) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return WebContentViewModel(repository) as T
        }
    }
}
