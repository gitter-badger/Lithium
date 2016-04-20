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
import java.time.Instant;
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
         * Announces a new remote node to the table and stores it if there is still sufficient storage within its
         * corresponding list.
         *
         * @param node a node.
         */
        void announce(@Nonnull RemoteNode node);

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
         * @return a request representation.
         * @see #getNode(UUID) to retrieve a direct reference to the remote node after finalizing the lookup.
         */
        LookupRequest lookupClosestKnownNodes(@Nonnull UUID identifier);

        /**
         * <strong>Lookup Request</strong>
         *
         * Represents a lookup request along with its timeout and nodes that the request was initially sent to.
         */
        interface LookupRequest {

                /**
                 * Receives a list of nodes which have been discovered in the process.
                 *
                 * @return a list of nodes.
                 */
                @Nonnull
                Set<RemoteNode> getDiscoveredNodes();

                /**
                 * Retrieves the timestamp at which this request is considered expired and the remaining queries are
                 * considered timed out.
                 *
                 * When this timestamp is hit, the data which has been received so far is passed along to the original
                 * caller and the request is considered fulfilled.
                 *
                 * @return a timestamp.
                 */
                @Nonnull
                Instant getExpirationTimestamp();

                /**
                 * Retrieves a set of fulfilled nodes which have already responded to the request.
                 *
                 * @return a set of nodes.
                 */
                @Nonnull
                Set<RemoteNode> getFulfilledNodes();

                /**
                 * Retrieves a lookup identifier which will be equal on all nodes.
                 *
                 * @return an identifier.
                 */
                @Nonnull
                UUID getIdentifier();

                /**
                 * Retrieves a set of outstanding nodes which have yet to respond to the request.
                 *
                 * @return a set of outstanding nodes.
                 */
                @Nonnull
                Set<RemoteNode> getOutstandingNodes();

                /**
                 * Retrieves a set of nodes which were queried for the sake of this query.
                 *
                 * @return a set of queried nodes.
                 */
                @Nonnull
                Set<RemoteNode> getQueriedNodes();

                /**
                 * Retrieves the original query timestamp.
                 *
                 * @return a timestamp.
                 */
                @Nonnull
                Instant getQueryTimestamp();

                /**
                 * Retrieves the target node identifier which we are currently attempting to find within the network.
                 *
                 * @return a target identifier.
                 */
                @Nonnull
                UUID getTargetIdentifier();

                /**
                 * Retrieves the target node, if it has already been found within this request or an empty optional if
                 * the target node was not found yet.
                 *
                 * @return a target node.
                 */
                @Nonnull
                Optional<RemoteNode> getTargetNode();

                /**
                 * Checks whether the request has expired before all nodes were able to fulfil it.
                 *
                 * @return true if expired, false otherwise.
                 */
                boolean hasExpired();

                /**
                 * Checks whether the request has been fulfilled.
                 *
                 * @return true if fulfilled, false otherwise.
                 */
                boolean isFulfilled();

                /**
                 * Registers a callback which is executed when the target node was not found after a request timed out
                 * or if an exact response for a node was returned.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 *
                 * @param consumer a consumer.
                 * @return a reference to this request.
                 */
                @Nonnull
                LookupRequest onFailure(@Nonnull Consumer<LookupRequest> consumer);

                /**
                 * Registers a callback which is executed when the target node has been found.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 *
                 * @param consumer a consumer.
                 * @return a reference to this request.
                 */
                @Nonnull
                LookupRequest onSuccess(@Nonnull Consumer<RemoteNode> consumer);
        }
}
