package com.example.canvasdemo.activities.base

import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    @JvmField
    internal val REQUEST_IMAGE_CAPTURE = 1

    internal open fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

}