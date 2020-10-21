package dcb.core;

import dcb.core.models.MessageCore;
import dcb.utils.Pair;

import java.util.List;

public interface ComponentCore {
    Pair<State, List<MessageCore>> init();

    Pair<State, List<MessageCore>> onMessage(State state, MessageCore message);
}
