/*
 * Copyright (C) 2017 Muhammad Tayyab Akram
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

package com.mta.tehreer.util;

import com.mta.tehreer.internal.util.Description;
import com.mta.tehreer.internal.util.Primitives;

public abstract class PointList implements PrimitiveList {

    public abstract void copyTo(float[] array, int at, int from, int count, float scale);
    public abstract int size();

    public abstract float getX(int index);
    public abstract float getY(int index);

    public abstract void setX(int index, float value);
    public abstract void setY(int index, float value);

    public float[] toArray(float scale) {
        int length = size() * 2;
        float[] array = new float[length];
        copyTo(array, 0, 0, length, scale);

        return array;
    }

    public float[] toArray() {
        return toArray(1.0f);
    }

    @Override
    public boolean equals(Object obj) {
        return Primitives.equals(this, obj);
    }

    @Override
    public int hashCode() {
        return Primitives.hashCode(this);
    }

    @Override
    public String toString() {
        return Description.forPointList(this);
    }
}
