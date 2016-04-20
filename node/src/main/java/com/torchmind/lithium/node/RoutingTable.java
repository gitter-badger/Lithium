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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * <strong>Routing Table</strong>
 *
 * Provides a registry of known nodes which were previously encountered while processing requests within the network
 * such as direct announcements or discoveries made by iterating nodes.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface RoutingTable {

        /**
         * Retrieves the nodes which have the closest distance within the network or, if possible, the searched node
         * itself.
         *
         * @param identifier an identifier.
         * @return a remote node.
         */
        @Nonnull
        Set<RemoteNode> getClosestNodes(@Nonnull UUID identifier);

        /**
         * Searches for a specific node within the local routing table. If no local version of the node was found, an
         * empty optional is returned.
         *
         * @param identifier an identifier.
         * @return a remote node.
         */
        @Nonnull
        Optional<RemoteNode> getNode(@Nonnull UUID identifier);

        /**
         * Retrieves a set of nodes which are a certain distance away from the local node.
         *
         * <strong>Implementation Nodes:</strong>
         * The distance between two nodes is calculated by applying a xor operation on every single bit within the two
         * node identifiers and counting the amount of equal bits within the result starting from the least significant
         * bit.
         *
         * @param distance a distance between two nodes.
         * @return a set of nodes known to be away by a certain amount.
         */
        @Nonnull
        Set<RemoteNode> getNodes(@Nonnegative int distance);

        /**
         * Attempts to look up a set of closest known nodes within the network. This step may be repeated until there
         * are no further nodes to be discovered which are closer to the node in question.
         *
         * To be able to resolve the closest node, you will have to call this method all over again until the integer
         * supplied to the specified consumer is set to zero as this will indicate that no further nodes are to be
         * queried.
         *
         * @param identifier an identifier.
         * @param consumer   a consumer.
         * @see #getNode(UUID) to retrieve a direct reference to the remote node after finalizing the lookup.
         */
        void lookupClosestKnownNodes(@Nonnull UUID identifier, @Nonnull Consumer<Integer> consumer);
}
