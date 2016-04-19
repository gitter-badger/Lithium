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
package com.torchmind.lithium.communication.packet;

import com.torchmind.lithium.communication.Buffer;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;
import java.util.function.Consumer;

/**
 * <strong>Class Name Packet Registry</strong>
 *
 * Provides a class name based packet registry implementation.
 * This is the default implementation.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Immutable
@ThreadSafe
class ClassNamePacketRegistry implements PacketRegistry {
        private final Map<String, Class<? extends Packet>> registry;

        private ClassNamePacketRegistry(@Nonnull Map<String, Class<? extends Packet>> registry) {
                this.registry = Collections.unmodifiableMap(registry);
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public PacketRegistry.Builder builderFor() {
                return new Builder(this.registry);
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Optional<Class<? extends Packet>> lookup(@Nonnull String identifier) throws IllegalArgumentException {
                // TODO: Verify identifiers against Java fully qualified class name specifications
                return Optional.ofNullable(this.registry.get(identifier));
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Optional<String> lookup(@Nonnull Class<? extends Packet> type) {
                return Optional.of(type.getName()).filter(this.registry::containsKey);
        }

        /**
         * <strong>Class Name Packet Registry Builder</strong>
         *
         * Provides a factory for instances of the {@link ClassNamePacketRegistry} implementation.
         * This is the default, internal, builder implementation.
         */
        static class Builder implements PacketRegistry.Builder {
                private final HashMap<String, Class<? extends Packet>> registry;

                private Builder(@Nonnull Map<String, Class<? extends Packet>> registry) {
                        this.registry = new HashMap<>(registry);
                }

                public Builder() {
                        this.registry = new HashMap<>();
                }

                /**
                 * {@inheritDoc}
                 */
                @Nonnull
                @Override
                public PacketRegistry build() {
                        return null;
                }

                /**
                 * {@inheritDoc}
                 */
                @Nonnull
                @Override
                public PacketRegistry.Builder clear() {
                        this.registry.clear();
                        return this;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public int getSize() {
                        return this.registry.size();
                }

                /**
                 * {@inheritDoc}
                 */
                @Nonnull
                @Override
                public PacketRegistry.Builder ifKnown(@Nonnull Class<? extends Packet> type, @Nonnull Runnable runnable) {
                        if (this.isKnown(type)) {
                                runnable.run();
                        }

                        return this;
                }

                /**
                 * {@inheritDoc}
                 */
                @Nonnull
                @Override
                public PacketRegistry.Builder ifKnown(@Nonnull Class<? extends Packet> type, @Nonnull Consumer<PacketRegistry.Builder> consumer) {
                        return this.ifKnown(type, () -> {
                                consumer.accept(this);
                        });
                }

                /**
                 * {@inheritDoc}
                 */
                @Nonnull
                @Override
                public PacketRegistry.Builder ifUnknown(@Nonnull Class<? extends Packet> type, @Nonnull Runnable runnable) {
                        if (!this.isKnown(type)) {
                                runnable.run();
                        }

                        return this;
                }

                /**
                 * {@inheritDoc}
                 */
                @Nonnull
                @Override
                public PacketRegistry.Builder ifUnknown(@Nonnull Class<? extends Packet> type, @Nonnull Consumer<PacketRegistry.Builder> consumer) {
                        return this.ifUnknown(type, () -> {
                                consumer.accept(this);
                        });
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean isEmpty() {
                        return this.registry.isEmpty();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean isKnown(@Nonnull Class<? extends Packet> type) {
                        return this.registry.containsKey(type.getName());
                }

                /**
                 * {@inheritDoc}
                 */
                @Nonnull
                @Override
                public PacketRegistry.Builder register(@Nonnull Class<? extends Packet> type) throws IllegalArgumentException {
                        this.ifKnown(type, () -> {
                                throw new IllegalArgumentException("Cannot register packet type " + type.getName() + ": Already registered");
                        });

                        try {
                                if (BroadcastPacket.class.isAssignableFrom(type)) {
                                        MethodHandles.publicLookup().findConstructor(type, MethodType.methodType(void.class, UUID.class, short.class, Buffer.class));
                                } else {
                                        MethodHandles.publicLookup().findConstructor(type, MethodType.methodType(void.class, UUID.class, Buffer.class));
                                }
                        } catch (NoSuchMethodException | IllegalAccessException ex) {
                                throw new IllegalArgumentException("Cannot register packet type " + type.getName() + ": Cannot access de-serialization constructor", ex);
                        }

                        this.registry.put(type.getName(), type);
                        return this;
                }

                /**
                 * {@inheritDoc}
                 */
                @Nonnull
                @Override
                public PacketRegistry.Builder registerIfUnknown(@Nonnull Class<? extends Packet> type) throws IllegalArgumentException {
                        return this.ifUnknown(type, () -> {
                                this.register(type);
                        });
                }

                /**
                 * {@inheritDoc}
                 */
                @Nonnull
                @Override
                public PacketRegistry.Builder remove(@Nonnull Class<? extends Packet> type) throws IllegalArgumentException {
                        this.ifUnknown(type, () -> {
                                throw new IllegalArgumentException("Cannot un-register packet type " + type.getName() + ": Unknown type");
                        });

                        this.registry.remove(type.getName());
                        return this;
                }

                /**
                 * {@inheritDoc}
                 */
                @Nonnull
                @Override
                public PacketRegistry.Builder removeIfKnown(@Nonnull Class<? extends Packet> type) {
                        return this.ifKnown(type, () -> {
                                this.remove(type);
                        });
                }
        }
}
