package dcb.core.component;

import dcb.core.models.MessageCore;
import dcb.core.utils.Pair;

import java.util.List;

public interface ComponentCore<State> {
    Pair<State, List<MessageCore>> init();

    Pair<State, List<MessageCore>> onMessage(State state, MessageCore message);
}
