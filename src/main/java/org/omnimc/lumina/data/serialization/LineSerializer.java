/*
 * MIT License
 *
 * Copyright (c) 2024-2025 OmniMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.omnimc.lumina.data.serialization;

import org.omnimc.lumina.data.Mappings;
import org.omnimc.lumina.data.types.ClassData;

/**
 * An interface that parses lines and populates {@link Mappings} or {@link ClassData}.
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public interface LineSerializer {

    /**
     * This is a {@link LineSerializer} that is {@code null}.
     *
     * @return a {@code null} {@link LineSerializer}
     */
    static LineSerializer getEmptyLineSerializer() {
        return null;
    }

    /**
     * This method serializes all different types of lines, this can include fields methods and classes.
     *
     * @param line     The line that contains the data you wish to parse.
     * @param mappings The {@link Mappings} you wish to populate with the data provided.
     * @return {@code true} meaning it passed, while {@code false} means you failed.
     */
    boolean serialize(String line, Mappings mappings);

    /**
     * This method serializes only fields.
     *
     * @param line        The line that contains the field you wish to parse.
     * @param classData   The {@link ClassData} you wish to populate with the data provided.
     * @return {@code true} meaning it passed, while {@code false} means you failed.
     */
    boolean serializeFields(String line, ClassData classData);

    /**
     * This method serializes only methods.
     *
     * @param line        The line that contains the method you wish to parse.
     * @param classData   The {@link ClassData} you wish to populate with the data provided.
     * @return {@code true} meaning it passed, while {@code false} means you failed.
     */
    boolean serializeMethods(String line, ClassData classData);

}