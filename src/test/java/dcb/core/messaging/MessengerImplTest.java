package dcb.core.messaging;

import dcb.core.MessengerImpl;
import dcb.exceptions.DcbException;
import dcb.core.models.Message;
import dcb.core.models.NetworkAddress;
import dcb.utils.Sender;
import dcb.utils.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class MessengerImplTest {
    static final int SELF_ID = 10;
    static final int REMOTE_ID = 1;
    static final int LOCAL_ID = 2;
    static final NetworkAddress REMOTE_ADDRESS = new NetworkAddress("localhost", 8080);
    private static final Map<Integer, NetworkAddress> addresses;
    private static final UUID uuid = UUID.randomUUID();

    static {
        addresses = new HashMap<>();
        addresses.put(REMOTE_ID, REMOTE_ADDRESS);
    }

    @Mock
    private Sender<Pair<Message, NetworkAddress>> networkSenderMock;

    @Mock
    private Sender<Message> localSenderMock;

    private MessengerImpl messenger = null;

    private static Message messageFor(int target) {
        return new Message(0L, 0L, SELF_ID, target, "", "", uuid, false);
    }

    @BeforeEach
    void setUp() {
        var localSenders = new HashMap<Integer, Sender<Message>>();
        localSenders.put(LOCAL_ID, localSenderMock);
        messenger = new MessengerImpl(
                localSenders,
                addresses,
                networkSenderMock
        );
    }

    @Test
    void send() throws DcbException, InterruptedException {
        var localMsg = messageFor(LOCAL_ID);
        var remoteMsg = messageFor(REMOTE_ID);

        messenger.send(localMsg);
        Mockito.verify(localSenderMock).put(localMsg);

        messenger.send(remoteMsg);
        Mockito.verify(networkSenderMock).put(new Pair<>(remoteMsg, REMOTE_ADDRESS));
    }

    @Test
    void sendLocally() {
        var localMsg = messageFor(LOCAL_ID);
        var remoteMsg = messageFor(REMOTE_ID);

        try {
            messenger.send(localMsg);
        } catch (Exception ignored) {
            assert false;
        }

        Assertions.assertThrows(DcbException.class, () -> messenger.sendLocally(remoteMsg));
    }
}