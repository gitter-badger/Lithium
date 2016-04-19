package com.torchmind.lithium.communication.packet;

import com.torchmind.lithium.communication.Buffer;
import com.torchmind.lithium.communication.storage.Version;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * <strong>Handshake Packet</strong>
 *
 * Provides a packet to introduce nodes to each other within the network and ensure they are compatible with each other.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class HandshakePacket implements Packet {
        private final String fingerprint;
        private final Version version;

        public HandshakePacket(@Nonnull Version version, @Nonnull String fingerprint) {
                this.version = version;
                this.fingerprint = fingerprint;
        }

        public HandshakePacket(@Nonnull Buffer buffer) {
                this.version = buffer.readStorageValue(Version.class);
                this.fingerprint = buffer.readString();
        }

        @Nonnull
        public String getFingerprint() {
                return fingerprint;
        }

        @Nonnull
        public Version getVersion() {
                return version;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void write(@Nonnull Buffer buffer) throws IOException {
                buffer.writeStorageValue(this.version)
                        .writeString(this.fingerprint);
        }
}
