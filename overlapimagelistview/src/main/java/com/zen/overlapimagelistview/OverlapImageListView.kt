package com.zen.overlapimagelistview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min
import kotlin.math.roundToInt

class OverlapImageListView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        const val DEFAULT_FILL_COLOR = Color.BLACK
        const val DEFAULT_STROKE_COLOR = Color.WHITE
        const val DEFAULT_STROKE_WIDTH = 2F
        const val DEFAULT_CIRCLE_COUNT = 1

        const val DEFAULT_MIN_CIRCLE_COUNT = 1
        const val DEFAULT_MAX_CIRCLE_COUNT = 50
    }

    var fillColor = DEFAULT_FILL_COLOR
        set(value) {
            field = value
            invalidate()
        }

    var strokeColor = DEFAULT_STROKE_COLOR
        set(value) {
            field = value
            invalidate()
        }

    var strokeWidth = DEFAULT_STROKE_WIDTH
        set(value) {
            field = value
            invalidate()
        }

    var circleCount = DEFAULT_CIRCLE_COUNT
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var size: Float? = null
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    var image: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }

    var imageList: ArrayList<Bitmap>? = null
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.isAntiAlias = true

        if (circleCount > DEFAULT_MAX_CIRCLE_COUNT) {
            circleCount = DEFAULT_MAX_CIRCLE_COUNT
        }

        if (circleCount < DEFAULT_MIN_CIRCLE_COUNT) {
            circleCount = DEFAULT_MIN_CIRCLE_COUNT
        }

        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.OverlapImageListView, 0, 0)

        fillColor = typedArray.getColor(R.styleable.OverlapImageListView_fillColor, DEFAULT_FILL_COLOR)
        strokeColor = typedArray.getColor(R.styleable.OverlapImageListView_strokeColor, DEFAULT_STROKE_COLOR)
        strokeWidth = typedArray.getDimension(R.styleable.OverlapImageListView_strokeWidth, DEFAULT_STROKE_WIDTH)
        circleCount = typedArray.getInt(R.styleable.OverlapImageListView_circleCount, DEFAULT_CIRCLE_COUNT)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizeIfFollowWidth = calculateCircleSize(measuredWidth.toFloat(), circleCount)
        val sizeIfFollowHeight = measuredHeight.toFloat()
        val chosenSize = min(sizeIfFollowWidth, sizeIfFollowHeight).roundToInt().toFloat()

        // size will be overwritten if the layout is too small
        if (size == null || size!! > chosenSize) {
            size = chosenSize
        }

        setMeasuredDimension(calculateMaxWidth(size!!, circleCount).roundToInt(), size!!.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        try {
            val radius = size!! / 2F
            var counter = 0

            while (counter < circleCount) {
                // handle color
                paint.color = fillColor
                paint.style = Paint.Style.FILL
                canvas.drawCircle(radius * (counter + 1), radius, radius, paint)

                // handle image
                if (!imageList.isNullOrEmpty()) {
                    val bitmap = imageList!![counter % imageList!!.size]
                    canvas.drawBitmap(bitmap, null, getImageRectF(radius, counter), null)
                } else if (image != null) {
                    canvas.drawBitmap(image!!, null, getImageRectF(radius, counter), null)
                }

                // handle stroke
                if (strokeWidth > size!! / 3) {
                    strokeWidth = size!! / 3
                }

                if (strokeWidth < DEFAULT_STROKE_WIDTH) {
                    strokeWidth = DEFAULT_STROKE_WIDTH
                }

                paint.color = strokeColor
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth

                canvas.drawCircle(radius * (counter + 1), radius, radius - strokeWidth / 4F, paint)

                counter += 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateMaxWidth(circleSize: Float, circleCount: Int): Float {
        return circleSize * circleCount - ((circleCount - 1) * circleSize / 2)
    }

    private fun calculateCircleSize(maxWidth: Float, circleCount: Int): Float {
        return 2 * maxWidth / (circleCount + 1)
    }

    private fun getImageRectF(radius: Float, counter: Int): RectF {
        return RectF(radius * counter, 0F, radius * counter + radius * 2, radius * 2)
    }
}