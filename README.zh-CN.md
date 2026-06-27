# PasteFlow Dev

PasteFlow Dev 是一个实验性的桌面剪贴板管理器，基于开源项目
[CrossPaste](https://github.com/CrossPaste/crosspaste-desktop) 构建。

当前目标是做出一个可用的 macOS MVP，并让它能与 CrossPaste 分开安装、分开运行。

[![License: AGPL-3.0](https://img.shields.io/badge/License-AGPL--3.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-desktop-blue.svg)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/UI-Compose%20Multiplatform-blue.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)

## 当前 MVP 状态

PasteFlow Dev 已经完成了基本的开发身份分离：

- 应用名称：PasteFlow Dev
- macOS bundle identity 已与 CrossPaste 分离
- 本地数据目录已与 CrossPaste 分离
- 开发网络端口已与 CrossPaste 分离
- 默认快捷键已与 CrossPaste 分离
- Native messaging 标识已与 CrossPaste 分离
- macOS Accessibility 权限提示已针对快捷键使用做过调整

已在本机 macOS 验证：

- 剪贴板捕获
- 主快捷键启动
- 搜索快捷键启动
- 重启行为
- macOS Accessibility 授权流程

## 暂缓处理

以下内容会在后续 PasteFlow Dev 阶段继续整理：

- Share 页面与 Share 菜单
- Check for updates / 发布更新通道
- 仍继承自 CrossPaste 的旧更新实现命名
- 公开定位、截图和发布打包
- 其他设备上的多机测试

更多后续事项见 [doc/zh/Roadmap.md](doc/zh/Roadmap.md)。

## 开发设置

克隆此 fork：

```bash
git clone https://github.com/clownfishrob/crosspaste-desktop.git
cd crosspaste-desktop
```

运行桌面应用：

```bash
./gradlew app:run -PappEnv=BETA
```

运行桌面测试：

```bash
./gradlew :app:desktopTest -PappEnv=BETA
```

创建 macOS 桌面应用包：

```bash
./gradlew :app:createDistributable -PappEnv=BETA
```

生成的应用位于：

```text
app/build/compose/binaries/main/app/
```

首次构建可能会下载 Gradle、Kotlin、Compose 和 JetBrains Runtime 依赖。
建议本地开发使用 JDK 21。

## macOS 本地安装

创建应用包后，可以复制到用户 Applications 目录：

```bash
rm -rf "$HOME/Applications/pasteflow-dev.app"
ditto "app/build/compose/binaries/main/app/pasteflow-dev.app" "$HOME/Applications/pasteflow-dev.app"
open -n "$HOME/Applications/pasteflow-dev.app"
```

全局快捷键需要 macOS Accessibility 权限。请打开：

```text
System Settings -> Privacy & Security -> Accessibility
```

然后启用 `pasteflow-dev`。

## 致谢

PasteFlow Dev 是 CrossPaste 的 fork。原项目提供了跨平台剪贴板、同步、存储、UI 和扩展等核心基础。

原项目：

- Repository: [CrossPaste/crosspaste-desktop](https://github.com/CrossPaste/crosspaste-desktop)
- Website: [crosspaste.com](https://crosspaste.com)

## License

本仓库使用 GNU Affero General Public License v3.0。
详见 [LICENSE](LICENSE)。

由于这是 AGPL-3.0 项目的 fork，衍生工作也需要继续遵守 AGPL-3.0 许可证条款。

## Contact

```text
rob@ngduk.co.uk
```
