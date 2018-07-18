package com.moyuru.cinematiccountdownview

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import android.graphics.RectF
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.view.View
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

class CinematicCountdownView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

  private val attributes = context.obtainStyledAttributes(attrs, R.styleable.CinematicCountdownView, defStyleAttr, 0)
  private val autoSizingPercentage = attributes.getFloat(R.styleable.CinematicCountdownView_autoSizingPercentage, 0.8f)
  private val countTextSize = attributes.getDimension(R.styleable.CinematicCountdownView_countTextSize, dp(16))
  private val paintColor = attributes.getColor(R.styleable.CinematicCountdownView_color, Color.BLACK)
  private val rimWidth = attributes.getDimension(R.styleable.CinematicCountdownView_rimStrokeWidth, dp(2))
  private val shouldAutoSizing = attributes.getBoolean(R.styleable.CinematicCountdownView_autoSizingText, false)

  private var animator: ValueAnimator? = null
  private var currentMilliSec = 0L
    set(value) {
      field = value
      invalidate()
    }
  private val offset = dp(4)
  private val oval
    get() = RectF(
        offset + if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) paddingStart else paddingLeft,
        offset + if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) paddingEnd else paddingRight,
        width - paddingTop - offset,
        height - paddingBottom - offset
    )

  private val circlePaint = Paint().apply {
    isAntiAlias = true
    strokeWidth = rimWidth
    style = STROKE
    color = paintColor
  }
  private val textPaint = Paint().apply {
    isAntiAlias = true
    textAlign = Paint.Align.CENTER
    textSize = countTextSize
    color = paintColor
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    drawCircle(canvas, currentMilliSec)
    drawText(canvas, currentMilliSec.toCountNumber())
  }

  override fun onDetachedFromWindow() {
    animator?.cancel()
    super.onDetachedFromWindow()
  }

  private fun drawCircle(canvas: Canvas, milliSec: Long) {
    if (milliSec > 0L && milliSec % 1000 == 0L) canvas.drawArc(360f)
    else canvas.drawArc(360 * (1000f - milliSec % 1000) / 1000)
  }

  private fun drawText(canvas: Canvas, sec: Int) {
    val secString = sec.toString()
    if (shouldAutoSizing) textPaint.adjustTextSize(secString)
    canvas.drawText(
        secString,
        width / 2f,
        (height / 2) + (textPaint.fontMetrics.run { top.absoluteValue + descent.absoluteValue } / 4),
        textPaint
    )
  }

  private fun Paint.adjustTextSize(text: String) {
    val textHeight = fontMetrics.run { top.absoluteValue + descent.absoluteValue }
    val textWidth = measureText(text)

    if (textHeight < oval.height() && textWidth < oval.width()
        && (textHeight >= oval.height() * autoSizingPercentage || textWidth >= oval.width() * autoSizingPercentage)
    ) return

    when {
      textHeight > oval.height() || textWidth > oval.width() -> {
        textSize--
        adjustTextSize(text)
      }
      textHeight < oval.height() && textWidth < oval.width() -> {
        textSize++
        adjustTextSize(text)
      }
    }
  }

  fun count(sec: Long, onEnd: () -> Unit = {}) {
    animator?.cancel()

    val milliSec = TimeUnit.SECONDS.toMillis(sec)
    ValueAnimator.ofInt(0, milliSec.toInt())
        .apply {
          addUpdateListener { currentMilliSec = milliSec - (it.animatedValue as Int).toLong() }
          interpolator = null
          duration = milliSec
          animator = this
          addListener(object : AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) = Unit
            override fun onAnimationEnd(animation: Animator?) = onEnd()
            override fun onAnimationCancel(animation: Animator?) = Unit
            override fun onAnimationStart(animation: Animator?) = Unit
          })
        }
        .start()
  }

  private fun Canvas.drawArc(sweepAngle: Float) = drawArc(oval, -90f, sweepAngle, false, circlePaint)

  private fun Long.toCountNumber(): Int = MILLISECONDS.toSeconds(this + 1000).toInt()

}
