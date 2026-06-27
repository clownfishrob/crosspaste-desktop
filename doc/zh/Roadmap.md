# 项目路线图

## PasteFlow Dev MVP 后续事项

### 已确定的 MVP 决策

- 这台 Mac 上的本地冒烟测试已通过：启动、剪贴板捕获、快捷键、重启行为和辅助功能授权均可用。
- 更新交付暂时暂停，直到产品方向和发布渠道更清晰。
- GitHub CI/构建暂时只允许手动触发，不会在 push 或 pull request 时自动运行。
- 公开发布和 beta 发布工作流暂时移除。
- Sponsors 更新、Issue 翻译和 AI review 自动化暂时移除。
- GitHub 仓库保持最小化：Issue 模板、Dependabot 和手动 CI。

### 暂缓的产品工作

- 根据 PasteFlow Dev 的产品方向更新 Share 设置页面。
- 等 PasteFlow Dev 的分享流程和文案确定后，再重新启用 Share 菜单。
- 等更新元数据、签名和发布交付都归 PasteFlow Dev 所有后，再重新启用 Check for updates。
- 在更新控件重新出现之前，重命名或替换仍带有 CrossPaste 命名的旧更新基础设施。
- 等公开定位更清晰后，再重写 GitHub README。
- 有第二台设备可用时，完成多设备测试。

以下是我们当前的项目路线图。我们不再把每个功能强行绑定到具体的版本号上，而是按"已发布"与"接下来要做"两类来组织：

```mermaid
graph LR
    B[当前已支持<br/>v2.0] --> U[后续规划]

    B -.- B1[OCR<br/>v1.2.3 起]
    B -.- B2[MCP 服务<br/>v1.2.8 起]
    B -.- B3[Chrome 扩展<br/>v2.0 起]

    U -.- U1[命令行模式]
    U -.- U2[插件系统]

    classDef current fill:#f9d5e5,stroke:#333,stroke-width:2px
    classDef upcoming fill:#eeac99,stroke:#333,stroke-width:2px
    class B current
    class U upcoming
```

## 当前已支持

- **OCR**（自 v1.2.3 起）：本地从图片中提取文字，全程离线，不产生任何网络请求。
- **MCP 服务**（自 v1.2.8 起）：通过 Model Context Protocol 把粘贴板历史暴露给 AI 助手使用。
- **Chrome 扩展**（自 v2.0 起）：让浏览器与已配对的 PasteFlow Dev 设备之间同步粘贴板内容。

## 后续规划

- **命令行模式**：让 PasteFlow Dev 可以在终端和 Shell 脚本中被驱动。
- **插件系统**：让社区可以为 PasteFlow Dev 扩展自定义粘贴类型与集成能力。

**注意**：此路线图代表了我们当前的开发计划和项目愿景。随着开发的进行，我们可能会根据社区反馈、技术进步和不断变化的优先级进行调整。我们欢迎社区参与和贡献！如果您对帮助塑造这个项目的未来感兴趣，请考虑加入我们的社区，为项目的成长贡献力量。

您的意见和贡献可以对项目的发展产生重大影响。无论是通过代码贡献、功能建议，还是帮助完善文档，都有多种方式可以参与其中。查看我们的[贡献指南](Contributing.md)，了解更多关于如何参与的信息。

让我们一起努力，让这个项目变的更棒！
