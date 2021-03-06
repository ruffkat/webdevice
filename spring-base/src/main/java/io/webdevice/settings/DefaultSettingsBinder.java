package io.webdevice.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.validation.DataBinder;

import static io.webdevice.settings.PropertyPath.normalize;
import static io.webdevice.wiring.WebDeviceScope.namespace;
import static java.util.Arrays.stream;

/**
 * This class allows the {@link io.webdevice.wiring.WebDeviceRuntime} to be used
 * in a traditional spring context where spring boot is not available.
 * <p>
 * This implementation uses the {@link DataBinder} to bind {@link Settings} from
 * the execution {@link ConfigurableEnvironment environment}.
 */
public class DefaultSettingsBinder
        implements SettingsBinder {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Settings from(ConfigurableEnvironment environment) {
        log.info("Binding Settings instance from environment ...");
        Settings settings = new Settings();
        DataBinder binder = new DataBinder(settings, "settings");
        binder.setConversionService(environment.getConversionService());
        binder.setAutoGrowNestedPaths(true);
        binder.bind(collectPropertyValues(environment));
        log.info("Settings bound.");
        return settings;
    }

    private MutablePropertyValues collectPropertyValues(ConfigurableEnvironment environment) {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        String prefix = namespace("");
        environment.getPropertySources().stream()
                // Collect all webdevice specific properties
                .filter(EnumerablePropertySource.class::isInstance)
                .map(EnumerablePropertySource.class::cast)
                .flatMap(source -> stream(source.getPropertyNames()))
                .filter(name -> name.startsWith(prefix))
                .distinct()
                // Apply each property to the settings
                .forEach(path -> {
                    String property = normalize(path.substring(prefix.length()));
                    log.debug("Normalized environment property path: {} -> {}", path, property);
                    Object value = environment.getProperty(path, Object.class);
                    propertyValues.addPropertyValue(property, value);
                });
        return propertyValues;
    }
}
