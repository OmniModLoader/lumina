/*
 * MIT License
 *
 * Copyright (c) 2024 OmniMC
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

package temp.hierarchy.info;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * {@code FieldInfo} stores details about a field in a class, including its obfuscated name, human-readable name, and
 * descriptor.
 *
 * <p>This class helps manage field information by providing both obfuscated and readable names, as well as
 * the field's descriptor. It’s useful when working with fields in obfuscated code.</p>
 *
 * @author <b><a href="https://github.com/CadenCCC">Caden</a></b>
 * @since 1.0.0
 */
@SuppressWarnings("ClassCanBeRecord")
public class FieldInfo {

    private final String obfuscatedName;
    private final String fieldName;
    private final String descriptor;

    /**
     * <h6>Creates a new {@code FieldInfo} instance with the given details.
     *
     * @param obfuscatedName The obfuscated name of the field.
     * @param fieldName      The human-readable name of the field.
     * @param descriptor     The field descriptor.
     */
    public FieldInfo(@NotNull String obfuscatedName, @NotNull String fieldName, @NotNull String descriptor) {
        this.obfuscatedName = obfuscatedName;
        this.fieldName = fieldName;
        this.descriptor = descriptor;
    }

    /**
     * <h6>Gets the obfuscated name of the field.
     *
     * @return The obfuscated name.
     */
    @NotNull
    public String getObfuscatedName() {
        return obfuscatedName;
    }

    /**
     * <h6>Gets the human-readable name of the field.
     *
     * @return The readable name.
     */
    @NotNull
    public String getFieldName() {
        return fieldName;
    }

    /**
     * <h6>Gets the descriptor of the field.
     *
     * @return The field descriptor.
     */
    @NotNull
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldInfo fieldInfo = (FieldInfo) o;
        return Objects.equals(getObfuscatedName(), fieldInfo.getObfuscatedName())
               && Objects.equals(getFieldName(), fieldInfo.getFieldName())
               && Objects.equals(getDescriptor(), fieldInfo.getDescriptor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObfuscatedName(), getFieldName(), getDescriptor());
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
                "obfuscatedName='" + obfuscatedName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", descriptor='" + descriptor + '\'' +
                '}';
    }
}