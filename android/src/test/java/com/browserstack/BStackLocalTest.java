package com.browserstack;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Local App Automate test against the LocalSample.apk app
 * (pkg com.example.android.basicnetworking).
 *
 * Proves the BrowserStack Local tunnel is connected: triggers the in-app network
 * test action and asserts the app reports it is "Up and running".
 *
 * Requires browserstackLocal: true and the LocalSample.apk app in browserstack.yml.
 * Disabled by default because this android/ dir is wired to the Wikipedia sample
 * app + a single platform; enable it once you point browserstack.yml at
 * LocalSample.apk and set browserstackLocal: true.
 */
@Disabled("Enable after configuring browserstack.yml with LocalSample.apk + browserstackLocal: true")
public class BStackLocalTest {

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
    public void localTunnelUpAndRunning() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Trigger the in-app network test action.
        wait.until(ExpectedConditions.elementToBeClickable(
                AppiumBy.id("com.example.android.basicnetworking:id/test_action"))).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.className("android.widget.TextView")));

        // Assert a TextView reports the tunnel is up.
        List<WebElement> textViews = driver.findElements(
                AppiumBy.className("android.widget.TextView"));

        boolean upAndRunning = textViews.stream()
                .map(WebElement::getText)
                .filter(t -> t != null)
                .anyMatch(t -> t.contains("Up and running"));

        assertTrue(upAndRunning, "Expected 'Up and running' text confirming the Local tunnel");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
