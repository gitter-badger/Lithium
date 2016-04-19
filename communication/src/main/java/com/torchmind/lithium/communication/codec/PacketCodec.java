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
package com.torchmind.lithium.communication.codec;

import com.torchmind.lithium.communication.Buffer;
import com.torchmind.lithium.communication.packet.Packet;
import com.torchmind.lithium.communication.packet.PacketRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <strong>Packet Codec</strong>
 *
 * Provides a netty codec capable of converting raw binary data into simple Java objects.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@ChannelHandler.Sharable
public class PacketCodec extends MessageToMessageCodec<Buffer, Packet> {
        private final PacketRegistry registry;

        public PacketCodec(@Nonnull PacketRegistry registry) {
                this.registry = registry;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, Buffer buffer, List<Object> list) throws Exception {
                String packetIdentifier = buffer.readString();
                list.add(buffer.readPacketData(this.registry.lookup(packetIdentifier).orElseThrow(() -> new NoSuchElementException("Identifier lookup for packet identifier \"" + packetIdentifier + "\" failed"))));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, List<Object> list) throws Exception {
                list.add(
                        Buffer.alloc(channelHandlerContext.alloc())
                                .writeString(this.registry.lookup(packet.getClass()).orElseThrow(() -> new NoSuchElementException("Identifier lookup for packet type " + packet.getClass() + " failed")))
                                .writePacketData(packet)
                );
        }
}
