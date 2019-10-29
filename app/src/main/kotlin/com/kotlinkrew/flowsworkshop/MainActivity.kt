package com.kotlinkrew.flowsworkshop

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
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
            imageView.setImageDrawable(getImageAsync())
        }
    }

    private suspend fun getImageAsync() = withContext(Dispatchers.IO) {
        Drawable.createFromStream(resources.openRawResource(R.raw.flows_in_flows), null)
    }

    // endregion

    // region step 2

    private fun step2ApplyGrayscale() {
        grayscaleBtn.setOnClickListener {
            lifecycleScope.launch {
                processLongTask(progressBar) {
                    applyGrayscale()
                }
            }
        }
        undoBtn.setOnClickListener {
            lifecycleScope.launch {
                processLongTask(progressBar) {
                    step1LoadOrigImages()
                }
            }
        }
    }

    private suspend fun applyGrayscale() {
        // process long task
        imageView.setImageBitmap(getGrayImagesFlowAsync().first())
    }

    private suspend fun getBitmapsFromApiAsync() = withContext(Dispatchers.IO) {
        async { arrayOf(BitmapFactory.decodeStream(resources.openRawResource(R.raw.flows_in_flows))) }
    }

    private suspend fun getGrayImagesFlowAsync() = withContext(Dispatchers.IO) {
        flowOf(*getBitmapsFromApiAsync().await())
            .map {
                it.toGrayscale()
            }
    }

    // endregion

    // region step 3

    private fun step3Rotate() {
        rotateLeftBtn.clicks()
            .debounce(400)
            .onEach {
                rotateLeft(imageView)
            }
            .launchIn(lifecycleScope)

        rotateRightBtn.clicks()
            .debounce(400)
            .onEach {
                rotateRight(imageView)
            }
            .launchIn(lifecycleScope)
    }

    private fun rotateLeft(imageView: ImageView) {
        imageView.rotate(-90f)
    }

    private fun rotateRight(imageView: ImageView) {
        imageView.rotate(90f)
    }

    // endregion
}
