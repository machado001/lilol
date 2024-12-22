package com.machado001.lilol.common.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import com.squareup.picasso.Transformation

object PicassoGradientTransformation : Transformation {

    private val startColor = Color.argb(240, 0, 0, 0)
    private const val END_COLOR = Color.TRANSPARENT

    override fun transform(source: Bitmap): Bitmap? {
        val x = source.width
        val y = source.height

        val gradientBitmap = source.config?.let { source.copy(it, true) }
        val canvas = gradientBitmap?.let { Canvas(it) }

        // Create a linear gradient from the center of the image to the top
        val grad = LinearGradient(
            x / 2.toFloat(), y.toFloat(), x / 2.toFloat(), y/ 5.toFloat(),
            startColor, END_COLOR, Shader.TileMode.CLAMP
        )

        val paint = Paint(Paint.DITHER_FLAG)

        paint.apply {
            shader = null
            isDither = true
            isFilterBitmap = true
            shader = grad
            canvas?.drawPaint(this)
        }

        source.recycle()

        return gradientBitmap
    }

    override fun key(): String {
        return "Gradient"
    }
}