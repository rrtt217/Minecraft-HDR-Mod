# Minecraft HDR Mod
[中文](https://github.com/rrtt217/Minecraft-HDR-Mod/blob/master/README-zh.md) | English

A minecraft mod that enables native HDR on Windows / macOS / Linux with Iris shaders. Currently, in active development.
[![PhotonHDRPic](https://i.postimg.cc/tgRwc3x5/2026-02-15-20-07-37-hdr.png)](https://postimg.cc/LJ7xYZvY)
([Photon](https://modrinth.com/shader/photon-shader) patched to HDR w/ [Patrix](https://modrinth.com/resourcepack/patrix-32x), taken by mod's screenshot feature.)
## What the mod have achieved
- Native HDR, both for scRGB and HDR10/PQ.
- UI color correction by a new render pass.
- Ingame config using Cloth Config API.
- Ingame HDR screenshot.
- Replay Mod HDR Video Export (need custom FFmpeg commandline, which can be found in config menu)
- Full Wayland IME fix.
## For Users
- See XgarhontX's temporary patches for HDR output and tonemapping on select shaderpacks with support for this mod: [Google Sheets](https://docs.google.com/spreadsheets/d/1WgOqKED2FxC11-2oyW4aBIyl8tAHo-8WJ7JPxhYAO2Q/edit?gid=0#gid=0)
  - See some pics here: [Wiki](https://github.com/rrtt217/Minecraft-HDR-Mod/wiki#gallery)
- Adjust HDR Brightness values (Paper White, UI, Peak) with config menu. (Default keybind: F9)
- Take a HDR screenshot ingame. (Default keybind: F10)
- Supported platforms:
  - Windows: Nvidia/AMD GPU generally run flawlessly. Intel GPU is supported since v2.1.0 with a DXGI fallback path and there's performance degradation (more info in [v2.1.0 release note](https://github.com/rrtt217/Minecraft-HDR-Mod/releases/tag/v2.1.0)).
  - Linux Wayland: AMD/Intel GPU generally run flawlessly. People using Nvidia GPU may encounter some problems/crashes, see the troubleshooting for a potential workaround.
  - macOS: Supported but need more testing, bugs are expected. Also, shaderpack support on macOS is limited.
  - Android/iOS: **Not supported**. If Mobile launcher developers are interested in the project, my glfw fork can be a starting point for HDR support in the future.
- **Troubleshooting**:
    - ReShade is currently not compatible under default settings, failing to load and breaking sky.
      - Enabling "Force Activate OpenGL-DirectX Interop" in config/advanced can improve compatibility with SpecialK and ReShade. Please make sure they only inject into DirectX 11 layer.
    - Mods also messing with GLFW may be incompatible. (e.g. [Ixeris](https://modrinth.com/mod/ixeris))
    - If your game is too bright/dark, try to adjust the custom brightness values yourself.
    - If you're on Windows, don't forget to install the latest [Visual C++ Redistributable](https://learn.microsoft.com/en-us/cpp/windows/latest-supported-vc-redist?view=msvc-170).
    - If your game crashes on NeoForge/Forge, disable `earlyWindowControl` in `<game folder>/config/fml.toml`
    - If HDR is broken when shaders are enabled, please reset "Colorspace" setting in Iris/Oculus to "sRGB".
    - Clean temp file if your game doesn't start or some features are broken (path can be found in log, %TEMP%/glfw on Windows and /tmp/glfw on Linux).
    - For Linux Nvidia users, if your game can't start, or you feel too much banding ingame, here's a potential workaround (using Zink, so performance degradation is expected):
      - Use these environment variables:
        ```
        export __GLX_VENDOR_LIBRARY_NAME=mesa
        export __EGL_VENDOR_LIBRARY_FILENAMES=/run/opengl-driver/share/glvnd/egl_vendor.d/50_mesa.json
        export MESA_LOADER_DRIVER_OVERRIDE=zink
        export GALLIUM_DRIVER=zink
        ```
      - **ONLY WORKS ON Mesa >= 26.0.0**. Lower versions crash your game due to the lack of [This PR](https://gitlab.freedesktop.org/mesa/mesa/-/merge_requests/37693).
## For Shader Packs
### Wiki Tutorial
- [For Shaderpack Devs](https://github.com/rrtt217/Minecraft-HDR-Mod/wiki/For-Shaderpack-Devs)
### Macros
- `HDR_MOD_INSTALLED`
- `HDR_ENABLED`
- **Deprecated**: `CURRENT_PRIMARIES`: One of `PRIMARIES_SRGB`, `PRIMARIES_PAL_M`, `PRIMARIES_PAL`, `PRIMARIES_NTSC`, `PRIMARIES_GENERIC_FILM`, `PRIMARIES_BT2020`, `PRIMARIES_CIE1931_XYZ`, `PRIMARIES_DCI_P3`, `PRIMARIES_DISPLAY_P3`, `PRIMARIES_ADOBE_RGB`;
- **Deprecated**: `CURRENT_TRANSFER_FUNCTION`: One of `TRANSFER_FUNCTION_BT1886`, `TRANSFER_FUNCTION_GAMMA22`, `TRANSFER_FUNCTION_GAMMA28`, `TRANSFER_FUNCTION_ST240`, `TRANSFER_FUNCTION_EXT_LINEAR`, `TRANSFER_FUNCTION_LOG_100`, `TRANSFER_FUNCTION_LOG_316`, `TRANSFER_FUNCTION_XVYCC`, `TRANSFER_FUNCTION_SRGB`, `TRANSFER_FUNCTION_EXT_SRGB`, `TRANSFER_FUNCTION_ST2084_PQ`, `TRANSFER_FUNCTION_ST428`, `TRANSFER_FUNCTION_HLG`.
### Uniforms
- `float HdrGameMinimumBrightness`;
- `float HdrGamePeakBrightness`;
- `float HdrGamePaperWhiteBrightness`;
- `float HdrUIBrightness`;

Use these uniforms inside `#if HDR_MOD_INSTALLED` block.
### Output
the output of the shader(aka. what is written to main render target in final pass) should have **scRGB-nl encoding(Rec.709 primaries, sRGB (for negative numbers use −f(−x)) transfer function)**, and you should not use RGBA8F for scene color.
- [Wiki Section](https://github.com/rrtt217/Minecraft-HDR-Mod/wiki/For-Shaderpack-Devs#4-ui-scaling--expected-output)
## Credits
- Tom94, for [glfw fork with color management](https://github.com/Tom94/glfw/tree/color-management);
- IMS212, for [Iris shaders](https://github.com/IrisShaders/Iris) and [original glfw patch idea](https://github.com/IMS212/glfw/tree/hdr);
- XgarhontX and some other fellows, for working on RenoDX Minecraft HDR and shader patches, and helping me to make color transform correct.
## Copyright
The main HDR Mod repo is licensed under MIT. Bundled GLFW from Tom94's/mine fork is licensed under the zlib/libpng license.
## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=rrtt217/Minecraft-HDR-Mod&type=date&legend=top-left)](https://www.star-history.com/#rrtt217/Minecraft-HDR-Mod&type=date&legend=top-left)
