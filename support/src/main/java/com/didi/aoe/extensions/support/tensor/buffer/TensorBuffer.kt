/*
 * Copyright 2019 The AoE Authors.
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.didi.aoe.extensions.support.tensor.buffer

import com.didi.aoe.extensions.support.common.DataType
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 *
 *
 * @author noctis
 * @since 1.1.0
 */
abstract class TensorBuffer {
    lateinit var buffer: ByteBuffer
        protected set
    lateinit var shape: IntArray
        protected set
    var flatSize: Int = -1
        protected set
    val isDynamic: Boolean

    constructor() {
        isDynamic = true
        allocateMemory(IntArray(1))
    }

    constructor(shape: IntArray) {
        isDynamic = false
        allocateMemory(shape)
    }

    abstract fun getDataType(): DataType

    abstract fun getIntArray(): IntArray
    abstract fun getFloatArray(): FloatArray

    abstract fun getTypeSize(): Int

    abstract fun loadArray(src: IntArray, shape: IntArray)
    abstract fun loadArray(src: FloatArray, shape: IntArray)

    fun loadBuffer(buffer: ByteBuffer) {
        loadBuffer(buffer, shape)
    }

    fun loadBuffer(buffer: ByteBuffer, shape: IntArray) {
        val flatSize = computeFlatSize(shape)
        check(buffer.limit() == flatSize * getTypeSize()) { "The size of byte buffer and the shape do not match." }
        if (isDynamic) {
            this.flatSize = flatSize
        } else {
            check(this.flatSize == flatSize) { "The size of byte buffer and the size of the tensor buffer do not match." }
        }
        this.shape = shape.clone()
        buffer.rewind()
        this.buffer = buffer
    }

    protected fun resize(shape: IntArray) {
        if (isDynamic) {
            allocateMemory(shape)
        } else {
            check(flatSize == computeFlatSize(shape))
            this.shape = shape.clone()
        }
    }

    private fun allocateMemory(shape: IntArray) {
        val newFlatSize = computeFlatSize(shape)
        if (flatSize == newFlatSize) {
            return
        }
        flatSize = newFlatSize
        this.shape = shape.clone()
        buffer = ByteBuffer.allocateDirect(flatSize * getTypeSize())
        buffer.order(ByteOrder.nativeOrder())
    }

    companion object {
        @JvmStatic
        fun createFixedSize(shape: IntArray, dataType: DataType): TensorBuffer {
            return when (dataType) {
                DataType.FLOAT32 -> TensorBufferFloat(shape)
                DataType.UINT8 -> TensorBufferUint8(shape)
                else -> throw IllegalStateException("TensorBuffer does not support data type: $dataType")
            }
        }

        @JvmStatic
        fun createDynamic(dataType: DataType): TensorBuffer {
            return when (dataType) {
                DataType.FLOAT32 -> TensorBufferFloat()
                DataType.UINT8 -> TensorBufferUint8()
                else -> throw IllegalStateException("TensorBuffer does not support data type: $dataType")
            }
        }

        @JvmStatic
        fun computeFlatSize(shape: IntArray): Int {
            return shape.fold(1, { x, y -> x * y })
        }

        @JvmStatic
        private fun isShapeValid(shape: IntArray): Boolean {
            if (shape.isEmpty()) {
                return true
            }
            for (dim: Int in shape) {
                if (dim < 0) {
                    return false
                }
            }
            return true
        }
    }

}