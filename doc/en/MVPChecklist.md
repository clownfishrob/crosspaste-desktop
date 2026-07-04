# MVP Completion Checklist

This checklist defines what remains before the PasteFlow Dev macOS MVP can be
called complete.

## Completed

- Development app identity is separated from CrossPaste.
- Local data/config paths are separated from CrossPaste.
- Default shortcuts are separated from CrossPaste.
- Development network port is separated from CrossPaste.
- Native messaging identifiers are separated from CrossPaste.
- macOS Accessibility prompt handling is adjusted for shortcut use.
- Check for updates entry points are hidden from the MVP app surface; the
  Share page is now a minimal project-sharing surface (copy, email,
  repository link) with social platforms still deferred.
- Update delivery is paused and no public updater channel is advertised.
- GitHub automation is reduced to manual CI, Dependabot, and issue templates.
- README, changelog, roadmap, About, settings, extension, and contact copy are reset for MVP context.
- Obsolete upstream updater runbooks and helper scripts are removed.
- Local macOS validation has covered launch, clipboard capture, shortcuts, restart behavior, and Accessibility authorization.
- Desktop tests and extension production build pass on the development machine.
- The local macOS distributable builds successfully with `:app:createDistributable`.
- 2026-07-02 automated re-verification: all `docs/clipboard-manager-mvp.md` claims
  confirmed in code; ktlintCheck, 1287 desktop tests, 79 extension tests, extension
  production build, and `:app:createDistributable` all pass (see `docs/HANDOFF.md`).
- 2026-07-04 full re-verification after phases 2-3 and the macOS stabilization
  pass: lint, desktop tests, extension tests/build, and the distributable all
  green, and `./smoke-test.sh` now PASSES non-interactively (the first-run
  keychain hang was resolved by the secure-store rework).

## Required Before Calling MVP Complete

- Run one final hands-on installed-app smoke test on this Mac
  (the automated boot check passes; these interactive confirmations remain):
  - Launch installed `PasteFlow Dev`.
  - Confirm clipboard capture.
  - Confirm main shortcut.
  - Confirm search shortcut.
  - Confirm restart behavior.
  - Confirm macOS Accessibility prompt does not loop once permission is granted.
- Complete one second-machine test when another machine is available:
  - Install or run PasteFlow Dev on another machine.
  - Confirm the device can be discovered or added manually.
  - Confirm basic clipboard sync works in at least one direction.

## Not MVP Blockers

These are intentionally deferred until product direction is clearer:

- Public launch/social sharing (the basic Share page rework is done).
- Public update/release delivery.
- Signing, updater metadata, and public packaging.
- Public positioning, screenshots, and polished release README.
- Deeper internal package/class rename away from inherited CrossPaste names.
