#ifndef DX11_SHIM_H
#define DX11_SHIM_H

#include <stdint.h>
#include <jni.h>
#include <d3d11.h>
#include <dxgi.h>
#include <dxgi1_4.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    ID3D11Device            *device;
    ID3D11DeviceContext     *deviceContext;
    IDXGISwapChain          *swapchain;

    ID3D11Texture2D         *interopTexture;
    HANDLE                   interopSharedHandle;

    ID3D11Texture2D         *backBuffer;

    ID3D11VertexShader      *flipVertexShader;
    ID3D11PixelShader       *flipPixelShader;
    ID3D11ShaderResourceView *flipSRV;
    ID3D11RenderTargetView  *flipRTV;

    uint32_t swapchainFormat;
    int      colorPrimaries;
    int      colorTransfer;
    int      allowTearing;
    int      swapInterval;
    HWND     hwnd;
} DX11ShimContext;

JNIEXPORT jlong JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nCreate(
    JNIEnv *env, jclass cls, jlong hwnd, jint width, jint height,
    jint dxgiFormat, jboolean allowTearing);

JNIEXPORT jlong JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nGetDevice(
    JNIEnv *env, jclass cls, jlong context);

JNIEXPORT jlong JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nGetSharedTextureHandle(
    JNIEnv *env, jclass cls, jlong context);

JNIEXPORT jlong JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nGetInteropTexture(
    JNIEnv *env, jclass cls, jlong context);

JNIEXPORT jboolean JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nResize(
    JNIEnv *env, jclass cls, jlong context, jint width, jint height);

JNIEXPORT jboolean JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nPresent(
    JNIEnv *env, jclass cls, jlong context, jint interval);

JNIEXPORT jboolean JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nSetColorSpace(
    JNIEnv *env, jclass cls, jlong context, jint colorPrimaries,
    jint colorTransfer);

JNIEXPORT void JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nDestroy(
    JNIEnv *env, jclass cls, jlong context);

JNIEXPORT jstring JNICALL Java_xyz_rrtt217_HDRMod_util_DX11InteropShim_nGetLastError(
    JNIEnv *env, jclass cls);

#ifdef __cplusplus
}
#endif

#endif /* DX11_SHIM_H */
