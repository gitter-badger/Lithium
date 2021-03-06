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

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.UUID;

/**
 * <strong>Packet</strong>
 *
 * Provides a base type for packet representations which are to be handled or to be sent by a node within the network.
 * Instances of this type generally represent the most primitive form of an instruction and only apply simple
 * validations of the data in question during de-serialization. They shall never include any handling related
 * information as they are unaware of the protocol direction in itself.
 *
 * <strong>Implementation Notes:</strong>
 * While this interface only dictates the existence of an {@link #write(Buffer)} method, all implementations are
 * required to provide at least one publicly visible constructor which accepts an instance of {@link Buffer} as well as
 * {@link UUID} which is capable of decoding the binary information within a serialized packet.
 *
 * Additionally instances of registered packet types which implement this interface will be instantiated within the
 * network backend and thus may not call any methods which aren't sufficiently thread safe from within their constructor
 * or {@link #write(Buffer)} method.
 *
 * For example:
 * <pre>
 *         public MyPacket(@Nonnull UUID identifier, @Nonnull Buffer buffer) {
 *              ...
 *         }
 * </pre>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Packet {

        /**
         * Retrieves a globally unique identifier for this packet which helps nodes to decide whether they have already
         * seen and processed a packet or whether it is new to them.
         *
         * This value should be generated upon construction and be final and immutable as of that point.
         *
         * @return an identifier.
         */
        @Nonnull
        UUID getIdentifier();

        /**
         * Writes the data contained within this representation of a packet into a machine-readable, network suitable
         * format which is in turn transmitted using a TCP connection by the underlying network stack.
         *
         * <strong>Implementation Notes:</strong>
         * While {@link Buffer} exposes access to the underlying memory structures, all implementations are advised to
         * stick to the root level methods as they provide improved compression and are part of the API contract.
         *
         * The underlying network stack may change at any time and implementations shall never directly interact with
         * its methods.
         *
         * @param buffer a packet buffer which the serialized state of this packet representation shall be written to.
         * @throws IOException when serializing one or multiple parts of the stored state fails.
         */
        void write(@Nonnull Buffer buffer) throws IOException;
}
