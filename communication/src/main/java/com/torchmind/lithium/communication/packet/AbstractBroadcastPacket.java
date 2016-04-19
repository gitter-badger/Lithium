package com.torchmind.lithium.communication.packet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * <strong>Abstract Broadcast Packet</strong>
 *
 * Provides an abstract base implementation for broadcast packet implementation types along with the required getters
 * and setters.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public abstract class AbstractBroadcastPacket extends AbstractPacket implements BroadcastPacket {
        private short ttl;

        public AbstractBroadcastPacket() {
                this((short) 8);
        }

        public AbstractBroadcastPacket(@Nonnegative short ttl) {
                super();
                this.ttl = ttl;
        }

        public AbstractBroadcastPacket(@Nonnull UUID identifier, @Nonnegative short ttl) {
                super(identifier);
                this.ttl = ttl;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnegative
        public short getTimeToLive() {
                return this.ttl;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTimeToLive(@Nonnegative short ttl) {
                this.ttl = ttl;
        }
}
