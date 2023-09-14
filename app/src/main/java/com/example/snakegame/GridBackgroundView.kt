package com.example.snakegame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GridBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val cellSize = MainActivity().cellSize // Adjust this to match your game's cell size
    private val paint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val screenWidth = width
        val screenHeight = height

        paint.color = Color.LTGRAY // Color of the grid lines
        paint.strokeWidth = 1f

        // Draw horizontal grid lines
        for (y in 0 until screenHeight step cellSize) {
            canvas.drawLine(0f, y.toFloat(), screenWidth.toFloat(), y.toFloat(), paint)
        }

        // Draw vertical grid lines
        for (x in 0 until screenWidth step cellSize) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), screenHeight.toFloat(), paint)
        }
    }
}
