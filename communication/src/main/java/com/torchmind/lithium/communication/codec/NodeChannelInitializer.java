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

import com.torchmind.lithium.communication.packet.PacketRegistry;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramChannel;

import javax.annotation.Nonnull;

/**
 * <strong>Node Channel Initializer</strong>
 *
 * Initializes the channel pipeline to be able to handle incoming binary data by means of step-by-step processing.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class NodeChannelInitializer extends ChannelInitializer<DatagramChannel> {
        private final PacketRegistry registry;

        public NodeChannelInitializer(@Nonnull PacketRegistry registry) {
                this.registry = registry;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initChannel(DatagramChannel ch) throws Exception {
                ch.pipeline()
                        .addLast("frameCodec", new FrameCodec())
                        .addLast("packetCodec", new PacketCodec(this.registry));
        }
}
