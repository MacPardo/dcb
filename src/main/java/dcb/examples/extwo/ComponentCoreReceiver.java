package dcb.examples.extwo;

import dcb.core.ComponentCore;
import dcb.core.State;
import dcb.core.models.MessageCore;
import dcb.utils.Pair;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ComponentCoreReceiver implements ComponentCore {
    private static class MyState implements State {
        @Override
        public State copy() {
            return new MyState();
        }
    }

    @Override
    public Pair<State, List<MessageCore>> init() {
        return new Pair<>(new MyState(), new ArrayList<>());
    }

    @Override
    public Pair<State, List<MessageCore>> onMessage(State state, MessageCore message) {
        return new Pair<>(new MyState(), new ArrayList<>());
    }
}
