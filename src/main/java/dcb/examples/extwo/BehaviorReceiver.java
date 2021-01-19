package dcb.examples.extwo;

import dcb.core.Behavior;
import dcb.core.State;
import dcb.core.models.BehaviorMessage;
import dcb.utils.Pair;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class BehaviorReceiver implements Behavior {
    private static class MyState implements State {
        @Override
        public State copy() {
            return new MyState();
        }
    }

    @Override
    public Pair<State, List<BehaviorMessage>> init() {
        return new Pair<>(new MyState(), new ArrayList<>());
    }

    @Override
    public Pair<State, List<BehaviorMessage>> onMessage(State state, BehaviorMessage message) {
        return new Pair<>(new MyState(), new ArrayList<>());
    }
}
