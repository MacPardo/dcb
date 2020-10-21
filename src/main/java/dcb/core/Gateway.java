package dcb.core;

import dcb.exceptions.DcbException;
import dcb.core.models.Message;
import dcb.utils.Pair;

import java.util.List;

public interface Gateway {
    Pair<State, List<Message>> init() throws DcbException;

    Pair<State, List<Message>> onMessage(State state, Message message) throws DcbException;
}
