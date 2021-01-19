package dcb.utils;

import java.util.concurrent.BlockingQueue;

public class Sender<T> {
    private final BlockingQueue<T> queue;

    public Sender(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    public void put(T t) throws InterruptedException {
        queue.put(t);
    }
}
