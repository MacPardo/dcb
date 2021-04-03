package dcb.core.models;

import dcb.components.ComponentType;
import dcb.core.Behavior;

import java.util.Objects;

public class ComponentInfo {
    public final int id;
    public final NetworkAddress address;
    public final Behavior core;
    public final ComponentType type;
    public final String outputFile;

    public ComponentInfo(int id, NetworkAddress address, Behavior core, ComponentType type, String outputFile) {
        this.id = id;
        this.address = address;
        this.core = core;
        this.type = type;
        this.outputFile = outputFile;
    }
}

