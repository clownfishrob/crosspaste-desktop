# Session Handoff — macOS Reliability Pass (2026-07-03)

This is the latest state before handing the repo to another agent or human.
Work was focused on this Mac only; Windows/Linux reliability remains deferred
until those machines are available.

## What Changed

- **Center overlay bubble placement**: the bubble now anchors to the vertical
  center search list instead of the old horizontal strip geometry. The math is
  isolated in `BubbleWindowPosition.kt` with a focused desktop test.
- **Search Enter handling**: pressing Enter in the center search overlay now
  pastes the selected item even when the search field has focus. A regression
  was fixed where native key-code fallback could treat normal typing (for
  example `google`) as a paste command; only Compose `Key.Enter` now submits.
- **macOS paste-back timing**: the search window now hides first, returns focus
  to the previous app, waits briefly, then sends the paste command through the
  existing app-level paste path. The Swift bridge also now posts modifier key
  events separately from the action key.
- **Desktop screenshot/Skitch capture**: on macOS, recent image files created
  on the Desktop (`png`, `jpg`, `jpeg`, `heic`) are watched and passed into
  the existing file/image capture pipeline. This covers screenshot-key saves
  and Skitch exports.
- **Secure-store startup**: the macOS dev build now uses a local secure-store
  key file for PasteFlow Dev instead of blocking on first-run Keychain access.
  This keeps local MVP smoke tests from hanging before the UI appears.
- **Local install/signing note**: the installed app at
  `~/Applications/pasteflow-dev.app` must be signed after all bundled native
  libraries exist. If the bundle seal changes, macOS Accessibility can show
  the app as enabled while the runtime check still reports denied. Resetting
  Accessibility for `com.robdev.pasteflow.dev`, re-enabling PasteFlow Dev, and
  reopening the app fixed it on this Mac.
- **Generated/local output hygiene**: `app/.pasteflow-dev/` and
  `graphify-out/` are ignored so local runtime data and generated graph output
  are not committed.

## Verified on This Mac

- Center search display stayed vertical/centered after reopening.
- Search typing no longer triggers paste.
- Enter paste works into Notes and TextEdit.
- Paste still works after quitting and reopening PasteFlow Dev.
- macOS screenshot key output and Skitch export both add items to PasteFlow.
- App bundle validation passed after re-signing the local install:
  `codesign -vvv --deep --strict ~/Applications/pasteflow-dev.app`.
- Build/check command passed before this handoff:
  `./gradlew -PappEnv=BETA :app:desktopTest --tests com.crosspaste.ui.search.center.CenterSearchKeyTest :app:ktlintDesktopMainSourceSetCheck :app:ktlintDesktopTestSourceSetCheck :app:packageDistributionForCurrentOS`.

## Remaining Before MVP Complete

- Second-machine sync/manual-add test remains blocked until another device is
  available.
- Windows/Linux Phase 4 reliability remains deferred.
- Update checking is re-enabled against the PasteFlow Dev metadata file and
  opens GitHub releases. macOS installation remains manual until signing and
  notarization are decided.
- GitHub README, app-facing changelog notes, and About footer have had the
  current PasteFlow Dev MVP refresh.
- Share page has been reworked into a basic PasteFlow Dev project-sharing
  surface; public launch/social sharing remains deferred.
- Deeper inherited CrossPaste internal naming remains a later cleanup item.

---

# Session Handoff — PasteFlow Dev MVP Verification + Phase 2 Start (2026-07-02)

## Two-Pane Center Search Overlay (Latest Work)

After seeing the incremental slot-badge changes, the owner shared a reference
layout again and asked for the overlay to be rebuilt to match its structure.
Delivered a new compact centered search panel replacing the full-width bottom
strip:

- New package `app/src/desktopMain/kotlin/com/crosspaste/ui/search/center/`
  (`CenterSearchWindowContent.kt`): category icon strip on top (All + one icon
  per paste type, using ThemeExt icon data), vertical item list on the left
  (slot number, type icon, snippet title from user-edit name or
  pasteSearchContent), full preview on the right (reuses `SidePreviewView`,
  which includes the title bar), search field + paste hints on the bottom.
- Keyboard model: search input holds focus; the root `onPreviewKeyEvent`
  intercepts Up/Down (selection), Enter (paste), Esc (hide), and
  Ctrl+1…9/0 (slot paste). Slot numbers in rows highlight while Ctrl is held.
- Window geometry: `DesktopAppSize.getSearchWindowState` now returns a
  centered 780×520dp floating panel (`centerSearchWindowSize` and friends);
  `SearchWindow.kt` uses `centerSearchSlideOffset` (48dp) for the show/hide
  slide and composes `CenterSearchWindowContent`.
- The old strip implementation (`ui/search/side/`, `ui/paste/side/`) is left
  intact — switching back is a one-line change in `SearchWindow.kt` plus
  reverting `getSearchWindowState`. `SideSearchWindowContent` is now unused
  but compiled.

Later fixes in the same session:
- Row context menus restored (right-click → pin/tag, copy, open, delete) via
  `PasteContextMenuView` + `sidePasteMenuItemsProvider`.
- Main window menu gained a top "Search Pasteboard" entry that hides the main
  window and reopens the overlay — previously there was no route back to the
  results from Settings (the results only exist in the overlay).
- Per-row pin indicators: rows show a filled pin icon when the item belongs
  to any collection. Backed by a new reactive DAO API
  `PasteTagDao.getTaggedPasteIdsFlow(ids)` (new `getTaggedPasteIds` query in
  `TagDatabase.sq`), which re-emits on tag-membership changes; covered by two
  new tests in `PasteDaoTest`.

Accessibility onboarding (Phase 2 item, same session): the
`GrantAccessibilityDialog` existed but had no call site — on a fresh macOS
install shortcuts silently failed. Added
`ui/settings/AccessibilitySettingsContentView.kt`:
`AccessibilityDialogHost()` (composed in `CrossPasteMainWindowContent`)
shows the dialog when the app launched without the permission
(`DesktopAppLaunchState.accessibilityPermissions`), gated by the
`showGrantAccessibility` config and a session-only dismiss; and a macOS-only
"Permissions" settings section with a live-polling status row that opens the
Privacy & Accessibility pane while not granted. New i18n keys:
`accessibility_permission`, `accessibility_permission_row_desc`, `granted`,
`not_granted`, `permissions`; `accessibility_permission_desc` copy now
explains shortcuts + paste-back and the restart requirement.

Theme polish pass (closing Phase 2): pin indicators now use each item's
primary collection colour via `PasteTagDao.getPasteTagColorsFlow` (replaced
`getTaggedPasteIdsFlow`; new `getPasteTagColors` join query ordered by tag
sort order — first colour per paste id wins); overlay rows and the top-bar
filters gained hover states (`HoverableFilterBox`); selected rows use
`secondaryContainer`; and the macOS acrylic is no longer applied behind the
opaque panel (call `MacAcrylicEffect` without `isDark` — keeps the popup
window level, drops the corner bleed). Deferred by choice: accent-colour
picker, open/close motion changes.

Phase 3 start (privacy): `PauseCaptureService` (app/commonMain) stops the
pasteboard monitor without touching the persisted listening setting; overlay
top bar hosts the pause menu (1m/5m/until resumed) and a warning resume
pill; flipping the settings listening toggle back on clears any pause
(observer drops the subscription-time emission — see the drop(1) comment).
`SecretDetector` (app/commonMain, pure + heavily tested) gates
`DesktopTransferableConsumer.consume` behind the new `enableSecretDetection`
config (toggle in pasteboard settings); skipped items trigger a warning
notification and never reach the DB. TestAppConfig.copy now handles
`enablePasteboardListening`. Excluded-apps: item context menus gained
"Stop capturing from <app>" (DesktopPasteMenuService +
DesktopSourceExclusionService). Phase 3 closed 2026-07-03 with two recorded
deferrals (encrypted local DB, per-collection sync controls) — see the
roadmap in docs/clipboard-manager-mvp.md for the reasoning.

Known follow-ups for the new layout:
- Center overlay bubble positioning was fixed in the macOS Phase 4 pass:
  `BubbleWindow.kt` now anchors to the vertical list column and the positioning
  math lives in `BubbleWindowPosition.kt` with a focused desktop test.
- Windows 11 blur still fills the square window rect behind the rounded
  panel (same corner bleed the macOS side had); needs a Windows machine to
  verify any change.
- Multi-select: restored — shift-click works in the new list, matching the
  old strip; double-tap quick-paste is suppressed while Shift is held.

---

## Phase 2 Work (Second Half of This Session)

The owner shared a reference screenshot of a commercial clipboard manager and
asked for Phase 2 to start, UI first. Treated strictly as a functional
reference (the MVP doc forbids copying any commercial app's look). Delivered:

1. **Persistent quick-slot numbers** — the first 10 overlay cards now always
   show their `#N` slot badge (quiet chip), switching to the accent color
   while Ctrl is held. Since quick slots operate on the filtered result list,
   selecting a collection (tag) chip makes Ctrl+1…0 paste from that
   collection — this is the "keyboard shortcuts for collection slots" item.
   Files: `Top9IndexView.kt`, `SidePreviewView.kt`,
   `SidePasteboardContentView.kt`, plus a `Ctrl+1…9` hint chip in
   `QuickPasteView.kt`.
2. **Single-collection export/import** — the export screen
   (`PasteExportContentView.kt`) gained an "Export a single collection (tag)"
   switch with a tag picker. The SQL export queries
   (`PasteDatabase.sq: getBatchExportPasteData/getExportNum`) accept a
   nullable `tagId` that takes precedence over `onlyTagged`. Export bundles
   `collection.info` (JSON: tag name + colour; see `PasteCollectionInfo.kt`)
   and `PasteImportService.restoreCollection()` finds-or-creates the tag by
   name and re-pins every imported item. Both services now take `PasteTagDao`
   (Koin wiring updated in `DesktopPasteComponentModule.kt`).
3. **Empty-state fix** — `PasteEmptyScreenView` takes a `messageKey`; the
   overlay shows `no_search_results` when a search term, type filter, or tag
   filter is active instead of implying the history is empty.
4. **Preview metadata** — verified already implemented upstream of this
   session (char counts, image format/resolution/size, file name/size); no
   changes needed.

New i18n keys (en + zh; other locales fall back to English):
`export_single_collection`, `no_search_results`.

Tests: two new tests in `PasteExportImportServiceTest` cover the
collection.info export bundling and the import-side collection restore, plus
a `PasteCollectionInfo` JSON round-trip. Full suite re-run green (1289 app
tests, 0 failures), ktlintCheck green, `:app:createDistributable` green.

Phase 2 items still open: macOS Accessibility onboarding, broader settings
copy review, theme polish. See the roadmap annotations in
`docs/clipboard-manager-mvp.md`.

---

# Session Handoff — PasteFlow Dev MVP Verification (2026-07-02, first half)

This note lets any harness (Claude Code, Codex, or a human) pick up the
PasteFlow Dev MVP work without re-deriving context. Read
`docs/clipboard-manager-mvp.md` for the MVP scope and
`doc/en/MVPChecklist.md` for the completion checklist.

## What This Session Did

1. **Verified every claim in `docs/clipboard-manager-mvp.md` against the code.**
   All identity, default, and limit claims are implemented:
   - Identity constants live in
     `app/src/desktopMain/kotlin/com/crosspaste/app/DesktopAppIdentity.kt`:
     display name `PasteFlow Dev`, bundle `com.robdev.pasteflow.dev`,
     native messaging host `com.robdev.pasteflow.dev.desktop`, Bonjour type
     `_pasteflowDevService._tcp.local.`, prefix `pasteflow-dev`, port `13139`,
     data dir `.pasteflow-dev`, app support dir `PasteFlow Dev`,
     main shortcut `42+3675+48` (Meta+Shift+0), search shortcut `42+3675+45`
     (Meta+Shift+Minus), `maxHistoryItems = 1000`, `maxTextBytes = 2 MB`,
     `maxImageBytes = 20 MB`.
   - Local-only defaults in
     `app/src/desktopMain/kotlin/com/crosspaste/config/DesktopAppConfig.kt`:
     `enableAutoStartUp = false`, `enableDiscovery = false`, all
     `enableSync* = false`, `maxBackupFileSize = 20`, `maxSyncFileSize = 20`,
     image/file clean time `TWO_MONTH`.
   - `Local-only mode` switch:
     `app/src/commonMain/kotlin/com/crosspaste/ui/settings/NetworkSettingsContentView.kt`
     (i18n key `local_only_mode`).
   - Quick paste slots: `quickSlotIndex()` in
     `app/src/desktopMain/kotlin/com/crosspaste/ui/paste/side/SidePasteboardContentView.kt`,
     covered by `QuickSlotIndexTest`.
   - Screenshot labelling: `ScreenshotLabel` and `SmartImageDisplayStrategy`
     under `.../ui/paste/side/preview/` and `.../ui/base/`.
   - Preview category titles: `SidePasteTitleView.kt` joins category + time
     into the detail line even when the item has a custom name.
   - Size limit enforcement: `DesktopTextTypePlugin` (2 MB text skip) and
     `DesktopImageTypePlugin.isWithinImageLimit` (20 MB image skip).
   - Untagged-only cleanup: SQLDelight queries such as
     `getOldestUntaggedCreateTimeAndSize` scope cleanup to untagged items.

2. **Fixed one real build bug** (the only code change this session):
   `shared/build.gradle.kts` — the `ktlint { }` block had no exclusion filter,
   so `./gradlew ktlintCheck` failed on SQLDelight-generated code
   (`shared/build/generated/sqldelight/...`). Added the same filter the app
   module uses (`generated`, `db`, `Database.kt`, `DatabaseImpl.kt`).
   Note: after changing the filter, a plain re-run can still fail from a
   cached lint result; `--rerun-tasks` (or a clean) picks up the fix.

3. **Ran the test matrix** (all green, executed fresh with `--rerun-tasks`,
   not from cache):
   - `./gradlew ktlintCheck` — passes after the fix above.
   - `./gradlew app:desktopTest shared:desktopTest core:desktopTest
     shared-ui:desktopTest` — 1287 tests, 0 failures
     (app: 1286, shared: 1; core and shared-ui have no desktop tests).
   - Web extension (`web/`): `npm test` — 79 tests pass; `npm run build` —
     production bundle builds cleanly.
   - `./gradlew :app:createDistributable` — macOS `.app` builds:
     `app/build/compose/binaries/main/app/pasteflow-dev.app`.
   - `./smoke-test.sh` — **FAILED in this session, but not because of a code
     regression.** See "Smoke Test Finding" below.

## Smoke Test Finding (Needs an Interactive Session)

`./smoke-test.sh` timed out waiting for the `PasteFlow Dev started` log line.
The app boots normally (DB pool up, network interfaces enumerated) until
`MacosSecureStoreFactory.createSecureStore()`
(`app/src/desktopMain/kotlin/com/crosspaste/secure/MacosSecureStoreFactory.kt`).
The log shows `Generate secureKeyPair` and then goes silent — the next step is
`MacosKeychainHelper.getPassword(...)`, and neither of its follow-up log lines
(`Found password in keychain` / `Not found password in keychain`) ever
appeared. First-run keychain access from an unsigned JVM triggers an
interactive macOS keychain authorization, which a non-interactive/automated
session cannot answer, so `startApplication()` blocks until the script kills
it. No `secure.data` was persisted, so the next run will hit the same path.

What this means for the next session:
- Run `./smoke-test.sh` once from a real interactive desktop session and
  approve the keychain prompt if it appears. After `secure.data` +
  keychain entry exist, subsequent boots should pass unattended.
- This matches the still-open checklist item "hands-on installed-app smoke
  test" — it genuinely needs a human at the machine.
- If unattended smoke testing matters later, consider deferring secure-store
  creation until sync is actually enabled (local-only mode never needs it),
  or an `--ephemeral-keys` test flag. Do not weaken keychain usage for real
  runs.

## Environment Gotchas (Save Yourself an Hour)

- **No system Java on this machine.** Use the bundled JBR:
  `export JAVA_HOME="$PWD/app/jbr/extracted/jbrsdk-21.0.9-osx-aarch64-b1163.94/Contents/Home"`
  before any `./gradlew` call.
- **`./gradlew test` does not exist** (CLAUDE.md is stale on this point).
  Use the per-module `desktopTest` tasks listed above.
- `e2e:desktopTest` is excluded from the routine matrix — it drives the real
  app and is not a unit suite.
- `smoke-test.sh` is local-only by design (not CI); it kills only
  `appEnv=DEVELOPMENT` JVMs so a production CrossPaste is never touched.

## What Remains Before Calling the MVP Complete

From `doc/en/MVPChecklist.md` (unchanged by this session, still open):

1. **Hands-on installed-app smoke test** on this Mac: launch the installed
   `pasteflow-dev.app`, confirm clipboard capture, main shortcut
   (Meta+Shift+0), search shortcut (Meta+Shift+Minus), restart behavior, and
   that the Accessibility prompt does not loop once granted. `smoke-test.sh`
   covers boot + first render only; the interactive items need a human.
2. **Second-machine test**: install on another machine, confirm discovery or
   manual add, and one-direction clipboard sync. Blocked on hardware
   availability, not on code.

## Uncommitted State on `pasteflow-dev-mvp`

Deliberately left uncommitted (commit only on explicit request):

- `shared/build.gradle.kts` — the ktlint filter fix (suggested message:
  `:construction_worker: exclude generated sqldelight code from shared ktlint`).
- `CLAUDE.md` — MVP context + graphify sections (inherited from the previous
  session, already reflected in the checked-in docs).
- `docs/HANDOFF.md` (this file), plus untracked `.claude/` and `graphify-out/`.

## Standing Constraints (Do Not Regress)

- Do not add automatic update installation or release automation without
  explicit instruction. Manual update checking via GitHub releases is allowed.
- User-facing copy says "PasteFlow Dev"; upstream attribution stays intact.
- Contact email: `rob@ngduk.co.uk`.
- `commonMain` code must stay mobile-compatible.
