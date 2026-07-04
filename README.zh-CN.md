# PasteFlow Dev

PasteFlow Dev 是一个实验性的桌面剪贴板管理器，fork 自开源项目
[CrossPaste](https://github.com/CrossPaste/crosspaste-desktop)。

当前目标是做出一个 macOS 优先的实用 MVP：本地剪贴板历史、快速搜索浮层、固定收藏、合理的隐私默认设置，以及与 CrossPaste 分开的开发身份。

## 当前 MVP 状态

当前构建已在维护者的 Mac 上验证：

- 文本、链接、HTML、RTF、图片、文件、文件夹和颜色的剪贴板捕获
- 居中的搜索浮层和键盘粘贴
- 基于标签系统的固定收藏
- 当前结果列表的 10 个快速粘贴位
- 桌面截图和 Skitch 导出捕获
- macOS Accessibility 权限引导
- 默认本地优先，发现和同步默认关闭
- 高置信度密钥和 token 的隐私检测
- 固定收藏的导入/导出
- 手动 macOS 打包

这个项目仍处于预发布阶段。目前没有公共更新通道、签名公开发布包或自动安装流程。

## 开发身份

此 fork 在开发阶段与 CrossPaste 隔离：

- 应用名称：`PasteFlow Dev`
- macOS bundle ID：`com.robdev.pasteflow.dev`
- macOS 应用数据目录：`~/Library/Application Support/PasteFlow Dev`
- 开发数据目录：`.pasteflow-dev`
- Bonjour 服务类型：`_pasteflowDevService._tcp.local.`
- 默认本地端口：`13139`
- Native messaging host：`com.robdev.pasteflow.dev.desktop`
- 默认主窗口快捷键：`Meta/Win+Shift+0`
- 默认搜索快捷键：`Meta/Win+Shift+Minus`

Kotlin package 名称仍保留为 `com.crosspaste`，以避免对 desktop、shared、CLI、扩展和移动端相关模块进行高风险的大规模重命名。

## 当前缺口

以下工作仍需后续设备或产品方向确认：

- 第二台设备同步/手动添加测试
- Windows 和 Linux 可靠性整理
- 公开发布签名、公证和更新交付
- 基础项目 Share 页面之外的公开发布/社交分享
- GitHub/产品截图和更完整的公开定位
- OCR、片段模板、智能收藏等高级功能

更多路线图请查看 [docs/clipboard-manager-mvp.md](docs/clipboard-manager-mvp.md)。

## 本地构建

```bash
git clone https://github.com/clownfishrob/crosspaste-desktop.git
cd crosspaste-desktop
```

使用 JDK 21。在当前开发 Mac 上可使用仓库中的 JetBrains Runtime：

```bash
export JAVA_HOME="$PWD/app/jbr/extracted/jbrsdk-21.0.9-osx-aarch64-b1163.94/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

运行桌面应用：

```bash
./gradlew -PappEnv=BETA :app:run
```

运行桌面测试：

```bash
./gradlew -PappEnv=BETA :app:desktopTest
```

创建 macOS 包：

```bash
./gradlew -PappEnv=BETA :app:packageDistributionForCurrentOS
```

生成文件位于：

```text
app/build/compose/binaries/main/
```

## macOS 本地安装

```bash
rm -rf "$HOME/Applications/pasteflow-dev.app"
ditto "app/build/compose/binaries/main/app/pasteflow-dev.app" "$HOME/Applications/pasteflow-dev.app"
codesign --force --deep --sign - "$HOME/Applications/pasteflow-dev.app"
open -n "$HOME/Applications/pasteflow-dev.app"
```

全局快捷键和粘贴回目标应用需要 macOS Accessibility 权限：

```text
System Settings -> Privacy & Security -> Accessibility
```

启用 `pasteflow-dev`。如果本地重新构建后 macOS 仍反复提示权限，请在 Accessibility 中移除并重新添加应用，然后退出并重新打开 PasteFlow Dev。

## 致谢

PasteFlow Dev 是 CrossPaste 的 fork。原项目提供了跨平台剪贴板、同步、存储、UI、扩展和 shared module 等核心基础。

- 上游仓库：[CrossPaste/crosspaste-desktop](https://github.com/CrossPaste/crosspaste-desktop)
- 上游网站：[crosspaste.com](https://crosspaste.com)

## 许可证

本仓库使用 GNU Affero General Public License v3.0。详见 [LICENSE](LICENSE)。

## 联系

```text
rob@ngduk.co.uk
```
