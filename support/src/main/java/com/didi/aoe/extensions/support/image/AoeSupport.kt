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

/**
 * 常用jni操作方法
 *
 * * 基于yuv的图像处理
 *
 * @author noctis
 * @since 1.1.0
 */
class AoeSupport {

    companion object {
        init {
            System.loadLibrary("aoe_support")
        }

        /**
         * 常用的图像模型处理格式一般是ARGB8888的，Android相机默认输出格式为NV21，通过yuv库NV21ToABGR方法进行直接转换
         */
        @JvmStatic
        external fun convertNV21ToARGB8888(nv21Src: ByteArray, srcWidth: Int, srcHeight: Int): ByteArray

        /**
         * 提供ABGR格式图像的裁剪方法
         * Note: 内部对长宽越界进行了容错，所以如果输入越界，输出结果长宽不一定与目标值匹配
         */
        @JvmStatic
        external fun cropABGR(abgrSrc: ByteArray,
                srcWidth: Int, srcHeight: Int,
                cropX: Int, cropY: Int,
                cropWidth: Int, cropHeight: Int
        ): ByteArray

        /**
         * 提供ABGR格式图像的旋转方法
         */
        @JvmStatic
        external fun rotateARGB(argbSrc: ByteArray, srcWidth: Int, srcHeight: Int, degree: Int): ByteArray

        /**
         * 提供JAVA层BGRA/RGBA格式图像的Scale方法
         */
        @JvmStatic
        external fun scaleBGRA(bgraSrc: ByteArray, srcWidth: Int, srcHeight: Int, dstWidth: Int,
                               dstHeight: Int, filterMode: Int): ByteArray
    }
}