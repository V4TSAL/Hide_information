package com.example.blurimage

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class BlurTouchImageView : View {

    private var mBitmap: Bitmap? = null
    private var mBlurRadius = 20f
    private var mBlurPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mX = -1f
    private var mY = -1f
    private var currentRotation = 0f
    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        // Initialize Paint for blur effect
//        mBitmap = BitmapFactory.decodeResource(resources,R.drawable.demo_id_card)
        mBlurPaint.maskFilter = BlurMaskFilter(mBlurRadius, BlurMaskFilter.Blur.INNER)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw original bitmap
        mBitmap?.let { bitmap ->
            val scale = width.toFloat() / bitmap.width.toFloat()
            canvas.scale(scale, scale)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
        }

        // Draw blur effect at touch position
        if (mX > 0 && mY > 0) {
            applyBlurEffect()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Update touch position
                mX = event.x
                mY = event.y

                // Apply blur effect to the touched area

                // Redraw the view
                invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Reset touch position
                mX = -1f
                mY = -1f

                // Redraw the view
                invalidate()
            }
        }
        return true
    }

    private fun applyBlurEffect() {
        mBitmap?.let { originalBitmap ->
            // Calculate scale factor
            val scaleFactor = originalBitmap.width.toFloat() / width.toFloat()

            // Adjust touch coordinates to bitmap scale
            val scaledX = mX * scaleFactor
            val scaledY = mY * scaleFactor

            // Adjust circle radius based on scaling factor
            val radius = 20f * scaleFactor

            // Create a mutable copy of the original bitmap
            val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

            // Create a canvas to draw on the mutable bitmap
            val canvas = Canvas(mutableBitmap)

            // Apply blur effect at touch position
            val blurPaint = Paint()
            blurPaint.maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)

            // Draw circle at adjusted touch coordinates
            canvas.drawCircle(scaledX, scaledY, radius, blurPaint)

            // Update the original bitmap with the blurred area
            mBitmap = mutableBitmap
        }
    }
    fun setBitmap(bitmap: Bitmap) {
        val rotatedBitmap = rotateBitmap(bitmap)
        mBitmap = bitmap
        invalidate()
    }
    private fun rotateBitmap(bitmap: Bitmap): Bitmap {
        // Get Exif orientation information
        val exif = ExifInterface(context.contentResolver.openInputStream(globalUri!!)!!)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        currentRotation = orientation.toFloat()
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    private fun getBitmapRotation(): Float {
        // Get Exif orientation information
        val exif = ExifInterface(context.contentResolver.openInputStream(globalUri!!)!!)
        return when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    }
    fun getBitmap() : Bitmap?{
        return mBitmap
    }
}
