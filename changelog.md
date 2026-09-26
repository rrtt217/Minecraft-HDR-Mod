# HDR Mod v2.5.2 Changelog
## Fixes
- Broken rendering when using RGBA16F on Linux because of a hack for Nvidia / missing opaque requirement
    - Linux Nvidia users should upgrade egl-wayland2 to 1.0.2 or above to avoid crash.