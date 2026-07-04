# MVP 完成清单

此清单用于定义 PasteFlow Dev macOS MVP 在正式标记为完成前还剩哪些事项。

## 已完成

- 开发应用身份已与 CrossPaste 分离。
- 本地数据和配置路径已与 CrossPaste 分离。
- 默认快捷键已与 CrossPaste 分离。
- 开发网络端口已与 CrossPaste 分离。
- Native messaging 标识已与 CrossPaste 分离。
- macOS Accessibility 权限提示已针对快捷键使用做过调整。
- Check for updates 入口仍从 MVP 应用界面隐藏；Share 页面已改为最小化的项目分享界面（复制、邮件、仓库链接），社交平台分享仍然推迟。
- 更新交付已暂停，当前不宣传公开 updater 通道。
- GitHub 自动化已缩减为手动 CI、Dependabot 和 issue templates。
- README、changelog、roadmap、About、settings、扩展和联系文案已重置为 MVP 上下文。
- 旧的上游 updater runbook 和辅助脚本已移除。
- 本机 macOS 已验证启动、剪贴板捕获、快捷键、重启行为和 Accessibility 授权。
- 桌面测试和扩展生产构建已在开发机器上通过。
- 本地 macOS 应用包已可通过 `:app:createDistributable` 成功构建。
- 2026-07-04 阶段 2-3 与 macOS 稳定性修复后的完整复验：lint、桌面测试、扩展测试/构建、
  应用包全部通过，且 `./smoke-test.sh` 现在可以无人值守通过（安全存储重构解决了
  首次运行的钥匙串挂起问题）。
- 2026-07-02 自动化复验：`docs/clipboard-manager-mvp.md` 全部声明已在代码中确认；
  ktlintCheck、1287 个桌面测试、79 个扩展测试、扩展生产构建和
  `:app:createDistributable` 全部通过（详见 `docs/HANDOFF.md`）。

## 标记 MVP 完成前仍需验证

- 在这台 Mac 上执行最后一次手动已安装应用冒烟测试
  （自动化启动检查已通过；以下交互式确认仍待完成）：
  - 启动已安装的 `PasteFlow Dev`。
  - 确认剪贴板捕获。
  - 确认主快捷键。
  - 确认搜索快捷键。
  - 确认重启行为。
  - 确认 macOS Accessibility 授权后提示不会反复出现。
- 有第二台机器可用时，完成一次双机测试：
  - 在另一台机器上安装或运行 PasteFlow Dev。
  - 确认设备可以被发现或手动添加。
  - 确认至少一个方向的基础剪贴板同步可用。

## 非 MVP 阻塞项

以下事项会等产品方向更清晰后再处理：

- 公开发布/社交分享（基础 Share 页面重做已完成）。
- 公开更新/发布交付。
- 签名、updater 元数据和公开打包。
- 公开定位、截图和 polished release README。
- 对继承自 CrossPaste 的内部 package/class 名称进行更深入重命名。
