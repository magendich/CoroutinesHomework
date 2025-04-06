package otus.homework.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val catsViewModel by lazy {
        ViewModelProvider(this)[CatsViewModel::class.java]
    }

    private val diContainer = DiContainer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        view.setRefreshListener { catsViewModel.onRefresh() }
        setContentView(view)

        catsViewModel.onCreate(
            catsService = diContainer.service,
            catsImageService = diContainer.catsImageService
        )

        lifecycleScope.launch {
            catsViewModel.state.collectLatest { newState ->
                view.populate(newState)
            }
        }
    }
}
