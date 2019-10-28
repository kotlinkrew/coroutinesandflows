package com.kotlinkrew.flowsworkshop

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.CheckResult
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        step1LoadOrigImages()
        step2ApplyGrayscale()
        step3Rotate()
    }

    // region step 1

    private fun step1LoadOrigImages() {
        lifecycleScope.launch {
            undoBtn.isEnabled = false

            processLongTask {
                val drawable: Deferred<Drawable> = getImageAsync()
                imageView.setImageDrawable(drawable.await())
            }

            grayscaleBtn.isEnabled = true
        }
    }

    private suspend fun getImageAsync() = withContext(Dispatchers.IO) {
        async { Drawable.createFromStream(resources.openRawResource(R.raw.flows_in_flows), null) }
    }

    // endregion

    // region step 2

    private fun step2ApplyGrayscale() {
        grayscaleBtn.setOnClickListener {
            applyGrayscale()
        }
        undoBtn.setOnClickListener {
            step1LoadOrigImages()
        }
    }

    private fun applyGrayscale() {
        lifecycleScope.launch {
            grayscaleBtn.isEnabled = false

            processLongTask {
                imageView.setImageBitmap(getCuteGrayImagesFlowAsync().await().firstOrNull())
            }

            undoBtn.isEnabled = true
        }
    }

    private suspend fun getBitmapsFromApiAsync() = withContext(Dispatchers.IO) {
        async { arrayOf(BitmapFactory.decodeStream(resources.openRawResource(R.raw.flows_in_flows))) }
    }

    private suspend fun getCuteGrayImagesFlowAsync() = withContext(Dispatchers.IO) {
        async { flowOf(*getBitmapsFromApiAsync().await())
            .map { it.toGrayscale() }
            .toList()
        }
    }

    // endregion

    // region step 3

    private fun step3Rotate() {
        rotateLeftBtn.clicks()
            .debounce(400)
            .onEach {
                imageView.rotation = imageView.rotation - 90f
            }
            .launchIn(lifecycleScope)

        rotateRightBtn.clicks()
            .debounce(400)
            .onEach {
                imageView.rotation = imageView.rotation + 90f
            }
            .launchIn(lifecycleScope)
    }

    // endregion

    // region helpers

    private suspend fun Bitmap.toGrayscale() = withContext(Dispatchers.IO) {
        val origBitmap = this@toGrayscale

        val grayColorMatrix = floatArrayOf(
            0.3f,
            0.59f,
            0.11f,
            0f,
            0f,
            0.3f,
            0.59f,
            0.11f,
            0f,
            0f,
            0.3f,
            0.59f,
            0.11f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f
        )
        val newBitmap = Bitmap.createBitmap(
            origBitmap.width,
            origBitmap.height,
            origBitmap.config
        )

        val canvas = Canvas(newBitmap)
        val paint = Paint()
        val filter = ColorMatrixColorFilter(grayColorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(origBitmap, 0f, 0f, paint)

        origBitmap.recycle()

        newBitmap
    }

    private suspend fun processLongTask(task: suspend () -> Unit) {
        progressBar.show()
        delay(1000)
        task.invoke()
        progressBar.hide()
    }

    // useful methods pulled from Corbind: https://github.com/LDRAlighieri/Corbind

    @CheckResult
    fun View.clicks(): Flow<Unit> = channelFlow {
        setOnClickListener(listener(this, ::offer))
        awaitClose { setOnClickListener(null) }
    }

    @CheckResult
    private fun listener(
        scope: CoroutineScope,
        emitter: (Unit) -> Boolean
    ) = View.OnClickListener {
        if (scope.isActive) { emitter(Unit) }
    }

    // endregion
}
