package io.webdevice.settings;

import io.webdevice.support.YamlPropertySourceFactory;
import io.webdevice.test.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Map;

import static java.lang.String.valueOf;

public abstract class EnvironmentBasedTest
        extends UnitTest {
    protected ConfigurableEnvironment environment;
    protected PropertySourceFactory propertySourceFactory;

    @Before
    public void setUpSystemProperties() {
        System.setProperty("saucelabs_username", "saucy");
        System.setProperty("saucelabs_accessKey", "2secret4u");
    }

    @Before
    public void setUpEnvironment() {
        environment = makeEnvironment();
        environment.setConversionService(makeConversionService());
        propertySourceFactory = makePropertySourceFactory();
    }

    @After
    public void tearDownSystemProperties() {
        System.clearProperty("saucelabs_accessKey");
        System.clearProperty("saucelabs_username");
    }

    protected ConfigurableEnvironment makeEnvironment() {
        return new StandardEnvironment();
    }

    protected ConfigurableConversionService makeConversionService() {
        return new DefaultConversionService();
    }

    protected PropertySourceFactory makePropertySourceFactory() {
        return new YamlPropertySourceFactory();
    }

    protected ConfigurableEnvironment environmentWith(String... resources)
            throws IOException {
        for (String resource : resources) {
            environment.getPropertySources()
                    .addLast(propertySourceFactory
                            .createPropertySource(null,
                                    new EncodedResource(new ClassPathResource(resource))));
        }
        return environment;
    }

    protected ConfigurableEnvironment environmentWith(Map<String, Object> properties) {
        environment.getPropertySources()
                .addLast(new MapPropertySource(valueOf(properties.hashCode()), properties));
        return environment;
    }
}
