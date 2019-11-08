/*
 * Copyright 2019 The AoE Authors. All Rights Reserved.
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
import kotlin.experimental.and


/**
 *
 *
 * @author noctis
 * @since 1.1.0
 */
class TensorBufferUint8 : TensorBuffer {

    constructor() : super()

    constructor(shape: IntArray) : super(shape)

    override fun getDataType(): DataType {
        return DataType.UINT8
    }

    override fun getIntArray(): IntArray {
        buffer.rewind()
        val arr = IntArray(flatSize)
        for (i in arr.indices) {
            arr[i] = (buffer.get() and 0xff.toByte()).toInt()
        }
        return arr
    }

    override fun getFloatArray(): FloatArray {
        buffer.rewind()
        val arr = FloatArray(flatSize)
        for (i in arr.indices) {
            arr[i] = (buffer.get() and 0xff.toByte()).toFloat()
        }
        return arr
    }

    override fun getTypeSize(): Int {
        return DataType.UINT8.byteSize()
    }

    override fun loadArray(src: IntArray, shape: IntArray) {
        check(src.size == computeFlatSize(shape)) { "The size of the array to be loaded does not match the specified shape." }
        resize(shape)
        buffer.rewind()

        for (a in src) {
            buffer.put(Math.max(Math.min(a.toDouble(), 255.0), 0.0).toByte())
        }
    }

    override fun loadArray(src: FloatArray, shape: IntArray) {
        check(src.size == computeFlatSize(shape)) { "The size of the array to be loaded does not match the specified shape." }
        resize(shape)
        buffer.rewind()
        for (a in src) {
            buffer.put(Math.max(Math.min(a.toInt(), 255), 0).toByte())
        }
    }
}