package dcb.examples.chat;

import dcb.core.Behavior;
import dcb.core.State;
import dcb.core.models.BehaviorMessage;
import dcb.utils.Pair;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ChatBehavior implements Behavior {
    private static final long TIMESTAMP_OFFSET = 10L;
    static final String INPUT = "input";
    static final String OUTPUT = "output";

    private final int speed;

    ChatBehavior(int speed) {
        this.speed = speed;
    }

    @Override
    public Pair<State, List<BehaviorMessage>> init() {
        ArrayList<BehaviorMessage> messages = new ArrayList<>(1);
        messages.add(new BehaviorMessage("0", OUTPUT, TIMESTAMP_OFFSET));
        return new Pair<>(new ChatState(0), messages);
    }

    @Override
    public Pair<State, List<BehaviorMessage>> onMessage(State state, BehaviorMessage message) {
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

        List<BehaviorMessage> list = new ArrayList<>(1);
        list.add(new BehaviorMessage(
                Integer.toString(newIncrement),
                OUTPUT,
                (long) (message.execTs + 1000 * Math.random()))
        );

        return new Pair<>(newState, list);
    }
}
