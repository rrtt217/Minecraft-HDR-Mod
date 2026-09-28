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


import org.freedesktop.wayland.raw.wl_dispatcher_func_t;

import java.lang.foreign.MemorySegment;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public final class Dispatcher {
    public static final MemorySegment INSTANCE = wl_dispatcher_func_t.allocate(Dispatcher::invoke, Memory.ARENA_AUTO);
    private static final Map<Class<?>, Map<Integer, Method>> METHOD_CACHE = new HashMap<>();
    private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE = new HashMap<>();

    public static int invoke(final MemorySegment implementation,
                             final MemorySegment wlObject /* wl_proxy or wl_resource */,
                             final int opcode,
                             MemorySegment wlMessage,
                             MemorySegment wlArguments) {

        Method method = null;
        Object[] jargs = null;
        Message message = null;
        WaylandObject waylandObject = null;

        try {
            message = ObjectCache.<MessageMeta>from(wlMessage)
                    .getMessage();
            waylandObject = ObjectCache.from(wlObject);
            method = get(
                    waylandObject.getClass(),
                    waylandObject.getImplementation().getClass(),
                    message
            );

            final String signature = message.signature();
            //TODO do something with the version signature? Somehow see which version the implementation exposes and
            // check if it matches?
            final String messageSignature;
            if (signature.length() > 0 && Character.isDigit(signature.charAt(0))) {
                messageSignature = signature.substring(1);
            } else {
                messageSignature = signature;
            }

            final int nroArgs = message.types().length;
            jargs = new Object[nroArgs + 1];
            jargs[0] = waylandObject;

            if (nroArgs > 0) {
                final Arguments arguments = new Arguments(wlArguments);
                boolean optional = false;
                int argIndex = 0;
                for (final char signatureChar : messageSignature.toCharArray()) {
                    if (signatureChar == '?') {
                        optional = true;
                        continue;
                    }
                    final Object jarg = fromArgument(arguments,
                            argIndex,
                            signatureChar,
                            message.types()[argIndex]);
                    if (!optional && jarg == null) {
                        throw new IllegalArgumentException(String.format("Got non optional argument that is null!. "
                                        + "Message: %s(%s), violated arg index: %d",
                                message.name(),
                                message.signature(),
                                argIndex));
                    }
                    argIndex++;
                    jargs[argIndex] = jarg;
                    optional = false;
                }
            }
            method.invoke(waylandObject.getImplementation(), jargs);
        } catch (final Exception e) {
            System.err.printf("""
                            Got an exception in the wayland dispatcher, This is most likely a bug.
                              method=%s
                              implementation=%s
                              arguments=%s
                              message=%s%n""",
                    method,
                    waylandObject == null ? "waylandObjectNull" : waylandObject.getImplementation(),
                    Arrays.toString(jargs),
                    message);
            e.printStackTrace();
        }

        return 0;
    }

    private static Method get(final Class<? extends WaylandObject> waylandObjectType,
                              final Class<?> implementationType,
                              final Message message) throws NoSuchMethodException {

        Map<Integer, Method> methodMap = METHOD_CACHE.get(implementationType);
        if (methodMap == null) {
            methodMap = new HashMap<>();
            METHOD_CACHE.put(implementationType,
                    methodMap);
        }

        final int methodHash = Objects.hash(waylandObjectType,
                message);
        Method method = methodMap.get(methodHash);
        if (method == null) {
            final Class<?>[] types = message.types();
            final Class<?>[] argTypes = new Class<?>[types.length + 1];
            //copy to new array and shift by 1
            System.arraycopy(types, 0, argTypes, 1, types.length);
            argTypes[0] = waylandObjectType;
            method = implementationType.getMethod(message.functionName(), argTypes);
            method.setAccessible(true);
            methodMap.put(methodHash, method);
        }
        return method;
    }

    private static Object fromArgument(final Arguments arguments,
                                       final int index,
                                       final char type,
                                       final Class<?> targetType) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        switch (type) {
            case 'u': {
                return arguments.getU(index);
            }
            case 'i': {
                return arguments.getI(index);
            }
            case 'f': {
                return arguments.getFixed(index);
            }
            case 'h': {
                return arguments.getH(index);
            }
            case 'o': {
                final MemorySegment waylandObjectPointer = arguments.getO(index);

                final WaylandObject waylandObject;
                if (MemorySegment.NULL.equals(waylandObjectPointer)) {
                    waylandObject = null;
                } else {
                    final WaylandObject cachedObject = ObjectCache.from(waylandObjectPointer);
                    if (cachedObject == null) {
                        waylandObject = reconstruct(waylandObjectPointer,
                                targetType);
                    } else {
                        waylandObject = cachedObject;
                    }
                }
                return waylandObject;
            }
            case 'n': {
                return arguments.getN(index);
            }
            case 's': {
                return arguments.getS(index);
            }
            case 'a': {
                return new WlArray(arguments.getA(index));
            }
            default: {
                throw new IllegalArgumentException("Can not convert wl_argument type: " + type);
            }
        }
    }

    private static WaylandObject reconstruct(final MemorySegment objectPointer,
                                             final Class<?> targetType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor = CONSTRUCTOR_CACHE.get(targetType);
        if (constructor == null) {
            //FIXME use static get(Pointer) method instead of proxy or resource
            constructor = targetType.getDeclaredConstructor(Long.class);
            constructor.setAccessible(true);
            CONSTRUCTOR_CACHE.put(targetType,
                    constructor);
        }
        return (WaylandObject) constructor.newInstance(objectPointer);
    }
}