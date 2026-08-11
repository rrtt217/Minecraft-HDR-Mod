package xyz.rrtt217.HDRMod.util.state;

public class HDRModInjectHooks {
    private static final ThreadLocal<Boolean> vanillaF2Screenshot = ThreadLocal.withInitial(() -> false);

    public static void setVanillaF2Screenshot() { vanillaF2Screenshot.set(true); }

    public static void unsetVanillaF2Screenshot() { vanillaF2Screenshot.set(false); }

    public static boolean getVanillaF2Screenshot() {return vanillaF2Screenshot.get();}
}
