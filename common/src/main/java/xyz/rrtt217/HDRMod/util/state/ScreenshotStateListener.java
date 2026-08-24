package xyz.rrtt217.HDRMod.util.state;

public class ScreenshotStateListener {
    private static final ThreadLocal<Boolean> vanillaF2Screenshot = ThreadLocal.withInitial(() -> false);

    public static void setVanillaF2Screenshot() { vanillaF2Screenshot.set(true); }

    public static void unsetVanillaF2Screenshot() { vanillaF2Screenshot.set(false); }

    public static boolean getVanillaF2Screenshot() {return vanillaF2Screenshot.get();}

    /**
     * Behavior of the mod's screenshot feature when the vanilla Minecraft
     * screenshot is also triggered.
     */
    public enum BehaviorOnVanillaScreenshotCalled{
        /** Only take the vanilla (SDR) screenshot. */
        ONLY_VANILLA,
        /** Take both the vanilla and the HDR screenshot. */
        BOTH,
        /** Only take the HDR screenshot. */
        ONLY_HDR
    }
}
