package dcb.core.models;

import dcb.core.exceptions.InvalidMessageException;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 8632777422616253354L;
    public final long sentTs;
    public final long execTs;
    public final int from;
    public final int to;
    public final String payload;
    public final String route;
    public final UUID id;
    public final boolean isAnti;

    public Message(long sentTs, long execTs, int from, int to, String payload, String route, UUID id, boolean isAnti) {
        this.sentTs = sentTs;
        this.execTs = execTs;
        this.from = from;
        this.to = to;
        this.payload = payload;
        this.route = route;
        this.id = id;
        this.isAnti = isAnti;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return sentTs == message.sentTs &&
                execTs == message.execTs &&
                from == message.from &&
                to == message.to &&
                isAnti == message.isAnti &&
                Objects.equals(payload, message.payload) &&
                Objects.equals(route, message.route) &&
                Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sentTs, execTs, from, to, payload, route, id, isAnti);
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
                ", id=" + id +
                ", isAnti=" + isAnti +
                '}';
    }

    public Message getAnti() throws InvalidMessageException {
        if (isAnti) throw new InvalidMessageException();
        return new Message(sentTs, execTs, from, to, payload, route, id, true);
    }

    public boolean isInverseOf(Message message) {
        return id == message.id && isAnti != message.isAnti;
    }
}
