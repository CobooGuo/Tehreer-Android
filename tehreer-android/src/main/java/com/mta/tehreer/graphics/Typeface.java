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

package com.mta.tehreer.graphics;

import android.content.res.AssetManager;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;

import com.mta.tehreer.internal.Sustain;
import com.mta.tehreer.opentype.SfntTag;

import java.io.File;
import java.io.InputStream;

/**
 * The Typeface class specifies the typeface and intrinsic style of a font. This is used in the
 * renderer, along with optionally Renderer settings like typeSize, slantAngle, scaleX, to specify
 * how text appears when drawn (and measured).
 */
public class Typeface {

    private class Finalizable {

        @Override
        protected void finalize() throws Throwable {
            try {
                dispose();
            } finally {
                super.finalize();
            }
        }
    }

    @Sustain
    long nativeTypeface;
    @Sustain
    private final Finalizable finalizable = new Finalizable();
    private final TypefaceDescription description;

    /**
     * Creates a new typeface from the specified asset. The data of the asset is not copied into the
     * memory. Rather, it is directly read from the stream when needed. So the performance of
     * resulting typeface might be slower and should be used with caution.
     *
     * @param assetManager The application's asset manager.
     * @param path The path of the font file in the assets directory.
     * @return The new typeface, or <code>null</code> if an error occurred while creating it.
     */
    public static Typeface create(AssetManager assetManager, String path) {
        long nativeTypeface = nativeCreateWithAsset(assetManager, path);
        if (nativeTypeface != 0) {
            return new Typeface(nativeTypeface);
        }

        return null;
    }

    /**
     * Creates a new typeface with the specified file. The data for the font is directly read from
     * the file when needed.
     *
     * @param file The font file.
     * @return The new typeface, or <code>null</code> if an error occurred while creating it.
     */
    public static Typeface create(File file) {
        long nativeTypeface = nativeCreateWithFile(file.getAbsolutePath());
        if (nativeTypeface != 0) {
            return new Typeface(nativeTypeface);
        }

        return null;
    }

    /**
     * Create a new typeface from the input stream by copying its data into a native memory buffer.
     * So it may take time to create the typeface if the stream holds larger data.
     *
     * @param stream The input stream that contains the data of the font.
     * @return The new typeface, or <code>null</code> if an error occurred while creating it.
     */
    public static Typeface create(InputStream stream) {
        long nativeTypeface = nativeCreateFromStream(stream);
        if (nativeTypeface != 0) {
            return new Typeface(nativeTypeface);
        }

        return null;
    }

	private Typeface(long nativeTypeface) {
	    this.nativeTypeface = nativeTypeface;
        this.description = new TypefaceDescription(this);
	}

    public String getFamilyName() {
        return description.getFamilyName();
    }

    public String getFaceName() {
        return description.getFaceName();
    }

    public TypeStyle getStyle() {
        return description.getStyle();
    }

    public TypeWeight getWeight() {
        return description.getWeight();
    }

    public TypeStretch getStretch() {
        return description.getStretch();
    }

    /**
     * Generates an array of bytes containing the data of the intended table.
     *
     * @param tableTag The tag of the table as an integer. It can be created from string by using
     *                 {@link SfntTag#make(String)} method.
     * @return An array of bytes containing the data of the table, or <code>null</code> if no such
     *         table exists.
     */
    public byte[] getTableData(int tableTag) {
        return nativeGetTableData(nativeTypeface, tableTag);
    }

    /**
     * Returns the number of font units per EM square for this typeface.
     *
     * @return The number of font units per EM square for this typeface.
     */
	public int getUnitsPerEm() {
		return nativeGetUnitsPerEm(nativeTypeface);
	}

    /**
     * Returns the typographic ascender of this typeface expressed in font units.
     *
     * @return The typographic ascender of this typeface expressed in font units.
     */
	public int getAscent() {
		return nativeGetAscent(nativeTypeface);
	}

    /**
     * Returns the typographic descender of this typeface expressed in font units.
     *
     * @return The typographic descender of this typeface expressed in font units.
     */
	public int getDescent() {
		return nativeGetDescent(nativeTypeface);
	}

    /**
     * Returns the typographic leading of this typeface expressed in font units.
     *
     * @return The typographic leading of this typeface expressed in font units.
     */
    public int getLeading() {
        return nativeGetLeading(nativeTypeface);
    }

    /**
     * Returns the number of glyphs in this typeface.
     *
     * @return The number of glyphs in this typeface.
     */
	public int getGlyphCount() {
        return nativeGetGlyphCount(nativeTypeface);
    }

    /**
     * Returns the glyph id for the specified code point.
     *
     * @param codePoint The code point for which the glyph id is obtained.
     * @return The glyph id for the specified code point.
     */
    public int getGlyphId(int codePoint) {
        return nativeGetGlyphId(nativeTypeface, codePoint);
    }

    /**
     * Retrieves the advance for the specified glyph.
     *
     * @param glyphId The glyph id for which to retrieve the advance.
     * @param typeSize The size for which the advance is retrieved.
     * @param vertical The flag which indicates the type of advance, either horizontal or vertical.
     * @return The advance for the specified glyph.
     */
    public float getGlyphAdvance(int glyphId, float typeSize, boolean vertical) {
        return nativeGetGlyphAdvance(nativeTypeface, glyphId, typeSize, vertical);
    }

    /**
     * Generates the path for the specified glyph.
     *
     * @param glyphId The glyph id for which the path is generated.
     * @param typeSize The size for which the glyph path is required.
     * @param matrix The matrix applied to the path. Can be <code>null</code> if no transformation
     *               is required.
     * @return The path for the specified glyph.
     */
    public Path getGlyphPath(int glyphId, float typeSize, Matrix matrix) {
        float[] values = null;
        if (matrix != null) {
            values = new float[9];
            matrix.getValues(values);
        }

        return nativeGetGlyphPath(nativeTypeface, glyphId, typeSize, values);
    }

    /**
     * Returns the font bounding box expressed in font units. The box is large enough to contain any
     * glyph from the font.
     *
     * @return The font bounding box expressed in font units.
     */
	public Rect getBoundingBox() {
	    Rect boundingBox = new Rect();
	    nativeGetBoundingBox(nativeTypeface, boundingBox);

	    return boundingBox;
	}

    /**
     * Returns the position, in font units, of the underline for this typeface.
     *
     * @return The position, in font units, of the underline for this typeface.
     */
	public int getUnderlinePosition() {
	    return nativeGetUnderlinePosition(nativeTypeface);
	}

    /**
     * Returns the thickness, in font units, of the underline for this typeface.
     *
     * @return The thickness, in font units, of the underline for this typeface.
     */
	public int getUnderlineThickness() {
	    return nativeGetUnderlineThickness(nativeTypeface);
	}

    void dispose() {
        nativeDispose(nativeTypeface);
    }

    @Override
    public String toString() {
        return "Typeface{unitsPerEm=" + getUnitsPerEm()
                + ", ascent=" + getAscent()
                + ", descent=" + getDescent()
                + ", leading=" + getLeading()
                + ", glyphCount=" + getGlyphCount()
                + ", boundingBox=" + getBoundingBox().toString()
                + ", underlinePosition=" + getUnderlinePosition()
                + ", underlineThickness=" + getUnderlineThickness()
                + "}";
    }

    private static native long nativeCreateWithAsset(AssetManager assetManager, String path);
    private static native long nativeCreateWithFile(String path);
    private static native long nativeCreateFromStream(InputStream stream);
	private static native void nativeDispose(long nativeTypeface);

    private static native byte[] nativeGetTableData(long nativeTypeface, int tableTag);

	private static native int nativeGetUnitsPerEm(long nativeTypeface);
	private static native int nativeGetAscent(long nativeTypeface);
	private static native int nativeGetDescent(long nativeTypeface);
    private static native int nativeGetLeading(long nativeTypeface);

	private static native int nativeGetGlyphCount(long nativeTypeface);
    private static native int nativeGetGlyphId(long nativeTypeface, int codePoint);
    private static native float nativeGetGlyphAdvance(long nativeTypeface, int glyphId, float typeSize, boolean vertical);
    private static native Path nativeGetGlyphPath(long nativeTypeface, int glyphId, float typeSize, float[] matrix);

	private static native void nativeGetBoundingBox(long nativeTypeface, Rect boundingBox);

	private static native int nativeGetUnderlinePosition(long nativeTypeface);
	private static native int nativeGetUnderlineThickness(long nativeTypeface);
}
