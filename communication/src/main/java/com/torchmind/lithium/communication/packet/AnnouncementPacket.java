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
