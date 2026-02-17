package xyz.rrtt217.util;

public class HDRModInjectHooks {
    private static final ThreadLocal<Boolean> shouldInject = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> shouldInject2 = ThreadLocal.withInitial(() -> false);

    public static void enableInject() {
        shouldInject.set(true);
    }
    public static void enableInject2() {
        shouldInject2.set(true);
    }

    public static void disableInject() {
        shouldInject.remove();
    }
    public static void disableInject2() {
        shouldInject2.remove();
    }

    public static boolean isInjectEnabled() {
        return shouldInject.get();
    }
    public static boolean isInject2Enabled() {
        return shouldInject2.get();
    }
}
