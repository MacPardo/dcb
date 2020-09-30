package dcb.config.models;

import java.util.List;

public class ProcessConfig {
    public final String name;
    public final int networkPort;
    public final List<ComponentConfig> components;

    public ProcessConfig(String name, int networkPort, List<ComponentConfig> components) {
        this.name = name;
        this.networkPort = networkPort;
        this.components = components;
    }
}
