# Changelog

All notable PasteFlow Dev changes will be documented here.

PasteFlow Dev is a fork of CrossPaste. Earlier CrossPaste release history belongs
to the upstream project and is available from:

- [CrossPaste releases](https://github.com/CrossPaste/crosspaste-desktop/releases)
- [CrossPaste changelog](https://github.com/CrossPaste/crosspaste-desktop/blob/main/CHANGELOG.md)

## 2026-06-27 - MVP repo cleanup

- Reset the GitHub-facing changelog for PasteFlow Dev.
- Updated About, help, settings, and changelog copy to point at PasteFlow Dev project context.
- Paused visible updater language while release delivery, signing, and product direction are undecided.
- Hid Share and Check for updates entry points from the MVP app surface.
- Updated contact wording to use `rob@ngduk.co.uk`.
- Kept GitHub automation minimal: manual CI, Dependabot, and issue templates.
- Removed obsolete Windows self-update runbooks and helper scripts from the MVP repo surface.
- Updated extension-facing About/settings links and visible PasteFlow Dev wording.

## 2026-06-26 - Development MVP baseline

- Created the PasteFlow Dev development identity.
- Separated the macOS app bundle identity, local data paths, default network port, shortcut defaults, and native messaging identifiers from CrossPaste.
- Installed and validated the macOS app locally.
- Confirmed clipboard capture, main shortcut launch, search shortcut launch, restart behavior, and macOS Accessibility authorization flow on the local Mac.
- Added MVP notes for manual macOS builds and local installation.

## Deferred

- Share page and Share menu rework.
- Public release packaging and update delivery.
- Product positioning, screenshots, and polished public README copy.
- Multi-device testing on another machine.
