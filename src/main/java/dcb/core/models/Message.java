package dcb.core.models;

import dcb.components.utils.DependencyVector;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 8632777422616253354L;

    public final long sentTs;
    public final long execTs;
    public final int from;
    public final int to;
    public final String payload;
    public String componentPayload = "";
    public final String route;
    public final UUID uuid;
    public final boolean isAnti;
    public DependencyVector dependencyVector;

    public Message(long sentTs, long execTs, int from, int to, String payload, String route, UUID uuid, boolean isAnti) {
        this.sentTs = sentTs;
        this.execTs = execTs;
        this.from = from;
        this.to = to;
        this.payload = payload;
        this.route = route;
        this.uuid = uuid;
        this.isAnti = isAnti;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(uuid, message.uuid) && isAnti == message.isAnti;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sentTs, execTs, from, to, payload, route, uuid, isAnti);
    }

    @Override
    public String toString() {
        return "Message{" +
               "sentTs=" + sentTs +
               ", execTs=" + execTs +
               ", from=" + from +
               ", to=" + to +
               ", payload='" + payload + '\'' +
               ", route='" + route + '\'' +
               ", id=" + uuid +
               ", isAnti=" + isAnti +
               '}';
    }

    public Message getInverse() {
        return new Message(sentTs, execTs, from, to, payload, route, uuid, !isAnti);
    }

    public boolean isInverseOf(Message message) {
        return uuid == message.uuid && isAnti != message.isAnti;
    }
}
