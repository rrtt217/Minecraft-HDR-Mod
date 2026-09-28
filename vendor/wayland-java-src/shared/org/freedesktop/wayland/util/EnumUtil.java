/*
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
 *
 */

package org.freedesktop.wayland.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EnumUtil {
    private static Map<Class<?>, Map<Integer, Enum<?>>> MAP = new HashMap<>();

    public static <E extends Enum<E>> void register(Class<E> enumClass) {
        MAP.put(enumClass, (Map<Integer, Enum<?>>) buildEnumMap(enumClass));
    }

    public static <E extends Enum<E>> Map<Integer, E> buildEnumMap(Class<E> enumClass) {
        Map<Integer, E> map = new HashMap<>();
        E[] values = enumClass.getEnumConstants();

        try {
            Method getValueMethod = enumClass.getDeclaredMethod("getValue");

            for (E value : values) {
                int intV = (int) getValueMethod.invoke(value);
                map.put(intV, value);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Enum class must have a getValue() method", e);
        }

        MAP.put(enumClass, (Map<Integer, Enum<?>>) map);
        return map;
    }

    /**
     * Given an enum type from the generated code and an integer value returns the corresponding
     * enum value for the integer.
     *
     * @param type the enum type from the wayland-scanner generated code
     * @param i    the integer value
     * @param <E>
     * @return the enum value
     * @throws NullPointerException if enumClass is null or is not from the wayland-scanner generated code
     */
    public static <E extends Enum<E>> E of(Class<E> type, Integer i) {
        Map<Integer, Enum<?>> integerEnumMap = MAP.get(type);
        if (integerEnumMap == null) {
            // force enum class static initialization
            try {
                Class.forName(type.getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            integerEnumMap = MAP.get(type);
        }
        Objects.requireNonNull(integerEnumMap);
        return (E) integerEnumMap.get(i);
    }


    /**
     * Decodes an integer bitmask into an EnumSet of the specified enum type.
     * <p>
     * The function assumes that the enum constants are properly defined in the natural order
     * of their bit positions. It iterates over the bits of the mask and adds the corresponding
     * enum values to the resulting EnumSet.
     * </p>
     *
     * @param <E>       the enum type
     * @param enumClass the Class object representing the enum type
     * @param mask      the integer bitmask to decode
     * @return an EnumSet containing the enum values corresponding to the set bits in the mask
     * @throws NullPointerException     if enumClass is null
     * @throws IllegalArgumentException if mask is negative or if the ordinal value of any enum
     *                                  constant exceeds the number of bits in an int
     */
    public static <E extends Enum<E>> EnumSet<E> decode(Class<E> enumClass, int mask) {
        E[] values = enumClass.getEnumConstants();
        EnumSet<E> result = EnumSet.noneOf(enumClass);
        int code = mask;
        while (code != 0) {
            int ordinal = Integer.numberOfTrailingZeros(code);
            code ^= Integer.lowestOneBit(code);
            result.add(values[ordinal]);
        }
        return result;
    }

    /**
     * Encodes an EnumSet into an integer bitmask.
     * <p>
     * The function assumes that the enum constants are defined in the order of powers of 2.
     * It uses the ordinal values of the enum constants to set the appropriate bits in the bitmask.
     *
     * @param <E> the enum type
     * @param set the EnumSet to be encoded
     * @return an integer bitmask representing the encoded EnumSet
     */
    public static <E extends Enum<E>> int encode(EnumSet<E> set) {
        int ret = 0;
        for (E val : set) {
            ret |= 1 << val.ordinal();
        }
        return ret;
    }

    /**
     * Encodes an EnumSet into an integer bitmask using reflection.
     * <p>
     * Unlike {@link EnumUtil#encode(EnumSet)}, this method does not rely on the ordinal values
     * of the enum constants. Instead, it uses reflection to invoke a {@code getValue()} method
     * on each enum constant to obtain the corresponding bit value. The resulting bitmask is
     * constructed by performing a bitwise OR operation on the obtained bit values.
     * <p>
     * The enum type must have a {@code getValue()} method that returns the desired bit value
     * for each constant.
     *
     * @param <E> the enum type
     * @param set the EnumSet to be encoded
     * @return an integer bitmask representing the encoded EnumSet
     * @throws IllegalArgumentException if the enum type does not have a {@code getValue()} method
     *                                  or if there is an exception during reflection
     */
    public static <E extends Enum<E>> int encodeWithReflection(EnumSet<E> set) {
        int ret = 0;
        for (E val : set) {
            try {
                Method getValueMethod = val.getClass().getMethod("getValue");
                int bitValue = (int) getValueMethod.invoke(val);
                ret |= bitValue;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Enum type must have a getValue() method", e);
            }
        }
        return ret;
    }
}
