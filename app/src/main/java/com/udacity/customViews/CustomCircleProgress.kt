package com.udacity.customViews

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.graphics.toRect
import com.udacity.R


class CustomCircleProgress @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val PERCENTAGE_DIVIDER = 1
    private val ARC_FULL_ROTATION_DEGREE = 4

    private val ovalSpace = RectF()

    private var currentPercentage = 0

    private val drawable =resources.getDrawable( R.drawable.ic_round_check , null)


    companion object {
        const val PERCENTAGE_VALUE_HOLDER = "ARC_PERCENTAGE"
    }

    //-------------------- painters -----------------------

    //parent
    private val parentArcColor = resources.getColor(R.color.grayLight, null)
    private val parentArcPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true // smooth edges
        color = parentArcColor
        strokeWidth = 40f
    }

    //fill
    private val fillArcColor = resources.getColor(R.color.colorPrimary, null)
    private val fillArcPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true // smooth edges
        color = fillArcColor
        strokeWidth = 40f
        strokeCap = Paint.Cap.ROUND // make rounded cap at the end of the arc
    }

    //-------------------- overrides Functions -----------------------
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setSpace()
        canvas?.let {
            drawParentArc(it)
            drawFillArc(it)
            if(currentPercentage>=100)
            drawCheckIcon(it)
        }
    }

    //-------------------- private Functions -----------------------

    private fun setSpace() {
        val horizontalCenter = (width.div(2)).toFloat()
        val verticalCenter = (height.div(2)).toFloat()
        val ovalSize = 200
        ovalSpace.set(
            horizontalCenter - ovalSize,
            verticalCenter - ovalSize,
            horizontalCenter + ovalSize,
            verticalCenter + ovalSize
        )
    }

    private fun drawParentArc(canvas: Canvas) {
        canvas.drawArc(ovalSpace, 0f, 360f, false, parentArcPaint)
    }

    private fun drawCheckIcon(canvas: Canvas) {
        drawable.setTint(fillArcColor)
        drawable.bounds = ovalSpace.toRect()
       drawable.draw(canvas)
    }

    private fun drawFillArc(canvas: Canvas) {
        val fillPercentage = getCurrentPercentageToFill()
        canvas.drawArc(ovalSpace, 270f,  fillPercentage, false, fillArcPaint)

    }

    private fun getCurrentPercentageToFill() = (ARC_FULL_ROTATION_DEGREE * currentPercentage/PERCENTAGE_DIVIDER).toFloat()

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

    //-------------------- public Functions -----------------------
    fun animateProgress() {
        val valueHolder = PropertyValuesHolder.ofFloat(
            PERCENTAGE_VALUE_HOLDER,
            0f, 100f
        )

        val animator = ValueAnimator().apply {
            setValues(valueHolder)
            duration = 4000
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener {
                val percentage = it.getAnimatedValue(PERCENTAGE_VALUE_HOLDER) as Float
                currentPercentage = percentage.toInt()
                invalidate()
            }
        }
        if (!animator.isStarted)
            animator.start()
    }


}