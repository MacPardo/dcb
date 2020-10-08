package examples.chat;

import dcb.components.ComponentType;
import dcb.core.models.ComponentInfo;
import dcb.core.models.ComponentPort;
import dcb.core.models.NetworkAddress;
import dcb.core.runner.Runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final int COMPONENT1 = 1;
    private static final int COMPONENT2 = 2;
    private static final int COMPONENT3 = 3;
    private static final NetworkAddress ADDR1 = new NetworkAddress("127.0.0.1", 8080);
    private static final NetworkAddress ADDR2 = new NetworkAddress("127.0.0.1", 8081);

    public static void main(String[] args) {
        final List<ComponentInfo> componentInfos = new ArrayList<>(10);
        componentInfos.add(new ComponentInfo(
                COMPONENT1,
                ADDR1,
                new ChatComponentCore(),
                ComponentType.OPTIMISTIC
        ));
        componentInfos.add(new ComponentInfo(
                COMPONENT2,
                ADDR1,
                new ChatComponentCore(),
                ComponentType.OPTIMISTIC
        ));
        componentInfos.add(new ComponentInfo(
                COMPONENT3,
                ADDR2,
                new ChatComponentCore(),
                ComponentType.OPTIMISTIC
        ));

        final Map<ComponentPort, ComponentPort> connections = new HashMap<>(10);
        connections.put(
                new ComponentPort(COMPONENT1, ChatComponentCore.OUTPUT),
                new ComponentPort(COMPONENT2, ChatComponentCore.INPUT)
        );
        connections.put(
                new ComponentPort(COMPONENT2, ChatComponentCore.OUTPUT),
                new ComponentPort(COMPONENT3, ChatComponentCore.INPUT)
        );
        connections.put(
                new ComponentPort(COMPONENT3, ChatComponentCore.OUTPUT),
                new ComponentPort(COMPONENT1, ChatComponentCore.INPUT)
        );

        System.out.println("componentInfos: " + componentInfos);
        System.out.println("connections: " + connections);
        final var runner = new Runner(componentInfos, connections);
        runner.run();
    }
}
