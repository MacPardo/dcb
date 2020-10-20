package dcb.core.messaging;

import dcb.core.exceptions.DcbException;
import dcb.core.exceptions.InvalidMessageException;
import dcb.core.models.Message;

import java.util.concurrent.Semaphore;

public class MessageQueue {
    private final MessageQueueBase messageQueueBase = new MessageQueueBase();
    private final Semaphore queueSizeSemaphore = new Semaphore(0, true);
    private final Semaphore mutex = new Semaphore(1, true);

    public void push(Message message) throws InterruptedException {
        mutex.acquire();
        try {
            messageQueueBase.push(message);
            queueSizeSemaphore.release();
        } finally {
            mutex.release();
        }
    }

    public Message pop() throws DcbException, InterruptedException {
        queueSizeSemaphore.acquire();
        mutex.acquire();
        Message message = messageQueueBase.pop();
        mutex.release();
        if (message == null) throw new DcbException();
        return message;
    }

    public long peekTimestamp() throws InterruptedException {
        queueSizeSemaphore.acquire();
        mutex.acquire();
        Long timestamp = messageQueueBase.peekTimestamp();
        mutex.release();
        queueSizeSemaphore.release();
        return timestamp;
    }
}
