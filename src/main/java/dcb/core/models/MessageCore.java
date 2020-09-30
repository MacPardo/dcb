package dcb.core.models;

public record MessageCore(String payload, String port, long execTs) {
    public static MessageCore fromMessage(Message message) {
        return new MessageCore(message.payload(), message.route(), message.execTs());
    }
}
