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
- Share and Check for updates entry points are hidden from the MVP app surface.
- Update delivery is paused and no public updater channel is advertised.
- GitHub automation is reduced to manual CI, Dependabot, and issue templates.
- README, changelog, roadmap, About, settings, extension, and contact copy are reset for MVP context.
- Obsolete upstream updater runbooks and helper scripts are removed.
- Local macOS validation has covered launch, clipboard capture, shortcuts, restart behavior, and Accessibility authorization.
- Desktop tests and extension production build pass on the development machine.
- The local macOS distributable builds successfully with `:app:createDistributable`.

## Required Before Calling MVP Complete

- Run one final hands-on installed-app smoke test on this Mac:
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

- Share page and Share menu rework.
- Public update/release delivery.
- Signing, updater metadata, and public packaging.
- Public positioning, screenshots, and polished release README.
- Deeper internal package/class rename away from inherited CrossPaste names.
