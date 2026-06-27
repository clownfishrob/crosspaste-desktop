# PasteFlow Dev

PasteFlow Dev is an experimental desktop clipboard manager based on the open-source
[CrossPaste](https://github.com/CrossPaste/crosspaste-desktop) codebase.

The current goal is a practical macOS MVP that can run separately from CrossPaste while
the product direction is refined.

[![License: AGPL-3.0](https://img.shields.io/badge/License-AGPL--3.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-desktop-blue.svg)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/UI-Compose%20Multiplatform-blue.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)

## Current MVP status

PasteFlow Dev has been separated from CrossPaste enough to install and run side by side
for local development:

- App name: PasteFlow Dev
- macOS bundle identity separated from CrossPaste
- Local app data folder separated from CrossPaste
- Development network port separated from CrossPaste
- Shortcut defaults separated from CrossPaste
- Native messaging identifiers separated from CrossPaste
- macOS Accessibility prompt handling adjusted for shortcut use

Validated locally on macOS:

- Clipboard capture
- Main shortcut launch
- Search shortcut launch
- Restart behavior
- macOS Accessibility authorization flow

## Repository status

This fork currently keeps GitHub automation deliberately small:

- CI is manual-only while product direction is being decided
- Dependabot remains enabled for Gradle dependency visibility
- Release publishing, beta publishing, sponsor updates, issue translation, and AI review workflows are disabled

There is no updater or public release channel until the product direction is clearer.

## Deferred for later

These areas are intentionally hidden or still need a PasteFlow Dev pass:

- Share page and share menu
- Check for updates / release delivery
- Legacy updater implementation names still inherited from CrossPaste
- Public positioning, screenshots, and release packaging
- Multi-device testing on additional machines

See [doc/en/Roadmap.md](doc/en/Roadmap.md) for the active follow-up list.
See [doc/en/MVPChecklist.md](doc/en/MVPChecklist.md) for the remaining MVP completion checks.

## Development setup

Clone this fork:

```bash
git clone https://github.com/clownfishrob/crosspaste-desktop.git
cd crosspaste-desktop
```

Run the desktop app in development:

```bash
./gradlew app:run -PappEnv=BETA
```

Run desktop tests:

```bash
./gradlew :app:desktopTest -PappEnv=BETA
```

Create the macOS desktop distributable:

```bash
./gradlew :app:createDistributable -PappEnv=BETA
```

The generated app is written under:

```text
app/build/compose/binaries/main/app/
```

First builds may download Gradle, Kotlin, Compose, and JetBrains Runtime dependencies.
JDK 21 is recommended for local development.

## Local install on macOS

After creating the distributable, the app bundle can be copied into the user
Applications folder:

```bash
rm -rf "$HOME/Applications/pasteflow-dev.app"
ditto "app/build/compose/binaries/main/app/pasteflow-dev.app" "$HOME/Applications/pasteflow-dev.app"
open -n "$HOME/Applications/pasteflow-dev.app"
```

macOS global shortcuts require Accessibility permission. Open:

```text
System Settings -> Privacy & Security -> Accessibility
```

Then enable `pasteflow-dev`.

## Attribution

PasteFlow Dev is a fork of CrossPaste. The original project provides the core
cross-platform clipboard, sync, storage, UI, and extension foundations.

Original project:

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
