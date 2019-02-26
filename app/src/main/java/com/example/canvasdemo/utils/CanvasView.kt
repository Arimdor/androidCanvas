package com.example.canvasdemo.utils

import android.content.Context
import android.graphics.*
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.graphics.Bitmap
import android.widget.ImageView
import com.example.canvasdemo.R
import com.example.canvasdemo.activities.MainActivity


class CanvasView(context: Context) : View(context) {
    private lateinit var canvas: Canvas
    private val mPaint = Paint()
    private var bitmap: Bitmap? = null
    private var oBitmap: Bitmap? = null
    private val mPath: Path = Path()
    private var listPoints = ArrayList<Point>()
    private var ratio = 1f

    init {
        mPaint.color = Color.RED
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = 10f
        mPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        this.canvas = canvas
        if (bitmap != null) {
            canvas.drawBitmap(
                bitmap!!, null, Rect(0, 0, bitmap!!.width, bitmap!!.height), mPaint
            )
        }
        canvas.drawPath(mPath, mPaint)
        super.onDraw(canvas)
    }

    fun setBackgroundImage(bitmap: Bitmap) {
        if ((bitmap.height / bitmap.width) < 1) {
            val matrix = Matrix()
            matrix.postRotate(90f)
            val rotateBitMap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height,
                matrix, true
            )
            this.oBitmap = rotateBitMap
            this.bitmap = scaleBitmap(rotateBitMap)
        } else {
            this.oBitmap = bitmap
            this.bitmap = scaleBitmap(bitmap)
        }
        invalidate()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when {
            event.action == MotionEvent.ACTION_DOWN -> {
                if (!listPoints.isEmpty()) {
                    listPoints = ArrayList()
                    mPath.reset()
                }
                Log.d("Arimdor", "ACTION_DOWN")
                mPath.moveTo(event.x, event.y)
                listPoints.add(Point(event.x.toInt(), event.y.toInt()))
            }
            event.action == MotionEvent.ACTION_MOVE -> {
                Log.d("Arimdor", "ACTION_MOVE")
                mPath.lineTo(event.x, event.y)
                listPoints.add(Point(event.x.toInt(), event.y.toInt()))
                invalidate()
            }
            event.action == MotionEvent.ACTION_UP -> {
                Log.d("Arimdor", "ACTION_UP")
                mPath.lineTo(
                    listPoints[0].x.toFloat(), listPoints[0].y.toFloat()
                )
                crop()
            }
        }
        return true
    }

    private fun crop() {
        val fullScreenBitmap = Bitmap.createBitmap(oBitmap!!.width, oBitmap!!.height, oBitmap!!.config)
        val paint = Paint()
        val canvas = Canvas(fullScreenBitmap)
        var scaledPath = Path()

        for (i in 0 until listPoints.size) {
            if (i == 0) {
                scaledPath.moveTo(listPoints[0].x * ratio, listPoints[0].y * ratio)
            } else {
                scaledPath.lineTo(listPoints[i].x * ratio, listPoints[i].y * ratio)
            }
        }

        canvas.drawPath(scaledPath, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(oBitmap, 0f, 0f, paint)

        val region = Region()
        val clip = Region(0, 0, fullScreenBitmap.width, fullScreenBitmap.height)
        region.setPath(scaledPath, clip)
        val bounds = region.bounds
        val croppedBitmap = Bitmap.createBitmap(
            fullScreenBitmap, bounds.left, bounds.top,
            bounds.width(), bounds.height()
        )
        //this.bitmap = croppedBitmap
        //mPath.reset()
        //invalidate()
        val mainActivity = (context as MainActivity)
        mainActivity.setContentView(R.layout.activity_second)
        val imageView = mainActivity.findViewById<ImageView>(R.id.resultImage)
        imageView.setImageBitmap(croppedBitmap)
    }

    private fun scaleBitmap(bm: Bitmap): Bitmap {
        var bm = bm
        var width = bm.width
        var height = bm.height

        Log.v("Pictures", "Width and height are $width--$height")
        val mWidth = this.width
        val mHeight = this.height
        // portrait
        val ratio = width.toFloat() / mWidth.toFloat()
        this.ratio = ratio
        width = mWidth
        height = (height / ratio).toInt()

        Log.v("Pictures", "after scaling Width and height are $width--$height")

        bm = Bitmap.createScaledBitmap(bm, width, height, true)
        return bm
    }
}
