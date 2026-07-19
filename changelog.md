# HDR Mod v3.1.0 Changelog
## Features Added
- BlazeSDL support
    - HDR Mod behavior on BlazeSDL is generally the same as on GLFW, except these differences;
        - As for OpenGL, Wayland use EXT_SRGB instead of ST2084_PQ or EXT_LINEAR, and 10 bit UNORM mode is not available;
        - As for Vulkan, Wayland on 10 bit UNORM mode may behave differently to OpenGL.
    - We ship an extra library called `dx11-interop-shim`, which is required for DX-GL interop and Windows Intel compat for BlazeSDL. The source code is avaliable at `native/dx11-interop-shim`.
## Fixed
- Partially fixed "Cursor doesn't align with UI in fullscreen mode" (#65)