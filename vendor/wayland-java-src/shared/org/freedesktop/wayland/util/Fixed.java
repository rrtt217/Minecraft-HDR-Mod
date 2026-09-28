/*
 * Copyright © 2015 Erik De Rijcke
 * Copyright © 2024 Casey Link
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package org.freedesktop.wayland.util;

public final class Fixed {

    private final int raw;

    public Fixed(final int raw) {
        this.raw = raw;
    }

    public static Fixed create(final int value) {
        return new Fixed(value << 8);
    }

    public static Fixed create(final float value) {
        return new Fixed((int) (value * 256 + 0.5));
    }

    public int getRaw() {
        return this.raw;
    }

    @Override
    public int hashCode() {
        return raw;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Fixed fixed = (Fixed) o;

        return raw == fixed.raw;
    }

    @Override
    public String toString() {
        return "Fixed{" +
                " raw=" + raw +
                " asInt=" + asInt() +
                " asFloat=" + asFloat() +
                '}';
    }

    public int asInt() {
        return this.raw >> 8;
    }

    public float asFloat() {
        return (float) this.raw / 256.0f;
    }
}