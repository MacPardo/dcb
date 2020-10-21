package dcb.examples.chat;

import dcb.core.ComponentCore;
import dcb.core.State;
import dcb.core.models.MessageCore;
import dcb.utils.Pair;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ChatComponentCore implements ComponentCore {
    private static final long TIMESTAMP_OFFSET = 10L;
    static final String INPUT = "input";
    static final String OUTPUT = "output";

    private final int speed;

    ChatComponentCore(int speed) {
        this.speed = speed;
    }

    @Override
    public Pair<State, List<MessageCore>> init() {
        ArrayList<MessageCore> messages = new ArrayList<>(1);
        messages.add(new MessageCore("0", OUTPUT, TIMESTAMP_OFFSET));
        return new Pair<>(new ChatState(0), messages);
    }

    @Override
    public Pair<State, List<MessageCore>> onMessage(State state, MessageCore message) {
        try {
            Thread.sleep((long) (Math.random() * 100.0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final var chatState = (ChatState) state;
        assert message.port.equals(INPUT);

        int increment = Integer.parseInt(message.payload);
        ChatState newState = new ChatState(chatState.counter + increment);

        //noinspection MagicNumber
        int newIncrement = increment + 1;

        List<MessageCore> list = new ArrayList<>(1);
        list.add(new MessageCore(
                Integer.toString(newIncrement),
                OUTPUT,
                (long) (message.execTs + 1000 * Math.random()))
        );

        return new Pair<>(newState, list);
    }
}
