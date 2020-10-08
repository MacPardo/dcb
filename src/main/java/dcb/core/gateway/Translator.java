package dcb.core.gateway;

import dcb.core.exceptions.DcbException;
import dcb.core.models.ComponentPort;
import dcb.core.models.Message;
import dcb.core.models.MessageCore;
import dcb.core.utils.UUIDGenerator;

import java.util.Map;

public class Translator {
    private final int localId;
    private final Map<ComponentPort, ComponentPort> connections;

    @SuppressWarnings("FieldNotUsedInToString")
    private final UUIDGenerator uuidGenerator;

    public Translator(int localId, Map<ComponentPort, ComponentPort> connections, UUIDGenerator uuidGenerator) {
        this.localId = localId;
        this.connections = connections;
        this.uuidGenerator = uuidGenerator;
    }

    Message translate(MessageCore messageCore, long sentTs) throws DcbException {
        final var localPort = new ComponentPort(localId, messageCore.port);
        final var destination = connections.get(localPort);
        if (destination == null) {
            throw new DcbException("could not translate " + messageCore);
        }
        return new Message(
                sentTs,
                messageCore.execTs,
                localId,
                destination.componentId,
                messageCore.payload,
                destination.port,
                uuidGenerator.randomUUID(),
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
