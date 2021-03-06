/*
 * Copyright (C) 2016-2018 Muhammad Tayyab Akram
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.mta.tehreer.internal.util.Preconditions.checkArgument;
import static com.mta.tehreer.internal.util.Preconditions.checkNotNull;

/**
 * The <code>TypefaceManager</code> class provides management activities related to typefaces.
 */
public class TypefaceManager {
    private static class TypefaceComparator implements Comparator<Typeface> {
        @Override
        public int compare(Typeface obj1, Typeface obj2) {
            int result = obj1.getFamilyName().compareToIgnoreCase(obj2.getFamilyName());
            if (result == 0) {
                return obj1.getStyleName().compareToIgnoreCase(obj2.getStyleName());
            }

            return result;
        }
    }

    private static final @NonNull HashMap<Object, Typeface> tags = new HashMap<>();
    private static final @NonNull ArrayList<Typeface> typefaces = new ArrayList<>();
    private static boolean sorted;

    private TypefaceManager() { }

    /**
     * Registers a typeface in <code>TypefaceManager</code>.
     *
     * @param typeface The typeface that will be registered.
     * @param tag An optional tag to identify the typeface.
     *
     * @throws IllegalArgumentException if <code>typeface</code> is already registered, or
     *         <code>tag</code> is already taken.
     */
    public static void registerTypeface(@NonNull Typeface typeface, @Nullable Object tag) {
        checkNotNull(typeface, "typeface");

        synchronized (TypefaceManager.class) {
            checkArgument(!typefaces.contains(typeface), "This typeface is already registered");

            if (tag != null) {
                checkArgument(!tags.containsKey(tag), "This tag is already taken");

                tags.put(tag, typeface);
                typeface.tag = tag;
            }

            sorted = false;
            typefaces.add(typeface);
        }
    }

    /**
     * Unregisters a typeface in <code>TypefaceManager</code>.
     *
     * @param typeface The typeface to unregister.
     *
     * @throws IllegalArgumentException if <code>typeface</code> is not registered.
     */
    public static void unregisterTypeface(@NonNull Typeface typeface) {
        checkNotNull(typeface, "typeface");

        synchronized (TypefaceManager.class) {
            int index = typefaces.indexOf(typeface);
            checkArgument(index >= 0, "This typeface is not registered");

            typefaces.remove(index);
            tags.remove(typeface.tag);
            typeface.tag = null;
        }
    }

    /**
     * Returns the typeface registered against the specified tag.
     *
     * @param tag The tag object that identifies the typeface.
     * @return The registered typeface, or <code>null</code> if no typeface is registered against
     *         the specified tag.
     */
    public static @Nullable Typeface getTypeface(@NonNull Object tag) {
        checkNotNull(tag, "tag");

        synchronized (TypefaceManager.class) {
            return tags.get(tag);
        }
    }

    /**
     * Returns the tag of a registered typeface.
     *
     * @param typeface The typeface whose tag is returned.
     * @return The tag of the typeface, or <code>null</code> if no tag was specified while
     *         registration.
     *
     * @throws IllegalArgumentException if <code>typeface</code> is not registered.
     */
    public static @Nullable Object getTypefaceTag(@NonNull Typeface typeface) {
        checkNotNull(typeface, "typeface");

        synchronized (TypefaceManager.class) {
            checkArgument(typefaces.contains(typeface), "This typeface is not registered");

            return typeface.tag;
        }
    }

    /**
     * Looks for a type family having specified family name.
     *
     * @param familyName The name of the family.
     * @return A type family having specified family name.
     */
    public static @Nullable TypeFamily getTypeFamily(@NonNull String familyName) {
        List<Typeface> entryList = new ArrayList<>();

        synchronized (TypefaceManager.class) {
            sortTypefaces();

            for (Typeface typeface : typefaces) {
                if (typeface.getFamilyName().equalsIgnoreCase(familyName)) {
                    entryList.add(typeface);
                }
            }
        }

        TypeFamily typeFamily = null;

        if (entryList.size() > 0) {
            typeFamily = new TypeFamily(familyName, entryList);
        }

        return typeFamily;
    }

    /**
     * Looks for a registered typeface having specified full name.
     *
     * @param fullName The full name of the typeface.
     * @return The typeface having specified full name, or <code>null</code> if no such typeface is
     *         registered.
     */
    public static @Nullable Typeface getTypefaceByName(@NonNull String fullName) {
        synchronized (TypefaceManager.class) {
            for (Typeface typeface : typefaces) {
                if (typeface.getFullName().equalsIgnoreCase(fullName)) {
                    return typeface;
                }
            }
        }

        return null;
    }

    /**
     * Returns a list of available type families sorted by their names in ascending order.
     *
     * @return A list of available type families.
     */
    public static @NonNull List<TypeFamily> getAvailableFamilies() {
        Map<String, List<Typeface>> familyMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        synchronized (TypefaceManager.class) {
            sortTypefaces();

            for (Typeface typeface : typefaces) {
                List<Typeface> entryList = familyMap.get(typeface.getFamilyName());
                if (entryList == null) {
                    entryList = new ArrayList<>();
                    familyMap.put(typeface.getFamilyName(), entryList);
                }

                entryList.add(typeface);
            }
        }

        List<TypeFamily> familyList = new ArrayList<>(familyMap.size());

        for (Map.Entry<String, List<Typeface>> entry : familyMap.entrySet()) {
            String familyName = entry.getKey();
            List<Typeface> typefaces = entry.getValue();

            familyList.add(new TypeFamily(familyName, typefaces));
        }

        return Collections.unmodifiableList(familyList);
    }

    /**
     * Returns a list of available typefaces sorted by their family and style names in ascending
     * order.
     *
     * @return A list of available typefaces.
     */
    public static @NonNull List<Typeface> getAvailableTypefaces() {
        synchronized (TypefaceManager.class) {
            sortTypefaces();

            return Collections.unmodifiableList(new ArrayList<>(typefaces));
        }
    }

    private static void sortTypefaces() {
        if (!sorted) {
            Collections.sort(typefaces, new TypefaceComparator());
            sorted = true;
        }
    }
}
