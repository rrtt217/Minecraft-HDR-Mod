# HDR Mod v2.5.0 Changelog
## New Features
- Brightness Control by shortcuts (default no keybind)
- Initial Super Resolution Vulkan Presentation and (possibly) framegen support
- Mod API
- Get HDR display metadata without GL-DX interop on Windows
## Changes
- Replaced dx11-interop-shim with FFI/FFM based pure-Java GL-DX interop that is heavily inspired by Perfect Presentation (which is MIT licensed, full license in code)
    - Currently there's a bug about fullscreen on BlazeSDL about GL-DX interop, which should not affect most users.
## Others
- Internal refactor (this doesn't affect mod compatibility like IMBlocker)