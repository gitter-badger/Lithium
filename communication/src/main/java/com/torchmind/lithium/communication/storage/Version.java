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
package com.torchmind.lithium.communication.storage;

import com.torchmind.lithium.communication.Buffer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * <strong>Version</strong>
 *
 * Represents a version value which consists of three bits (major, minor and patch) within the protocol.
 * To achieve a perfect compression, this representation indicates whether more data follows by setting its most
 * significant bit on each byte written. Thus the minimal amount taken is equal to one byte while the maximum amount
 * is three bytes.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class Version implements StorageValue {
        private final byte major;
        private final byte minor;
        private final byte patch;

        public Version(@Nonnegative byte major, @Nonnegative byte minor, @Nonnegative byte patch) {
                this.major = major;
                this.minor = minor;
                this.patch = patch;
        }

        public Version(@Nonnull Buffer buffer) {
                byte major = buffer.readByte();
                byte minor = 0;
                byte patch = 0;

                if ((major & 0x80) == 0x80) {
                        minor = buffer.readByte();

                        if ((minor & 0x80) == 0x80) {
                                patch = buffer.readByte();
                        }
                }

                this.major = (byte) (major & 0x7F);
                this.minor = (byte) (minor & 0x7F);
                this.patch = patch;
        }

        @Nonnegative
        public byte getMajor() {
                return major;
        }

        @Nonnegative
        public byte getMinor() {
                return minor;
        }

        @Nonnegative
        public byte getPatch() {
                return patch;
        }

        /**
         * Checks whether this version is considered compatible with the supplied version by comparing their major bits
         * which indicate backwards incompatible changes.
         *
         * @param version a version to compare against.
         * @return true if compatible, false otherwise.
         */
        boolean isCompatibleWith(@Nonnull Version version) {
                return this.major == version.major;
        }

        /**
         * Checks whether this version is considered newer than the supplied version by means of comparing its separate
         * bits by priority (in order: major, minor, patch).
         *
         * @param version a version to compare against.
         * @return true if newer, false otherwise.
         */
        boolean isNewerThan(@Nonnull Version version) {
                return (this.major > version.major || this.minor > version.minor || this.patch > version.patch);
        }

        /**
         * Checks whether this version is considered older than the supplied version by means of comparing its separate
         * bits by priority (in order: major, minor, patch).
         *
         * @param version a version to compare against.
         * @return true if newer, false otherwise.
         */
        boolean isOlderThan(@Nonnull Version version) {
                return (this.major < version.major || this.minor < version.minor || this.patch < version.patch);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void write(@Nonnull Buffer buffer) throws IOException {
                buffer.writeByte((byte) (this.major ^ (this.minor != 0 && this.patch != 0 ? 0x80 : 0x00)));

                if (this.minor != 0 || this.patch != 0) {
                        buffer.writeByte((byte) (this.minor ^ (this.patch != 0 ? 0x80 : 0x00)));
                }

                if (this.patch != 0) {
                        buffer.writeByte(this.patch);
                }
        }
}
