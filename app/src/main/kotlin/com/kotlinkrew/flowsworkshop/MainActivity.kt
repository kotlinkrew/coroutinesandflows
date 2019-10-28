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
        // TODO need to get our image
    }

    /*
    private fun getImageAsync() = withContext(Dispatchers.IO) {
        Drawable.createFromStream(resources.openRawResource(R.raw.flows_in_flows), null)
    }
    */

    // endregion

    // region step 2

    private fun step2ApplyGrayscale() {
        grayscaleBtn.setOnClickListener {
            // TODO
        }
        undoBtn.setOnClickListener {
            // TODO
        }
    }

    private fun applyGrayscale() {
        // process long task
        // imageView.setImageBitmap(getGrayImagesFlowAsync().await().firstOrNull())
    }

    private suspend fun getBitmapsFromApiAsync() = withContext(Dispatchers.IO) {
        async { arrayOf(BitmapFactory.decodeStream(resources.openRawResource(R.raw.flows_in_flows))) }
    }

    private suspend fun getGrayImagesFlowAsync() = withContext(Dispatchers.IO) {
        // TODO make each image grayscale
        // use Bitmap.toGrayscale
    }

    // endregion

    // region step 3

    private fun step3Rotate() {
        // TODO add click listener for left rotation

        // TODO add click listener for right rotation
    }

    private fun rotateLeft(imageView: ImageView) {
        imageView.rotate(-90f)
    }

    private fun rotateRight(imageView: ImageView) {
        imageView.rotate(90f)
    }

    // endregion
}
