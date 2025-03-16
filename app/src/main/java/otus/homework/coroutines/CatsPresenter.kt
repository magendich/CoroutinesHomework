package otus.homework.coroutines

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class CatsPresenter(
    private val catsService: CatsService
) {

    private var _catsView: ICatsView? = null
    private var presenterJob: Job? = null


    private val presenterScope = CoroutineScope(Dispatchers.Main + CoroutineName("CatsCoroutine"))

    fun onInitComplete() {
        presenterJob?.cancel()
        presenterJob = presenterScope.launch {
            try {
                val fact = withContext(Dispatchers.IO) {
                    catsService.getCatFact()
                }
                _catsView?.populate(fact)
            } catch (e: SocketTimeoutException) {
                Toast.makeText(
                    _catsView as Context,
                    "Не удалось получить ответ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun attachView(catsView: ICatsView) {
        _catsView = catsView
    }

    fun detachView() {
        _catsView = null
        presenterJob?.cancel()
    }
}
