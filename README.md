# Minecraft HDR Mod
A minecraft mod that enables native HDR on Windows/Linux with Iris shaders. Currently in early development.
## For shader patches
### Macros
- HDR_MOD_INSTALLED
- HDR_ENABLED
- CURRENT_PRIMARIES: One of SRGB, PAL_M, PAL, NTSC, GENERIC_FILM, BT2020, CIE1931_XYZ, DCI_P3, DISPLAY_P3, ADOBE_RGB;
- CURRENT_TRANSFER_FUNCTION: One of BT1886, GAMMA22, GAMMA28, ST240, EXT_LINEAR, LOG_100, LOG_316, XVYCC, SRGB, EXT_SRGB, ST2084_PQ, ST428, HLG.
### Uniforms
- float MinLuminance;
- float MaxLuminance;
- float SDRWhiteLevel.
## TODOs
- [] Built-in vanilla core shader for GUI color correction; 
- [] Macros and Uniforms for vanilla core shader compat;
- [] Translation of config;
- [] HDR screenshot in game.
## Credits
Tom94, for [glfw fork with color management](https://github.com/Tom94/glfw/tree/color-management);
IMS212, for [Iris shaders](https://github.com/IrisShaders/Iris) and [original gifw patch idea](https://github.com/IMS212/glfw/tree/hdr);
and a lot of people working on RenoDX Minecraft HDR.
