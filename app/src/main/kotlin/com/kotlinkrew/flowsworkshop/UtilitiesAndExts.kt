package com.kotlinkrew.flowsworkshop

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import androidx.annotation.CheckResult
import androidx.core.widget.ContentLoadingProgressBar
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

/**
 * @author Ryan Simon
 */
suspend fun Bitmap.toGrayscale(): Bitmap = withContext(Dispatchers.IO) {
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

suspend inline fun processLongTask(progressBar: ContentLoadingProgressBar, task: () -> Unit) {
    progressBar.show()
    delay(1000)
    task.invoke()
    progressBar.hide()
}

fun ImageView.rotate(degrees: Float) {
    this.rotation = this.rotation + degrees
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
