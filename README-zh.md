# Minecraft HDR Mod
一个允许你在Windows / macOS / Linux 平台上使用Iris光影实现HDR输出的Minecraft模组.
## 已实现功能
- 原生HDR输出, 支持 scRGB , HDR10/PQ 输出格式.
- 通过在`blitToScreen()`之前进行的`ColorTransformRenderer`实现将UI颜色校正到输出格式.
- 使用 Cloth Config API 实现的游戏内配置界面.
- 游戏内 HDR PNG 截图.
- Replay Mod HDR 视频导出 (需要自定义 FFmpeg 命令行, 可以在配置菜单里找到)
## 用户指南
- 参见 XgarhontX 的关于 HDR 输出和色调映射并兼容该模组的对特定光影包的临时补丁: [Google Sheets](https://docs.google.com/spreadsheets/d/1WgOqKED2FxC11-2oyW4aBIyl8tAHo-8WJ7JPxhYAO2Q/edit?gid=0#gid=0)
  - 在这里查看截图效果: [Wiki](https://github.com/rrtt217/Minecraft-HDR-Mod/wiki#gallery)
- 通过配置菜单调节 HDR 相关亮度值 (参考白 / UI / 峰值亮度). (默认按键绑定: F9)
- 在游戏内拍摄 HDR 截图 (默认按键绑定: F10)
- 支持平台:
  - Windows: 现代 Nvidia/AMD GPU 基本无缝运行. 现代 Intel GPU 自 v2.1.0 起通过 DXGI 回退路径支持，存在性能损失(更多信息详见 [v2.1.0 发行说明](https://github.com/rrtt217/Minecraft-HDR-Mod/releases/tag/v2.1.0)).
  - Linux Wayland: 现代 AMD/Intel GPU 基本无缝运行. 使用 Nvidia GPU 的用户可能遇到一些问题或崩溃, 参见 troubleshooting 以了解可能的解决方案.
  - macOS: 可运行，但需要更多测试，且 macOS 上光影包支持受限。
  - Android/iOS: **不支持**. 如果手机启动器开发者对这个项目感兴趣，我的 GLFW 分支可以作为实现 HDR Mod 兼容的参考。
- **Troubleshooting**:
    - ReShade 在默认配置下与该模组不兼容, 会加载失败并破坏天空渲染。
      - 在设置 / 高级中打开 "Force Activate OpenGL-DirectX Interop" 可以提高与 SpecialK and ReShade 的兼容性. 请确保它们只注入 DirectX 11 层.
    - 同样修改GLFW的模组可能不兼容. (如 [Ixeris](https://modrinth.com/mod/ixeris))
    - 如果游戏太亮，太暗或 HDR 效果不明显，请在配置里修改相关亮度值；
    - Windows用户不要忘记安装最新版 [Visual C++ Redistributable](https://learn.microsoft.com/en-us/cpp/windows/latest-supported-vc-redist?view=msvc-170).
    - 在 Forge/NeoForge 上请关闭 `<game folder>/config/fml.toml` 中的 `earlyWindowControl`。 
    - 如果光影开启时 HDR 失效, 请将 Iris/Oculus 中的 "色彩空间" 重置为 "sRGB".
    - 如果游戏无法启动或功能缺失请清理临时文件 / 旧 GLFW 库 (可以在日志中找到路径, 或者打开 Windows 上的 %TEMP%/glfw 及 Linux 上的 /tmp/glfw).
    - 对 Linux Nvidia 用户, 如果游戏无法启动或画面出现明显色带, 这里有一个可能的解决方案 (使用了 Zink, 所以很可能存在性能损失):
      - 使用这些环境变量:
        ```
        export __GLX_VENDOR_LIBRARY_NAME=mesa
        export __EGL_VENDOR_LIBRARY_FILENAMES=/run/opengl-driver/share/glvnd/egl_vendor.d/50_mesa.json
        export MESA_LOADER_DRIVER_OVERRIDE=zink
        export GALLIUM_DRIVER=zink
        ```
      - **只在 Mesa >= 26.0.0 有效**. 较低的版本会因为缺乏 [这个PR](https://gitlab.freedesktop.org/mesa/mesa/-/merge_requests/37693) 而崩溃。
## 光影开发参考
### Wiki 教程
- [光影开发者适配指南](https://github.com/rrtt217/Minecraft-HDR-Mod/wiki/%E5%85%89%E5%BD%B1%E5%BC%80%E5%8F%91%E8%80%85%E9%80%82%E9%85%8D%E6%8C%87%E5%8D%97)
### 宏
- `HDR_MOD_INSTALLED`
- `HDR_ENABLED`
- **已弃用**: `CURRENT_PRIMARIES`: `PRIMARIES_SRGB`, `PRIMARIES_PAL_M`, `PRIMARIES_PAL`, `PRIMARIES_NTSC`, `PRIMARIES_GENERIC_FILM`, `PRIMARIES_BT2020`, `PRIMARIES_CIE1931_XYZ`, `PRIMARIES_DCI_P3`, `PRIMARIES_DISPLAY_P3`, `PRIMARIES_ADOBE_RGB` 之一;
- **已弃用**: `CURRENT_TRANSFER_FUNCTION`: `TRANSFER_FUNCTION_BT1886`, `TRANSFER_FUNCTION_GAMMA22`, `TRANSFER_FUNCTION_GAMMA28`, `TRANSFER_FUNCTION_ST240`, `TRANSFER_FUNCTION_EXT_LINEAR`, `TRANSFER_FUNCTION_LOG_100`, `TRANSFER_FUNCTION_LOG_316`, `TRANSFER_FUNCTION_XVYCC`, `TRANSFER_FUNCTION_SRGB`, `TRANSFER_FUNCTION_EXT_SRGB`, `TRANSFER_FUNCTION_ST2084_PQ`, `TRANSFER_FUNCTION_ST428`, `TRANSFER_FUNCTION_HLG` 之一.
### 统一变量
- `float HdrGameMinimumBrightness`;
- `float HdrGamePeakBrightness`;
- `float HdrGamePaperWhiteBrightness`;
- `float HdrUIBrightness`;

在 `#if HDR_MOD_INSTALLED` 块内声明及使用这些变量.

### 目标输出
光影的输出(即在 final 中绘制到主渲染目标的内容) 应采用 **ext-sRGB 编码(Rec.709 primaries, sRGB (for negative numbers use −f(−x)) transfer function)**。你不应该为场景颜色使用 RGBA8 格式。
- [Wiki 部分](https://github.com/rrtt217/Minecraft-HDR-Mod/wiki/%E5%85%89%E5%BD%B1%E5%BC%80%E5%8F%91%E8%80%85%E9%80%82%E9%85%8D%E6%8C%87%E5%8D%97#4-ui-%E7%BC%A9%E6%94%BE%E4%B8%8E%E9%A2%84%E6%9C%9F%E8%BE%93%E5%87%BA)
## 致谢
- Tom94, for [glfw fork with color management](https://github.com/Tom94/glfw/tree/color-management);
- IMS212, for [Iris shaders](https://github.com/IrisShaders/Iris) and [original glfw patch idea](https://github.com/IMS212/glfw/tree/hdr);
- XgarhontX 以及其他人, 因为他们为 RenoDX Minecraft HDR 以及光影包补丁做了很多工作, 也帮助我保证色彩转换的正确性。
## 版权声明
HDR Mod 主仓库采用 MIT 授权. 打包的 GLFW 分支采用 zlib/libpng 授权.
## 星星历史
[![Star History Chart](https://api.star-history.com/svg?repos=rrtt217/Minecraft-HDR-Mod&type=date&legend=top-left)](https://www.star-history.com/#rrtt217/Minecraft-HDR-Mod&type=date&legend=top-left)
