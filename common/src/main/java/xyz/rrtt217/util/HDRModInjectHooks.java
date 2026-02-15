package xyz.rrtt217.util;

public class HDRModInjectHooks {
    private static final ThreadLocal<Boolean> shouldInject = ThreadLocal.withInitial(() -> false);

    public static void enableInject() {
        shouldInject.set(true);
    }

    public static void disableInject() {
        shouldInject.remove();
    }

    public static boolean isInjectEnabled() {
        return shouldInject.get();
    }
}
