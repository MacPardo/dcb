package dcb.core.runner;

import dcb.components.ComponentFactory;
import dcb.components.ComponentFactoryArgs;
import dcb.core.gateway.Translator;
import dcb.core.messaging.Messenger;
import dcb.core.models.ComponentInfo;
import dcb.core.models.ComponentPort;
import dcb.core.models.Message;
import dcb.core.models.NetworkAddress;
import dcb.core.network.Client;
import dcb.core.network.Server;
import dcb.core.utils.BlockingQueueReceiver;
import dcb.core.utils.BlockingQueueSender;
import dcb.core.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class AddressRunner implements Runnable {
    private static final int QUEUE_CAPACITY = 256;
    private final NetworkAddress networkAddress;
    private final List<ComponentInfo> componentInfoList;
    private final Map<ComponentPort, ComponentPort> connections;

    public AddressRunner(NetworkAddress networkAddress, List<ComponentInfo> componentInfoList, Map<ComponentPort, ComponentPort> connections) {
        this.networkAddress = networkAddress;
        this.componentInfoList = componentInfoList;
        this.connections = connections;
    }


    @Override
    public void run() {
        System.out.println("inside address runner with " + networkAddress);
        var localSenders = new HashMap<Integer, BlockingQueueSender<Message>>();
        var localReceivers = new HashMap<Integer, BlockingQueueReceiver<Message>>();
        var addresses = new HashMap<Integer, NetworkAddress>();

        for (ComponentInfo info : componentInfoList) {
            if (info.address == networkAddress) {
                var queue = new ArrayBlockingQueue<Message>(QUEUE_CAPACITY);
                localReceivers.put(info.id, new BlockingQueueReceiver<>(queue));
                localSenders.put(info.id, new BlockingQueueSender<>(queue));
            } else {
                addresses.put(info.id, info.address);
            }
        }

        var clientQueue = new ArrayBlockingQueue<Pair<Message, NetworkAddress>>(QUEUE_CAPACITY);
        var messenger = new Messenger(
                localSenders,
                addresses,
                new BlockingQueueSender<>(clientQueue)
        );

        var clientThread = new Thread(new Client(new BlockingQueueReceiver<>(clientQueue)));
        clientThread.start();


        localReceivers.forEach((id, receiver) -> {
            //noinspection OptionalGetWithoutIsPresent
            final var info = componentInfoList.stream()
                    .filter(componentInfo -> componentInfo.id == id)
                    .findFirst()
                    .get();
            final var component = ComponentFactory.createComponent(info.type, new ComponentFactoryArgs(
                    id,
                    info.core,
                    receiver,
                    messenger,
                    new Translator(id, connections)
            ));
            final var thread = new Thread(component);
            thread.start();
        });

        final var server = new Server(networkAddress.port, messenger);
        server.run();
    }
}