# Project Roadmap

This roadmap tracks the PasteFlow Dev MVP fork. It intentionally does not mirror
the upstream CrossPaste release roadmap.

## Locked MVP decisions

- The local macOS smoke test is clear: launch, clipboard capture, shortcuts,
  restart behavior, and Accessibility authorization are working.
- Update delivery is paused until the product direction and release channel are
  clearer.
- GitHub CI/builds are manual-only; they do not run on push or pull request.
- Public release and beta publishing workflows are removed for now.
- Sponsor updates, issue translation, and AI review automation are removed for now.
- The GitHub repo should stay minimal: issue templates, Dependabot, and manual CI.

## Available now

- Development app identity separated from CrossPaste.
- Local data/config paths separated from CrossPaste.
- Default shortcuts separated from CrossPaste.
- Development network port separated from CrossPaste.
- Native messaging identifiers separated from CrossPaste.
- macOS Accessibility prompt handling adjusted for shortcut use.
- Local macOS manual build and install path documented.
- GitHub README, changelog, and app-facing copy reset for MVP context.

## Next focus

- Keep the MVP stable as a local desktop clipboard manager.
- Reduce remaining user-visible CrossPaste wording where it is not attribution,
  package namespace, or inherited internal structure.
- Improve settings labels and empty states as they are encountered during use.
- Keep documenting decisions in this roadmap and the changelog.

## Deferred product work

- Update the Share settings page for the PasteFlow Dev product direction.
- Re-enable the Share menu once the PasteFlow Dev share flow and copy are ready.
- Re-enable Check for updates once update metadata, signing, and release delivery
  are owned by PasteFlow Dev.
- Rebrand or replace legacy CrossPaste update infrastructure names before update
  controls return.
- Add public positioning, screenshots, and release instructions once the product
  direction is clearer.
- Complete multi-device testing on another machine when available.
