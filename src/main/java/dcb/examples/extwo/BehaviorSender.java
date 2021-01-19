package dcb.examples.extwo;

import dcb.core.Behavior;
import dcb.core.State;
import dcb.core.models.BehaviorMessage;
import dcb.utils.Pair;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class BehaviorSender implements Behavior {
    public static final String OUTPUT = "output";
    public static final String MYSELF = "myself";

    private static class MyState implements State {
        @Override
        public State copy() {
            return new MyState();
        }
    }

    private static List<BehaviorMessage> getMessages() {
        var ts = (long) (Math.random() * 1000);
        var msgs= new ArrayList<BehaviorMessage>();
        msgs.add(new BehaviorMessage("", OUTPUT, ts));
        msgs.add(new BehaviorMessage("", MYSELF, 0));
        return msgs;
    }

    @Override
    public Pair<State, List<BehaviorMessage>> init() {
        return new Pair<>(new MyState(), getMessages());
    }

    @Override
    public Pair<State, List<BehaviorMessage>> onMessage(State state, BehaviorMessage message) {
        return new Pair<>(new MyState(), getMessages());
    }
}
