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
 * {@link Buffer} as well as a {@link short} value which represents the TTL.
 *
 * For example:
 * <pre>
 *         public MyPacket(@Nonnegative short ttl, @Nonnull Buffer buffer) {
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
