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

import javax.annotation.Nonnull;
import javax.crypto.ShortBufferException;
import java.security.PrivateKey;
import java.security.SignatureException;

/**
 * <strong>Local Node</strong>
 *
 * Represents the local node along with all of its metadata and capabilities.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface LocalNode extends Node {

        /**
         * Decrypts a buffer using the local private key.
         *
         * @param allocator an allocator for creating new buffers.
         * @param buffer    a buffer.
         * @return a decrypted set of data.
         */
        @Nonnull
        Buffer decrypt(@Nonnull ByteBufAllocator allocator, @Nonnull Buffer buffer) throws ShortBufferException;

        /**
         * Retrieves the private key utilized for decryption and creation of signatures by this node.
         *
         * @return a private key.
         */
        @Nonnull
        PrivateKey getPrivateKey();

        /**
         * Retrieves a reference to the local routing table which contains a set of discovered nodes.
         *
         * @return a routing table.
         */
        @Nonnull
        RoutingTable getRoutingTable();

        /**
         * Creates a signature for the data contained within the supplied buffer and returns it as a new buffer
         * instance.
         *
         * @param buffer a buffer.
         * @return a signature buffer.
         */
        @Nonnull
        Buffer sign(@Nonnull Buffer buffer) throws SignatureException;
}
