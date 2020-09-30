package dcb.config.models;

public class PortConnectionConfig {
    public final ComponentPort from;
    public final ComponentPort to;

    public PortConnectionConfig(ComponentPort from, ComponentPort to) {
        this.from = from;
        this.to = to;
    }
}
