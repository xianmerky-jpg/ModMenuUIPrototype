# GitHub Actions APK build fix

## Changes
- app/build.gradle.kts: explicitly align Java source/target compatibility and Kotlin jvmTarget at 17. This fixes the reported Java 1.8 versus Kotlin 17 target mismatch.
- .github/workflows/build-apk.yml: run on pushes to main or master; retain manual dispatch; include stack traces; fail artifact upload if the APK is missing.
- Original application/UI code and dependency versions are unchanged.

## Apply to your GitHub repository
1. Extract this ZIP.
2. Copy its contents into your repository root, replacing existing files. Do not nest the project inside another folder. Include the hidden .github directory. If editing on GitHub instead, replace app/build.gradle.kts and .github/workflows/build-apk.yml with the copies in this ZIP.
3. Commit and push to main or master.
4. Open Actions > Build APK and open the new run. You can also select Run workflow when the workflow exists on the default branch.
5. After success, download the mod-menu-ui-debug artifact at the bottom of the run page. Extract it to get app-debug.apk.

This is a debug APK, not a production release-signed APK.

## Environment
The workflow uses JDK 17, Gradle 8.9, Android Gradle Plugin 8.7.3, and Kotlin 2.0.21, with compileSdk 35. The project intentionally uses Gradle installed by the setup-gradle action; the original archive does not contain a Gradle wrapper.

The Node 20 deprecation warning in the supplied log is separate from the Kotlin compilation failure. No insecure Node override was added.

## Verification limits
The downloaded source and configuration were inspected and the ZIP contents checked. A full Android build was not executed here: this workspace has Java 11 and no configured Android SDK. Re-run GitHub Actions to verify the complete APK build; this patch addresses the specific JVM-target error supplied, not a guarantee against unrelated later errors.
