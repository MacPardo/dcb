package examples.chat;

import dcb.components.optimistic.OptimisticComponent;
import dcb.core.models.ComponentPort;
import dcb.components.ComponentFactoryArgs;
import dcb.core.gateway.Translator;
import dcb.core.messaging.Messenger;
import dcb.core.models.Message;
import dcb.core.models.NetworkAddress;
import dcb.core.network.Client;
import dcb.core.network.Server;
import dcb.core.utils.BlockingQueueReceiver;
import dcb.core.utils.BlockingQueueSender;
import dcb.core.utils.Pair;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

/*
connections
list of ComponentInfo
 */

public class MainOld {
    private static final int INITIAL_CAPACITY = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final String CHAT_INPUT_PORT = "INPUT";
    private static final String CHAT_OUTPUT_PORT = "OUTPUT";
    private static final int CHAT1 = 1;
    private static final int CHAT2 = 2;
    private static final int CHAT3 = 3;

    private static final NetworkAddress FIRST_ADDRESS = new NetworkAddress("127.0.0.1", 8080);
    private static final NetworkAddress SECOND_ADDRESS = new NetworkAddress("127.0.0.1", 8081);

    public static void main(String[] args) {
        final var connections = new HashMap<ComponentPort, ComponentPort>(INITIAL_CAPACITY);
        connections.put(new ComponentPort(CHAT1, CHAT_OUTPUT_PORT), new ComponentPort(CHAT2, CHAT_INPUT_PORT));
        connections.put(new ComponentPort(CHAT2, CHAT_OUTPUT_PORT), new ComponentPort(CHAT3, CHAT_INPUT_PORT));
        connections.put(new ComponentPort(CHAT3, CHAT_OUTPUT_PORT), new ComponentPort(CHAT1, CHAT_INPUT_PORT));

        final var addrs = new HashMap<Integer, NetworkAddress>(INITIAL_CAPACITY);
        addrs.put(CHAT1, FIRST_ADDRESS);
        addrs.put(CHAT2, FIRST_ADDRESS);
        addrs.put(CHAT3, SECOND_ADDRESS);

        final var chat1Translator = new Translator(CHAT1, connections);
        final var chat1Core = new ChatComponentCore();
        final var chat1Queue = new ArrayBlockingQueue<Message>(INITIAL_CAPACITY);
        final var chat1Sender = new BlockingQueueSender<>(chat1Queue);
        final var chat1Receiver = new BlockingQueueReceiver<>(chat1Queue);

        final var localSenders = new HashMap<Integer, BlockingQueueSender<Message>>(INITIAL_CAPACITY);
        localSenders.put(CHAT1, chat1Sender);

        final var clientQueue = new ArrayBlockingQueue<Pair<Message, NetworkAddress>>(QUEUE_CAPACITY);

        final var messenger = new Messenger(localSenders, addrs, new BlockingQueueSender<>(clientQueue));

        final var chat1 = new OptimisticComponent(new ComponentFactoryArgs(
                CHAT1,
                chat1Core,
                chat1Receiver,
                messenger,
                chat1Translator
        ));

        final var server = new Server(8080, messenger);
        final var client = new Client(new BlockingQueueReceiver<>(clientQueue));
    }
}
