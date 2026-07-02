# Clipboard Manager MVP

This fork keeps CrossPaste's local-first, cross-platform clipboard engine and adds a scoped clipboard-manager MVP under the development identity **PasteFlow Dev**.

## What Was Added

- Development identity isolation:
  - App display name: `PasteFlow Dev`
  - macOS bundle ID: `com.robdev.pasteflow.dev`
  - Native messaging host: `com.robdev.pasteflow.dev.desktop`
  - Bonjour service type: `_pasteflowDevService._tcp.local.`
  - Bonjour service name prefix: `pasteflow-dev`
  - Default port: `13139`
  - Development data/config directory: `.pasteflow-dev`
  - macOS app support directory: `~/Library/Application Support/PasteFlow Dev`
  - Windows/Linux default data directory: `.pasteflow-dev`
  - Default main-window shortcut: Meta/Win+Shift+0
  - Default search-overlay shortcut: Meta/Win+Shift+Minus
- Local-only defaults:
  - Autostart is off by default.
  - Device discovery is off by default.
  - Sync content-type toggles are off by default.
  - File/image sync size limit defaults to 20 MB.
- Network settings now include a `Local-only mode` switch that disables discovery, encrypted sync, remote pairing wakeups, and all sync content types together.
- The shortcut overlay now supports 10 quick paste slots: Ctrl+1 through Ctrl+9 and Ctrl+0 for slot 10.
- Preview titles now show the paste category even when an item has a custom name.
- Image previews label likely screenshots and long screenshots.
- Cleanup now enforces a 1,000-item cap for untagged history.
- Text capture skips items over 2 MB.
- Clipboard image auto-save skips images over 20 MB.

## What Remains From CrossPaste

- Kotlin Multiplatform and Compose Desktop app structure.
- Local SQLDelight database and local file storage.
- Rich clipboard support for text, URL, HTML, RTF, image, file, folder, and color data.
- Search-first overlay and full app views.
- Paste-back workflow through the existing platform-specific window managers.
- Encrypted LAN sync, pairing, device management, and sync settings.
- Browser extension/native messaging architecture.
- CLI, e2e scaffolding, and mobile-oriented shared modules.
- AGPL-3.0 license and upstream attribution.

## Pinned Collections

Pinned collections use the existing tag system. A collection is a named tag, and assigning that tag to a paste item pins it into that collection.

Tagged items are excluded from normal cleanup queries, so pinned collection items do not expire during time-based, storage-threshold, or history-cap cleanup. This means collections can hold reusable snippets such as HTML blocks, local SEO details, replies, development snippets, images, and file/path entries using the paste types CrossPaste already supports.

No database schema migration was needed for this MVP because `TagEntity` and `PasteTagEntity` already model named collections and item membership.

## Current Limits

- Untagged history cap: 1,000 items.
- Untagged image retention: 60 days.
- Untagged file retention: 60 days.
- Text item max: 2 MB.
- Clipboard image auto-save max: 20 MB.
- File backup size limit: 20 MB.
- File sync size limit: 20 MB.

Files and folders are not silently duplicated when they exceed the backup limit; the existing file plugin stores references instead.

## Privacy

The existing source exclusion service remains in place. It can skip capture from excluded apps through the current source-control settings flow.

Future privacy work should add pause-capture controls and secret detection for password managers, 2FA codes, API keys, private keys, recovery phrases, and other sensitive snippets.

## Paste-Back Behavior

Paste-back still uses the existing CrossPaste flow:

- The selected item is written back to the local pasteboard.
- The search overlay hides.
- The OS-specific window manager returns focus and injects the configured paste shortcut.

Current platform behavior remains OS-specific. macOS uses the Swift bridge and accessibility-assisted focus/paste behavior. Windows and Linux use their existing focus and synthetic paste paths. Reliability remains preferred over clever transforms.

## Development Isolation Notes

PasteFlow Dev should not overwrite, migrate, delete, or reuse a production CrossPaste database or configuration during development. The fork now uses separate runtime paths, metadata, database/storage roots, keychain service prefix, native messaging host, mDNS identifiers, package metadata, and default port.

The code package names remain `com.crosspaste` to avoid a large, risky rename across shared, desktop, CLI, web, and mobile-oriented modules.

## Roadmap

### Phase 1 - MVP

- Development app identity isolation
- Quick-access overlay
- Search-first history
- Pinned collections with 10 quick slots
- Basic categories
- Screenshot labelling
- Local-only mode
- Sensible history/storage limits

### Phase 2 - UX polish

- Better previews for text, code, HTML, links, images, files, colours
  — done: cards show character counts (text/HTML/RTF), format/resolution/size
  and screenshot labels (images), and name/size (files)
- Keyboard shortcuts for collection slots
  — done: slots follow the active collection (tag) filter; slot numbers are
  now always visible on the first 10 cards (highlighted while Ctrl is held)
  and the top bar advertises Ctrl+1…9 alongside the existing paste hints
- Better onboarding for macOS Accessibility permissions — open
- Better empty states and settings explanations
  — partial: the search overlay now distinguishes "nothing captured yet" from
  "no matches for your search or filters"; broader settings copy review open
- Import/export pinned collections
  — done: the export screen can export a single collection; the bundle
  carries `collection.info` (tag name + colour) and import recreates the
  collection and re-pins the imported items
- Theme polish without copying any commercial app — open

### Phase 3 - Privacy and safety

- Excluded apps list improvements
- Pause capture for 1 minute, 5 minutes, or until resumed
- Secret detection for API keys, private keys, passwords, 2FA codes, recovery phrases
- Optional encrypted local database
- Per-collection sync controls

### Phase 4 - Cross-platform reliability

- OS-specific paste-back improvements for macOS, Windows, and Linux
- Better file/folder behaviour
- Improved screenshot detection per platform
- Performance work for large histories
- Packaging, signing, auto-update review

### Phase 5 - Advanced features

- Optional OCR for screenshots/images
- Snippet variables/templates
- Smart collections
- Browser extension integration improvements
- Mobile pinned collection support
- Optional self-hosted sync or relay only if it preserves the local-first privacy model
