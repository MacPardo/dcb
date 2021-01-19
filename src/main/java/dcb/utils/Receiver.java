package dcb.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Receiver<T> {
    private final BlockingQueue<T> queue;

    public Receiver(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    public T poll(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return queue.poll(timeout, timeUnit);
    }
}
