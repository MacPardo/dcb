package dcb.components.optimistic;

import dcb.core.Component;
import dcb.components.ComponentFactoryArgs;
import dcb.core.TranslatorGateway;
import dcb.components.utils.MessageQueue;
import dcb.components.utils.MessageQueueProducer;

public class OptimisticComponent extends Component {
    public OptimisticComponent(ComponentFactoryArgs args) {
        super(args);
    }

    @Override
    public void run() {
        final var gateway = new TranslatorGateway(args.translator, args.core);
        final var messageQueue = new MessageQueue();
        final var producer = new MessageQueueProducer(messageQueue, args.receiver);
        final var consumer = new OptimisticMessageConsumer(messageQueue, gateway, args);

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
