# [2.1.7] - 2026-07-15

## macOS 12 packaging
The macOS package metadata now targets macOS 12.0 consistently across the Gradle and Conveyor package paths.

## Collection sync controls
Collections can now store sync settings, and disabling sync for a collection prevents its items from being sent automatically.

## OCR and collection foundations
Image OCR text is shown in preview, with initial foundations added for snippet templates and smart collections.

# [2.1.6] - 2026-07-15

## Device pairing reliability
Verification prompts now appear globally instead of only inside the Devices screen, so a newly discovered device can be trusted wherever you are in the app.

## Manual network pairing
Manual device add now preserves the exact address you enter, including LAN IPs, Tailscale IPs, and MagicDNS hostnames, then immediately checks that route for verification.

# [2.1.5] - 2026-06-26

## PasteFlow Dev MVP
Initial PasteFlow Dev desktop MVP based on the CrossPaste open-source codebase.

## Separate development identity
PasteFlow Dev now uses its own app name, bundle ID, storage folder, network port, shortcuts, and native messaging identifiers so it can be developed separately from CrossPaste.

## Local clipboard manager baseline
The desktop app has been validated on this Mac for clipboard capture, main shortcut launch, search shortcut launch, restart behavior, and macOS Accessibility prompt handling.

## macOS reliability pass
Search paste, restart behaviour, screenshot-key capture, and Skitch export capture have been checked on this Mac. Windows, Linux, and second-machine sync checks remain deferred until those environments are available.

## MVP operating mode
Settings, About, Change Log, and help links now point at PasteFlow Dev project context. Updates, public release delivery, and Share-page work are paused until product direction is clearer.
