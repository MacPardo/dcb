package dcb.core;

import dcb.exceptions.DcbException;
import dcb.core.models.Message;
import dcb.core.models.BehaviorMessage;
import dcb.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class TranslatorGateway implements Gateway {
    private final Translator translator;
    private final Behavior behavior;

    public TranslatorGateway(Translator translator, Behavior behavior) {
        this.translator = translator;
        this.behavior = behavior;
    }

    private List<Message> translateList(List<BehaviorMessage> list, long sentTs) throws DcbException {
        List<Message> result = new ArrayList<>();
        for (BehaviorMessage behaviorMessage : list) {
            Message translate = translator.translate(behaviorMessage, sentTs);
            result.add(translate);
        }
        return result;
    }

    @Override
    public Pair<State, List<Message>> init() throws DcbException {
        Pair<State, List<BehaviorMessage>> initial = behavior.init();
        State initialState = initial.first;
        List<Message> translatedMessages = translateList(initial.second, 0L);
        return new Pair<>(initialState, translatedMessages);
    }

    @Override
    public Pair<State, List<Message>> onMessage(State state, Message message) throws DcbException {
        BehaviorMessage behaviorMessage = BehaviorMessage.fromMessage(message);
        Pair<State, List<BehaviorMessage>> result = behavior.onMessage(state, behaviorMessage);
        State newState = result.first;
        List<Message> translatedMessages = translateList(result.second, message.execTs);
        return new Pair<>(newState, translatedMessages);
    }

    @Override
    public String toString() {
        return "TranslatorGateway{" +
               "translator=" + translator +
               ", componentCore=" + behavior +
               '}';
    }
}
