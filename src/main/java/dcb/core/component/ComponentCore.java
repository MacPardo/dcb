package dcb.core.component;

import dcb.core.models.MessageCore;
import dcb.core.utils.Pair;

import java.util.List;

public interface ComponentCore {
    Pair<State, List<MessageCore>> init();

    Pair<State, List<MessageCore>> onMessage(State state, MessageCore message);
}
