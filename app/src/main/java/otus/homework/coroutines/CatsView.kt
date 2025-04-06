package otus.homework.coroutines

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.picasso.Picasso

class CatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ICatsView {

    private var onRefresh: (() -> Unit)? = null

    fun setRefreshListener(l: () -> Unit) {
        onRefresh = l
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        findViewById<Button>(R.id.button).setOnClickListener {
            onRefresh?.invoke()
        }
    }

    override fun populate(state: Result) {
        val button = findViewById<Button>(R.id.button)
        val errorMessage = findViewById<TextView>(R.id.error_textView)
        val factTextView = findViewById<TextView>(R.id.fact_textView)
        val catImageView = findViewById<ImageView>(R.id.cat_imageView)

        when (state) {
            is Result.Loading -> {
                button.isEnabled = false
            }

            is Result.Error -> {
                errorMessage.visibility = VISIBLE
                errorMessage.text = state.message
                button.isEnabled = true
            }

            is Result.Success<*> -> {
                val data = state.data as? CatsFactState
                factTextView.text = data?.fact
                if (data?.imageUrl?.isNotEmpty() == true) {
                    Picasso.get().load(data.imageUrl).into(catImageView)
                }
                button.isEnabled = true
                errorMessage.visibility = GONE
            }
        }
    }
}

interface ICatsView {

    fun populate(state: Result)
}
