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

package com.mta.tehreer.internal;

public class Raw {

    public static native boolean isEqual(long pointer, long other, int bufferSize);
    public static native int getHash(long pointer, int bufferSize);

    public static native byte getInt8(long pointer, int arrayIndex);
    public static native int getInt32(long pointer, int arrayIndex);
    public static native int getUInt16(long pointer, int arrayIndex);

    public static native void putInt8(long pointer, int arrayIndex, byte value);
    public static native void putInt32(long pointer, int arrayIndex, int value);
    public static native void putUInt16(long pointer, int arrayIndex, int value);

    public static native byte[] toInt8Array(long pointer, int arraySize);
    public static native int[] toInt32Array(long pointer, int arraySize);
    public static native int[] toUInt16Array(long pointer, int arraySize);
}
