package org.freedesktop.wayland.util;

import org.freedesktop.wayland.raw.C;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class GlobalRef {
    private static final Map<Integer, WeakReference<Object>> objectMap = new ConcurrentHashMap<>();

    public static MemorySegment from(Object obj) {
        MemorySegment ref = Memory.ARENA_AUTO.allocate(C.C_INT); // TODO audit arena
        ref.set(ValueLayout.JAVA_INT, 0, _objectToInt(obj));
        return ref;
    }

    public static Optional<Object> reify(MemorySegment ref) {
        int id = ref.get(ValueLayout.JAVA_INT, 0);
        return _intToObject(id);
    }

    public static int _objectToInt(Object obj) {
        Objects.requireNonNull(obj);
        int id = System.identityHashCode(obj);
        objectMap.put(id, new WeakReference<>(obj));
        return id;
    }

    public static Optional<Object> _intToObject(int id) {
        WeakReference<Object> ref = objectMap.get(id);
        Optional<Object> ret;
        if (ref != null) {
            Object obj = ref.get();
            if (obj != null) {
                ret = Optional.of(obj);
            } else {
                objectMap.remove(id);
                ret = Optional.empty();
                // The object has been garbage collected, remove the mapping
            }
        } else {
            ret = Optional.empty();
        }
        return ret;
    }

    public static void remove(Object obj) {
        int id = _objectToInt(obj);
        objectMap.remove(id);
        cleanup(); // because why not?
    }

    public static void remove(MemorySegment ref) {
        reify(ref).ifPresent(
                GlobalRef::remove
        );
    }

    public static void remove(int id) {
        objectMap.remove(id);
        cleanup(); // because why not?
    }

    // Optionally, periodically clean up null references
    public static void cleanup() {
        objectMap.entrySet().removeIf(entry -> entry.getValue().get() == null);
    }

}
