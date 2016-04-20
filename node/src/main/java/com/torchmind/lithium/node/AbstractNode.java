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

import com.torchmind.lithium.communication.Buffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
import java.util.UUID;

/**
 * <strong>Abstract Node</strong>
 *
 * Provides a base implementation for methods which are shared between all types of nodes.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
abstract class AbstractNode implements Node {
        private final Cipher cipher;
        private final UUID identifier;
        private final PublicKey publicKey;
        private final Signature signature;

        AbstractNode(@Nonnull PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
                this.identifier = UUID.nameUUIDFromBytes((new X509EncodedKeySpec(publicKey.getEncoded())).getEncoded());
                this.publicKey = publicKey;

                this.cipher = Cipher.getInstance("RSA");
                this.cipher.init(Cipher.ENCRYPT_MODE, publicKey);

                this.signature = Signature.getInstance("RSA");
                this.signature.initVerify(publicKey);
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Buffer encrypt(@Nonnull ByteBufAllocator allocator, @Nonnull Buffer buffer) throws IllegalBlockSizeException, ShortBufferException {
                byte[] heap = buffer.toByteArray();
                ByteBuf heapOut = allocator.heapBuffer(this.cipher.getOutputSize(heap.length));

                heapOut.writerIndex(this.cipher.update(heap, 0, heap.length, heapOut.array(), heapOut.arrayOffset()));
                return Buffer.wrap(heapOut);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnegative
        public int getDistance(@Nonnull Node node) {
                return this.getDistance(node.getIdentifier());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnegative
        public int getDistance(@Nonnull UUID identifier) {
                return Node.toInteger(this.identifier).xor(Node.toInteger(identifier)).bitLength();
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public UUID getIdentifier() {
                return this.identifier;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public PublicKey getPublicKey() {
                return this.publicKey;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean verify(@Nonnull Buffer data, @Nonnull Buffer signature) throws SignatureException {
                this.signature.update(data.toByteArray());
                return this.signature.verify(signature.toByteArray());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                AbstractNode that = (AbstractNode) o;
                return Objects.equals(this.identifier, that.identifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
                return Objects.hash(this.identifier);
        }
}
