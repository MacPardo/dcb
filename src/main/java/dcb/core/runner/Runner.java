package dcb.core.runner;

import dcb.core.models.ComponentInfo;
import dcb.core.models.ComponentPort;
import dcb.core.models.NetworkAddress;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Runner implements Runnable {
    private final List<ComponentInfo> componentInfoList;
    private final Map<ComponentPort, ComponentPort> connections;

    public Runner(List<ComponentInfo> componentInfoList, Map<ComponentPort, ComponentPort> connections) {
        this.componentInfoList = componentInfoList;
        this.connections = connections;
    }

    @Override
    public void run() {
        System.out.println("inside runner");
        verifyConnections();

        final var addressSet = new HashSet<NetworkAddress>();
        final var threads = new ArrayList<Thread>();

        for (ComponentInfo info : componentInfoList) {
            if (AddressVerifier.isAddressLocal(info.address.host)) {
                addressSet.add(info.address);
            }
        }

        for (NetworkAddress networkAddress : addressSet) {
            System.out.println("networkAddress " + networkAddress);
            var thread = new Thread(new AddressRunner(
                    networkAddress,
                    componentInfoList,
                    connections
            ));
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void verifyConnections() {
        for (Map.Entry<ComponentPort, ComponentPort> entry : connections.entrySet()) {
            ComponentPort senderPort = entry.getKey();
            ComponentPort receiverPort = entry.getValue();

            var senderType = findWithId(senderPort.componentId).type;
            var receiverType = findWithId(receiverPort.componentId).type;

            if (!senderType.canSendMessagesTo(receiverType)) {
                System.out.println("CONNECTIONS ARE NOT VALID");
                System.out.println(senderType + " cannot send messages to " + receiverType);
                //noinspection CallToSystemExit
                System.exit(1);
            }
        }
    }

    ComponentInfo findWithId(int id) {
        //noinspection OptionalGetWithoutIsPresent
        return componentInfoList.stream().filter(info -> info.id == id).findFirst().get();
    }
}
