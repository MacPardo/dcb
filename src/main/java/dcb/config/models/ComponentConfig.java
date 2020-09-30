package dcb.config.models;

import dcb.components.ComponentType;

public class ComponentConfig {
    public final String name;
    public final String core;
    public final ComponentType type;

    public ComponentConfig(String name, String core, ComponentType type) {
        this.name = name;
        this.core = core;
        this.type = type;
    }
}
