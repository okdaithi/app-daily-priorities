# Daily Priorities — Android widget

A home-screen widget that shows your **3 priorities for the day**.

- Open the app, type up to three priorities, tap **Save** — the widget updates instantly.
- Tap a priority on the widget to strike it through as done (tap again to undo).
- Tap the widget title to open the app and edit.
- Follows the phone's light/dark mode. No internet permission, no tracking; data stays on the device.

## Download & install

**Latest APK:** https://github.com/okdaithi/app-daily-priorities/releases/latest/download/DailyPriorities.apk


1. Open the link on your Android phone (Android 8.0+).
2. When prompted, allow your browser to **install unknown apps**.
3. Open the downloaded file and tap **Install**.
4. Long-press the home screen → **Widgets** → **Daily Priorities** → drag it onto the screen.

## How builds work

Every push to `main` (or a manual run from the **Actions** tab) runs
[`.github/workflows/build.yml`](.github/workflows/build.yml), which:

1. Builds a release APK with Gradle on GitHub's servers (nothing needs installing locally).
2. Attaches it to the workflow run as an artifact.
3. Publishes a GitHub Release `v1.0.<run number>` containing `DailyPriorities.apk`.

## Optional: stable signing key (recommended)

Without a signing key the build uses a throwaway debug key that changes on every run, so a new
version **won't install over an old one** — you'd have to uninstall first (which clears your
priorities). To fix this, create a keystore once and add it as repository secrets.

Create the keystore (needs a JDK — e.g. from Android Studio — for `keytool`):

```bash
keytool -genkeypair -v -keystore release.keystore -alias priorities -keyalg RSA -keysize 2048 -validity 10000
```

Then in GitHub: **Settings → Secrets and variables → Actions → New repository secret**, add:

| Secret | Value |
| --- | --- |
| `KEYSTORE_BASE64` | output of `base64 -w0 release.keystore` (PowerShell: `[Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore"))`) |
| `KEYSTORE_PASSWORD` | the keystore password |
| `KEY_ALIAS` | `priorities` |
| `KEY_PASSWORD` | the key password (same as keystore password unless you chose otherwise) |

Keep `release.keystore` somewhere safe and **never commit it**.

## Project layout

```
app/src/main/java/com/dailypriorities/
  MainActivity.kt       – screen for entering the 3 priorities
  PrioritiesWidget.kt   – AppWidgetProvider that renders the widget and handles taps
  PrioritiesStore.kt    – SharedPreferences storage
app/src/main/res/
  layout/widget_priorities.xml     – widget layout
  xml/priorities_widget_info.xml   – widget size/metadata
```

Built with Kotlin, Android Gradle Plugin 8.7, compileSdk 35, minSdk 26. No third-party libraries.
