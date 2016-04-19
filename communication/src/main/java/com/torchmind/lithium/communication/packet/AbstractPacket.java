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
