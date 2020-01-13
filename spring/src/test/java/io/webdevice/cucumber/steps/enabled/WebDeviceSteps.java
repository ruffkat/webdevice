package io.webdevice.cucumber.steps.enabled;

import io.cucumber.java.en.Given;
import io.webdevice.device.WebDevice;
import io.webdevice.wiring.EnableWebDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

/**
 * This demonstrates using WbeDevice with an existing
 * configuration with WebDevice enabled
 */
@SpringBootTest
@EnableWebDevice
public class WebDeviceSteps {

    @Autowired
    private WebDevice browser;

    @Given("a {string} browser")
    public void useBrowser(String name) {
        browser.use(name);
    }

    @Given("a browser")
    public void useBrowser() {
        browser.useDefault();
    }

    @Given("I navigate home")
    public void navigateHome() {
        browser.home();
    }

    @Given("I navigate to {string}")
    public void navigateTo(String relativePath) {
        browser.navigateTo(relativePath);
    }

    @Configuration
    public static class Context {
    }
}