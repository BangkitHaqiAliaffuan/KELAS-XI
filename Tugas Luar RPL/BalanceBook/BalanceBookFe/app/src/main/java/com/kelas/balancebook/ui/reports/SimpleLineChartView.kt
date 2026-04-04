package com.kelas.balancebook.ui.reports

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class SimpleLineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#16A34A")
        strokeWidth = dp(2.5f)
        style = Paint.Style.STROKE
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#16A34A")
        style = Paint.Style.FILL
    }

    private val areaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E5E7EB")
        strokeWidth = dp(1f)
        style = Paint.Style.STROKE
    }

    private var values: List<Double> = emptyList()

    fun setData(input: List<Double>) {
        values = input
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val data = if (values.isEmpty()) listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0) else values
        if (data.size < 2) return

        val left = paddingLeft + dp(6f)
        val right = width - paddingRight - dp(6f)
        val top = paddingTop + dp(8f)
        val bottom = height - paddingBottom - dp(8f)
        val chartWidth = right - left
        val chartHeight = bottom - top
        if (chartWidth <= 0f || chartHeight <= 0f) return

        val maxValue = max(data.maxOrNull() ?: 0.0, 1.0)
        val count = data.size
        val stepX = if (count > 1) chartWidth / (count - 1) else chartWidth

        canvas.drawLine(left, top, right, top, gridPaint)
        canvas.drawLine(left, top + chartHeight / 2f, right, top + chartHeight / 2f, gridPaint)
        canvas.drawLine(left, bottom, right, bottom, gridPaint)

        val linePath = Path()
        val fillPath = Path()

        data.forEachIndexed { index, value ->
            val x = left + (stepX * index)
            val ratio = (value / maxValue).toFloat().coerceIn(0f, 1f)
            val y = bottom - (ratio * chartHeight)

            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, bottom)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        fillPath.lineTo(right, bottom)
        fillPath.close()

        areaPaint.shader = LinearGradient(
            0f,
            top,
            0f,
            bottom,
            intArrayOf(Color.parseColor("#4416A34A"), Color.parseColor("#0016A34A")),
            null,
            Shader.TileMode.CLAMP
        )

        canvas.drawPath(fillPath, areaPaint)
        canvas.drawPath(linePath, linePaint)

        data.forEachIndexed { index, value ->
            val x = left + (stepX * index)
            val ratio = (value / maxValue).toFloat().coerceIn(0f, 1f)
            val y = bottom - (ratio * chartHeight)
            canvas.drawCircle(x, y, dp(3f), pointPaint)
        }
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density
}
