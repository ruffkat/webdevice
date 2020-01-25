package io.webdevice.wiring;

import org.springframework.core.env.ConfigurableEnvironment;

import static java.lang.ThreadLocal.withInitial;

public class MockSettingsBinder
        implements SettingsBinder {
    private static final ThreadLocal<Settings> mockSettings = withInitial(() -> null);

    @Override
    public Settings from(ConfigurableEnvironment environment) {
        return mockSettings.get();
    }

    public static Settings install(Settings settings) {
        mockSettings.set(settings);
        return settings;
    }

    public static Settings uninstall() {
        Settings settings = mockSettings.get();
        mockSettings.remove();
        return settings;
    }
}
