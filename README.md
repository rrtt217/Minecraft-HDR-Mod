# Minecraft HDR Mod
A minecraft mod that enables native HDR on Windows/Linux with Iris shaders. Currently in early development.
## What the mod have achieved
- Native HDR, both for Windows scRGB and HDR10/PQ;
- UI color correction by a BEFORE_BLIT pass;
- Ingame config using Cloth Config API(May switch to Sodium Options API later)
## For shader patches
### Macros
- `HDR_MOD_INSTALLED`
- `HDR_ENABLED`
- `CURRENT_PRIMARIES`: One of `PRIMARIES_SRGB`, `PRIMARIES_PAL_M`, `PRIMARIES_PAL`, `PRIMARIES_NTSC`, `PRIMARIES_GENERIC_FILM`, `PRIMARIES_BT2020`, `PRIMARIES_CIE1931_XYZ`, `PRIMARIES_DCI_P3`, `PRIMARIES_DISPLAY_P3`, `PRIMARIES_ADOBE_RGB`;
- `CURRENT_TRANSFER_FUNCTION`: One of `TRANSFER_FUNCTION_BT1886`, `TRANSFER_FUNCTION_GAMMA22`, `TRANSFER_FUNCTION_GAMMA28`, `TRANSFER_FUNCTION_ST240`, `TRANSFER_FUNCTION_EXT_LINEAR`, `TRANSFER_FUNCTION_LOG_100`, `TRANSFER_FUNCTION_LOG_316`, `TRANSFER_FUNCTION_XVYCC`, `TRANSFER_FUNCTION_SRGB`, `TRANSFER_FUNCTION_EXT_SRGB`, `TRANSFER_FUNCTION_ST2084_PQ`, `TRANSFER_FUNCTION_ST428`, `TRANSFER_FUNCTION_HLG`.
### Uniforms
- `float MinBrightness`;
- `float MaxBrightness`;
- `float SDRWhiteLevel`;
- `float UIBrightness`;

Use these uniforms inside `#if HDR_MOD_INSTALLED` block.
## Known Issue
Ingame screenshot is broken.
## Credits
- Tom94, for [glfw fork with color management](https://github.com/Tom94/glfw/tree/color-management);
- IMS212, for [Iris shaders](https://github.com/IrisShaders/Iris) and [original gifw patch idea](https://github.com/IMS212/glfw/tree/hdr);
- XgarhontX and some other fellows, for working on RenoDX Minecraft HDR and shader patches, and helping me to make color transform correct.
