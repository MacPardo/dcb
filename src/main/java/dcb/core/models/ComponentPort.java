package dcb.core.models;

import java.util.Objects;

public class ComponentPort {
    public final int componentId;
    public final String port;

    public ComponentPort(int componentId, String port) {
        this.componentId = componentId;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentPort that = (ComponentPort) o;
        return componentId == that.componentId &&
                Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId, port);
    }

    @Override
    public String toString() {
        return "ComponentPort{" +
                "componentId=" + componentId +
                ", port='" + port + '\'' +
                '}';
    }
}

