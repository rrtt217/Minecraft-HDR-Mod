#define COBJMACROS
#include <initguid.h>
#include <d3d11.h>
#include <d3dcompiler.h>
#include <dxgi.h>
#include <dxgi1_4.h>
#include <dxgi1_6.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "dx11_shim.h"

static char g_LastError[512] = "";

#define SET_LAST_ERROR(fmt, ...) \
    snprintf(g_LastError, sizeof(g_LastError), fmt, ##__VA_ARGS__)

#ifndef DXGI_SWAP_CHAIN_FLAG_ALLOW_TEARING
#define DXGI_SWAP_CHAIN_FLAG_ALLOW_TEARING 2048
#endif

#ifndef DXGI_PRESENT_ALLOW_TEARING
#define DXGI_PRESENT_ALLOW_TEARING 0x00000200
#endif

static const char *g_FlipYHLSL =
    "struct VSOut {\n"
    "    float4 position : SV_POSITION;\n"
    "    float2 uv       : TEXCOORD0;\n"
    "};\n"
    "\n"
    "VSOut mainVS(uint vertexID : SV_VertexID) {\n"
    "    VSOut o;\n"
    "    float2 uv = float2((vertexID << 1) & 2, vertexID & 2);\n"
    "    o.position = float4(uv * float2(2.0, -2.0) + float2(-1.0, 1.0), 0.0, 1.0);\n"
    "    o.uv = float2(uv.x, 1.0 - uv.y);\n"
    "    return o;\n"
    "}\n"
    "\n"
    "Texture2D sourceTexture : register(t0);\n"
    "SamplerState pointSampler {\n"
    "    Filter = MIN_MAG_MIP_POINT;\n"
    "    AddressU = Clamp;\n"
    "    AddressV = Clamp;\n"
    "    AddressW = Clamp;\n"
    "};\n"
    "\n"
    "float4 mainPS(VSOut input) : SV_Target {\n"
    "    return sourceTexture.SampleLevel(pointSampler, input.uv, 0.0);\n"
    "}\n";

static void assignColorStateFromFormat(DX11ShimContext *ctx, DXGI_FORMAT format) {
    ctx->swapchainFormat = (uint32_t)format;

    if (format == DXGI_FORMAT_R16G16B16A16_FLOAT) {
        ctx->colorPrimaries = 1;
        ctx->colorTransfer = 5;
    } else if (format == DXGI_FORMAT_R10G10B10A2_UNORM) {
        ctx->colorPrimaries = 6;
        ctx->colorTransfer = 11;
    } else {
        ctx->colorPrimaries = 1;
        ctx->colorTransfer = 10;
    }
}

static void configureSwapchainColorSpace(DX11ShimContext *ctx) {
    IDXGISwapChain3 *swapchain3 = NULL;
    DXGI_COLOR_SPACE_TYPE requested;
    UINT support = 0;
    HRESULT hr;

    if (!ctx->swapchain)
        return;

    if (ctx->colorPrimaries == 1 && ctx->colorTransfer == 5)
        requested = DXGI_COLOR_SPACE_RGB_FULL_G10_NONE_P709;
    else if (ctx->colorPrimaries == 6 && ctx->colorTransfer == 11)
        requested = DXGI_COLOR_SPACE_RGB_FULL_G2084_NONE_P2020;
    else
        requested = DXGI_COLOR_SPACE_RGB_FULL_G22_NONE_P709;

    hr = IDXGISwapChain_QueryInterface(ctx->swapchain, &IID_IDXGISwapChain3,
                                       (void **)&swapchain3);
    if (FAILED(hr) || !swapchain3)
        return;

    hr = IDXGISwapChain3_CheckColorSpaceSupport(swapchain3, requested, &support);
    if (SUCCEEDED(hr) &&
        (support & DXGI_SWAP_CHAIN_COLOR_SPACE_SUPPORT_FLAG_PRESENT)) {
        IDXGISwapChain3_SetColorSpace1(swapchain3, requested);
    }

    IDXGISwapChain3_Release(swapchain3);
}

static void releaseFlipViews(DX11ShimContext *ctx) {
    if (ctx->flipSRV) {
        ID3D11ShaderResourceView_Release(ctx->flipSRV);
        ctx->flipSRV = NULL;
    }
    if (ctx->flipRTV) {
        ID3D11RenderTargetView_Release(ctx->flipRTV);
        ctx->flipRTV = NULL;
    }
}

static void releaseFlipPipeline(DX11ShimContext *ctx) {
    if (ctx->flipPixelShader) {
        ID3D11PixelShader_Release(ctx->flipPixelShader);
        ctx->flipPixelShader = NULL;
    }
    if (ctx->flipVertexShader) {
        ID3D11VertexShader_Release(ctx->flipVertexShader);
        ctx->flipVertexShader = NULL;
    }
}

static void releaseD3DObjects(DX11ShimContext *ctx) {
    releaseFlipViews(ctx);

    if (ctx->interopTexture) {
        ID3D11Texture2D_Release(ctx->interopTexture);
        ctx->interopTexture = NULL;
    }
    ctx->interopSharedHandle = NULL;

    if (ctx->backBuffer) {
        ID3D11Texture2D_Release(ctx->backBuffer);
        ctx->backBuffer = NULL;
    }
}

static int createFlipPipeline(DX11ShimContext *ctx) {
    ID3D10Blob *vsBlob = NULL, *psBlob = NULL, *errBlob = NULL;
    HRESULT hr;

    hr = D3DCompile(g_FlipYHLSL, strlen(g_FlipYHLSL), NULL, NULL, NULL,
                    "mainVS", "vs_5_0", 0, 0, &vsBlob, &errBlob);
    if (FAILED(hr)) {
        if (errBlob) {
            SET_LAST_ERROR("VS compile failed: %s",
                           (const char *)ID3D10Blob_GetBufferPointer(errBlob));
            ID3D10Blob_Release(errBlob);
        } else {
            SET_LAST_ERROR("VS compile failed: HRESULT 0x%08lX",
                           (unsigned long)hr);
        }
        return 0;
    }

    hr = D3DCompile(g_FlipYHLSL, strlen(g_FlipYHLSL), NULL, NULL, NULL,
                    "mainPS", "ps_5_0", 0, 0, &psBlob, &errBlob);
    if (FAILED(hr)) {
        if (errBlob) {
            SET_LAST_ERROR("PS compile failed: %s",
                           (const char *)ID3D10Blob_GetBufferPointer(errBlob));
            ID3D10Blob_Release(errBlob);
        } else {
            SET_LAST_ERROR("PS compile failed: HRESULT 0x%08lX",
                           (unsigned long)hr);
        }
        ID3D10Blob_Release(vsBlob);
        return 0;
    }

    hr = ID3D11Device_CreateVertexShader(ctx->device,
            ID3D10Blob_GetBufferPointer(vsBlob),
            ID3D10Blob_GetBufferSize(vsBlob),
            NULL, &ctx->flipVertexShader);
    if (FAILED(hr)) {
        SET_LAST_ERROR("CreateVertexShader failed: HRESULT 0x%08lX",
                       (unsigned long)hr);
        ID3D10Blob_Release(psBlob);
        ID3D10Blob_Release(vsBlob);
        return 0;
    }

    hr = ID3D11Device_CreatePixelShader(ctx->device,
            ID3D10Blob_GetBufferPointer(psBlob),
            ID3D10Blob_GetBufferSize(psBlob),
            NULL, &ctx->flipPixelShader);
    if (FAILED(hr)) {
        SET_LAST_ERROR("CreatePixelShader failed: HRESULT 0x%08lX",
                       (unsigned long)hr);
        ID3D11VertexShader_Release(ctx->flipVertexShader);
        ctx->flipVertexShader = NULL;
        ID3D10Blob_Release(psBlob);
        ID3D10Blob_Release(vsBlob);
        return 0;
    }

    ID3D10Blob_Release(psBlob);
    ID3D10Blob_Release(vsBlob);
    return 1;
}

static int createFlipViews(DX11ShimContext *ctx) {
    HRESULT hr;

    releaseFlipViews(ctx);

    if (!ctx->device || !ctx->interopTexture || !ctx->backBuffer)
        return 0;

    hr = ID3D11Device_CreateShaderResourceView(
        ctx->device, (ID3D11Resource *)ctx->interopTexture, NULL, &ctx->flipSRV);
    if (FAILED(hr))
        return 0;

    hr = ID3D11Device_CreateRenderTargetView(
        ctx->device, (ID3D11Resource *)ctx->backBuffer, NULL, &ctx->flipRTV);
    if (FAILED(hr)) {
        ID3D11ShaderResourceView_Release(ctx->flipSRV);
        ctx->flipSRV = NULL;
        return 0;
    }

    return 1;
}

static int cacheBackBuffer(DX11ShimContext *ctx) {
    HRESULT hr;

    if (!ctx->swapchain)
        return 0;

    hr = IDXGISwapChain_GetBuffer(ctx->swapchain, 0, &IID_ID3D11Texture2D,
                                  (void **)&ctx->backBuffer);
    return SUCCEEDED(hr);
}

static int createInteropSurface(DX11ShimContext *ctx, int width, int height) {
    D3D11_TEXTURE2D_DESC desc;
    IDXGIResource *sharedResource = NULL;
    HRESULT hr;

    releaseD3DObjects(ctx);

    if (!cacheBackBuffer(ctx))
        return 0;

    ZeroMemory(&desc, sizeof(desc));
    desc.Width = (UINT)width;
    desc.Height = (UINT)height;
    desc.MipLevels = 1;
    desc.ArraySize = 1;
    desc.Format = (DXGI_FORMAT)ctx->swapchainFormat;
    desc.SampleDesc.Count = 1;
    desc.Usage = D3D11_USAGE_DEFAULT;
    desc.BindFlags = D3D11_BIND_RENDER_TARGET | D3D11_BIND_SHADER_RESOURCE;
    desc.MiscFlags = D3D11_RESOURCE_MISC_SHARED;

    hr = ID3D11Device_CreateTexture2D(ctx->device, &desc, NULL,
                                      &ctx->interopTexture);
    if (FAILED(hr))
        return 0;

    hr = ID3D11Texture2D_QueryInterface(ctx->interopTexture, &IID_IDXGIResource,
                                        (void **)&sharedResource);
    if (FAILED(hr)) {
        ID3D11Texture2D_Release(ctx->interopTexture);
        ctx->interopTexture = NULL;
        return 0;
    }

    hr = IDXGIResource_GetSharedHandle(sharedResource, &ctx->interopSharedHandle);
    IDXGIResource_Release(sharedResource);
    if (FAILED(hr)) {
        ID3D11Texture2D_Release(ctx->interopTexture);
        ctx->interopTexture = NULL;
        return 0;
    }

    if (!createFlipViews(ctx))
        return 0;

    return 1;
}

static int renderFlipToBackBuffer(DX11ShimContext *ctx) {
    D3D11_TEXTURE2D_DESC backBufferDesc;
    D3D11_VIEWPORT viewport;

    if (!ctx->deviceContext || !ctx->backBuffer || !ctx->flipVertexShader ||
        !ctx->flipPixelShader || !ctx->flipSRV || !ctx->flipRTV)
        return 0;

    ID3D11Texture2D_GetDesc(ctx->backBuffer, &backBufferDesc);

    viewport.TopLeftX = 0.0f;
    viewport.TopLeftY = 0.0f;
    viewport.Width = (FLOAT)backBufferDesc.Width;
    viewport.Height = (FLOAT)backBufferDesc.Height;
    viewport.MinDepth = 0.0f;
    viewport.MaxDepth = 1.0f;

    ID3D11DeviceContext_OMSetRenderTargets(ctx->deviceContext, 1, &ctx->flipRTV, NULL);
    ID3D11DeviceContext_RSSetViewports(ctx->deviceContext, 1, &viewport);
    ID3D11DeviceContext_IASetInputLayout(ctx->deviceContext, NULL);
    ID3D11DeviceContext_IASetPrimitiveTopology(
        ctx->deviceContext, D3D11_PRIMITIVE_TOPOLOGY_TRIANGLELIST);
    ID3D11DeviceContext_VSSetShader(ctx->deviceContext, ctx->flipVertexShader, NULL, 0);
    ID3D11DeviceContext_PSSetShader(ctx->deviceContext, ctx->flipPixelShader, NULL, 0);
    ID3D11DeviceContext_PSSetShaderResources(ctx->deviceContext, 0, 1, &ctx->flipSRV);
    ID3D11DeviceContext_Draw(ctx->deviceContext, 3, 0);

    return 1;
}

static DXGI_OUTPUT_DESC1 getOutputDesc(DX11ShimContext *ctx) {
    IDXGIOutput *output = NULL;
    IDXGIOutput6 *output6 = NULL;
    DXGI_OUTPUT_DESC1 desc;
    HRESULT hr;

    ZeroMemory(&desc, sizeof(desc));

    if (!ctx->swapchain)
        return desc;

    hr = IDXGISwapChain_GetContainingOutput(ctx->swapchain, &output);
    if (FAILED(hr))
        return desc;

    hr = IDXGIOutput_QueryInterface(output, &IID_IDXGIOutput6, (void **)&output6);
    if (SUCCEEDED(hr)) {
        IDXGIOutput6_GetDesc1(output6, &desc);
        IDXGIOutput6_Release(output6);
    }

    IDXGIOutput_Release(output);
    return desc;
}

JNIEXPORT jlong JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nCreate(
    JNIEnv *env, jclass cls, jlong hwnd, jint width, jint height,
    jint dxgiFormat, jboolean allowTearing) {
    DX11ShimContext *ctx;
    IDXGIDevice *dxgiDevice = NULL;
    IDXGIAdapter *adapter = NULL;
    IDXGIFactory *factory = NULL;
    DXGI_SWAP_CHAIN_DESC desc;
    D3D_FEATURE_LEVEL featureLevel;
    HRESULT hr;

    (void)env;
    (void)cls;

    if (width < 1)
        width = 1;
    if (height < 1)
        height = 1;

    ctx = (DX11ShimContext *)calloc(1, sizeof(DX11ShimContext));
    if (!ctx) {
        SET_LAST_ERROR("calloc failed: out of memory");
        return 0;
    }

    ctx->hwnd = (HWND)(uintptr_t)hwnd;
    ctx->swapInterval = 1;
    ctx->allowTearing = allowTearing ? 1 : 0;

    assignColorStateFromFormat(ctx, (DXGI_FORMAT)dxgiFormat);

    hr = D3D11CreateDevice(NULL, D3D_DRIVER_TYPE_HARDWARE, NULL,
                           D3D11_CREATE_DEVICE_BGRA_SUPPORT, NULL, 0,
                           D3D11_SDK_VERSION, &ctx->device, &featureLevel,
                           &ctx->deviceContext);
    if (FAILED(hr)) {
        SET_LAST_ERROR("D3D11CreateDevice failed: HRESULT 0x%08lX", (unsigned long)hr);
        free(ctx);
        return 0;
    }

    hr = ID3D11Device_QueryInterface(ctx->device, &IID_IDXGIDevice,
                                     (void **)&dxgiDevice);
    if (FAILED(hr)) {
        SET_LAST_ERROR("ID3D11Device->QueryInterface(IID_IDXGIDevice) failed: HRESULT 0x%08lX", (unsigned long)hr);
        ID3D11DeviceContext_Release(ctx->deviceContext);
        ID3D11Device_Release(ctx->device);
        free(ctx);
        return 0;
    }

    hr = IDXGIDevice_GetAdapter(dxgiDevice, &adapter);
    IDXGIDevice_Release(dxgiDevice);
    if (FAILED(hr)) {
        SET_LAST_ERROR("IDXGIDevice->GetAdapter failed: HRESULT 0x%08lX", (unsigned long)hr);
        ID3D11DeviceContext_Release(ctx->deviceContext);
        ID3D11Device_Release(ctx->device);
        free(ctx);
        return 0;
    }

    hr = IDXGIAdapter_GetParent(adapter, &IID_IDXGIFactory, (void **)&factory);
    IDXGIAdapter_Release(adapter);
    if (FAILED(hr)) {
        SET_LAST_ERROR("IDXGIAdapter->GetParent(IDXGIFactory) failed: HRESULT 0x%08lX", (unsigned long)hr);
        ID3D11DeviceContext_Release(ctx->deviceContext);
        ID3D11Device_Release(ctx->device);
        free(ctx);
        return 0;
    }

    ZeroMemory(&desc, sizeof(desc));
    desc.BufferCount = 2;
    desc.BufferDesc.Format = (DXGI_FORMAT)dxgiFormat;
    desc.BufferDesc.RefreshRate.Numerator = 0;
    desc.BufferDesc.RefreshRate.Denominator = 1;
    desc.BufferUsage = DXGI_USAGE_RENDER_TARGET_OUTPUT;
    desc.OutputWindow = ctx->hwnd;
    desc.SampleDesc.Count = 1;
    desc.Windowed = TRUE;
    desc.SwapEffect = DXGI_SWAP_EFFECT_FLIP_DISCARD;
    desc.Flags = ctx->allowTearing ? DXGI_SWAP_CHAIN_FLAG_ALLOW_TEARING : 0;

    hr = IDXGIFactory_CreateSwapChain(factory, (IUnknown *)ctx->device, &desc,
                                      &ctx->swapchain);
    if (FAILED(hr) && (desc.Flags & DXGI_SWAP_CHAIN_FLAG_ALLOW_TEARING)) {
        desc.Flags &= ~DXGI_SWAP_CHAIN_FLAG_ALLOW_TEARING;
        ctx->allowTearing = 0;
        hr = IDXGIFactory_CreateSwapChain(factory, (IUnknown *)ctx->device,
                                          &desc, &ctx->swapchain);
    }

    IDXGIFactory_Release(factory);
    if (FAILED(hr)) {
        SET_LAST_ERROR("CreateSwapChain failed: HRESULT 0x%08lX", (unsigned long)hr);
        ID3D11DeviceContext_Release(ctx->deviceContext);
        ID3D11Device_Release(ctx->device);
        free(ctx);
        return 0;
    }

    if (!createFlipPipeline(ctx)) {
        IDXGISwapChain_Release(ctx->swapchain);
        ID3D11DeviceContext_Release(ctx->deviceContext);
        ID3D11Device_Release(ctx->device);
        free(ctx);
        return 0;
    }

    if (!createInteropSurface(ctx, width, height)) {
        SET_LAST_ERROR("createInteropSurface(w=%d,h=%d) failed", (int)width, (int)height);
        releaseFlipPipeline(ctx);
        IDXGISwapChain_Release(ctx->swapchain);
        ID3D11DeviceContext_Release(ctx->deviceContext);
        ID3D11Device_Release(ctx->device);
        free(ctx);
        return 0;
    }

    configureSwapchainColorSpace(ctx);

    g_LastError[0] = '\0';
    return (jlong)(uintptr_t)ctx;
}

JNIEXPORT jlong JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nGetDevice(
    JNIEnv *env, jclass cls, jlong context) {
    DX11ShimContext *ctx = (DX11ShimContext *)(uintptr_t)context;
    (void)env;
    (void)cls;
    if (!ctx)
        return 0;
    return (jlong)(uintptr_t)ctx->device;
}

JNIEXPORT jlong JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nGetSharedTextureHandle(
    JNIEnv *env, jclass cls, jlong context) {
    DX11ShimContext *ctx = (DX11ShimContext *)(uintptr_t)context;
    (void)env;
    (void)cls;
    if (!ctx)
        return 0;
    return (jlong)(uintptr_t)ctx->interopSharedHandle;
}

JNIEXPORT jlong JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nGetInteropTexture(
    JNIEnv *env, jclass cls, jlong context) {
    DX11ShimContext *ctx = (DX11ShimContext *)(uintptr_t)context;
    (void)env;
    (void)cls;
    if (!ctx)
        return 0;
    return (jlong)(uintptr_t)ctx->interopTexture;
}

JNIEXPORT jboolean JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nResize(
    JNIEnv *env, jclass cls, jlong context, jint width, jint height) {
    DX11ShimContext *ctx = (DX11ShimContext *)(uintptr_t)context;
    HRESULT hr;
    (void)env;
    (void)cls;

    if (!ctx || !ctx->swapchain)
        return JNI_FALSE;

    if (width < 1)
        width = 1;
    if (height < 1)
        height = 1;

    releaseD3DObjects(ctx);

    hr = IDXGISwapChain_ResizeBuffers(
        ctx->swapchain, 0, (UINT)width, (UINT)height, DXGI_FORMAT_UNKNOWN,
        ctx->allowTearing ? DXGI_SWAP_CHAIN_FLAG_ALLOW_TEARING : 0);
    if (FAILED(hr))
        return JNI_FALSE;

    if (!createInteropSurface(ctx, width, height))
        return JNI_FALSE;

    configureSwapchainColorSpace(ctx);

    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nPresent(
    JNIEnv *env, jclass cls, jlong context, jint interval) {
    DX11ShimContext *ctx = (DX11ShimContext *)(uintptr_t)context;
    UINT presentFlags = 0;
    HRESULT hr;
    (void)env;
    (void)cls;

    if (!ctx || !ctx->swapchain || !ctx->deviceContext)
        return JNI_FALSE;

    ctx->swapInterval = interval;
    if (interval < 0)
        interval = 0;

    if (interval == 0 && ctx->allowTearing)
        presentFlags |= DXGI_PRESENT_ALLOW_TEARING;

    if (!renderFlipToBackBuffer(ctx))
        return JNI_FALSE;

    hr = IDXGISwapChain_Present(ctx->swapchain, (UINT)interval, presentFlags);
    if (FAILED(hr))
        return JNI_FALSE;

    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nSetColorSpace(
    JNIEnv *env, jclass cls, jlong context, jint colorPrimaries,
    jint colorTransfer) {
    DX11ShimContext *ctx = (DX11ShimContext *)(uintptr_t)context;
    (void)env;
    (void)cls;

    if (!ctx)
        return JNI_FALSE;

    ctx->colorPrimaries = colorPrimaries;
    ctx->colorTransfer = colorTransfer;

    configureSwapchainColorSpace(ctx);

    return JNI_TRUE;
}

JNIEXPORT jfloat JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nGetMaxLuminance(
    JNIEnv *env, jclass cls, jlong context) {
    DX11ShimContext *ctx = (DX11ShimContext *)(uintptr_t)context;
    DXGI_OUTPUT_DESC1 desc;
    (void)env;
    (void)cls;

    if (!ctx)
        return 0.0f;

    desc = getOutputDesc(ctx);
    return (jfloat)desc.MaxLuminance;
}

JNIEXPORT jfloat JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nGetMinLuminance(
    JNIEnv *env, jclass cls, jlong context) {
    DX11ShimContext *ctx = (DX11ShimContext *)(uintptr_t)context;
    DXGI_OUTPUT_DESC1 desc;
    (void)env;
    (void)cls;

    if (!ctx)
        return 0.0f;

    desc = getOutputDesc(ctx);
    return (jfloat)desc.MinLuminance;
}

JNIEXPORT void JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nDestroy(
    JNIEnv *env, jclass cls, jlong context) {
    DX11ShimContext *ctx = (DX11ShimContext *)(uintptr_t)context;
    (void)env;
    (void)cls;

    if (!ctx)
        return;

    releaseD3DObjects(ctx);
    releaseFlipPipeline(ctx);

    if (ctx->swapchain) {
        IDXGISwapChain_Release(ctx->swapchain);
        ctx->swapchain = NULL;
    }

    if (ctx->deviceContext) {
        ID3D11DeviceContext_Release(ctx->deviceContext);
        ctx->deviceContext = NULL;
    }

    if (ctx->device) {
        ID3D11Device_Release(ctx->device);
        ctx->device = NULL;
    }

    free(ctx);
}

JNIEXPORT jstring JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nGetLastError(
    JNIEnv *env, jclass cls) {
    (void)cls;
    return (*env)->NewStringUTF(env, g_LastError[0] ? g_LastError : "");
}