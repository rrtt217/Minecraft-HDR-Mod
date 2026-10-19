# HDR Mod v3.2.3 Changelog
## New Features
- Windows Intel Vulkan HDR
    - Only works under exclusive full screen
## Fixes
- Broken rendering when using RGBA16F on Linux because of a hack for Nvidia / missing opaque requirement
    - Linux Nvidia users should upgrade egl-wayland2 to 1.0.2 or above to avoid crash.
- Compatibility with latest Super Resolution
- Some Vulkan HDR metadata related bugs