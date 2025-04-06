package otus.homework.coroutines

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class CatsPresenter(
    private val catsImageService: CatsImageService,
    private val catsService: CatsService,
) {

    private val presenterScope = CoroutineScope(Dispatchers.Main + CoroutineName("CatsCoroutine"))
    private var _catsView: ICatsView? = null

    fun onInitComplete() {
        presenterScope.launch {
            try {
                val fact = catsService.getCatFact()
                val image = catsImageService.getCatImage()

                _catsView?.populate(
                    Result.Success(
                        CatsFactState(
                            fact.fact,
                            image.firstOrNull()?.url.orEmpty()
                        )
                    )
                )

            } catch (e: SocketTimeoutException) {
                _catsView?.populate(Result.Error("Не удалось получить ответ"))
            } catch (e: Exception) {
                CrashMonitor.trackWarning()
                _catsView?.populate(Result.Error(e.message.orEmpty()))
            }
        }
    }

    fun attachView(catsView: ICatsView) {
        _catsView = catsView
    }

    fun detachView() {
        _catsView = null
    }

    fun onStop() {
        presenterScope.coroutineContext.cancel()
    }
}
