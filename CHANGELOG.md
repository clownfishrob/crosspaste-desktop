# Changelog

All notable PasteFlow Dev changes will be documented here.

PasteFlow Dev is a fork of CrossPaste. Earlier CrossPaste release history belongs
to the upstream project and is available from:

- [CrossPaste releases](https://github.com/CrossPaste/crosspaste-desktop/releases)
- [CrossPaste changelog](https://github.com/CrossPaste/crosspaste-desktop/blob/main/CHANGELOG.md)

## 2026-07-03 - GitHub and About refresh

- Refreshed the GitHub README for the current macOS-first MVP state.
- Updated the About page footer to describe PasteFlow Dev as a local-first clipboard MVP while keeping upstream attribution.
- Reworked the Share page into a PasteFlow Dev project-sharing surface with copy, email, and repository actions.
- Added the first OCR pass: image "Extract Text" now stores extracted text on the image item and refreshes search content.
- Added the macOS reliability pass to the app-facing changelog notes.
- Documented that Windows/Linux reliability, second-machine testing, updater work, and public launch/social sharing remain deferred.

## 2026-06-27 - MVP repo cleanup

- Reset the GitHub-facing changelog for PasteFlow Dev.
- Updated About, help, settings, and changelog copy to point at PasteFlow Dev project context.
- Paused visible updater language while release delivery, signing, and product direction are undecided.
- Hid Share and Check for updates entry points from the MVP app surface.
- Updated contact wording to use `rob@ngduk.co.uk`.
- Kept GitHub automation minimal: manual CI, Dependabot, and issue templates.
- Removed obsolete Windows self-update runbooks and helper scripts from the MVP repo surface.
- Updated extension-facing About/settings links and visible PasteFlow Dev wording.
- Updated extension pairing and notification wording from CrossPaste to PasteFlow Dev.
- Clarified README attribution links as upstream CrossPaste resources.
- Normalized hidden Share copy and Contact email text for the MVP.
- Pointed dormant Share/project web links at the fork repository instead of a paused download page.
- Added an explicit MVP completion checklist with manual blockers and deferred work.
- Hid built-in guide entries from Clipboard Source Control so only real app sources are shown.

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
