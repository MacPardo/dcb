package dcb.core.models;

import java.util.Objects;

public class BehaviorMessage {
    public final String payload;
    public final String port;
    public final long execTs;

    public BehaviorMessage(String payload, String port, long execTs) {
        this.payload = payload;
        this.port = port;
        this.execTs = execTs;
    }

    public static BehaviorMessage fromMessage(Message message) {
        return new BehaviorMessage(message.payload, message.route, message.execTs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BehaviorMessage that = (BehaviorMessage) o;
        return execTs == that.execTs &&
                Objects.equals(payload, that.payload) &&
                Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payload, port, execTs);
    }

    @Override
    public String toString() {
        return "MessageCore{" +
                "payload='" + payload + '\'' +
                ", port='" + port + '\'' +
                ", execTs=" + execTs +
                '}';
    }
}

//public record MessageCore(String payload, String port, long execTs) {
//    public static MessageCore fromMessage(Message message) {
//        return new MessageCore(message.payload, message.route, message.execTs);
//    }
//}
