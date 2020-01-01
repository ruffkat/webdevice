package io.webdevice.wiring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Inherited
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Import(WebDeviceWiring.class)
public @interface EnableWebDevice {
}
