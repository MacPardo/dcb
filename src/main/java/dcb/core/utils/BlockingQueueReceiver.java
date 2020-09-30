package dcb.core.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueReceiver<T> {
    private final BlockingQueue<T> queue;

    public BlockingQueueReceiver(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    public T poll(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return queue.poll(timeout, timeUnit);
    }
}
