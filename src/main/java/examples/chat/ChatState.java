package examples.chat;

import dcb.core.utils.Copyable;

public class ChatState implements Copyable<ChatState> {
    public final int counter;

    public ChatState(int counter) {
        this.counter = counter;
    }

    @Override
    public ChatState copy() {
        return new ChatState(counter);
    }
}
