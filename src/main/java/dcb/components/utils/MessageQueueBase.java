package dcb.components.utils;

import dcb.core.models.Message;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class MessageQueueBase {
    private static final int INITIAL_CAPACITY = 0xFF;
    private static final Comparator<Message> comparator = (m1, m2) -> Long.compare(m2.execTs, m1.execTs);
    public final PriorityQueue<Message> regularQueue = new PriorityQueue<>(
            INITIAL_CAPACITY,
            comparator
    );
    public final Queue<Message> antiQueue = new PriorityQueue<>(
            INITIAL_CAPACITY,
            comparator
    );

    public boolean canPop() {
        return !regularQueue.isEmpty();
    }

    // Returns the difference in the number of regular messages after performing the operation
    public void push(Message message) {
        Message inverse = message.getInverse();

        if (message.isAnti) {
            if (regularQueue.contains(inverse)) {
                regularQueue.remove(inverse);
            } else {
                antiQueue.add(message);
            }
        } else {
            if (antiQueue.contains(inverse)) {
                antiQueue.remove(inverse);
            } else {
                regularQueue.add(message);
            }
        }

        /*
        Queue<Message> targetQueue;
        Queue<Message> inverseQueue;

        if (!message.isAnti) {
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
        }*/
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
            return Math.min(regularQueue.peek().execTs, antiQueue.peek().execTs);
        } else if (regularQueue.peek() != null) {
            return regularQueue.peek().execTs;
        } else if (antiQueue.peek() != null) {
            return antiQueue.peek().execTs;
        } else {
            //noinspection ReturnOfNull
            return null;
        }
    }

    public boolean canPeekTimestamp() {
        return regularQueue.peek() != null || antiQueue.peek() != null;
    }
}
