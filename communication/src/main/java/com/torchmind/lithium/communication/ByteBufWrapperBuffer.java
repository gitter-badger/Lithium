package com.torchmind.lithium.communication;

import com.torchmind.lithium.communication.storage.StorageValue;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * <strong>ByteBuf Wrapper Buffer</strong>
 *
 * Provides a basic buffer implementation which utilizes a wrapped {@link io.netty.buffer.ByteBuf} instance to be able
 * to interact with its data.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
class ByteBufWrapperBuffer implements Buffer {
        private final ByteBuf buf;

        public ByteBufWrapperBuffer(@Nonnull ByteBuf buf) {
                this.buf = buf;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public ByteBuf getBuffer() {
                return this.buf;
        }

        /**
         * Retrieves a method handle capable of creating instances of a certain type by means of type specific
         * de-serialization.
         *
         * @param type a type.
         * @return a method handle.
         *
         * @throws NoSuchMethodException  when no de-serializer method was found.
         * @throws IllegalAccessException when the de-serializer method is inaccessible.
         */
        @Nonnull
        private MethodHandle getDeserializationConstructor(@Nonnull Class<?> type) throws IllegalAccessException, NoSuchMethodException {
                return MethodHandles.publicLookup().findConstructor(type, MethodType.methodType(void.class, Buffer.class));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getReadableBytes() {
                return this.buf.readableBytes();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getReaderIndex() {
                return this.buf.readerIndex();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getWriterIndex() {
                return this.buf.writerIndex();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isNative() {
                return this.buf.isDirect();
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer markReaderIndex() {
                this.buf.markReaderIndex();
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer markWriterIndex() {
                this.buf.markWriterIndex();
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer readBuffer() throws IndexOutOfBoundsException {
                return Buffer.wrap(this.buf.readSlice((int) this.readUnsignedInteger()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte readByte() throws IndexOutOfBoundsException {
                return this.buf.readByte();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] readByteArray() throws IndexOutOfBoundsException {
                return this.readByteArray((int) this.readUnsignedInteger());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] readByteArray(@Nonnegative int length) throws IndexOutOfBoundsException {
                byte[] buffer = new byte[length];
                this.readByteArray(buffer);
                return buffer;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer readByteArray(@Nonnull byte[] array) throws IndexOutOfBoundsException {
                this.buf.readBytes(array);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public char readChar() throws IndexOutOfBoundsException {
                return this.buf.readChar();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int readInteger() throws IndexOutOfBoundsException {
                long tmp = this.readUnsignedInteger();
                return (int) ((tmp >>> 1) ^ (tmp << 31));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long readLong() throws IndexOutOfBoundsException {
                long tmp = this.readUnsignedLong();
                return (tmp >>> 1) ^ (tmp << 63);
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public <P extends Packet> P readPacketData(@Nonnull Class<P> type) throws IllegalArgumentException, IllegalStateException, IndexOutOfBoundsException {
                try {
                        MethodHandle handle = this.getDeserializationConstructor(type);
                        return (P) handle.invokeExact(this);
                } catch (NoSuchMethodException | IllegalAccessException ex) {
                        throw new IllegalArgumentException("Invalid packet implementation: Could not access de-serialization constructor", ex);
                } catch (Throwable ex) {
                        throw new IllegalStateException("Cannot de-serialize packet: " + ex.getMessage(), ex);
                }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public short readShort() throws IndexOutOfBoundsException {
                return this.buf.readShort();
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public <V extends StorageValue> V readStorageValue(@Nonnull Class<V> type) {
                try {
                        MethodHandle handle = this.getDeserializationConstructor(type);
                        return (V) handle.invokeExact(this);
                } catch (NoSuchMethodException | IllegalAccessException ex) {
                        throw new IllegalArgumentException("Invalid storage value implementation: Cannot access de-serialization constructor", ex);
                } catch (Throwable ex) {
                        throw new IllegalStateException("Could not decode storage value: " + ex.getMessage(), ex);
                }
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public String readString() throws IndexOutOfBoundsException {
                return this.readString(StandardCharsets.UTF_8);
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public String readString(@Nonnull Charset charset) throws IndexOutOfBoundsException {
                byte[] encoded = this.readByteArray();
                return new String(encoded, charset);
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public UUID readUUID() throws IndexOutOfBoundsException {
                return new UUID(this.readLong(), this.readLong());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long readUnsignedInteger() throws IndexOutOfBoundsException {
                return this.readUnsignedLong(5) & 0xFFFFFFFFL;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long readUnsignedLong() throws IndexOutOfBoundsException {
                return this.readUnsignedLong(10);
        }

        /**
         * Retrieves a single unsigned long with a maximum amount of bytes as specified from the buffer and increases
         * its reader index by the amount required to store the value (Base-128 VarInt).
         *
         * @param maxLength a maximum amount of bytes to retrieve before aborting.
         * @return a long value.
         *
         * @throws IndexOutOfBoundsException when there is not enough data within the buffer to retrieve a value of this type.
         */
        public long readUnsignedLong(int maxLength) throws IndexOutOfBoundsException {
                long value = 0;

                for (int i = 0; ; ++i) {
                        if (i > maxLength) {
                                throw new IndexOutOfBoundsException("Received more than maximum of " + maxLength + " bytes of data in Base-128 VarInt");
                        }

                        byte current = this.readByte();
                        value ^= ((current & 0x7F) << i);

                        if ((value & 0x80) != 0x80) {
                                break;
                        }
                }

                return value;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer resetReaderIndex() {
                this.buf.resetReaderIndex();
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer resetWriterIndex() {
                this.buf.resetWriterIndex();
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer setReaderIndex(int index) {
                this.buf.readerIndex(index);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer setWriterIndex(int index) {
                this.buf.writerIndex(index);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeBuffer(@Nonnull Buffer buffer) {
                this.writeUnsignedInteger(buffer.getReadableBytes());
                return this.writeBuffer(buffer.getBuffer());
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeBuffer(@Nonnull ByteBuf buffer) {
                this.buf.writeBytes(buffer);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeByte(byte value) {
                this.buf.writeByte(value);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeByteArray(@Nonnull byte[] array) {
                this.writeUnsignedInteger(array.length);
                this.buf.writeBytes(array);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeChar(char value) {
                this.buf.writeChar(value);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeInteger(int value) {
                return this.writeUnsignedInteger(((value << 1) ^ (value >>> 31)));
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeLong(long value) {
                return this.writeUnsignedLong(((value << 1) ^ (value >>> 63)));
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writePacketData(@Nonnull Packet packet) throws IllegalArgumentException, IOException {
                try {
                        this.getDeserializationConstructor(packet.getClass());
                } catch (NoSuchMethodException | IllegalAccessException ex) {
                        throw new IllegalArgumentException("Invalid packet implementation: Could not access de-serialization constructor", ex);
                }

                packet.write(this);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeShort(short value) {
                this.buf.writeShort(value);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeStorageValue(@Nonnull StorageValue value) throws IOException {
                try {
                        this.getDeserializationConstructor(value.getClass());
                } catch (NoSuchMethodException | IllegalAccessException ex) {
                        throw new IllegalArgumentException("Invalid storage value implementation: Cannot access de-serialization constructor", ex);
                }

                value.write(this);
                return this;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeString(@Nonnull String value) {
                return this.writeString(value, StandardCharsets.UTF_8);
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeString(@Nonnull String value, @Nonnull Charset charset) {
                byte[] encoded = value.getBytes(charset);
                return this.writeByteArray(encoded);
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeUUID(@Nonnull UUID value) {
                return this.writeLong(value.getMostSignificantBits())
                        .writeLong(value.getLeastSignificantBits());
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeUnsignedInteger(@Nonnegative long value) {
                return this.writeUnsignedLong(value);
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer writeUnsignedLong(@Nonnegative long value) {
                while (value != 0) {
                        byte current = (byte) (value & 0x7F);
                        value >>>= 7;

                        if (value != 0) {
                                current ^= 0x80;
                        }

                        this.writeByte(current);
                }

                return this;
        }
}
