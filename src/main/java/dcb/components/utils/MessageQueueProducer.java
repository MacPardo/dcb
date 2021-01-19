package dcb.components.utils;

import dcb.core.models.Message;
import dcb.utils.Receiver;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("InfiniteLoopStatement")
public class MessageQueueProducer implements Runnable {
    private final MessageQueue queue;
    private final Receiver<Message> receiver;

    public MessageQueueProducer(MessageQueue queue, Receiver<Message> receiver) {
        this.queue = queue;
        this.receiver = receiver;
    }


    @Override
    public void run() {
        try {
            while (true) {
                Message message = receiver.poll(1L, TimeUnit.DAYS);
                queue.push(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
