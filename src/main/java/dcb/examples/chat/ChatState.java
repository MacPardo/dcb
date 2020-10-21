package dcb.examples.chat;

import dcb.core.State;

public class ChatState implements State {
    public final int counter;

    public ChatState(int counter) {
        this.counter = counter;
    }

    @Override
    public ChatState copy() {
        return new ChatState(counter);
    }
}
