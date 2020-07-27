/*
 * Copyright (C) 2020 Stanislav Georgiev
 * https://github.com/slaviboy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.slaviboy.infinitegridview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver

/**
 * View for drawing a infinite grid view, that is transformed with the simple one or
 * two finger gesture. Supported transformations are scale, move and translate, custom
 * attributes are supported, that can change the color and line stroke width, distance
 * between lines.
 */
class InfiniteGridView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setAttributes(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setAttributes(context, attrs, defStyleAttr)
    }

    // global paint object
    var paint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    var infiniteGrid: InfiniteGrid   // infinite grid class that generates and can draw lines

    init {

        infiniteGrid = InfiniteGrid()
        this.afterMeasured {
            infiniteGrid.setViewSize(width.toFloat(), height.toFloat())
        }
    }

    /**
     * Method called to get the xml attributes and then used them, as properties
     * for the class. Called only when the View is created using xml.
     * @param context context for the view
     * @param attrs attribute set when properties are set using the xml
     */
    fun setAttributes(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) {
        val attributes =
            context!!.obtainStyledAttributes(attrs, R.styleable.InfiniteGridView, defStyleAttr, 0)

        infiniteGrid.distanceBetweenVerticalLines = attributes.getDimension(
            R.styleable.InfiniteGridView_distanceBetweenVerticalLines,
            InfiniteGrid.DISTANCE_BETWEEN_LINES
        )
        infiniteGrid.distanceBetweenHorizontalLines = attributes.getDimension(
            R.styleable.InfiniteGridView_distanceBetweenHorizontalLines,
            InfiniteGrid.DISTANCE_BETWEEN_LINES
        )
        infiniteGrid.verticalLinesStrokeWidthThick = attributes.getFloat(
            R.styleable.InfiniteGridView_verticalLinesStrokeWidthThick,
            InfiniteGrid.STROKE_WIDTH_THICK
        )
        infiniteGrid.verticalLinesStrokeWidthNormal = attributes.getFloat(
            R.styleable.InfiniteGridView_verticalLinesStrokeWidthNormal,
            InfiniteGrid.STROKE_WIDTH_NORMAL
        )
        infiniteGrid.horizontalLinesStrokeWidthThick = attributes.getFloat(
            R.styleable.InfiniteGridView_horizontalLinesStrokeWidthThick,
            InfiniteGrid.STROKE_WIDTH_THICK
        )
        infiniteGrid.horizontalLinesStrokeWidthNormal = attributes.getFloat(
            R.styleable.InfiniteGridView_horizontalLinesStrokeWidthNormal,
            InfiniteGrid.STROKE_WIDTH_NORMAL
        )
        infiniteGrid.verticalLinesThickLineIndex = attributes.getInteger(
            R.styleable.InfiniteGridView_verticalLinesThickLineIndex,
            InfiniteGrid.THICK_LINE_INDEX
        )
        infiniteGrid.horizontalLinesThickLineIndex = attributes.getInteger(
            R.styleable.InfiniteGridView_horizontalLinesThickLineIndex,
            InfiniteGrid.THICK_LINE_INDEX
        )
        infiniteGrid.verticalNormalLinesColor =
            attributes.getColor(R.styleable.InfiniteGridView_verticalNormalLinesColor, InfiniteGrid.LINE_COLOR)
        infiniteGrid.verticalThickLinesColor =
            attributes.getColor(R.styleable.InfiniteGridView_verticalThickLinesColor, InfiniteGrid.LINE_COLOR)
        infiniteGrid.horizontalNormalLinesColor =
            attributes.getColor(R.styleable.InfiniteGridView_horizontalNormalLinesColor, InfiniteGrid.LINE_COLOR)
        infiniteGrid.horizontalThickLinesColor =
            attributes.getColor(R.styleable.InfiniteGridView_horizontalThickLinesColor, InfiniteGrid.LINE_COLOR)

        attributes.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        // update transformations to the gesture detector using the finger gestures applied by the user
        infiniteGrid.matrixGestureDetector.onTouchEvent(event)

        // force redrawing of the view
        invalidate()

        return true
    }

    override fun onDraw(canvas: Canvas?) {

        // update the parallel lines arrays
        infiniteGrid.updateParallelLines()

        // draw all parallel lines
        infiniteGrid.drawParallelLines(canvas!!, paint)
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
    }
}

 