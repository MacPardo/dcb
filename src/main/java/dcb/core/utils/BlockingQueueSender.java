package dcb.core.utils;

import java.util.concurrent.BlockingQueue;

public class BlockingQueueSender<T> {
    private final BlockingQueue<T> queue;

    public BlockingQueueSender(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    public void put(T t) throws InterruptedException {
        queue.put(t);
    }
}
