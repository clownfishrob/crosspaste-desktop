# Session Handoff — PasteFlow Dev MVP Verification (2026-07-02)

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

- Do not re-enable the visible updater, Share entry points, or release
  automation without explicit instruction.
- User-facing copy says "PasteFlow Dev"; upstream attribution stays intact.
- Contact email: `rob@ngduk.co.uk`.
- `commonMain` code must stay mobile-compatible.
