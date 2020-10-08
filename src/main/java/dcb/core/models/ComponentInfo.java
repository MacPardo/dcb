package dcb.core.models;

import dcb.components.ComponentType;
import dcb.core.component.ComponentCore;

import java.util.Objects;

public class ComponentInfo {
    public final int id;
    public final NetworkAddress address;
    public final ComponentCore core;
    public final ComponentType type;

    public ComponentInfo(int id, NetworkAddress address, ComponentCore core, ComponentType type) {
        this.id = id;
        this.address = address;
        this.core = core;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentInfo info = (ComponentInfo) o;
        return id == info.id &&
                Objects.equals(address, info.address) &&
                Objects.equals(core, info.core) &&
                type == info.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, core, type);
    }

    @Override
    public String toString() {
        return "ComponentInfo{" +
                "id=" + id +
                ", address=" + address +
                ", core=" + core +
                ", type=" + type +
                '}';
    }
}

