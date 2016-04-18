package com.torchmind.lithium.communication;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * <strong>Packet Buffer</strong>
 *
 * Provides a method of interacting with binary data through simplified getter and setter methods which utilize their
 * type awareness to improve the overall data compression and optimization within the network protocol.
 *
 * <strong>Implementation Notes:</strong>
 * All implementations of this type are currently wrappers for netty's {@link io.netty.buffer.ByteBuf} which in turn is
 * a wrapper for on-heap byte arrays or off-heap {@link java.nio.ByteBuffer} instances which represent the actual data
 * in-memory.
 *
 * While instances of this type may be passed along between different threads, it is not required to be thread safe as
 * its processing is handled by a single specific thread at a time. Objects of this type are additionally usually short
 * lived objects and will be garbage collected shortly after decoding or encoding took place within the network backend.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface Buffer {

        /**
         * Allocates a new native buffer without any specific bounds and without a specific allocation context.
         *
         * @return a reference to the newly created buffer.
         */
        @Nonnull
        static Buffer alloc() {
                return new ByteBufWrapperBuffer(Unpooled.directBuffer());
        }

        /**
         * Allocates a new native buffer with an initial capacity and without a specific allocation context.
         *
         * Note: This method is to be preferred over {@link #alloc()} as it may provide performance improvement due to
         * the reduced amount of memory allocations caused when writing data.
         *
         * @param initialCapacity an initial capacity (in bytes).
         * @return a reference to the newly created buffer.
         */
        @Nonnull
        static Buffer alloc(@Nonnegative int initialCapacity) {
                return new ByteBufWrapperBuffer(Unpooled.directBuffer(initialCapacity));
        }

        /**
         * Allocates a new native buffer with an initial capacity, a maximum capacity and without a specific allocation
         * context.
         *
         * Note: This method is to be preferred over {@link #alloc()} as it may provide performance improvement due to
         * the reduced amount of memory allocations caused when writing data.
         *
         * @param initialCapacity an initial capacity (in bytes).
         * @param maxCapacity     a maximum capacity (in bytes).
         * @return a reference to the newly created buffer.
         */
        @Nonnull
        static Buffer alloc(@Nonnegative int initialCapacity, @Nonnegative int maxCapacity) {
                return new ByteBufWrapperBuffer(Unpooled.directBuffer(initialCapacity, maxCapacity));
        }

        /**
         * Allocates a new native buffer using a specific allocator without any specific bounds.
         *
         * @param allocator an allocator.
         * @return a reference to the newly created buffer.
         */
        @Nonnull
        static Buffer alloc(@Nonnull ByteBufAllocator allocator) {
                return new ByteBufWrapperBuffer(allocator.directBuffer());
        }

        /**
         * Allocates a new native buffer using a specific allocator with an initial capacity.
         *
         * Note: This method is to be preferred over {@link #alloc(ByteBufAllocator)} as it may provide performance
         * improvement due to the reduced amount of memory allocations caused when writing data.
         *
         * @param allocator       an allocator.
         * @param initialCapacity an initial capacity (in bytes).
         * @return a reference to the newly created buffer.
         */
        @Nonnull
        static Buffer alloc(@Nonnull ByteBufAllocator allocator, @Nonnegative int initialCapacity) {
                return new ByteBufWrapperBuffer(allocator.directBuffer(initialCapacity));
        }

        /**
         * Allocates a new native buffer using a specific allocator with an initial and maximum capacity.
         *
         * Note: This method is to be preferred over {@link #alloc(ByteBufAllocator)} as it may provide performance
         * improvement due to the reduced amount of memory allocations caused when writing data.
         *
         * @param allocator       an allocator.
         * @param initialCapacity an initial capacity (in bytes)
         * @param maxCapacity     a maximum capacity (in bytes).
         * @return a reference to the newly created buffer.
         */
        @Nonnull
        static Buffer alloc(@Nonnull ByteBufAllocator allocator, @Nonnegative int initialCapacity, @Nonnegative int maxCapacity) {
                return new ByteBufWrapperBuffer(allocator.directBuffer(initialCapacity, maxCapacity));
        }

        /**
         * Retrieves the backing buffer implementation.
         *
         * Note: This method should generally be avoided as it is likely to change in future API versions while most
         * of the methods herein will stay untouched even when compatibility breaking changes are made.
         *
         * @return a buffer.
         */
        @Nonnull
        ByteBuf getBuffer();

        /**
         * Retrieves the absolute amount of readable bytes left within this buffer.
         *
         * @return an amount.
         */
        @Nonnegative
        int getReadableBytes();

        /**
         * Retrieves the current buffer reader index which designates where read methods will start to read new data
         * when invoked.
         *
         * @return an index (in bytes).
         */
        @Nonnegative
        int getReaderIndex();

        /**
         * Retrieves the current buffer writer index which designates where the write methods will start to write new
         * data when invoked.
         *
         * @return an index (in bytes).
         */
        @Nonnegative
        int getWriterIndex();

        /**
         * Checks whether the underlying buffer is utilizing native components to provide efficient methods of storing,
         * modifying and retrieving data utilizing this interface.
         *
         * Note: On some platforms native buffers may not be available or the developer chose to allocate an on-heap
         * buffer instead.
         *
         * @return true if native, false otherwise.
         */
        boolean isNative();

        /**
         * Stores the current reader index to be able to return to it later within the program logic if necessary.
         * Only a single reader index can be stored at a time.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer markReaderIndex();

        /**
         * Stores the current writer index to be able to return to it later within the program logic if necessary.
         * Only a single writer index can be stored at a time.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer markWriterIndex();

        /**
         * Retrieves a single buffer from the buffer and increases its reader index by the amount required to represent
         * it in its encoded form.
         *
         * @return a buffer.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        @Nonnull
        Buffer readBuffer() throws IndexOutOfBoundsException;

        /**
         * Retrieves a single byte from the buffer and increases its reader index by one byte.
         *
         * @return a byte.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        byte readByte() throws IndexOutOfBoundsException;

        /**
         * Retrieves an array of byte values from the buffer and increases its reader index by the amount required to
         * represent its length (Base-128 VarInt) as well as the actual data.
         *
         * @return a byte array.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        byte[] readByteArray() throws IndexOutOfBoundsException;

        /**
         * Retrieves an array of byte values from the buffer and increases its reader index by the amount required to
         * represent its actual data as specified by the length parameter.
         *
         * @param length an overall amount of bytes to retrieve.
         * @return a byte array.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        byte[] readByteArray(@Nonnegative int length) throws IndexOutOfBoundsException;

        /**
         * Retrieves an array of byte values from the buffer, increases its reader index by the amount required to
         * represent its actual data and copies its values into the supplied buffer.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param array a buffer.
         * @return a reference to this buffer.
         *
         * @throws IndexOutOfBoundsException
         */
        @Nonnull
        Buffer readByteArray(@Nonnull byte[] array) throws IndexOutOfBoundsException;

        /**
         * Retrieves a single character from the buffer and increases its reader index by two bytes.
         *
         * @return a character.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        char readChar() throws IndexOutOfBoundsException;

        /**
         * Retrieves a single integer from the buffer and increases its reader index by the amount required to represent
         * it in its Base-128 VarInt encoded form.
         *
         * @return an integer.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        int readInteger() throws IndexOutOfBoundsException;

        /**
         * Retrieves a single long from the buffer and increases its reader index by the amount required to represent it
         * in its Base-128 VarInt encoded form.
         *
         * @return a long.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        long readLong() throws IndexOutOfBoundsException;

        /**
         * Retrieves a single short from the buffer and increases its reader index by two bytes.
         *
         * @return a short.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        short readShort() throws IndexOutOfBoundsException;

        /**
         * Retrieves a single string from the buffer and increases its reader index by the amount required to represent
         * the encoded string length as well as its actual data.
         *
         * This method assumes the string is encoded using UTF-8.
         *
         * @return a string.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        @Nonnull
        String readString() throws IndexOutOfBoundsException;

        /**
         * Retrieves a single string from the buffer and increases its reader index by the amount required to represent
         * the encoded string length (Base-128 VarInt) as well as its actual data.
         *
         * @param charset a charset.
         * @return a string.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        @Nonnull
        String readString(@Nonnull Charset charset) throws IndexOutOfBoundsException;

        /**
         * Retrieves a single UUID from the buffer and increases its reader index by the amount required to represent
         * the encoded UUID as represented by two long values (Base-128 VarInt).
         *
         * @return a UUID.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        @Nonnull
        UUID readUUID() throws IndexOutOfBoundsException;

        /**
         * Retrieves a single unsigned integer from the buffer and increases its reader index by the amount required to
         * represent it in its Base-128 VarInt encoded form.
         *
         * @return an integer.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        @Nonnegative
        long readUnsignedInteger() throws IndexOutOfBoundsException;

        /**
         * Retrieves a single unsigned long from the buffer and increases its reader index by the amount required to
         * represent it in its Base-128 VarInt encoded form.
         *
         * @return an unsigned long.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        @Nonnegative
        long readUnsignedLong() throws IndexOutOfBoundsException;

        /**
         * Returns the buffer reader index to a previously marked position as designated by the previously marked reader
         * index.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer resetReaderIndex();

        /**
         * Returns the buffer writer index to a previously marked position as designated by the previously marked writer
         * index.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer resetWriterIndex();

        /**
         * Sets the current reader index to a specific offset within the buffer to be able to return to this position
         * later within the program logic if necessary. Only a single reader index can be stored at a time.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param index an index (in bytes).
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer setReaderIndex(int index);

        /**
         * Sets the current writer index to a specific offset within the buffer to be able to return to this position
         * later within the program logic if necessary. Only a single writer index can be stored at a time.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param index an index (in bytes).
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer setWriterIndex(int index);

        /**
         * Creates a wrapper around the specified {@link ByteBuf} buffer.
         *
         * @param buffer a buffer to wrap.
         * @return a reference to the wrapped buffer.
         */
        @Nonnull
        static Buffer wrap(@Nonnull ByteBuf buffer) {
                return new ByteBufWrapperBuffer(buffer);
        }

        /**
         * Writes a buffer and thus all of its contents into this buffer and increases its writer index by the amount
         * required to store its length (as a Base-128 VarInt) as well as the passed data itself.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param buffer a buffer.
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer writeBuffer(@Nonnull Buffer buffer);

        /**
         * Writes a buffer and thus all of its contents into this buffer and increases its writer index by the amount
         * required to store the passed data.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param buffer a buffer.
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer writeBuffer(@Nonnull ByteBuf buffer);

        /**
         * Writes a single byte value into the buffer and increases its writer index by one.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param value a byte.
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer writeByte(byte value);

        /**
         * Writes an array of bytes into the buffer and increases its writer index by the amount of bytes used up by its
         * length (as an integer) as well as its actual data (one byte per element).
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param array an array of bytes.
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer writeByteArray(@Nonnull byte[] array);

        /**
         * Writes a single character into the buffer and increases its writer index by two.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param value a character.
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer writeChar(char value);

        /**
         * Writes a single integer value into the buffer and increases its writer index by the amount required to store
         * its value utilizing the Base-128 VarInt standard.
         *
         * The integer is treated as an unsigned type for the purpose of this method by moving its signing bit to the
         * least significant bit.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param value an integer.
         * @return a reference to this buffer.
         *
         * @see #writeUnsignedLong(long) for a detailed documentation on Base-128 VarInts.
         */
        @Nonnull
        Buffer writeInteger(int value);

        /**
         * Writes a single long value into the buffer and increases its writer index by the amount required to store its
         * value utilizing the Base-128 VarInt standard.
         *
         * The integer is treated as an unsigned type for the purpose of this method by moving its signing bit to the
         * least significant bit.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param value a long.
         * @return a reference to this buffer.
         *
         * @see #writeUnsignedLong(long) for a detailed documentation on Base-128 VarInts.
         */
        @Nonnull
        Buffer writeLong(long value);

        /**
         * Writes a single short value into the buffer and increases its writer index by two.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param value a short.
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer writeShort(short value);

        /**
         * Writes a single string value in its encoded form into this buffer and increases its writer index by the
         * amount required to represent its encoded value.
         *
         * This method will assume the target charset is UTF-8.
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param value a string value.
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer writeString(@Nonnull String value);

        /**
         * Writes a single string value in its encoded form into this buffer and increases its writer index by the
         * amount required to represent its encoded value.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param value   a string value.
         * @param charset a charset.
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer writeString(@Nonnull String value, @Nonnull Charset charset);

        /**
         * Writes a single UUID value in its encoded form into this buffer and increases its writer index by the amount
         * required to represent two long values (Base-128 VarInt).
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param value a UUID value.
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer writeUUID(@Nonnull UUID value);

        /**
         * Writes a single unsigned integer value into the buffer and increases its writer index by the amount required
         * to store its value utilizing the Base-128 VarInt standard.
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param value an integer.
         * @return a reference to this buffer.
         *
         * @see #writeUnsignedLong(long) for a detailed documentation on Base-128 VarInts.
         */
        @Nonnull
        Buffer writeUnsignedInteger(@Nonnegative long value);

        /**
         * Writes a single unsigned long value into the buffer and increases its writer index by the amount required to
         * store its value utilizing the Base-128 VarInt standard.
         *
         * For the purposes of compression, an integer is treated as a set of independent bytes consisting of 7 bits
         * where the most significant bit is used to signify whether further data is to be found within the adjacent
         * byte. Additionally the integer is encoded in reverse order (e.g. least significant first).
         *
         * This method returns a reference to its parent instance and is thus chain-able.
         *
         * @param value an unsigned long.
         * @return a reference to this buffer.
         */
        @Nonnull
        Buffer writeUnsignedLong(@Nonnegative long value);
}
