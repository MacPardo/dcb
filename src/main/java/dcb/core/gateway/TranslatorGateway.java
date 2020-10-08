package dcb.core.gateway;

import dcb.core.component.ComponentCore;
import dcb.core.component.State;
import dcb.core.models.Message;
import dcb.core.models.MessageCore;
import dcb.core.utils.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class TranslatorGateway implements Gateway {
    private final Translator translator;
    private final ComponentCore componentCore;

    public TranslatorGateway(Translator translator, ComponentCore componentCore) {
        this.translator = translator;
        this.componentCore = componentCore;
    }

    private List<Message> translateList(List<MessageCore> list, long sentTs) {
        return list.stream()
                .map(messageCore -> translator.translate(messageCore, sentTs))
                .collect(Collectors.toList());
    }

    @Override
    public Pair<State, List<Message>> init() {
        Pair<State, List<MessageCore>> initial = componentCore.init();
        State initialState = initial.first;
        List<Message> translatedMessages = translateList(initial.second, 0L);
        return new Pair<>(initialState, translatedMessages);
    }

    @Override
    public Pair<State, List<Message>> onMessage(State state, Message message) {
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
