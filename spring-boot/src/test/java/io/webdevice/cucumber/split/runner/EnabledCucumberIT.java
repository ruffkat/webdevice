package io.webdevice.cucumber.split.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.webdevice.cucumber.split.steps.enabled.ConfigurationStep;
import org.junit.runner.RunWith;

/**
 * @see ConfigurationStep
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        tags = "@direct and not @ignore",
        glue = {"io.webdevice.cucumber.split.steps.enabled", "io.webdevice.cucumber.common"},
        features = {"src/test/resources/features"},
        plugin = {"pretty"})
public class EnabledCucumberIT {
}
