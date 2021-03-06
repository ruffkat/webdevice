package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.Deque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Predicate;

import static java.lang.String.format;

/**
 * TODO: This needs a proper implementation
 */
public class DevicePool<Driver extends WebDriver>
        implements DeviceProvider<Driver> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final BlockingDeque<Device<Driver>> free;
    private final BlockingDeque<Device<Driver>> used;
    private final String name;
    private final DeviceProvider<Driver> provider;
    private final Predicate<Device<Driver>> usable;

    protected DevicePool(String name,
                         DeviceProvider<Driver> provider,
                         Predicate<Device<Driver>> usable,
                         BlockingDeque<Device<Driver>> free,
                         BlockingDeque<Device<Driver>> used) {
        this.name = name;
        this.provider = provider;
        this.usable = usable;
        this.free = free;
        this.used = used;
    }

    public DevicePool(String name, DeviceProvider<Driver> provider, Predicate<Device<Driver>> usable) {
        this(name, provider, usable, new LinkedBlockingDeque<>(), new LinkedBlockingDeque<>());
    }

    /**
     * Acquires a {@link Device} for exclusive use
     *
     * @return a {@link Device} for exclusive use
     */
    @Override
    public synchronized Device<Driver> get() {
        Device<Driver> device = free.poll();
        if (device == null) {
            device = create();
        } else {
            if (!usable.test(device)) {
                log.info("Device {} in {} pool is not usable", device.getSessionId(), name);
                release(device);
                device = create();
            }
        }
        used.push(device);
        log.info("Acquired {} in {} pool", device.getSessionId(), name);
        logStats();
        return device;
    }

    /**
     * Marks the {@link Device} as free for use
     *
     * @param device The {@link Device} to be made available
     */
    @Override
    public synchronized void accept(Device<Driver> device) {
        log.info("Removing device {} from used deque in {} pool", device.getSessionId(), name);
        if (used.remove(device)) {
            log.info("Placing device {} to free deque in {} pool", device.getSessionId(), name);
            free.push(device);
        } else {
            log.warn("Device {} was not in used deque in {} pool!", device.getSessionId(), name);
        }
        logStats();
    }

    @PreDestroy
    public synchronized void dispose() {
        log.info("Shutting down {} pool...", name);
        drain(free);
        drain(used);
        log.info("Pool {} shut down.", name);
    }

    private Device<Driver> create() {
        log.info("Obtaining new device from provider {}...", name);
        Device<Driver> device = provider.get();
        log.info("Obtained new device {} from provider {}.", device.getSessionId(), name);
        return device;
    }

    private void release(Device<Driver> device) {
        SessionId sessionId = device.getSessionId();
        log.info("Releasing {} from use in {} pool", device.getSessionId(), name);
        try {
            provider.accept(device);
        } catch (Exception e) {
            log.warn(format("Failure releasing device %s from %s pool", sessionId, name), e);
        }
    }

    private void drain(Deque<Device<Driver>> devices) {
        for (Device<Driver> device = devices.poll();
             device != null;
             device = devices.poll()) {
            release(device);
        }
    }

    private void logStats() {
        log.info("DevicePool: {}, Free: {}, Used: {}", name, free.size(), used.size());
    }
}
