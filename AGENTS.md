# AGENTS.md

Project-specific guidance for coding agents. For architecture, build/lint/test
commands, code style, and commit conventions, see `CLAUDE.md` and `README.md` —
they are the source of truth and are not duplicated here.

## Cursor Cloud specific instructions

This is a Kotlin Multiplatform + Compose desktop clipboard manager. Everything
runs in-process inside a single JVM app (`:app`, main class
`com.crosspaste.CrossPaste`) — there is no external database, container, or
companion service to start. Local storage is an embedded SQLite DB. Build/lint/
test/run commands are in `CLAUDE.md`/`README.md`.

Non-obvious caveats for this cloud (headless Linux) environment:

- Toolchain: build, `ktlintCheck`, and `:app:desktopTest` run fine with the
  system JDK 21 (`/usr/lib/jvm/java-21-openjdk-amd64`). Gradle 9.6 is
  auto-downloaded by the wrapper; the Maven dependency cache and the downloaded
  JBR persist in the VM snapshot.
- Running the app requires the JetBrains Runtime (JBR), NOT the system
  OpenJDK/Temurin: Jewel's `DecoratedWindow` throws on a non-JBR JVM on the
  first Compose composition. The JBR is downloaded by `./gradlew app:build` into
  `app/jbr/` (git-ignored). The easiest way to launch is `./smoke-test.sh`,
  which extracts the JBR, points `JAVA_HOME` at it, and boots the app. To run it
  yourself, set `JAVA_HOME=/workspace/app/jbr/extracted/jbrsdk-21.0.9-linux-x64-b1163.94`
  before `./gradlew -PappEnv=BETA :app:run`.
- Headless GUI requirements: a display is available at `DISPLAY=:1`. The VM has
  no GPU, so export `SKIKO_RENDER_API=SOFTWARE` (otherwise Skiko fails with
  "Cannot create Linux GL context"). The system libraries `libxkbcommon-x11-0`
  (needed by the `jnativehook` global-key native lib, else an
  `UnsatisfiedLinkError` kills the main thread) and Mesa software-GL libs
  (`libgl1-mesa-dri`, `libglx-mesa0`, `libegl1`) are pre-installed in the
  snapshot.
- No window appears on launch: the Xfce panel has no system-tray plugin, so the
  AppIndicator tray icon is invisible and both app windows start hidden. Open
  the search overlay by injecting its global shortcut:
  `DISPLAY=:1 xdotool key super+shift+x` (verified to log "Open search window").
  `Escape` hides the visible window. Shortcut bindings live in
  `app/.pasteflow-dev/<...>/shortcut-keys.properties` as jnativehook virtual
  codes.
- Clipboard capture can be exercised without a real copy gesture:
  `printf 'text' | DISPLAY=:1 xclip -selection clipboard` triggers
  `LinuxPasteboardService -- notify change event` and stores a new paste row.
- Dev data lives (git-ignored) under `app/.pasteflow-dev/`: SQLite DB at
  `data/crosspaste.db`, single-instance guard `app.lock`, logs at
  `logs/crosspaste.log`. If boot reports "Another instance ... already running",
  delete a stale `app/.pasteflow-dev/app.lock`.
- Benign headless log noise (not failures): `WMCtrl -- Cannot get WM_CLASS
  property`, `sni: failed to connect to session bus: No medium found`, and a
  one-time Skiko "Fallback to next API" before software rendering engages.
