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
package com.torchmind.lithium.node;

import com.torchmind.lithium.communication.Buffer;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.UUID;

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
         * @param allocator an allocator to utilize for encryption.
         * @param buffer    a buffer.
         * @return an encrypted representation of the buffer.
         *
         * @throws IllegalBlockSizeException when no padding is available for the current encryption method and the data length is not a multiple of the cipher block size.
         */
        @Nonnull
        Buffer encrypt(@Nonnull ByteBufAllocator allocator, @Nonnull Buffer buffer) throws IllegalBlockSizeException, ShortBufferException;

        /**
         * Retrieves the overall distance between two nodes.
         *
         * @param node a node.
         * @return a representation of the overall distance.
         */
        @Nonnegative
        int getDistance(@Nonnull Node node);

        /**
         * Retrieves the overall distance between two nodes based on their identifier.
         *
         * @param identifier an identifier.
         * @return a representation of the overall distance.
         */
        @Nonnegative
        int getDistance(@Nonnull UUID identifier);

        /**
         * Retrieves a (hopefully) globally unique identifier which is derived from a node's public key.
         *
         * @return an identifier.
         */
        @Nonnull
        UUID getIdentifier();

        /**
         * Retrieves the public key which this node currently utilizes for the sake of communication as declared in its
         * announcement packet.
         *
         * @return a public key.
         */
        @Nonnull
        PublicKey getPublicKey();

        /**
         * Converts any UUID into its integer representation.
         *
         * @param identifier an identifier.
         * @return an integer.
         */
        @Nonnull
        static BigInteger toInteger(@Nonnull UUID identifier) {
                BigInteger integer = new BigInteger(ByteBuffer.allocate(8).putLong(identifier.getMostSignificantBits()).array());
                integer = integer.shiftLeft(64);
                return integer.xor(new BigInteger(ByteBuffer.allocate(8).putLong(identifier.getLeastSignificantBits()).array()));
        }

        /**
         * Verifies a signature against a node's public key.
         *
         * @param data      the data to verify.
         * @param signature the signature to verify.
         * @return true if valid, false otherwise.
         */
        boolean verify(@Nonnull Buffer data, @Nonnull Buffer signature) throws SignatureException;
}
