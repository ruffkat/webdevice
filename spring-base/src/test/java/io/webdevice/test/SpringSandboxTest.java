package io.webdevice.test;

import org.junit.Before;

public abstract class SpringSandboxTest {
    private SpringSandbox sandbox;

    @Before
    public void setUpSandbox() {
        sandbox = new SpringSandbox();
    }

    protected SpringSandbox sandbox() {
        return sandbox;
    }
}
