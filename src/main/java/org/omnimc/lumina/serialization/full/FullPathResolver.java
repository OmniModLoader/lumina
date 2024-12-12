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

package org.omnimc.lumina.serialization.full;

/**
 * Defines a structure for resolving the paths to class, method, and field mapping files within a directory.
 *
 * <p>This record specifies the relative locations of mapping files inside a directory
 * to support the {@link FullSerializer} in reading structured mappings.</p>
 *
 * <p>For example:</p>
 * <ul>
 *   <li>{@code classLocation} defines the file name or path for class mappings (e.g., "class.mmap").</li>
 *   <li>{@code methodLocation} defines the file name or path for method mappings (e.g., "method.mmap").</li>
 *   <li>{@code fieldLocation} defines the file name or path for field mappings (e.g., "field.mmap").</li>
 * </ul>
 *
 * @param classLocation  The relative path or file name for class mappings.
 * @param methodLocation The relative path or file name for method mappings.
 * @param fieldLocation  The relative path or file name for field mappings.
 *
 * @see FullSerializer
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public record FullPathResolver(String classLocation, String methodLocation, String fieldLocation) {

    public static FullPathResolver of(String classLocation, String methodLocation, String fieldLocation) {
        return new FullPathResolver(classLocation, methodLocation, fieldLocation);
    }

    @Override
    public String toString() {
        return "FullPathResolver{" +
                "classLocation='" + classLocation + '\'' +
                ", methodLocation='" + methodLocation + '\'' +
                ", fieldLocation='" + fieldLocation + '\'' +
                '}';
    }
}