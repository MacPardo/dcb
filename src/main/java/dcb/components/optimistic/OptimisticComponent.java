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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"InfiniteLoopStatement", "MagicNumber", "UnsecureRandomNumberGeneration"})
public class OptimisticComponent extends Component {

    private class CCB {
        public int ind;
        public int rc;
    }

    private final MessageQueueBase messageQueue = new MessageQueueBase();
    private final Gateway gateway;
    private RollbackManager rollbackManager = null;
    private int eventCount = 0;
    private Map<Integer, CCB> UC = new HashMap<>();

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
            print(" -----> received " + message);
            print("__RB-stat (recv=" + rollbackManager.receivedMessages.size() + ", sent=" + rollbackManager.sentMessages.size() + ", chck=" + rollbackManager.checkpoints.size() + ")");

            messageQueue.push(message);

            if (messageQueue.canPeekTimestamp()) {
                boolean violates = isThereLCCViolation();
                if (violates) {
                    print("rolling back to " + messageQueue.peekTimestamp());
                    Collection<Message> messages = rollbackManager.rollback(messageQueue.peekTimestamp());
                    print("rolled back to " + rollbackManager.getLvt());
                    for (Message msg : messages) {
                        args.messenger.send(msg);
                    }
                }
                onMessage(messageQueue.pop());
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

    @SuppressWarnings("MethodMayBeStatic")
    public boolean shouldTakeCheckpoint() {
        return eventCount % 10 == 0;
    }
}
