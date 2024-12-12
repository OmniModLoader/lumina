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

import java.util.function.Consumer;

/**
 * Provides a base class for handling failure states using a {@link Consumer} of {@link FailedState}.
 *
 * <p>The `AcceptConsumer` allows for flexible error handling by defining a consumer
 * callback that can handle `FailedState` instances. This enables a mechanism for propagating or processing
 * failures in a central, configurable way.</p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Defines a **protected** mechanism to handle failures for subclasses.</li>
 *   <li>Supports late binding of the `FailedState` consumer, allowing it to be set at runtime.</li>
 *   <li>Acts as an extensible base for other classes that require failure handling.</li>
 * </ul>
 *
 * @author <b><a href=https://github.com/CadenCCC>Caden</a></b>
 * @since 1.0.0
 */
public class AcceptConsumer {

    /**
     * The consumer used to handle failure states.
     */
    private Consumer<FailedState> failedStateConsumer;

    /**
     * Retrieves the currently configured consumer for handling {@link FailedState}.
     *
     * @return The configured consumer, or {@code null} if none is set.
     */
    protected Consumer<FailedState> getConsumer() {
        return failedStateConsumer;
    }


    /**
     * Sets the consumer that will handle {@link FailedState} objects.
     *
     * @param failedStateConsumer The consumer to handle `FailedState` instances.
     */
    public void setConsumer(Consumer<FailedState> failedStateConsumer) {
        this.failedStateConsumer = failedStateConsumer;
    }

    /**
     * Dispatches a {@link FailedState} object to the configured consumer, if one is set.
     *
     * <p>If no consumer is set, calling this method has no effect.</p>
     *
     * @param failedState The failure state to dispatch.
     */
    protected void fail(FailedState failedState) {
        Consumer<FailedState> consumer = getConsumer();
        if (consumer != null) {
            consumer.accept(failedState);
        }
    }
}