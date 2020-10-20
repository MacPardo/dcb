package dcb.components.optimistic;

import dcb.components.ComponentFactoryArgs;
import dcb.core.component.State;
import dcb.core.exceptions.DcbException;
import dcb.core.gateway.Gateway;
import dcb.core.messaging.MessageQueue;
import dcb.core.messaging.Messenger;
import dcb.core.models.Message;
import dcb.core.synchronization.RollbackManager;
import dcb.core.utils.Pair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"InfiniteLoopStatement", "CallToSystemExit"})
class OptimisticMessageConsumer implements Runnable {
    private static final double CHECKPOINTING_PROBABILITY = 0.5;
    private final MessageQueue messageQueue;
    private final Messenger messenger;
    private final Gateway gateway;
    private final List<Message> initialMessages;
    private final RollbackManager rollbackManager;

    OptimisticMessageConsumer(MessageQueue messageQueue,
                              Gateway gateway,
                              ComponentFactoryArgs componentFactoryArgs) {
        this.messageQueue = messageQueue;
        this.messenger = componentFactoryArgs.messenger;
        this.gateway = gateway;

        Pair<State, List<Message>> initialValues = null;
        try {
            initialValues = gateway.init();
        } catch (DcbException e) {
            e.printStackTrace();
            System.exit(1);
        }
        State initialState = initialValues.first;

        this.initialMessages = initialValues.second;
        this.rollbackManager = new RollbackManager(componentFactoryArgs.id, initialState);
    }

    @Override
    public void run() {
        try {
            for (Message message : initialMessages) {
                messenger.send(message);
            }

            while (true) {
                Message message = messageQueue.pop();
                System.out.println("Got a message: " + message);
                onMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dateTimeFormatter.format(now));
    }

    private void onMessage(Message message) throws InterruptedException, DcbException {
        System.out.print("on message: ");
        printTime();
        System.out.println("there are " + rollbackManager.getCheckpoints().size() + " checkpoints");

        boolean violatesLcc = message.execTs < rollbackManager.getLvt();
        if (violatesLcc) {
            System.out.println("this message caused an LCC violation");
            Set<Message> messages = rollbackManager.rollback(message.execTs);
            for (Message message1 : messages) {
                messenger.send(message1);
            }
            return;
        } else if (message.isAnti) {
            System.out.println("I got an anti message that does not violate LCC");
        }

        rollbackManager.saveMessage(message);

        long timestamp = message.execTs;
        Pair<State, List<Message>> pair = gateway.onMessage(rollbackManager.getState(), message);
        State newState = pair.first;
        List<Message> messages = pair.second;
        rollbackManager.update(newState, timestamp);

        if (shouldTakeCheckpoint()) {
            rollbackManager.takeCheckpoint();
        }

        for (Message msg : messages) {
            rollbackManager.saveMessage(msg);
            messenger.send(msg);
        }
    }

    private static boolean shouldTakeCheckpoint() {
        //noinspection UnsecureRandomNumberGeneration
        return Math.random() < CHECKPOINTING_PROBABILITY;
    }
}
