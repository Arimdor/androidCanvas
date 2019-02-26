package com.example.canvasdemo.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import com.example.canvasdemo.utils.CanvasView
import com.example.canvasdemo.activities.base.BaseActivity
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.FrameLayout
import com.example.canvasdemo.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity() {

    companion object {
        private const val REQUEST_TAKE_PHOTO = 1
    }

    private var currentPhotoPath: String = ""

    private lateinit var myCanvas: CanvasView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val canvasLayout = findViewById<FrameLayout>(R.id.canvas_container)
        myCanvas = CanvasView(this)
        canvasLayout.addView(myCanvas)

        val btnCamera = findViewById<Button>(R.id.btn_camera)
        btnCamera.setOnClickListener {
            dispatchTakePictureIntent()
        }

        myCanvas.invalidate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("Arimdor", "gggggg")
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            myCanvas.setBackgroundImage(getBitmap())
        }
    }

    private fun getBitmap(): Bitmap {
        return BitmapFactory.decodeFile(currentPhotoPath)
    }


    @Throws(IOException::class)
    private fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile(this)
                } catch (ex: IOException) {
                    Log.d("Arimdor", ex.stackTrace.toString())
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.canvasdemo.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }
}
