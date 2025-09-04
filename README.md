## quali.chat Android

[<img src="resources/img/google-play-badge.png" alt="Get it on Google Play" height="60">](https://play.google.com/store/apps/details?id=chat.quali.android)

### Requirements
- **Minimum Android version**: API 26 (Android 8.0)
- **Compile/Target SDK**: 34
- **JDK**: 11
- **Android Studio**: latest stable recommended

### Getting started
1. Clone the repository
2. Open the project in Android Studio and let Gradle sync
3. Select the app module `vector-app` and a build variant, then Run

Alternatively, build from the command line:
- Debug: `./gradlew :vector-app:assembleQualiProductionDebug`
- Install on a connected device/emulator: `./gradlew :vector-app:installQualiProductionDebug`


### SDK and architecture
quali.chat is based on the Matrix Android SDK v2, fully written in Kotlin. The SDK lives in this monorepo and is also published at [`matrix-android-sdk2`](https://github.com/matrix-org/matrix-android-sdk2) for third‑party consumers.

### Code quality
- Ktlint: `./gradlew ktlintCheck` (format with `./gradlew ktlintFormat`)
- Detekt: `./gradlew detekt`

### Testing
- Unit tests: `./gradlew test`
- Instrumented tests (device/emulator): `./gradlew :vector-app:connectedAndroidTest`

### Contributing & community
- See [CONTRIBUTING.md](./CONTRIBUTING.md) for contribution guidelines
- Developer onboarding: [docs/_developer_onboarding.md](./docs/_developer_onboarding.md)
