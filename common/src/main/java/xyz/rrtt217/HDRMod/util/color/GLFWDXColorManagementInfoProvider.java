package xyz.rrtt217.HDRMod.util.color;

import com.sun.jna.Platform;
import org.lwjgl.glfw.GLFWNativeWin32;
import windows.win32.devices.display.DISPLAYCONFIG_DEVICE_INFO_HEADER;
import windows.win32.devices.display.DISPLAYCONFIG_DEVICE_INFO_TYPE;
import windows.win32.devices.display.DISPLAYCONFIG_GET_ADVANCED_COLOR_INFO;
import windows.win32.devices.display.DISPLAYCONFIG_MODE_INFO;
import windows.win32.devices.display.DISPLAYCONFIG_PATH_INFO;
import windows.win32.devices.display.DISPLAYCONFIG_PATH_SOURCE_INFO;
import windows.win32.devices.display.DISPLAYCONFIG_PATH_TARGET_INFO;
import windows.win32.devices.display.DISPLAYCONFIG_SDR_WHITE_LEVEL;
import windows.win32.devices.display.DISPLAYCONFIG_SOURCE_DEVICE_NAME;
import windows.win32.devices.display.QUERY_DISPLAY_CONFIG_FLAGS;
import windows.win32.graphics.dxgi.DXGI_OUTPUT_DESC1;
import windows.win32.graphics.dxgi.IDXGIOutput;
import windows.win32.graphics.dxgi.IDXGIOutput6;
import windows.win32.graphics.dxgi.IDXGISwapChain;
import windows.win32.graphics.gdi.MONITOR_FROM_FLAGS;
import windows.win32.graphics.gdi.MONITORINFO;
import windows.win32.graphics.gdi.MONITORINFOEXW;
import xyz.rrtt217.HDRMod.core.interop.InteropDXDevice;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_CHAR;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static windows.win32.devices.display.Apis.DisplayConfigGetDeviceInfo;
import static windows.win32.devices.display.Apis.GetDisplayConfigBufferSizes;
import static windows.win32.devices.display.Apis.QueryDisplayConfig;
import static windows.win32.graphics.gdi.Apis.GetMonitorInfoW;
import static windows.win32.graphics.gdi.Apis.MonitorFromWindow;
import static windows.win32.graphics.gdi.Constants.DISPLAYCONFIG_PATH_ACTIVE;

public class GLFWDXColorManagementInfoProvider extends ColorManagementInfoProvider {
    InteropDXDevice dxDevice;
    public GLFWDXColorManagementInfoProvider(InteropDXDevice dxDevice){
        this.dxDevice = dxDevice;
    }

    public void setDxDevice(InteropDXDevice dxDevice){
        this.dxDevice = dxDevice;
    }

    @Override
    public float getWindowSdrWhiteLevel(long handle) {
        if(!Platform.isWindows()) return super.getWindowSdrWhiteLevel(handle);

        try (var arena = Arena.ofConfined()) {
            long hwnd = GLFWNativeWin32.glfwGetWin32Window(handle);
            MemorySegment hMonitor = MonitorFromWindow(MemorySegment.ofAddress(hwnd), MONITOR_FROM_FLAGS.MONITOR_DEFAULTTONEAREST);

            String monitorName = null;
            MemorySegment monitorInfo = MONITORINFOEXW.allocate(arena);
            MONITORINFO.cbSize(monitorInfo, (int) MONITORINFOEXW.sizeof());
            if (GetMonitorInfoW(hMonitor, monitorInfo) != 0) {
                monitorName = readUtf16(MONITORINFOEXW.szDevice(monitorInfo));
            }

            MemorySegment numPaths = arena.allocate(JAVA_INT);
            MemorySegment numModes = arena.allocate(JAVA_INT);
            int result = GetDisplayConfigBufferSizes(QUERY_DISPLAY_CONFIG_FLAGS.QDC_ONLY_ACTIVE_PATHS, numPaths, numModes);
            if (result != 0) return 80.0f;

            int pathCount = numPaths.get(JAVA_INT, 0);
            int modeCount = numModes.get(JAVA_INT, 0);
            if (pathCount <= 0 || modeCount <= 0) return 80.0f;

            MemorySegment paths = DISPLAYCONFIG_PATH_INFO.allocateArray(pathCount, arena);
            MemorySegment modes = DISPLAYCONFIG_MODE_INFO.allocateArray(modeCount, arena);
            result = QueryDisplayConfig(QUERY_DISPLAY_CONFIG_FLAGS.QDC_ONLY_ACTIVE_PATHS, numPaths, paths, numModes, modes, MemorySegment.NULL);
            if (result != 0) return 80.0f;

            for (int i = 0; i < pathCount; i++) {
                MemorySegment path = DISPLAYCONFIG_PATH_INFO.elementAsSlice(paths, i);
                if ((DISPLAYCONFIG_PATH_INFO.flags(path) & DISPLAYCONFIG_PATH_ACTIVE) == 0) continue;

                MemorySegment sourceName = DISPLAYCONFIG_SOURCE_DEVICE_NAME.allocate(arena);
                MemorySegment sourceNameHeader = DISPLAYCONFIG_SOURCE_DEVICE_NAME.header(sourceName);
                DISPLAYCONFIG_DEVICE_INFO_HEADER.type(sourceNameHeader, DISPLAYCONFIG_DEVICE_INFO_TYPE.DISPLAYCONFIG_DEVICE_INFO_GET_SOURCE_NAME);
                DISPLAYCONFIG_DEVICE_INFO_HEADER.size(sourceNameHeader, (int) DISPLAYCONFIG_SOURCE_DEVICE_NAME.sizeof());
                MemorySegment sourceInfo = DISPLAYCONFIG_PATH_INFO.sourceInfo(path);
                DISPLAYCONFIG_DEVICE_INFO_HEADER.adapterId(sourceNameHeader, DISPLAYCONFIG_PATH_SOURCE_INFO.adapterId(sourceInfo));
                DISPLAYCONFIG_DEVICE_INFO_HEADER.id(sourceNameHeader, DISPLAYCONFIG_PATH_SOURCE_INFO.id(sourceInfo));

                result = DisplayConfigGetDeviceInfo(sourceName);
                if (result != 0) continue;

                String sourceDeviceName = readUtf16(DISPLAYCONFIG_SOURCE_DEVICE_NAME.viewGdiDeviceName(sourceName));
                if (monitorName != null && !sourceDeviceName.equals(monitorName)) continue;

                MemorySegment advancedColor = DISPLAYCONFIG_GET_ADVANCED_COLOR_INFO.allocate(arena);
                MemorySegment advancedColorHeader = DISPLAYCONFIG_GET_ADVANCED_COLOR_INFO.header(advancedColor);
                DISPLAYCONFIG_DEVICE_INFO_HEADER.type(advancedColorHeader, DISPLAYCONFIG_DEVICE_INFO_TYPE.DISPLAYCONFIG_DEVICE_INFO_GET_ADVANCED_COLOR_INFO);
                DISPLAYCONFIG_DEVICE_INFO_HEADER.size(advancedColorHeader, (int) DISPLAYCONFIG_GET_ADVANCED_COLOR_INFO.sizeof());
                MemorySegment targetInfo = DISPLAYCONFIG_PATH_INFO.targetInfo(path);
                MemorySegment targetAdapterId = DISPLAYCONFIG_PATH_TARGET_INFO.adapterId(targetInfo);
                DISPLAYCONFIG_DEVICE_INFO_HEADER.adapterId(advancedColorHeader, targetAdapterId);
                DISPLAYCONFIG_DEVICE_INFO_HEADER.id(advancedColorHeader, DISPLAYCONFIG_PATH_TARGET_INFO.id(targetInfo));

                result = DisplayConfigGetDeviceInfo(advancedColor);
                if (result != 0) continue;

                // Bit 0 of the bitfield is advancedColorEnabled.
                if ((DISPLAYCONFIG_GET_ADVANCED_COLOR_INFO.value(advancedColor) & 0x1) == 0) return 80.0f;

                MemorySegment whiteLevel = DISPLAYCONFIG_SDR_WHITE_LEVEL.allocate(arena);
                MemorySegment whiteLevelHeader = DISPLAYCONFIG_SDR_WHITE_LEVEL.header(whiteLevel);
                DISPLAYCONFIG_DEVICE_INFO_HEADER.type(whiteLevelHeader, DISPLAYCONFIG_DEVICE_INFO_TYPE.DISPLAYCONFIG_DEVICE_INFO_GET_SDR_WHITE_LEVEL);
                DISPLAYCONFIG_DEVICE_INFO_HEADER.size(whiteLevelHeader, (int) DISPLAYCONFIG_SDR_WHITE_LEVEL.sizeof());
                DISPLAYCONFIG_DEVICE_INFO_HEADER.adapterId(whiteLevelHeader, targetAdapterId);
                DISPLAYCONFIG_DEVICE_INFO_HEADER.id(whiteLevelHeader, DISPLAYCONFIG_PATH_TARGET_INFO.id(targetInfo));

                if (DisplayConfigGetDeviceInfo(whiteLevel) != 0) continue;

                return DISPLAYCONFIG_SDR_WHITE_LEVEL.SDRWhiteLevel(whiteLevel) / 1000.0f * 80.0f;
            }

            return 80.0f;
        }
    }

    @Override
    public float getWindowMinLuminance(long handle) {
        if(!Platform.isWindows() || dxDevice == null) return super.getWindowMinLuminance(handle);
        return queryOutputDesc1(DXGI_OUTPUT_DESC1::MinLuminance);
    }

    @Override
    public float getWindowMaxLuminance(long handle) {
        if(!Platform.isWindows() || dxDevice == null) return super.getWindowMaxLuminance(handle);
        return queryOutputDesc1(DXGI_OUTPUT_DESC1::MaxLuminance);
    }

    private float queryOutputDesc1(ToFloatFunction<MemorySegment> field) {
        try (var arena = Arena.ofConfined()) {
            IDXGISwapChain swapChain = dxDevice.getSwapChain();
            MemorySegment outputPtr = arena.allocate(ADDRESS);
            if (swapChain.GetContainingOutput(outputPtr) != 0) return 0.0f;

            IDXGIOutput output = IDXGIOutput.wrap(outputPtr.get(ADDRESS, 0));
            try {
                IDXGIOutput6 output6 = dxDevice.comCast(arena, output, IDXGIOutput6.class);
                MemorySegment desc = DXGI_OUTPUT_DESC1.allocate(arena);
                if (output6.GetDesc1(desc) != 0) return 0.0f;
                return field.applyAsFloat(desc);
            } finally {
                output.Release();
            }
        }
    }

    @FunctionalInterface
    public interface ToFloatFunction<T> {
        float applyAsFloat(T value);
    }

    private static String readUtf16(MemorySegment segment) {
        StringBuilder sb = new StringBuilder();
        for (long i = 0; i * 2 < segment.byteSize(); i++) {
            char c = segment.get(JAVA_CHAR, i * 2);
            if (c == 0) break;
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public Enums.Primaries getWindowPrimaries(long handle) {
        if(Platform.isWindows() && config.useUNORMWindowPixelFormat) return Enums.Primaries.BT2020;
        return Enums.Primaries.SRGB;
    }

    @Override
    public Enums.TransferFunction getWindowTransferFunction(long handle) {
        if(Platform.isWindows() && config.useUNORMWindowPixelFormat) return Enums.TransferFunction.ST2084_PQ;
        if(Platform.isWindows()) return Enums.TransferFunction.EXT_LINEAR;
        else return Enums.TransferFunction.EXT_SRGB;
    }
}