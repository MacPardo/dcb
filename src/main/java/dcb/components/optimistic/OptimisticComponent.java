package dcb.components.optimistic;

import dcb.components.utils.MessageQueueBase;
import dcb.components.utils.RollbackManager;
import dcb.core.Component;
import dcb.components.ComponentFactoryArgs;
import dcb.core.Gateway;
import dcb.core.State;
import dcb.core.TranslatorGateway;
import dcb.core.models.Message;
import dcb.exceptions.DcbException;
import dcb.utils.Pair;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"InfiniteLoopStatement", "MagicNumber", "UnsecureRandomNumberGeneration"})
public class OptimisticComponent extends Component {
    private final MessageQueueBase messageQueue = new MessageQueueBase();
    private Gateway gateway;
    private RollbackManager rollbackManager;

    public OptimisticComponent(ComponentFactoryArgs args) {
        super(args);
        gateway = new TranslatorGateway(args.translator, args.core);
    }

    private void print(String message) {
        System.out.println("<" + rollbackManager.id + "> - " + message);
    }

    @Override
    public void run() {
        try {
            runThrows();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runThrows() throws Exception {
        Pair<State, List<Message>> initialValues = gateway.init();
        State initialState = initialValues.first;
        List<Message> initialMessages = initialValues.second;

        rollbackManager = new RollbackManager(args.id, initialState);

        for (Message message : initialMessages) {
            args.messenger.send(message);
        }

        while (true) {
            Message message = args.receiver.poll(1L, TimeUnit.DAYS);

            messageQueue.push(message);

            boolean canPeek = messageQueue.canPeekTimestamp();
            if (messageQueue.canPeekTimestamp()) {
                boolean violates = isThereLCCViolation();
                if (violates) {
                    print("rolling back to " + messageQueue.peekTimestamp());
                    Collection<Message> messages = rollbackManager.rollback(messageQueue.peekTimestamp());
                    print("rolled back to " + rollbackManager.getLvt());
                    for (Message m : messages) {
                        args.messenger.send(m);
                    }
                } else if (messageQueue.canPop()) {
                    onMessage(messageQueue.pop());
                } else {
                    print("???");
                }
            }
        }
    }

    boolean isThereLCCViolation() {
        return messageQueue.peekTimestamp() < rollbackManager.getLvt();
    }

    void onMessage(Message message) throws DcbException, InterruptedException {
        print("got message " + message);
        rollbackManager.saveMessage(message);
        long timestamp = message.execTs;
        Pair<State, List<Message>> values = gateway.onMessage(rollbackManager.getState(), message);
        State newState = values.first;
        List<Message> messages = values.second;
        rollbackManager.update(newState, timestamp);

        if (shouldTakeCheckpoint()) {
            rollbackManager.takeCheckpoint();
        }

        for (Message m : messages) {
            rollbackManager.saveMessage(m);
            args.messenger.send(m);
        }
    }

    static boolean shouldTakeCheckpoint() {
        return true;
    }
}
