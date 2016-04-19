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

import com.torchmind.lithium.communication.packet.Packet;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;

/**
 * <strong>Remote Note</strong>
 *
 * Represents a remote node within the network which is in direct reach of the local node (e.g. a peer which is either
 * part of the initial set of known remotes discovered by contacting a root server or a node which is currently
 * connected as a communication partner).
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface RemoteNode extends Node {

        /**
         * Retrieves the physical network address which this node is currently known to respond on.
         *
         * @return an address which this node is reachable at.
         */
        @Nonnull
        InetSocketAddress getAddress();

        /**
         * Sends a single packet to this node using the current method of communication.
         *
         * Note: Unless a direct connection has been established previously, a packet will be dispatched as a broadcast
         * to the known routing path. Make sure to encrypt sensitive data with a known public key when no direct
         * connection is present at the moment.
         *
         * @param packet a packet.
         */
        void send(@Nonnull Packet packet);
}
