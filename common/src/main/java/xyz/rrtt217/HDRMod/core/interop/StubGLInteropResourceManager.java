package xyz.rrtt217.HDRMod.core.interop;

public class StubGLInteropResourceManager extends GLInteropResourceManager {
    @Override
    public boolean shouldReplaceFbo(int originalFbo) {
        return false;
    }
}
