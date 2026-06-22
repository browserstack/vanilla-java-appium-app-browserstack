package com.browserstack;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Sample App Automate test against the WikipediaSample.apk app.
 *
 * The BrowserStack Java SDK (attached via -javaagent) reads browserstack.yml and
 * injects the app + device capabilities into the Appium session, so the driver is
 * created with an empty options object pointed at the BrowserStack hub.
 *
 * Flow: tap "Search Wikipedia" (accessibility id) -> type "BrowserStack" ->
 * assert the results list rendered at least one entry.
 */
public class BStackSampleTest {

    private AndroidDriver driver;

    @BeforeEach
    public void setUp() throws Exception {
        String userName = System.getenv("BROWSERSTACK_USERNAME");
        String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");

        // Minimal capabilities — the SDK injects the app + device caps from
        // browserstack.yml on top. platformName satisfies the Appium java-client
        // 8.x W3C validation (it rejects a fully-empty capability set).
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "android");

        driver = new AndroidDriver(
                new URL("http://" + userName + ":" + accessKey + "@hub.browserstack.com/wd/hub"),
                capabilities);
    }

    @Test
    public void searchWikipedia() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // 1. Tap "Search Wikipedia" (accessibility id) to open the search field.
        wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.accessibilityId("Search Wikipedia"))).click();

        // 2. Type "BrowserStack" into the search input.
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.id("org.wikipedia.alpha:id/search_src_text")));
        searchInput.sendKeys("BrowserStack");

        // Give the results list a moment to populate.
        wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.className("android.widget.TextView")));

        // 3. Assert at least one search result rendered.
        List<WebElement> results = driver.findElements(
                AppiumBy.className("android.widget.TextView"));
        assertFalse(results.isEmpty(), "Expected Wikipedia search to return results");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
