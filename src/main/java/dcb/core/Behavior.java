package dcb.core;

import dcb.core.models.BehaviorMessage;
import dcb.utils.Pair;

import java.util.List;

public interface Behavior {
    Pair<State, List<BehaviorMessage>> init();

    Pair<State, List<BehaviorMessage>> onMessage(State state, BehaviorMessage message);
}
