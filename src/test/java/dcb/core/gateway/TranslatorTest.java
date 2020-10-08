package dcb.core.gateway;

import dcb.core.exceptions.DcbException;
import dcb.core.models.ComponentPort;
import dcb.core.models.Message;
import dcb.core.models.MessageCore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

class TranslatorTest {
    @Test
    void translate() {
        int localId = 1;
        int otherId = 2;
        String outputPort = "OUTPUT";
        String inputPort = "INPUT";
        String payload = "payload";
        final long execTs = 10L;
        final long sentTs = 5L;
        var connections = new HashMap<ComponentPort, ComponentPort>();
        connections.put(
                new ComponentPort(localId, outputPort),
                new ComponentPort(otherId, inputPort)
        );

        var uuid = UUID.randomUUID();
        var mock = new UUIDGeneratorMock(uuid);
        var translator = new Translator(localId, connections, mock);

        var firstMsgCore = new MessageCore(payload, outputPort, execTs);
        Message firstTranslated = null;
        try {
            firstTranslated = translator.translate(firstMsgCore, sentTs);
        } catch (DcbException e) {
            assert false;
        }

        Assertions.assertEquals(
                new Message(
                        sentTs,
                        execTs,
                        localId,
                        otherId,
                        payload,
                        inputPort,
                        uuid,
                        false
                ),
                firstTranslated
        );

        // test an exception is thrown when the port is not configured to send messages
        var secondMsgCore = new MessageCore(payload, inputPort, execTs);
        Assertions.assertThrows(
                DcbException.class,
                () -> translator.translate(secondMsgCore, sentTs)
        );
    }
}