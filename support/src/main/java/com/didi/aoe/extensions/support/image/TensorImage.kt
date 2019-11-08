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

package com.didi.aoe.extensions.support.image

import android.graphics.Bitmap
import androidx.annotation.NonNull
import com.didi.aoe.extensions.support.common.DataType
import com.didi.aoe.extensions.support.tensor.buffer.TensorBuffer
import com.didi.aoe.extensions.support.utils.getCompatAllocationByteCount
import java.nio.ByteBuffer


/**
 *
 *
 * @author noctis
 * @since 1.1.0
 */
class TensorImage(val dataType: DataType) {
    private val container: ImageContainer

    init {
        check(dataType == DataType.UINT8 || dataType == DataType.FLOAT32) { "Illegal data type for TensorImage: Only FLOAT32 and UINT8 are accepted" }
        container = ImageContainer(dataType)
    }

    fun load(@NonNull bitmap: Bitmap) {
        check(bitmap.config == Bitmap.Config.ARGB_8888) { "Only supports loading ARGB_8888 bitmaps." }
        container.set(bitmap)
    }

    fun load(@NonNull pixels: FloatArray, @NonNull shape: IntArray) {
        check(shape.size == 3 && shape[2] == 3) { "Only supports image shape in (h, w, c), and channels representing R, G, B in order." }
        val buffer = TensorBuffer.createDynamic(dataType)
        buffer.loadArray(pixels, shape)
        load(buffer)
    }

    fun load(@NonNull pixels: IntArray, @NonNull shape: IntArray) {
        check(shape.size == 3 && shape[2] == 3) { "Only supports image shape in (h, w, c), and channels representing R, G, B in order." }
        val buffer = TensorBuffer.createDynamic(dataType)
        buffer.loadArray(pixels, shape)
        load(buffer)
    }

    fun load(buffer: TensorBuffer) {
        container.set(buffer)
    }

    @NonNull
    fun getBitmap(): Bitmap {
        return container.getBitmap()
    }

    @NonNull
    fun getTensorBuffer(): TensorBuffer {
        return container.getTensorBuffer()
    }

    @NonNull
    fun getBuffer(): ByteBuffer {
        return getTensorBuffer().buffer
    }

    private class ImageContainer constructor(private val dataType: DataType) {
        private var bufferImage: TensorBuffer? = null
        private var isBufferUpdated: Boolean = false
        private var bitmapImage: Bitmap? = null
        private var isBitmapUpdated: Boolean = false

        private val ARGB_8888_ELEMENT_BYTES = 4

        // Internal method to set the image source-of-truth with a bitmap. The bitmap has to be
        // ARGB_8888.
        fun set(bitmap: Bitmap) {
            bitmapImage = bitmap
            isBufferUpdated = false
            isBitmapUpdated = true
        }

        // Internal method to set the image source-of-truth with a TensorBuffer.
        fun set(buffer: TensorBuffer) {
            bufferImage = buffer
            isBitmapUpdated = false
            isBufferUpdated = true
        }

        fun getDataType(): DataType {
            return dataType
        }

        // Internal method to update the internal Bitmap data by TensorBuffer data.
        fun getBitmap(): Bitmap {
            if (isBitmapUpdated) {
                return bitmapImage!!
            }
            check(isBufferUpdated) { "Both buffer and bitmap data are obsolete." }
            check(bufferImage?.getDataType() == DataType.UINT8) { "TensorImage is holding a float-value image which is not able to convert a Bitmap." }
            val requiredAllocation = bufferImage!!.flatSize.times(ARGB_8888_ELEMENT_BYTES)
            // Create a new bitmap and reallocate memory for it.
            if (bitmapImage == null || bitmapImage!!.getCompatAllocationByteCount() < requiredAllocation) {
                val shape = bufferImage!!.shape
                val w = shape[0]
                val h = shape[1]
                bitmapImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            }
            ImageConversions.convertTensorBufferToBitmap(bufferImage!!, bitmapImage!!)
            isBitmapUpdated = true
            return bitmapImage!!
        }

        // Internal method to update the internal TensorBuffer data by Bitmap data.
        fun getTensorBuffer(): TensorBuffer {
            if (isBufferUpdated) {
                return bufferImage!!
            }
            check(isBitmapUpdated) { "Both buffer and bitmap data are obsolete." }
            val requiredFlatSize = bitmapImage!!.width * bitmapImage!!.height * 3
            if (bufferImage == null || !bufferImage!!.isDynamic && bufferImage!!.flatSize != requiredFlatSize) {
                bufferImage = TensorBuffer.createDynamic(dataType)
            }
            ImageConversions.convertBitmapToTensorBuffer(bitmapImage!!, bufferImage!!)
            isBufferUpdated = true
            return bufferImage!!
        }
    }
}