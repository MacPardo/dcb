package dcb.examples.simple;

import dcb.core.Behavior;
import dcb.core.State;
import dcb.core.models.BehaviorMessage;
import dcb.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class SimpleBehavior implements Behavior {
    public static final String OUTPUT_PORT = "output";
    public static final String INPUT_PORT = "input";
    public static final String MYSELF_PORT = "myself";

    private static class SimpleState implements State {
        @Override
        public State copy() {
            return new SimpleState();
        }
    }

    @Override
    public Pair<State, List<BehaviorMessage>> init() {
        State state = new SimpleState();
        List<BehaviorMessage> list = new ArrayList<>();
        list.add(new BehaviorMessage("", OUTPUT_PORT, 10L));
        list.add(new BehaviorMessage("", MYSELF_PORT, 10L));
        return new Pair<>(state, list);
    }

    @Override
    public Pair<State, List<BehaviorMessage>> onMessage(State state, BehaviorMessage message) {
        if (message.port == MYSELF_PORT) {
            List<BehaviorMessage> list = new ArrayList<>();
            list.add(new BehaviorMessage("", OUTPUT_PORT, message.execTs));
            list.add(new BehaviorMessage("", MYSELF_PORT, message.execTs + 10));
            return new Pair<>(state.copy(), list);
        } else {
            var offset =  (int)(Math.random() * 5);
            BehaviorMessage response = new BehaviorMessage("", OUTPUT_PORT, message.execTs + offset);
            List<BehaviorMessage> list = new ArrayList<>();
            list.add(response);
            return new Pair<>(state.copy(), list);
        }

    }
}
