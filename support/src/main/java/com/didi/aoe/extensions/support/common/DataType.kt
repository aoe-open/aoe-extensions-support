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

package com.didi.aoe.extensions.support.common

/**
 *
 *
 * @author noctis
 * @since 1.1.0
 */
enum class DataType constructor(value: Int) {
    FLOAT32(1),
    INT32(2),
    UINT8(3),
    INT64(4),
    STRING(5);

    fun byteSize(): Int {
        return when (this) {
            FLOAT32 -> 4
            INT32 -> 4
            UINT8 -> 1
            INT64 -> 8
            STRING -> -1
        }
    }
}