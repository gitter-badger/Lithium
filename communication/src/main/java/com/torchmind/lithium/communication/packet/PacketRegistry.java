package com.torchmind.lithium.communication.packet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * <strong>Packet Registry</strong>
 *
 * Provides a method of looking up information on packet identifiers as well as packet types for the purpose of en- and
 * de-coding plain old Java objects (POJOs) from or into binary data which in turn can be signed and encrypted for
 * secure communication between single nodes within the network.
 *
 * <strong>Implementation Notes:</strong>
 * As packet registries are most commonly called from within the network stack, it is recommended to ensure thread
 * safety of all methods contained herein. The path of immutability should generally be preferred as frequent changes
 * to the set of known packet types during runtime are unlikely and usually undesirable.
 *
 * For more information on extending the protocol from within third-party software or plugins, refer to the
 * <a href="https://github.com/LordAkkarin/Lithium/wiki">wiki</a>.
 *
 * In addition to the thread-safety guidelines outlined above, implementations of this interface are required to provide
 * consistent identifiers to their local packets such as packet names or somehow generated UUIDs. These identifiers are
 * however not required to be consistent between versions specifically as long as the correct and consistent assignment
 * of handshake related packets is guaranteed (Shuffling of handshake related packet identifiers will break proper
 * notifications within user interfaces and logs).
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@ThreadSafe
public interface PacketRegistry {

        /**
         * Creates a new default packet registry builder instance which is capable of creating class-name based
         * registries.
         *
         * @return a reference to the newly created builder.
         */
        @Nonnull
        static Builder builder() {
                return new ClassNamePacketRegistry.Builder();
        }

        /**
         * Creates a new builder instance which is aware of the configuration of this packet registry at the moment
         * of invocation.
         *
         * This method does however not allow to alter an existing packet registry. It allows to create similar
         * registries which may be used as a substitute after finalizing their configuration within the resulting
         * builder instance.
         *
         * @return a reference to a newly created builder which bears a representation of the current registry
         * configuration.
         */
        @Nonnull
        Builder builderFor();

        /**
         * Looks up a packet type based on its registry assigned identifier within this packet registry instance and
         * returns it back to the caller. If however no registration for the supplied identifier is found, an empty
         * optional will be returned to signify an empty response.
         *
         * <strong>Implementation Notes:</strong>
         * This method shall return known packet types regardless of their associated capabilities. Its sole purpose
         * is to act as a central authority for identifying packet types based on their identifiers.
         *
         * @param identifier a packet identifier as received by the network backend.
         * @return an optional consisting of the known packet type which is used as a representation of the instruction
         * identified by the supplied identifier.
         *
         * @throws IllegalArgumentException when the supplied identifier is not well formed based on the specifications
         *                                  outlined by this packet registry implementation.
         */
        @Nonnull
        Optional<Class<? extends Packet>> lookup(@Nonnull String identifier) throws IllegalArgumentException;

        /**
         * Looks up a packet identifier based on its local plain old Java object (POJO) representation type and returns
         * it back to the caller. If however no registration for the supplied type is found, an empty optional will be
         * returned to signify an empty response.
         *
         * <strong>Implementation Notes:</strong>
         * This method shall return known packet types regardless of their associated capabilities. Its sole purpose is
         * to act as a central authority for identifying packet types.
         *
         * @param type a packet type which will serve as a representation for a specific instruction.
         * @return an identifier which will serve as an identifier for the specified packet representation type.
         */
        @Nonnull
        Optional<String> lookup(@Nonnull Class<? extends Packet> type);

        /**
         * <strong>Packet Registry Builder</strong>
         *
         * Provides a method of creating customized versions of a packet registry by calling chain-able methods on a
         * comprehensive interface.
         *
         * This type is additionally used to circumvent the design implications introduced by immutability within
         * packet registries and should thus be the only method of creating or altering new or existing packet registry
         * instances during runtime.
         *
         * Note: Implementations of this type shall never be thread-safe as access from multiple threads is unlikely.
         * The builder is expected to be held by a single thread at a time only and thus general performance
         * implications take priority over thread safety.
         */
        interface Builder {

                /**
                 * Creates a new packet registry instance based on the packet representations which have previously been
                 * registered with the instance of this builder.
                 *
                 * <strong>Implementation Notes:</strong>
                 * For the sake of thread safety, all implementations should at least create a copy of the internal
                 * representation of packet representation registrations at this point.
                 *
                 * @return a reference to a newly created packet registry instance based on the specification outlined
                 * within the state of this builder instance.
                 */
                @Nonnull
                PacketRegistry build();

                /**
                 * Removes all packet representations which have previously been registered with this builder instance
                 * and ensures the resulting packet registry is empty.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 *
                 * <strong>Implementation Notes:</strong>
                 * While this method should only be called rarely by third party implementations which wish to declare
                 * the exact set of registries within a builder at a specific point in time, implementations should
                 * still consider performance and possibly create new collections instead of attempting to clear them
                 * in order to save some memory in short term.
                 *
                 * @return a reference to this builder instance.
                 */
                @Nonnull
                Builder clear();

                /**
                 * Retrieves the overall amount of packet registrations which are currently known to this builder
                 * instance at the time of invocation.
                 *
                 * <strong>Implementation Notes:</strong>
                 * This method is rarely called and thus implementations should prioritize other methods such as
                 * {@link #register(Class)} when considering timing/performance implications.
                 *
                 * @return an absolute number of known packet registrations within this builder at the time of
                 * invocation.
                 */
                @Nonnegative
                int getSize();

                /**
                 * Checks whether a specific packet representation type has already been registered with this builder
                 * instance and is thus to be considered when performing look ups against the resulting packet registry.
                 * If the check outlined above succeeds, the supplied runnable is called.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 *
                 * <strong>Implementation Notes:</strong>
                 * This method should be considered an alias to {@link #isKnown(Class)} which executes the supplied
                 * runnable when the check succeeds.
                 *
                 * @param type     a packet implementation which will serve as a representation for a specific instruction.
                 * @param runnable a runnable to call if the check succeeds.
                 * @return a reference to this builder instance.
                 */
                @Nonnull
                Builder ifKnown(@Nonnull Class<? extends Packet> type, @Nonnull Runnable runnable);

                /**
                 * Checks whether a specific packet representation type has already been registered with this builder
                 * instance and is thus to be considered when performing look ups against the resulting packet registry.
                 * If the check outlined above succeeds, the supplied consumer is called and a reference to this builder
                 * instance is passed along.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 *
                 * <strong>Implementation Notes:</strong>
                 * This method should be considered an alias to {@link #ifKnown(Class, Runnable)} which passes a
                 * reference to this builder instance to the supplied consumer when the check succeeds.
                 *
                 * @param type     a packet implementation which will serve as a representation for a specific instruction.
                 * @param consumer a consumer to pass an instance of this builder to if the check succeeds.
                 * @return a reference to this builder instance.
                 */
                @Nonnull
                Builder ifKnown(@Nonnull Class<? extends Packet> type, @Nonnull Consumer<Builder> consumer);

                /**
                 * Checks whether a specific packet representation type is not registered with this builder instance and
                 * is thus not considered when performing look ups against the resulting packet registry.
                 * If the check outlined above succeeds, the supplied runnable is called.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 *
                 * <strong>Implementation Notes:</strong>
                 * This method should be considered an alias to {@link #isKnown(Class)} which calls the supplied code
                 * when the check fails.
                 *
                 * @param type     a packet implementation which will serve as a representation for a specific instruction.
                 * @param runnable a runnable to call if the check succeeds.
                 * @return a refernce to this builder instance.
                 */
                @Nonnull
                Builder ifUnknown(@Nonnull Class<? extends Packet> type, @Nonnull Runnable runnable);

                /**
                 * Checks whether a specific packet representation type is not registered with this builder instance
                 * and is thus not considered when performing look ups against the resulting packet registry.
                 * If the check outlined above succeeds, the supplied consumer is called and a reference to this builder
                 * instance is passed along.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 *
                 * <strong>Implementation Notes:</strong>
                 * This method should be considered an alias to {@link #ifUnknown(Class, Runnable)} which passes a
                 * reference to this builder instance to the supplied consumer when the check succeeds.
                 *
                 * @param type     a packet implementation which will serve as a representation for a specific instruction.
                 * @param consumer a consumer to pass an instance of this builder to if the check succeeds.
                 * @return a reference to this builder instance.
                 */
                @Nonnull
                Builder ifUnknown(@Nonnull Class<? extends Packet> type, @Nonnull Consumer<Builder> consumer);

                /**
                 * Checks whether there are currently no packet representation which have previously been registered
                 * with this builder instance and thus the resulting packet registry would be empty.
                 *
                 * @return true if no packet types are currently known to this builder instance.
                 */
                boolean isEmpty();

                /**
                 * Checks whether a specific packet representation type has already been registered with this builder
                 * instance and is thus to be considered when performing look ups against the resulting packet registry.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 *
                 * <strong>Implementation Notes:</strong>
                 * In most cases the caller will be aware of the state the builder is currently in and will not attempt
                 * to make use of this method. In addition to this fact, such calls are most likely to appear during
                 * application initialization. The timing for this method is thus not important. Slow performance may
                 * be preferred when beneficial to methods such as {@link #register(Class)}.
                 *
                 * @param type a packet implementation which will serve as a representation for a specific instruction.
                 * @return a reference to this builder instance.
                 */
                boolean isKnown(@Nonnull Class<? extends Packet> type);

                /**
                 * Registers a new packet representation with this builder instance and appends it to the list of types
                 * to be considered when performing look ups against the resulting packet registry.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 *
                 * <strong>Implementation Notes:</strong>
                 * A general verification against the requirements outlined within the documentation of {@link Packet}
                 * are to be executed within this method to reduce the need of proper error handling within the network
                 * backend later on and thus improve the performance when handling huge amounts of data.
                 *
                 * @param type a packet implementation which will serve as a representation for a specific instruction.
                 * @return a reference to this builder instance.
                 *
                 * @throws IllegalArgumentException when the supplied type does not match the requirements outlined
                 *                                  within {@link Packet}'s documentation or when the supplied type has already been registered with
                 *                                  this builder instance.
                 */
                @Nonnull
                Builder register(@Nonnull Class<? extends Packet> type) throws IllegalArgumentException;

                /**
                 * Registers a new packet representation with this builder instance and appends it to the list of types
                 * to be considered when performing look ups against the resulting packet registry unless the specified
                 * type has already previously been registered with this builder instance.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 *
                 * <strong>Implementation Notes:</strong>
                 * A general verification against the requirements outlined within the documentation of {@link Packet}
                 * are to be executed within this method to reduce the need of proper error handling within the network
                 * backend later on and thus improve the performance when handling huge amounts of data.
                 *
                 * Implementations may as well implement this method as a slightly smarter alias to
                 * {@link #register(Class)} by verifying the presence of a packet type within the current registry
                 * before passing the value onto {@link #register(Class)}.
                 *
                 * @param type a packet implementation which will serve as a representation for a specific instruction.
                 * @return a reference to this builder instance.
                 *
                 * @throws IllegalArgumentException when the supplied type does not match the requirements outlined
                 *                                  within {@link Packet}'s documentation.
                 */
                @Nonnull
                Builder registerIfUnknown(@Nonnull Class<? extends Packet> type) throws IllegalArgumentException;

                /**
                 * Removes a known packet representation from this builder instance and ensures it is not part of the
                 * list of types to be considered when performing look ups against the resulting packet registry unless
                 * the supplied type is currently not registered with the builder instance.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 *
                 * @param type a packet implementation which will serve as a representation for a specific instruction.
                 * @return a reference to this builder instance.
                 *
                 * @throws IllegalArgumentException when the supplied type is not known to this builder instance at the
                 *                                  current point in time.
                 */
                @Nonnull
                Builder remove(@Nonnull Class<? extends Packet> type) throws IllegalArgumentException;

                /**
                 * Removes a known packet representation from this builder instance and ensures it is not part of the
                 * list of types to be considered when performing look ups against the resulting packet registry.
                 *
                 * This method returns a reference to its parent instance and is thus chain-able.
                 *
                 * <strong>Implementation Notes:</strong>
                 * Implementations may as well implement this method as a slightly smarter alias to
                 * {@link #remove(Class)} which verifies the existence of the supplied type using
                 * {@link #isKnown(Class)} before passing the supplied type on to {@link #remove(Class)}.
                 *
                 * @param type a packet implementation whichb will serve as a representation for a specific instruction.
                 * @return a reference to this builder instance.
                 */
                @Nonnull
                Builder removeIfKnown(@Nonnull Class<? extends Packet> type);
        }
}
