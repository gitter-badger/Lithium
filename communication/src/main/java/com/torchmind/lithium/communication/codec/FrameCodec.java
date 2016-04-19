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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <strong>Frame Codec</strong>
 *
 * Provides a netty codec capable of converting raw binary streams into a set of chunks which consist of
 * content-complete meaningful data.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@ChannelHandler.Sharable
public class FrameCodec extends ByteToMessageCodec<Buffer> {
        private static final Logger logger = LoggerFactory.getLogger(FrameCodec.class);
        private final AtomicBoolean nativeWarning = new AtomicBoolean(false);

        /**
         * {@inheritDoc}
         */
        @Override
        protected void decode(@Nonnull ChannelHandlerContext channelHandlerContext, @Nonnull ByteBuf byteBuf, @Nonnull List<Object> list) throws Exception {
                Buffer buffer = Buffer.wrap(byteBuf);

                if (!this.nativeWarning.getAndSet(true)) {
                        logger.warn("Your platform does not seem to use native buffers. This may cause performance issues!");
                }

                while (buffer.isIntegerAvailable()) {
                        list.add(buffer.readBuffer());
                }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void encode(@Nonnull ChannelHandlerContext channelHandlerContext, @Nonnull Buffer buffer, @Nonnull ByteBuf byteBuf) throws Exception {
                Buffer output = Buffer.alloc(channelHandlerContext.alloc(), buffer.getReadableBytes());
                output.writeBuffer(buffer);
                byteBuf.writeBytes(output.getBuffer());
        }
}
