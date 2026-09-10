package com.example.modmenuuiproto

import android.app.Activity
import android.os.Bundle
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.view.*
import android.content.Context
import kotlin.math.max
import kotlin.math.min

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ModMenuView(this))
    }
}

private class ModMenuView(context: Context) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bubble = PointF(72f, 180f)
    private var menuOpen = false
    private var dragging = false
    private var downX = 0f
    private var downY = 0f
    private var moved = false
    private val toggles = BooleanArray(4)

    private val names = arrayOf("ESP", "AIM", "NO RECOIL", "SPEED")

    init {
        paint.typeface = Typeface.create("sans", Typeface.NORMAL)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        c.drawColor(Color.rgb(8, 10, 15))

        // Simple game-like background
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(14, 17, 24)
        c.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Decorative grid
        paint.color = Color.rgb(20, 24, 33)
        paint.strokeWidth = 1f
        for (x in 0..width step 48) c.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), paint)
        for (y in 0..height step 48) c.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), paint)

        if (menuOpen) drawMenu(c)
        drawBubble(c)
    }

    private fun rounded(c: Canvas, l: Float, t: Float, r: Float, b: Float, radius: Float, color: Int) {
        paint.style = Paint.Style.FILL
        paint.color = color
        c.drawRoundRect(l, t, r, b, radius, radius, paint)
    }

    private fun drawBubble(c: Canvas) {
        val r = 27f
        paint.setShadowLayer(14f, 0f, 5f, Color.argb(120, 0, 0, 0))
        paint.color = Color.rgb(126, 105, 255)
        paint.style = Paint.Style.FILL
        c.drawCircle(bubble.x, bubble.y, r, paint)
        paint.clearShadowLayer()

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 25f
        paint.typeface = Typeface.DEFAULT_BOLD
        c.drawText("●", bubble.x, bubble.y + 9f, paint)
    }

    private fun drawMenu(c: Canvas) {
        val w = min(330f, width - 32f)
        val h = 350f
        val left = (width - w) / 2f
        val top = max(24f, height / 2f - h / 2f)
        val right = left + w

        paint.setShadowLayer(24f, 0f, 10f, Color.argb(180, 0, 0, 0))
        rounded(c, left, top, right, top + h, 22f, Color.rgb(18, 21, 29))
        paint.clearShadowLayer()

        rounded(c, left + 10f, top + 10f, right - 10f, top + 62f, 16f, Color.rgb(27, 30, 40))

        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 18f
        paint.color = Color.WHITE
        c.drawText("MOD MENU", (left + right) / 2f, top + 43f, paint)

        paint.color = Color.rgb(55, 59, 72)
        paint.strokeWidth = 1.5f
        c.drawLine(left + 18f, top + 78f, right - 18f, top + 78f, paint)

        for (i in names.indices) {
            val y = top + 104f + i * 53f
            paint.textAlign = Paint.Align.LEFT
            paint.typeface = Typeface.DEFAULT
            paint.textSize = 15f
            paint.color = Color.rgb(230, 232, 238)
            c.drawText(names[i], left + 22f, y + 6f, paint)
            drawSwitch(c, right - 70f, y, toggles[i])
        }

        paint.color = Color.rgb(55, 59, 72)
        c.drawLine(left + 18f, top + 315f, right - 18f, top + 315f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 14f
        paint.color = Color.rgb(190, 194, 205)
        c.drawText("SETTINGS", left + 22f, top + 340f, paint)
    }

    private fun drawSwitch(c: Canvas, cx: Float, cy: Float, on: Boolean) {
        rounded(c, cx - 30f, cy - 13f, cx + 30f, cy + 13f, 15f,
            if (on) Color.rgb(104, 88, 220) else Color.rgb(52, 56, 67))
        paint.color = Color.WHITE
        c.drawCircle(if (on) cx + 16f else cx - 16f, cy, 9f, paint)
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 8f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.color = if (on) Color.WHITE else Color.rgb(165, 169, 180)
        c.drawText(if (on) "ON" else "OFF", cx, cy + 3f, paint)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = e.x
                downY = e.y
                moved = false
                val dx = e.x - bubble.x
                val dy = e.y - bubble.y
                dragging = dx * dx + dy * dy <= 38f * 38f
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (dragging) {
                    if (kotlin.math.abs(e.x - downX) > 8 || kotlin.math.abs(e.y - downY) > 8) moved = true
                    bubble.x = e.x.coerceIn(32f, width - 32f)
                    bubble.y = e.y.coerceIn(60f, height - 32f)
                    invalidate()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (dragging && !moved) {
                    menuOpen = !menuOpen
                    invalidate()
                } else if (menuOpen) {
                    val w = min(330f, width - 32f)
                    val h = 350f
                    val left = (width - w) / 2f
                    val top = max(24f, height / 2f - h / 2f)
                    val right = left + w
                    if (e.x >= left && e.x <= right && e.y >= top + 80f && e.y <= top + 290f) {
                        val index = ((e.y - (top + 80f)) / 53f).toInt()
                        if (index in toggles.indices) {
                            toggles[index] = !toggles[index]
                            invalidate()
                        }
                    }
                }
                dragging = false
                return true
            }
        }
        return true
    }
}
