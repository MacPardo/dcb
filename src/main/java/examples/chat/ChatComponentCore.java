package examples.chat;

import dcb.core.component.ComponentCore;
import dcb.core.models.MessageCore;
import dcb.core.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class ChatComponentCore extends ComponentCore<ChatState> {
    private static final long TIMESTAMP_OFFSET = 10L;
    private static final String INPUT_PORT = "input";
    private static final String OUTPUT_PORT = "output";
    private static final String[] PORTS = {INPUT_PORT, OUTPUT_PORT};
    private static final String NAME = "CHAT_COMPONENT_CORE";

    @Override
    public Pair<ChatState, List<MessageCore>> init() {
        ArrayList<MessageCore> messages = new ArrayList<>(1);
        messages.add(new MessageCore("1", OUTPUT_PORT, TIMESTAMP_OFFSET));
        return new Pair<>(new ChatState(0), messages);
    }

    @Override
    public Pair<ChatState, List<MessageCore>> onMessage(ChatState state, MessageCore message) {
        assert message.port.equals(INPUT_PORT);
        int increment = Integer.parseInt(message.payload);
        ChatState newState = new ChatState(state.counter + increment);
        int newIncrement = (int) (Math.random() * 10.0);
        List<MessageCore> list = new ArrayList<>(1);
        list.add(new MessageCore(Integer.toString(newIncrement), OUTPUT_PORT, message.execTs + TIMESTAMP_OFFSET));
        return new Pair<>(newState, list);
    }
}
