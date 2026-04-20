package xyz.rrtt217.HDRMod.compat.ixeris;

import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import me.decce.ixeris.core.threading.RenderThreadDispatcher;
import xyz.rrtt217.HDRMod.util.ime.*;
import org.lwjgl.system.Callback;

public class IMEStatusCallbackDispatcher {
    private static final Long2ReferenceMap<IMEStatusCallbackDispatcher> instance = new Long2ReferenceArrayMap<>(1);

    private final ReferenceArrayList<GLFWIMEStatusCallbackI> mainThreadCallbacks = new ReferenceArrayList<>(1);
    private boolean lastCallbackSet;
    public GLFWIMEStatusCallbackI lastCallback;
    public long lastCallbackAddress;

    private final long window;
    public volatile boolean suppressChecks;

    private IMEStatusCallbackDispatcher(long window) {
        this.window = window;
    }

    public synchronized static IMEStatusCallbackDispatcher get(long window) {
        if (!instance.containsKey(window)) {
            instance.put(window, new IMEStatusCallbackDispatcher(window));
            instance.get(window).validate();
        }
        return instance.get(window);
    }

    public synchronized void registerMainThreadCallback(GLFWIMEStatusCallbackI callback) {
        mainThreadCallbacks.add(callback);
        this.validate();
    }

    public synchronized long update(long newAddress) {
        suppressChecks = true;
        long ret = lastCallbackAddress;
        if (newAddress == 0L && this.mainThreadCallbacks.isEmpty()) {
            GLFWIMEUtils.nglfwSetIMEStatusCallback(window, 0L);
        } else {
            GLFWIMEUtils.nglfwSetIMEStatusCallback(window, CommonCallbacks_334.iMEStatusCallback.address());
        }
        lastCallbackAddress = newAddress;
        if (!lastCallbackSet) {
            lastCallback = newAddress == 0L ? null : Callback.get(newAddress);
        }
        lastCallbackSet = false;
        suppressChecks = false;
        return ret;
    }

    public synchronized void update(GLFWIMEStatusCallbackI callback) {
        lastCallback = callback;
        lastCallbackSet = true;
    }

    public synchronized void validate() {
        suppressChecks = true;
        var current = GLFWIMEUtils.nglfwSetIMEStatusCallback(window, CommonCallbacks_334.iMEStatusCallback.address());
        if (current == 0L) {
            if (this.mainThreadCallbacks.isEmpty()) {
                // Remove callback when not needed
                GLFWIMEUtils.nglfwSetIMEStatusCallback(window, 0L);
            }
        } else if (current != CommonCallbacks_334.iMEStatusCallback.address()) {
            // This only happens when mods register callbacks without using LWJGL (e.x. directly in native code)
            lastCallback = Callback.get(current);
            lastCallbackAddress = current;
        }
        suppressChecks = false;
    }

    public void onCallback(long window) {
        if (this.window != window) {
            return;
        }
        for (int i = 0; i < mainThreadCallbacks.size(); i++) {
            mainThreadCallbacks.get(i).invoke(window);
        }
        if (lastCallback != null) {
            var callback = lastCallback; // Keep a reference to the current callback; they are used as FunctionalInterface's so there are no issue even if the callback is already freed when we use it
            RenderThreadDispatcher.runLater((DispatchedRunnable) () -> {
                callback.invoke(window);
            });
        }
    }

    @FunctionalInterface
    public interface DispatchedRunnable extends Runnable {
    }
}