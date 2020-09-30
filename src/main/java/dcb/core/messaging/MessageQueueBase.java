package dcb.core.messaging;

import dcb.core.exceptions.InvalidMessageException;
import dcb.core.models.Message;

import java.util.PriorityQueue;

public class MessageQueueBase {
    private final PriorityQueue<Message> queue = new PriorityQueue<>(
            16,
            (message1, message2) -> Long.compare(message2.execTs, message1.execTs));

    public void push(Message message) throws InvalidMessageException {
        Message anti = message.getAnti();
        if (queue.contains(anti)) {
            queue.remove(anti);
        } else {
            queue.add(message);
        }
    }

    public Message pop() {
        return queue.remove();
    }
}
