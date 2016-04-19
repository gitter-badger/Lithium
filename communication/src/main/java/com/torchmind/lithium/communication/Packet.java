package com.torchmind.lithium.communication;

import javax.annotation.Nonnull;
import java.io.IOException;

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
 * required to provide at least one publicly visible constructor which accepts an instance of {@link Buffer} which is
 * capable of decoding the binary information within a serialized packet.
 *
 * Additionally instances of registered packet types which implement this interface will be instantiated within the
 * network backend and thus may not call any methods which aren't sufficiently thread safe from within their constructor
 * or {@link #write(Buffer)} method.
 *
 * For example:
 * <pre>
 *         public MyPacket(@Nonnull Buffer buffer) {
 *              ...
 *         }
 * </pre>
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Packet {

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
