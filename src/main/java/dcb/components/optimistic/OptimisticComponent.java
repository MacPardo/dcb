package dcb.components.optimistic;

import dcb.core.component.ComponentData;
import dcb.core.component.Component;
import dcb.core.gateway.Gateway;
import dcb.core.gateway.TranslatorGateway;
import dcb.core.messaging.MessageQueue;
import dcb.core.messaging.MessageQueueProducer;
import dcb.core.utils.Copyable;

public class OptimisticComponent<State extends Copyable<State>> extends Component<State> {
    public OptimisticComponent(ComponentData<State> data) {
        super(data);
    }

    @Override
    public void run() {
        Gateway<State> gateway = new TranslatorGateway<>(data.translator, data.core);
        MessageQueue messageQueue = new MessageQueue();
        MessageQueueProducer producer = new MessageQueueProducer(messageQueue, data.messageReceiver);
        OptimisticMessageConsumer<State> consumer = new OptimisticMessageConsumer<>(messageQueue, gateway, data);

        Thread producerThread = new Thread(producer);
        producerThread.start();

        consumer.run();

        try {
            producerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
