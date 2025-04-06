package otus.homework.coroutines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatsViewModel : ViewModel() {
    private lateinit var catsImageService: CatsImageService
    private lateinit var catsService: CatsService

    private val _state = MutableStateFlow<Result>(
        Result.Success(
            CatsFactState(
                fact = "",
                imageUrl = ""
            )
        )
    )
    val state: StateFlow<Result> = _state

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        CrashMonitor.trackWarning()
        _state.update { Result.Error(throwable.message.orEmpty()) }
    }

    private fun fetchCats() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            _state.update { Result.Loading }
            try {
                val fact = catsService.getCatFact()
                val images = catsImageService.getCatImage()
                _state.update {
                    Result.Success(
                        CatsFactState(
                            fact = fact.fact,
                            imageUrl = images.getOrNull(0)?.url.orEmpty()
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update { Result.Error(e.message.orEmpty()) }
            }
        }
    }

    fun onCreate(
        catsImageService: CatsImageService,
        catsService: CatsService
    ) {
        this.catsService = catsService
        this.catsImageService = catsImageService
    }

    fun onRefresh() {
        fetchCats()
    }
}


sealed class Result {
    data class Success<T>(val data: T) : Result()
    data class Error(val message: String) : Result()
    object Loading : Result()
}
