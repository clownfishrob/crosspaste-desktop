# 项目路线图

此路线图用于记录 PasteFlow Dev MVP fork。它不会照搬上游 CrossPaste 的发布路线图。

## 已确定的 MVP 决策

- 这台 Mac 上的本地冒烟测试已通过：启动、剪贴板捕获、快捷键、重启行为和辅助功能授权均可用。
- 更新交付暂时暂停，直到产品方向和发布渠道更清晰。
- GitHub CI/构建暂时只允许手动触发，不会在 push 或 pull request 时自动运行。
- 公开发布和 beta 发布工作流暂时移除。
- Sponsors 更新、Issue 翻译和 AI review 自动化暂时移除。
- GitHub 仓库保持最小化：Issue 模板、Dependabot 和手动 CI。

## 当前可用

- 开发应用身份已与 CrossPaste 分离。
- 本地数据和配置路径已与 CrossPaste 分离。
- 默认快捷键已与 CrossPaste 分离。
- 开发网络端口已与 CrossPaste 分离。
- Native messaging 标识已与 CrossPaste 分离。
- macOS Accessibility 权限提示已针对快捷键使用做过调整。
- 已记录本地 macOS 手动构建和安装流程。
- GitHub README、changelog 和应用内可见文案已重置为 MVP 上下文。
- 扩展端设置、配对和通知文案已重置为 MVP 上下文。

## 下一步重点

- 让 MVP 作为本地桌面剪贴板管理器保持稳定。
- 继续减少不属于致谢、包命名或继承内部结构的可见 CrossPaste 文案。
- 在实际使用中遇到问题时，继续改善设置标签和空状态。
- 持续在路线图和 changelog 中记录产品决策。

## 暂缓的产品工作

- 根据 PasteFlow Dev 的产品方向更新 Share 设置页面。
- 等 PasteFlow Dev 的分享流程和文案确定后，再重新启用 Share 菜单。
- 等更新元数据、签名和发布交付都归 PasteFlow Dev 所有后，再重新启用 Check for updates。
- 在更新控件重新出现之前，重命名或替换仍带有 CrossPaste 命名的旧更新基础设施。
- 等产品方向更清晰后，再补充公开定位、截图和发布说明。
- 有第二台设备可用时，完成多设备测试。
