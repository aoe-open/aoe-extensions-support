/*
 * Copyright 2019 The AoE Authors. All Rights Reserved.
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
import com.didi.aoe.extensions.support.common.DataType
import com.didi.aoe.extensions.support.tensor.buffer.TensorBuffer


/**
 *
 *
 * @author noctis
 * @since 1.1.0
 */
class ImageConversions private constructor() {

    companion object {
        /**
         * Converts an Image in a TensorBuffer to a Bitmap, whose memory is already allocated.
         *
         * Notice: We only support ARGB_8888 at this point.
         *
         * @param buffer The TensorBuffer object representing the image. It should be an UInt8 buffer with
         * 3 dimensions: width, height, channel. Size of each dimension should be positive and the size of
         * channels should be 3 (representing R, G, B).
         * @param bitmap The destination of the conversion. Needs to be created in advance, needs to be
         * mutable, and needs to have the same width and height with the buffer.
         * @throws IllegalArgumentException 1) if the `buffer` is not uint8 (e.g. a float buffer),
         * or has an invalid shape. 2) if the `bitmap` is not mutable. 3) if the `bitmap` has
         * different height or width with the buffer.
         */
        @JvmStatic
        fun convertTensorBufferToBitmap(buffer: TensorBuffer, bitmap: Bitmap) {
            if (buffer.getDataType() != DataType.UINT8) {
                // We will add support to FLOAT format conversion in the future, as it may need other configs.
                throw UnsupportedOperationException("Converting TensorBuffer of type ${buffer.getDataType()} to Bitmap is not supported yet.")
            }
            val shape = buffer.shape
            if (shape.size != 3 || shape[0] <= 0 || shape[1] <= 0 || shape[2] != 3) {
                throw IllegalArgumentException(java.lang.String.format(
                        "Buffer shape %s is not valid. 3D TensorBuffer with shape [w, h, 3] is required",
                        shape.contentToString()))
            }
            val h = shape[0]
            val w = shape[1]
            if (bitmap.width != w || bitmap.height != h) {
                throw IllegalArgumentException(java.lang.String.format(
                        "Given bitmap has different width or height %s with the expected ones %s.",
                        intArrayOf(bitmap.width, bitmap.height).contentToString(),
                        intArrayOf(w, h).contentToString()))
            }
            if (!bitmap.isMutable) {
                throw IllegalArgumentException("Given bitmap is not mutable")
            }
            // TODO(b/138904567): Find a way to avoid creating multiple intermediate buffers every time.


            val intValues = IntArray(w * h)
            val rgbValues = buffer.getIntArray()
            var i = 0
            var j = 0
            while (i < intValues.size) {
                val r = rgbValues[j++]
                val g = rgbValues[j++]
                val b = rgbValues[j++]
                intValues[i] = r shl 16 or (g shl 8) or b
                i++
            }
            bitmap.setPixels(intValues, 0, w, 0, 0, w, h)
        }

        /**
         * Converts an Image in a Bitmap to a TensorBuffer (3D Tensor: Width-Height-Channel) whose memory
         * is already allocated, or could be dynamically allocated.
         *
         * @param bitmap The Bitmap object representing the image. Currently we only support ARGB_8888
         * config.
         * @param buffer The destination of the conversion. Needs to be created in advance. If it's
         * fixed-size, its flat size should be w*h*3.
         * @throws IllegalArgumentException if the buffer is fixed-size, but the size doesn't match.
         */
        @JvmStatic
        fun convertBitmapToTensorBuffer(bitmap: Bitmap, buffer: TensorBuffer) {
            val w = bitmap.width
            val h = bitmap.height
            val intValues = IntArray(w * h)
            bitmap.getPixels(intValues, 0, w, 0, 0, w, h)
            // TODO(b/138904567): Find a way to avoid creating multiple intermediate buffers every time.


            val rgbValues = IntArray(w * h * 3)
            var i = 0
            var j = 0
            while (i < intValues.size) {
                rgbValues[j++] = intValues[i] shr 16 and 0xFF
                rgbValues[j++] = intValues[i] shr 8 and 0xFF
                rgbValues[j++] = intValues[i] and 0xFF
                i++
            }
            val shape = intArrayOf(h, w, 3)
            buffer.loadArray(rgbValues, shape)
        }
    }

}