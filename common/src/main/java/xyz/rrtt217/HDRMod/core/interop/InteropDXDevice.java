package xyz.rrtt217.HDRMod.core.interop;

// With a lot of reference from https://github.com/sidit77/perfect_presentation.
/*
    The MIT License (MIT)

    Copyright (c) 2025 sidit77

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
            in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

            THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/

import com.mojang.blaze3d.opengl.GlStateManager;
import org.jetbrains.annotations.Nullable;
import windows.win32.foundation.WAIT_EVENT;
import windows.win32.graphics.direct3d.D3D_DRIVER_TYPE;
import windows.win32.graphics.direct3d.D3D_PRIMITIVE_TOPOLOGY;
import windows.win32.graphics.direct3d.ID3DBlob;
import windows.win32.graphics.direct3d11.*;
import windows.win32.graphics.dxgi.*;
import windows.win32.graphics.dxgi.common.DXGI_ALPHA_MODE;
import windows.win32.graphics.dxgi.common.DXGI_COLOR_SPACE_TYPE;
import windows.win32.graphics.dxgi.common.DXGI_FORMAT;
import windows.win32.graphics.dxgi.common.DXGI_SAMPLE_DESC;
import windows.win32.system.com.IUnknown;

import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import static java.lang.foreign.MemorySegment.NULL;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12.*;
import static windows.win32.foundation.Apis.CloseHandle;
import static windows.win32.graphics.direct3d.fxc.Apis.D3DCompile;
import static windows.win32.graphics.direct3d11.Apis.D3D11CreateDevice;
import static windows.win32.graphics.direct3d11.Constants.D3D11_SDK_VERSION;
import static windows.win32.graphics.direct3d11.D3D11_CREATE_DEVICE_FLAG.D3D11_CREATE_DEVICE_BGRA_SUPPORT;
import static windows.win32.graphics.dxgi.Apis.CreateDXGIFactory1;
import static windows.win32.system.threading.Apis.WaitForSingleObject;
import static xyz.rrtt217.HDRMod.HDRMod.LOGGER;
import static xyz.rrtt217.HDRMod.core.interop.WinError.checkSuccessful;

public class InteropDXDevice implements AutoCloseable {

    private static final ThreadLocal<InteropDXDevice> CURRENT_CONTEXT = new ThreadLocal<>();
    public static @Nullable InteropDXDevice getCurrentContext() {
        return CURRENT_CONTEXT.get();
    }

    private static final int SWAP_CHAIN_FLAGS = DXGI_SWAP_CHAIN_FLAG.ALLOW_TEARING | DXGI_SWAP_CHAIN_FLAG.FRAME_LATENCY_WAITABLE_OBJECT;

    final ID3D11Device device;
    private final ID3D11DeviceContext context;
    private final IDXGISwapChain2 swapChain;
    private final WaitHandle waitHandle;
    private @Nullable ID3D11RenderTargetView renderTargetView = null;

    private int syncInterval = 1;

    private int swapchainFormat;

    public InteropDXDevice(long hwnd, int dxgiFormat) {
        try (var arena = Arena.ofConfined()) {
            {
                var devicePtr = arena.allocate(ADDRESS);
                var contextPtr = arena.allocate(ADDRESS);
                var hr = D3D11CreateDevice(
                        NULL,
                        D3D_DRIVER_TYPE.HARDWARE,
                        NULL,
                        D3D11_CREATE_DEVICE_BGRA_SUPPORT,
                        NULL,
                        0,
                        D3D11_SDK_VERSION,
                        devicePtr,
                        NULL,
                        contextPtr
                );
                checkSuccessful(hr);

                this.device = ID3D11Device.wrap(devicePtr.get(ADDRESS, 0));
                this.context = ID3D11DeviceContext.wrap(contextPtr.get(ADDRESS, 0));
            }

            var factory = makeResource(arena, ptr -> CreateDXGIFactory1(IDXGIFactory2.iid(), ptr), IDXGIFactory2::wrap);

            var swapChainDesc = DXGI_SWAP_CHAIN_DESC1.allocate(arena);
            DXGI_SWAP_CHAIN_DESC1.Format(swapChainDesc, dxgiFormat);
            DXGI_SAMPLE_DESC.Count(DXGI_SWAP_CHAIN_DESC1.SampleDesc(swapChainDesc), 1);
            DXGI_SAMPLE_DESC.Quality(DXGI_SWAP_CHAIN_DESC1.SampleDesc(swapChainDesc), 0);
            DXGI_SWAP_CHAIN_DESC1.BufferUsage(swapChainDesc, DXGI_USAGE.RENDER_TARGET_OUTPUT);
            DXGI_SWAP_CHAIN_DESC1.BufferCount(swapChainDesc, 2);
            DXGI_SWAP_CHAIN_DESC1.Scaling(swapChainDesc, DXGI_SCALING.NONE);
            DXGI_SWAP_CHAIN_DESC1.SwapEffect(swapChainDesc, DXGI_SWAP_EFFECT.FLIP_DISCARD);
            DXGI_SWAP_CHAIN_DESC1.AlphaMode(swapChainDesc, DXGI_ALPHA_MODE.UNSPECIFIED);
            DXGI_SWAP_CHAIN_DESC1.Flags(swapChainDesc, SWAP_CHAIN_FLAGS);
            this.swapchainFormat = dxgiFormat;

            var swapChain1 = makeResource(arena,
                    ptr -> factory.CreateSwapChainForHwnd(
                            asRaw(device),
                            MemorySegment.ofAddress(hwnd),
                            swapChainDesc,
                            NULL,
                            NULL,
                            ptr),
                    IDXGISwapChain1::wrap);

            swapChain = comCast(arena, swapChain1, IDXGISwapChain2.class);

            checkSuccessful(factory.MakeWindowAssociation(
                    MemorySegment.ofAddress(hwnd),
                    DXGI_MWA_FLAGS.DXGI_MWA_NO_ALT_ENTER | DXGI_MWA_FLAGS.DXGI_MWA_NO_WINDOW_CHANGES));

            checkSuccessful(swapChain.SetMaximumFrameLatency(1));
            waitHandle = new WaitHandle(swapChain.GetFrameLatencyWaitableObject());

            swapChain1.Release();
            factory.Release();

            var shaderSource = arena.allocateFrom(BLIT_SHADER_SOURCE, UTF_8);

            var vertexShaderBlob = compileShaderSource(arena, shaderSource, "VsMain", "vs_5_0");
            var vertexShader = makeResource(arena, ptr -> device.CreateVertexShader(vertexShaderBlob.GetBufferPointer(), vertexShaderBlob.GetBufferSize(), NULL, ptr), ID3D11VertexShader::wrap);
            vertexShaderBlob.Release();

            var pixelShaderBlob = compileShaderSource(arena, shaderSource, "PsMain", "ps_5_0");
            var pixelShader = makeResource(arena, ptr -> device.CreatePixelShader(pixelShaderBlob.GetBufferPointer(), pixelShaderBlob.GetBufferSize(), NULL, ptr), ID3D11PixelShader::wrap);
            pixelShaderBlob.Release();

            var rasterizerStateDesc = D3D11_RASTERIZER_DESC.allocate(arena);
            D3D11_RASTERIZER_DESC.FillMode(rasterizerStateDesc, D3D11_FILL_MODE.D3D11_FILL_SOLID);
            D3D11_RASTERIZER_DESC.CullMode(rasterizerStateDesc, D3D11_CULL_MODE.D3D11_CULL_NONE);
            var rasterizerState = makeResource(arena, ptr -> device.CreateRasterizerState(rasterizerStateDesc, ptr), ID3D11RasterizerState::wrap);

            var samplerStateDesc = D3D11_SAMPLER_DESC.allocate(arena);
            D3D11_SAMPLER_DESC.Filter(samplerStateDesc, D3D11_FILTER.MIN_MAG_MIP_POINT);
            D3D11_SAMPLER_DESC.AddressU(samplerStateDesc, D3D11_TEXTURE_ADDRESS_MODE.D3D11_TEXTURE_ADDRESS_CLAMP);
            D3D11_SAMPLER_DESC.AddressV(samplerStateDesc, D3D11_TEXTURE_ADDRESS_MODE.D3D11_TEXTURE_ADDRESS_CLAMP);
            D3D11_SAMPLER_DESC.AddressW(samplerStateDesc, D3D11_TEXTURE_ADDRESS_MODE.D3D11_TEXTURE_ADDRESS_CLAMP);
            var samplerState = makeResource(arena, ptr -> device.CreateSamplerState(samplerStateDesc, ptr), ID3D11SamplerState::wrap);

            context.IASetPrimitiveTopology(D3D_PRIMITIVE_TOPOLOGY.D3D11_PRIMITIVE_TOPOLOGY_TRIANGLELIST);
            context.VSSetShader(asRaw(vertexShader), NULL, 0);
            context.RSSetState(asRaw(rasterizerState));
            context.PSSetShader(asRaw(pixelShader), NULL, 0);
            context.PSSetSamplers(0, 1, arena.allocateFrom(ADDRESS, asRaw(samplerState)));

            vertexShader.Release();
            pixelShader.Release();
            rasterizerState.Release();
            samplerState.Release();
        }

        configureSwapchainColorSpace();
    }

    static MemorySegment asRaw(IUnknown obj) {
        if(obj instanceof IUnknown.$DOWNCALL downcall) {
            return downcall.comObject;
        }
        throw new IllegalArgumentException("Not a native IUnknown object");
    }

    private static ID3DBlob compileShaderSource(Arena arena, MemorySegment source, String entryPoint, String target) {
        var vertexShaderBlobPtr = arena.allocate(ADDRESS);
        var vertexShaderErrorBlobPtr = arena.allocateFrom(ADDRESS, NULL);
        var hr = D3DCompile(
                source,
                source.byteSize(),
                NULL, NULL, NULL,
                arena.allocateFrom(entryPoint, UTF_8),
                arena.allocateFrom(target, UTF_8),
                0, 0,
                vertexShaderBlobPtr, vertexShaderErrorBlobPtr);
        if(hr != 0) {
            var buffer = ID3DBlob.wrap(vertexShaderErrorBlobPtr.get(ADDRESS, 0));
            var content = buffer.GetBufferPointer().reinterpret(buffer.GetBufferSize()).toArray(ValueLayout.JAVA_BYTE);
            buffer.Release();
            throw new RuntimeException("Failed to compile shader: " + new String(content, UTF_8));
        }
        checkSuccessful(hr);
        return ID3DBlob.wrap(vertexShaderBlobPtr.get(ADDRESS, 0));
    }

    private static <T extends IUnknown> T makeResource(Arena arena, Function<MemorySegment, Integer> factory, Function<MemorySegment, T> wrapper) {
        var ptr = arena.allocate(ADDRESS);
        var hr = factory.apply(ptr);
        checkSuccessful(hr);
        return wrapper.apply(ptr.get(ADDRESS, 0));
    }

    public <T extends IUnknown> T comCast(Arena arena, IUnknown resource, Class<T> clazz){
        try {
            Method iidMethod = clazz.getMethod("iid");
            MemorySegment iid = (MemorySegment) iidMethod.invoke(null);

            Method wrapMethod = clazz.getMethod("wrap", MemorySegment.class);

            var ptr = arena.allocate(ADDRESS);
            checkSuccessful(resource.QueryInterface(iid, ptr));
            return clazz.cast(wrapMethod.invoke(null, ptr.get(ADDRESS, 0)));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void makeCurrent() {
        CURRENT_CONTEXT.set(this);
    }

    public IDXGISwapChain2 getSwapChain() {
        return swapChain;
    }

    public void setSyncInterval(int syncInterval) {
        if(syncInterval < 0) {
            LOGGER.warn("Sync interval cannot be negative, clamping to 0");
            syncInterval = 0;
        }
        this.syncInterval = syncInterval;
    }

    public void waitForSwapChainSignal() {
        waitHandle.waitForSignal();
    }

    public void swapChainPresent() {
        checkSuccessful(swapChain.Present(syncInterval, syncInterval == 0 ? DXGI_PRESENT.ALLOW_TEARING : 0));
    }

    public void resizeSwapChain(int width, int height) {
        if(renderTargetView != null) {
            renderTargetView.Release();
            renderTargetView = null;
        }
        checkSuccessful(swapChain.ResizeBuffers(0, width, height, DXGI_FORMAT.UNKNOWN, SWAP_CHAIN_FLAGS));
        configureSwapchainColorSpace();
    }

    private void configureSwapchainColorSpace() {
        int colorSpace;
        if (swapchainFormat == DXGI_FORMAT.R16G16B16A16_FLOAT) {
            colorSpace = DXGI_COLOR_SPACE_TYPE.DXGI_COLOR_SPACE_RGB_FULL_G10_NONE_P709;
        } else if (swapchainFormat == DXGI_FORMAT.R10G10B10A2_UNORM) {
            colorSpace = DXGI_COLOR_SPACE_TYPE.DXGI_COLOR_SPACE_RGB_FULL_G2084_NONE_P2020;
        } else {
            colorSpace = DXGI_COLOR_SPACE_TYPE.DXGI_COLOR_SPACE_RGB_FULL_G22_NONE_P709;
        }

        try (var arena = Arena.ofConfined()) {
            var swapchain3 = comCast(arena, swapChain, IDXGISwapChain3.class);
            try {
                var supportPtr = arena.allocate(ValueLayout.JAVA_INT);
                var hr = swapchain3.CheckColorSpaceSupport(colorSpace, supportPtr);
                if (hr >= 0 && (supportPtr.get(ValueLayout.JAVA_INT, 0) & DXGI_SWAP_CHAIN_COLOR_SPACE_SUPPORT_FLAG.PRESENT) != 0) {
                    checkSuccessful(swapchain3.SetColorSpace1(colorSpace));
                }
            } finally {
                swapchain3.Release();
            }
        }
    }

    public void blitSharedTextureToSwapChain(SharedTexture texture) {
        try (var arena = Arena.ofConfined()) {
            texture.lock();
            if(renderTargetView == null) {
                var backBuffer = makeResource(arena, ptr -> swapChain.GetBuffer(0, ID3D11Texture2D.iid(), ptr), ID3D11Texture2D::wrap);

                renderTargetView = makeResource(arena, ptr -> device.CreateRenderTargetView(asRaw(backBuffer), NULL, ptr), ID3D11RenderTargetView::wrap);

                var backBufferDesc = D3D11_TEXTURE2D_DESC.allocate(arena);
                backBuffer.GetDesc(backBufferDesc);

                var viewPort = D3D11_VIEWPORT.allocate(arena);
                D3D11_VIEWPORT.Width(viewPort, D3D11_TEXTURE2D_DESC.Width(backBufferDesc));
                D3D11_VIEWPORT.Height(viewPort, D3D11_TEXTURE2D_DESC.Height(backBufferDesc));
                D3D11_VIEWPORT.MaxDepth(viewPort, 1.0f);

                context.RSSetViewports(1, viewPort);

                backBuffer.Release();
            }

            context.PSSetShaderResources(0, 1, arena.allocateFrom(ADDRESS, MemorySegment.ofAddress(texture.getDxTextureViewHandle())));
            context.OMSetRenderTargets(1, arena.allocateFrom(ADDRESS, asRaw(renderTargetView)), NULL);
            context.Draw(3, 0);

        } finally {
            texture.unlock();
        }
    }

    public SharedTexture createSharedTexture(@Nullable String debugName, int dxUsage, int width, int height, long interopDeviceHandle) {
        try (var arena = Arena.ofConfined()) {
            var textureDesc = D3D11_TEXTURE2D_DESC.allocate(arena);
            D3D11_TEXTURE2D_DESC.Width(textureDesc, width);
            D3D11_TEXTURE2D_DESC.Height(textureDesc, height);
            D3D11_TEXTURE2D_DESC.MipLevels(textureDesc, 1);
            D3D11_TEXTURE2D_DESC.ArraySize(textureDesc, 1);
            D3D11_TEXTURE2D_DESC.Format(textureDesc, this.swapchainFormat);
            DXGI_SAMPLE_DESC.Count(D3D11_TEXTURE2D_DESC.SampleDesc(textureDesc), 1);
            DXGI_SAMPLE_DESC.Quality(D3D11_TEXTURE2D_DESC.SampleDesc(textureDesc), 0);
            D3D11_TEXTURE2D_DESC.Usage(textureDesc, D3D11_USAGE.DEFAULT);
            D3D11_TEXTURE2D_DESC.BindFlags(textureDesc, dxUsage);
            D3D11_TEXTURE2D_DESC.CPUAccessFlags(textureDesc, 0);
            D3D11_TEXTURE2D_DESC.MiscFlags(textureDesc, 0);

            var texture = makeResource(arena, ptr -> device.CreateTexture2D(textureDesc, NULL, ptr), ID3D11Texture2D::wrap);
            var textureView = makeResource(arena, ptr -> device.CreateShaderResourceView(asRaw(texture), NULL, ptr), ID3D11ShaderResourceView::wrap);

            GlStateManager.clearGlErrors();
            int texId = GlStateManager._genTexture();
            if (debugName == null) {
                debugName = String.valueOf(texId);
            }

            GlStateManager._bindTexture(texId);
            GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
            GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_LOD, 0);
            GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAX_LOD, 0);

            int m = GlStateManager._getError();
            if (m != 0)
                throw new IllegalStateException("OpenGL error " + m);

            texture.Release();

            return new SharedTexture(interopDeviceHandle, texId, asRaw(texture).address(), asRaw(textureView).address());
        }
    }

    @Override
    public void close() {
        context.ClearState();

        if (renderTargetView != null) {
            renderTargetView.Release();
            renderTargetView = null;
        }

        waitHandle.close();
        swapChain.Release();


        context.Release();
        device.Release();

    }

    private record WaitHandle(MemorySegment handle) implements AutoCloseable {

        public void waitForSignal() {
            try(var arena = Arena.ofConfined()) {
                var errorState = arena.allocate(Linker.Option.captureStateLayout());
                var result = WaitForSingleObject(errorState, handle, 1000);

                switch (result) {
                    case WAIT_EVENT.WAIT_OBJECT_0 -> {}
                    case WAIT_EVENT.WAIT_ABANDONED -> LOGGER.warn("Swap chain wait abandoned unexpectedly");
                    case WAIT_EVENT.WAIT_TIMEOUT -> LOGGER.warn("Swap chain wait timed out");
                    case WAIT_EVENT.WAIT_FAILED -> checkSuccessful(errorState);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to wait for swap chain signal", e);
            }
        }

        @Override
        public void close() {
            try(var arena = Arena.ofConfined()) {
                var errorState = arena.allocate(Linker.Option.captureStateLayout());
                var result = CloseHandle(errorState, handle);
                if(result == 0) {
                    checkSuccessful(errorState);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to close wait handle", e);
            }

        }
    }

    private static final String BLIT_SHADER_SOURCE = """
            struct VSOut
            {
                float4 pos : SV_Position;
                float2 uv  : TEXCOORD0;
            };
            
            VSOut VsMain(uint id : SV_VertexID)
            {
                VSOut o;
                o.pos = float4(id >> 1, id & 1, 0, 0.5) * 4 - 1;
                o.uv  = float2(id >> 1, id & 1) * 2;
            
                return o;
            }
            
            Texture2D srcTex : register(t0);
            SamplerState samp : register(s0);
            
            float4 PsMain(VSOut i) : SV_Target
            {
                return srcTex.Sample(samp, i.uv);
            }
            """;

}
