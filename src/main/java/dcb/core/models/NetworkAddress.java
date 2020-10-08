package dcb.core.models;

import java.util.Objects;

public class NetworkAddress {
    public final String host;
    public final int port;

    public NetworkAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkAddress that = (NetworkAddress) o;
        return port == that.port &&
                Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return "NetworkAddress{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
