# Vanilla Java Appium with BrowserStack App Automate

Run a vanilla Java Appium (Android) test on real devices via
[BrowserStack App Automate](https://app-automate.browserstack.com/) using the
[BrowserStack Java SDK](https://www.browserstack.com/docs/app-automate/appium/getting-started/java).

The SDK is attached as a `-javaagent`, reads `browserstack.yml`, selects the
pre-uploaded app, starts the Appium session on the chosen device, and reports
results back to BrowserStack — your test code stays plain `io.appium:java-client`
driving JUnit 5.

## Prerequisites

- A [BrowserStack](https://www.browserstack.com/) account (username + access key).
- JDK 8+ and Maven 3.6+.

## Setup

```bash
git clone <this-repo>
cd vanilla-java-appium/android
mvn -DskipTests compile
```

Configure credentials either in `browserstack.yml` (`userName` / `accessKey`)
or as environment variables:

```bash
export BROWSERSTACK_USERNAME="YOUR_USERNAME"
export BROWSERSTACK_ACCESS_KEY="YOUR_ACCESS_KEY"
```

The sample app is already referenced in `android/browserstack.yml` as a
pre-uploaded `bs://` app id (WikipediaSample.apk). To use your own build, set
`app:` to a local `.apk` path and the SDK will upload it for you.

## Run Sample Test

```bash
cd android
mvn test -Dtest=BStackSampleTest
```

The `-javaagent` is wired into the Maven Surefire plugin's `argLine` (the jar
path resolves from `~/.m2` via the `maven-dependency-plugin` `properties` goal),
so a plain `mvn test` attaches the SDK automatically. The sample test:

1. taps **Search Wikipedia** (accessibility id),
2. types `BrowserStack` into the search field, and
3. asserts the results list rendered at least one entry.

## Run Local Test

`BStackLocalTest` drives `LocalSample.apk` (pkg
`com.example.android.basicnetworking`) and asserts the in-app network check
reports **Up and running**, proving the BrowserStack Local tunnel is connected.
It is `@Disabled` by default because this `android/` dir ships wired to the
Wikipedia sample app. To run it:

1. point `browserstack.yml` `app:` at `LocalSample.apk`,
2. set `browserstackLocal: true`,
3. remove the `@Disabled` annotation, then:

```bash
cd android
mvn test -Dtest=BStackLocalTest
```

## Notes / Dashboard

- Watch sessions live and review video/logs at
  [app-automate.browserstack.com](https://app-automate.browserstack.com/).
- `testObservability: true` also surfaces this build in
  [BrowserStack Test Observability](https://observability.browserstack.com/).
- The SDK dependency `com.browserstack:browserstack-java-sdk` uses `LATEST`,
  matching the published-sample convention (resolved to `1.59.7` at validation
  time).
- `io.appium:java-client` is pinned to `8.2.1` with `selenium-java` `4.5.0`.
  Newer java-client (8.6.0) trips the SDK 1.59.7 javaagent on an empty/injected
  capability set (`IllegalArgumentException: Capabilities must be set`, session
  never starts); the 8.2.1 + Selenium 4.5.0 combo is the known-good pairing and
  passes a live device session.
