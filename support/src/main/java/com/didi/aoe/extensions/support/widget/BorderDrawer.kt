package com.didi.aoe.extensions.support.widget

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 *
 * @author noctis
 */
class BorderDrawer {
    private val borderPaint: Paint by lazy {
        Paint().apply {
            color = Color.BLACK
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }
    }

    public fun drawRect(canvas: Canvas, xmin: Float, ymin: Float, xmax: Float, ymax: Float) {
        canvas.drawRect(xmin, ymin, xmax, ymax, borderPaint)
    }

    public fun drawRectWithText(
        canvas: Canvas,
        xmin: Float,
        ymin: Float,
        xmax: Float,
        ymax: Float,
        text: CharSequence
    ) {
        canvas.drawRect(xmin, ymin, xmax, ymax, borderPaint)
        canvas.drawText(text, 0, text.length, xmin, ymin, borderPaint)
    }
}