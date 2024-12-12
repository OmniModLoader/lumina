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

package org.omnimc.lumina.consumer;

/**
 * Represents a failure state, containing information about the failure reason, the exception (if any), and the caller's class.
 *
 * <p>The `FailedState` record stores information that can be used for debugging or reporting errors in the deserialization process
 * or other operations. It supports various ways to construct a failure state with or without a reason or throwable.</p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Immutable by design due to being implemented as a record.</li>
 *   <li>Provides static factory methods to simplify construction.</li>
 *   <li>Includes a detailed `toString()` implementation for easy logging and debugging.</li>
 * </ul>
 *
 * @param reason   The reason for the failure, represented as a string (nullable).
 * @param throwable The associated exception, if applicable (nullable).
 * @param caller   The class in which the failure occurred, represented as a {@link Class} object.
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public record FailedState(String reason, Throwable throwable, Class<?> caller) {

    /**
     * Creates a new {@link FailedState} with a reason, an associated throwable, and the caller's class.
     *
     * @param reason    The reason for the failure.
     * @param throwable The associated exception.
     * @param caller    The class in which the failure occurred.
     * @return A new instance of {@link FailedState}.
     */
    public static FailedState of(String reason, Throwable throwable, Class<?> caller) {
        return new FailedState(reason, throwable, caller);
    }

    /**
     * Creates a new {@link FailedState} with only a throwable and the caller's class.
     *
     * @param throwable The associated exception.
     * @param caller    The class in which the failure occurred.
     * @return A new instance of {@link FailedState}.
     */
    public static FailedState of(Throwable throwable, Class<?> caller) {
        return new FailedState(null, throwable, caller);
    }

    /**
     * Creates a new {@link FailedState} with only a failure reason and the caller's class.
     *
     * @param reason The reason for the failure.
     * @param caller The class in which the failure occurred.
     * @return A new instance of {@link FailedState}.
     */
    public static FailedState of(String reason, Class<?> caller) {
        return new FailedState(reason, null, caller);
    }

    /**
     * Converts the failure state to a string representation for logging or debugging.
     *
     * <p>The `toString()` outputs details of the reason, throwable, and caller. If no throwable is present,
     * it omits the throwable-related information.</p>
     *
     * @return A string representation of the failure state.
     */
    @Override
    public String toString() {
        if (throwable == null) {
            return "FailedState{" +
                    "reason='" + reason + '\'' +
                    ", caller=" + caller +
                    '}';
        }

        return "FailedState{" +
                "reason='" + reason + '\'' +
                ", throwable=" + throwable +
                ", caller=" + caller +
                '}';
    }
}