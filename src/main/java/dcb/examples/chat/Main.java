package dcb.examples.chat;

import dcb.components.ComponentType;
import dcb.core.models.ComponentInfo;
import dcb.core.models.ComponentPort;
import dcb.core.models.NetworkAddress;
import dcb.core.runner.Runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("MagicNumber")
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
                new ChatBehavior(1000),
                ComponentType.OPTIMISTIC
        ));
        componentInfos.add(new ComponentInfo(
                COMPONENT2,
                ADDR1,
                new ChatBehavior(100),
                ComponentType.OPTIMISTIC
        ));
        componentInfos.add(new ComponentInfo(
                COMPONENT3,
                ADDR1,
                new ChatBehavior(500),
                ComponentType.OPTIMISTIC
        ));

        final Map<ComponentPort, ComponentPort> connections = new HashMap<>(10);
        connections.put(
                new ComponentPort(COMPONENT1, ChatBehavior.OUTPUT),
                new ComponentPort(COMPONENT2, ChatBehavior.INPUT)
        );
        connections.put(
                new ComponentPort(COMPONENT2, ChatBehavior.OUTPUT),
                new ComponentPort(COMPONENT3, ChatBehavior.INPUT)
        );
        connections.put(
                new ComponentPort(COMPONENT3, ChatBehavior.OUTPUT),
                new ComponentPort(COMPONENT1, ChatBehavior.INPUT)
        );

        final var runner = new Runner(componentInfos, connections);
        runner.run();
    }
}
