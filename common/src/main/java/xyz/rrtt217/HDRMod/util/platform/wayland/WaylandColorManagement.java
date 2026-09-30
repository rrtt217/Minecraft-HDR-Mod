package xyz.rrtt217.HDRMod.util.platform.wayland;

import org.freedesktop.wayland.client.EventQueue;
import org.freedesktop.wayland.client.WlDisplayProxy;
import org.freedesktop.wayland.client.WlRegistryEvents;
import org.freedesktop.wayland.client.WlRegistryProxy;
import org.freedesktop.wayland.client.WlSurfaceProxy;
import org.freedesktop.wayland.client.WpColorManagementSurfaceFeedbackV1EventsV2;
import org.freedesktop.wayland.client.WpColorManagementSurfaceFeedbackV1Proxy;
import org.freedesktop.wayland.client.WpColorManagementSurfaceV1Events;
import org.freedesktop.wayland.client.WpColorManagementSurfaceV1Proxy;
import org.freedesktop.wayland.client.WpColorManagerV1EventsV2;
import org.freedesktop.wayland.client.WpColorManagerV1Proxy;
import org.freedesktop.wayland.client.WpImageDescriptionCreatorParamsV1Events;
import org.freedesktop.wayland.client.WpImageDescriptionCreatorParamsV1Proxy;
import org.freedesktop.wayland.client.WpImageDescriptionInfoV1Events;
import org.freedesktop.wayland.client.WpImageDescriptionInfoV1Proxy;
import org.freedesktop.wayland.client.WpImageDescriptionV1EventsV2;
import org.freedesktop.wayland.client.WpImageDescriptionV1Proxy;
import org.freedesktop.wayland.shared.WpColorManagerV1Feature;
import org.freedesktop.wayland.shared.WpColorManagerV1RenderIntent;
import org.freedesktop.wayland.shared.WpColorManagerV1TransferFunction;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * Wayland HDR color management built directly on Ramblurr/wayland-java.
 *
 * wayland-java's generated proxies all have a public {@code (MemorySegment)}
 * constructor, so SDL's raw wl_display / wl_surface can be wrapped instead of
 * opening a second Wayland connection:
 *
 *   WlDisplayProxy display = new WlDisplayProxy(sdlWlDisplay);
 *   WlSurfaceProxy surface = new WlSurfaceProxy(sdlWlSurface);
 *
 * Everything then happens on a private EventQueue (the same isolation trick the
 * C shim uses), and the wl_display is never connected or disconnected here —
 * SDL owns it.
 *
 * To make it correct, I referred a lot to <a href="https://github.com/Tom94/glfw/tree/tev">...</a>.
 * Thanks a lot for tom94's hard effort. Here's the complete original license:
 *
 * Copyright (c) 2002-2006 Marcus Geelnard
 *
 * Copyright (c) 2006-2019 Camilla Löwy
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would
 *    be appreciated but is not required.
 *
 * 2. Altered source versions must be plainly marked as such, and must not
 *    be misrepresented as being the original software.
 *
 * 3. This notice may not be removed or altered from any source
 *    distribution.
 *
 */
public final class WaylandColorManagement implements AutoCloseable {

    public static final int OK = 0;
    public static final int UNSUPPORTED = -1;
    public static final int FAILED = -2;
    public static final int TIMEOUT = -4;

    /** wp_color_manager_v1: chromaticity coordinates are multiplied by 1e6. */
    public static final int COLOR_FACTOR = 1_000_000;
    /** wp_color_manager_v1: minimum luminance is multiplied by 1e4. */
    public static final int MIN_LUMINANCE_FACTOR = 10_000;

    private static final int POLLIN = 0x001;

    /** Do not touch the display proxy: SDL owns the connection. */
    private final WlDisplayProxy display;
    /** Do not destroy the surface proxy: SDL owns the wl_surface. */
    private final WlSurfaceProxy surface;
    private final EventQueue queue;
    private final MethodHandle poll;
    private final MemorySegment pollfd;

    private WlRegistryProxy registry;
    private WpColorManagerV1Proxy cm;
    private WpColorManagementSurfaceV1Proxy cmSurface;
    private WpColorManagementSurfaceFeedbackV1Proxy feedback;
    private WpImageDescriptionV1Proxy desc;

    private final Set<Integer> features = new HashSet<>();
    private final Set<Integer> tfs = new HashSet<>();
    private final Set<Integer> primaries = new HashSet<>();
    private final Set<Integer> intents = new HashSet<>();
    private boolean featuresDone;

    private boolean descReady;
    private boolean descFailed;
    private long descIdentity;
    private String message = "";

    private boolean infoDone;
    private float minLuminance;
    private float maxLuminance;
    private float sdrWhiteLevel;

    /** Whether the last apply left luminance choice to us (2-arg overload). */
    private boolean autoLuminances;
    private int lastTf;
    private int lastPrimaries;

    private WaylandColorManagement(WlDisplayProxy display, WlSurfaceProxy surface, EventQueue queue,
                            MethodHandle poll, MemorySegment pollfd) {
        this.display = display;
        this.surface = surface;
        this.queue = queue;
        this.poll = poll;
        this.pollfd = pollfd;
    }

    public static WaylandColorManagement attach(MemorySegment wlDisplayPtr, MemorySegment wlSurfacePtr,
                                         int timeoutMs) throws Throwable {
        WlDisplayProxy display = new WlDisplayProxy(wlDisplayPtr);
        EventQueue queue = display.createQueue();
        // NOTE: deliberately NOT surface.setQueue(queue). SDL keeps using its
        // wl_surface, and its proxy is only ever passed as an argument to
        // get_surface() -- the wp_color_management_surface_v1 is a child of the
        // manager, so it inherits the manager's queue anyway.
        WlSurfaceProxy surface = new WlSurfaceProxy(wlSurfacePtr);

        Arena arena = Arena.global();
        SymbolLookup libc = SymbolLookup.libraryLookup("libc.so.6", arena);
        MethodHandle poll = Linker.nativeLinker().downcallHandle(
                libc.find("poll").orElseThrow(() -> new UnsatisfiedLinkError("poll")),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
                        ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT));
        MemorySegment pollfd = arena.allocate(8);

        WaylandColorManagement cm = new WaylandColorManagement(display, surface, queue, poll, pollfd);
        cm.handshake(timeoutMs);
        if (cm.cm == null) {
            throw new IllegalStateException(cm.message.isEmpty()
                    ? "compositor does not advertise wp_color_manager_v1" : cm.message);
        }
        cm.setupFeedback(timeoutMs);
        return cm;
    }

    private void handshake(int timeoutMs) throws Throwable {
        final WpColorManagerV1Proxy[] bound = new WpColorManagerV1Proxy[1];

        WpColorManagerV1EventsV2 cmEvents = new WpColorManagerV1EventsV2() {
            @Override public void supportedIntent(WpColorManagerV1Proxy e, int renderIntent) { intents.add(renderIntent); }
            @Override public void supportedFeature(WpColorManagerV1Proxy e, int feature) { features.add(feature); }
            @Override public void supportedTfNamed(WpColorManagerV1Proxy e, int tf) { tfs.add(tf); }
            @Override public void supportedPrimariesNamed(WpColorManagerV1Proxy e, int p) { primaries.add(p); }
            @Override public void done(WpColorManagerV1Proxy e) { featuresDone = true; }
        };

        registry = display.getRegistry(new WlRegistryEvents() {
            @Override
            public void global(WlRegistryProxy emitter, int name, String interfaceName, int version) {
                if (WpColorManagerV1Proxy.INTERFACE_NAME.equals(interfaceName)) {
                    int v = Math.min(version, WpColorManagerV1EventsV2.VERSION);
                    bound[0] = emitter.bind(name, WpColorManagerV1Proxy.class, v, cmEvents);
                    // children (creator, description, cm-surface) inherit this queue
                    bound[0].setQueue(queue);
                }
            }

            @Override
            public void globalRemove(WlRegistryProxy emitter, int name) { }
        });
        registry.setQueue(queue);

        if (!waitUntil(() -> bound[0] != null && featuresDone, timeoutMs)) {
            message = "timed out discovering wp_color_manager_v1 (bound=" + (bound[0] != null) + ")";
            return;
        }
        cm = bound[0];
    }

    /**
     * Wait with a deadline using the prepare-read / poll / read-events /
     * dispatch-pending cycle, exactly like the C shim, so we never block
     * forever and never dispatch SDL's objects.
     */
    private boolean waitUntil(BooleanSupplier cond, int timeoutMs) throws Throwable {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (!cond.getAsBoolean() && System.currentTimeMillis() < deadline) {
            pump(50);
        }
        return cond.getAsBoolean();
    }

    /** One non-blocking-ish iteration of the queue. */
    public void pump(int timeoutMs) throws Throwable {
        if (display.prepareReadQueue(queue) == 0) {
            display.flush();
            pollfd.set(ValueLayout.JAVA_INT, 0, display.getFD());
            pollfd.set(ValueLayout.JAVA_SHORT, 4, (short) POLLIN);
            // poll(fds, nfds, timeout): exactly ONE pollfd, timeout in ms.
            int n = (int) poll.invoke(pollfd, 1L, timeoutMs);
            if (n > 0) {
                display.readEvents();
            } else {
                display.cancelRead();
            }
        }
        display.dispatchQueuePending(queue);
    }

    public boolean supportsFeature(int feature) {
        return features.contains(feature);
    }

    public boolean supportsPrimaries(int p) {
        return primaries.contains(p);
    }

    public boolean supportsTf(int tf) {
        return tfs.contains(tf);
    }

    public boolean supportsIntent(int intent) {
        return intents.contains(intent);
    }

    public Set<Integer> features() {
        return features;
    }

    public String message() {
        return message;
    }

    public long descriptionIdentity() {
        return descIdentity;
    }

    /** Primary color volume minimum luminance in cd/m², as advertised by the compositor. */
    public float minLuminance() {
        return minLuminance;
    }

    /** Primary color volume maximum luminance in cd/m², as advertised by the compositor. */
    public float maxLuminance() {
        return maxLuminance;
    }

    /**
     * Reference white (SDR white) luminance in cd/m², as advertised by the
     * compositor. Falls back to 80 cd/m², mirroring GLFW's
     * {@code _glfwGetWindowSdrWhiteLevelWayland}.
     */
    public float sdrWhiteLevel() {
        return sdrWhiteLevel != 0.0f ? sdrWhiteLevel : 80.0f;
    }

    /**
     * Rendering intent: prefer relative colorimetric (colour-accurate) and fall
     * back to perceptual, which is guaranteed when colour management is
     * supported.
     */
    private int renderingIntent() {
        if (supportsIntent(WpColorManagerV1RenderIntent.RELATIVE.getValue())) {
            return WpColorManagerV1RenderIntent.RELATIVE.getValue();
        }
        return WpColorManagerV1RenderIntent.PERCEPTUAL.getValue();
    }

    /** Default primary color volume minimum luminance (cd/m²) for a named transfer function. */
    public static float transferDefaultMinNits(int tf) {
        WpColorManagerV1TransferFunction function = WpColorManagerV1TransferFunction.of(tf);
        if (function == null) {
            return 0.02f;
        }
        return switch (function) {
            case BT1886 -> 0.01f;
            case ST2084_PQ -> 0.005f;
            case HLG -> 0.005f;
            default -> 0.02f;
        };
    }

    /** Default primary color volume maximum luminance (cd/m²) for a named transfer function. */
    public static float transferDefaultMaxNits(int tf) {
        WpColorManagerV1TransferFunction function = WpColorManagerV1TransferFunction.of(tf);
        if (function == null) {
            return 80.0f;
        }
        return switch (function) {
            case BT1886 -> 100.0f;
            case ST2084_PQ -> 10000.0f;
            case HLG -> 1000.0f;
            default -> 80.0f;
        };
    }

    /** Default reference white (SDR white) luminance (cd/m²) for a named transfer function. */
    public static float transferDefaultRefWhiteNits(int tf) {
        WpColorManagerV1TransferFunction function = WpColorManagerV1TransferFunction.of(tf);
        if (function == null) {
            return 80.0f;
        }
        return switch (function) {
            case BT1886 -> 100.0f;
            case ST2084_PQ -> 203.0f;
            case HLG -> 203.0f;
            default -> 80.0f;
        };
    }

    /**
     * Create and attach a parametric image description without exposing
     * luminance settings. Mirrors GLFW's {@code updateColorManagedSurface}:
     * luminances are only sent when the compositor advertises
     * {@code set_luminances} and a system SDR white level is known; otherwise
     * the transfer function's default reference white is remembered so that a
     * later call (or the info query) can apply it.
     *
     * @param tf        named transfer function ({@code set_tf_named})
     * @param primaries named primaries ({@code set_primaries_named})
     */
    public int apply(int tf, int primaries) throws Throwable {
        autoLuminances = true;
        lastTf = tf;
        lastPrimaries = primaries;
        boolean setLuminances = supportsFeature(WpColorManagerV1Feature.SET_LUMINANCES.getValue())
                && sdrWhiteLevel != 0.0f;
        int minLum = (int) (transferDefaultMinNits(tf) * MIN_LUMINANCE_FACTOR);
        int maxLum = (int) transferDefaultMaxNits(tf);
        int referenceLum;
        if (setLuminances) {
            referenceLum = (int) sdrWhiteLevel;
        } else {
            // No system white level (yet): adhere to the default white of the
            // chosen transfer function rather than reporting a bogus 0.
            referenceLum = (int) transferDefaultRefWhiteNits(tf);
            sdrWhiteLevel = transferDefaultRefWhiteNits(tf);
        }
        return applyInternal(tf, primaries, setLuminances, minLum, maxLum, referenceLum);
    }

    /**
     * Create and attach a parametric image description, passing the transfer
     * function, primaries and luminance range straight through to the
     * corresponding {@link WpImageDescriptionCreatorParamsV1Proxy} setters.
     *
     * @param tf           named transfer function ({@code set_tf_named})
     * @param primaries    named primaries ({@code set_primaries_named})
     * @param minLum       minimum luminance in cd/m² * 10000 ({@code set_luminances})
     * @param maxLum       maximum luminance in cd/m² ({@code set_luminances})
     * @param referenceLum reference white luminance in cd/m² ({@code set_luminances})
     */
    public int apply(int tf, int primaries, int minLum, int maxLum, int referenceLum) throws Throwable {
        autoLuminances = false;
        sdrWhiteLevel = referenceLum;
        return applyInternal(tf, primaries, true, minLum, maxLum, referenceLum);
    }

    /**
     * Refresh after the compositor's preferred image description changed:
     * re-reads the preferred luminances and re-applies the last two-argument
     * {@link #apply(int, int)} request. Does nothing when the last apply used
     * explicit luminances (the five-argument overload), so callers that manage
     * luminances themselves are never overridden.
     *
     * @return the status of the re-apply, or {@link #OK} when nothing was done
     */
    public int reapply() throws Throwable {
        if (!autoLuminances) {
            return OK;
        }
        refreshPreferred(1000);
        return apply(lastTf, lastPrimaries);
    }

    private int applyInternal(int tf, int primaries, boolean setLuminances,
                              int minLum, int maxLum, int referenceLum) throws Throwable {
        descReady = false;
        descFailed = false;
        descIdentity = 0;

        if (!supportsTf(tf)) {
            message = "compositor does not support named tf " + tf;
            return UNSUPPORTED;
        }
        if (!supportsPrimaries(primaries)) {
            message = "compositor does not support named primaries " + primaries;
            return UNSUPPORTED;
        }

        WpImageDescriptionCreatorParamsV1Proxy creator =
                cm.createParametricCreator(new WpImageDescriptionCreatorParamsV1Events() { });
        creator.setTfNamed(tf);
        creator.setPrimariesNamed(primaries);
        if (setLuminances) {
            creator.setLuminances(minLum, maxLum, referenceLum);
        }
        // For a linear transfer (e.g. scRGB) we may express colors outside the
        // [0, 1] range; tell the compositor we target the wider BT.2100 volume.
        if (tf == WpColorManagerV1TransferFunction.EXT_LINEAR.getValue()
                && supportsFeature(WpColorManagerV1Feature.SET_MASTERING_DISPLAY_PRIMARIES.getValue())) {
            creator.setMasteringDisplayPrimaries(
                    (int) (0.708f * COLOR_FACTOR), (int) (0.292f * COLOR_FACTOR),
                    (int) (0.170f * COLOR_FACTOR), (int) (0.797f * COLOR_FACTOR),
                    (int) (0.131f * COLOR_FACTOR), (int) (0.046f * COLOR_FACTOR),
                    (int) (0.31271f * COLOR_FACTOR), (int) (0.32902f * COLOR_FACTOR));
            creator.setMasteringLuminance(
                    (int) (transferDefaultMinNits(WpColorManagerV1TransferFunction.ST2084_PQ.getValue())
                            * MIN_LUMINANCE_FACTOR),
                    (int) transferDefaultMaxNits(WpColorManagerV1TransferFunction.ST2084_PQ.getValue()));
        }
        // create() consumed the creator on the wire (type="destructor"),
        // so it must never be sent a destroy request.
        WpImageDescriptionV1Proxy descLocal = creator.create(descEvents());
        return finish(descLocal, "tf=" + tf + " primaries=" + primaries);
    }

    /** Create and attach the compositor's built-in windows_scrgb image description. */
    public int applyWindowsScrgb() throws Throwable {
        descReady = false;
        descFailed = false;
        descIdentity = 0;
        minLuminance = 0.0f;
        maxLuminance = 0.0f;
        sdrWhiteLevel = 0.0f;

        if (!supportsFeature(WpColorManagerV1Feature.WINDOWS_SCRGB.getValue())) {
            message = "compositor lacks the windows_scrgb feature";
            return UNSUPPORTED;
        }
        WpImageDescriptionV1Proxy descLocal = cm.createWindowsScrgb(descEvents());
        return finish(descLocal, "windows_scrgb");
    }

    private WpImageDescriptionV1EventsV2 descEvents() {
        return new WpImageDescriptionV1EventsV2() {
            @Override
            public void failed(WpImageDescriptionV1Proxy e, int cause, String msg) {
                descFailed = true;
                message = "image description failed (cause=" + cause + "): " + msg;
            }

            @Override
            public void ready(WpImageDescriptionV1Proxy e, int identity) {
                descReady = true;
                descIdentity = identity & 0xffffffffL;
            }

            @Override
            public void ready2(WpImageDescriptionV1Proxy e, int hi, int lo) {
                descReady = true;
                descIdentity = ((long) hi << 32) | (lo & 0xffffffffL);
            }
        };
    }

    private int finish(WpImageDescriptionV1Proxy descLocal, String label) throws Throwable {
        if (!waitUntil(() -> descReady || descFailed, 3000)) {
            message = "timed out waiting for image description";
            return TIMEOUT;
        }
        if (descFailed) {
            return FAILED;
        }

        if (desc != null) {           // drop the previous description
            desc.destroy();
        }
        desc = descLocal;

        if (cmSurface == null) {
            cmSurface = cm.getSurface(new WpColorManagementSurfaceV1Events() { }, surface);
            cmSurface.setQueue(queue);
        }
        cmSurface.setImageDescription(desc, renderingIntent());
        display.flush();
        message = "applied " + label + " (identity=0x" + Long.toHexString(descIdentity) + ")";
        return OK;
    }

    /**
     * Create the surface feedback object and read the compositor's preferred
     * image description, mirroring {@code createNativeSurface} +
     * {@code getPreferredImageDescription}. The preferred description's
     * luminances seed {@link #minLuminance()}, {@link #maxLuminance()} and
     * {@link #sdrWhiteLevel()}.
     */
    private void setupFeedback(int timeoutMs) throws Throwable {
        feedback = cm.getSurfaceFeedback(new WpColorManagementSurfaceFeedbackV1EventsV2() {
            @Override
            public void preferredChanged(WpColorManagementSurfaceFeedbackV1Proxy emitter, int identity) {
            }

            @Override
            public void preferredChanged2(WpColorManagementSurfaceFeedbackV1Proxy emitter, int hi, int lo) {
            }
        }, surface);
        feedback.setQueue(queue);
        refreshPreferred(timeoutMs);
    }

    /**
     * Fetch the preferred image description and its information, caching the
     * reported luminances. Mirrors {@code getPreferredImageDescription}.
     */
    public void refreshPreferred(int timeoutMs) throws Throwable {
        if (feedback == null) {
            return;
        }
        WpImageDescriptionV1Proxy preferred = feedback.getPreferred(new WpImageDescriptionV1EventsV2() {
            @Override public void failed(WpImageDescriptionV1Proxy e, int cause, String msg) { }
            @Override public void ready(WpImageDescriptionV1Proxy e, int identity) { }
            @Override public void ready2(WpImageDescriptionV1Proxy e, int hi, int lo) { }
        });
        infoDone = false;
        preferred.getInformation(infoEvents());
        preferred.destroy();
        display.flush();
        // The info object destroys itself on 'done'; failure to complete is not fatal.
        waitUntil(() -> infoDone, timeoutMs);
    }

    private WpImageDescriptionInfoV1Events infoEvents() {
        return new WpImageDescriptionInfoV1Events() {
            @Override public void done(WpImageDescriptionInfoV1Proxy e) { infoDone = true; }
            @Override public void iccFile(WpImageDescriptionInfoV1Proxy e, int icc, int iccSize) { }
            @Override public void primaries(WpImageDescriptionInfoV1Proxy e,
                    int rX, int rY, int gX, int gY, int bX, int bY, int wX, int wY) { }
            @Override public void primariesNamed(WpImageDescriptionInfoV1Proxy e, int p) { }
            @Override public void tfPower(WpImageDescriptionInfoV1Proxy e, int eexp) { }
            @Override public void tfNamed(WpImageDescriptionInfoV1Proxy e, int tf) { }
            @Override public void luminances(WpImageDescriptionInfoV1Proxy e,
                    int minLum, int maxLum, int referenceLum) {
                sdrWhiteLevel = referenceLum;
                minLuminance = minLum / (float) MIN_LUMINANCE_FACTOR;
                maxLuminance = maxLum;
            }
            @Override public void targetPrimaries(WpImageDescriptionInfoV1Proxy e,
                    int rX, int rY, int gX, int gY, int bX, int bY, int wX, int wY) { }
            @Override public void targetLuminance(WpImageDescriptionInfoV1Proxy e, int minLum, int maxLum) { }
            @Override public void targetMaxCll(WpImageDescriptionInfoV1Proxy e, int maxCll) { }
            @Override public void targetMaxFall(WpImageDescriptionInfoV1Proxy e, int maxFall) { }
        };
    }

    public void unset() throws Throwable {
        if (cmSurface != null) {
            cmSurface.unsetImageDescription();
            display.flush();
        }
    }


    @Override
    public void close() {
        // Deliberately NOT calling display.disconnect()/surface.destroy():
        // SDL owns those objects and will tear them down itself.
        try {
            // Only the protocol destroy requests: wayland-java's generated
            // destroy() already handles the local proxy for destructor-type
            // requests, and calling wl_proxy_destroy ourselves double-frees
            // (observed as a SIGSEGV inside libwayland-client).
            if (feedback != null) {
                feedback.destroy();
            }
            if (cmSurface != null) {
                cmSurface.destroy();
            }
            if (desc != null) {
                desc.destroy();
            }
            if (cm != null) {
                cm.destroy();
            }
            if (registry != null) {
                registry.destroy();
            }
            display.flush();
            // The private queue is intentionally NOT destroyed: it is cheap, the
            // process is exiting, and destroying it only produces a "proxies
            // still attached" warning.
        } catch (Throwable t) {
            // best effort
        }
    }
}