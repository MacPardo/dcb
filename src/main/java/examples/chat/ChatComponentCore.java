package examples.chat;

import dcb.core.component.ComponentCore;
import dcb.core.component.State;
import dcb.core.models.MessageCore;
import dcb.core.utils.Pair;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({
        "UnsecureRandomNumberGeneration",
        "NumericCastThatLosesPrecision",
        "CastToConcreteClass"
})
public class ChatComponentCore implements ComponentCore {
    private static final long TIMESTAMP_OFFSET = 10L;
    static final String INPUT = "input";
    static final String OUTPUT = "output";

    @Override
    public Pair<State, List<MessageCore>> init() {
        ArrayList<MessageCore> messages = new ArrayList<>(1);
        messages.add(new MessageCore("1", OUTPUT, TIMESTAMP_OFFSET));
        return new Pair<>(new ChatState(0), messages);
    }

    @Override
    public Pair<State, List<MessageCore>> onMessage(State state, MessageCore message) {
        final var chatState = (ChatState) state;
        assert message.port.equals(INPUT);

        int increment = Integer.parseInt(message.payload);
        ChatState newState = new ChatState(chatState.counter + increment);

        //noinspection MagicNumber
        int newIncrement = (int) (Math.random() * 10.0);

        List<MessageCore> list = new ArrayList<>(1);
        list.add(new MessageCore(Integer.toString(newIncrement), OUTPUT, message.execTs + TIMESTAMP_OFFSET));

        return new Pair<>(newState, list);
    }
}
