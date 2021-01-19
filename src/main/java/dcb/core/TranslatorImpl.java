package dcb.core;

import dcb.exceptions.DcbException;
import dcb.core.models.ComponentPort;
import dcb.core.models.Message;
import dcb.core.models.BehaviorMessage;
import dcb.utils.UUIDGenerator;

import java.util.Map;

public class TranslatorImpl implements Translator {
    private final int localId;
    private final Map<ComponentPort, ComponentPort> connections;

    @SuppressWarnings("FieldNotUsedInToString")
    private final UUIDGenerator uuidGenerator;

    public TranslatorImpl(int localId, Map<ComponentPort, ComponentPort> connections, UUIDGenerator uuidGenerator) {
        this.localId = localId;
        this.connections = connections;
        this.uuidGenerator = uuidGenerator;
    }

    public Message translate(BehaviorMessage behaviorMessage, long sentTs) throws DcbException {
        final var localPort = new ComponentPort(localId, behaviorMessage.port);
        final var destination = connections.get(localPort);
        if (destination == null) {
            throw new DcbException("could not translate " + behaviorMessage);
        }
        return new Message(
                sentTs,
                behaviorMessage.execTs,
                localId,
                destination.componentId,
                behaviorMessage.payload,
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
