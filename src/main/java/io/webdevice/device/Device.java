package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Device<Driver extends WebDriver> {
    private final String name;
    private final Driver driver;
    private final Supplier<SessionId> sessionId;
    private final Function<Driver, Boolean> usable;

    public Device(String name, Driver driver, Supplier<SessionId> sessionId, Function<Driver, Boolean> usable) {
        this.name = name;
        this.driver = driver;
        this.sessionId = sessionId;
        this.usable = usable;
    }

    public String getName() {
        return name;
    }

    public Driver getDriver() {
        return driver;
    }

    public <T> T as(Class<T> type) {
        return type.cast(driver);
    }

    public SessionId getSessionId() {
        return sessionId.get();
    }

    public boolean usable() {
        return usable.apply(driver);
    }

    public Device<Driver> perform(Consumer<Driver> consumer) {
        consumer.accept(driver);
        return this;
    }

    public <R> R invoke(Function<Driver, R> function) {
        return function.apply(driver);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device<?> that = (Device<?>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(driver.getClass(), that.driver.getClass()) &&
                Objects.equals(getSessionId(), that.getSessionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, driver.getClass(), getSessionId());
    }
}
