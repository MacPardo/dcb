package dcb.examples.extwo;

import dcb.core.ComponentCore;
import dcb.core.State;
import dcb.core.models.MessageCore;
import dcb.utils.Pair;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ComponentCoreSender implements ComponentCore {
    public static final String OUTPUT = "output";
    public static final String MYSELF = "myself";

    private static class MyState implements State {
        @Override
        public State copy() {
            return new MyState();
        }
    }

    private static List<MessageCore> getMessages() {
        var ts = (long) (Math.random() * 1000);
        var msgs= new ArrayList<MessageCore>();
        msgs.add(new MessageCore("", OUTPUT, ts));
        msgs.add(new MessageCore("", MYSELF, 0));
        return msgs;
    }

    @Override
    public Pair<State, List<MessageCore>> init() {
        return new Pair<>(new MyState(), getMessages());
    }

    @Override
    public Pair<State, List<MessageCore>> onMessage(State state, MessageCore message) {
        return new Pair<>(new MyState(), getMessages());
    }
}
