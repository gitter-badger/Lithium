package com.torchmind.lithium.node;

import com.torchmind.lithium.communication.Buffer;

import javax.annotation.Nonnull;
import javax.crypto.IllegalBlockSizeException;
import java.security.PublicKey;

/**
 * <strong>Node</strong>
 *
 * Represents a single node within the network along with its publicly known data.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Node {

        /**
         * Encrypts a buffer using the known public key to ensure private conversation with the node.
         *
         * Note: The encryption method provided by this method is not sufficient for encryption of bigger amounts of
         * data and should thus only be used for handshaking between two parties which intend to communicate in a secure
         * fashion.
         *
         * @param buffer a buffer.
         * @return an encrypted representation of the buffer.
         *
         * @throws IllegalBlockSizeException when no padding is available for the current encryption method and the data length is not a multiple of the cipher block size.
         */
        @Nonnull
        Buffer encrypt(@Nonnull Buffer buffer) throws IllegalBlockSizeException;

        /**
         * Retrieves a fingerprint which identifies the node as well as its public key.
         *
         * Note: A fingerprint may not be globally unique. This may be caused either due to collisions within the
         * backing hashing algorithm or due to malicious intend. In those cases we will verify whether a node is who
         * they claim to be simply by sending them an encrypted message which they will have to decrypt on their own
         * in order to read it.
         *
         * While this may disclose our intention of creating a connection with a certain user, it will not disclose
         * any detail about the content of our message or our identity.
         *
         * @return a fingerprint of the node's public key.
         */
        @Nonnull
        String getFingerprint();

        /**
         * Retrieves the public key which this node currently utilizes for the sake of communication as declared in its
         * announcement packet.
         *
         * @return a public key.
         */
        @Nonnull
        PublicKey getPublicKey();
}
