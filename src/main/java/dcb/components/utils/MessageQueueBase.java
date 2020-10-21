package dcb.components.utils;

import dcb.core.models.Message;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class MessageQueueBase {
    private static final int INITIAL_CAPACITY = 0xFF;
    private static final Comparator<Message> comparator = (m1, m2) -> Long.compare(m2.execTs, m1.execTs);
    private final PriorityQueue<Message> regularQueue = new PriorityQueue<>(
            INITIAL_CAPACITY,
            comparator
    );
    private final Queue<Message> antiQueue = new PriorityQueue<>(
            INITIAL_CAPACITY,
            comparator
    );

    public void push(Message message) {
        Message inverse = message.getInverse();

        Queue<Message> targetQueue;
        Queue<Message> inverseQueue;
        if (message.isAnti) {
            targetQueue = regularQueue;
            inverseQueue = antiQueue;
        } else {
            targetQueue = antiQueue;
            inverseQueue = regularQueue;
        }

        if (inverseQueue.contains(inverse)) {
            inverseQueue.remove(inverse);
        } else {
            targetQueue.add(message);
        }
    }

    /**
     * The returned message's execTs might differ from the timestamp returned by peekTimestamp.
     * This is because pop only removes from the regularQueue, while peekTimestamp also looks at the antiQueue.
     */
    public Message pop() {
        return regularQueue.remove();
    }

    public Long peekTimestamp() {
        if (regularQueue.peek() != null && antiQueue.peek() != null) {
            return Math.max(regularQueue.peek().execTs, antiQueue.peek().execTs);
        }
        //noinspection ReturnOfNull
        return null;
    }
}
