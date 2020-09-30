package dcb.core.messaging;

import dcb.core.models.Message;
import dcb.core.utils.BlockingQueueReceiver;

import java.util.concurrent.TimeUnit;

public class MessageQueueProducer implements Runnable {
    private final MessageQueue queue;
    private final BlockingQueueReceiver<Message> receiver;

    public MessageQueueProducer(MessageQueue queue, BlockingQueueReceiver<Message> receiver) {
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
