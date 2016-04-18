package com.torchmind.lithium.communication.storage;

import com.torchmind.lithium.communication.Buffer;

import javax.annotation.Nonnull;

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
         */
        void write(@Nonnull Buffer buffer);
}
