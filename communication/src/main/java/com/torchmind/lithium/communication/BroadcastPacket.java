package com.torchmind.lithium.communication;

import javax.annotation.Nonnegative;

/**
 * <strong>Broadcast Packet</strong>
 *
 * Represents a packet which has a specific Time-To-Live attached to itself to limit the scope of network flooding.
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
}
