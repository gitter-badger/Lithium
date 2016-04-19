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

import com.torchmind.lithium.communication.Buffer;
import com.torchmind.lithium.communication.storage.Version;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.security.PublicKey;
import java.util.UUID;

/**
 * <strong>Handshake Packet</strong>
 *
 * Provides a packet to introduce nodes to each other within the network and ensure they are compatible with each other.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class AnnouncementPacket extends AbstractPacket {
        private final Version version;
        private final PublicKey publicKey;

        public AnnouncementPacket(@Nonnull Version version, @Nonnull PublicKey publicKey) {
                super();

                this.version = version;
                this.publicKey = publicKey;
        }

        public AnnouncementPacket(@Nonnull UUID identifier, @Nonnull Buffer buffer) throws IOException {
                super(identifier);

                this.version = buffer.readStorageValue(Version.class);
                this.publicKey = buffer.readPublicKey();
        }

        @Nonnull
        public Version getVersion() {
                return version;
        }

        @Nonnull
        public PublicKey getPublicKey() {
                return publicKey;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void write(@Nonnull Buffer buffer) throws IOException {
                buffer.writeStorageValue(this.version)
                        .writePublicKey(this.publicKey);
        }
}
