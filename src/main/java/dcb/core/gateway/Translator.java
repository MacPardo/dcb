package dcb.core.gateway;

import dcb.core.models.Message;
import dcb.core.models.MessageCore;
import dcb.core.utils.Pair;

import java.util.Map;
import java.util.UUID;

public class Translator {
    private final int localId;
    private final Map<String, Pair<Integer, String>> routeToDestination;

    public Translator(int localId, Map<String, Pair<Integer, String>> routeToDestination) {
        this.localId = localId;
        this.routeToDestination = routeToDestination;
    }

    Message translate(MessageCore messageCore, long sentTs) {
        Pair<Integer, String> destination = routeToDestination.get(messageCore.port);
        return new Message(
                sentTs,
                messageCore.execTs,
                localId,
                destination.first,
                messageCore.payload,
                destination.second,
                UUID.randomUUID(),
                false);
    }
}
