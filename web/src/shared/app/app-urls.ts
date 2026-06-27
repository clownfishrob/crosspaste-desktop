/**
 * App-wide external URLs. Mirrors the desktop `AppUrls` interface.
 * Desktop reads these from `app-urls.properties`; the extension keeps
 * them as constants since it has no classpath resources.
 */
export const AppUrls = {
  homeUrl: "https://github.com/clownfishrob/crosspaste-desktop",
  changeLogUrl: "https://github.com/clownfishrob/crosspaste-desktop/blob/pasteflow-dev-mvp/CHANGELOG.md",
  issueTrackerUrl: "https://github.com/clownfishrob/crosspaste-desktop/issues",
} as const;
