/*
 * Copyright (C) 2017-2018 Muhammad Tayyab Akram
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

package com.mta.tehreer.internal.collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mta.tehreer.collections.FloatList;
import com.mta.tehreer.internal.Raw;

import static com.mta.tehreer.internal.util.Preconditions.checkArrayBounds;
import static com.mta.tehreer.internal.util.Preconditions.checkElementIndex;
import static com.mta.tehreer.internal.util.Preconditions.checkIndexRange;
import static com.mta.tehreer.internal.util.Preconditions.checkNotNull;

public class Int32BufferFloatList extends FloatList {
    private final @Nullable Object owner;
    private final long pointer;
    private final int size;
    private final float scale;

    public Int32BufferFloatList(@Nullable Object owner, long pointer, int size, float scale) {
        this.owner = owner;
        this.pointer = pointer;
        this.size = size;
        this.scale = scale;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public float get(int index) {
        checkElementIndex(index, size);

        return Raw.getInt32Value(pointer + (index * Raw.INT32_SIZE)) * scale;
    }

    @Override
    public void copyTo(@NonNull float[] array, int atIndex) {
        checkNotNull(array);
        checkArrayBounds(array, atIndex, size);

        Raw.copyInt32Buffer(pointer, array, atIndex, size, scale);
    }

    @Override
    public @NonNull FloatList subList(int fromIndex, int toIndex) {
        checkIndexRange(fromIndex, toIndex, size);

        return new Int32BufferFloatList(owner, pointer + (fromIndex * Raw.INT32_SIZE), toIndex - fromIndex, scale);
    }
}
