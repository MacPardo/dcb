package dcb.components.optimistic;

import dcb.core.component.ComponentData;
import dcb.core.gateway.Gateway;
import dcb.core.messaging.MessageQueue;
import dcb.core.messaging.Messenger;
import dcb.core.models.Message;
import dcb.core.synchronization.RollbackManager;
import dcb.core.utils.Copyable;
import dcb.core.utils.Pair;

import java.util.List;
import java.util.Set;

class OptimisticMessageConsumer<State extends Copyable<State>> implements Runnable {
    private static final double CHECKPOINTING_PROBABILITY = 0.5;
    private final MessageQueue messageQueue;
    private final Messenger messenger;
    private final Gateway<State> gateway;
    private final ComponentData<State> componentData;
    private final List<Message> initialMessages;
    private final RollbackManager<State> rollbackManager;

    OptimisticMessageConsumer(MessageQueue messageQueue,
                              Gateway<State> gateway,
                              ComponentData<State> componentData) {
        this.messageQueue = messageQueue;
        this.messenger = componentData.messenger;
        this.gateway = gateway;
        this.componentData = componentData;

        Pair<State, List<Message>> initialValues = gateway.init();
        State initialState = initialValues.first;

        this.initialMessages = initialValues.second;
        this.rollbackManager = new RollbackManager<>(componentData.id, initialState);
    }

    @Override
    public void run() {
        try {
            for (Message m : initialMessages) {
                messenger.send(m);
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

    private void onMessage(Message message) throws Exception {
        boolean violatesLcc = message.execTs < rollbackManager.getLvt();
        if (violatesLcc) {
            Set<Message> messages = rollbackManager.rollback(message.execTs);
            for (Message message1 : messages) {
                messenger.send(message1);
            }
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

        for (Message m : messages) {
            rollbackManager.saveMessage(m);
            messenger.send(m);
        }
    }

    private static boolean shouldTakeCheckpoint() {
        return Math.random() < CHECKPOINTING_PROBABILITY;
    }
}
