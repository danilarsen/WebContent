import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.webcontent.data.repository.WebContentRepository
import com.example.webcontent.ui.viewmodel.WebContentViewModel
import com.example.webcontent.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class WebContentViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var viewModel: WebContentViewModel
    @Mock
    private lateinit var repository: WebContentRepository

    @Before
    fun setUp() {
        // This initializes all mocks annotated with @Mock
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(StandardTestDispatcher())

        // Configure the mock's expected behavior
        runBlocking {
            whenever(repository.fetchWebContent()).thenReturn("<html><body>Some example text here with enough content to test.</body></html>")
        }

        // Initialize the ViewModel with the mocked repository
        viewModel = WebContentViewModel(repository)
    }

    @Test
    fun `fetch data and validate LiveData updates`() = runTest {
        viewModel.fetchData()

        // Wait until all coroutines finish
        advanceUntilIdle()

        // Get the LiveData values using the getOrAwaitValue extension
        val tenthCharResult = viewModel.everyTenthCharacter.getOrAwaitValue()
        val wordCountResult = viewModel.wordCount.getOrAwaitValue()

        // Check the expected results
        Assert.assertTrue("Expected every tenth character data was not received", tenthCharResult.isNotEmpty())
        Assert.assertTrue("Expected word count data was not received", wordCountResult.isNotEmpty())
    }
}