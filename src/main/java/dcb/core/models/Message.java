package dcb.core.models;

import dcb.core.exceptions.InvalidMessageException;

import java.util.UUID;

public record Message(
        long sentTs,
        long execTs,
        int from,
        int to,
        String payload,
        String route,
        UUID id,
        boolean isAnti
) {
    public Message getAnti() throws InvalidMessageException {
        if (isAnti) throw new InvalidMessageException();
        return new Message(sentTs, execTs, from, to, payload, route, id, true);
    }

    public boolean isInverseOf(Message message) {
        return id == message.id && isAnti != message.isAnti;
    }
}
