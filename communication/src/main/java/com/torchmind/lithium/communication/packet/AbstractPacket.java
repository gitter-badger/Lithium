package com.torchmind.lithium.communication.packet;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * <strong>Abstract Packet</strong>
 *
 * Provides a base for packet implementation which introduces getters and setters required by all packet implementations
 * regardless of their actual purpose.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public abstract class AbstractPacket implements Packet {
        private final UUID identifier;

        public AbstractPacket() {
                this.identifier = UUID.randomUUID();
        }

        public AbstractPacket(@Nonnull UUID identifier) {
                this.identifier = identifier;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public UUID getIdentifier() {
                return this.identifier;
        }
}
