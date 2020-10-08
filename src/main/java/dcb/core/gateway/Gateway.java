package dcb.core.gateway;

import dcb.core.component.State;
import dcb.core.exceptions.DcbException;
import dcb.core.models.Message;
import dcb.core.utils.Pair;

import java.util.List;

public interface Gateway {
    Pair<State, List<Message>> init() throws DcbException;

    Pair<State, List<Message>> onMessage(State state, Message message) throws DcbException;
}
