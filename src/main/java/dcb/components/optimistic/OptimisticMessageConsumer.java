package dcb.components.optimistic;

import dcb.components.ComponentFactoryArgs;
import dcb.core.State;
import dcb.exceptions.DcbException;
import dcb.core.Gateway;
import dcb.components.utils.MessageQueue;
import dcb.core.MessengerImpl;
import dcb.core.models.Message;
import dcb.components.utils.RollbackManager;
import dcb.utils.Pair;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"InfiniteLoopStatement", "CallToSystemExit"})
class OptimisticMessageConsumer implements Runnable {
    private static final double CHECKPOINTING_PROBABILITY = 0.5;
    private final MessageQueue messageQueue;
    private final MessengerImpl messenger;
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
                onMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
        LocalDateTime now = LocalDateTime.now();
    }

    private void onMessage(Message message) throws InterruptedException, DcbException {
        printTime();

        boolean violatesLcc = message.execTs < rollbackManager.getLvt();
        if (violatesLcc) {
            Set<Message> messages = rollbackManager.rollback(message.execTs);
            for (Message message1 : messages) {
                messenger.send(message1);
            }
            return;
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
