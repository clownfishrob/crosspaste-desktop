# [2.1.7] - 2026-07-15

## macOS 12 打包
macOS 包元数据现在在 Gradle 和 Conveyor 打包路径中都统一声明支持 macOS 12.0。

## 收藏同步控制
收藏现在可以保存同步设置，关闭某个收藏的同步后，其中的项目不会再自动发送到其他设备。

## OCR 和收藏基础能力
图片预览现在会显示已提取的 OCR 文本，并加入了片段模板和智能收藏的基础能力。

# [2.1.6] - 2026-07-15

## 设备配对可靠性
验证提示现在会在应用全局显示，而不是只在 Devices 页面中显示，因此新发现的设备可以在任意位置完成信任验证。

## 手动网络配对
手动添加设备现在会保留你输入的确切地址，包括局域网 IP、Tailscale IP 和 MagicDNS 主机名，并立即通过该路径进行验证检查。

# [2.1.5] - 2026-06-26

## PasteFlow Dev MVP
基于 CrossPaste 开源代码库的 PasteFlow Dev 桌面 MVP 初始版本。

## 独立开发身份
PasteFlow Dev 已使用独立的应用名称、bundle ID、存储目录、网络端口、快捷键和 native messaging 标识，便于与 CrossPaste 分开开发。

## 本地剪贴板管理基线
已在这台 Mac 上验证剪贴板捕获、主快捷键启动、搜索快捷键启动、重启行为和 macOS Accessibility 授权流程。

## macOS 可靠性整理
已在这台 Mac 上检查搜索粘贴、重启行为、截图快捷键捕获和 Skitch 导出捕获。Windows、Linux 和第二台设备同步检查会等相关环境可用后继续。

## MVP 运行模式
Settings、About、Change Log 和帮助链接已指向 PasteFlow Dev 项目上下文。更新、公开发布交付和 Share 页面工作会等产品方向更清晰后再继续。
