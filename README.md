# Minecraft HDR Mod
A minecraft mod that enables native HDR on Windows/Linux with Iris shaders. Currently in early development.
[![PhotonHDRPic](https://i.postimg.cc/tgRwc3x5/2026-02-15-20-07-37-hdr.png)](https://postimg.cc/LJ7xYZvY)
([Photon](https://modrinth.com/shader/photon-shader) patched to HDR w/ [Patrix](https://modrinth.com/resourcepack/patrix-32x), taken by mod's screenshot feature.)
## What the mod have achieved
- Native HDR, both for Windows scRGB and HDR10/PQ;
- UI color correction by a BEFORE_BLIT pass;
- Ingame config using Cloth Config API;
- Ingame HDR screenshot.
## For Users
- See XgarhontX's temporary patches for HDR output and tonemapping on select shaderpacks with support for this mod: [Google Sheets](https://docs.google.com/spreadsheets/d/1WgOqKED2FxC11-2oyW4aBIyl8tAHo-8WJ7JPxhYAO2Q/edit?gid=0#gid=0)
- Adjust the HDR Brightness values with config menu. (Default keybind: F9)
- Take a HDR screenshot ingame. (Default keybind: F10)
- *Troubleshooting*: ReShade is currently not compatible, failing to load and breaking sky.
- *Troubleshooting*: If your game is too bright/dark, try adjust the custom brightness values yourself.
- *Troubleshooting*: If you're on Windows, don't forget to install the latest Visual C++ Redistributable.
- *Troubleshooting*: If your game crashes on NeoForge, disable `earlyWindowControl` in `<game folder>/config/fml.toml`
- *Troubleshooting*: Clean temp file if your game doesn't start (path can be found in log, %TEMP%/glfw on Windows and /tmp/glfw on Linux).
## For Shader Packs
### Macros
- `HDR_MOD_INSTALLED`
- `HDR_ENABLED`
- `CURRENT_PRIMARIES`: One of `PRIMARIES_SRGB`, `PRIMARIES_PAL_M`, `PRIMARIES_PAL`, `PRIMARIES_NTSC`, `PRIMARIES_GENERIC_FILM`, `PRIMARIES_BT2020`, `PRIMARIES_CIE1931_XYZ`, `PRIMARIES_DCI_P3`, `PRIMARIES_DISPLAY_P3`, `PRIMARIES_ADOBE_RGB`;
- `CURRENT_TRANSFER_FUNCTION`: One of `TRANSFER_FUNCTION_BT1886`, `TRANSFER_FUNCTION_GAMMA22`, `TRANSFER_FUNCTION_GAMMA28`, `TRANSFER_FUNCTION_ST240`, `TRANSFER_FUNCTION_EXT_LINEAR`, `TRANSFER_FUNCTION_LOG_100`, `TRANSFER_FUNCTION_LOG_316`, `TRANSFER_FUNCTION_XVYCC`, `TRANSFER_FUNCTION_SRGB`, `TRANSFER_FUNCTION_EXT_SRGB`, `TRANSFER_FUNCTION_ST2084_PQ`, `TRANSFER_FUNCTION_ST428`, `TRANSFER_FUNCTION_HLG`.
### Uniforms
- `float HdrGameMinimumBrightness`;
- `float HdrGamePeakBrightness`;
- `float HdrGamePaperWhiteBrightness`;
- `float HdrUIBrightness`;

Use these uniforms inside `#if HDR_MOD_INSTALLED` block.
### Output
the output of the shader(aka. what is written to main render target in final pass) should have **scRGB-nl encoding(Rec.709 primaries, sRGB (for negative numbers use −f(−x)) transfer function)**, and you should not use RGBA8F for scene color.
## Known Issue
- Xaero Map may cause black screen on startup, but recover to normal before you see title screen.
## Credits
- Tom94, for [glfw fork with color management](https://github.com/Tom94/glfw/tree/color-management);
- IMS212, for [Iris shaders](https://github.com/IrisShaders/Iris) and [original glfw patch idea](https://github.com/IMS212/glfw/tree/hdr);
- XgarhontX and some other fellows, for working on RenoDX Minecraft HDR and shader patches, and helping me to make color transform correct.
## Copyright
The main HDR Mod repo is licensed under MIT. Bundled GLFW from Tom94's/mine fork is licensed under the zlib/libpng license.
