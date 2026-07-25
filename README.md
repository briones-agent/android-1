# Home Assistant (Android) + React Native

Experimental fork of [home-assistant/android](https://github.com/home-assistant/android)
testing brownfield support for existing Android codebases. Reference for
integrating React Native (via Expo) into an existing native app without
refactoring the project structure.

Uses Expo's brownfield **isolated** approach: the RN + Expo code is built into a
prebuilt **AAR** and consumed like any other Maven dependency. A floating "Expo"
button on the onboarding screen launches an `ExpoActivity` that renders the
React Native screen (JS bundle embedded in the release AAR, so no Metro).

## Integration steps

1. `npx create-expo-app@latest expo-app --template default@canary` (in `./expo-app`)
2. Build the AAR:
   ```sh
   cd expo-app
   npx expo install expo-brownfield expo-build-properties
   npx expo prebuild -p android --clean
   npx expo-brownfield build:android --release --fused --verbose
   ```
   Published to local Maven as `io.homeassistant.brownfield:habrownfield-fused-release`,
   built with `-Pbrownfield.fused.host-provided=com.caverock` (HA's `coil-svg`
   pulls androidsvg, which `expo-image` fuses).
3. Consume from `app/build.gradle.kts`, launch from `ExpoActivity`. Build the
   `minimal` (no-GMS) flavor: `assembleMinimalDebug`.

### Notes for reproducing the build

Home Assistant is a large, strict host and needed several accommodations:

- **`androidSdk-min` 23 -> 24** (`gradle/libs.versions.toml`) — React Native floor.
- **`mavenLocal()`** (scoped) in `settings.gradle.kts` (`FAIL_ON_PROJECT_REPOS`).
- **Gradle dependency locking** — HA pins dependencies in `app/gradle.lockfile`.
  Regenerate after adding the AAR with `./gradlew :app:assembleMinimalDebug --write-locks`.
- **`app/google-services.json`** — the google-services plugin is applied to all
  flavors and fails without it; a placeholder (no real Firebase project) is
  committed.
- **`tools:replace="android:maxSdkVersion"`** on `WRITE_EXTERNAL_STORAGE`
  (host 28 vs AAR 32).
- **Disable `HAStrictMode`** in `HomeAssistantApplication` — its fail-fast
  `detectIncorrectContextUse()` aborts React Native's `ReactHostImpl.createSurface()`
  (which accesses WindowManager from the Application context), leaving the RN
  screen blank.
- `reactNativeArchitectures=arm64-v8a`. No compileSdk/Kotlin bump (compileSdk 37,
  Kotlin 2.4).

---

<details>
<summary>Home Assistant Android (original README)</summary>

# Home Assistant Companion for Android

[![Build Status](https://github.com/home-assistant/android/actions/workflows/onPush.yml/badge.svg)](https://github.com/home-assistant/android/actions/workflows/onPush.yml)  
[![Play Store](https://img.shields.io/badge/Play%20Store-Download-blue?logo=google-play)](https://play.google.com/store/apps/details?id=io.homeassistant.companion.android)
[![Play Store Beta](https://img.shields.io/badge/Play%20Store%20Beta-Download-blue?logo=google-play)](https://play.google.com/apps/testing/io.homeassistant.companion.android)
[![Discord](https://img.shields.io/discord/330944238910963714?label=Discord&logo=discord)](https://discord.gg/c5DvZ4e)
[![Stars](https://img.shields.io/github/stars/home-assistant/android?style=social)](https://github.com/home-assistant/android/stargazers)

Welcome to the **Home Assistant Companion for Android**! This is the official Android app for [Home Assistant](https://www.home-assistant.io/), a powerful open-source home automation platform. Join us in building an app used by millions of users worldwide.

---

## Features

- **Control Your Smart Home**: Seamlessly interact with your Home Assistant instance.
- **Native Android Experience**: Leverage Android-specific features like widgets, notifications, and location tracking.
- **Customizable**: Tailor the app to your needs with themes, dashboards, and more.
- **Open Source**: Contribute to a project that empowers users to take control of their smart homes.

## Get the app

- **[Download from the Play Store](https://play.google.com/store/apps/details?id=io.homeassistant.companion.android)**  
  Join the [Play Store Beta](https://play.google.com/apps/testing/io.homeassistant.companion.android) to test new features early.
- **Other Stores**: The app is also available in other app stores.

## Documentation

Looking for help? Check out the [Home Assistant Companion Documentation](https://companion.home-assistant.io/) for detailed guides on using the app.

## Report a bug or request a feature

Found a bug or have an idea for a new feature? Let us know!  

- **[Open a Bug Report](https://github.com/home-assistant/android/issues/new?template=Bug_report.md)**  
- **[Request a Feature](https://github.com/home-assistant/android/issues/new?template=feature_request.md)**  

We appreciate your feedback and contributions to make the app even better!

## Contributing

We are thrilled to welcome contributions from the community! This app exists thanks to the incredible efforts of the Home Assistant community. Whether you're fixing bugs, adding new features, or improving documentation, your contributions make a difference.

Every contribution, big or small, is greatly appreciated. Together, we can make the Home Assistant Companion for Android even better!

### Getting started

1. Read the [Developer Guide](https://developers.home-assistant.io/docs/android/).
2. Fork the repository and create a branch for your changes.
3. Submit a pull request and join the discussion!

## Join the community

Connect with other contributors and users in our vibrant **[Discord Community](https://discord.gg/c5DvZ4e)**: Join the **[#Android](https://discord.com/channels/330944238910963714/1346948551892009101)** channel to chat with developers and contributors.

## Star the repository

If you find this project useful, consider giving it a star on GitHub!  
It helps others discover the project and motivates us to keep improving.

<a href="https://next.ossinsight.io/widgets/official/analyze-repo-stars-history?repo_id=179008173" target="_blank" style="display: block" align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://next.ossinsight.io/widgets/official/analyze-repo-stars-history/thumbnail.png?repo_id=179008173&image_size=auto&color_scheme=dark" width="721" height="auto">
    <img alt="Star History of home-assistant/android" src="https://next.ossinsight.io/widgets/official/analyze-repo-stars-history/thumbnail.png?repo_id=179008173&image_size=auto&color_scheme=light" width="721" height="auto">
  </picture>
</a>

[![Home Assistant - A project from the Open Home Foundation](https://www.openhomefoundation.org/badges/home-assistant.png)](https://www.openhomefoundation.org/)


</details>
