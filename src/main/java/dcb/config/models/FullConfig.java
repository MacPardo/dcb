package dcb.config.models;

import java.util.List;

public class FullConfig {
    public final List<ComponentCoreConfig> componentCores;
    public final List<ProcessConfig> processes;
    public final List<PortConnectionConfig> connections;

    public FullConfig(List<ComponentCoreConfig> componentCores, List<ProcessConfig> processes, List<PortConnectionConfig> connections) {
        this.componentCores = componentCores;
        this.processes = processes;
        this.connections = connections;
    }
}
