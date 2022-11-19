package com.udacity.customViews

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.withStyledAttributes
import com.udacity.ButtonState
import com.udacity.R
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var loadingStateColor = 0
    private var normalStateColor = 0
    private var buttonText = "Download"
    private var textHorizontalOffset = 0f
    private var textVerticalOffset = 0f
    private var cornerRadius: Float = 20f
    private var progress: Float = 0f
    private var animationDuration: Long = 0L

    fun setProgress(value: Float) {
        progress = value
        invalidate()
    }

    fun getProgress(): Float = progress

    fun setDuration(value: Long) {
        animationDuration = value
    }


    companion object {
        const val PERCENTAGE_VALUE_HOLDER = "PERCENTAGE_VALUE_HOLDER"
    }

    //Paint object with a handful of basic styles.
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    //------------------------ initialization -----------------------------
    init {
        isClickable = true
        animationDuration = 4000

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonText = getString(R.styleable.LoadingButton_buttonText)
                ?: resources.getString(R.string.download)
            cornerRadius = getDimension(R.styleable.LoadingButton_cornerRadius, 20f)
            loadingStateColor = getColor(R.styleable.LoadingButton_loadingStateColor, Color.GRAY)
            normalStateColor = getColor(
                R.styleable.LoadingButton_normalStateColor,
                context.getColor(R.color.colorPrimary)
            )
        }
    }

    //-------------------- overrides Functions -----------------------
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {

            // draw the button background
            paint.color =
                if (isEnabled) context.getColor(R.color.colorPrimary) else context.getColor(R.color.grayDark)
            canvas.drawRoundRect(
                0f,
                0f,
                widthSize.toFloat(),
                heightSize.toFloat(),
                cornerRadius,
                cornerRadius,
                paint
            )
            canvas.save()

            // draw the button progress
            paint.color = context.getColor(R.color.colorPrimary)

            canvas.drawRoundRect(
                0f,
                0f,
                progress,
                heightSize.toFloat(),
                cornerRadius,
                cornerRadius,
                paint
            )

            canvas.save()

            // draw Download text on the top of the button
            paint.color = Color.WHITE
            canvas.drawText(
                buttonText,
                textHorizontalOffset,
                textVerticalOffset,
                paint
            )
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        textHorizontalOffset = (w / 2).toFloat()
        textVerticalOffset = (h / 2 + 15).toFloat()
        setMeasuredDimension(w, h)
    }

//    override fun performClick(): Boolean {
//        animateProgress()
//        if (super.performClick()) return true
//        return true
//    }


    //-------------------- public Functions -----------------------
    fun animateProgress() {
        val valueHolder = PropertyValuesHolder.ofFloat(
            PERCENTAGE_VALUE_HOLDER,
            0f, widthSize.toFloat() - 100
        )

        val animator = ValueAnimator().apply {
            setValues(valueHolder)
            duration = animationDuration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                val percentage = it.getAnimatedValue(PERCENTAGE_VALUE_HOLDER) as Float
                progress = percentage + 100
                invalidate()
            }
        }
        animator.disableViewDuringAnimation(this)
        if (!animator.isStarted)
            animator.start()
    }


    private fun ValueAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }
}