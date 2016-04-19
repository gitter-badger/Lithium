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

import javax.annotation.Nonnegative;

/**
 * <strong>Broadcast Packet</strong>
 *
 * Represents a packet which has a specific Time-To-Live attached to itself to limit the scope of network flooding.
 *
 * <strong>Implementation Notes:</strong>
 * While this interface does not declare any special constructors due to the limitations of the Java programming
 * language, implementations are required to provide a de-serialization constructor which accepts an instance of
 * {@link Buffer}, a {@link Short} value which represents the TTL and a {@link java.util.UUID} value which represents
 * the packet identifier.
 *
 * Additionally instances of registered packet types which implement this interface will be instantiated within the
 * network backend and thus may not call any methods which aren't sufficiently thread safe from within their constructor
 * or {@link #write(Buffer)} method.
 *
 * For example:
 * <pre>
 *         public MyPacket(@Nonnull UUID identifier, @Nonnegative short ttl, @Nonnull Buffer buffer) {
 *              ...
 *         }
 * </pre>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface BroadcastPacket extends Packet {

        /**
         * Retrieves the maximum amount of hops this packet will live.
         * This value is decreased on all forwarding nodes and a value of zero designated that the receiving node is
         * prohibited to forward the message any further.
         *
         * Note: Nodes may impose a limit to the time to live they accept within sane bounds.
         *
         * @return a numeric value representing the time to live.
         */
        @Nonnegative
        short getTimeToLive();

        /**
         * Sets the maximum amount of hops this packet will live.
         *
         * Note: Nodes may impose a limit to the time to live they accept within sane bounds.
         *
         * @param ttl a numeric value representing the time to live.
         */
        void setTimeToLive(@Nonnegative short ttl);
}
