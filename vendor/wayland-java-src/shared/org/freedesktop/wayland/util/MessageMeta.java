package org.freedesktop.wayland.util;

import org.freedesktop.wayland.raw.wl_message;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Iterator;
import java.util.stream.Stream;

public class MessageMeta {

    public final MemorySegment wlMessagePointer;
    private final Message message;

    protected MessageMeta(final MemorySegment wlMessagePointer,
                          final Message message) {
        this.wlMessagePointer = wlMessagePointer;
        this.message = message;
        ObjectCache.store(this.wlMessagePointer, this);
    }

    public MemorySegment getNativeWlMessage() {
        return this.wlMessagePointer;
    }

    public static MemorySegment initArray(Message[] methods, Arena arena) {
        MemorySegment methods_array = wl_message.allocateArray(methods.length, arena);
        for (int i = 0; i < methods.length; i++) {
            MemorySegment wlMessagePointer = wl_message.asSlice(methods_array, i);
            MessageMeta.init(wlMessagePointer, methods[i]);
        }
        return methods_array;
    }

    /**
     * Is this signature character an argument?
     *
     * Signatures also carry non-argument characters: '?' marks the following
     * argument nullable, and some messages are prefixed with a version digit
     * ("2no", "4iiii", "5ii"). Both the sizing pass and the filling pass use
     * this one predicate, so they cannot disagree and run off the end.
     */
    private static boolean isArgument(final char c) {
        return c == 'i' || c == 'u' || c == 'f' || c == 's'
                || c == 'o' || c == 'n' || c == 'a' || c == 'h';
    }

    private static int argumentCount(final String signature) {
        int count = 0;
        for (int i = 0; i < signature.length(); i++) {
            if (isArgument(signature.charAt(i))) {
                count++;
            }
        }
        return count;
    }

    private static boolean isObject(final char c) {
        return c == 'o' || c == 'n';
    }

    public static MessageMeta init(final MemorySegment wlMessagePointer, final Message message) {
        wl_message.name(wlMessagePointer, Memory.ARENA_AUTO.allocateFrom(message.name()));
        wl_message.signature(wlMessagePointer, Memory.ARENA_AUTO.allocateFrom(message.signature()));

        // libwayland requires wl_message.types to hold ONE ENTRY PER ARGUMENT, with
        // NULL for every argument that is not an object or a new_id (it indexes the
        // array by argument position: attach "?oii" occupies 3 slots for the 3
        // arguments, not 4 for the 4 signature characters).
        //
        // @Message.types only carries the interface classes, and sometimes
        // placeholders (int.class, or the bare Proxy.class standing in for a
        // dynamic new_id), which is both the wrong length and the wrong content.
        // Passing that through made libwayland read past the end of the array:
        // harmless while marshalling, because the new_id interface is supplied
        // explicitly by wl_proxy_marshal_array_constructor, but
        // wl_closure_print() walks *every* argument, so WAYLAND_DEBUG=1
        // dereferenced the out-of-bounds entries and crashed inside
        // wl_proxy_marshal_array_flags.
        final String signature = message.signature();
        final Iterator<Class<?>> interfaces = Stream.of(message.types())
                .filter(c -> c.isAnnotationPresent(Interface.class))
                .iterator();

        final int arguments = argumentCount(signature);
        MemorySegment typesArray = MemorySegment.NULL;
        if (arguments > 0) {
            typesArray = PointerArray.allocate(arguments, Memory.ARENA_AUTO);
            int argument = 0;
            for (int i = 0; i < signature.length(); i++) {
                final char c = signature.charAt(i);
                if (!isArgument(c)) {
                    continue;
                }
                MemorySegment type = MemorySegment.NULL;
                if (isObject(c) && interfaces.hasNext()) {
                    type = InterfaceMeta.get(interfaces.next()).getNativeWlInterface();
                }
                PointerArray.setAtIndex(typesArray, argument++, type);
            }
        }
        wl_message.types(wlMessagePointer, typesArray);
        return new MessageMeta(wlMessagePointer, message);
    }

    public Message getMessage() {
        return this.message;
    }

    @Override
    public int hashCode() {
        return getNativeWlMessage().hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MessageMeta messageMeta = (MessageMeta) o;
        return getNativeWlMessage().equals(messageMeta.getNativeWlMessage());
    }
}
