package dcb.examples.extwo;

import dcb.components.ComponentType;
import dcb.core.models.ComponentInfo;
import dcb.core.models.ComponentPort;
import dcb.core.models.NetworkAddress;
import dcb.core.runner.Runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class Main {
    private static int SENDER_ID = 0;
    private static int RECEIVER_ID = 1;
    private static NetworkAddress address = new NetworkAddress("localhost", 8080);

    public static void main(String[] args) {
        List<ComponentInfo> componentInfos = new ArrayList<>();
        componentInfos.add(new ComponentInfo(
                SENDER_ID,
                address,
                new BehaviorSender(),
                ComponentType.OPTIMISTIC
        ));
        componentInfos.add(new ComponentInfo(
                RECEIVER_ID,
                address,
                new BehaviorReceiver(),
                ComponentType.OPTIMISTIC
        ));

        Map<ComponentPort, ComponentPort> connections = new HashMap<>();
        connections.put(
                new ComponentPort(SENDER_ID, BehaviorSender.OUTPUT),
                new ComponentPort(RECEIVER_ID, "input")
        );
        connections.put(
                new ComponentPort(SENDER_ID, BehaviorSender.MYSELF),
                new ComponentPort(SENDER_ID, "input")
        );

        Runner runner = new Runner(componentInfos, connections);
        runner.run();
    }
}
