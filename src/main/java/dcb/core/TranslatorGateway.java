package dcb.core;

import dcb.exceptions.DcbException;
import dcb.core.models.Message;
import dcb.core.models.MessageCore;
import dcb.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class TranslatorGateway implements Gateway {
    private final Translator translator;
    private final ComponentCore componentCore;

    public TranslatorGateway(Translator translator, ComponentCore componentCore) {
        this.translator = translator;
        this.componentCore = componentCore;
    }

    private List<Message> translateList(List<MessageCore> list, long sentTs) throws DcbException {
        List<Message> result = new ArrayList<>();
        for (MessageCore messageCore : list) {
            Message translate = translator.translate(messageCore, sentTs);
            result.add(translate);
        }
        return result;
    }

    @Override
    public Pair<State, List<Message>> init() throws DcbException {
        Pair<State, List<MessageCore>> initial = componentCore.init();
        State initialState = initial.first;
        List<Message> translatedMessages = translateList(initial.second, 0L);
        return new Pair<>(initialState, translatedMessages);
    }

    @Override
    public Pair<State, List<Message>> onMessage(State state, Message message) throws DcbException {
        MessageCore messageCore = MessageCore.fromMessage(message);
        Pair<State, List<MessageCore>> result = componentCore.onMessage(state, messageCore);
        State newState = result.first;
        List<Message> translatedMessages = translateList(result.second, message.execTs);
        return new Pair<>(newState, translatedMessages);
    }

    @Override
    public String toString() {
        return "TranslatorGateway{" +
               "translator=" + translator +
               ", componentCore=" + componentCore +
               '}';
    }
}
