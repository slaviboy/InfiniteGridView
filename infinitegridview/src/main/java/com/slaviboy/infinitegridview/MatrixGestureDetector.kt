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

import android.graphics.Matrix
import android.view.MotionEvent

/**
 * Gesture detection using a transformation matrix, the changes according to
 * gestures made by the user, supported gestures are Move, Scale and Rotate.
 * All transformations are then applied to a matrix, that can be used to
 * transform array with coordinates as float array, paths or canvas elements.
 * @param listener listener with callback, triggered when new transformations are done
 */
open class MatrixGestureDetector(listener: OnMatrixChangeListener? = null) {
    private var pointerIndex: Int
    private var tempMatrix: Matrix
    private var source: FloatArray
    private var distance: FloatArray
    private var count: Int

    var matrix: Matrix = Matrix()
    lateinit var listener: OnMatrixChangeListener

    init {
        if (listener != null) {
            this.listener = listener
        }

        count = 0
        pointerIndex = 0
        tempMatrix = Matrix()
        source = FloatArray(4)
        distance = FloatArray(4)
    }

    fun onTouchEvent(event: MotionEvent) {

        // only two fingers
        if (event.pointerCount > 2) {
            return
        }

        val action = event.actionMasked
        val index = event.actionIndex
        when (action) {

            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {

                // get the coordinates for the particular finger
                val idx = index * 2
                source[idx] = event.getX(index)
                source[idx + 1] = event.getY(index)

                count++
                pointerIndex = 0
            }

            MotionEvent.ACTION_MOVE -> {
                var i = 0
                while (i < count) {
                    val idx = pointerIndex + i * 2
                    distance[idx] = event.getX(i)
                    distance[idx + 1] = event.getY(i)
                    i++
                }

                // use poly to poly to detect transformations
                tempMatrix.setPolyToPoly(source, pointerIndex, distance, pointerIndex, count)
                matrix.postConcat(tempMatrix)

                if (::listener.isInitialized) {
                    listener.onChange(matrix)
                }

                System.arraycopy(distance, 0, source, 0, distance.size)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (event.getPointerId(index) == 0) pointerIndex = 2
                count--
            }
        }
    }

    /**
     * Listener triggered when the matrix is changed when new gesture is detected from
     * the user. The first argument is the matrix with the updated transformations.
     */
    interface OnMatrixChangeListener {
        fun onChange(matrix: Matrix)
    }
}