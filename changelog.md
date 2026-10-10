# HDR Mod v3.2.2-beta2-26.3 Changelog
## Fixes Compared to beta1
- More Wayland Color Management parity (w/ 26.2) fixes, on both OpenGL and Vulkan
   - Thanks for fililip for continuous testing, though it might still be broken on some corner cases, it has been well aligned to 26.2.
- Fixed crash with WAYLAND_DEBUG=1
   - This is an upstream wayland-java bug.
- SR Vulkan presentation support for latest version
## Note
Replay Mod for 26.3 has still not yet published, so I cannot fix the compatibility. Will fix it in v3.2.2 full release.