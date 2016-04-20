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
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
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
         * Creates a new lookup request builder.
         * @param identifier a node identifier to search for.
         * @return a request builder.
         */
        @Nonnull
        LookupRequestBuilder lookup(@Nonnull UUID identifier);

        /**
         * <strong>Lookup Request Builder</strong>
         *
         * Provides a factory which is capable of creating and committing lookup requests based on specifications
         * which have been put in place by invoking a simple natural API.
         */
        interface LookupRequestBuilder {

                /**
                 * Creates and submits the request based on the specifications within this builder instance.
                 * @return a reference to the newly created request.
                 */
                @Nonnull
                LookupRequest commit();

                /**
                 * Registers a new success callback which will be invoked when a perfect match to the request is
                 * returned by any of the queried nodes.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 * @param consumer a callback.
                 * @return a reference to this builder instance.
                 */
                @Nonnull
                LookupRequestBuilder onSuccess(@Nonnull Consumer<RemoteNode> consumer);

                /**
                 * Registers a new failure callback which will be invoked when no perfect match to the request is
                 * returned after all nodes return or after the expiration timeout has been reached.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 * @param runnable a callback.
                 * @return a reference to this builder instance.
                 */
                @Nonnull
                LookupRequestBuilder onFailure(@Nonnull Runnable runnable);

                /**
                 * Marks a request as recursive which will cause it to continue polling new data from the closest set of
                 * nodes known to the routing table unless either a result is found or no closer nodes are returned.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 * @return a reference to this builder instance.
                 */
                @Nonnull
                default LookupRequestBuilder recursive() {
                        return this.recursive(true);
                }

                /**
                 * Sets whether the request is recursive which will cause it to continue polling new data from the
                 * closest set of nodes known to the routing table unless either a result is found or no closer nodes
                 * are returned.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 * @param value if true, enables recursive polling, otherwise disables it.
                 * @return a reference to this builder instance.
                 */
                @Nonnull
                LookupRequestBuilder recursive(boolean value);

                /**
                 * Sets a specific expiration duration to apply to the requests created by this builder.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 * @param duration a duration.
                 * @return a reference to this builder instance.
                 */
                @Nonnull
                LookupRequestBuilder expiration(@Nonnull Duration duration);

                /**
                 * Sets a specific expiration time to apply to the requests created by this builder.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 * @param value a time value.
                 * @param unit a time unit.
                 * @return a reference to this builder instance.
                 */
                @Nonnull
                LookupRequestBuilder expiration(@Nonnegative long value, @Nonnull TemporalUnit unit);

                /**
                 * Sets a specific expiration time to apply to the requests created by this builder.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 * @param timestamp an exact expiration timestamp.
                 * @return a reference to this builder instance.
                 */
                @Nonnull
                LookupRequestBuilder expiration(Instant timestamp);
        }

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
        }
}
