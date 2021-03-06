package io.webdevice.settings;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.net.URL;

public class SettingsMaker {

    public static Settings allDevices()
            throws Exception {
        return new Settings()
                .withDevice(new DeviceDefinition()
                        .withName("LocalFirefox")
                        .withDriver(FirefoxDriver.class)
                        .withPooled(true)
                        .withAlias("Firefox")
                        .withAlias("Local Firefox")
                        .withCapability("version", "71.0"))
                .withDevice(new DeviceDefinition()
                        .withName("LocalChrome")
                        .withDriver(ChromeDriver.class)
                        .withPooled(true)
                        .withAlias("Chrome")
                        .withAlias("Local Chrome")
                        .withCapability("version", "59.0"))
                .withDevice(new DeviceDefinition()
                        .withName("Chrome59Windows10")
                        .withRemoteAddress(new URL("https://ondemand.saucelabs.com:443/wd/hub"))
                        .withPooled(false)
                        .withAlias("Chrome v59 Windows 10")
                        .withCapability("username", "saucy")
                        .withCapability("accessKey", "2secret4u")
                        .withCapability("browserName", "Chrome")
                        .withCapability("platform", "Windows 10")
                        .withCapability("version", "59.0")
                        .withCapability("extendedDebugging", true)
                        .withConfidential("accessKey"))
                .withDevice(new DeviceDefinition()
                        .withName("FirefoxLatestMojave")
                        .withRemoteAddress(new URL("https://ondemand.saucelabs.com:443/wd/hub"))
                        .withPooled(false)
                        .withOptions(FirefoxOptions.class)
                        .withCapability("username", "saucy")
                        .withCapability("accessKey", "2secret4u")
                        .withCapability("extendedDebugging", true)
                        .withCapability("platform", "macOS 10.14")
                        .withCapability("version", "latest")
                        .withConfidential("accessKey"))
                .withDevice(new DeviceDefinition()
                        .withName("SafariLatestMojave")
                        .withRemoteAddress(new URL("https://ondemand.saucelabs.com:443/wd/hub"))
                        .withAlias("Safari")
                        .withPooled(false)
                        .withDesired("safari")
                        .withCapability("username", "saucy")
                        .withCapability("accessKey", "2secret4u")
                        .withCapability("extendedDebugging", true)
                        .withCapability("platform", "macOS 10.14")
                        .withCapability("version", "latest")
                        .withConfidential("accessKey"))
                .withDevice(new DeviceDefinition()
                        .withName("ChromeLatestMojave")
                        .withRemoteAddress(new URL("https://ondemand.saucelabs.com:443/wd/hub"))
                        .withPooled(false)
                        .withOptions(ChromeOptions.class)
                        .withCapability("w3c", true)
                        .withCapability("platformName", "macOS 10.14")
                        .withCapability("browserVersion", "latest")
                        .withExtraCapability("sauce:options")
                        .withExtraOption("username", "saucy")
                        .withExtraOption("accessKey", "2secret4u")
                        .withExtraOption("extendedDebugging", true)
                        .withConfidential("accessKey"))
                .withDevice(new DeviceDefinition()
                        .withName("iPhone8")
                        .withRemoteAddress(new URL("https://ondemand.saucelabs.com:443/wd/hub"))
                        .withAlias("iPhone")
                        .withAlias("iPhone 8")
                        .withPooled(false)
                        .withDesired("iphone")
                        .withCapability("username", "saucy")
                        .withCapability("accessKey", "2secret4u")
                        .withCapability("extendedDebugging", true)
                        .withCapability("appiumVersion", "1.13.0")
                        .withCapability("deviceName", "iPhone 8")
                        .withCapability("deviceOrientation", "portrait")
                        .withCapability("platformVersion", "12.2")
                        .withCapability("platformName", "iOS")
                        .withCapability("browserName", "Safari")
                        .withConfidential("accessKey"));
    }
}
