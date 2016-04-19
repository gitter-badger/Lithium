package com.torchmind.lithium.node;

import com.torchmind.lithium.communication.Buffer;
import com.torchmind.lithium.communication.packet.BroadcastPacket;

import javax.annotation.Nonnull;
import java.security.PrivateKey;

/**
 * <strong>Local Node</strong>
 *
 * Represents the local node along with all of its metadata and capabilities.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface LocalNode extends Node {

        /**
         * Broadcasts a packet to all adjacent nodes within the network.
         *
         * @param packet a broadcast packet.
         */
        void broadcast(@Nonnull BroadcastPacket packet);

        /**
         * Creates a signature for the data contained within the supplied buffer and returns it as a new buffer
         * instance.
         *
         * @param buffer a buffer.
         * @return a signature buffer.
         */
        @Nonnull
        Buffer createSignature(@Nonnull Buffer buffer);

        /**
         * Decrypts a buffer using the local private key.
         *
         * @param buffer a buffer.
         * @return a decrypted set of data.
         */
        @Nonnull
        Buffer decrypt(@Nonnull Buffer buffer);

        /**
         * Retrieves the private key utilized for decryption and creation of signatures by this node.
         *
         * @return a private key.
         */
        @Nonnull
        PrivateKey getPrivateKey();
}
