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
