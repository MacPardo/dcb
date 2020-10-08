package dcb.core.gateway;

import dcb.core.models.ComponentPort;
import dcb.core.models.Message;
import dcb.core.models.MessageCore;

import java.util.Map;
import java.util.UUID;

public class Translator {
    private final int localId;
    private final Map<ComponentPort, ComponentPort> connections;

    public Translator(int localId, Map<ComponentPort, ComponentPort> connections) {
        this.localId = localId;
        this.connections = connections;
    }

    Message translate(MessageCore messageCore, long sentTs) {
        final var localPort = new ComponentPort(localId, messageCore.port);
        final var destination = connections.get(localPort);
        return new Message(
                sentTs,
                messageCore.execTs,
                localId,
                destination.componentId,
                messageCore.payload,
                destination.port,
                UUID.randomUUID(),
                false);
    }

    @Override
    public String toString() {
        return "Translator{" +
                "localId=" + localId +
                ", connections=" + connections +
                '}';
    }
}
