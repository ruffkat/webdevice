package io.webdevice.wiring;

import io.webdevice.device.Browser;
import io.webdevice.device.DevicePool;
import io.webdevice.settings.DeviceDefinition;
import io.webdevice.settings.Settings;
import io.webdevice.settings.SettingsFactory;
import io.webdevice.support.SimpleDeviceCheck;
import io.webdevice.support.SpringDeviceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import static io.webdevice.wiring.WebDeviceScope.namespace;
import static io.webdevice.wiring.WebDeviceScope.registerScope;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;
import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;
import static org.springframework.util.ClassUtils.isPresent;
import static org.springframework.util.StringUtils.isEmpty;

public class WebDeviceRegistrar
        implements ImportBeanDefinitionRegistrar {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ConfigurableEnvironment environment;
    private final SettingsFactory factory;

    @Autowired
    public WebDeviceRegistrar(Environment environment) {
        // Spring implodes when ConfigurableEnvironment is declared in constructor
        this.environment = (ConfigurableEnvironment) environment;
        this.factory = new SettingsFactory((ConfigurableEnvironment) environment);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Settings settings = registerSettings(factory.from(environment), registry);
        registerScope((ConfigurableBeanFactory) registry);
        registerDevices(settings, registry);
        registerDeviceRegistry(settings, registry);
        registerWebDevice(settings, registry);
    }

    private Settings registerSettings(Settings settings, BeanDefinitionRegistry registry) {
        log.info("Registering Settings ...");
        registry.registerBeanDefinition(namespace("Settings"),
                genericBeanDefinition(Settings.class, () -> settings)
                        .getBeanDefinition());
        log.info("Settings registered.");
        return settings;
    }

    private String maybeRegisterProvider(DeviceDefinition device, BeanDefinitionRegistry registry) {
        String provider = namespace("%s-Provider", device.getName());
        if (!registry.isBeanNameInUse(provider)) {
            log.info("Registering WebDeviceProvider definition named {}", provider);
            registry.registerBeanDefinition(provider,
                    device.build()
                            .getBeanDefinition());
        }
        return provider;
    }

    private String maybeRegisterPool(String provider, DeviceDefinition device, BeanDefinitionRegistry registry) {
        String pool = namespace("%s-Pool", device.getName());
        if (!registry.isBeanNameInUse(pool)) {
            log.info("Registering WebDevicePool definition named {}", pool);
            registry.registerBeanDefinition(pool,
                    genericBeanDefinition(DevicePool.class)
                            .addConstructorArgValue(device.getName())
                            .addConstructorArgReference(provider)
                            .addConstructorArgValue(new SimpleDeviceCheck<>())
                            .setAutowireMode(AUTOWIRE_CONSTRUCTOR)
                            .setDestroyMethodName("dispose")
                            .setRole(ROLE_INFRASTRUCTURE)
                            .getBeanDefinition());
        }
        return pool;
    }

    private void registerAliases(String canonical, DeviceDefinition device, BeanDefinitionRegistry registry) {
        log.info("Registering alias '{}' for '{}'", device.getName(), canonical);
        registry.registerAlias(canonical, device.getName());
        device.aliases().forEach(alias -> {
            log.info("Registering alias '{}' for '{}'", alias, canonical);
            registry.registerAlias(canonical, alias);
        });
    }

    private void registerDevices(Settings settings, BeanDefinitionRegistry registry) {
        log.info("Registering Devices ...");
        settings.devices()
                .filter(device -> {
                    boolean defined = registry.isBeanNameInUse(namespace(device.getName()));
                    if (defined) {
                        log.warn("Device {} is already defined, skipping registration", device.getName());
                    }
                    return !defined;
                })
                .forEach(device -> {
                    String provider = maybeRegisterProvider(device, registry);
                    if (device.isPooled()) {
                        provider = maybeRegisterPool(provider, device, registry);
                    }
                    registerAliases(provider, device, registry);
                });
        log.info("Devices registered.");
    }

    private String scope(Settings settings, BeanDefinitionRegistry registry) {
        String scope = settings.getScope();
        if (isEmpty(scope)) {
            // TODO: Determine a reliable way to detect running with cucumber
            scope = isPresent("io.cucumber.spring.CucumberTestContext", null)
                    ? "cucumber-glue"
                    : "webdevice";
        }
        return scope;
    }

    private void registerDeviceRegistry(Settings settings, BeanDefinitionRegistry registry) {
        String scope = scope(settings, registry);
        log.info("Registering DeviceRegistry in {} scope ...", scope);
        registry.registerBeanDefinition(namespace("DeviceRegistry"),
                genericBeanDefinition(SpringDeviceRegistry.class)
                        .setScope(scope)
                        .setAutowireMode(AUTOWIRE_CONSTRUCTOR)
                        .getBeanDefinition());
        log.info("DeviceRegistry registered.");
    }

    private void registerWebDevice(Settings settings, BeanDefinitionRegistry registry) {
        String scope = scope(settings, registry);
        log.info("Registering WebDevice in {} scope ...", scope);
        registry.registerBeanDefinition(namespace("WebDevice"),
                genericBeanDefinition(Browser.class)
                        .setScope(scope)
                        .addConstructorArgReference(namespace("DeviceRegistry"))
                        .setAutowireMode(AUTOWIRE_CONSTRUCTOR)
                        .addPropertyValue("baseUrl", settings.getBaseUrl())
                        .addPropertyValue("defaultDevice", settings.getDefaultDevice())
                        .addPropertyValue("eager", settings.isEager())
                        .addPropertyValue("strict", settings.isStrict())
                        .setInitMethodName("initialize")
                        .setDestroyMethodName("release")
                        .getBeanDefinition());
        log.info("WebDevice registered.");
    }
}
