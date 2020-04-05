package com.slaviboy.infinitegridview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver

// Copyright (C) 2020 Stanislav Georgiev
//  https://github.com/slaviboy
//
//	This program is free software: you can redistribute it and/or modify
//	it under the terms of the GNU Affero General Public License as
//	published by the Free Software Foundation, either version 3 of the
//	License, or (at your option) any later version.
//
//	This program is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU Affero General Public License for more details.
//
//	You should have received a copy of the GNU Affero General Public License
//	along with this program.  If not, see <http://www.gnu.org/licenses/>.

/**
 * View for drawing a infinite grid view, that is transformed with the simple one or
 * two finger gesture. Supported transformations are scale, move and translate, custom
 * attributes are supported, that can change the color and line stroke width, distance
 * between lines.
 */
class InfiniteGridView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setAttributes(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setAttributes(context, attrs, defStyleAttr)
    }

    /**
     * Secondary constructor, used when a View is created using Kotlin,
     * instead of using in a layout using xml.
     */
    constructor(
        context: Context?,
        distanceBetweenVerticalLines: Float,
        distanceBetweenHorizontalLines: Float,
        verticalLinesStrokeWidthThick: Float,
        verticalLinesStrokeWidthNormal: Float,
        horizontalLinesStrokeWidthThick: Float,
        horizontalLinesStrokeWidthNormal: Float,
        verticalLinesThickLineIndex: Int,
        horizontalLinesThickLineIndex: Int,
        verticalNormalLinesColor: Int,
        verticalThickLinesColor: Int,
        horizontalNormalLinesColor: Int,
        horizontalThickLinesColor: Int
    ) : super(context) {
        this.distanceBetweenVerticalLines = distanceBetweenVerticalLines
        this.distanceBetweenHorizontalLines = distanceBetweenHorizontalLines
        this.verticalLinesStrokeWidthThick = verticalLinesStrokeWidthThick
        this.verticalLinesStrokeWidthNormal = verticalLinesStrokeWidthNormal
        this.horizontalLinesStrokeWidthThick = horizontalLinesStrokeWidthThick
        this.horizontalLinesStrokeWidthNormal = horizontalLinesStrokeWidthNormal
        this.verticalLinesThickLineIndex = verticalLinesThickLineIndex
        this.horizontalLinesThickLineIndex = horizontalLinesThickLineIndex
        this.verticalNormalLinesColor = verticalNormalLinesColor
        this.verticalThickLinesColor = verticalThickLinesColor
        this.horizontalNormalLinesColor = horizontalNormalLinesColor
        this.horizontalThickLinesColor = horizontalThickLinesColor
    }

    /**
     * Method called to get the xml attributes and then used them, as properties
     * for the class. Called only when the View is created using xml.
     */
    fun setAttributes(context: Context?, attrs: AttributeSet?, defStyleAttr: Int = 0) {
        val attributes =
            context!!.obtainStyledAttributes(attrs, R.styleable.InfiniteGridView, defStyleAttr, 0)

        distanceBetweenVerticalLines = attributes.getDimension(
            R.styleable.InfiniteGridView_distanceBetweenVerticalLines,
            DISTANCE_BETWEEN_LINES
        )
        distanceBetweenHorizontalLines = attributes.getDimension(
            R.styleable.InfiniteGridView_distanceBetweenHorizontalLines,
            DISTANCE_BETWEEN_LINES
        )
        verticalLinesStrokeWidthThick = attributes.getFloat(
            R.styleable.InfiniteGridView_verticalLinesStrokeWidthThick,
            STROKE_WIDTH_THICK
        )
        verticalLinesStrokeWidthNormal = attributes.getFloat(
            R.styleable.InfiniteGridView_verticalLinesStrokeWidthNormal,
            STROKE_WIDTH_NORMAL
        )
        horizontalLinesStrokeWidthThick = attributes.getFloat(
            R.styleable.InfiniteGridView_horizontalLinesStrokeWidthThick,
            STROKE_WIDTH_THICK
        )
        horizontalLinesStrokeWidthNormal = attributes.getFloat(
            R.styleable.InfiniteGridView_horizontalLinesStrokeWidthNormal,
            STROKE_WIDTH_NORMAL
        )
        verticalLinesThickLineIndex = attributes.getInteger(
            R.styleable.InfiniteGridView_verticalLinesThickLineIndex,
            THICK_LINE_INDEX
        )
        horizontalLinesThickLineIndex = attributes.getInteger(
            R.styleable.InfiniteGridView_horizontalLinesThickLineIndex,
            THICK_LINE_INDEX
        )
        verticalNormalLinesColor =
            attributes.getColor(R.styleable.InfiniteGridView_verticalNormalLinesColor, LINE_COLOR)
        verticalThickLinesColor =
            attributes.getColor(R.styleable.InfiniteGridView_verticalThickLinesColor, LINE_COLOR)
        horizontalNormalLinesColor =
            attributes.getColor(R.styleable.InfiniteGridView_horizontalNormalLinesColor, LINE_COLOR)
        horizontalThickLinesColor =
            attributes.getColor(R.styleable.InfiniteGridView_horizontalThickLinesColor, LINE_COLOR)

        attributes.recycle()
    }


    // global paint object
    var paint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    lateinit var infiniteGrid: InfiniteGrid           // infinite grid class that generates and can draw lines

    var matrixGestureDetector: MatrixGestureDetector  // transform matrix with finger gestures - rotation, scale and move
    var fingerDownTime: Long                          // how long the finger is down, used to detect click or long click with touch events
    var isMoved: Boolean                              // if finger is moved, used to detect click or long click with touch events
    var consumedTouchEvent: Boolean                   // whether or not to consume the touch event

    var distanceBetweenVerticalLines: Float           // distance between each two vertical lines
    var distanceBetweenHorizontalLines: Float         // distance between each two horizontal lines
    var verticalLinesStrokeWidthThick: Float          // stroke width for the thick vertical lines
    var verticalLinesStrokeWidthNormal: Float         // stroke width for the normal vertical lines
    var horizontalLinesStrokeWidthThick: Float        // stroke width for the thick horizontal lines
    var horizontalLinesStrokeWidthNormal: Float       // stroke width for the normal horizontal lines
    var verticalLinesThickLineIndex: Int              // through how many normal lines a thick line will be drawn for vertical lines
    var horizontalLinesThickLineIndex: Int            // through how many normal lines a thick line will be drawn for horizontal lines
    var verticalNormalLinesColor: Int                 // color for normal vertical lines
    var verticalThickLinesColor: Int                  // color for thick vertical lines
    var horizontalNormalLinesColor: Int               // color for normal horizontal lines
    var horizontalThickLinesColor: Int                // color for thick horizontal lines

    init {

        // init values used by touch events
        fingerDownTime = 0
        isMoved = false
        matrixGestureDetector = MatrixGestureDetector()
        consumedTouchEvent = true

        // init values fot
        distanceBetweenVerticalLines = DISTANCE_BETWEEN_LINES
        distanceBetweenHorizontalLines = DISTANCE_BETWEEN_LINES
        verticalLinesStrokeWidthThick = STROKE_WIDTH_THICK
        verticalLinesStrokeWidthNormal = STROKE_WIDTH_NORMAL
        horizontalLinesStrokeWidthThick = STROKE_WIDTH_THICK
        horizontalLinesStrokeWidthNormal = STROKE_WIDTH_NORMAL
        verticalLinesThickLineIndex = THICK_LINE_INDEX
        horizontalLinesThickLineIndex = THICK_LINE_INDEX
        verticalNormalLinesColor = LINE_COLOR
        verticalThickLinesColor = LINE_COLOR
        horizontalNormalLinesColor = LINE_COLOR
        horizontalThickLinesColor = LINE_COLOR

        this.afterMeasured {

            // lines coordinates for the canvas bound
            val canvasBoundLinesCoordinates = floatArrayOf(
                0f, 0f, width.toFloat(), 0f,                                // top side    -QR
                0f, height.toFloat(), width.toFloat(), height.toFloat(),    // bottom side -MN
                0f, 0f, 0f, height.toFloat(),                               // left side   -QM
                width.toFloat(), 0f, width.toFloat(), height.toFloat()      // right side  -RN
            )

            // the original base points
            val originalBasePoints = floatArrayOf(
                width / 2f, 0f,                     // C
                0f, height / 2f,                    // A
                width / 2f, height / 2f,            // O
                width / 2f, height.toFloat(),       // D
                width.toFloat(), height / 2f        // B
            )

            infiniteGrid = InfiniteGrid(
                width.toFloat(),
                height.toFloat(),
                originalBasePoints,
                canvasBoundLinesCoordinates
            )
        }
    }

    companion object {

        /**
         * Inline function that is called, when the final measurement is made and
         * the view is about to be draw.
         */
        inline fun View.afterMeasured(crossinline function: View.() -> Unit) {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (measuredWidth > 0 && measuredHeight > 0) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        function()
                    }
                }
            })
        }

        const val DISTANCE_BETWEEN_LINES: Float = 20f
        const val STROKE_WIDTH_THICK: Float = 3f
        const val STROKE_WIDTH_NORMAL: Float = 1f
        const val THICK_LINE_INDEX: Int = 5
        const val LINE_COLOR: Int = Color.BLACK
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val action = event.actionMasked

        when (action) {

            MotionEvent.ACTION_DOWN -> {
                fingerDownTime = System.currentTimeMillis()
            }

            MotionEvent.ACTION_UP -> {
                val passedTime: Long = System.currentTimeMillis() - fingerDownTime

                if (!isMoved && passedTime < 500) {
                    // detect click events
                }
                isMoved = false
            }

            MotionEvent.ACTION_MOVE -> {
                isMoved = true
            }
        }

        matrixGestureDetector.onTouchEvent(event)

        invalidate()
        return consumedTouchEvent
    }

    override fun onDraw(canvas: Canvas?) {

        // update the parallel lines arrays
        infiniteGrid.updateParallelLines(
            matrixGestureDetector.matrix,
            distanceBetweenHorizontalLines,
            distanceBetweenVerticalLines,
            verticalLinesThickLineIndex,
            horizontalLinesThickLineIndex
        )

        // draw the parallel lines
        infiniteGrid.drawParallelLines(
            canvas!!, paint,
            horizontalNormalLinesColor,
            horizontalThickLinesColor,
            verticalNormalLinesColor,
            verticalThickLinesColor,
            verticalLinesStrokeWidthThick,
            verticalLinesStrokeWidthNormal,
            horizontalLinesStrokeWidthThick,
            horizontalLinesStrokeWidthNormal
        )
    }
}

 