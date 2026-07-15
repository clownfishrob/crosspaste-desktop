# PasteFlow Dev

PasteFlow Dev is an experimental desktop clipboard manager forked from the
open-source [CrossPaste](https://github.com/CrossPaste/crosspaste-desktop)
project.

The current aim is a practical macOS-first MVP: a local clipboard history,
fast search overlay, pinned collections, sensible privacy defaults, and a
separate development identity so it can run alongside CrossPaste without
sharing app data.

[![License: AGPL-3.0](https://img.shields.io/badge/License-AGPL--3.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-desktop-blue.svg)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/UI-Compose%20Multiplatform-blue.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)

## MVP Status

PasteFlow Dev is usable as a local macOS clipboard-manager MVP. The current
build has been validated on the maintainer's Mac for:

- Clipboard capture for text, links, HTML, RTF, images, files, folders, and colours
- Centered search overlay and keyboard-driven paste
- Pinned collections through the existing tag system
- 10 quick paste slots for the active result list
- Desktop screenshot and Skitch-export capture
- macOS Accessibility onboarding for global shortcuts and paste-back
- Local-only defaults with discovery and sync off by default
- Secret detection for high-confidence keys and tokens
- Import/export for pinned collections
- Manual macOS package creation

The app is still pre-release. It can check the PasteFlow Dev update metadata
and open GitHub releases, but macOS installation is still manual until release
signing/notarization is decided.

## Development Identity

This fork is intentionally isolated from CrossPaste during development:

- App name: `PasteFlow Dev`
- macOS bundle ID: `com.robdev.pasteflow.dev`
- macOS app support folder: `~/Library/Application Support/PasteFlow Dev`
- Development data folder: `.pasteflow-dev`
- Bonjour service type: `_pasteflowDevService._tcp.local.`
- Default local port: `13139`
- Native messaging host: `com.robdev.pasteflow.dev.desktop`
- Default main shortcut: `Meta/Win+Shift+0`
- Default search shortcut: `Meta/Win+Shift+Minus`

The Kotlin package names remain `com.crosspaste` to avoid a risky broad rename
across the desktop, shared, CLI, extension, and mobile-oriented modules.

## Current Gaps

These items are intentionally paused or still awaiting hardware/product
decisions:

- Second-machine sync/manual-add testing
- Windows and Linux reliability passes
- Public release signing, notarization, and automatic update installation
- Public launch/social sharing beyond the basic project Share page
- GitHub/product screenshots and broader public positioning
- Optional OCR, snippet templates, smart collections, and other advanced ideas

See [docs/clipboard-manager-mvp.md](docs/clipboard-manager-mvp.md) for the
working roadmap and [doc/en/MVPChecklist.md](doc/en/MVPChecklist.md) for the
remaining MVP checks.

## Build Locally

Clone this fork:

```bash
git clone https://github.com/clownfishrob/crosspaste-desktop.git
cd crosspaste-desktop
```

Use JDK 21. On this development Mac, the repo uses the bundled JetBrains Runtime:

```bash
export JAVA_HOME="$PWD/app/jbr/extracted/jbrsdk-21.0.9-osx-aarch64-b1163.94/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

Run the desktop app:

```bash
./gradlew -PappEnv=BETA :app:run
```

Run the focused desktop test suite:

```bash
./gradlew -PappEnv=BETA :app:desktopTest
```

Create the macOS package:

```bash
./gradlew -PappEnv=BETA :app:packageDistributionForCurrentOS
```

The generated app and DMG are written under:

```text
app/build/compose/binaries/main/
```

## Local macOS Install

PasteFlow Dev currently targets macOS 12.0 or newer.

For local manual testing:

```bash
rm -rf "$HOME/Applications/pasteflow-dev.app"
ditto "app/build/compose/binaries/main/app/pasteflow-dev.app" "$HOME/Applications/pasteflow-dev.app"
codesign --force --deep --sign - "$HOME/Applications/pasteflow-dev.app"
open -n "$HOME/Applications/pasteflow-dev.app"
```

macOS paste-back and global shortcuts require Accessibility permission:

```text
System Settings -> Privacy & Security -> Accessibility
```

Enable `pasteflow-dev`. If macOS keeps prompting after a local rebuild, remove
and re-add the app in Accessibility, then quit and reopen PasteFlow Dev.

## Repository Automation

GitHub automation is deliberately small while product direction is being
decided:

- Manual CI only
- Dependabot for Gradle dependency visibility
- Release publishing disabled
- Sponsor updates disabled
- Issue translation and AI review workflows disabled

## Attribution

PasteFlow Dev is a fork of CrossPaste. The original project provides the core
cross-platform clipboard, sync, storage, UI, extension, and shared-module
foundations.

- Upstream repository: [CrossPaste/crosspaste-desktop](https://github.com/CrossPaste/crosspaste-desktop)
- Upstream website: [crosspaste.com](https://crosspaste.com)

## License

This repository is licensed under the GNU Affero General Public License v3.0.
See [LICENSE](LICENSE).

Because this is a fork of an AGPL-3.0 project, derivative work must continue to
respect the AGPL-3.0 license terms.

## Contact

For PasteFlow Dev questions, contact:

```text
rob@ngduk.co.uk
```
