package dcb.core;

import dcb.components.ComponentFactoryArgs;

public abstract class Component implements Runnable {
    protected final ComponentFactoryArgs args;

    protected Component(ComponentFactoryArgs args) {
        this.args = args;
    }
}
