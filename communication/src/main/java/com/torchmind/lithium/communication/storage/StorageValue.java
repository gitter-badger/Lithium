/*
 * Copyright 2016 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.torchmind.lithium.communication.storage;

import com.torchmind.lithium.communication.Buffer;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * <strong>Storage Value</strong>
 *
 * Represents a special, serializable value which may be used as a substitute for multiple values within packet types to
 * remove redundancies and simplify data structures.
 *
 * <strong>Implementation Notes:</strong>
 * While this interface does not declare a specific constructor due to restrictions in the Java language, all
 * implementations are required to bear at least a single publicly visible constructor which accepts an instance of
 * {@link Buffer} as its sole parameter.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface StorageValue {

        /**
         * Writes the data represented by this value into a buffer using an implementation specific binary encoding.
         *
         * @param buffer a buffer to write all data into.
         * @throws IOException when serializing the storage value fails.
         */
        void write(@Nonnull Buffer buffer) throws IOException;
}
